/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.MegMeet;

import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;

public class MegMeetHelper {
    public static OperateResultExOne<String> PraseMegMeetAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = (String)stationPara.Content2;
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + Convert.ToInt32(address.substring(1), 8));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + Convert.ToInt32(address.substring(1), 8));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Convert.ToInt32(address.substring(1));
                    if (add < 2048) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 2000));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add - 2048 + 12000));
                }
                if (address.startsWith("SM") || address.startsWith("sm")) {
                    int add = Convert.ToInt32(address.substring(2));
                    if (add < 256) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 4400));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add - 256 + 30000));
                }
                if (address.startsWith("S") || address.startsWith("s")) {
                    int add = Convert.ToInt32(address.substring(1));
                    if (add < 1024) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 6000));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add - 1024 + 31000));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    int add = Convert.ToInt32(address.substring(1));
                    if (add < 256) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 8000));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add - 256 + 11000));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    int add = Convert.ToInt32(address.substring(1));
                    if (add < 256) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 9200));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add - 256 + 10000));
                }
            } else {
                if (address.startsWith("T") || address.startsWith("t")) {
                    int add = Convert.ToInt32(address.substring(1));
                    if (add < 256) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 9000));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add - 256 + 11000));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    int add = Convert.ToInt32(address.substring(1));
                    if (add < 200) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 9500));
                    }
                    if (add < 256) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add * 2 - 200 + 9700));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add * 2 - 256 + 10000));
                }
                if (address.startsWith("D") || address.startsWith("d")) {
                    int add = Convert.ToInt32(address.substring(1));
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add));
                }
                if (address.startsWith("SD") || address.startsWith("sd")) {
                    int add = Convert.ToInt32(address.substring(2));
                    if (add < 256) {
                        return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 8000));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add - 256 + 12000));
                }
                if (address.startsWith("Z") || address.startsWith("z")) {
                    int add = Convert.ToInt32(address.substring(1));
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 8500));
                }
                if (address.startsWith("R") || address.startsWith("r")) {
                    int add = Convert.ToInt32(address.substring(1));
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add + 13000));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }
}

