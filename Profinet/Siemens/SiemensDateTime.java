/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Siemens;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.IByteTransform;
import java.util.Calendar;
import java.util.Date;

public class SiemensDateTime {
    public static Date FromByteArray(byte[] bytes) throws Exception {
        return SiemensDateTime.FromByteArrayImpl(bytes);
    }

    public static Date[] ToArray(byte[] bytes) throws Exception {
        int cnt = bytes.length / 8;
        Date[] result = new Date[bytes.length / 8];
        for (int i = 0; i < cnt; ++i) {
            result[i] = SiemensDateTime.FromByteArrayImpl(SoftBasic.BytesArraySelectMiddle(bytes, i * 8, 8));
        }
        return result;
    }

    private static int DecodeBcd(byte input) {
        return 10 * (input >> 4) + (input & 0xF);
    }

    private static int ByteToYear(byte bcdYear) throws Exception {
        int input = SiemensDateTime.DecodeBcd(bcdYear);
        if (input < 90) {
            return input + 2000;
        }
        if (input < 100) {
            return input + 1900;
        }
        throw new Exception("Value '" + input + "' is higher than the maximum '99' of S7 date and time representation.");
    }

    private static int AssertRangeInclusive(int input, byte min, byte max, String field) throws Exception {
        if (input < min) {
            throw new Exception("Value '" + input + "' is lower than the minimum '" + min + "' allowed for " + field + ".");
        }
        if (input > max) {
            throw new Exception("Value '" + input + "' is higher than the maximum '" + max + "' allowed for " + field + ".");
        }
        return input;
    }

    private static Date FromByteArrayImpl(byte[] bytes) throws Exception {
        if (bytes.length != 8) {
            throw new Exception("Parsing a DateTime requires exactly 8 bytes of input data, input data is " + bytes.length + " bytes long.");
        }
        int year = SiemensDateTime.ByteToYear(bytes[0]);
        int month = SiemensDateTime.AssertRangeInclusive(SiemensDateTime.DecodeBcd(bytes[1]), (byte)1, (byte)12, "month");
        int day = SiemensDateTime.AssertRangeInclusive(SiemensDateTime.DecodeBcd(bytes[2]), (byte)1, (byte)31, "day of month");
        int hour = SiemensDateTime.AssertRangeInclusive(SiemensDateTime.DecodeBcd(bytes[3]), (byte)0, (byte)23, "hour");
        int minute = SiemensDateTime.AssertRangeInclusive(SiemensDateTime.DecodeBcd(bytes[4]), (byte)0, (byte)59, "minute");
        int second = SiemensDateTime.AssertRangeInclusive(SiemensDateTime.DecodeBcd(bytes[5]), (byte)0, (byte)59, "second");
        int hsec = SiemensDateTime.AssertRangeInclusive(SiemensDateTime.DecodeBcd(bytes[6]), (byte)0, (byte)99, "first two millisecond digits");
        int msec = SiemensDateTime.AssertRangeInclusive(bytes[7] >> 4, (byte)0, (byte)9, "third millisecond digit");
        int dayOfWeek = SiemensDateTime.AssertRangeInclusive(bytes[7] & 0xF, (byte)1, (byte)7, "day of week");
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, hour, minute, second);
        return c.getTime();
    }

    public static Date GetDTLTime(IByteTransform byteTransform, byte[] buffer, int index) {
        short year = byteTransform.TransInt16(buffer, index);
        byte month = buffer[index + 2];
        byte day = buffer[index + 3];
        byte hour = buffer[index + 5];
        byte minute = buffer[index + 6];
        byte second = buffer[index + 7];
        int microsecond = byteTransform.TransInt32(buffer, index + 8) / 1000 / 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(14, microsecond);
        return calendar.getTime();
    }

    public static byte[] GetBytesFromDTLTime(IByteTransform byteTransform, Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        byte[] buffer = new byte[12];
        System.arraycopy(byteTransform.TransByte((short)calendar.get(1)), 0, buffer, 0, 2);
        buffer[2] = (byte)(calendar.get(2) + 1);
        buffer[3] = (byte)calendar.get(5);
        buffer[4] = 5;
        buffer[5] = (byte)calendar.get(11);
        buffer[6] = (byte)calendar.get(12);
        buffer[7] = (byte)calendar.get(13);
        System.arraycopy(byteTransform.TransByte(calendar.get(14) * 1000 * 1000), 0, buffer, 8, 4);
        return buffer;
    }

    private static byte EncodeBcd(int value) {
        return (byte)(value / 10 << 4 | value % 10);
    }

    private static byte MapYear(int year) {
        return (byte)(year < 2000 ? year - 1900 : year - 2000);
    }

    public static byte[] ToByteArray(Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        return new byte[]{SiemensDateTime.EncodeBcd(SiemensDateTime.MapYear(calendar.get(1))), SiemensDateTime.EncodeBcd(calendar.get(2) + 1), SiemensDateTime.EncodeBcd(calendar.get(5)), SiemensDateTime.EncodeBcd(calendar.get(11)), SiemensDateTime.EncodeBcd(calendar.get(12)), SiemensDateTime.EncodeBcd(calendar.get(13)), SiemensDateTime.EncodeBcd(calendar.get(14) / 10), (byte)(calendar.get(14) % 10 << 4 | calendar.get(7))};
    }

    public static byte[] ToByteArray(Date[] dateTimes) throws Exception {
        byte[] bytes = new byte[dateTimes.length * 8];
        for (int i = 0; i < dateTimes.length; ++i) {
            byte[] byt = SiemensDateTime.ToByteArray(dateTimes[i]);
            System.arraycopy(byt, 0, bytes, i * 8, 8);
        }
        return bytes;
    }
}

