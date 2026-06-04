/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Fuji;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.FujiSPBAddress;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import java.nio.charset.StandardCharsets;

public class FujiSPBHelper {
    public static String AnalysisIntegerAddress(int address) {
        String tmp = String.format("%04d", address);
        return tmp.substring(2) + tmp.substring(0, 2);
    }

    public static String CalculateAcc(String data) {
        byte[] buffer = data.getBytes(StandardCharsets.US_ASCII);
        int count = 0;
        for (int i = 0; i < buffer.length; ++i) {
            count += buffer[i];
        }
        return String.format("%04X", count).substring(2);
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(byte station, String address, short length) {
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)analysis.Content1).byteValue();
        address = (String)analysis.Content2;
        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        return FujiSPBHelper.BuildReadCommand(station, (FujiSPBAddress)addressAnalysis.Content, length);
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(byte station, FujiSPBAddress address, short length) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("09");
        stringBuilder.append("FFFF");
        stringBuilder.append("00");
        stringBuilder.append("00");
        stringBuilder.append(address.GetWordAddress());
        stringBuilder.append(FujiSPBHelper.AnalysisIntegerAddress(length));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(byte station, String[] address, short[] length, boolean isBool) {
        if (address == null || length == null) {
            return new OperateResultExOne<byte[]>("Parameter address or length can't be null");
        }
        if (address.length != length.length) {
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append(String.format("%02X", 6 + address.length * 4));
        stringBuilder.append("FFFF");
        stringBuilder.append("00");
        stringBuilder.append("04");
        stringBuilder.append("00");
        stringBuilder.append(String.format("%02X", address.length));
        for (int i = 0; i < address.length; ++i) {
            OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address[i], "s", station);
            station = ((Integer)analysis.Content1).byteValue();
            address[i] = (String)analysis.Content2;
            OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address[i]);
            if (!addressAnalysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(addressAnalysis);
            }
            stringBuilder.append(((FujiSPBAddress)addressAnalysis.Content).getTypeCode());
            stringBuilder.append(String.format("%02X", length[i]));
            stringBuilder.append(FujiSPBHelper.AnalysisIntegerAddress(((FujiSPBAddress)addressAnalysis.Content).getAddressStart()));
        }
        stringBuilder.setCharAt(1, String.format("%02X", station).charAt(0));
        stringBuilder.setCharAt(2, String.format("%02X", station).charAt(1));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildWriteByteCommand(byte station, String address, byte[] value) {
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)analysis.Content1).byteValue();
        address = (String)analysis.Content2;
        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("00");
        stringBuilder.append("FFFF");
        stringBuilder.append("01");
        stringBuilder.append("00");
        stringBuilder.append(((FujiSPBAddress)addressAnalysis.Content).GetWordAddress());
        stringBuilder.append(FujiSPBHelper.AnalysisIntegerAddress(value.length / 2));
        stringBuilder.append(SoftBasic.ByteToHexString(value));
        stringBuilder.setCharAt(3, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(0));
        stringBuilder.setCharAt(4, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(1));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte station, String address, boolean value) {
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)analysis.Content1).byteValue();
        address = (String)analysis.Content2;
        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        if ((address.startsWith("X") || address.startsWith("Y") || address.startsWith("M") || address.startsWith("L") || address.startsWith("TC") || address.startsWith("CC")) && address.indexOf(46) < 0) {
            ((FujiSPBAddress)addressAnalysis.Content).setBitIndex(((FujiSPBAddress)addressAnalysis.Content).getAddressStart() % 16);
            ((FujiSPBAddress)addressAnalysis.Content).setAddressStart(((FujiSPBAddress)addressAnalysis.Content).getAddressStart() / 16);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("00");
        stringBuilder.append("FFFF");
        stringBuilder.append("01");
        stringBuilder.append("02");
        stringBuilder.append(((FujiSPBAddress)addressAnalysis.Content).GetWriteBoolAddress());
        stringBuilder.append(value ? "01" : "00");
        stringBuilder.setCharAt(3, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(0));
        stringBuilder.setCharAt(4, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(1));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> CheckResponseData(byte[] content) {
        if (content[0] != 58) {
            return new OperateResultExOne<byte[]>(content[0], "Read Faild:" + SoftBasic.ByteToHexString(content, ' '));
        }
        String code = new String(content, 9, 2, StandardCharsets.US_ASCII);
        if (!code.equals("00")) {
            return new OperateResultExOne<byte[]>(Integer.parseInt(code, 16), FujiSPBHelper.GetErrorDescriptionFromCode(code));
        }
        if (content[content.length - 2] == 13 && content[content.length - 1] == 10) {
            content = SoftBasic.BytesArrayRemoveLast(content, 2);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 11));
    }

    public static String GetErrorDescriptionFromCode(String code) {
        switch (code) {
            case "01": {
                return StringResources.Language.FujiSpbStatus01();
            }
            case "02": {
                return StringResources.Language.FujiSpbStatus02();
            }
            case "03": {
                return StringResources.Language.FujiSpbStatus03();
            }
            case "04": {
                return StringResources.Language.FujiSpbStatus04();
            }
            case "05": {
                return StringResources.Language.FujiSpbStatus05();
            }
            case "06": {
                return StringResources.Language.FujiSpbStatus06();
            }
            case "07": {
                return StringResources.Language.FujiSpbStatus07();
            }
            case "09": {
                return StringResources.Language.FujiSpbStatus09();
            }
            case "0C": {
                return StringResources.Language.FujiSpbStatus0C();
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> Read(IReadWriteDevice device, byte station, String address, short length) {
        OperateResultExOne<byte[]> command = FujiSPBHelper.BuildReadCommand(station, address, length);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> check = FujiSPBHelper.CheckResponseData((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.HexStringToBytes(new String(SoftBasic.BytesArrayRemoveBegin((byte[])check.Content, 4), StandardCharsets.US_ASCII)));
    }

    public static OperateResult Write(IReadWriteDevice device, byte station, String address, byte[] value) {
        OperateResultExOne<byte[]> command = FujiSPBHelper.BuildWriteByteCommand(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPBHelper.CheckResponseData((byte[])read.Content);
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice device, byte station, String address, short length) {
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)analysis.Content1).byteValue();
        address = (String)analysis.Content2;
        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        if ((address.startsWith("X") || address.startsWith("Y") || address.startsWith("M") || address.startsWith("L") || address.startsWith("TC") || address.startsWith("CC")) && address.indexOf(46) < 0) {
            ((FujiSPBAddress)addressAnalysis.Content).setBitIndex(((FujiSPBAddress)addressAnalysis.Content).getAddressStart() % 16);
            ((FujiSPBAddress)addressAnalysis.Content).setAddressStart(((FujiSPBAddress)addressAnalysis.Content).getAddressStart() / 16);
        }
        short len = (short)((((FujiSPBAddress)addressAnalysis.Content).GetBitIndex() + length - 1) / 16 - ((FujiSPBAddress)addressAnalysis.Content).GetBitIndex() / 16 + 1);
        OperateResultExOne<byte[]> command = FujiSPBHelper.BuildReadCommand(station, (FujiSPBAddress)addressAnalysis.Content, len);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> check = FujiSPBHelper.CheckResponseData((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(SoftBasic.HexStringToBytes(new String(SoftBasic.BytesArrayRemoveBegin((byte[])check.Content, 4)))), ((FujiSPBAddress)addressAnalysis.Content).getBitIndex(), length));
    }

    public static OperateResult Write(IReadWriteDevice device, byte station, String address, boolean value) {
        OperateResultExOne<byte[]> command = FujiSPBHelper.BuildWriteBoolCommand(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return FujiSPBHelper.CheckResponseData((byte[])read.Content);
    }
}

