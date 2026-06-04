/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Delta.Helper;

import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;

public class DeltaASHelper {
    private static int ParseDeltaBitAddress(String address) {
        int bitIndex = address.indexOf(46);
        if (bitIndex > 0) {
            return Integer.parseInt(address.substring(0, bitIndex)) * 16 + HslHelper.CalculateBitStartIndex(address.substring(bitIndex + 1));
        }
        return Integer.parseInt(address) * 16;
    }

    public static OperateResultExOne<String> ParseDeltaASAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + ((Integer)stationPara.Content1).toString() + ";";
                address = (String)stationPara.Content2;
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("SM") || address.startsWith("sm")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 16384));
                }
                if (address.startsWith("HC") || address.startsWith("hc")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 64512));
                }
                if (address.startsWith("S") || address.startsWith("s")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 20480));
                }
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + (DeltaASHelper.ParseDeltaBitAddress(address.substring(1)) + 24576));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (DeltaASHelper.ParseDeltaBitAddress(address.substring(1)) + 40960));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 57344));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 61440));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0));
                }
            } else {
                if (address.startsWith("SR") || address.startsWith("sr")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 49152));
                }
                if (address.startsWith("HC") || address.startsWith("hc")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 64512));
                }
                if (address.startsWith("D") || address.startsWith("d")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0));
                }
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=4;" + (Integer.parseInt(address.substring(1)) + 32768));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 40960));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 61440));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 57344));
                }
                if (address.startsWith("E") || address.startsWith("e")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 65024));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }
}

