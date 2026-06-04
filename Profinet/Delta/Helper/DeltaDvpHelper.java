/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Delta.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.FunctionOperateExTwo;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;

public class DeltaDvpHelper {
    public static OperateResultExOne<String> ParseDeltaDvpAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = (String)stationPara.Content2;
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("S") || address.startsWith("s")) {
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                }
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + (Integer.parseInt(address.substring(1), 8) + 1024));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1), 8) + 1280));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 1536));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 3584));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 1536) {
                        return OperateResultExOne.CreateSuccessResult(station + (add - 1536 + 45056));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (add + 2048));
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 4096) {
                        return OperateResultExOne.CreateSuccessResult(station + (add - 4096 + 36864));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (add + 4096));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 200) {
                        return OperateResultExOne.CreateSuccessResult(station + (add - 200 + 3784));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (add + 3584));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 1536));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<boolean[]> ReadBool(FunctionOperateExTwo<String, Short, OperateResultExOne<boolean[]>> readBoolFunc, String address, short length) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + ((Integer)stationPara.Content1).toString() + ";";
            address = (String)stationPara.Content2;
        }
        if (address.startsWith("M")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 1536 && add + length > 1536) {
                    short len1 = (short)(1536 - add);
                    short len2 = (short)(length - len1);
                    OperateResultExOne<boolean[]> read1 = readBoolFunc.Action(station + address, len1);
                    if (!read1.IsSuccess) {
                        return read1;
                    }
                    OperateResultExOne<boolean[]> read2 = readBoolFunc.Action(station + "M1536", len2);
                    if (!read2.IsSuccess) {
                        return read2;
                    }
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceTwoBoolArray((boolean[])read1.Content, (boolean[])read2.Content));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return readBoolFunc.Action(address, length);
    }

    public static OperateResult Write(FunctionOperateExTwo<String, boolean[], OperateResult> writeBoolFunc, String address, boolean[] value) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + ((Integer)stationPara.Content1).toString() + ";";
            address = (String)stationPara.Content2;
        }
        if (address.startsWith("M")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 1536 && add + value.length > 1536) {
                    short len1 = (short)(1536 - add);
                    OperateResult write1 = writeBoolFunc.Action(station + address, SoftBasic.BoolArraySelectMiddle(value, 0, len1));
                    if (!write1.IsSuccess) {
                        return write1;
                    }
                    OperateResult write2 = writeBoolFunc.Action(station + "M1536", SoftBasic.BoolArraySelectMiddle(value, len1, value.length - len1));
                    if (!write2.IsSuccess) {
                        return write2;
                    }
                    return OperateResult.CreateSuccessResult();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return writeBoolFunc.Action(address, value);
    }

    public static OperateResultExOne<byte[]> Read(FunctionOperateExTwo<String, Short, OperateResultExOne<byte[]>> readFunc, String address, short length) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + ((Integer)stationPara.Content1).toString() + ";";
            address = (String)stationPara.Content2;
        }
        if (address.startsWith("D")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 4096 && add + length > 4096) {
                    short len1 = (short)(4096 - add);
                    short len2 = (short)(length - len1);
                    OperateResultExOne<byte[]> read1 = readFunc.Action(station + address, len1);
                    if (!read1.IsSuccess) {
                        return read1;
                    }
                    OperateResultExOne<byte[]> read2 = readFunc.Action(station + "D4096", len2);
                    if (!read2.IsSuccess) {
                        return read2;
                    }
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceTwoByteArray((byte[])read1.Content, (byte[])read2.Content));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return readFunc.Action(address, length);
    }

    public static OperateResult Write(FunctionOperateExTwo<String, byte[], OperateResult> writeFunc, String address, byte[] value) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + ((Integer)stationPara.Content1).toString() + ";";
            address = (String)stationPara.Content2;
        }
        if (address.startsWith("D")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 4096 && add + value.length / 2 > 4096) {
                    short len1 = (short)(4096 - add);
                    OperateResult write1 = writeFunc.Action(station + address, SoftBasic.BytesArraySelectBegin(value, len1 * 2));
                    if (!write1.IsSuccess) {
                        return write1;
                    }
                    OperateResult write2 = writeFunc.Action(station + "D4096", SoftBasic.BytesArrayRemoveBegin(value, len1 * 2));
                    if (!write2.IsSuccess) {
                        return write2;
                    }
                    return OperateResult.CreateSuccessResult();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return writeFunc.Action(address, value);
    }
}

