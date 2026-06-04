/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.GE;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.GeSRTPAddress;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

public class GeHelper {
    public static OperateResultExOne<byte[]> BuildReadCoreCommand(long id, byte code, byte[] data) {
        byte[] buffer = new byte[56];
        buffer[0] = 2;
        buffer[1] = 0;
        buffer[2] = Utilities.getBytes(id)[0];
        buffer[3] = Utilities.getBytes(id)[1];
        buffer[4] = 0;
        buffer[5] = 0;
        buffer[9] = 1;
        buffer[17] = 1;
        buffer[18] = 0;
        buffer[30] = 6;
        buffer[31] = -64;
        buffer[36] = 16;
        buffer[37] = 14;
        buffer[40] = 1;
        buffer[41] = 1;
        buffer[42] = code;
        System.arraycopy(data, 0, buffer, 43, data.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(long id, GeSRTPAddress address) {
        if (address.getDataCode() == 10 || address.getDataCode() == 12 || address.getDataCode() == 8) {
            address.setLength(address.getLength() / 2);
        }
        byte[] buffer = new byte[]{address.getDataCode(), Utilities.getBytes(address.getAddressStart())[0], Utilities.getBytes(address.getAddressStart())[1], Utilities.getBytes(address.getLength())[0], Utilities.getBytes(address.getLength())[1]};
        return GeHelper.BuildReadCoreCommand(id, (byte)4, buffer);
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(long id, String address, short length, boolean isBit) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, length, isBit);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return GeHelper.BuildReadCommand(id, (GeSRTPAddress)analysis.Content);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(long id, GeSRTPAddress address, byte[] value) {
        int length = address.getLength();
        if (address.getDataCode() == 10 || address.getDataCode() == 12 || address.getDataCode() == 8) {
            length /= 2;
        }
        byte[] buffer = new byte[56 + value.length];
        buffer[0] = 2;
        buffer[1] = 0;
        buffer[2] = Utilities.getBytes(id)[0];
        buffer[3] = Utilities.getBytes(id)[1];
        buffer[4] = Utilities.getBytes(value.length)[0];
        buffer[5] = Utilities.getBytes(value.length)[1];
        buffer[9] = 2;
        buffer[17] = 2;
        buffer[18] = 0;
        buffer[30] = 9;
        buffer[31] = -128;
        buffer[36] = 16;
        buffer[37] = 14;
        buffer[40] = 1;
        buffer[41] = 1;
        buffer[42] = 2;
        buffer[48] = 1;
        buffer[49] = 1;
        buffer[50] = 7;
        buffer[51] = address.getDataCode();
        buffer[52] = Utilities.getBytes(address.getAddressStart())[0];
        buffer[53] = Utilities.getBytes(address.getAddressStart())[1];
        buffer[54] = Utilities.getBytes(length)[0];
        buffer[55] = Utilities.getBytes(length)[1];
        System.arraycopy(value, 0, buffer, 56, value.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(long id, String address, byte[] value) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short)value.length, false);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return GeHelper.BuildWriteCommand(id, (GeSRTPAddress)analysis.Content, value);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(long id, String address, boolean[] value) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short)value.length, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        boolean[] boolArray = new boolean[((GeSRTPAddress)analysis.Content).getAddressStart() % 8 + value.length];
        System.arraycopy(value, 0, boolArray, ((GeSRTPAddress)analysis.Content).getAddressStart() % 8, value.length);
        return GeHelper.BuildWriteCommand(id, (GeSRTPAddress)analysis.Content, SoftBasic.BoolArrayToByte(boolArray));
    }

    public static OperateResultExOne<byte[]> ExtraResponseContent(byte[] content) {
        try {
            if (content[0] != 3) {
                return new OperateResultExOne<byte[]>(content[0], StringResources.Language.UnknownError() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
            }
            if (content[31] == -44) {
                int status = Utilities.getUShort(content, 42);
                if (status != 0) {
                    return new OperateResultExOne<byte[]>(status, StringResources.Language.UnknownError());
                }
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(content, 44, 6));
            }
            if (content[31] == -108) {
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 56));
            }
            return new OperateResultExOne<byte[]>("Extra Wrong:" + StringResources.Language.UnknownError() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Extra Wrong:" + ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    public static OperateResultExOne<Date> ExtraDateTime(byte[] content) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<Date>(StringResources.Language.InsufficientPrivileges());
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(String.format("%02X", content[5])) + 2000, Integer.parseInt(String.format("%02X", content[4])), Integer.parseInt(String.format("%02X", content[3])), Integer.parseInt(String.format("%02X", content[2])), Integer.parseInt(String.format("%02X", content[1])), Integer.parseInt(String.format("%02X", content[0])));
            return OperateResultExOne.CreateSuccessResult(calendar.getTime());
        }
        catch (Exception ex) {
            return new OperateResultExOne<Date>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    public static OperateResultExOne<String> ExtraProgramName(byte[] content) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<String>(StringResources.Language.InsufficientPrivileges());
        }
        try {
            return OperateResultExOne.CreateSuccessResult(new String(content, 18, 16, StandardCharsets.UTF_8).trim());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }
}

