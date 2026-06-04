/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Keyence;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Keyence.KeyencePLCS;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

public class KeyenceNanoHelper {
    public static byte[] GetConnectCmd(byte station, boolean useStation) {
        return useStation ? String.format("CR %02d\r", station).getBytes(StandardCharsets.US_ASCII) : "CR\r".getBytes(StandardCharsets.US_ASCII);
    }

    public static byte[] GetDisConnectCmd(byte station, boolean useStation) {
        return "CQ\r".getBytes(StandardCharsets.US_ASCII);
    }

    public static int GetWordAddressMultiple(String type) {
        if (type.equals("CTH") || type.equals("CTC") || type.equals("C") || type.equals("T") || type.equals("TS") || type.equals("TC") || type.equals("CS") || type.equals("CC") || type.equals("AT")) {
            return 2;
        }
        if (type.equals("DM") || type.equals("CM") || type.equals("TM") || type.equals("EM") || type.equals("FM") || type.equals("Z") || type.equals("W") || type.equals("ZF") || type.equals("VM")) {
            return 1;
        }
        return 1;
    }

    private static boolean IsBitAddressDefault(String type) {
        switch (type) {
            case "": 
            case "R": 
            case "B": 
            case "CR": 
            case "MR": 
            case "LR": 
            case "VB": {
                return true;
            }
        }
        return false;
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length, boolean isBit) {
        OperateResultExTwo<String, Integer> addressResult = KeyenceNanoHelper.KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        if (length > 1) {
            length = (short)(length / KeyenceNanoHelper.GetWordAddressMultiple((String)addressResult.Content1));
        }
        StringBuilder cmd = new StringBuilder();
        cmd.append("RDS");
        cmd.append(" ");
        cmd.append((String)addressResult.Content1);
        cmd.append(((Integer)addressResult.Content2).toString());
        if (!isBit && KeyenceNanoHelper.IsBitAddressDefault((String)addressResult.Content1)) {
            cmd.append(".U");
        }
        cmd.append(" ");
        cmd.append(String.valueOf(length));
        cmd.append("\r");
        byte[] _PLCCommand = cmd.toString().getBytes(StandardCharsets.US_ASCII);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] value) {
        OperateResultExTwo<String, Integer> addressResult = KeyenceNanoHelper.KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        StringBuilder cmd = new StringBuilder();
        cmd.append("WRS");
        cmd.append(" ");
        cmd.append((String)addressResult.Content1);
        cmd.append(addressResult.Content2);
        if (KeyenceNanoHelper.IsBitAddressDefault((String)addressResult.Content1)) {
            cmd.append(".U");
        }
        cmd.append(" ");
        int length = value.length / (KeyenceNanoHelper.GetWordAddressMultiple((String)addressResult.Content1) * 2);
        cmd.append(length);
        for (int i = 0; i < length; ++i) {
            cmd.append(" ");
            cmd.append(Utilities.getUShort(value, i * KeyenceNanoHelper.GetWordAddressMultiple((String)addressResult.Content1) * 2));
        }
        cmd.append("\r");
        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildWriteExpansionMemoryCommand(byte unit, int address, byte[] value) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("UWR");
        cmd.append(" ");
        cmd.append(unit);
        cmd.append(" ");
        cmd.append(address);
        cmd.append(".U");
        cmd.append(" ");
        int length = value.length / 2;
        cmd.append(length);
        for (int i = 0; i < length; ++i) {
            cmd.append(" ");
            cmd.append(Utilities.getUShort(value, i * 2));
        }
        cmd.append("\r");
        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, boolean value) {
        OperateResultExTwo<String, Integer> addressResult = KeyenceNanoHelper.KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        StringBuilder cmd = new StringBuilder();
        if (value) {
            cmd.append("ST");
        } else {
            cmd.append("RS");
        }
        cmd.append(" ");
        cmd.append((String)addressResult.Content1);
        cmd.append(addressResult.Content2);
        cmd.append("\r");
        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, boolean[] value) {
        OperateResultExTwo<String, Integer> addressResult = KeyenceNanoHelper.KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        StringBuilder cmd = new StringBuilder();
        cmd.append("WRS");
        cmd.append(" ");
        cmd.append((String)addressResult.Content1);
        cmd.append(addressResult.Content2);
        cmd.append(" ");
        cmd.append(value.length);
        for (int i = 0; i < value.length; ++i) {
            cmd.append(" ");
            cmd.append(value[i] ? "1" : "0");
        }
        cmd.append("\r");
        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    private static String GetErrorText(String err) {
        if (err.startsWith("E0")) {
            return StringResources.Language.KeyenceNanoE0();
        }
        if (err.startsWith("E1")) {
            return StringResources.Language.KeyenceNanoE1();
        }
        if (err.startsWith("E2")) {
            return StringResources.Language.KeyenceNanoE2();
        }
        if (err.startsWith("E4")) {
            return StringResources.Language.KeyenceNanoE4();
        }
        if (err.startsWith("E5")) {
            return StringResources.Language.KeyenceNanoE5();
        }
        if (err.startsWith("E6")) {
            return StringResources.Language.KeyenceNanoE6();
        }
        return StringResources.Language.UnknownError() + " " + err;
    }

    public static OperateResult CheckPlcReadResponse(byte[] ack) {
        if (ack.length == 0) {
            return new OperateResult(StringResources.Language.MelsecFxReceiveZero());
        }
        if (ack[0] == 69) {
            return new OperateResult(KeyenceNanoHelper.GetErrorText(new String(ack, StandardCharsets.US_ASCII)));
        }
        if (ack[ack.length - 1] != 10 && ack[ack.length - 2] != 13) {
            return new OperateResult(StringResources.Language.MelsecFxAckWrong() + " Actual: " + SoftBasic.ByteToHexString(ack, ' '));
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult CheckPlcWriteResponse(byte[] ack) {
        if (ack.length == 0) {
            return new OperateResult(StringResources.Language.MelsecFxReceiveZero());
        }
        if (ack[0] == 79 && ack[1] == 75) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(KeyenceNanoHelper.GetErrorText(new String(ack, StandardCharsets.US_ASCII)));
    }

    public static OperateResultExOne<boolean[]> ExtractActualBoolData(String addressType, byte[] response) {
        try {
            if (Utilities.IsStringNullOrEmpty(addressType)) {
                addressType = "R";
            }
            String strResponse = new String(SoftBasic.BytesArrayRemoveLast(response, 2), StandardCharsets.US_ASCII);
            if (addressType.equals("R") || addressType.equals("CR") || addressType.equals("MR") || addressType.equals("LR") || addressType.equals("B") || addressType.equals("VB")) {
                String[] splits = strResponse.split("\\s+");
                boolean[] values = new boolean[splits.length];
                for (int i = 0; i < values.length; ++i) {
                    values[i] = splits[i].equals("1");
                }
                return OperateResultExOne.CreateSuccessResult(values);
            }
            if (addressType.equals("T") || addressType.equals("C") || addressType.equals("CTH") || addressType.equals("CTC")) {
                String[] splits = strResponse.split("\\s+");
                boolean[] values = new boolean[splits.length];
                for (int i = 0; i < values.length; ++i) {
                    values[i] = splits[i].startsWith("1");
                }
                return OperateResultExOne.CreateSuccessResult(values);
            }
            return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<boolean[]>("Extract Msg\uff1a" + ex.getMessage() + "\r\nData: " + SoftBasic.ByteToHexString(response));
        }
    }

    public static OperateResultExOne<byte[]> ExtractActualData(String addressType, byte[] response) {
        try {
            if (Utilities.IsStringNullOrEmpty(addressType)) {
                addressType = "R";
            }
            String strResponse = new String(SoftBasic.BytesArrayRemoveLast(response, 2), StandardCharsets.US_ASCII);
            String[] splits = strResponse.split("\\s+");
            if (addressType.equals("DM") || addressType.equals("EM") || addressType.equals("FM") || addressType.equals("ZF") || addressType.equals("W") || addressType.equals("TM") || addressType.equals("Z") || addressType.equals("CM") || addressType.equals("VM")) {
                byte[] buffer = new byte[splits.length * 2];
                for (int i = 0; i < splits.length; ++i) {
                    Utilities.ByteArrayCopyTo(Utilities.getBytes((short)Integer.parseInt(splits[i])), buffer, i * 2);
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
            if (addressType.equals("AT") || addressType.equals("TC") || addressType.equals("CC") || addressType.equals("TS") || addressType.equals("CS")) {
                byte[] buffer = new byte[splits.length * 4];
                for (int i = 0; i < splits.length; ++i) {
                    Utilities.ByteArrayCopyTo(Utilities.getBytes(Integer.parseInt(splits[i])), buffer, i * 4);
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
            if (addressType.equals("T") || addressType.equals("C") || addressType.equals("CTH") || addressType.equals("CTC")) {
                byte[] buffer = new byte[splits.length * 4];
                for (int i = 0; i < splits.length; ++i) {
                    String[] datas = splits[i].split(",");
                    Utilities.ByteArrayCopyTo(Utilities.getBytes(Integer.parseInt(datas[1])), buffer, i * 4);
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
            if (addressType.equals("R") || addressType.equals("B") || addressType.equals("MR") || addressType.equals("LR") || addressType.equals("CR") || addressType.equals("VB")) {
                byte[] buffer = new byte[splits.length * 2];
                for (int i = 0; i < splits.length; ++i) {
                    Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)Integer.parseInt(splits[i])), buffer, i * 2);
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Extract Msg\uff1a" + ex.getMessage() + "\r\nData: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    public static OperateResultExTwo<String, Integer> KvAnalysisAddress(String address) {
        try {
            if (address.startsWith("CTH") || address.startsWith("cth")) {
                return OperateResultExTwo.CreateSuccessResult("CTH", Integer.parseInt(address.substring(3)));
            }
            if (address.startsWith("CTC") || address.startsWith("ctc")) {
                return OperateResultExTwo.CreateSuccessResult("CTC", Integer.parseInt(address.substring(3)));
            }
            if (address.startsWith("CR") || address.startsWith("cr")) {
                return OperateResultExTwo.CreateSuccessResult("CR", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("MR") || address.startsWith("mr")) {
                return OperateResultExTwo.CreateSuccessResult("MR", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("LR") || address.startsWith("lr")) {
                return OperateResultExTwo.CreateSuccessResult("LR", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("DM") || address.startsWith("dm")) {
                return OperateResultExTwo.CreateSuccessResult("DM", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("CM") || address.startsWith("cm")) {
                return OperateResultExTwo.CreateSuccessResult("CM", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("W") || address.startsWith("w")) {
                return OperateResultExTwo.CreateSuccessResult("W", Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("TM") || address.startsWith("tm")) {
                return OperateResultExTwo.CreateSuccessResult("TM", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("VM") || address.startsWith("vm")) {
                return OperateResultExTwo.CreateSuccessResult("VM", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("EM") || address.startsWith("em")) {
                return OperateResultExTwo.CreateSuccessResult("EM", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("FM") || address.startsWith("fm")) {
                return OperateResultExTwo.CreateSuccessResult("EM", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("ZF") || address.startsWith("zf")) {
                return OperateResultExTwo.CreateSuccessResult("ZF", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("AT") || address.startsWith("at")) {
                return OperateResultExTwo.CreateSuccessResult("AT", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("TS") || address.startsWith("ts")) {
                return OperateResultExTwo.CreateSuccessResult("TS", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("TC") || address.startsWith("tc")) {
                return OperateResultExTwo.CreateSuccessResult("TC", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("CC") || address.startsWith("cc")) {
                return OperateResultExTwo.CreateSuccessResult("CC", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("CS") || address.startsWith("cs")) {
                return OperateResultExTwo.CreateSuccessResult("CS", Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("Z") || address.startsWith("z")) {
                return OperateResultExTwo.CreateSuccessResult("Z", Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("R") || address.startsWith("r")) {
                return OperateResultExTwo.CreateSuccessResult("", Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("B") || address.startsWith("b")) {
                return OperateResultExTwo.CreateSuccessResult("B", Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("T") || address.startsWith("t")) {
                return OperateResultExTwo.CreateSuccessResult("T", Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("C") || address.startsWith("c")) {
                return OperateResultExTwo.CreateSuccessResult("C", Integer.parseInt(address.substring(1)));
            }
            throw new Exception(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExTwo<String, Integer>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> Read(IReadWriteDevice keyence, String address, short length) {
        if (address.startsWith("unit=")) {
            OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "unit", 0);
            address = (String)extra.Content2;
            byte unit = ((Integer)extra.Content1).byteValue();
            try {
                short offset = Short.parseShort(address);
                return KeyenceNanoHelper.ReadExpansionMemory(keyence, unit, offset, length);
            }
            catch (Exception ex) {
                return new OperateResultExOne<byte[]>("Address is not right, convert ushort wrong! " + ex.getMessage());
            }
        }
        OperateResultExOne<byte[]> command = KeyenceNanoHelper.BuildReadCommand(address, length, false);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult ackResult = KeyenceNanoHelper.CheckPlcReadResponse((byte[])read.Content);
        if (!ackResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(ackResult);
        }
        OperateResultExTwo<String, Integer> addressResult = KeyenceNanoHelper.KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        return KeyenceNanoHelper.ExtractActualData((String)addressResult.Content1, (byte[])read.Content);
    }

    public static OperateResult Write(IReadWriteDevice keyence, String address, byte[] value) {
        OperateResultExOne<byte[]> command = KeyenceNanoHelper.BuildWriteCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult checkResult = KeyenceNanoHelper.CheckPlcWriteResponse((byte[])read.Content);
        if (!checkResult.IsSuccess) {
            return checkResult;
        }
        return OperateResult.CreateSuccessResult();
    }

    private static boolean CheckBoolOnWordAddress(String address) {
        return address.matches("^(DM|CM|EM|FM|ZF|VM)[0-9]+\\.[0-9]+$");
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice keyence, String address, short length) {
        if (KeyenceNanoHelper.CheckBoolOnWordAddress(address)) {
            return HslHelper.ReadBool(keyence, address, length);
        }
        OperateResultExOne<byte[]> command = KeyenceNanoHelper.BuildReadCommand(address, length, true);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult ackResult = KeyenceNanoHelper.CheckPlcReadResponse((byte[])read.Content);
        if (!ackResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(ackResult);
        }
        OperateResultExTwo<String, Integer> addressResult = KeyenceNanoHelper.KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        return KeyenceNanoHelper.ExtractActualBoolData((String)addressResult.Content1, (byte[])read.Content);
    }

    public static OperateResult Write(IReadWriteDevice keyence, String address, boolean value) {
        if (KeyenceNanoHelper.CheckBoolOnWordAddress(address)) {
            return HslHelper.WriteBool(keyence, address, new boolean[]{value});
        }
        OperateResultExOne<byte[]> command = KeyenceNanoHelper.BuildWriteCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult checkResult = KeyenceNanoHelper.CheckPlcWriteResponse((byte[])read.Content);
        if (!checkResult.IsSuccess) {
            return checkResult;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult Write(IReadWriteDevice keyence, String address, boolean[] value) {
        if (KeyenceNanoHelper.CheckBoolOnWordAddress(address)) {
            return HslHelper.WriteBool(keyence, address, value);
        }
        OperateResultExOne<byte[]> command = KeyenceNanoHelper.BuildWriteCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult checkResult = KeyenceNanoHelper.CheckPlcWriteResponse((byte[])read.Content);
        if (!checkResult.IsSuccess) {
            return checkResult;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<KeyencePLCS> ReadPlcType(IReadWriteDevice keyence) {
        String type;
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<KeyencePLCS>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer("?K\r".getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult check = KeyenceNanoHelper.CheckPlcReadResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        switch (type = new String(SoftBasic.BytesArrayRemoveLast((byte[])read.Content, 2), StandardCharsets.US_ASCII)) {
            case "48": 
            case "49": {
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV700);
            }
            case "50": {
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV1000);
            }
            case "51": {
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV3000);
            }
            case "52": {
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV5000);
            }
            case "53": {
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV5500);
            }
        }
        return new OperateResultExOne<KeyencePLCS>("Unknow type:" + type);
    }

    public static OperateResultExOne<Integer> ReadPlcMode(IReadWriteDevice keyence) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<Integer>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer("?M\r".getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult check = KeyenceNanoHelper.CheckPlcReadResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        String type = new String(SoftBasic.BytesArrayRemoveLast((byte[])read.Content, 2), StandardCharsets.US_ASCII);
        if (type.equals("0")) {
            return OperateResultExOne.CreateSuccessResult(0);
        }
        return OperateResultExOne.CreateSuccessResult(1);
    }

    public static OperateResult ClearError(IReadWriteDevice keyence) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(Encoding.ASCII.GetBytes("ER\r"));
        if (!read.IsSuccess) {
            return read;
        }
        return KeyenceNanoHelper.CheckPlcWriteResponse((byte[])read.Content);
    }

    public static OperateResult SetPlcDateTime(IReadWriteDevice keyence, Date dateTime) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(String.format("WRT %02d %02d %02d %02d %02d %02d %d", calendar.get(1) - 2000, calendar.get(2), calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(7)).getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) {
            return read;
        }
        return KeyenceNanoHelper.CheckPlcWriteResponse((byte[])read.Content);
    }

    public static OperateResultExOne<String> ReadAddressAnnotation(IReadWriteDevice keyence, String address) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<String>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(("RDC " + address + "\r").getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult check = KeyenceNanoHelper.CheckPlcReadResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(new String(SoftBasic.BytesArrayRemoveLast((byte[])read.Content, 2)).trim());
    }

    public static OperateResultExOne<byte[]> ReadExpansionMemory(IReadWriteDevice keyence, byte unit, short address, short length) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(("URD " + unit + " " + address + ".U " + length + "\r").getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = KeyenceNanoHelper.CheckPlcReadResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return KeyenceNanoHelper.ExtractActualData("DM", (byte[])read.Content);
    }

    public static OperateResult WriteExpansionMemory(IReadWriteDevice keyence, byte unit, short address, byte[] value) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer((byte[])KeyenceNanoHelper.BuildWriteExpansionMemoryCommand((byte)unit, (int)address, (byte[])value).Content);
        if (!read.IsSuccess) {
            return read;
        }
        return KeyenceNanoHelper.CheckPlcWriteResponse((byte[])read.Content);
    }
}

