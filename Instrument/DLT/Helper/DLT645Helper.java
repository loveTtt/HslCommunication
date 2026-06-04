/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Instrument.DLT.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.HslExtension;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Instrument.DLT.DLTTransform;
import HslCommunication.Instrument.DLT.Helper.DLT645Type;
import HslCommunication.Instrument.DLT.Helper.IDlt645;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class DLT645Helper {
    public static boolean CheckReceiveDataComplete(MemoryStream ms) {
        byte[] buffer = ms.ToArray();
        if (buffer.length < 10) {
            return false;
        }
        int begin = DLT645Helper.FindHeadCode68H(buffer);
        if (begin < 0) {
            return false;
        }
        if (buffer.length < begin + 10) {
            return false;
        }
        return buffer[begin + 9] + 12 + begin == buffer.length && buffer[buffer.length - 1] == 22;
    }

    public static OperateResultExOne<byte[]> GetAddressByteFromString(String address) {
        if (address == null || address.isEmpty()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.DLTAddressCannotNull());
        }
        if (address.length() > 12) {
            return new OperateResultExOne<byte[]>(StringResources.Language.DLTAddressCannotMoreThan12());
        }
        Pattern pattern = Pattern.compile("^[0-9A-A]+$");
        if (!pattern.matcher(address).find()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.DLTAddressMatchFailed());
        }
        if (address.length() < 12) {
            address = Utilities.PadLeft(address, 12, '0');
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ReverseNew(SoftBasic.HexStringToBytes(address)));
    }

    public static OperateResultExOne<byte[]> BuildDlt645EntireCommand(String address, byte control, byte[] dataArea) {
        if (dataArea == null) {
            dataArea = new byte[]{};
        }
        OperateResultExOne<byte[]> add = DLT645Helper.GetAddressByteFromString(address);
        if (!add.IsSuccess) {
            return add;
        }
        byte[] buffer = new byte[12 + dataArea.length];
        buffer[0] = 104;
        Utilities.ByteArrayCopyTo((byte[])add.Content, buffer, 1);
        buffer[7] = 104;
        buffer[8] = control;
        buffer[9] = (byte)dataArea.length;
        if (dataArea.length > 0) {
            Utilities.ByteArrayCopyTo(dataArea, buffer, 10);
            for (int i = 0; i < dataArea.length; ++i) {
                buffer[i + 10] = (byte)((buffer[i + 10] & 0xFF) + 51);
            }
        }
        int count = 0;
        for (int i = 0; i < buffer.length - 2; ++i) {
            count += buffer[i] & 0xFF;
        }
        buffer[buffer.length - 2] = (byte)count;
        buffer[buffer.length - 1] = 22;
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResult CheckResponseCS(byte[] response, int index) {
        if (response.length > 2 + index) {
            byte count = 0;
            for (int i = index; i < response.length - 2; ++i) {
                count = (byte)(count + (response[i] & 0xFF));
            }
            if ((count = (byte)count) == response[response.length - 2]) {
                return OperateResult.CreateSuccessResult();
            }
            return new OperateResult("CS check failed, need[" + response[response.length - 2] + "] actual[" + count + "]");
        }
        return new OperateResult("Receive length too short: " + SoftBasic.ByteToHexString(response));
    }

    public static OperateResultExTwo<String, byte[]> AnalysisBytesAddress(DLT645Type type, String address, String defaultStation) {
        return DLT645Helper.AnalysisBytesAddress(type, address, defaultStation, (short)1);
    }

    public static OperateResultExTwo<String, byte[]> AnalysisBytesAddress(DLT645Type type, String address, String defaultStation, short length) {
        try {
            String region = defaultStation;
            byte[] dataId = null;
            int offset = 0;
            if (type == DLT645Type.DLT2007) {
                byte[] byArray = dataId = length == 1 ? new byte[4] : new byte[5];
                if (length != 1) {
                    dataId[4] = (byte)length;
                }
            } else {
                byte[] byArray = dataId = length == 1 ? new byte[2] : new byte[3];
                if (length != 1) {
                    dataId[0] = (byte)length;
                    offset = 1;
                }
            }
            if (address.indexOf(59) > 0) {
                String[] splits = address.split(";");
                for (int i = 0; i < splits.length; ++i) {
                    if (splits[i].startsWith("s=")) {
                        region = splits[i].substring(2);
                        continue;
                    }
                    Utilities.ByteArrayCopyTo(Utilities.ReverseNew(SoftBasic.HexStringToBytes(splits[i])), dataId, offset);
                }
            } else {
                Utilities.ByteArrayCopyTo(Utilities.ReverseNew(SoftBasic.HexStringToBytes(address)), dataId, offset);
            }
            return OperateResultExTwo.CreateSuccessResult(region, dataId);
        }
        catch (Exception ex) {
            return new OperateResultExTwo<String, byte[]>("Address prase wrong: " + ex.getMessage());
        }
    }

    public static OperateResultExTwo<String, Integer> AnalysisIntegerAddress(String address, String defaultStation) {
        try {
            String region = defaultStation;
            int value = 0;
            if (address.indexOf(59) > 0) {
                String[] splits = Utilities.SplitDot(address);
                for (int i = 0; i < splits.length; ++i) {
                    if (splits[i].startsWith("s=")) {
                        region = splits[i].substring(2);
                        continue;
                    }
                    value = Convert.ToInt32(splits[i]);
                }
            } else {
                value = Convert.ToInt32(address);
            }
            return OperateResultExTwo.CreateSuccessResult(region, value);
        }
        catch (Exception ex) {
            return new OperateResultExTwo<String, Integer>(ex.getMessage());
        }
    }

    public static OperateResult CheckResponse(IDlt645 dlt, byte[] send, byte[] response) {
        if (response.length < 9) {
            return new OperateResult(StringResources.Language.ReceiveDataLengthTooShort());
        }
        OperateResult check = DLT645Helper.CheckResponseCS(response, 0);
        if (!check.IsSuccess) {
            return check;
        }
        OperateResult stationCheck = DLT645Helper.CheckStation(send, response);
        if (!stationCheck.IsSuccess) {
            return stationCheck;
        }
        if ((response[8] & 0x40) == 64) {
            if (response.length < 11) {
                return new OperateResult(StringResources.Language.ReceiveDataLengthTooShort());
            }
            byte error = response[10];
            if (dlt.getDLTType() == DLT645Type.DLT2007) {
                if (SoftBasic.BoolOnByteIndex(error, 0)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit0());
                }
                if (SoftBasic.BoolOnByteIndex(error, 1)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit1());
                }
                if (SoftBasic.BoolOnByteIndex(error, 2)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit2());
                }
                if (SoftBasic.BoolOnByteIndex(error, 3)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit3());
                }
                if (SoftBasic.BoolOnByteIndex(error, 4)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit4());
                }
                if (SoftBasic.BoolOnByteIndex(error, 5)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit5());
                }
                if (SoftBasic.BoolOnByteIndex(error, 6)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit6());
                }
                if (SoftBasic.BoolOnByteIndex(error, 7)) {
                    return new OperateResult(error, StringResources.Language.DLTErrorInfoBit7());
                }
                return new OperateResult(error, StringResources.Language.UnknownError());
            }
            if (SoftBasic.BoolOnByteIndex(error, 0)) {
                return new OperateResult(error, StringResources.Language.DLT1997ErrorInfoBit0());
            }
            if (SoftBasic.BoolOnByteIndex(error, 1)) {
                return new OperateResult(error, StringResources.Language.DLT1997ErrorInfoBit1());
            }
            if (SoftBasic.BoolOnByteIndex(error, 2)) {
                return new OperateResult(error, StringResources.Language.DLT1997ErrorInfoBit2());
            }
            if (SoftBasic.BoolOnByteIndex(error, 4)) {
                return new OperateResult(error, StringResources.Language.DLT1997ErrorInfoBit4());
            }
            if (SoftBasic.BoolOnByteIndex(error, 5)) {
                return new OperateResult(error, StringResources.Language.DLT1997ErrorInfoBit5());
            }
            if (SoftBasic.BoolOnByteIndex(error, 6)) {
                return new OperateResult(error, StringResources.Language.DLT1997ErrorInfoBit6());
            }
            return new OperateResult(error, StringResources.Language.UnknownError());
        }
        return OperateResult.CreateSuccessResult();
    }

    private static OperateResult CheckStation(byte[] send, byte[] response) {
        if (send.length < 8) {
            return OperateResult.CreateSuccessResult();
        }
        if (response.length < 8) {
            return OperateResult.CreateSuccessResult();
        }
        if ((send[1] & 0xFF) == 170 && (send[2] & 0xFF) == 170 && (send[3] & 0xFF) == 170 && (send[4] & 0xFF) == 170 && (send[5] & 0xFF) == 170 && (send[6] & 0xFF) == 170) {
            return OperateResult.CreateSuccessResult();
        }
        if ((send[1] & 0xFF) == 153 && (send[2] & 0xFF) == 153 && (send[3] & 0xFF) == 153 && (send[4] & 0xFF) == 153 && (send[5] & 0xFF) == 153 && (send[6] & 0xFF) == 153) {
            return OperateResult.CreateSuccessResult();
        }
        if (send[1] == response[1] && send[2] == response[2] && send[3] == response[3] && send[4] == response[4] && send[5] == response[5] && send[6] == response[6]) {
            return OperateResult.CreateSuccessResult();
        }
        if (send[1] == response[6] && send[2] == response[5] && send[3] == response[4] && send[4] == response[3] && send[5] == response[2] && send[6] == response[1]) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult("Station check failed, need: " + SoftBasic.ByteToHexString(SoftBasic.BytesArraySelectMiddle(send, 1, 6), ' ') + " But Actual: " + SoftBasic.ByteToHexString(SoftBasic.BytesArraySelectMiddle(response, 1, 6), ' '));
    }

    public static int FindHeadCode68H(byte[] buffer) {
        if (buffer == null) {
            return -1;
        }
        for (int i = 0; i < buffer.length; ++i) {
            if (buffer[i] != 104) continue;
            return i;
        }
        return -1;
    }

    private static OperateResultExOne<byte[]> ReadWithAddress(IDlt645 dlt, String address, byte[] dataArea) {
        OperateResultExOne<byte[]> command = DLT645Helper.BuildDlt645EntireCommand(address, dlt.getDLTType() == DLT645Type.DLT2007 ? (byte)17 : 1, dataArea);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = dlt.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = DLT645Helper.CheckResponse(dlt, (byte[])command.Content, (byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        try {
            if (dlt.getDLTType() == DLT645Type.DLT2007) {
                if (((byte[])read.Content).length < 16) {
                    return OperateResultExOne.CreateSuccessResult(new byte[0]);
                }
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle((byte[])read.Content, 14, ((byte[])read.Content).length - 16));
            }
            if (((byte[])read.Content).length < 14) {
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle((byte[])read.Content, 12, ((byte[])read.Content).length - 14));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("ReadWithAddress failed: " + ex.getMessage() + Environment.NewLine + "Source: " + SoftBasic.ByteToHexString((byte[])read.Content, ' '));
        }
    }

    public static OperateResultExOne<byte[]> Read(IDlt645 dlt, String address, short length) {
        OperateResultExTwo<String, byte[]> analysis = DLT645Helper.AnalysisBytesAddress(dlt.getDLTType(), address, dlt.getStation(), length);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return DLT645Helper.ReadWithAddress(dlt, (String)analysis.Content1, (byte[])analysis.Content2);
    }

    public static OperateResultExOne<String[]> ReadStringArray(IDlt645 dlt, String address) {
        OperateResultExTwo<Boolean, String> extra = HslHelper.ExtractBooleanParameter(address, "reverse");
        boolean reverse = true;
        if (extra.IsSuccess) {
            reverse = (Boolean)extra.Content1;
            address = (String)extra.Content2;
        }
        OperateResultExTwo<String, byte[]> analysis = DLT645Helper.AnalysisBytesAddress(dlt.getDLTType(), address, dlt.getStation(), (short)1);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<byte[]> read = DLT645Helper.ReadWithAddress(dlt, (String)analysis.Content1, (byte[])analysis.Content2);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return DLTTransform.TransStringsFromDLt(dlt.getDLTType(), (byte[])read.Content, (byte[])analysis.Content2, reverse);
    }

    public static OperateResultExOne<double[]> ReadDouble(IDlt645 dlt, String address, short length) {
        OperateResultExOne<String[]> read = DLT645Helper.ReadStringArray(dlt, address);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        try {
            double[] values = new double[length];
            for (int i = 0; i < length; ++i) {
                values[i] = Double.parseDouble(((String[])read.Content)[i]);
            }
            return OperateResultExOne.CreateSuccessResult(values);
        }
        catch (Exception ex) {
            return new OperateResultExOne<double[]>("double.Parse failed: " + ex.getMessage() + Environment.NewLine + "Source: " + Utilities.ToArrayString((String[])read.Content));
        }
    }

    public static OperateResult Function1C(IDlt645 dlt, String password, String opCode, String station, byte controlType, Date validTime) {
        byte[] value = new byte[8];
        value[0] = controlType;
        Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes(HslExtension.DateToString(validTime, "ss-mm-HH-dd-MM-yy")), value, 2);
        byte[] content = null;
        content = dlt.getDLTType() == DLT645Type.DLT2007 ? SoftBasic.SpliceArray(SoftBasic.HexStringToBytes(password), SoftBasic.HexStringToBytes(opCode), value) : value;
        OperateResultExOne<byte[]> command = DLT645Helper.BuildDlt645EntireCommand(Utilities.IsStringNullOrEmpty(station) ? dlt.getStation() : station, (byte)28, content);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = dlt.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return DLT645Helper.CheckResponse(dlt, (byte[])command.Content, (byte[])read.Content);
    }

    public static OperateResult Write(IDlt645 dlt, String password, String opCode, String address, byte[] value) {
        OperateResultExTwo<String, byte[]> analysis = DLT645Helper.AnalysisBytesAddress(dlt.getDLTType(), address, dlt.getStation());
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] content = null;
        content = dlt.getDLTType() == DLT645Type.DLT2007 ? SoftBasic.SpliceArray((byte[])analysis.Content2, SoftBasic.HexStringToBytes(password), SoftBasic.HexStringToBytes(opCode), value) : SoftBasic.SpliceArray((byte[])analysis.Content2, value);
        OperateResultExOne<byte[]> command = DLT645Helper.BuildDlt645EntireCommand((String)analysis.Content1, dlt.getDLTType() == DLT645Type.DLT2007 ? (byte)20 : 4, content);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = dlt.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return DLT645Helper.CheckResponse(dlt, (byte[])command.Content, (byte[])read.Content);
    }

    public static OperateResult Write(IDlt645 dlt, String password, String opCode, String address, String[] value) {
        OperateResultExTwo<Boolean, String> extra = HslHelper.ExtractBooleanParameter(address, "reverse");
        boolean reverse = true;
        if (extra.IsSuccess) {
            reverse = (Boolean)extra.Content1;
            address = (String)extra.Content2;
        }
        OperateResultExTwo<String, byte[]> analysis = DLT645Helper.AnalysisBytesAddress(dlt.getDLTType(), address, dlt.getStation());
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<byte[]> command = DLTTransform.TransDltFromStrings(dlt.getDLTType(), value, (byte[])analysis.Content2, reverse);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        return DLT645Helper.Write(dlt, password, opCode, address, (byte[])command.Content);
    }

    public static OperateResultExOne<String> ReadAddress(IDlt645 dlt) {
        OperateResultExOne<byte[]> command = DLT645Helper.BuildDlt645EntireCommand("AAAAAAAAAAAA", (byte)19, null);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = dlt.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult check = DLT645Helper.CheckResponse(dlt, (byte[])command.Content, (byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        String string = SoftBasic.ByteToHexString(Utilities.ReverseNew(SoftBasic.BytesArraySelectMiddle((byte[])read.Content, 1, 6)));
        dlt.setStation(string);
        return OperateResultExOne.CreateSuccessResult(string);
    }

    public static OperateResult WriteAddress(IDlt645 dlt, String address) {
        OperateResultExOne<byte[]> add = DLT645Helper.GetAddressByteFromString(address);
        if (!add.IsSuccess) {
            return add;
        }
        OperateResultExOne<byte[]> command = DLT645Helper.BuildDlt645EntireCommand("AAAAAAAAAAAA", dlt.getDLTType() == DLT645Type.DLT2007 ? (byte)21 : 10, (byte[])add.Content);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = dlt.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = DLT645Helper.CheckResponse(dlt, (byte[])command.Content, (byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        if (SoftBasic.IsTwoBytesEquel(SoftBasic.BytesArraySelectMiddle((byte[])read.Content, 1, 6), (byte[])DLT645Helper.GetAddressByteFromString((String)address).Content)) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(StringResources.Language.DLTErrorWriteReadCheckFailed());
    }

    public static OperateResult BroadcastTime(IDlt645 dlt, Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%d2", calendar.get(13)));
        stringBuilder.append(String.format("%d2", calendar.get(12)));
        stringBuilder.append(String.format("%d2", calendar.get(10)));
        stringBuilder.append(String.format("%d2", calendar.get(5)));
        stringBuilder.append(String.format("%d2", calendar.get(2)));
        stringBuilder.append(String.format("%d2", calendar.get(1) % 100));
        String hex = stringBuilder.toString();
        OperateResultExOne<byte[]> command = DLT645Helper.BuildDlt645EntireCommand("999999999999", dlt.getDLTType() == DLT645Type.DLT2007 ? (byte)8 : 8, SoftBasic.HexStringToBytes(hex));
        if (!command.IsSuccess) {
            return command;
        }
        return dlt.ReadFromCoreServer((byte[])command.Content, false, true);
    }

    public static OperateResult FreezeCommand(IDlt645 dlt, String dataArea) {
        OperateResultExTwo<String, byte[]> analysis = DLT645Helper.AnalysisBytesAddress(dlt.getDLTType(), dataArea, dlt.getStation());
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<byte[]> command = DLT645Helper.BuildDlt645EntireCommand((String)analysis.Content1, (byte)22, (byte[])analysis.Content2);
        if (!command.IsSuccess) {
            return command;
        }
        if (analysis.Content1 == "999999999999") {
            return dlt.ReadFromCoreServer((byte[])command.Content, false, true);
        }
        OperateResultExOne<byte[]> read = dlt.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return DLT645Helper.CheckResponse(dlt, (byte[])command.Content, (byte[])read.Content);
    }

    private static OperateResultExOne<byte[]> BuildChangeBaudRateCommand(IDlt645 dlt, String baudRate, byte code) {
        OperateResultExTwo<String, Integer> analysis;
        block17: {
            block16: {
                code = 0;
                analysis = DLT645Helper.AnalysisIntegerAddress(baudRate, dlt.getStation());
                if (!analysis.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(analysis);
                }
                if (dlt.getDLTType() != DLT645Type.DLT2007) break block16;
                switch ((Integer)analysis.Content2) {
                    case 600: {
                        code = (byte)2;
                        break block17;
                    }
                    case 1200: {
                        code = (byte)4;
                        break block17;
                    }
                    case 2400: {
                        code = (byte)8;
                        break block17;
                    }
                    case 4800: {
                        code = (byte)16;
                        break block17;
                    }
                    case 9600: {
                        code = (byte)32;
                        break block17;
                    }
                    case 19200: {
                        code = (byte)64;
                        break block17;
                    }
                    default: {
                        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
                    }
                }
            }
            switch ((Integer)analysis.Content2) {
                case 300: {
                    code = (byte)2;
                    break;
                }
                case 600: {
                    code = (byte)4;
                    break;
                }
                case 2400: {
                    code = (byte)16;
                    break;
                }
                case 4800: {
                    code = (byte)32;
                    break;
                }
                case 9600: {
                    code = (byte)64;
                    break;
                }
                default: {
                    return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
                }
            }
        }
        return DLT645Helper.BuildDlt645EntireCommand((String)analysis.Content1, dlt.getDLTType() == DLT645Type.DLT2007 ? (byte)23 : 12, new byte[]{code});
    }

    public static OperateResult ChangeBaudRate(IDlt645 dlt, String baudRate) {
        byte code = 0;
        OperateResultExOne<byte[]> command = DLT645Helper.BuildChangeBaudRateCommand(dlt, baudRate, code);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = dlt.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = DLT645Helper.CheckResponse(dlt, (byte[])command.Content, (byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        if (((byte[])read.Content)[10] == code) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(StringResources.Language.DLTErrorWriteReadCheckFailed());
    }
}

