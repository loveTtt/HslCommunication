/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.FATEK;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.FatekProgramAddress;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Serial.SoftLRC;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FatekProgramHelper {
    public static String CalculateAcc(String data) {
        byte[] buffer = data.getBytes(StandardCharsets.US_ASCII);
        int count = 0;
        for (int i = 0; i < buffer.length; ++i) {
            count += buffer[i];
        }
        return String.format("%04X", count).substring(2);
    }

    public static byte[] PackFatekCommand(byte station, String cmd) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('\u0002');
        stringBuilder.append(String.format("%02X", station));
        byte[] buffer = new byte[6 + cmd.length()];
        buffer[0] = 2;
        buffer[1] = SoftBasic.BuildAsciiBytesFrom(station)[0];
        buffer[2] = SoftBasic.BuildAsciiBytesFrom(station)[1];
        Utilities.ByteArrayCopyTo(cmd.getBytes(StandardCharsets.US_ASCII), buffer, 3);
        SoftLRC.CalculateAccAndFill(buffer, 0, 3);
        buffer[buffer.length - 1] = 3;
        return buffer;
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadWordCommand(byte station, String address, short length) {
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)extractParameter.Content1).byteValue();
        address = (String)extractParameter.Content2;
        OperateResultExOne<FatekProgramAddress> addressAnalysis = FatekProgramAddress.ParseFrom(address, length);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        ArrayList<byte[]> contentArray = new ArrayList<byte[]>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 64);
        for (int i = 0; i < splits.length; ++i) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("46");
            stringBuilder.append(String.format("%02X", splits[i]));
            if (((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("X") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("Y") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("M") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("S") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("T") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("C")) {
                stringBuilder.append("W");
            }
            stringBuilder.append(((FatekProgramAddress)addressAnalysis.Content).toString());
            contentArray.add(FatekProgramHelper.PackFatekCommand(station, stringBuilder.toString()));
            if (((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("X") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("Y") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("M") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("S") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("T") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("C")) {
                ((FatekProgramAddress)addressAnalysis.Content).setAddressOffset(splits[i] * 16);
                continue;
            }
            ((FatekProgramAddress)addressAnalysis.Content).setAddressOffset(splits[i]);
        }
        return OperateResultExOne.CreateSuccessResult(contentArray);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadBoolCommand(byte station, String address, short length) {
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)extractParameter.Content1).byteValue();
        address = (String)extractParameter.Content2;
        OperateResultExOne<FatekProgramAddress> addressAnalysis = FatekProgramAddress.ParseFrom(address, length);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        ArrayList<byte[]> contentArray = new ArrayList<byte[]>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 255);
        for (int i = 0; i < splits.length; ++i) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("44");
            stringBuilder.append(String.format("%02X", splits[i]));
            stringBuilder.append(((FatekProgramAddress)addressAnalysis.Content).toString());
            contentArray.add(FatekProgramHelper.PackFatekCommand(station, stringBuilder.toString()));
            ((FatekProgramAddress)addressAnalysis.Content).setAddressOffset(splits[i]);
        }
        return OperateResultExOne.CreateSuccessResult(contentArray);
    }

    public static byte[] ExtraResponse(byte[] response, short length) {
        byte[] Content = new byte[length * 2];
        for (int i = 0; i < Content.length / 2; ++i) {
            int tmp = Integer.parseInt(new String(response, i * 4 + 6, 4, StandardCharsets.US_ASCII), 16);
            byte[] buffer = Utilities.getBytes(tmp);
            Content[i * 2] = buffer[0];
            Content[i * 2 + 1] = buffer[1];
        }
        return Content;
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte station, String address, boolean[] value) {
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)extractParameter.Content1).byteValue();
        address = (String)extractParameter.Content2;
        OperateResultExOne<FatekProgramAddress> addressAnalysis = FatekProgramAddress.ParseFrom(address, 0);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("45");
        stringBuilder.append(String.format("%02X", value.length));
        stringBuilder.append(((FatekProgramAddress)addressAnalysis.Content).toString());
        for (int i = 0; i < value.length; ++i) {
            stringBuilder.append(value[i] ? "1" : "0");
        }
        return OperateResultExOne.CreateSuccessResult(FatekProgramHelper.PackFatekCommand(station, stringBuilder.toString()));
    }

    public static OperateResultExOne<byte[]> BuildWriteByteCommand(byte station, String address, byte[] value) {
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", station);
        station = ((Integer)extractParameter.Content1).byteValue();
        address = (String)extractParameter.Content2;
        OperateResultExOne<FatekProgramAddress> addressAnalysis = FatekProgramAddress.ParseFrom(address, 0);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("47");
        stringBuilder.append(String.format("%02X", value.length / 2));
        if (((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("X") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("Y") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("M") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("S") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("T") || ((FatekProgramAddress)addressAnalysis.Content).getDataCode().startsWith("C")) {
            stringBuilder.append("W");
        }
        stringBuilder.append(((FatekProgramAddress)addressAnalysis.Content).toString());
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length / 2; ++i) {
            byte[] tmp = SoftBasic.BuildAsciiBytesFrom(Utilities.getShort(value, i * 2));
            System.arraycopy(tmp, 0, buffer, 4 * i, tmp.length);
        }
        stringBuilder.append(new String(buffer, StandardCharsets.US_ASCII));
        return OperateResultExOne.CreateSuccessResult(FatekProgramHelper.PackFatekCommand(station, stringBuilder.toString()));
    }

    public static OperateResult CheckResponse(byte[] content) {
        if (content[0] != 2) {
            return new OperateResult(content[0], "Write Faild:" + SoftBasic.ByteToHexString(content, ' '));
        }
        if (content[5] != 48) {
            return new OperateResult(content[5], FatekProgramHelper.GetErrorDescriptionFromCode((char)content[5]));
        }
        return OperateResult.CreateSuccessResult();
    }

    public static String GetErrorDescriptionFromCode(char code) {
        switch (code) {
            case '2': {
                return StringResources.Language.FatekStatus02();
            }
            case '3': {
                return StringResources.Language.FatekStatus03();
            }
            case '4': {
                return StringResources.Language.FatekStatus04();
            }
            case '5': {
                return StringResources.Language.FatekStatus05();
            }
            case '6': {
                return StringResources.Language.FatekStatus06();
            }
            case '7': {
                return StringResources.Language.FatekStatus07();
            }
            case '9': {
                return StringResources.Language.FatekStatus09();
            }
            case 'A': {
                return StringResources.Language.FatekStatus10();
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> Read(IReadWriteDevice device, byte station, String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = FatekProgramHelper.BuildReadWordCommand(station, address, length);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> content = new ArrayList<Byte>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 64);
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult check = FatekProgramHelper.CheckResponse((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            Utilities.ArrayListAddArray(content, FatekProgramHelper.ExtraResponse((byte[])read.Content, (short)splits[i]));
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(content));
    }

    public static OperateResult Write(IReadWriteDevice device, byte station, String address, byte[] value) {
        OperateResultExOne<byte[]> command = FatekProgramHelper.BuildWriteByteCommand(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = FatekProgramHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice device, byte station, String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = FatekProgramHelper.BuildReadBoolCommand(station, address, length);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Boolean> content = new ArrayList<Boolean>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 255);
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResult check = FatekProgramHelper.CheckResponse((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            byte[] buffer = SoftBasic.BytesArraySelectMiddle((byte[])read.Content, 6, splits[i]);
            boolean[] booleans = new boolean[buffer.length];
            for (int j = 0; j < booleans.length; ++j) {
                booleans[j] = buffer[j] == 49;
            }
            Utilities.ArrayListAddArray(content, booleans);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(content));
    }

    public static OperateResult Write(IReadWriteDevice device, byte station, String address, boolean[] value) {
        OperateResultExOne<byte[]> command = FatekProgramHelper.BuildWriteBoolCommand(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = FatekProgramHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult Run(IReadWriteDevice device, byte station) {
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(FatekProgramHelper.PackFatekCommand(station, "411"));
        if (!read.IsSuccess) {
            return read;
        }
        return FatekProgramHelper.CheckResponse((byte[])read.Content);
    }

    public static OperateResult Stop(IReadWriteDevice device, byte station) {
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(FatekProgramHelper.PackFatekCommand(station, "410"));
        if (!read.IsSuccess) {
            return read;
        }
        return FatekProgramHelper.CheckResponse((byte[])read.Content);
    }

    public static OperateResultExOne<boolean[]> ReadStatus(IReadWriteDevice device, byte station) {
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(FatekProgramHelper.PackFatekCommand(station, "40"));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult check = FatekProgramHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(SoftBasic.HexStringToBytes(new String((byte[])read.Content, 6, 2, StandardCharsets.US_ASCII))));
    }
}

