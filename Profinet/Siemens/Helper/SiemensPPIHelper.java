/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Siemens.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.S7AddressData;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class SiemensPPIHelper {
    public static boolean CheckReceiveDataComplete(MemoryStream ms) {
        byte[] buffer = ms.ToArray();
        if (buffer.length == 1 && buffer[0] == -27) {
            return true;
        }
        return buffer.length > 6 && buffer[0] == 104 && buffer[1] + 6 == buffer.length && buffer[buffer.length - 1] == 22;
    }

    public static OperateResultExThree<Byte, Integer, Integer> AnalysisAddress(String address) {
        OperateResultExThree<Byte, Integer, Integer> result;
        block13: {
            result = new OperateResultExThree<Byte, Integer, Integer>();
            try {
                result.Content3 = 0;
                if (address.substring(0, 2).equals("AI")) {
                    result.Content1 = 6;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(2), false);
                    break block13;
                }
                if (address.substring(0, 2).equals("AQ")) {
                    result.Content1 = 7;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(2), false);
                    break block13;
                }
                if (address.charAt(0) == 'T') {
                    result.Content1 = 31;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
                    break block13;
                }
                if (address.charAt(0) == 'C') {
                    result.Content1 = 30;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
                    break block13;
                }
                if (address.substring(0, 2).equals("SM")) {
                    result.Content1 = 5;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(2), false);
                    break block13;
                }
                if (address.charAt(0) == 'S') {
                    result.Content1 = 4;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
                    break block13;
                }
                if (address.charAt(0) == 'I') {
                    result.Content1 = (byte)-127;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
                    break block13;
                }
                if (address.charAt(0) == 'Q') {
                    result.Content1 = (byte)-126;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
                    break block13;
                }
                if (address.charAt(0) == 'M') {
                    result.Content1 = (byte)-125;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
                    break block13;
                }
                if (address.charAt(0) == 'D' || address.substring(0, 2).equals("DB")) {
                    result.Content1 = (byte)-124;
                    String[] adds = address.split("\\.");
                    result.Content3 = address.charAt(1) == 'B' ? Integer.valueOf(Integer.parseInt(adds[0].substring(2))) : Integer.valueOf(Integer.parseInt(adds[0].substring(1)));
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(address.indexOf(".") + 1), false);
                    break block13;
                }
                if (address.charAt(0) == 'V') {
                    result.Content1 = (byte)-124;
                    result.Content3 = 1;
                    result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
                    break block13;
                }
                result.Message = StringResources.Language.NotSupportedDataType();
                result.Content1 = 0;
                result.Content2 = 0;
                result.Content3 = 0;
                return result;
            }
            catch (Exception ex) {
                result.Message = ex.getMessage();
                return result;
            }
        }
        result.IsSuccess = true;
        return result;
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(byte station, String address, short length, boolean isBit) {
        OperateResultExThree<Byte, Integer, Integer> analysis = SiemensPPIHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] _PLCCommand = new byte[33];
        _PLCCommand[0] = 104;
        _PLCCommand[1] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[2] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[3] = 104;
        _PLCCommand[4] = station;
        _PLCCommand[5] = 0;
        _PLCCommand[6] = 108;
        _PLCCommand[7] = 50;
        _PLCCommand[8] = 1;
        _PLCCommand[9] = 0;
        _PLCCommand[10] = 0;
        _PLCCommand[11] = 0;
        _PLCCommand[12] = 0;
        _PLCCommand[13] = 0;
        _PLCCommand[14] = 14;
        _PLCCommand[15] = 0;
        _PLCCommand[16] = 0;
        _PLCCommand[17] = 4;
        _PLCCommand[18] = 1;
        _PLCCommand[19] = 18;
        _PLCCommand[20] = 10;
        _PLCCommand[21] = 16;
        _PLCCommand[22] = (byte) (isBit ? 1 : 2);
        _PLCCommand[23] = 0;
        _PLCCommand[24] = Utilities.getBytes(length)[0];
        _PLCCommand[25] = Utilities.getBytes(length)[1];
        _PLCCommand[26] = ((Integer)analysis.Content3).byteValue();
        _PLCCommand[27] = (Byte)analysis.Content1;
        _PLCCommand[28] = Utilities.getBytes((Integer)analysis.Content2)[2];
        _PLCCommand[29] = Utilities.getBytes((Integer)analysis.Content2)[1];
        _PLCCommand[30] = Utilities.getBytes((Integer)analysis.Content2)[0];
        int count = 0;
        for (int i = 4; i < 31; ++i) {
            count += _PLCCommand[i];
        }
        _PLCCommand[31] = Utilities.getBytes(count)[0];
        _PLCCommand[32] = 22;
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte station, String address, byte[] values) {
        OperateResultExThree<Byte, Integer, Integer> analysis = SiemensPPIHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        int length = values.length;
        byte[] _PLCCommand = new byte[37 + values.length];
        _PLCCommand[0] = 104;
        _PLCCommand[1] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[2] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[3] = 104;
        _PLCCommand[4] = station;
        _PLCCommand[5] = 0;
        _PLCCommand[6] = 124;
        _PLCCommand[7] = 50;
        _PLCCommand[8] = 1;
        _PLCCommand[9] = 0;
        _PLCCommand[10] = 0;
        _PLCCommand[11] = 0;
        _PLCCommand[12] = 0;
        _PLCCommand[13] = 0;
        _PLCCommand[14] = 14;
        _PLCCommand[15] = 0;
        _PLCCommand[16] = (byte)(values.length + 4);
        _PLCCommand[17] = 5;
        _PLCCommand[18] = 1;
        _PLCCommand[19] = 18;
        _PLCCommand[20] = 10;
        _PLCCommand[21] = 16;
        _PLCCommand[22] = 2;
        _PLCCommand[23] = 0;
        _PLCCommand[24] = Utilities.getBytes(length)[0];
        _PLCCommand[25] = Utilities.getBytes(length)[1];
        _PLCCommand[26] = ((Integer)analysis.Content3).byteValue();
        _PLCCommand[27] = (Byte)analysis.Content1;
        _PLCCommand[28] = Utilities.getBytes((Integer)analysis.Content2)[2];
        _PLCCommand[29] = Utilities.getBytes((Integer)analysis.Content2)[1];
        _PLCCommand[30] = Utilities.getBytes((Integer)analysis.Content2)[0];
        _PLCCommand[31] = 0;
        _PLCCommand[32] = 4;
        _PLCCommand[33] = Utilities.getBytes(length * 8)[1];
        _PLCCommand[34] = Utilities.getBytes(length * 8)[0];
        System.arraycopy(values, 0, _PLCCommand, 35, values.length);
        int count = 0;
        for (int i = 4; i < _PLCCommand.length - 2; ++i) {
            count += _PLCCommand[i];
        }
        _PLCCommand[_PLCCommand.length - 2] = Utilities.getBytes(count)[0];
        _PLCCommand[_PLCCommand.length - 1] = 22;
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static String GetMsgFromStatus(byte code) {
        switch (code) {
            case -1: {
                return "No error";
            }
            case 1: {
                return "Hardware fault";
            }
            case 3: {
                return "Illegal object access";
            }
            case 5: {
                return "Invalid address(incorrent variable address)";
            }
            case 6: {
                return "Data type is not supported";
            }
            case 10: {
                return "Object does not exist or length error";
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static String GetMsgFromStatus(byte errorClass, byte errorCode) {
        if (errorClass == -128 && errorCode == 1) {
            return "Switch\u2002in\u2002wrong\u2002position\u2002for\u2002requested\u2002operation";
        }
        if (errorClass == -127 && errorCode == 4) {
            return "Miscellaneous\u2002structure\u2002error\u2002in\u2002command.\u2002\u2002Command is not supportedby CPU";
        }
        if (errorClass == -124 && errorCode == 4) {
            return "CPU is busy processing an upload or download CPU cannot process command because of system fault condition";
        }
        if (errorClass == -123 && errorCode == 0) {
            return "Length fields are not correct or do not agree with the amount of data received";
        }
        if (errorClass == -46) {
            return "Error in upload or download command";
        }
        if (errorClass == -42) {
            return "Protection error(password)";
        }
        if (errorClass == -36 && errorCode == 1) {
            return "Error in time-of-day clock data";
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte station, String address, boolean[] values) {
        OperateResultExThree<Byte, Integer, Integer> analysis = SiemensPPIHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] bytesValue = SoftBasic.BoolArrayToByte(values);
        byte[] _PLCCommand = new byte[37 + bytesValue.length];
        _PLCCommand[0] = 104;
        _PLCCommand[1] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[2] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[3] = 104;
        _PLCCommand[4] = station;
        _PLCCommand[5] = 0;
        _PLCCommand[6] = 124;
        _PLCCommand[7] = 50;
        _PLCCommand[8] = 1;
        _PLCCommand[9] = 0;
        _PLCCommand[10] = 0;
        _PLCCommand[11] = 0;
        _PLCCommand[12] = 0;
        _PLCCommand[13] = 0;
        _PLCCommand[14] = 14;
        _PLCCommand[15] = 0;
        _PLCCommand[16] = 5;
        _PLCCommand[17] = 5;
        _PLCCommand[18] = 1;
        _PLCCommand[19] = 18;
        _PLCCommand[20] = 10;
        _PLCCommand[21] = 16;
        _PLCCommand[22] = 1;
        _PLCCommand[23] = 0;
        _PLCCommand[24] = Utilities.getBytes(values.length)[0];
        _PLCCommand[25] = Utilities.getBytes(values.length)[1];
        _PLCCommand[26] = ((Integer)analysis.Content3).byteValue();
        _PLCCommand[27] = (Byte)analysis.Content1;
        _PLCCommand[28] = Utilities.getBytes((Integer)analysis.Content2)[2];
        _PLCCommand[29] = Utilities.getBytes((Integer)analysis.Content2)[1];
        _PLCCommand[30] = Utilities.getBytes((Integer)analysis.Content2)[0];
        _PLCCommand[31] = 0;
        _PLCCommand[32] = 3;
        _PLCCommand[33] = Utilities.getBytes(values.length)[1];
        _PLCCommand[34] = Utilities.getBytes(values.length)[0];
        System.arraycopy(bytesValue, 0, _PLCCommand, 35, bytesValue.length);
        int count = 0;
        for (int i = 4; i < _PLCCommand.length - 2; ++i) {
            count += _PLCCommand[i];
        }
        _PLCCommand[_PLCCommand.length - 2] = Utilities.getBytes(count)[0];
        _PLCCommand[_PLCCommand.length - 1] = 22;
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResult CheckResponse(byte[] content) {
        if (content.length < 21) {
            return new OperateResult(10000, "Failed, data too short:" + SoftBasic.ByteToHexString(content, ' '));
        }
        if (content[17] != 0 || content[18] != 0) {
            return new OperateResult(content[19], SiemensPPIHelper.GetMsgFromStatus(content[18], content[19]));
        }
        if (content[21] != -1) {
            return new OperateResult(content[21], SiemensPPIHelper.GetMsgFromStatus(content[21]));
        }
        return OperateResult.CreateSuccessResult();
    }

    public static byte[] GetExecuteConfirm(byte station) {
        byte[] buffer = new byte[]{16, 2, 0, 92, 94, 22};
        buffer[1] = station;
        int count = 0;
        for (int i = 1; i < 4; ++i) {
            count += buffer[i];
        }
        buffer[4] = (byte)count;
        return buffer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OperateResultExOne<byte[]> Read(IReadWriteDevice plc, String address, short length, byte station, Object communicationLock) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = SiemensPPIHelper.BuildReadCommand(station, address, length, false);
        if (!command.IsSuccess) {
            return command;
        }
        Object object = communicationLock;
        synchronized (object) {
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer((byte[])command.Content);
            if (!read1.IsSuccess) {
                return read1;
            }
            if (((byte[])read1.Content)[0] != -27) {
                return new OperateResultExOne<byte[]>("PLC Receive Check Failed:" + SoftBasic.ByteToHexString((byte[])read1.Content, ' '));
            }
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(SiemensPPIHelper.GetExecuteConfirm(station));
            if (!read2.IsSuccess) {
                return read2;
            }
            OperateResult check = SiemensPPIHelper.CheckResponse((byte[])read2.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            byte[] buffer = new byte[length];
            if (((byte[])read2.Content)[21] == -1 && ((byte[])read2.Content)[22] == 4) {
                System.arraycopy(read2.Content, 25, buffer, 0, length);
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice plc, String address, short length, byte station, Object communicationLock) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = SiemensPPIHelper.BuildReadCommand(station, address, length, true);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        Object object = communicationLock;
        synchronized (object) {
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer((byte[])command.Content);
            if (!read1.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read1);
            }
            if (((byte[])read1.Content)[0] != -27) {
                return new OperateResultExOne<boolean[]>("PLC Receive Check Failed:" + SoftBasic.ByteToHexString((byte[])read1.Content, ' '));
            }
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(SiemensPPIHelper.GetExecuteConfirm(station));
            if (!read2.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read2);
            }
            OperateResult check = SiemensPPIHelper.CheckResponse((byte[])read2.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            byte[] buffer = new byte[((byte[])read2.Content).length - 27];
            if (((byte[])read2.Content)[21] == -1 && ((byte[])read2.Content)[22] == 3) {
                System.arraycopy(read2.Content, 25, buffer, 0, buffer.length);
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(buffer, length));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OperateResult Write(IReadWriteDevice plc, String address, byte[] value, byte station, Object communicationLock) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = SiemensPPIHelper.BuildWriteCommand(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        Object object = communicationLock;
        synchronized (object) {
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer((byte[])command.Content);
            if (!read1.IsSuccess) {
                return read1;
            }
            if (((byte[])read1.Content)[0] != -27) {
                return new OperateResultExOne("PLC Receive Check Failed:" + ((byte[])read1.Content)[0]);
            }
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(SiemensPPIHelper.GetExecuteConfirm(station));
            if (!read2.IsSuccess) {
                return read2;
            }
            OperateResult check = SiemensPPIHelper.CheckResponse((byte[])read2.Content);
            if (!check.IsSuccess) {
                return check;
            }
            return OperateResult.CreateSuccessResult();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OperateResult Write(IReadWriteDevice plc, String address, boolean[] value, byte station, Object communicationLock) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = SiemensPPIHelper.BuildWriteCommand(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        Object object = communicationLock;
        synchronized (object) {
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer((byte[])command.Content);
            if (!read1.IsSuccess) {
                return read1;
            }
            if (((byte[])read1.Content)[0] != -27) {
                return new OperateResultExOne("PLC Receive Check Failed:" + ((byte[])read1.Content)[0]);
            }
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(SiemensPPIHelper.GetExecuteConfirm(station));
            if (!read2.IsSuccess) {
                return read2;
            }
            OperateResult check = SiemensPPIHelper.CheckResponse((byte[])read2.Content);
            if (!check.IsSuccess) {
                return check;
            }
            return OperateResult.CreateSuccessResult();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OperateResult Start(IReadWriteDevice plc, String parameter, byte station, Object communicationLock) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(parameter, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
        }
        byte[] cmd = new byte[]{104, 33, 33, 104, station, 0, 108, 50, 1, 0, 0, 0, 0, 0, 20, 0, 0, 40, 0, 0, 0, 0, 0, 0, -3, 0, 0, 9, 80, 95, 80, 82, 79, 71, 82, 65, 77, -86, 22};
        Object object = communicationLock;
        synchronized (object) {
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(cmd);
            if (!read1.IsSuccess) {
                return read1;
            }
            if (((byte[])read1.Content)[0] != -27) {
                return new OperateResultExOne("PLC Receive Check Failed:" + ((byte[])read1.Content)[0]);
            }
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(SiemensPPIHelper.GetExecuteConfirm(station));
            if (!read2.IsSuccess) {
                return read2;
            }
            return OperateResult.CreateSuccessResult();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OperateResult Stop(IReadWriteDevice plc, String parameter, byte station, Object communicationLock) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(parameter, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
        }
        byte[] cmd = new byte[]{104, 29, 29, 104, station, 0, 108, 50, 1, 0, 0, 0, 0, 0, 16, 0, 0, 41, 0, 0, 0, 0, 0, 9, 80, 95, 80, 82, 79, 71, 82, 65, 77, -86, 22};
        Object object = communicationLock;
        synchronized (object) {
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(cmd);
            if (!read1.IsSuccess) {
                return read1;
            }
            if (((byte[])read1.Content)[0] != -27) {
                return new OperateResultExOne("PLC Receive Check Failed:" + ((byte[])read1.Content)[0]);
            }
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(SiemensPPIHelper.GetExecuteConfirm(station));
            if (!read2.IsSuccess) {
                return read2;
            }
            return OperateResult.CreateSuccessResult();
        }
    }
}

