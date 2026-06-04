/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.MelsecA1EDataType;
import HslCommunication.Profinet.Melsec.MelsecMcDataType;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;

public class MelsecHelper {
    public static OperateResultExTwo<MelsecA1EDataType, Integer> McA1EAnalysisAddress(String address) {
        OperateResultExTwo<MelsecA1EDataType, Integer> result = new OperateResultExTwo<MelsecA1EDataType, Integer>();
        try {
            switch (address.charAt(0)) {
                case 'T': 
                case 't': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecA1EDataType.TS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TS.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecA1EDataType.TC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TC.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecA1EDataType.TN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TN.getFromBase());
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'C': 
                case 'c': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecA1EDataType.CS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CS.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecA1EDataType.CC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CC.getFromBase());
                        break;
                    }
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecA1EDataType.CN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CN.getFromBase());
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'X': 
                case 'x': {
                    result.Content1 = MelsecA1EDataType.X;
                    address = address.substring(1);
                    if (address.startsWith("0")) {
                        result.Content2 = Integer.parseInt(address, 8);
                        break;
                    }
                    result.Content2 = Integer.parseInt(address, MelsecA1EDataType.X.getFromBase());
                    break;
                }
                case 'Y': 
                case 'y': {
                    result.Content1 = MelsecA1EDataType.Y;
                    address = address.substring(1);
                    if (address.startsWith("0")) {
                        result.Content2 = Integer.parseInt(address, 8);
                        break;
                    }
                    result.Content2 = Integer.parseInt(address, MelsecA1EDataType.Y.getFromBase());
                    break;
                }
                case 'M': 
                case 'm': {
                    result.Content1 = MelsecA1EDataType.M;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.M.getFromBase());
                    break;
                }
                case 'S': 
                case 's': {
                    result.Content1 = MelsecA1EDataType.S;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.S.getFromBase());
                    break;
                }
                case 'F': 
                case 'f': {
                    result.Content1 = MelsecA1EDataType.F;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.F.getFromBase());
                    break;
                }
                case 'B': 
                case 'b': {
                    result.Content1 = MelsecA1EDataType.B;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.B.getFromBase());
                    break;
                }
                case 'D': 
                case 'd': {
                    result.Content1 = MelsecA1EDataType.D;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.D.getFromBase());
                    break;
                }
                case 'R': 
                case 'r': {
                    result.Content1 = MelsecA1EDataType.R;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.R.getFromBase());
                    break;
                }
                case 'W': 
                case 'w': {
                    result.Content1 = MelsecA1EDataType.W;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.W.getFromBase());
                    break;
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

    public static byte[] BuildBytesFromData(byte value) {
        return Utilities.getBytes(String.format("%02x", value), "ASCII");
    }

    public static byte[] BuildBytesFromData(short value) {
        return Utilities.getBytes(String.format("%04x", value), "ASCII");
    }

    public static byte[] BuildBytesFromData(int value) {
        return Utilities.getBytes(String.format("%04x", value), "ASCII");
    }

    public static byte[] BuildBytesFromAddress(int address, MelsecMcDataType type) {
        return Utilities.getBytes(String.format(type.getFromBase() == 10 ? "%06d" : "%06x", address), "ASCII");
    }

    public static byte[] BuildBytesFromData(byte[] value) {
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length; ++i) {
            byte[] data = MelsecHelper.BuildBytesFromData(value[i]);
            buffer[2 * i + 0] = data[0];
            buffer[2 * i + 1] = data[1];
        }
        return buffer;
    }

    public static byte[] TransBoolArrayToByteData(byte[] value) {
        int length = value.length % 2 == 0 ? value.length / 2 : value.length / 2 + 1;
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; ++i) {
            if (value[i * 2 + 0] != 0) {
                int n = i;
                buffer[n] = (byte)(buffer[n] + 16);
            }
            if (i * 2 + 1 >= value.length || value[i * 2 + 1] == 0) continue;
            int n = i;
            buffer[n] = (byte)(buffer[n] + 1);
        }
        return buffer;
    }

    public static byte[] TransBoolArrayToByteData(boolean[] value) {
        int length = (value.length + 1) / 2;
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; ++i) {
            if (value[i * 2 + 0]) {
                int n = i;
                buffer[n] = (byte)(buffer[n] + 16);
            }
            if (i * 2 + 1 >= value.length || !value[i * 2 + 1]) continue;
            int n = i;
            buffer[n] = (byte)(buffer[n] + 1);
        }
        return buffer;
    }

    public static byte[] FxCalculateCRC(byte[] data) {
        return MelsecHelper.FxCalculateCRC(data, 1, 2);
    }

    public static byte[] FxCalculateCRC(byte[] data, int start, int tail) {
        int sum = 0;
        for (int i = start; i < data.length - tail; ++i) {
            sum += data[i] & 0xFF;
        }
        return SoftBasic.BuildAsciiBytesFrom((byte)sum);
    }

    public static boolean CheckCRC(byte[] data) {
        byte[] crc = MelsecHelper.FxCalculateCRC(data);
        if (crc[0] != data[data.length - 2]) {
            return false;
        }
        return crc[1] == data[data.length - 1];
    }

    public static byte[] TransByteArrayToAsciiByteArray(byte[] value) {
        if (value == null) {
            return new byte[0];
        }
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length / 2; ++i) {
            byte[] data = SoftBasic.BuildAsciiBytesFrom(Utilities.getShort(value, i * 2));
            System.arraycopy(data, 0, buffer, 4 * i, data.length);
        }
        return buffer;
    }

    public static byte[] TransAsciiByteArrayToByteArray(byte[] value) {
        byte[] Content = new byte[value.length / 2];
        for (int i = 0; i < Content.length / 2; ++i) {
            int tmp = Integer.parseInt(new String(value, i * 4, 4, StandardCharsets.US_ASCII), 16);
            System.arraycopy(Utilities.getBytes(tmp), 0, Content, i * 2, 2);
        }
        return Content;
    }

    public static String GetErrorDescription(int code) {
        switch (code) {
            case 2: {
                return StringResources.Language.MelsecError02();
            }
            case 81: {
                return StringResources.Language.MelsecError51();
            }
            case 82: {
                return StringResources.Language.MelsecError52();
            }
            case 84: {
                return StringResources.Language.MelsecError54();
            }
            case 85: {
                return StringResources.Language.MelsecError55();
            }
            case 86: {
                return StringResources.Language.MelsecError56();
            }
            case 88: {
                return StringResources.Language.MelsecError58();
            }
            case 89: {
                return StringResources.Language.MelsecError59();
            }
            case 17165: {
                return StringResources.Language.MelsecError430D();
            }
            case 49229: {
                return StringResources.Language.MelsecErrorC04D();
            }
            case 49232: {
                return StringResources.Language.MelsecErrorC050();
            }
            case 49233: 
            case 49234: 
            case 49235: 
            case 49236: {
                return StringResources.Language.MelsecErrorC051_54();
            }
            case 49237: {
                return StringResources.Language.MelsecErrorC055();
            }
            case 49238: {
                return StringResources.Language.MelsecErrorC056();
            }
            case 49239: {
                return StringResources.Language.MelsecErrorC057();
            }
            case 49240: {
                return StringResources.Language.MelsecErrorC058();
            }
            case 49241: {
                return StringResources.Language.MelsecErrorC059();
            }
            case 49242: 
            case 49243: {
                return StringResources.Language.MelsecErrorC05A_B();
            }
            case 49244: {
                return StringResources.Language.MelsecErrorC05C();
            }
            case 49245: {
                return StringResources.Language.MelsecErrorC05D();
            }
            case 49246: {
                return StringResources.Language.MelsecErrorC05E();
            }
            case 49247: {
                return StringResources.Language.MelsecErrorC05F();
            }
            case 49248: {
                return StringResources.Language.MelsecErrorC060();
            }
            case 49249: {
                return StringResources.Language.MelsecErrorC061();
            }
            case 49250: {
                return StringResources.Language.MelsecErrorC062();
            }
            case 49264: {
                return StringResources.Language.MelsecErrorC070();
            }
            case 49266: {
                return StringResources.Language.MelsecErrorC072();
            }
            case 49268: {
                return StringResources.Language.MelsecErrorC074();
            }
        }
        return StringResources.Language.MelsecPleaseReferToManualDocument();
    }
}

