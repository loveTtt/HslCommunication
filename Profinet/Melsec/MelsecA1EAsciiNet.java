/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MelsecA1EAsciiMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.MelsecA1EDataType;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecA1EAsciiNet
extends NetworkDeviceBase {
    private byte PLCNumber = (byte)-1;

    public MelsecA1EAsciiNet() {
        this.WordLength = 1;
        this.LogMsgFormatBinary = false;
        this.setByteTransform(new RegularByteTransform());
    }

    public MelsecA1EAsciiNet(String ipAddress, int port) {
        this();
        super.setIpAddress(ipAddress);
        super.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecA1EAsciiMessage();
    }

    public byte getPLCNumber() {
        return this.PLCNumber;
    }

    public void setPLCNumber(byte plcNumber) {
        this.PLCNumber = plcNumber;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = MelsecA1EAsciiNet.BuildReadCommand(address, length, false, this.PLCNumber);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult check = MelsecA1EAsciiNet.CheckResponseLegal((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            OperateResultExOne<byte[]> extra = MelsecA1EAsciiNet.ExtractActualData((byte[])read.Content, false);
            if (!extra.IsSuccess) {
                return extra;
            }
            Utilities.ArrayListAddArray(array, (byte[])extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(array));
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command = MelsecA1EAsciiNet.BuildWriteWordCommand(address, value, this.PLCNumber);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = MelsecA1EAsciiNet.CheckResponseLegal((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = MelsecA1EAsciiNet.BuildReadCommand(address, length, true, this.PLCNumber);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult check = MelsecA1EAsciiNet.CheckResponseLegal((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            OperateResultExOne<byte[]> extract = MelsecA1EAsciiNet.ExtractActualData((byte[])read.Content, true);
            if (!extract.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(extract);
            }
            Utilities.ArrayListAddArray(array, (byte[])extract.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBoolArray(array, length));
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        OperateResultExOne<byte[]> command = MelsecA1EAsciiNet.BuildWriteBoolCommand(address, values, this.PLCNumber);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecA1EAsciiNet.CheckResponseLegal((byte[])read.Content);
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
            byte[] _PLCCommand = new byte[24];
            _PLCCommand[0] = SoftBasic.BuildAsciiBytesFrom(subtitle)[0];
            _PLCCommand[1] = SoftBasic.BuildAsciiBytesFrom(subtitle)[1];
            _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[0];
            _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[1];
            _PLCCommand[4] = 48;
            _PLCCommand[5] = 48;
            _PLCCommand[6] = 48;
            _PLCCommand[7] = 65;
            _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1])[0];
            _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1])[1];
            _PLCCommand[10] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0])[0];
            _PLCCommand[11] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0])[1];
            _PLCCommand[12] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[3])[0];
            _PLCCommand[13] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[3])[1];
            _PLCCommand[14] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[2])[0];
            _PLCCommand[15] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[2])[1];
            _PLCCommand[16] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[1])[0];
            _PLCCommand[17] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[1])[1];
            _PLCCommand[18] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[0])[0];
            _PLCCommand[19] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[0])[1];
            int len = splits[i];
            if (len == 256) {
                len = 0;
            }
            _PLCCommand[20] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(len % 256)[0])[0];
            _PLCCommand[21] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(len % 256)[0])[1];
            _PLCCommand[22] = 48;
            _PLCCommand[23] = 48;
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
        value = MelsecHelper.TransByteArrayToAsciiByteArray(value);
        byte[] _PLCCommand = new byte[24 + value.length];
        _PLCCommand[0] = 48;
        _PLCCommand[1] = 51;
        _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[0];
        _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[1];
        _PLCCommand[4] = 48;
        _PLCCommand[5] = 48;
        _PLCCommand[6] = 48;
        _PLCCommand[7] = 65;
        _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1])[0];
        _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1])[1];
        _PLCCommand[10] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0])[0];
        _PLCCommand[11] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0])[1];
        _PLCCommand[12] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[3])[0];
        _PLCCommand[13] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[3])[1];
        _PLCCommand[14] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[2])[0];
        _PLCCommand[15] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[2])[1];
        _PLCCommand[16] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[1])[0];
        _PLCCommand[17] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[1])[1];
        _PLCCommand[18] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[0])[0];
        _PLCCommand[19] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[0])[1];
        _PLCCommand[20] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(value.length / 4)[0])[0];
        _PLCCommand[21] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(value.length / 4)[0])[1];
        _PLCCommand[22] = 48;
        _PLCCommand[23] = 48;
        System.arraycopy(value, 0, _PLCCommand, 24, value.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(String address, boolean[] value, byte plcNumber) {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] buffer = value.length % 2 == 1 ? new byte[value.length + 1] : new byte[value.length];
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = (byte) (i < value.length && value[i] ? 49 : 48);
        }
        byte[] _PLCCommand = new byte[24 + buffer.length];
        _PLCCommand[0] = 48;
        _PLCCommand[1] = 50;
        _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[0];
        _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[1];
        _PLCCommand[4] = 48;
        _PLCCommand[5] = 48;
        _PLCCommand[6] = 48;
        _PLCCommand[7] = 65;
        _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1])[0];
        _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[1])[1];
        _PLCCommand[10] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0])[0];
        _PLCCommand[11] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(((MelsecA1EDataType)analysis.Content1).getDataCode())[0])[1];
        _PLCCommand[12] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[3])[0];
        _PLCCommand[13] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[3])[1];
        _PLCCommand[14] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[2])[0];
        _PLCCommand[15] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[2])[1];
        _PLCCommand[16] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[1])[0];
        _PLCCommand[17] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[1])[1];
        _PLCCommand[18] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[0])[0];
        _PLCCommand[19] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes((Integer)analysis.Content2)[0])[1];
        _PLCCommand[20] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(value.length)[0])[0];
        _PLCCommand[21] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(value.length)[0])[1];
        _PLCCommand[22] = 48;
        _PLCCommand[23] = 48;
        System.arraycopy(buffer, 0, _PLCCommand, 24, buffer.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResult CheckResponseLegal(byte[] response) {
        if (response.length < 4) {
            return new OperateResult(StringResources.Language.ReceiveDataLengthTooShort());
        }
        if (response[2] == 48 && response[3] == 48) {
            return OperateResult.CreateSuccessResult();
        }
        if (response[2] == 53 && response[3] == 66) {
            return new OperateResult(Integer.parseInt(new String(response, 4, 2, StandardCharsets.US_ASCII), 16), StringResources.Language.MelsecPleaseReferToManualDocument());
        }
        return new OperateResult(Integer.parseInt(new String(response, 2, 2, StandardCharsets.US_ASCII), 16), StringResources.Language.MelsecPleaseReferToManualDocument());
    }

    public static OperateResultExOne<byte[]> ExtractActualData(byte[] response, boolean isBit) {
        if (isBit) {
            byte[] buffer = new byte[response.length - 4];
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = response[i + 4] == 48 ? (byte)0 : 1;
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(response, 4)));
    }
}

