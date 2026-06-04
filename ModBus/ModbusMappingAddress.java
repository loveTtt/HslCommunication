/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.ModBus.ModbusHelper;
import HslCommunication.StringResources;

public class ModbusMappingAddress {
    private static int ParseBitAddress(String address) {
        return ModbusMappingAddress.ParseBitAddress(address, 16);
    }

    private static int ParseBitAddress(String address, int wordLength) {
        int bitIndex = address.indexOf(46);
        if (bitIndex > 0) {
            return Convert.ToInt32(address.substring(0, bitIndex)) * wordLength + HslHelper.CalculateBitStartIndex(address.substring(bitIndex + 1));
        }
        return Convert.ToInt32(address) * wordLength;
    }

    public static OperateResultExOne<String> Delta_AS(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                address = (String)stationPara.Content2;
                station = "s=" + stationPara.Content1 + ";";
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("SM") || address.startsWith("sm")) {
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(Convert.ToInt32(address.substring(2)) + 16384));
                }
                if (address.startsWith("HC") || address.startsWith("hc")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 64512));
                }
                if (address.startsWith("S") || address.startsWith("s")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 20480));
                }
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + (ModbusMappingAddress.ParseBitAddress(address.substring(1)) + 24576));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (ModbusMappingAddress.ParseBitAddress(address.substring(1)) + 40960));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 57344));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 61440));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0));
                }
                if (address.startsWith("D") && address.indexOf(".") > 0) {
                    return OperateResultExOne.CreateSuccessResult(station + address);
                }
            } else {
                if (address.startsWith("SR") || address.startsWith("sr")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 49152));
                }
                if (address.startsWith("HC") || address.startsWith("hc")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 64512));
                }
                if (address.startsWith("D") || address.startsWith("d")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0));
                }
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + "x=4;" + (Convert.ToInt32(address.substring(1)) + 32768));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 40960));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 61440));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 57344));
                }
                if (address.startsWith("E") || address.startsWith("e")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 65024));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<String> WeCon_Lx5v(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                address = (String)stationPara.Content2;
                station = "s=" + stationPara.Content1 + ";";
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.matches("^T[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0));
                }
                if (address.matches("^C[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 1536));
                }
                if (address.matches("^LC[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 2560));
                }
                if (address.matches("^HSC[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(3)) + 3584));
                }
                if (address.matches("^M[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 4096));
                }
                if (address.matches("^SM[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 20480));
                }
                if (address.matches("^S[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 49152));
                }
                if (address.matches("^X[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1), 8) + 57344));
                }
                if (address.matches("^Y[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1), 8) + 61440));
                }
                return OperateResultExOne.CreateSuccessResult(ModbusHelper.TransPointAddressToModbus(station, address, new String[]{"D", "SD", "R"}, new int[]{4096, 20480, 32768}, new FunctionOperateExOne<String, Integer>(){

                    @Override
                    public Integer Action(String content) {
                        return Integer.parseInt(content);
                    }
                }));
            }
            if (address.matches("^T[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0));
            }
            if (address.matches("^C[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 1536));
            }
            if (address.matches("^LC[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) * 2 + 2560));
            }
            if (address.matches("^HSC[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(3)) * 2 + 3584));
            }
            if (address.matches("^D[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 4096));
            }
            if (address.matches("^SD[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 20480));
            }
            if (address.matches("^R[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 32768));
            }
            throw new Exception(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(DeviceAddressDataBase.GetUnsupportedAddressInfo(address, ex));
        }
    }

    public static OperateResultExOne<String> Invt_Ts(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                address = (String)stationPara.Content2;
                station = "s=" + stationPara.Content1 + ";";
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.matches("^M[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + Convert.ToInt32(address.substring(1)));
                }
                if (address.matches("^S[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 32768));
                }
                if (address.matches("^X[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1), 8) + 40960));
                }
                if (address.matches("^Y[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1), 8) + 45056));
                }
                if (address.matches("^T[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 49152));
                }
                if (address.matches("^C[0-9]+")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 49664));
                }
                return OperateResultExOne.CreateSuccessResult(ModbusHelper.TransPointAddressToModbus(station, address, new String[]{"D", "R"}, new int[]{0, 32768}, new FunctionOperateExOne<String, Integer>(){

                    @Override
                    public Integer Action(String content) {
                        return Integer.parseInt(content);
                    }
                }));
            }
            if (address.matches("^T[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 57344));
            }
            if (address.matches("^C[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 57856));
            }
            if (address.matches("^Z[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) * 2 + 58368));
            }
            if (address.matches("^D[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + Convert.ToInt32(address.substring(1)));
            }
            if (address.matches("^R[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 32768));
            }
            if (address.matches("^M[0-9]+")) {
                return OperateResultExOne.CreateSuccessResult(station + "x=1;" + Convert.ToInt32(address.substring(1)));
            }
            throw new Exception(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(DeviceAddressDataBase.GetUnsupportedAddressInfo(address, ex));
        }
    }
}

