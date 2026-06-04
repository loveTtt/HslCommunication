/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.Profinet.Melsec.MelsecMcDataType;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecFxSerialHelper {
    public static OperateResultExOne<byte[]> Read(IReadWriteDevice plc, String address, short length, boolean isNewVersion) {
        OperateResultExOne<ArrayList<byte[]>> command = MelsecFxSerialHelper.BuildReadWordCommand(address, length, isNewVersion);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult ackResult = MelsecFxSerialHelper.CheckPlcReadResponse((byte[])read.Content);
            if (!ackResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(ackResult);
            }
            OperateResultExOne<byte[]> extra = MelsecFxSerialHelper.ExtractActualData((byte[])read.Content);
            if (!extra.IsSuccess) {
                return extra;
            }
            Utilities.ArrayListAddArray(array, (byte[])extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(array));
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice plc, String address, short length, boolean isNewVersion) {
        OperateResultExTwo<ArrayList<byte[]>, Integer> command = MelsecFxSerialHelper.BuildReadBoolCommand(address, length, isNewVersion);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content1).size(); ++i) {
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])((ArrayList)command.Content1).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult ackResult = MelsecFxSerialHelper.CheckPlcReadResponse((byte[])read.Content);
            if (!ackResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(ackResult);
            }
            OperateResultExOne<byte[]> extra = MelsecFxSerialHelper.ExtractActualData((byte[])read.Content);
            if (!extra.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(extra);
            }
            Utilities.ArrayListAddArray(array, (byte[])extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(Utilities.ToByteArray(array)), (Integer)command.Content2, length));
    }

    public static OperateResult Write(IReadWriteDevice plc, String address, byte[] value, boolean isNewVersion) {
        OperateResultExOne<byte[]> command = MelsecFxSerialHelper.BuildWriteWordCommand(address, value, isNewVersion);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecFxSerialHelper.CheckPlcWriteResponse((byte[])read.Content);
    }

    public static OperateResult Write(IReadWriteDevice plc, String address, boolean value) {
        OperateResultExOne<byte[]> command = MelsecFxSerialHelper.BuildWriteBoolPacket(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecFxSerialHelper.CheckPlcWriteResponse((byte[])read.Content);
    }

    public static OperateResult ActivePlc(IReadWriteDevice plc) {
        OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(new byte[]{5});
        if (!read1.IsSuccess) {
            return read1;
        }
        if (((byte[])read1.Content)[0] != 6) {
            return new OperateResult("Send 0x05, Check Receive 0x06 failed");
        }
        OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(new byte[]{2, 48, 48, 69, 48, 50, 48, 50, 3, 54, 67});
        if (!read2.IsSuccess) {
            return read2;
        }
        return plc.ReadFromCoreServer(new byte[]{2, 48, 48, 69, 48, 50, 48, 50, 3, 54, 67});
    }

    public static OperateResult CheckPlcReadResponse(byte[] ack) {
        if (ack.length == 0) {
            return new OperateResult(StringResources.Language.MelsecFxReceiveZero());
        }
        if (ack[0] == 21) {
            return new OperateResult(StringResources.Language.MelsecFxAckNagative() + " Actual: " + SoftBasic.ByteToHexString(ack, ' '));
        }
        if (ack[0] != 2) {
            return new OperateResult(StringResources.Language.MelsecFxAckWrong() + ack[0] + " Actual: " + SoftBasic.ByteToHexString(ack, ' '));
        }
        try {
            if (!MelsecHelper.CheckCRC(ack)) {
                return new OperateResult(StringResources.Language.MelsecFxCrcCheckFailed() + " Actual: " + SoftBasic.ByteToHexString(ack, ' '));
            }
        }
        catch (Exception ex) {
            return new OperateResult(StringResources.Language.MelsecFxCrcCheckFailed() + ex.getMessage() + "\r\nActual: " + SoftBasic.ByteToHexString(ack, ' '));
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult CheckPlcWriteResponse(byte[] ack) {
        if (ack.length == 0) {
            return new OperateResult(StringResources.Language.MelsecFxReceiveZero());
        }
        if (ack[0] == 21) {
            return new OperateResult(StringResources.Language.MelsecFxAckNagative() + " Actual: " + SoftBasic.ByteToHexString(ack, ' '));
        }
        if (ack[0] != 6) {
            return new OperateResult(StringResources.Language.MelsecFxAckWrong() + ack[0] + " Actual: " + SoftBasic.ByteToHexString(ack, ' '));
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolPacket(String address, boolean value) {
        OperateResultExTwo<MelsecMcDataType, Integer> analysis = MelsecFxSerialHelper.FxAnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        short startAddress = ((Integer)analysis.Content2).shortValue();
        if (analysis.Content1 == MelsecMcDataType.M) {
            startAddress = startAddress >= 8000 ? (short)(startAddress - 8000 + 3840) : (short)(startAddress + 2048);
        } else if (analysis.Content1 == MelsecMcDataType.S) {
            startAddress = (short)(startAddress + 0);
        } else if (analysis.Content1 == MelsecMcDataType.X) {
            startAddress = (short)(startAddress + 1024);
        } else if (analysis.Content1 == MelsecMcDataType.Y) {
            startAddress = (short)(startAddress + 1280);
        } else if (analysis.Content1 == MelsecMcDataType.CS) {
            startAddress = (short)(startAddress + 448);
        } else if (analysis.Content1 == MelsecMcDataType.CC) {
            startAddress = (short)(startAddress + 960);
        } else if (analysis.Content1 == MelsecMcDataType.CN) {
            startAddress = (short)(startAddress + 3584);
        } else if (analysis.Content1 == MelsecMcDataType.TS) {
            startAddress = (short)(startAddress + 192);
        } else if (analysis.Content1 == MelsecMcDataType.TC) {
            startAddress = (short)(startAddress + 704);
        } else if (analysis.Content1 == MelsecMcDataType.TN) {
            startAddress = (short)(startAddress + 1536);
        } else {
            return new OperateResultExOne<byte[]>(StringResources.Language.MelsecCurrentTypeNotSupportedBitOperate());
        }
        byte[] _PLCCommand = new byte[9];
        _PLCCommand[0] = 2;
        _PLCCommand[1] = (byte) (value ? 55 : 56);
        _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
        _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
        _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];
        _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
        _PLCCommand[6] = 3;
        Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 7);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadWordCommand(String address, short length, boolean isNewVersion) {
        OperateResultExOne<Integer> addressResult = MelsecFxSerialHelper.FxCalculateWordStartAddress(address, isNewVersion);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        length = (short)(length * 2);
        short startAddress = ((Integer)addressResult.Content).shortValue();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 254);
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        for (int i = 0; i < splits.length; ++i) {
            byte[] _PLCCommand;
            if (isNewVersion) {
                _PLCCommand = new byte[13];
                _PLCCommand[0] = 2;
                _PLCCommand[1] = 69;
                _PLCCommand[2] = 48;
                _PLCCommand[3] = 48;
                _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];
                _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
                _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
                _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
                _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom((byte)length)[0];
                _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom((byte)length)[1];
                _PLCCommand[10] = 3;
                Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 11);
                array.add(_PLCCommand);
                startAddress = (short)(startAddress + splits[i]);
                continue;
            }
            _PLCCommand = new byte[11];
            _PLCCommand[0] = 2;
            _PLCCommand[1] = 48;
            _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];
            _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
            _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
            _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
            _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom((byte)length)[0];
            _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom((byte)length)[1];
            _PLCCommand[8] = 3;
            Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 9);
            array.add(_PLCCommand);
            startAddress = (short)(startAddress + splits[i]);
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    public static OperateResultExTwo<ArrayList<byte[]>, Integer> BuildReadBoolCommand(String address, short length, boolean isNewVersion) {
        OperateResultExThree<Integer, Integer, Integer> addressResult = MelsecFxSerialHelper.FxCalculateBoolStartAddress(address, isNewVersion);
        if (!addressResult.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(addressResult);
        }
        short length2 = (short)(((Integer)addressResult.Content2 + length - 1) / 8 - (Integer)addressResult.Content2 / 8 + 1);
        short startAddress = ((Integer)addressResult.Content1).shortValue();
        int[] splits = SoftBasic.SplitIntegerToArray(length2, 254);
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        for (int i = 0; i < splits.length; ++i) {
            byte[] _PLCCommand;
            if (isNewVersion) {
                _PLCCommand = new byte[13];
                _PLCCommand[0] = 2;
                _PLCCommand[1] = 69;
                _PLCCommand[2] = 48;
                _PLCCommand[3] = 48;
                _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];
                _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
                _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
                _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
                _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom((byte)length2)[0];
                _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom((byte)length2)[1];
                _PLCCommand[10] = 3;
                Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 11);
                array.add(_PLCCommand);
                continue;
            }
            _PLCCommand = new byte[11];
            _PLCCommand[0] = 2;
            _PLCCommand[1] = 48;
            _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];
            _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
            _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
            _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
            _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom((byte)length2)[0];
            _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom((byte)length2)[1];
            _PLCCommand[8] = 3;
            Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 9);
            array.add(_PLCCommand);
        }
        return OperateResultExTwo.CreateSuccessResult(array, Integer.valueOf((Integer)addressResult.Content3));
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(String address, byte[] value, boolean isNewVersion) {
        OperateResultExOne<Integer> addressResult = MelsecFxSerialHelper.FxCalculateWordStartAddress(address, isNewVersion);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        if (value != null) {
            value = SoftBasic.BuildAsciiBytesFrom(value);
        }
        short startAddress = ((Integer)addressResult.Content).shortValue();
        if (isNewVersion) {
            byte[] _PLCCommand = new byte[13 + value.length];
            _PLCCommand[0] = 2;
            _PLCCommand[1] = 69;
            _PLCCommand[2] = 49;
            _PLCCommand[3] = 48;
            _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];
            _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
            _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
            _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
            _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom((byte)(value.length / 2))[0];
            _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom((byte)(value.length / 2))[1];
            System.arraycopy(value, 0, _PLCCommand, 10, value.length);
            _PLCCommand[_PLCCommand.length - 3] = 3;
            Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, _PLCCommand.length - 2);
            return OperateResultExOne.CreateSuccessResult(_PLCCommand);
        }
        byte[] _PLCCommand = new byte[11 + value.length];
        _PLCCommand[0] = 2;
        _PLCCommand[1] = 49;
        _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];
        _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
        _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
        _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
        _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom((byte)(value.length / 2))[0];
        _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom((byte)(value.length / 2))[1];
        System.arraycopy(value, 0, _PLCCommand, 8, value.length);
        _PLCCommand[_PLCCommand.length - 3] = 3;
        Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, _PLCCommand.length - 2);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> ExtractActualData(byte[] response) {
        try {
            byte[] data = new byte[(response.length - 4) / 2];
            for (int i = 0; i < data.length; ++i) {
                byte[] buffer = new byte[]{response[i * 2 + 1], response[i * 2 + 2]};
                data[i] = (byte)Integer.parseInt(new String(buffer, StandardCharsets.US_ASCII), 16);
            }
            return OperateResultExOne.CreateSuccessResult(data);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Extract Msg\uff1a" + ex.getMessage() + "\r\nData: " + SoftBasic.ByteToHexString(response));
        }
    }

    public static OperateResultExOne<boolean[]> ExtractActualBoolData(byte[] response, int start, int length) {
        OperateResultExOne<byte[]> extraResult = MelsecFxSerialHelper.ExtractActualData(response);
        if (!extraResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extraResult);
        }
        try {
            boolean[] data = new boolean[length];
            boolean[] array = SoftBasic.ByteToBoolArray((byte[])extraResult.Content, ((byte[])extraResult.Content).length * 8);
            for (int i = 0; i < length; ++i) {
                data[i] = array[i + start];
            }
            return OperateResultExOne.CreateSuccessResult(data);
        }
        catch (Exception ex) {
            return new OperateResultExOne<boolean[]>("Extract Msg\uff1a" + ex.getMessage() + "\r\nData: " + SoftBasic.ByteToHexString(response));
        }
    }

    private static OperateResultExTwo<MelsecMcDataType, Integer> FxAnalysisAddress(String address) {
        OperateResultExTwo<MelsecMcDataType, Integer> result = new OperateResultExTwo<MelsecMcDataType, Integer>();
        try {
            switch (address.charAt(0)) {
                case 'M': 
                case 'm': {
                    result.Content1 = MelsecMcDataType.M;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecMcDataType.M.getFromBase());
                    break;
                }
                case 'X': 
                case 'x': {
                    result.Content1 = MelsecMcDataType.X;
                    result.Content2 = Integer.parseInt(address.substring(1), 8);
                    break;
                }
                case 'Y': 
                case 'y': {
                    result.Content1 = MelsecMcDataType.Y;
                    result.Content2 = Integer.parseInt(address.substring(1), 8);
                    break;
                }
                case 'D': 
                case 'd': {
                    result.Content1 = MelsecMcDataType.D;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecMcDataType.D.getFromBase());
                    break;
                }
                case 'S': 
                case 's': {
                    result.Content1 = MelsecMcDataType.S;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecMcDataType.S.getFromBase());
                    break;
                }
                case 'T': 
                case 't': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecMcDataType.TN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecMcDataType.TN.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecMcDataType.TS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecMcDataType.TS.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecMcDataType.TC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecMcDataType.TC.getFromBase());
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'C': 
                case 'c': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecMcDataType.CN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecMcDataType.CN.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecMcDataType.CS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecMcDataType.CS.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecMcDataType.CC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecMcDataType.CC.getFromBase());
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
        }
        catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }
        result.IsSuccess = true;
        return result;
    }

    private static OperateResultExOne<Integer> FxCalculateWordStartAddress(String address, boolean isNewVersion) {
        OperateResultExTwo<MelsecMcDataType, Integer> analysis = MelsecFxSerialHelper.FxAnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        int startAddress = (Integer)analysis.Content2;
        if (analysis.Content1 == MelsecMcDataType.D) {
            startAddress = startAddress >= 8000 ? (int)((short)((startAddress - 8000) * 2 + (isNewVersion ? 32768 : 3584))) : (int)(isNewVersion ? (short)(startAddress * 2 + 16384) : (short)(startAddress * 2 + 4096));
        } else if (analysis.Content1 == MelsecMcDataType.CN) {
            startAddress = startAddress >= 200 ? (int)((short)((startAddress - 200) * 4 + 3072)) : (int)((short)(startAddress * 2 + 2560));
        } else if (analysis.Content1 == MelsecMcDataType.TN) {
            startAddress = isNewVersion ? (int)((short)(startAddress * 2 + 4096)) : (int)((short)(startAddress * 2 + 2048));
        } else {
            return new OperateResultExOne<Integer>(StringResources.Language.MelsecCurrentTypeNotSupportedWordOperate());
        }
        return OperateResultExOne.CreateSuccessResult(startAddress);
    }

    private static OperateResultExThree<Integer, Integer, Integer> FxCalculateBoolStartAddress(String address, boolean isNewVersion) {
        OperateResultExTwo<MelsecMcDataType, Integer> analysis = MelsecFxSerialHelper.FxAnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(analysis);
        }
        int startAddress = (Integer)analysis.Content2;
        if (analysis.Content1 == MelsecMcDataType.M) {
            startAddress = isNewVersion ? (startAddress >= 8000 ? (startAddress - 8000) / 8 + 35840 : startAddress / 8 + 34816) : (startAddress >= 8000 ? (startAddress - 8000) / 8 + 480 : startAddress / 8 + 256);
        } else if (analysis.Content1 == MelsecMcDataType.X) {
            startAddress = startAddress / 8 + (isNewVersion ? 36000 : 128);
        } else if (analysis.Content1 == MelsecMcDataType.Y) {
            startAddress = startAddress / 8 + (isNewVersion ? 35776 : 160);
        } else if (analysis.Content1 == MelsecMcDataType.S) {
            startAddress = startAddress / 8 + (isNewVersion ? 36064 : 0);
        } else if (analysis.Content1 == MelsecMcDataType.CS) {
            startAddress = startAddress / 8 + (isNewVersion ? 37696 : 448);
        } else if (analysis.Content1 == MelsecMcDataType.CC) {
            startAddress = startAddress / 8 + (isNewVersion ? 37600 : 960);
        } else if (analysis.Content1 == MelsecMcDataType.TS) {
            startAddress = startAddress / 8 + (isNewVersion ? 37728 : 192);
        } else if (analysis.Content1 == MelsecMcDataType.TC) {
            startAddress = startAddress / 8 + (isNewVersion ? 37632 : 704);
        } else {
            return new OperateResultExThree<Integer, Integer, Integer>(StringResources.Language.MelsecCurrentTypeNotSupportedBitOperate());
        }
        return OperateResultExThree.CreateSuccessResult(startAddress, analysis.Content2, (Integer)analysis.Content2 % 8);
    }
}

