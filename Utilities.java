/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication;

import HslCommunication.Core.Types.Array;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

public class Utilities {
    private static final char[] HEX_CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[]{(byte)(data & 0xFF), (byte)((data & 0xFF00) >> 8)};
        return bytes;
    }

    public static byte[] ReverseNew(byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length == 0) {
            return new byte[0];
        }
        byte[] buffer = new byte[data.length];
        for (int i = 0; i < data.length; ++i) {
            buffer[i] = data[data.length - 1 - i];
        }
        return buffer;
    }

    public static byte[] EveryByteAdd(byte[] data, int add) {
        if (data == null) {
            return null;
        }
        if (data.length == 0) {
            return new byte[0];
        }
        byte[] buffer = new byte[data.length];
        for (int i = 0; i < data.length; ++i) {
            buffer[i] = (byte)((data[i] & 0xFF) + add);
        }
        return buffer;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[]{(byte)(data & 0xFF), (byte)(data >> 8 & 0xFF), (byte)(data >> 16 & 0xFF), (byte)(data >> 24 & 0xFF)};
        return bytes;
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[]{(byte)(data & 0xFFL), (byte)(data >> 8 & 0xFFL), (byte)(data >> 16 & 0xFFL), (byte)(data >> 24 & 0xFFL), (byte)(data >> 32 & 0xFFL), (byte)(data >> 40 & 0xFFL), (byte)(data >> 48 & 0xFFL), (byte)(data >> 56 & 0xFFL)};
        return bytes;
    }

    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return Utilities.getBytes(intBits);
    }

    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return Utilities.getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static byte[] getBytes(ArrayList<Byte> data) {
        if (data == null) {
            return null;
        }
        byte[] array = new byte[data.size()];
        for (int i = 0; i < data.size(); ++i) {
            array[i] = data.get(i);
        }
        return array;
    }

    public static String[] getStrings(ArrayList<String> data) {
        if (data == null) {
            return null;
        }
        String[] array = new String[data.size()];
        for (int i = 0; i < data.size(); ++i) {
            array[i] = data.get(i);
        }
        return array;
    }

    public static byte int2OneByte(int num) {
        return (byte)(num & 0xFF);
    }

    public static int oneByte2Int(byte byteNum) {
        return byteNum > 0 ? byteNum : 256 + byteNum;
    }

    public static void ByteArrayCopyTo(byte[] source, byte[] destination, int index) {
        System.arraycopy(source, 0, destination, index, source.length);
    }

    public static void ByteArrayCopyTo(boolean[] source, boolean[] destination, int index) {
        System.arraycopy(source, 0, destination, index, source.length);
    }

    public static short getShort(byte[] bytes, int index) {
        return (short)(0xFF & bytes[0 + index] | 0xFF00 & bytes[1 + index] << 8);
    }

    public static short getShortReverse(byte[] bytes, int index) {
        return (short)(0xFF & bytes[1 + index] | 0xFF00 & bytes[0 + index] << 8);
    }

    public static int getUShort(byte[] bytes, int index) {
        return 0xFF & bytes[0 + index] | 0xFF00 & bytes[1 + index] << 8;
    }

    public static int getUShortReverse(byte[] bytes, int index) {
        return 0xFF & bytes[1 + index] | 0xFF00 & bytes[0 + index] << 8;
    }

    public static int getInt(byte[] bytes, int index) {
        return 0xFF & bytes[0 + index] | 0xFF00 & bytes[1 + index] << 8 | 0xFF0000 & bytes[2 + index] << 16 | 0xFF000000 & bytes[3 + index] << 24;
    }

    public static long getUInt(byte[] bytes, int index) {
        int value = Utilities.getInt(bytes, index);
        if (value >= 0) {
            return value;
        }
        return 0x100000000L + (long)value;
    }

    public static int getIntReverse(byte[] bytes, int index) {
        return 0xFF & bytes[3 + index] | 0xFF00 & bytes[2 + index] << 8 | 0xFF0000 & bytes[1 + index] << 16 | 0xFF000000 & bytes[0 + index] << 24;
    }

    public static long getUIntReverse(byte[] bytes, int index) {
        int value = Utilities.getIntReverse(bytes, index);
        if (value >= 0) {
            return value;
        }
        return 0x100000000L + (long)value;
    }

    public static boolean[] getBools(ArrayList<Boolean> data) {
        if (data == null) {
            return null;
        }
        boolean[] array = new boolean[data.size()];
        for (int i = 0; i < data.size(); ++i) {
            array[i] = data.get(i);
        }
        return array;
    }

    public static long getLong(byte[] bytes, int index) {
        return 0xFFL & (long)bytes[0 + index] | 0xFF00L & (long)bytes[1 + index] << 8 | 0xFF0000L & (long)bytes[2 + index] << 16 | 0xFF000000L & (long)bytes[3 + index] << 24 | 0xFF00000000L & (long)bytes[4 + index] << 32 | 0xFF0000000000L & (long)bytes[5 + index] << 40 | 0xFF000000000000L & (long)bytes[6 + index] << 48 | 0xFF00000000000000L & (long)bytes[7 + index] << 56;
    }

    public static long getLongReverse(byte[] bytes, int index) {
        return 0xFFL & (long)bytes[7 + index] | 0xFF00L & (long)bytes[6 + index] << 8 | 0xFF0000L & (long)bytes[5 + index] << 16 | 0xFF000000L & (long)bytes[4 + index] << 24 | 0xFF00000000L & (long)bytes[3 + index] << 32 | 0xFF0000000000L & (long)bytes[2 + index] << 40 | 0xFF000000000000L & (long)bytes[1 + index] << 48 | 0xFF00000000000000L & (long)bytes[0 + index] << 56;
    }

    public static float getFloat(byte[] bytes, int index) {
        return Float.intBitsToFloat(Utilities.getInt(bytes, index));
    }

    public static double getDouble(byte[] bytes, int index) {
        long l = Utilities.getLong(bytes, index);
        return Double.longBitsToDouble(l);
    }

    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    public static String getString(byte[] bytes, int index, int length, String charsetName) {
        return new String(bytes, index, length, Charset.forName(charsetName));
    }

    public static String GetStringOrEndChar(byte[] buffer, int index, int length, Charset encoding) {
        for (int i = index; i < index + length; ++i) {
            if (buffer[i] != 0) continue;
            length = i - index;
            break;
        }
        return new String(buffer, index, length, encoding);
    }

    public static UUID Byte2UUID(byte[] data) {
        int i;
        if (data.length != 16) {
            throw new IllegalArgumentException("Invalid UUID byte[]");
        }
        long msb = 0L;
        long lsb = 0L;
        for (i = 0; i < 8; ++i) {
            msb = msb << 8 | (long)(data[i] & 0xFF);
        }
        for (i = 8; i < 16; ++i) {
            lsb = lsb << 8 | (long)(data[i] & 0xFF);
        }
        return new UUID(msb, lsb);
    }

    public static byte[] UUID2Byte(UUID uuid) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream(16);
        DataOutputStream da = new DataOutputStream(ba);
        try {
            da.writeLong(uuid.getMostSignificantBits());
            da.writeLong(uuid.getLeastSignificantBits());
            ba.close();
            da.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = ba.toByteArray();
        byte temp = buffer[0];
        buffer[0] = buffer[3];
        buffer[3] = temp;
        temp = buffer[1];
        buffer[1] = buffer[2];
        buffer[2] = temp;
        temp = buffer[4];
        buffer[4] = buffer[5];
        buffer[5] = temp;
        temp = buffer[6];
        buffer[6] = buffer[7];
        buffer[7] = temp;
        return buffer;
    }

    public static byte[][] ToArray(ArrayList<byte[]> list) {
        byte[][] tmp = new byte[list.size()][];
        for (int i = 0; i < list.size(); ++i) {
            tmp[i] = list.get(i);
        }
        return tmp;
    }

    public static <T> T[] ArrayListToArray(Class<T> tClass, ArrayList<T> list) {
        if (list == null) {
            return null;
        }
        T[] tmp = (T[])java.lang.reflect.Array.newInstance(tClass, list.size());
        for (int i = 0; i < list.size(); ++i) {
            tmp[i] = list.get(i);
        }
        return tmp;
    }

    public static byte[] ToByteArray(ArrayList<Byte> list) {
        byte[] tmp = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            tmp[i] = list.get(i);
        }
        return tmp;
    }

    public static boolean[] ToBoolArray(ArrayList<Boolean> list) {
        boolean[] tmp = new boolean[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            tmp[i] = list.get(i);
        }
        return tmp;
    }

    public static byte[] ToByteArray(boolean[] data) {
        return Utilities.ToByteArray(data, (byte)1, (byte)0);
    }

    public static byte[] ToByteArray(boolean[] data, byte trueValue, byte falseValue) {
        if (data == null) {
            return null;
        }
        byte[] array = new byte[data.length];
        for (int i = 0; i < data.length; ++i) {
            array[i] = data[i] ? trueValue : falseValue;
        }
        return array;
    }

    public static boolean[] getBoolArray(byte[] data) {
        return Utilities.getBoolArray(data, (byte)0);
    }

    public static boolean[] getBoolArray(byte[] data, byte falseValue) {
        if (data == null) {
            return null;
        }
        boolean[] array = new boolean[data.length];
        for (int i = 0; i < data.length; ++i) {
            array[i] = data[i] != falseValue;
        }
        return array;
    }

    public static boolean[] getBoolArray(ArrayList<Byte> data, int length) {
        if (data == null) {
            return null;
        }
        boolean[] array = new boolean[Math.min(data.size(), length)];
        for (int i = 0; i < array.length; ++i) {
            array[i] = data.get(i) != 0;
        }
        return array;
    }

    public static byte[] csharpString2Byte(String str) {
        byte[] byteArray;
        if (str == null) {
            return null;
        }
        try {
            byteArray = str.getBytes("unicode");
        }
        catch (Exception ex) {
            byteArray = str.getBytes();
        }
        if (byteArray.length >= 2) {
            if (byteArray[0] == -1 && byteArray[1] == -2) {
                byte[] newArray = new byte[byteArray.length - 2];
                System.arraycopy(byteArray, 2, newArray, 0, newArray.length);
                byteArray = newArray;
            } else if (byteArray[0] == -2 && byteArray[1] == -1) {
                for (int i = 0; i < byteArray.length; ++i) {
                    byte temp = byteArray[i];
                    byteArray[i] = byteArray[i + 1];
                    byteArray[i + 1] = temp;
                    ++i;
                }
                byte[] newArray = new byte[byteArray.length - 2];
                System.arraycopy(byteArray, 2, newArray, 0, newArray.length);
                byteArray = newArray;
            }
        }
        return byteArray;
    }

    public static String byte2CSharpString(byte[] byteArray) {
        return Utilities.byte2CSharpString(byteArray, 0, byteArray.length);
    }

    public static String byte2CSharpString(byte[] byteArray, String charsetName) {
        return Utilities.byte2CSharpString(byteArray, 0, byteArray.length, charsetName);
    }

    public static String byte2CSharpString(byte[] byteArray, int index, int length) {
        return Utilities.byte2CSharpString(byteArray, index, length, "unicode");
    }

    public static String byte2CSharpString(byte[] byteArray, int index, int length, String charsetName) {
        String str;
        int i;
        if (byteArray == null) {
            return null;
        }
        byte[] buffer = new byte[length];
        for (i = index; i < index + length; ++i) {
            if (i >= byteArray.length) continue;
            buffer[i - index] = byteArray[i];
        }
        for (i = 0; i < buffer.length; ++i) {
            byte temp = buffer[i];
            buffer[i] = buffer[i + 1];
            buffer[i + 1] = temp;
            ++i;
        }
        try {
            str = new String(buffer, charsetName);
        }
        catch (Exception ex) {
            str = new String(byteArray);
        }
        return str;
    }

    public static void bytesReverse(byte[] reverse) {
        if (reverse != null) {
            byte tmp = 0;
            for (int i = 0; i < reverse.length / 2; ++i) {
                tmp = reverse[i];
                reverse[i] = reverse[reverse.length - 1 - i];
                reverse[reverse.length - 1 - i] = tmp;
            }
        }
    }

    public static void ArrayListAddArray(ArrayList<Byte> arrayList, byte[] bytes) {
        if (bytes != null) {
            for (int i = 0; i < bytes.length; ++i) {
                arrayList.add(bytes[i]);
            }
        }
    }

    public static void ArrayListAddArray(ArrayList<Boolean> arrayList, boolean[] booleans) {
        if (booleans != null) {
            for (int i = 0; i < booleans.length; ++i) {
                arrayList.add(booleans[i]);
            }
        }
    }

    public static String bytes2HexString(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            buf[index++] = HEX_CHAR[b >>> 4 & 0xF];
            buf[index++] = HEX_CHAR[b & 0xF];
        }
        return new String(buf);
    }

    public static String getStringDateShort(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static boolean IsStringNullOrEmpty(String string) {
        return string == null || string.length() <= 0;
    }

    public static Date AddDateDays(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, amount);
        return calendar.getTime();
    }

    public static String[] SplitDot(String str) {
        return str.split("\\.");
    }

    public static <TInput, TResult> TResult[] TranslateArray(Class<TResult> type, TInput[] input, Function<TInput, TResult> function) {
        if (input == null) {
            return null;
        }
        TResult[] results = (TResult[])java.lang.reflect.Array.newInstance(type, input.length);
        for (int i = 0; i < input.length; ++i) {
            results[i] = function.apply(input[i]);
        }
        return results;
    }

    public static <T> String[] TranslateStringArray(T[] input) {
        return Utilities.TranslateArray(String.class, input, new Function<T, String>(){

            @Override
            public String apply(T t) {
                return t.toString();
            }
        });
    }

    public static String PadLeft(String value, int count, char insert) {
        StringBuilder stringBuilder = new StringBuilder(value);
        while (stringBuilder.length() < count) {
            stringBuilder.insert(0, insert);
        }
        return stringBuilder.toString();
    }

    public static String PadLeft(String value, int count) {
        return Utilities.PadLeft(value, count, '0');
    }

    public static byte[] CopyFrom(byte[] buffer) {
        if (buffer == null) {
            return null;
        }
        if (buffer.length == 0) {
            return new byte[0];
        }
        byte[] copy = new byte[buffer.length];
        Array.Copy(buffer, 0, copy, 0, buffer.length);
        return copy;
    }

    public static Date getDateFrom(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }

    public static String ToArrayString(String[] value) {
        if (value == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < value.length; ++i) {
            stringBuilder.append(value[i]);
            if (i >= value.length - 1) continue;
            stringBuilder.append(",");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static long calculateDifferenceInSeconds(Date date1, Date date2) {
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long differenceInMillis = Math.abs(time1 - time2);
        return differenceInMillis / 1000L;
    }
}

