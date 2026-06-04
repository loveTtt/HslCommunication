/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.XINJE;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.XinJEAddress;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.XINJE.XinJESeries;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class XinJEHelper {
    private static int CalculateXinJEStartAddress(String address) {
        if (address.indexOf(46) < 0) {
            return Integer.parseInt(address, 8);
        }
        String[] splits = address.split("\\.");
        return Integer.parseInt(splits[0], 8) * 8 + Integer.parseInt(splits[1]);
    }

    public static OperateResultExOne<String> PraseXinJEAddress(XinJESeries series, String address, byte modbusCode) {
        if (series == XinJESeries.XC) {
            return XinJEHelper.PraseXinJEXCAddress(address, modbusCode);
        }
        return XinJEHelper.PraseXinJEXD1XD2XD3XL1XL3Address(address, modbusCode);
    }

    public static OperateResultExOne<String> PraseXinJEXCAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = (String)stationPara.Content2;
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("X") || address.startsWith("x")) {
                    return OperateResultExOne.CreateSuccessResult(station + (XinJEHelper.CalculateXinJEStartAddress(address.substring(1)) + 16384));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    return OperateResultExOne.CreateSuccessResult(station + (XinJEHelper.CalculateXinJEStartAddress(address.substring(1)) + 18432));
                }
                if (address.startsWith("S") || address.startsWith("s")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 20480));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 25600));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 27648));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000) {
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 24576));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + add);
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000) {
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 16384));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + add);
                }
                if (address.startsWith("F") || address.startsWith("f")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000) {
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 26624));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (add + 18432));
                }
                if (address.startsWith("E") || address.startsWith("e")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 28672));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 12288));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 14336));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<String> PraseXinJEXD1XD2XD3XL1XL3Address(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = (String)stationPara.Content2;
            }
            if (modbusCode == 1 || modbusCode == 15 || modbusCode == 5) {
                if (address.startsWith("X") || address.startsWith("x")) {
                    int start = XinJEHelper.CalculateXinJEStartAddress(address.substring(1));
                    if (start < 4096) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0 + 20480));
                    }
                    if (start < 8192) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 4096 + 20736));
                    }
                    if (start < 12288) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 8192 + 22736));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (start - 12288 + 23536));
                }
                if (address.startsWith("Y") || address.startsWith("y")) {
                    int start = XinJEHelper.CalculateXinJEStartAddress(address.substring(1));
                    if (start < 4096) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0 + 24576));
                    }
                    if (start < 8192) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 4096 + 24832));
                    }
                    if (start < 12288) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 8192 + 26832));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (start - 12288 + 27632));
                }
                if (address.startsWith("SEM") || address.startsWith("sem")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 49280));
                }
                if (address.startsWith("HSC") || address.startsWith("hsc")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 59648));
                }
                if (address.startsWith("SM") || address.startsWith("sm")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 36864));
                }
                if (address.startsWith("ET") || address.startsWith("et")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 49152));
                }
                if (address.startsWith("HM") || address.startsWith("hm")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 49408));
                }
                if (address.startsWith("HS") || address.startsWith("hs")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 55552));
                }
                if (address.startsWith("HT") || address.startsWith("ht")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 57600));
                }
                if (address.startsWith("HC") || address.startsWith("hc")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 58624));
                }
                if (address.startsWith("S") || address.startsWith("s")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 28672));
                }
                if (address.startsWith("T") || address.startsWith("t")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 40960));
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 45056));
                }
                if (address.startsWith("M") || address.startsWith("m")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0));
                }
            } else {
                if (address.startsWith("ID") || address.startsWith("id")) {
                    int start = Integer.parseInt(address.substring(2));
                    if (start < 10000) {
                        return OperateResultExOne.CreateSuccessResult(station + (start + 20480));
                    }
                    if (start < 20000) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 10000 + 20736));
                    }
                    if (start < 30000) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 20000 + 22736));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (start - 30000 + 23536));
                }
                if (address.startsWith("QD") || address.startsWith("qd")) {
                    int start = Integer.parseInt(address.substring(2));
                    if (start < 10000) {
                        return OperateResultExOne.CreateSuccessResult(station + (start + 24576));
                    }
                    if (start < 20000) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 10000 + 24832));
                    }
                    if (start < 30000) {
                        return OperateResultExOne.CreateSuccessResult(station + (start - 20000 + 26832));
                    }
                    return OperateResultExOne.CreateSuccessResult(station + (start - 30000 + 27632));
                }
                if (address.startsWith("HSCD") || address.startsWith("hscd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(4)) + 50304));
                }
                if (address.startsWith("ETD") || address.startsWith("etd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 40960));
                }
                if (address.startsWith("HSD") || address.startsWith("hsd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 47232));
                }
                if (address.startsWith("HTD") || address.startsWith("htd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 48256));
                }
                if (address.startsWith("HCD") || address.startsWith("hcd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 49280));
                }
                if (address.startsWith("SFD") || address.startsWith("sfd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 58560));
                }
                if (address.startsWith("SD") || address.startsWith("sd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 28672));
                }
                if (address.startsWith("TD") || address.startsWith("td")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 32768));
                }
                if (address.startsWith("CD") || address.startsWith("cd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 36864));
                }
                if (address.startsWith("HD") || address.startsWith("hd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 41088));
                }
                if (address.startsWith("FD") || address.startsWith("fd")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 50368));
                }
                if (address.startsWith("FS") || address.startsWith("fs")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 62656));
                }
                if (address.startsWith("D") || address.startsWith("d")) {
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0));
                }
            }
            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte station, String address, short length, boolean isBit) {
        OperateResultExOne<XinJEAddress> read = XinJEAddress.ParseFrom(address, station);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return XinJEHelper.BuildReadCommand((XinJEAddress)read.Content, length, isBit);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(XinJEAddress address, short length, boolean isBit) {
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, isBit ? 1920 : 120);
        for (int i = 0; i < splits.length; ++i) {
            byte[] command = new byte[]{address.Station, isBit ? (byte)30 : 32, address.DataCode, BitConverter.GetBytes(address.getAddressStart())[2], BitConverter.GetBytes(address.getAddressStart())[1], BitConverter.GetBytes(address.getAddressStart())[0], BitConverter.GetBytes(splits[i])[1], BitConverter.GetBytes(splits[i])[0]};
            address.setAddressStart(address.getAddressStart() + splits[i]);
            array.add(command);
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(byte station, String address, byte[] value) {
        OperateResultExOne<XinJEAddress> read = XinJEAddress.ParseFrom(address, station);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return XinJEHelper.BuildWriteWordCommand((XinJEAddress)read.Content, value);
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(XinJEAddress address, byte[] value) {
        byte[] command = new byte[9 + value.length];
        command[0] = address.Station;
        command[1] = 33;
        command[2] = address.DataCode;
        command[3] = BitConverter.GetBytes(address.getAddressStart())[2];
        command[4] = BitConverter.GetBytes(address.getAddressStart())[1];
        command[5] = BitConverter.GetBytes(address.getAddressStart())[0];
        command[6] = BitConverter.GetBytes(value.length / 2)[1];
        command[7] = BitConverter.GetBytes(value.length / 2)[0];
        command[8] = (byte)value.length;
        Utilities.ByteArrayCopyTo(value, command, 9);
        return OperateResultExOne.CreateSuccessResult(command);
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte station, String address, boolean[] value) {
        OperateResultExOne<XinJEAddress> read = XinJEAddress.ParseFrom(address, station);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return XinJEHelper.BuildWriteBoolCommand((XinJEAddress)read.Content, value);
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(XinJEAddress address, boolean[] value) {
        byte[] buffer = SoftBasic.BoolArrayToByte(value);
        byte[] command = new byte[9 + buffer.length];
        command[0] = address.Station;
        command[1] = 31;
        command[2] = address.DataCode;
        command[3] = BitConverter.GetBytes(address.getAddressStart())[2];
        command[4] = BitConverter.GetBytes(address.getAddressStart())[1];
        command[5] = BitConverter.GetBytes(address.getAddressStart())[0];
        command[6] = BitConverter.GetBytes(value.length)[1];
        command[7] = BitConverter.GetBytes(value.length)[0];
        command[8] = (byte)buffer.length;
        Utilities.ByteArrayCopyTo(buffer, command, 9);
        return OperateResultExOne.CreateSuccessResult(command);
    }
}

