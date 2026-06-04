/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Toyota;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.ToyoPucAddress;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.ToyoPucMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Toyota.WordAddress;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.regex.Pattern;

public class ToyoPuc
extends NetworkDeviceBase {
    public ToyoPuc() {
        this.setByteTransform(new RegularByteTransform());
        this.WordLength = 1;
    }

    public ToyoPuc(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new ToyoPucMessage();
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        byte[] buffer = new byte[command.length + 4];
        buffer[2] = BitConverter.GetBytes(command.length)[0];
        buffer[3] = BitConverter.GetBytes(command.length)[1];
        Utilities.ByteArrayCopyTo(command, buffer, 4);
        return buffer;
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        if (response == null || response.length < 4) {
            return new OperateResultExOne<byte[]>("Receive data too short: " + SoftBasic.ByteToHexString(response, ' '));
        }
        if (response[0] != -128) {
            return new OperateResultExOne<byte[]>("FT check failed: " + SoftBasic.ByteToHexString(response, ' '));
        }
        if (response[1] != 0) {
            return new OperateResultExOne<byte[]>(response.length == 4 ? ToyoPuc.GetErrorText(response[1]) : ToyoPuc.GetErrorText(response[4]));
        }
        if (response.length > 5) {
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 5));
        }
        return OperateResultExOne.CreateSuccessResult(new byte[0]);
    }

    private static String GetErrorText(byte code) {
        switch (code) {
            case 17: {
                return StringResources.Language.ToyoPuc11();
            }
            case 32: {
                return StringResources.Language.ToyoPuc20();
            }
            case 33: {
                return StringResources.Language.ToyoPuc21();
            }
            case 35: {
                return StringResources.Language.ToyoPuc23();
            }
            case 36: {
                return StringResources.Language.ToyoPuc24();
            }
            case 37: {
                return StringResources.Language.ToyoPuc25();
            }
            case 52: {
                return StringResources.Language.ToyoPuc34();
            }
            case 62: {
                return StringResources.Language.ToyoPuc3E();
            }
            case 63: {
                return StringResources.Language.ToyoPuc3F();
            }
            case 64: {
                return StringResources.Language.ToyoPuc40();
            }
            case 65: {
                return StringResources.Language.ToyoPuc41();
            }
        }
        return StringResources.Language.UnknownError();
    }

    private OperateResultExOne<WordAddress> ExtraWordAddress(String address, short length) {
        try {
            int bitIndex = 0;
            int index = address.indexOf(46);
            if (index > 0) {
                bitIndex = Convert.ToInt32(address.substring(index + 1), 16);
                address = address.substring(0, index);
            } else {
                bitIndex = Convert.ToInt32(address.substring(address.length() - 1), 16);
                if ((address = address.substring(0, address.length() - 1)).startsWith("EK") || address.startsWith("EV") || address.startsWith("ET") || address.startsWith("EC") || address.startsWith("EL") || address.startsWith("EX") || address.startsWith("EY") || address.startsWith("EM")) {
                    if (address.length() == 2) {
                        address = address + "0";
                    }
                } else if (address.length() == 1) {
                    address = address + "0";
                }
            }
            int readLength = (bitIndex + length + 15) / 16;
            WordAddress wordAddress = new WordAddress();
            wordAddress.Address = address;
            wordAddress.BitIndex = bitIndex;
            wordAddress.WordLength = readLength;
            return OperateResultExOne.CreateSuccessResult(wordAddress);
        }
        catch (Exception ex) {
            return new OperateResultExOne<WordAddress>("ExtraWordAddress failed: " + ex.getMessage());
        }
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<WordAddress> analysis = this.ExtraWordAddress(address, length);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<byte[]> read = this.Read(((WordAddress)analysis.Content).Address, (short)((WordAddress)analysis.Content).WordLength);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray((byte[])read.Content), ((WordAddress)analysis.Content).BitIndex, length));
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        Pattern p = Pattern.compile("prg=[0-9]+;");
        if (p.matcher(address).matches()) {
            return HslHelper.WriteBool(this, address, new boolean[]{value}, 16, false, true);
        }
        if (address.indexOf(46) > 0) {
            return HslHelper.WriteBool(this, address, new boolean[]{value});
        }
        OperateResultExOne<byte[]> command = ToyoPuc.BuildWriteBoolCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        return this.ReadFromCoreServer((byte[])command.Content);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<byte[]> command = ToyoPuc.BuildReadWordCommand(address, length);
        if (!command.IsSuccess) {
            return command;
        }
        return this.ReadFromCoreServer((byte[])command.Content);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command = ToyoPuc.BuildWriteWordCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        return this.ReadFromCoreServer((byte[])command.Content);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return HslHelper.WriteBool(this, address, value, 16, false, true);
    }

    @Override
    public String toString() {
        return "ToyoPuc[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }

    private static OperateResultExOne<byte[]> BuildReadBoolCommand(String address) {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom(address, (short)1, true);
        if (!analysisAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysisAddress);
        }
        ToyoPucAddress pucAddress = (ToyoPucAddress)analysisAddress.Content;
        if (pucAddress.PRG >= 0) {
            return new OperateResultExOne<byte[]>();
        }
        byte[] buffer = new byte[]{32, BitConverter.GetBytes(pucAddress.getAddressStart())[0], BitConverter.GetBytes(pucAddress.getAddressStart())[1]};
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    private static OperateResultExOne<byte[]> BuildReadWordCommand(String address, short length) {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom(address, length, false);
        if (!analysisAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysisAddress);
        }
        ToyoPucAddress pucAddress = (ToyoPucAddress)analysisAddress.Content;
        if (pucAddress.PRG >= 0) {
            byte[] buffer = new byte[]{-108, (byte)pucAddress.PRG, BitConverter.GetBytes(pucAddress.getAddressStart())[0], BitConverter.GetBytes(pucAddress.getAddressStart())[1], BitConverter.GetBytes(length)[0], BitConverter.GetBytes(length)[1]};
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        byte[] buffer = new byte[]{28, BitConverter.GetBytes(pucAddress.getAddressStart())[0], BitConverter.GetBytes(pucAddress.getAddressStart())[1], BitConverter.GetBytes(length)[0], BitConverter.GetBytes(length)[1]};
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    private static OperateResultExOne<byte[]> BuildWriteWordCommand(String address, byte[] value) {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom(address, (short)1, false);
        if (!analysisAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysisAddress);
        }
        ToyoPucAddress pucAddress = (ToyoPucAddress)analysisAddress.Content;
        if (pucAddress.PRG >= 0) {
            byte[] buffer = new byte[4 + value.length];
            buffer[0] = -107;
            buffer[1] = (byte)pucAddress.PRG;
            buffer[2] = BitConverter.GetBytes(pucAddress.getAddressStart())[0];
            buffer[3] = BitConverter.GetBytes(pucAddress.getAddressStart())[1];
            Utilities.ByteArrayCopyTo(value, buffer, 4);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        byte[] buffer = new byte[3 + value.length];
        buffer[0] = 29;
        buffer[1] = BitConverter.GetBytes(pucAddress.getAddressStart())[0];
        buffer[2] = BitConverter.GetBytes(pucAddress.getAddressStart())[1];
        Utilities.ByteArrayCopyTo(value, buffer, 3);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    private static OperateResultExOne<byte[]> BuildWriteBoolCommand(String address, boolean value) {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom(address, (short)1, true);
        if (!analysisAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysisAddress);
        }
        ToyoPucAddress pucAddress = (ToyoPucAddress)analysisAddress.Content;
        if (pucAddress.PRG >= 0) {
            return new OperateResultExOne<byte[]>("Not supported prg write bool");
        }
        byte[] buffer = new byte[4];
        buffer[0] = 33;
        buffer[1] = BitConverter.GetBytes(pucAddress.getAddressStart())[0];
        buffer[2] = BitConverter.GetBytes(pucAddress.getAddressStart())[1];
        if (value) {
            buffer[3] = 1;
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }
}

