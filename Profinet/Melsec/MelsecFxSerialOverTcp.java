/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.MelsecFxSerialHelper;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.Utilities;
import java.net.Socket;
import java.util.ArrayList;

public class MelsecFxSerialOverTcp
extends NetworkDeviceBase {
    private ArrayList<String> inis = new ArrayList();
    public boolean IsNewVersion = false;
    private boolean useGot = false;
    private SoftIncrementCount incrementCount;

    public MelsecFxSerialOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.IsNewVersion = true;
        this.getByteTransform().setIsStringReverse(true);
        this.setSleepTime(20);
        this.incrementCount = new SoftIncrementCount(Integer.MAX_VALUE, 1L);
        this.inis.add("00 10 02 FF FF FC 01 10 03");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010201000000023030453032303203364310033543");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010202000000023030454341303203384510033833");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010203000000023030453032303203364310033545");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010204000000023030454341303203384510033835");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102050000000245303138303030343003443510034342");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102060000000245303138303430314303453910034535");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102070000000245303030453030343003453110034436");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102080000000245303030453430343003453510034446");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102090000000245303030453830343003453910034538");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D701020A0000000245303030454330343003463410034630");
    }

    public MelsecFxSerialOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    public boolean isUseGot() {
        return this.useGot;
    }

    public void setUseGot(boolean useGot) {
        this.useGot = useGot;
    }

    private byte[] GetBytesSend(byte[] command) {
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < command.length; ++i) {
            if (i < 2) {
                array.add(command[i]);
                continue;
            }
            if (i < command.length - 4) {
                if (command[i] == 16) {
                    array.add(command[i]);
                }
                array.add(command[i]);
                continue;
            }
            array.add(command[i]);
        }
        return Utilities.ToByteArray(array);
    }

    private byte[] GetBytesReceive(byte[] response) {
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < response.length; ++i) {
            if (i < 2) {
                array.add(response[i]);
                continue;
            }
            if (i < response.length - 4) {
                if (response[i] == 16 && response[i + 1] == 16) {
                    array.add(response[i]);
                    ++i;
                    continue;
                }
                array.add(response[i]);
                continue;
            }
            array.add(response[i]);
        }
        return Utilities.ToByteArray(array);
    }

    @Override
    public byte[] PackCommandWithHeader(byte[] command) {
        if (this.useGot) {
            String[] ips = this.getIpAddress().split("\\.");
            byte[] buffer = new byte[66 + command.length];
            buffer[0] = 16;
            buffer[1] = 2;
            buffer[2] = 94;
            buffer[6] = -4;
            buffer[12] = 18;
            buffer[13] = 18;
            buffer[17] = -1;
            buffer[18] = -1;
            buffer[19] = 3;
            buffer[22] = -1;
            buffer[23] = 3;
            buffer[26] = BitConverter.GetBytes(34 + command.length)[0];
            buffer[27] = BitConverter.GetBytes(34 + command.length)[1];
            buffer[28] = 28;
            buffer[29] = 9;
            buffer[30] = 26;
            buffer[31] = 24;
            buffer[41] = -4;
            buffer[44] = 18;
            buffer[45] = 18;
            buffer[46] = 4;
            buffer[47] = 20;
            buffer[49] = 1;
            buffer[50] = BitConverter.GetBytes(this.getPort())[1];
            buffer[51] = BitConverter.GetBytes(this.getPort())[0];
            buffer[52] = (byte)Integer.parseInt(ips[0]);
            buffer[53] = (byte)Integer.parseInt(ips[1]);
            buffer[54] = (byte)Integer.parseInt(ips[2]);
            buffer[55] = (byte)Integer.parseInt(ips[3]);
            buffer[56] = 1;
            buffer[57] = 2;
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes((int)this.incrementCount.GetCurrentValue()), buffer, 58);
            Utilities.ByteArrayCopyTo(command, buffer, 62);
            buffer[buffer.length - 4] = 16;
            buffer[buffer.length - 3] = 3;
            Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(buffer, 2, 4), buffer, buffer.length - 2);
            return this.GetBytesSend(buffer);
        }
        return super.PackCommandWithHeader(command);
    }

    @Override
    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        if (this.useGot) {
            if (response.length > 68) {
                response = this.GetBytesReceive(response);
                int index = -1;
                for (int i = 0; i < response.length - 4; ++i) {
                    if (response[i] != 16 || response[i + 1] != 2) continue;
                    index = i;
                    break;
                }
                if (index >= 0) {
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveDouble(response, 64 + index, 4));
                }
            }
            return new OperateResultExOne<byte[]>("Got failed: " + SoftBasic.ByteToHexString(response, ' ', 16));
        }
        return super.UnpackResponseContent(send, response);
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        if (this.useGot) {
            for (int i = 0; i < this.inis.size(); ++i) {
                OperateResultExOne<byte[]> ini1 = this.ReadFromCoreServer(socket, SoftBasic.HexStringToBytes(this.inis.get(i)), true, false);
                if (ini1.IsSuccess) continue;
                return ini1;
            }
        }
        return super.InitializationOnConnect(socket);
    }

    @Override
    public OperateResultExOne<byte[]> ReadFromCoreServer(Socket socket, byte[] send, boolean hasResponseData, boolean usePackAndUnpack) {
        OperateResultExOne<byte[]> read = super.ReadFromCoreServer(socket, send, hasResponseData, usePackAndUnpack);
        if (!read.IsSuccess) {
            return read;
        }
        if (read.Content == null) {
            return read;
        }
        if (((byte[])read.Content).length > 2) {
            return read;
        }
        OperateResultExOne<byte[]> read2 = super.ReadFromCoreServer(socket, send, hasResponseData, usePackAndUnpack);
        if (!read2.IsSuccess) {
            return read2;
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceArray((byte[])read.Content, (byte[])read2.Content));
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return MelsecFxSerialHelper.Read(this, address, length, this.IsNewVersion);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return MelsecFxSerialHelper.Write(this, address, value, this.IsNewVersion);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return MelsecFxSerialHelper.ReadBool(this, address, length, this.IsNewVersion);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return MelsecFxSerialHelper.Write(this, address, value);
    }

    public OperateResult ActivePlc() {
        return MelsecFxSerialHelper.ActivePlc(this);
    }

    @Override
    public String toString() {
        return "MelsecFxSerialOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

