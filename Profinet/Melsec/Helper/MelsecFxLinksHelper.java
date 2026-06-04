/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec.Helper;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.MelsecFxLinksAddress;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.MelsecFxLinksOverTcp;
import HslCommunication.Serial.SoftLRC;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecFxLinksHelper {
    public static byte[] PackCommandWithHeader(MelsecFxLinksOverTcp plc, byte[] command) {
        if (command.length > 3 && command[0] == 5) {
            return command;
        }
        byte[] core = command;
        if (plc.getSumCheck()) {
            core = new byte[command.length + 2];
            Utilities.ByteArrayCopyTo(command, core, 0);
            SoftLRC.CalculateAccAndFill(core, 0, 2);
        }
        if (plc.getFormat() == 1) {
            return SoftBasic.SpliceArray(new byte[]{5}, core);
        }
        if (plc.getFormat() == 4) {
            return SoftBasic.SpliceArray(new byte[]{5}, core, new byte[]{13, 10});
        }
        return SoftBasic.SpliceArray(new byte[]{5}, core);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte station, String address, short length, boolean isBool, byte waitTime) {
        OperateResultExOne<MelsecFxLinksAddress> addressAnalysis = MelsecFxLinksAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        int[] lens = SoftBasic.SplitIntegerToArray(length, isBool ? 256 : 64);
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        for (int i = 0; i < lens.length; ++i) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%02X", station));
            stringBuilder.append("FF");
            if (isBool) {
                stringBuilder.append("BR");
            } else if (((MelsecFxLinksAddress)addressAnalysis.Content).getAddressStart() >= 10000) {
                stringBuilder.append("QR");
            } else {
                stringBuilder.append("WR");
            }
            stringBuilder.append(String.format("%X", waitTime));
            stringBuilder.append(((MelsecFxLinksAddress)addressAnalysis.Content).toString());
            if (lens[i] == 256) {
                stringBuilder.append("00");
            } else {
                stringBuilder.append(String.format("%02X", lens[i]));
            }
            list.add(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
            ((MelsecFxLinksAddress)addressAnalysis.Content).setAddressStart(((MelsecFxLinksAddress)addressAnalysis.Content).getAddressStart() + lens[i]);
        }
        return OperateResultExOne.CreateSuccessResult(list);
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte station, String address, boolean[] value, byte waitTime) {
        OperateResultExOne<MelsecFxLinksAddress> addressAnalysis = MelsecFxLinksAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("BW");
        stringBuilder.append(String.format("%X", waitTime));
        stringBuilder.append(((MelsecFxLinksAddress)addressAnalysis.Content).toString());
        stringBuilder.append(String.format("%02X", value.length));
        for (int i = 0; i < value.length; ++i) {
            stringBuilder.append(value[i] ? "1" : "0");
        }
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildWriteByteCommand(byte station, String address, byte[] value, byte waitTime) {
        OperateResultExOne<MelsecFxLinksAddress> addressAnalysis = MelsecFxLinksAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressAnalysis);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        if (((MelsecFxLinksAddress)addressAnalysis.Content).getAddressStart() >= 10000) {
            stringBuilder.append("QW");
        } else {
            stringBuilder.append("WW");
        }
        stringBuilder.append(String.format("%X", waitTime));
        stringBuilder.append(((MelsecFxLinksAddress)addressAnalysis.Content).toString());
        stringBuilder.append(String.format("%02X", value.length / 2));
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length / 2; ++i) {
            Utilities.ByteArrayCopyTo(SoftBasic.BuildAsciiBytesFrom((short)BitConverter.ToUInt16(value, i * 2)), buffer, 4 * i);
        }
        stringBuilder.append(new String(buffer, StandardCharsets.US_ASCII));
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildStart(byte station, byte waitTime) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("RR");
        stringBuilder.append(String.format("%X", waitTime));
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildStop(byte station, byte waitTime) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("RS");
        stringBuilder.append(String.format("%X", waitTime));
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildReadPlcType(byte station, byte waitTime) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("PC");
        stringBuilder.append(String.format("%X", waitTime));
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<String> GetPlcTypeFromCode(String code) {
        switch (code) {
            case "F2": {
                return OperateResultExOne.CreateSuccessResult("FX1S");
            }
            case "8E": {
                return OperateResultExOne.CreateSuccessResult("FX0N");
            }
            case "8D": {
                return OperateResultExOne.CreateSuccessResult("FX2/FX2C");
            }
            case "9E": {
                return OperateResultExOne.CreateSuccessResult("FX1N/FX1NC");
            }
            case "9D": {
                return OperateResultExOne.CreateSuccessResult("FX2N/FX2NC");
            }
            case "F4": {
                return OperateResultExOne.CreateSuccessResult("FX3G");
            }
            case "F3": {
                return OperateResultExOne.CreateSuccessResult("FX3U/FX3UC");
            }
            case "98": {
                return OperateResultExOne.CreateSuccessResult("A0J2HCPU");
            }
            case "A1": {
                return OperateResultExOne.CreateSuccessResult("A1CPU /A1NCPU");
            }
            case "A2": {
                return OperateResultExOne.CreateSuccessResult("A2CPU/A2NCPU/A2SCPU");
            }
            case "92": {
                return OperateResultExOne.CreateSuccessResult("A2ACPU");
            }
            case "93": {
                return OperateResultExOne.CreateSuccessResult("A2ACPU-S1");
            }
            case "9A": {
                return OperateResultExOne.CreateSuccessResult("A2CCPU");
            }
            case "82": {
                return OperateResultExOne.CreateSuccessResult("A2USCPU");
            }
            case "83": {
                return OperateResultExOne.CreateSuccessResult("A2CPU-S1/A2USCPU-S1");
            }
            case "A3": {
                return OperateResultExOne.CreateSuccessResult("A3CPU/A3NCPU");
            }
            case "94": {
                return OperateResultExOne.CreateSuccessResult("A3ACPU");
            }
            case "A4": {
                return OperateResultExOne.CreateSuccessResult("A3HCPU/A3MCPU");
            }
            case "84": {
                return OperateResultExOne.CreateSuccessResult("A3UCPU");
            }
            case "85": {
                return OperateResultExOne.CreateSuccessResult("A4UCPU");
            }
            case "AB": {
                return OperateResultExOne.CreateSuccessResult("AJ72P25/R25");
            }
            case "8B": {
                return OperateResultExOne.CreateSuccessResult("AJ72LP25/BR15");
            }
        }
        return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType() + " Code:" + code);
    }

    private static String GetErrorText(int error) {
        switch (error) {
            case 2: {
                return StringResources.Language.MelsecFxLinksError02();
            }
            case 3: {
                return StringResources.Language.MelsecFxLinksError03();
            }
            case 6: {
                return StringResources.Language.MelsecFxLinksError06();
            }
            case 7: {
                return StringResources.Language.MelsecFxLinksError07();
            }
            case 10: {
                return StringResources.Language.MelsecFxLinksError0A();
            }
            case 16: {
                return StringResources.Language.MelsecFxLinksError10();
            }
            case 24: {
                return StringResources.Language.MelsecFxLinksError18();
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> CheckPlcResponse(byte[] response) {
        try {
            if (response[0] == 21) {
                int err = Convert.ToInt32(new String(response, 5, 2, StandardCharsets.US_ASCII), 16);
                return new OperateResultExOne<byte[]>(err, MelsecFxLinksHelper.GetErrorText(err));
            }
            if (response[0] != 2 && response[0] != 6) {
                return new OperateResultExOne<byte[]>(response[0], "Check command failed: " + SoftBasic.GetAsciiStringRender(response));
            }
            if (response[0] == 6) {
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }
            int etxIndex = -1;
            for (int i = 5; i < response.length; ++i) {
                if (response[i] != 3) continue;
                etxIndex = i;
                break;
            }
            if (etxIndex == -1) {
                etxIndex = response.length;
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(response, 5, etxIndex - 5));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Check Plc Response failed Error: " + ex.getMessage() + " Source: " + SoftBasic.GetAsciiStringRender(response));
        }
    }

    private static OperateResultExOne<byte[]> ExtraResponse(byte[] response) {
        try {
            byte[] content = new byte[response.length / 2];
            for (int i = 0; i < content.length / 2; ++i) {
                int tmp = Convert.ToInt32(new String(response, i * 4, 4, StandardCharsets.US_ASCII), 16);
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)tmp), content, i * 2);
            }
            return OperateResultExOne.CreateSuccessResult(content);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Extra source data failed: " + ex.getMessage() + "\r\nSource: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    public static OperateResultExOne<byte[]> Read(MelsecFxLinksOverTcp plc, String address, short length) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<ArrayList<byte[]>> command = MelsecFxLinksHelper.BuildReadCommand(stat, address, length, false, plc.getWaittingTime());
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> result = new ArrayList<Byte>();
        for (int j = 0; j < ((ArrayList)command.Content).size(); ++j) {
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(j));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> extra = MelsecFxLinksHelper.CheckPlcResponse((byte[])read.Content);
            if (!extra.IsSuccess) {
                return extra;
            }
            OperateResultExOne<byte[]> content = MelsecFxLinksHelper.ExtraResponse((byte[])extra.Content);
            if (!content.IsSuccess) {
                return content;
            }
            Utilities.ArrayListAddArray(result, (byte[])content.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(result));
    }

    public static OperateResult Write(MelsecFxLinksOverTcp plc, String address, byte[] value) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<byte[]> command = MelsecFxLinksHelper.BuildWriteByteCommand(stat, address, value, plc.getWaittingTime());
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = MelsecFxLinksHelper.CheckPlcResponse((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<boolean[]> ReadBool(MelsecFxLinksOverTcp plc, String address, short length) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<ArrayList<byte[]>> command = MelsecFxLinksHelper.BuildReadCommand(stat, address, length, true, plc.getWaittingTime());
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Boolean> result = new ArrayList<Boolean>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> extra = MelsecFxLinksHelper.CheckPlcResponse((byte[])read.Content);
            if (!extra.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(extra);
            }
            Utilities.ArrayListAddArray(result, Utilities.getBoolArray((byte[])extra.Content, (byte)48));
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(result));
    }

    public static OperateResult Write(MelsecFxLinksOverTcp plc, String address, boolean[] value) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<byte[]> command = MelsecFxLinksHelper.BuildWriteBoolCommand(stat, address, value, plc.getWaittingTime());
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = MelsecFxLinksHelper.CheckPlcResponse((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult StartPLC(MelsecFxLinksOverTcp plc, String parameter) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(parameter, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
        }
        OperateResultExOne<byte[]> command = MelsecFxLinksHelper.BuildStart(stat, plc.getWaittingTime());
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = MelsecFxLinksHelper.CheckPlcResponse((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult StopPLC(MelsecFxLinksOverTcp plc, String parameter) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(parameter, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
        }
        OperateResultExOne<byte[]> command = MelsecFxLinksHelper.BuildStop(stat, plc.getWaittingTime());
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = MelsecFxLinksHelper.CheckPlcResponse((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<String> ReadPlcType(MelsecFxLinksOverTcp plc, String parameter) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(parameter, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
        }
        OperateResultExOne<byte[]> command = MelsecFxLinksHelper.BuildReadPlcType(stat, plc.getWaittingTime());
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> extra = MelsecFxLinksHelper.CheckPlcResponse((byte[])read.Content);
        if (!extra.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extra);
        }
        return MelsecFxLinksHelper.GetPlcTypeFromCode(new String((byte[])read.Content, 5, 2, StandardCharsets.US_ASCII));
    }
}

