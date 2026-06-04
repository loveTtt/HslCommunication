/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Fuji;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.FujiSPHAddress;
import HslCommunication.Core.IMessage.FujiSPHMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class FujiSPHNet
extends NetworkDeviceBase {
    private byte ConnectionID = (byte)-2;

    public FujiSPHNet() {
        this.setByteTransform(new RegularByteTransform());
        this.WordLength = 1;
        this.setPort(18245);
    }

    public FujiSPHNet(String ipAddress, int port) {
        this.setByteTransform(new RegularByteTransform());
        this.WordLength = 1;
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FujiSPHMessage();
    }

    public byte getConnectionID() {
        return this.ConnectionID;
    }

    public void setConnectionID(byte connectionID) {
        this.ConnectionID = connectionID;
    }

    private OperateResultExOne<byte[]> ReadFujiSPHAddress(FujiSPHAddress address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = FujiSPHNet.BuildReadCommand(this.ConnectionID, address, length);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return read;
            }
            OperateResultExOne<byte[]> extra = FujiSPHNet.ExtractActualData((byte[])read.Content);
            if (!extra.IsSuccess) {
                return extra;
            }
            Utilities.ArrayListAddArray(array, (byte[])extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(array));
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return this.ReadFujiSPHAddress((FujiSPHAddress)analysis.Content, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command = FujiSPHNet.BuildWriteCommand(this.ConnectionID, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = FujiSPHNet.ExtractActualData((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        int bitCount = ((FujiSPHAddress)analysis.Content).getBitIndex() + length;
        int wordLength = bitCount % 16 == 0 ? bitCount / 16 : bitCount / 16 + 1;
        OperateResultExOne<byte[]> read = this.ReadFujiSPHAddress((FujiSPHAddress)analysis.Content, (short)wordLength);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray((byte[])read.Content), ((FujiSPHAddress)analysis.Content).getBitIndex(), length));
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return analysis;
        }
        int bitCount = ((FujiSPHAddress)analysis.Content).getBitIndex() + value.length;
        int wordLength = bitCount % 16 == 0 ? bitCount / 16 : bitCount / 16 + 1;
        OperateResultExOne<byte[]> read = this.ReadFujiSPHAddress((FujiSPHAddress)analysis.Content, (short)wordLength);
        if (!read.IsSuccess) {
            return read;
        }
        boolean[] writeBoolArray = SoftBasic.ByteToBoolArray((byte[])read.Content);
        System.arraycopy(value, 0, writeBoolArray, ((FujiSPHAddress)analysis.Content).getBitIndex(), value.length);
        OperateResultExOne<byte[]> command = FujiSPHNet.BuildWriteCommand(this.ConnectionID, address, SoftBasic.BoolArrayToByte(writeBoolArray));
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> write = this.ReadFromCoreServer((byte[])command.Content);
        if (!write.IsSuccess) {
            return write;
        }
        OperateResultExOne<byte[]> extra = FujiSPHNet.ExtractActualData((byte[])write.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult CpuBatchStart() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)0, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    public OperateResult CpuBatchInitializeAndStart() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)1, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    public OperateResult CpuBatchStop() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)2, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    public OperateResult CpuBatchReset() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)3, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    public OperateResult CpuIndividualStart() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)4, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    public OperateResult CpuIndividualInitializeAndStart() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)5, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    public OperateResult CpuIndividualStop() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)6, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    public OperateResult CpuIndividualReset() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(FujiSPHNet.PackCommand(this.ConnectionID, (byte)4, (byte)7, null));
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPHNet.ExtractActualData((byte[])read.Content);
    }

    @Override
    public String toString() {
        return "FujiSPHNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }

    public static String GetErrorDescription(byte code) {
        switch (code) {
            case 16: {
                return "Command cannot be executed because an error occurred in the CPU.";
            }
            case 17: {
                return "Command cannot be executed because the CPU is running.";
            }
            case 18: {
                return "Command cannot be executed due to the key switch condition of the CPU.";
            }
            case 32: {
                return "CPU received undefined command or mode.";
            }
            case 34: {
                return "Setting error was found in command header part.";
            }
            case 35: {
                return "Transmission is interlocked by a command from another device.";
            }
            case 40: {
                return "Requested command cannot be executed because another command is now being executed.";
            }
            case 43: {
                return "Requested command cannot be executed because the loader is now performing another processing( including program change).";
            }
            case 47: {
                return "Requested command cannot be executed because the system is now being initialized.";
            }
            case 64: {
                return "Invalid data type or number was specified.";
            }
            case 65: {
                return "Specified data cannot be found.";
            }
            case 68: {
                return "Specified address exceeds the valid range.";
            }
            case 69: {
                return "Address + the number of read/write words exceed the valid range.";
            }
            case -96: {
                return "No module exists at specified destination station No.";
            }
            case -94: {
                return "No response data is returned from the destination module.";
            }
            case -92: {
                return "Command cannot be communicated because an error occurred in the SX bus.";
            }
            case -91: {
                return "Command cannot be communicated because NAK occurred while sending data via the SX bus.";
            }
        }
        return StringResources.Language.UnknownError();
    }

    private static byte[] PackCommand(byte connectionId, byte command, byte mode, byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        byte[] buffer = new byte[20 + data.length];
        buffer[0] = -5;
        buffer[1] = -128;
        buffer[2] = -128;
        buffer[3] = 0;
        buffer[4] = -1;
        buffer[5] = 123;
        buffer[6] = connectionId;
        buffer[7] = 0;
        buffer[8] = 17;
        buffer[9] = 0;
        buffer[10] = 0;
        buffer[11] = 0;
        buffer[12] = 0;
        buffer[13] = 0;
        buffer[14] = command;
        buffer[15] = mode;
        buffer[16] = 0;
        buffer[17] = 1;
        buffer[18] = Utilities.getBytes(data.length)[0];
        buffer[19] = Utilities.getBytes(data.length)[1];
        if (data.length > 0) {
            System.arraycopy(data, 0, buffer, 20, data.length);
        }
        return buffer;
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte connectionId, String address, short length) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return FujiSPHNet.BuildReadCommand(connectionId, (FujiSPHAddress)analysis.Content, length);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte connectionId, FujiSPHAddress address, short length) {
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 240);
        for (int i = 0; i < splits.length; ++i) {
            byte[] buffer = new byte[]{address.getTypeCode(), Utilities.getBytes(address.getAddressStart())[0], Utilities.getBytes(address.getAddressStart())[1], Utilities.getBytes(address.getAddressStart())[2], Utilities.getBytes(splits[i])[0], Utilities.getBytes(splits[i])[1]};
            array.add(FujiSPHNet.PackCommand(connectionId, (byte)0, (byte)0, buffer));
            address.setAddressStart(address.getAddressStart() + splits[i]);
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte connectionId, String address, byte[] data) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        int length = data.length / 2;
        byte[] buffer = new byte[6 + data.length];
        buffer[0] = ((FujiSPHAddress)analysis.Content).getTypeCode();
        buffer[1] = Utilities.getBytes(((FujiSPHAddress)analysis.Content).getAddressStart())[0];
        buffer[2] = Utilities.getBytes(((FujiSPHAddress)analysis.Content).getAddressStart())[1];
        buffer[3] = Utilities.getBytes(((FujiSPHAddress)analysis.Content).getAddressStart())[2];
        buffer[4] = Utilities.getBytes(length)[0];
        buffer[5] = Utilities.getBytes(length)[1];
        System.arraycopy(data, 0, buffer, 6, data.length);
        return OperateResultExOne.CreateSuccessResult(FujiSPHNet.PackCommand(connectionId, (byte)1, (byte)0, buffer));
    }

    public static OperateResultExOne<byte[]> ExtractActualData(byte[] response) {
        try {
            if (response[4] != 0) {
                return new OperateResultExOne<byte[]>(response[4], FujiSPHNet.GetErrorDescription(response[4]));
            }
            if (response.length > 26) {
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 26));
            }
            return OperateResultExOne.CreateSuccessResult(new byte[0]);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + " Source: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }
}

