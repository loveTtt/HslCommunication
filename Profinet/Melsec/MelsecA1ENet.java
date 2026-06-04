/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MelsecA1EBinaryMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.MelsecA1EDataType;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class MelsecA1ENet
extends NetworkDeviceBase {
    private byte PLCNumber = (byte)-1;

    public MelsecA1ENet() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
    }

    public MelsecA1ENet(String ipAddress, int port) {
        this.WordLength = 1;
        super.setIpAddress(ipAddress);
        super.setPort(port);
        this.setByteTransform(new RegularByteTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecA1EBinaryMessage();
    }

    public byte getPLCNumber() {
        return this.PLCNumber;
    }

    public void setPLCNumber(byte plcNumber) {
        this.PLCNumber = plcNumber;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = MelsecA1ENet.BuildReadCommand(address, length, false, this.PLCNumber);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult check = MelsecA1ENet.CheckResponseLegal((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            OperateResultExOne<byte[]> extra = MelsecA1ENet.ExtractActualData((byte[])read.Content, false);
            if (!extra.IsSuccess) {
                return extra;
            }
            Utilities.ArrayListAddArray(array, (byte[])extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(array));
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = MelsecA1ENet.BuildReadCommand(address, length, true, this.PLCNumber);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult check = MelsecA1ENet.CheckResponseLegal((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            OperateResultExOne<byte[]> extract = MelsecA1ENet.ExtractActualData((byte[])read.Content, true);
            if (!extract.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(extract);
            }
            Utilities.ArrayListAddArray(array, (byte[])extract.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBoolArray(array, length));
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command = MelsecA1ENet.BuildWriteWordCommand(address, value, this.PLCNumber);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = MelsecA1ENet.CheckResponseLegal((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        OperateResultExOne<byte[]> command = MelsecA1ENet.BuildWriteBoolCommand(address, values, this.PLCNumber);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecA1ENet.CheckResponseLegal((byte[])read.Content);
    }

    @Override
    public String toString() {
        return "MelsecA1ENet";
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(String address, short length, boolean isBit, byte plcNumber) {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte subtitle = isBit ? (byte)0 : 1;
        int[] splits = SoftBasic.SplitIntegerToArray(length, isBit ? 256 : 64);
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        for (int i = 0; i < splits.length; ++i) {
            byte[] _PLCCommand = new byte[12];
            _PLCCommand[0] = subtitle;
            _PLCCommand[1] = plcNumber;
            _PLCCommand[2] = 10;
            _PLCCommand[3] = 0;
            _PLCCommand[4] = BitConverter.GetBytes((Integer)analysis.Content2)[0];
            _PLCCommand[5] = BitConverter.GetBytes((Integer)analysis.Content2)[1];
            _PLCCommand[6] = BitConverter.GetBytes((Integer)analysis.Content2)[2];
            _PLCCommand[7] = BitConverter.GetBytes((Integer)analysis.Content2)[3];
            _PLCCommand[8] = BitConverter.GetBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0];
            _PLCCommand[9] = BitConverter.GetBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1];
            int len = splits[i];
            if (len == 256) {
                len = 0;
            }
            _PLCCommand[10] = BitConverter.GetBytes(len)[0];
            _PLCCommand[11] = BitConverter.GetBytes(len)[1];
            array.add(_PLCCommand);
            OperateResultExTwo<MelsecA1EDataType, Integer> operateResultExTwo = analysis;
            Integer.valueOf((Integer)operateResultExTwo.Content2 + splits[i]);
            operateResultExTwo.Content2 = operateResultExTwo.Content2;
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(String address, byte[] value, byte plcNumber) {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] _PLCCommand = new byte[12 + value.length];
        _PLCCommand[0] = 3;
        _PLCCommand[1] = plcNumber;
        _PLCCommand[2] = 10;
        _PLCCommand[3] = 0;
        _PLCCommand[4] = Utilities.getBytes((Integer)analysis.Content2)[0];
        _PLCCommand[5] = Utilities.getBytes((Integer)analysis.Content2)[1];
        _PLCCommand[6] = Utilities.getBytes((Integer)analysis.Content2)[2];
        _PLCCommand[7] = Utilities.getBytes((Integer)analysis.Content2)[3];
        _PLCCommand[8] = Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0];
        _PLCCommand[9] = Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1];
        _PLCCommand[10] = Utilities.getBytes(value.length / 2)[0];
        _PLCCommand[11] = Utilities.getBytes(value.length / 2)[1];
        System.arraycopy(value, 0, _PLCCommand, 12, value.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(String address, boolean[] value, byte plcNumber) {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] buffer = MelsecHelper.TransBoolArrayToByteData(value);
        byte[] _PLCCommand = new byte[12 + buffer.length];
        _PLCCommand[0] = 2;
        _PLCCommand[1] = plcNumber;
        _PLCCommand[2] = 10;
        _PLCCommand[3] = 0;
        _PLCCommand[4] = Utilities.getBytes((Integer)analysis.Content2)[0];
        _PLCCommand[5] = Utilities.getBytes((Integer)analysis.Content2)[1];
        _PLCCommand[6] = Utilities.getBytes((Integer)analysis.Content2)[2];
        _PLCCommand[7] = Utilities.getBytes((Integer)analysis.Content2)[3];
        _PLCCommand[8] = Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0];
        _PLCCommand[9] = Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1];
        _PLCCommand[10] = Utilities.getBytes(value.length)[0];
        _PLCCommand[11] = Utilities.getBytes(value.length)[1];
        System.arraycopy(buffer, 0, _PLCCommand, 12, buffer.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResult CheckResponseLegal(byte[] response) {
        if (response.length < 2) {
            return new OperateResult(StringResources.Language.ReceiveDataLengthTooShort());
        }
        if (response[1] == 0) {
            return OperateResult.CreateSuccessResult();
        }
        if (response[1] == 91) {
            return new OperateResult(response[2], StringResources.Language.MelsecPleaseReferToManualDocument());
        }
        return new OperateResult(response[1], StringResources.Language.MelsecPleaseReferToManualDocument());
    }

    public static OperateResultExOne<byte[]> ExtractActualData(byte[] response, boolean isBit) {
        if (isBit) {
            byte[] Content = new byte[(response.length - 2) * 2];
            for (int i = 2; i < response.length; ++i) {
                if ((response[i] & 0x10) == 16) {
                    Content[(i - 2) * 2 + 0] = 1;
                }
                if ((response[i] & 1) != 1) continue;
                Content[(i - 2) * 2 + 1] = 1;
            }
            return OperateResultExOne.CreateSuccessResult(Content);
        }
        byte[] Content = new byte[response.length - 2];
        System.arraycopy(response, 2, Content, 0, Content.length);
        return OperateResultExOne.CreateSuccessResult(Content);
    }
}

