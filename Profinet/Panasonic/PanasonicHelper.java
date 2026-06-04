/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Panasonic;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class PanasonicHelper {
    public static boolean CheckBoolOnWordAddress(String address) {
        return address.matches("^(s=[0-9]+;)?(D|LD|DT|F)[0-9]+\\.[0-9]+$");
    }

    private static String CalculateCrc(StringBuilder sb) {
        byte tmp = 0;
        tmp = (byte)sb.charAt(0);
        for (int i = 1; i < sb.length(); ++i) {
            tmp = (byte)(tmp ^ (byte)sb.charAt(i));
        }
        return SoftBasic.ByteToHexString(new byte[]{tmp});
    }

    public static int CalculateComplexAddress(String address) {
        int add = 0;
        if (!address.contains(".")) {
            add = address.length() == 1 ? Integer.parseInt(address, 16) : Integer.parseInt(address.substring(0, address.length() - 1)) * 16 + Integer.parseInt(address.substring(address.length() - 1), 16);
        } else {
            add = Integer.parseInt(address.substring(0, address.indexOf("."))) * 16;
            String bit = address.substring(address.indexOf(".") + 1);
            add = bit.contains("A") || bit.contains("B") || bit.contains("C") || bit.contains("D") || bit.contains("E") || bit.contains("F") ? (add += Integer.parseInt(bit, 16)) : (add += Integer.parseInt(bit));
        }
        return add;
    }

    public static OperateResultExTwo<String, Integer> AnalysisAddress(String address) {
        OperateResultExTwo<String, Integer> result;
        block18: {
            result = new OperateResultExTwo<String, Integer>();
            try {
                result.Content2 = 0;
                if (address.startsWith("IX") || address.startsWith("ix")) {
                    result.Content1 = "IX";
                    result.Content2 = Integer.parseInt(address.substring(2));
                    break block18;
                }
                if (address.startsWith("IY") || address.startsWith("iy")) {
                    result.Content1 = "IY";
                    result.Content2 = Integer.parseInt(address.substring(2));
                    break block18;
                }
                if (address.startsWith("ID") || address.startsWith("id")) {
                    result.Content1 = "ID";
                    result.Content2 = Integer.parseInt(address.substring(2));
                    break block18;
                }
                if (address.startsWith("SR") || address.startsWith("sr")) {
                    result.Content1 = "SR";
                    result.Content2 = PanasonicHelper.CalculateComplexAddress(address.substring(2));
                    break block18;
                }
                if (address.startsWith("LD") || address.startsWith("ld")) {
                    result.Content1 = "LD";
                    result.Content2 = Integer.parseInt(address.substring(2));
                    break block18;
                }
                if (address.startsWith("DT") || address.startsWith("dt")) {
                    result.Content1 = "D";
                    result.Content2 = Integer.parseInt(address.substring(2));
                    break block18;
                }
                if (address.charAt(0) == 'X' || address.charAt(0) == 'x') {
                    result.Content1 = "X";
                    result.Content2 = PanasonicHelper.CalculateComplexAddress(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'Y' || address.charAt(0) == 'y') {
                    result.Content1 = "Y";
                    result.Content2 = PanasonicHelper.CalculateComplexAddress(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'R' || address.charAt(0) == 'r') {
                    result.Content1 = "R";
                    result.Content2 = PanasonicHelper.CalculateComplexAddress(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'T' || address.charAt(0) == 't') {
                    result.Content1 = "T";
                    result.Content2 = Integer.parseInt(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'C' || address.charAt(0) == 'c') {
                    result.Content1 = "C";
                    result.Content2 = Integer.parseInt(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'L' || address.charAt(0) == 'l') {
                    result.Content1 = "L";
                    result.Content2 = PanasonicHelper.CalculateComplexAddress(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'D' || address.charAt(0) == 'd') {
                    result.Content1 = "D";
                    result.Content2 = Integer.parseInt(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'F' || address.charAt(0) == 'f') {
                    result.Content1 = "F";
                    result.Content2 = Integer.parseInt(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'S' || address.charAt(0) == 's') {
                    result.Content1 = "S";
                    result.Content2 = Integer.parseInt(address.substring(1));
                    break block18;
                }
                if (address.charAt(0) == 'K' || address.charAt(0) == 'k') {
                    result.Content1 = "K";
                    result.Content2 = Integer.parseInt(address.substring(1));
                    break block18;
                }
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            catch (Exception ex) {
                result.Message = ex.getMessage();
                return result;
            }
        }
        result.IsSuccess = true;
        return result;
    }

    public static String GetStationString(int station) {
        if (station >= 0) {
            return String.format("%02X", station);
        }
        return String.format("%02X", (byte)station);
    }

    public static OperateResultExOne<byte[]> BuildReadOneCoil(int station, String address) {
        if (address == null) {
            return new OperateResultExOne<byte[]>("address is not allowed null");
        }
        if (address.length() < 1 || address.length() > 8) {
            return new OperateResultExOne<byte[]>("length must be 1-8");
        }
        StringBuilder sb = new StringBuilder("%");
        sb.append(PanasonicHelper.GetStationString(station));
        sb.append("#RCS");
        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        sb.append((String)analysis.Content1);
        if (((String)analysis.Content1).equals("X") || ((String)analysis.Content1).equals("Y") || ((String)analysis.Content1).equals("R") || ((String)analysis.Content1).equals("L")) {
            sb.append(String.format("%03d", (Integer)analysis.Content2 / 16));
            sb.append(String.format("%01x", (Integer)analysis.Content2 % 16));
        } else if (((String)analysis.Content1).equals("T") || ((String)analysis.Content1).equals("C")) {
            sb.append("0");
            sb.append(String.format("%03d", analysis.Content2));
        } else {
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }
        sb.append(PanasonicHelper.CalculateCrc(sb));
        sb.append('\r');
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(sb.toString(), "ascii"));
    }

    public static OperateResultExOne<byte[]> BuildWriteOneCoil(int station, String address, boolean value) {
        StringBuilder sb = new StringBuilder("%");
        sb.append(PanasonicHelper.GetStationString(station));
        sb.append("#WCS");
        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        sb.append((String)analysis.Content1);
        if (((String)analysis.Content1).equals("X") || ((String)analysis.Content1).equals("Y") || ((String)analysis.Content1).equals("R") || ((String)analysis.Content1).equals("L")) {
            sb.append(String.format("%03d", (Integer)analysis.Content2 / 16));
            sb.append(String.format("%01x", (Integer)analysis.Content2 % 16));
        } else if (((String)analysis.Content1).equals("T") || ((String)analysis.Content1).equals("C")) {
            sb.append("0");
            sb.append(String.format("%03d", analysis.Content2));
        } else {
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }
        sb.append(value ? (char)'1' : '0');
        sb.append(PanasonicHelper.CalculateCrc(sb));
        sb.append('\r');
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(sb.toString(), "ascii"));
    }

    public static OperateResultExOne<byte[]> PackPanasonicCommand(int station, String cmd, boolean useExpandedHeader) {
        StringBuilder sb = new StringBuilder(useExpandedHeader ? "<" : "%");
        sb.append(String.format("%02X", station));
        sb.append(cmd);
        sb.append(PanasonicHelper.CalculateCrc(sb));
        sb.append('\r');
        return OperateResultExOne.CreateSuccessResult(Encoding.ASCII.GetBytes(sb.toString()));
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(int station, String address, int length, boolean isBit) {
        if (address == null) {
            return new OperateResultExOne<ArrayList<byte[]>>(StringResources.Language.PanasonicAddressParameterCannotBeNull());
        }
        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        ArrayList list = new ArrayList();
        if (isBit) {
            OperateResultExTwo<String, Integer> operateResultExTwo = analysis;
            operateResultExTwo.Content2 = (Integer)operateResultExTwo.Content2 - (Integer)analysis.Content2 % 16;
            int[] splits = SoftBasic.SplitIntegerToArray(length += (Integer)analysis.Content2 % 16, 400);
            for (int i = 0; i < splits.length; ++i) {
                int len = splits[i];
                StringBuilder sb = new StringBuilder("#");
                if (!(((String)analysis.Content1).equals("X") || ((String)analysis.Content1).equals("Y") || ((String)analysis.Content1).equals("R") || ((String)analysis.Content1).equals("L"))) {
                    return new OperateResultExOne<ArrayList<byte[]>>("Bit read only support X,Y,R,L");
                }
                sb.append("RCC");
                sb.append((String)analysis.Content1);
                int wordStart = (Integer)analysis.Content2 / 16;
                int wordFinish = ((Integer)analysis.Content2 + len - 1) / 16;
                sb.append(String.format("%04d", wordStart));
                sb.append(String.format("%04d", wordFinish));
                OperateResultExTwo<String, Integer> operateResultExTwo2 = analysis;
                Integer.valueOf((Integer)operateResultExTwo2.Content2 + len);
                operateResultExTwo2.Content2 = operateResultExTwo2.Content2;
                list.add(PanasonicHelper.PackPanasonicCommand((int)station, (String)sb.toString(), (boolean)false).Content);
            }
            return OperateResultExOne.CreateSuccessResult(list);
        }
        int[] splits = SoftBasic.SplitIntegerToArray(length, 500);
        for (int i = 0; i < splits.length; ++i) {
            int len = splits[i];
            StringBuilder sb = new StringBuilder("#");
            if (((String)analysis.Content1).equals("X") || ((String)analysis.Content1).equals("Y") || ((String)analysis.Content1).equals("R") || ((String)analysis.Content1).equals("L")) {
                sb.append("RCC");
                sb.append((String)analysis.Content1);
                int wordStart = (Integer)analysis.Content2 / 16;
                int wordFinish = ((Integer)analysis.Content2 + (len - 1) * 16) / 16;
                sb.append(String.format("%04d", wordStart));
                sb.append(String.format("%04d", wordFinish));
                OperateResultExTwo<String, Integer> operateResultExTwo = analysis;
                Integer.valueOf((Integer)operateResultExTwo.Content2 + len * 16);
                operateResultExTwo.Content2 = operateResultExTwo.Content2;
            } else if (((String)analysis.Content1).equals("D") || ((String)analysis.Content1).equals("LD") || ((String)analysis.Content1).equals("F")) {
                sb.append("RD");
                sb.append(((String)analysis.Content1).substring(0, 1));
                sb.append(String.format("%05d", analysis.Content2));
                sb.append(String.format("%05d", (Integer)analysis.Content2 + len - 1));
                OperateResultExTwo<String, Integer> operateResultExTwo = analysis;
                Integer.valueOf((Integer)operateResultExTwo.Content2 + len);
                operateResultExTwo.Content2 = operateResultExTwo.Content2;
            } else if (((String)analysis.Content1).equals("IX") || ((String)analysis.Content1).equals("IY") || ((String)analysis.Content1).equals("ID")) {
                sb.append("RD");
                sb.append((String)analysis.Content1);
                sb.append("000000000");
                OperateResultExTwo<String, Integer> operateResultExTwo = analysis;
                Integer.valueOf((Integer)operateResultExTwo.Content2 + len);
                operateResultExTwo.Content2 = operateResultExTwo.Content2;
            } else if (((String)analysis.Content1).equals("C") || ((String)analysis.Content1).equals("T")) {
                sb.append("RS");
                sb.append(String.format("%04d", analysis.Content2));
                sb.append(String.format("%04d", (Integer)analysis.Content2 + len - 1));
                OperateResultExTwo<String, Integer> operateResultExTwo = analysis;
                Integer.valueOf((Integer)operateResultExTwo.Content2 + len);
                operateResultExTwo.Content2 = operateResultExTwo.Content2;
            } else if (((String)analysis.Content1).equals("K") || ((String)analysis.Content1).equals("S")) {
                sb.append("R");
                sb.append((String)analysis.Content1);
                sb.append(String.format("%04d", analysis.Content2));
                sb.append(String.format("%04d", (Integer)analysis.Content2 + len - 1));
                OperateResultExTwo<String, Integer> operateResultExTwo = analysis;
                Integer.valueOf((Integer)operateResultExTwo.Content2 + len);
                operateResultExTwo.Content2 = operateResultExTwo.Content2;
            } else {
                return new OperateResultExOne<ArrayList<byte[]>>(StringResources.Language.NotSupportedDataType());
            }
            list.add(PanasonicHelper.PackPanasonicCommand((int)station, (String)sb.toString(), (boolean)(len > 27 ? true : false)).Content);
        }
        return OperateResultExOne.CreateSuccessResult(list);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(int station, String address, byte[] values) {
        return PanasonicHelper.BuildWriteCommand(station, address, values, (short)-1);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(int station, String address, byte[] values, short length) {
        if (address == null) {
            return new OperateResultExOne<byte[]>(StringResources.Language.PanasonicAddressParameterCannotBeNull());
        }
        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        values = SoftBasic.ArrayExpandToLengthEven(values);
        if (length == -1) {
            length = (short)(values.length / 2);
        }
        StringBuilder sb = new StringBuilder("%");
        sb.append(PanasonicHelper.GetStationString(station));
        sb.append("#");
        if (((String)analysis.Content1).equals("X") || ((String)analysis.Content1).equals("Y") || ((String)analysis.Content1).equals("R") || ((String)analysis.Content1).equals("L")) {
            sb.append("WCC");
            sb.append((String)analysis.Content1);
            sb.append(String.format("%04d", analysis.Content2));
            sb.append(String.format("%04d", (Integer)analysis.Content2 + length - 1));
        } else if (((String)analysis.Content1).equals("D") || ((String)analysis.Content1).equals("LD") || ((String)analysis.Content1).equals("F")) {
            sb.append("WD");
            sb.append(((String)analysis.Content1).substring(0, 1));
            sb.append(String.format("%05d", analysis.Content2));
            sb.append(String.format("%05d", (Integer)analysis.Content2 + length - 1));
        } else if (((String)analysis.Content1).equals("IX") || ((String)analysis.Content1).equals("IY") || ((String)analysis.Content1).equals("ID")) {
            sb.append("WD");
            sb.append((String)analysis.Content1);
            sb.append(String.format("%09d", analysis.Content2));
            sb.append(String.format("%09d", (Integer)analysis.Content2 + length - 1));
        } else if (((String)analysis.Content1).equals("C") || ((String)analysis.Content1).equals("T")) {
            sb.append("WS");
            sb.append(String.format("%04d", analysis.Content2));
            sb.append(String.format("%04d", (Integer)analysis.Content2 + length - 1));
        }
        sb.append(SoftBasic.ByteToHexString(values));
        sb.append(PanasonicHelper.CalculateCrc(sb));
        sb.append('\r');
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(sb.toString(), "ascii"));
    }

    public static OperateResultExOne<byte[]> ExtraActualData(byte[] response) {
        if (response.length < 9) {
            return new OperateResultExOne<byte[]>(StringResources.Language.PanasonicReceiveLengthMustLargerThan9());
        }
        if (response[3] == 36) {
            byte[] data = new byte[response.length - 9];
            if (data.length > 0) {
                System.arraycopy(response, 6, data, 0, data.length);
                data = SoftBasic.HexStringToBytes(Utilities.getString(data, "US-ASCII"));
            }
            return OperateResultExOne.CreateSuccessResult(data);
        }
        if (response[3] == 33) {
            int err = Integer.parseInt(Utilities.getString(response, 4, 2, "US-ASCII"));
            return new OperateResultExOne<byte[]>(err, PanasonicHelper.GetErrorDescription(err));
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.UnknownError());
    }

    public static OperateResultExOne<Boolean> ExtraActualBool(byte[] response) {
        if (response.length < 9) {
            return new OperateResultExOne<Boolean>(StringResources.Language.PanasonicReceiveLengthMustLargerThan9());
        }
        if (response[3] == 36) {
            return OperateResultExOne.CreateSuccessResult(response[6] == 49);
        }
        if (response[3] == 33) {
            int err = Integer.parseInt(Utilities.getString(response, 4, 2, "US-ASCII"));
            return new OperateResultExOne<Boolean>(err, PanasonicHelper.GetErrorDescription(err));
        }
        return new OperateResultExOne<Boolean>(StringResources.Language.UnknownError());
    }

    public static String GetErrorDescription(int err) {
        switch (err) {
            case 20: {
                return StringResources.Language.PanasonicMewStatus20();
            }
            case 21: {
                return StringResources.Language.PanasonicMewStatus21();
            }
            case 22: {
                return StringResources.Language.PanasonicMewStatus22();
            }
            case 23: {
                return StringResources.Language.PanasonicMewStatus23();
            }
            case 24: {
                return StringResources.Language.PanasonicMewStatus24();
            }
            case 25: {
                return StringResources.Language.PanasonicMewStatus25();
            }
            case 26: {
                return StringResources.Language.PanasonicMewStatus26();
            }
            case 27: {
                return StringResources.Language.PanasonicMewStatus27();
            }
            case 28: {
                return StringResources.Language.PanasonicMewStatus28();
            }
            case 29: {
                return StringResources.Language.PanasonicMewStatus29();
            }
            case 30: {
                return StringResources.Language.PanasonicMewStatus30();
            }
            case 40: {
                return StringResources.Language.PanasonicMewStatus40();
            }
            case 41: {
                return StringResources.Language.PanasonicMewStatus41();
            }
            case 42: {
                return StringResources.Language.PanasonicMewStatus42();
            }
            case 43: {
                return StringResources.Language.PanasonicMewStatus43();
            }
            case 50: {
                return StringResources.Language.PanasonicMewStatus50();
            }
            case 51: {
                return StringResources.Language.PanasonicMewStatus51();
            }
            case 52: {
                return StringResources.Language.PanasonicMewStatus52();
            }
            case 53: {
                return StringResources.Language.PanasonicMewStatus53();
            }
            case 60: {
                return StringResources.Language.PanasonicMewStatus60();
            }
            case 61: {
                return StringResources.Language.PanasonicMewStatus61();
            }
            case 62: {
                return StringResources.Language.PanasonicMewStatus62();
            }
            case 63: {
                return StringResources.Language.PanasonicMewStatus63();
            }
            case 65: {
                return StringResources.Language.PanasonicMewStatus65();
            }
            case 66: {
                return StringResources.Language.PanasonicMewStatus66();
            }
            case 67: {
                return StringResources.Language.PanasonicMewStatus67();
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static String GetMcErrorDescription(int code) {
        switch (code) {
            case 16433: {
                return StringResources.Language.PanasonicMc4031();
            }
            case 49233: {
                return StringResources.Language.PanasonicMcC051();
            }
            case 49238: {
                return StringResources.Language.PanasonicMcC056();
            }
            case 49241: {
                return StringResources.Language.PanasonicMcC059();
            }
            case 49243: {
                return StringResources.Language.PanasonicMcC05B();
            }
            case 49244: {
                return StringResources.Language.PanasonicMcC05C();
            }
            case 49247: {
                return StringResources.Language.PanasonicMcC05F();
            }
            case 49248: {
                return StringResources.Language.PanasonicMcC060();
            }
            case 49249: {
                return StringResources.Language.PanasonicMcC061();
            }
        }
        return StringResources.Language.MelsecPleaseReferToManualDocument();
    }
}

