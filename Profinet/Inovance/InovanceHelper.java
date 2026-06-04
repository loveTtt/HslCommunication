/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Inovance;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.ModBus.IModbus;
import HslCommunication.Profinet.Inovance.InovanceSeries;
import HslCommunication.StringResources;
import java.nio.charset.Charset;

public class InovanceHelper {
    private static int CalculateStartAddress(String address) {
        if (address.indexOf(46) < 0) {
            return Integer.parseInt(address);
        }
        String[] splits = address.split("\\.");
        return Integer.parseInt(splits[0]) * 8 + Integer.parseInt(splits[1]);
    }

    public static OperateResultExOne<String> PraseInovanceAddress(InovanceSeries series, String address, byte modbusCode) {
        if (series == InovanceSeries.AM) {
            return InovanceHelper.PraseInovanceAMAddress(address, modbusCode);
        }
        if (series == InovanceSeries.H3U) {
            return InovanceHelper.PraseInovanceH3UAddress(address, modbusCode);
        }
        if (series == InovanceSeries.H5U) {
            return InovanceHelper.PraseInovanceH5UAddress(address, modbusCode);
        }
        if (series == InovanceSeries.Easy) {
            return InovanceHelper.PraseInovanceH5UAddress(address, modbusCode);
        }
        return new OperateResultExOne<String>("[" + (Object)((Object)series) + "] Not supported series of plc");
    }

    public static OperateResultExOne<String> PraseInovanceAMAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = (String)stationPara.Content2;
            }
            if (address.startsWith("QX") || address.startsWith("qx")) {
                return OperateResultExOne.CreateSuccessResult(station + InovanceHelper.CalculateStartAddress(address.substring(2)));
            }
            if (address.startsWith("Q") || address.startsWith("q")) {
                return OperateResultExOne.CreateSuccessResult(station + InovanceHelper.CalculateStartAddress(address.substring(1)));
            }
            if (address.startsWith("IX") || address.startsWith("ix")) {
                return OperateResultExOne.CreateSuccessResult(station + "x=2;" + InovanceHelper.CalculateStartAddress(address.substring(2)));
            }
            if (address.startsWith("I") || address.startsWith("i")) {
                return OperateResultExOne.CreateSuccessResult(station + "x=2;" + InovanceHelper.CalculateStartAddress(address.substring(1)));
            }
            if (address.startsWith("MW") || address.startsWith("mw")) {
                return OperateResultExOne.CreateSuccessResult(station + address.substring(2));
            }
            if (address.startsWith("MD") || address.startsWith("md")) {
                if ((modbusCode == 1 || modbusCode == 15 || modbusCode == 5) && address.indexOf(46) > 0) {
                    return OperateResultExOne.CreateSuccessResult(station + address);
                }
                int add = Integer.parseInt(address.substring(2)) * 2;
                return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add));
            }
            if (address.startsWith("MB") || address.startsWith("mb")) {
                int add = Convert.ToInt32(address.substring(2));
                if (add % 2 == 1) {
                    return new OperateResultExOne<String>("Address[" + address + "] " + StringResources.Language.AddressOffsetEven());
                }
                return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add / 2));
            }
            if (address.startsWith("MX") || address.startsWith("mx")) {
                if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                    if (address.indexOf(46) > 0) {
                        String[] splits = address.substring(2).split("\\.");
                        int add = Integer.parseInt(splits[0]);
                        int bit = Integer.parseInt(splits[1]);
                        return OperateResultExOne.CreateSuccessResult(station + add / 2 + "." + (add % 2 * 8 + bit));
                    }
                    int add = Integer.parseInt(address.substring(2));
                    return OperateResultExOne.CreateSuccessResult(station + add / 2 + ".0");
                }
                int add = Integer.parseInt(address.substring(2));
                return OperateResultExOne.CreateSuccessResult(station + add / 2);
            }
            if (address.startsWith("M") || address.startsWith("m")) {
                return OperateResultExOne.CreateSuccessResult(station + address.substring(1));
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("SMX") || address.startsWith("smx")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 48) + ";" + InovanceHelper.CalculateStartAddress(address.substring(3)));
                }
                if (address.startsWith("SM") || address.startsWith("sm")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 48) + ";" + InovanceHelper.CalculateStartAddress(address.substring(2)));
                }
            } else {
                if (address.startsWith("SDW") || address.startsWith("sdw")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 48) + ";" + address.substring(3));
                }
                if (address.startsWith("SD") || address.startsWith("sd")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 48) + ";" + address.substring(2));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    private static int CalculateH3UStartAddress(String address) {
        if (address.indexOf(46) < 0) {
            return Integer.parseInt(address, 8);
        }
        String[] splits = address.split("\\.");
        return Integer.parseInt(splits[0], 8) * 8 + Integer.parseInt(splits[1]);
    }

    public static OperateResultExOne<String> PraseInovanceH3UAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = (String)stationPara.Content2;
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + (InovanceHelper.CalculateH3UStartAddress(address.substring(1)) + 63488));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (InovanceHelper.CalculateH3UStartAddress(address.substring(1)) + 64512));
                }
                if (address.startsWith("SM") || address.startsWith("sm")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 9216));
                }
                if (address.startsWith("S") || address.startsWith("s")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 57344));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 61440));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 62464));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000) {
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 8000));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + add);
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d")) {
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                }
                if (address.startsWith("SD") || address.startsWith("sd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 9216));
                }
                if (address.startsWith("R") || address.startsWith("r")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 12288));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 61440));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 200) {
                        return OperateResultExOne.CreateSuccessResult(station + ((add - 200) * 2 + 63232));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (add + 62464));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<String> PraseInovanceH5UAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = (String)stationPara.Content2;
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + (InovanceHelper.CalculateH3UStartAddress(address.substring(1)) + 63488));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (InovanceHelper.CalculateH3UStartAddress(address.substring(1)) + 64512));
                }
                if (address.startsWith("S") || address.startsWith("s")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 57344));
                }
                if (address.startsWith("B") || address.startsWith("b")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 12288));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d")) {
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                }
                if (address.startsWith("R") || address.startsWith("r")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 12288));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<Byte> ReadByte(IModbus modbus, String address) {
        try {
            int offset = 0;
            if (address.startsWith("MB") || address.startsWith("mb")) {
                offset = Convert.ToInt32(address.substring(2));
            } else if (address.startsWith("M") || address.startsWith("m")) {
                offset = Convert.ToInt32(address.substring(1));
            } else {
                new OperateResultExOne(StringResources.Language.NotSupportedDataType());
            }
            OperateResultExOne<byte[]> read = modbus.Read("MW" + String.valueOf(offset / 2), (short)1);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            return OperateResultExOne.CreateSuccessResult(offset % 2 == 0 ? ((byte[])read.Content)[1] : ((byte[])read.Content)[0]);
        }
        catch (Exception ex) {
            return new OperateResultExOne<Byte>("Address prase failed: " + ex.getMessage());
        }
    }

    public static OperateResultExOne<String> ReadAMString(IModbus modbus, String address, short length, Charset encoding) {
        int index = Convert.ToInt32(address.substring(address.length() - 1));
        address = address.substring(0, address.length() - 1) + (index - 1);
        OperateResultExOne<byte[]> read = modbus.Read(address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        if (modbus.isStringReverse()) {
            read.Content = SoftBasic.BytesReverseByWord((byte[])read.Content);
        }
        read.Content = SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 1);
        return OperateResultExOne.CreateSuccessResult(new String((byte[])read.Content, encoding));
    }
}

