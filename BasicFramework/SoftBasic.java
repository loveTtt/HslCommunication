/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.BasicFramework;

import HslCommunication.BasicFramework.SystemVersion;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class SoftBasic {
    public static SystemVersion FrameworkVersion = new SystemVersion("3.9.1");

    public static String CalculateStreamMD5(String data) {
        return SoftBasic.CalculateStreamMD5(data, "utf8");
    }

    public static String CalculateStreamMD5(String data, String charsetName) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(Utilities.getBytes(data, charsetName));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("None md5!");
        }
        return SoftBasic.ByteToHexString(secretBytes);
    }

    public static String GetSizeDescription(long size) {
        if (size < 1000L) {
            return size + " B";
        }
        if (size < 1000000L) {
            float data = (float)size / 1024.0f;
            return String.format("%.2f", Float.valueOf(data)) + " Kb";
        }
        if (size < 1000000000L) {
            float data = (float)size / 1024.0f / 1024.0f;
            return String.format("%.2f", Float.valueOf(data)) + " Mb";
        }
        float data = (float)size / 1024.0f / 1024.0f / 1024.0f;
        return String.format("%.2f", Float.valueOf(data)) + " Gb";
    }

    public static String GetTimeSpanDescription(long secondTicks) {
        if (secondTicks <= 60L) {
            return secondTicks + StringResources.Language.TimeDescriptionSecond();
        }
        if (secondTicks <= 3600L) {
            float data = (float)secondTicks / 60.0f;
            return String.format("%.1f", Float.valueOf(data)) + StringResources.Language.TimeDescriptionMinute();
        }
        if (secondTicks <= 86400L) {
            float data = (float)secondTicks / 60.0f / 60.0f;
            return String.format("%.2f", Float.valueOf(data)) + StringResources.Language.TimeDescriptionHour();
        }
        float data = (float)secondTicks / 60.0f / 60.0f / 24.0f;
        return String.format("%.2f", Float.valueOf(data)) + StringResources.Language.TimeDescriptionDay();
    }

    public static <T> String ArrayFormat(T[] array) {
        return SoftBasic.ArrayFormat(array, "");
    }

    public static <T> String ArrayFormat(T[] array, String format) {
        if (array == null) {
            return "NULL";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; ++i) {
            sb.append(Utilities.IsStringNullOrEmpty(format) ? array[i].toString() : String.format(format, array[i]));
            if (i == array.length - 1) continue;
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T> String ArrayFormat(T array) {
        return SoftBasic.ArrayFormat(array, "");
    }

    public static <T> String ArrayFormat(T array, String format) {
        StringBuilder sb = new StringBuilder("[");
        if (array.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(array); ++i) {
                sb.append(Utilities.IsStringNullOrEmpty(format) ? Array.get(array, i).toString() : String.format(format, Array.get(array, i)));
                sb.append(",");
            }
            if (Array.getLength(array) > 0 && sb.charAt(sb.length() - 1) == ',') {
                sb.delete(sb.length() - 1, sb.length());
            }
        } else {
            sb.append(Utilities.IsStringNullOrEmpty(format) ? array.toString() : String.format(format, array));
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T> T[] AddArrayData(Class<T> tClass, T[] array, T[] data, int max) {
        int i;
        if (data == null) {
            return array;
        }
        if (data.length == 0) {
            return array;
        }
        if (array.length == max) {
            System.arraycopy(array, data.length, array, 0, array.length - data.length);
            System.arraycopy(data, 0, array, array.length - data.length, data.length);
            return array;
        }
        if (array.length + data.length > max) {
            int i2;
            T[] tmp = (T[])Array.newInstance(tClass, max);
            for (i2 = 0; i2 < max - data.length; ++i2) {
                tmp[i2] = array[i2 + (array.length - max + data.length)];
            }
            for (i2 = 0; i2 < data.length; ++i2) {
                tmp[tmp.length - data.length + i2] = data[i2];
            }
            return tmp;
        }
        T[] tmp = (T[])Array.newInstance(tClass, array.length + data.length);
        for (i = 0; i < array.length; ++i) {
            tmp[i] = array[i];
        }
        for (i = 0; i < data.length; ++i) {
            tmp[tmp.length - data.length + i] = data[i];
        }
        return tmp;
    }

    public static byte[] ArrayExpandToLength(byte[] data, int length) {
        if (data == null) {
            return new byte[0];
        }
        byte[] buffer = new byte[length];
        System.arraycopy(data, 0, buffer, 0, Math.min(data.length, buffer.length));
        return buffer;
    }

    public static byte[] ArrayExpandToLengthEven(byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        if (data.length % 2 == 1) {
            return SoftBasic.ArrayExpandToLength(data, data.length + 1);
        }
        return data;
    }

    public static <T> T[] ArrayExpandToLength(Class<T> tClass, T[] data, int length) {
        if (data == null) {
            return (T[])Array.newInstance(tClass, 0);
        }
        if (data.length == length) {
            return data;
        }
        T[] buffer = (T[])Array.newInstance(tClass, length);
        System.arraycopy(data, 0, buffer, 0, Math.min(data.length, buffer.length));
        return buffer;
    }

    public static <T> T[] ArrayExpandToLengthEven(Class<T> tClass, T[] data) {
        if (data == null) {
            data = (T[])Array.newInstance(tClass, 0);
        }
        if (data.length % 2 == 1) {
            return SoftBasic.ArrayExpandToLength(tClass, data, data.length + 1);
        }
        return data;
    }

    public static <T> ArrayList<T[]> ArraySplitByLength(Class<T> tClass, T[] array, int length) {
        if (array == null) {
            return new ArrayList<T[]>();
        }
        ArrayList<T[]> result = new ArrayList<T[]>();
        int index = 0;
        while (index < array.length) {
            T[] tmp;
            if (index + length < array.length) {
                tmp = (T[])Array.newInstance(tClass, length);
                System.arraycopy(array, index, tmp, 0, length);
                index += length;
                result.add(tmp);
                continue;
            }
            tmp = (T[])Array.newInstance(tClass, array.length - index);
            System.arraycopy(array, index, tmp, 0, tmp.length);
            index += length;
            result.add(tmp);
        }
        return result;
    }

    public static ArrayList<byte[]> ArraySplitByLength(byte[] array, int length) {
        if (array == null) {
            return new ArrayList<byte[]>();
        }
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        int index = 0;
        while (index < array.length) {
            byte[] tmp;
            if (index + length < array.length) {
                tmp = new byte[length];
                System.arraycopy(array, index, tmp, 0, length);
                index += length;
                result.add(tmp);
                continue;
            }
            tmp = new byte[array.length - index];
            System.arraycopy(array, index, tmp, 0, tmp.length);
            index += length;
            result.add(tmp);
        }
        return result;
    }

    public static ArrayList<String[]> ArraySplitByLength(String[] array, int length) {
        if (array == null) {
            return new ArrayList<String[]>();
        }
        ArrayList<String[]> result = new ArrayList<String[]>();
        int index = 0;
        while (index < array.length) {
            String[] tmp;
            if (index + length < array.length) {
                tmp = new String[length];
                System.arraycopy(array, index, tmp, 0, length);
                index += length;
                result.add(tmp);
                continue;
            }
            tmp = new String[array.length - index];
            System.arraycopy(array, index, tmp, 0, tmp.length);
            index += length;
            result.add(tmp);
        }
        return result;
    }

    public static int[] SplitIntegerToArray(int integer, int everyLength) {
        int[] result = new int[integer / everyLength + (integer % everyLength == 0 ? 0 : 1)];
        for (int i = 0; i < result.length; ++i) {
            result[i] = i == result.length - 1 ? (integer % everyLength == 0 ? everyLength : integer % everyLength) : everyLength;
        }
        return result;
    }

    public static boolean IsTwoBytesEquel(byte[] b1, int start1, byte[] b2, int start2, int length) {
        if (b1 == null || b2 == null) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (b1[i + start1] == b2[i + start2]) continue;
            return false;
        }
        return true;
    }

    public static boolean IsTwoBytesEquel(byte[] b1, byte[] b2) {
        if (b1 == null || b2 == null) {
            return false;
        }
        if (b1.length != b2.length) {
            return false;
        }
        return SoftBasic.IsTwoBytesEquel(b1, 0, b2, 0, b1.length);
    }

    public static boolean IsByteTokenEquel(byte[] head, UUID token) {
        return SoftBasic.IsTwoBytesEquel(head, 12, Utilities.UUID2Byte(token), 0, 16);
    }

    public static boolean IsTwoTokenEquel(UUID token1, UUID token2) {
        return SoftBasic.IsTwoBytesEquel(Utilities.UUID2Byte(token1), 0, Utilities.UUID2Byte(token2), 0, 16);
    }

    public static String GetUniqueStringByGuidAndRandom() {
        Random random = new Random();
        return UUID.randomUUID().toString() + (random.nextInt(9000) + 1000);
    }

    public static String ByteToHexString(byte[] InBytes) {
        return SoftBasic.ByteToHexString(InBytes, '\u0000');
    }

    public static String ByteToHexString(byte[] InBytes, char segment) {
        return SoftBasic.ByteToHexString(InBytes, segment, 0);
    }

    public static String ByteToHexString(byte[] InBytes, char segment, int newLineCount) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (InBytes == null || InBytes.length <= 0) {
            return null;
        }
        long tick = 0L;
        for (int i = 0; i < InBytes.length; ++i) {
            String hv = Integer.toHexString(InBytes[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (segment > '\u0000') {
                stringBuilder.append(segment);
            }
            if (newLineCount <= 0 || ++tick < (long)newLineCount) continue;
            stringBuilder.append("\r\n");
            tick = 0L;
        }
        if (segment != '\u0000' && stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == segment) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    public static String ByteToHexString(String InString) throws UnsupportedEncodingException {
        return SoftBasic.ByteToHexString(InString.getBytes("unicode"));
    }

    private static int GetHexCharIndex(char ch) {
        switch (ch) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            case '2': {
                return 2;
            }
            case '3': {
                return 3;
            }
            case '4': {
                return 4;
            }
            case '5': {
                return 5;
            }
            case '6': {
                return 6;
            }
            case '7': {
                return 7;
            }
            case '8': {
                return 8;
            }
            case '9': {
                return 9;
            }
            case 'A': 
            case 'a': {
                return 10;
            }
            case 'B': 
            case 'b': {
                return 11;
            }
            case 'C': 
            case 'c': {
                return 12;
            }
            case 'D': 
            case 'd': {
                return 13;
            }
            case 'E': 
            case 'e': {
                return 14;
            }
            case 'F': 
            case 'f': {
                return 15;
            }
        }
        return -1;
    }

    public static byte[] HexStringToBytes(String hex) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        for (int i = 0; i < hex.length(); ++i) {
            if (i + 1 >= hex.length() || SoftBasic.GetHexCharIndex(hex.charAt(i)) < 0 || SoftBasic.GetHexCharIndex(hex.charAt(i + 1)) < 0) continue;
            ms.write((byte)(SoftBasic.GetHexCharIndex(hex.charAt(i)) * 16 + SoftBasic.GetHexCharIndex(hex.charAt(i + 1))));
            ++i;
        }
        byte[] result = ms.toByteArray();
        try {
            ms.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return result;
    }

    public static byte[] BytesReverseByWord(byte[] inBytes) {
        if (inBytes == null) {
            return null;
        }
        byte[] buffer = SoftBasic.ArrayExpandToLengthEven(Utilities.CopyFrom(inBytes));
        for (int i = 0; i < buffer.length / 2; ++i) {
            byte tmp = buffer[i * 2 + 0];
            buffer[i * 2 + 0] = buffer[i * 2 + 1];
            buffer[i * 2 + 1] = tmp;
        }
        return buffer;
    }

    public static String GetAsciiStringRender(byte[] content) {
        if (content == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length; ++i) {
            if (content[i] < 32 || content[i] > 126) {
                sb.append("\\").append(String.format("%02X", content[i]));
                continue;
            }
            sb.append((char)content[i]);
        }
        return sb.toString();
    }

    public static byte[] GetFromAsciiStringRender(String render) {
        if (Utilities.IsStringNullOrEmpty(render)) {
            return new byte[0];
        }
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        for (int i = 0; i < render.length(); ++i) {
            if (i + 2 < render.length() && render.charAt(i) == '\\' && SoftBasic.GetHexCharIndex(render.charAt(i + 1)) >= 0 && SoftBasic.GetHexCharIndex(render.charAt(i + 2)) >= 0) {
                ms.write((byte)(SoftBasic.GetHexCharIndex(render.charAt(i + 1)) * 16 + SoftBasic.GetHexCharIndex(render.charAt(i + 2))));
                i += 2;
                continue;
            }
            ms.write((byte)render.charAt(i));
        }
        byte[] result = ms.toByteArray();
        try {
            ms.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return result;
    }

    public static byte[] BytesToAsciiBytes(byte[] inBytes) {
        return Utilities.getBytes(SoftBasic.ByteToHexString(inBytes), "ascii");
    }

    public static byte[] AsciiBytesToBytes(byte[] inBytes) {
        return SoftBasic.HexStringToBytes(Utilities.getString(inBytes, "ascii"));
    }

    public static byte[] BuildAsciiBytesFrom(byte value) {
        String hv = Integer.toHexString(value & 0xFF).toUpperCase();
        if (hv.length() < 2) {
            return Utilities.getBytes("0" + hv, "ascii");
        }
        return Utilities.getBytes(hv, "ascii");
    }

    public static byte[] BuildAsciiBytesFrom(short value) {
        String hv = Integer.toHexString(value & 0xFFFF).toUpperCase();
        while (hv.length() < 4) {
            hv = "0" + hv;
        }
        return Utilities.getBytes(hv, "ascii");
    }

    public static byte[] BuildAsciiBytesFrom(int value) {
        StringBuilder hv = new StringBuilder(Integer.toHexString(value).toUpperCase());
        while (hv.length() < 8) {
            hv.insert(0, "0");
        }
        return hv.toString().getBytes(StandardCharsets.US_ASCII);
    }

    public static byte[] BuildAsciiBytesFrom(byte[] value) {
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length; ++i) {
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(value[i]), 0, buffer, 2 * i, 2);
        }
        return buffer;
    }

    private static byte GetDataByBitIndex(int offset) {
        switch (offset) {
            case 0: {
                return 1;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 4;
            }
            case 3: {
                return 8;
            }
            case 4: {
                return 16;
            }
            case 5: {
                return 32;
            }
            case 6: {
                return 64;
            }
            case 7: {
                return -128;
            }
        }
        return 0;
    }

    public static byte[] SpliceArray(byte[] ... arrays) {
        int count = 0;
        for (int i = 0; i < arrays.length; ++i) {
            if (arrays[i] == null || arrays[i].length <= 0) continue;
            count += arrays[i].length;
        }
        int index = 0;
        byte[] buffer = new byte[count];
        for (int i = 0; i < arrays.length; ++i) {
            if (arrays[i] == null || arrays[i].length <= 0) continue;
            System.arraycopy(arrays[i], 0, buffer, index, arrays[i].length);
            index += arrays[i].length;
        }
        return buffer;
    }

    public static boolean BoolOnByteIndex(byte value, int offset) {
        byte temp = SoftBasic.GetDataByBitIndex(offset);
        return (value & temp) == temp;
    }

    public static byte SetBoolOnByteIndex(byte byt, int offset, boolean value) {
        byte temp = SoftBasic.GetDataByBitIndex(offset);
        if (value) {
            return (byte)(byt | temp);
        }
        return (byte)(byt & ~temp);
    }

    public static byte[] BoolArrayToByte(boolean[] array) {
        if (array == null) {
            return null;
        }
        int length = array.length % 8 == 0 ? array.length / 8 : array.length / 8 + 1;
        byte[] buffer = new byte[length];
        for (int i = 0; i < array.length; ++i) {
            if (!array[i]) continue;
            int n = i / 8;
            buffer[n] = (byte)(buffer[n] + SoftBasic.GetDataByBitIndex(i % 8));
        }
        return buffer;
    }

    public static boolean[] ByteToBoolArray(byte[] InBytes, int length) {
        if (InBytes == null) {
            return null;
        }
        if (length > InBytes.length * 8) {
            length = InBytes.length * 8;
        }
        boolean[] buffer = new boolean[length];
        for (int i = 0; i < length; ++i) {
            buffer[i] = SoftBasic.BoolOnByteIndex(InBytes[i / 8], i % 8);
        }
        return buffer;
    }

    public static boolean[] ByteToBoolArray(byte[] InBytes) {
        return InBytes == null ? null : SoftBasic.ByteToBoolArray(InBytes, InBytes.length * 8);
    }

    public static byte[] SpliceTwoByteArray(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == null && bytes2 == null) {
            return null;
        }
        if (bytes1 == null) {
            return bytes2;
        }
        if (bytes2 == null) {
            return bytes1;
        }
        byte[] buffer = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, buffer, 0, bytes1.length);
        System.arraycopy(bytes2, 0, buffer, bytes1.length, bytes2.length);
        return buffer;
    }

    public static boolean[] SpliceTwoBoolArray(boolean[] bytes1, boolean[] bytes2) {
        if (bytes1 == null && bytes2 == null) {
            return null;
        }
        if (bytes1 == null) {
            return bytes2;
        }
        if (bytes2 == null) {
            return bytes1;
        }
        boolean[] buffer = new boolean[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, buffer, 0, bytes1.length);
        System.arraycopy(bytes2, 0, buffer, bytes1.length, bytes2.length);
        return buffer;
    }

    public static byte[] SpliceTwoByteArray(byte[] ... bytes) {
        int count = 0;
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] == null || bytes[i].length <= 0) continue;
            count += bytes[i].length;
        }
        int index = 0;
        byte[] buffer = new byte[count];
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] == null || bytes[i].length <= 0) continue;
            System.arraycopy(buffer, 0, buffer, index, buffer.length);
            index += bytes[i].length;
        }
        return buffer;
    }

    public static String[] SpliceStringArray(String first, String[] array) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(first);
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
        }
        return (String[])list.toArray();
    }

    public static String[] SpliceStringArray(String first, String second, String[] array) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(first);
        list.add(second);
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
        }
        return (String[])list.toArray();
    }

    public static byte[] BytesArrayRemoveBegin(byte[] value, int length) {
        return SoftBasic.BytesArrayRemoveDouble(value, length, 0);
    }

    public static byte[] BytesArrayRemoveLast(byte[] value, int length) {
        return SoftBasic.BytesArrayRemoveDouble(value, 0, length);
    }

    public static byte[] BytesArrayRemoveDouble(byte[] value, int leftLength, int rightLength) {
        if (value == null) {
            return null;
        }
        if (value.length <= leftLength + rightLength) {
            return new byte[0];
        }
        byte[] buffer = new byte[value.length - leftLength - rightLength];
        System.arraycopy(value, leftLength, buffer, 0, buffer.length);
        return buffer;
    }

    public static byte[] BytesArraySelectMiddle(byte[] value, int index, int length) {
        if (value == null) {
            return null;
        }
        byte[] buffer = new byte[Math.min(value.length, length)];
        System.arraycopy(value, index, buffer, 0, buffer.length);
        return buffer;
    }

    public static byte[] BytesArraySelectBegin(byte[] value, int length) {
        byte[] buffer = new byte[Math.min(value.length, length)];
        if (buffer.length > 0) {
            System.arraycopy(value, 0, buffer, 0, buffer.length);
        }
        return buffer;
    }

    public static byte[] BytesArraySelectLast(byte[] value, int length) {
        byte[] buffer = new byte[Math.min(value.length, length)];
        System.arraycopy(value, value.length - length, buffer, 0, buffer.length);
        return buffer;
    }

    public static boolean[] BoolArraySelectMiddle(boolean[] value, int index, int length) {
        if (value == null) {
            return null;
        }
        boolean[] buffer = new boolean[Math.min(value.length, length)];
        System.arraycopy(value, index, buffer, 0, buffer.length);
        return buffer;
    }

    public static boolean[] BoolArraySelectBegin(boolean[] value, int length) {
        boolean[] buffer = new boolean[Math.min(value.length, length)];
        if (buffer.length > 0) {
            System.arraycopy(value, 0, buffer, 0, buffer.length);
        }
        return buffer;
    }

    public static <T> T[] ArrayRemoveBegin(Class<T> clazz, T[] value, int length) {
        return SoftBasic.ArrayRemoveDouble(clazz, value, length, 0);
    }

    public static <T> T[] ArrayRemoveLast(Class<T> clazz, T[] value, int length) {
        return SoftBasic.ArrayRemoveDouble(clazz, value, 0, length);
    }

    public static <T> T[] ArrayRemoveDouble(Class<T> clazz, T[] value, int leftLength, int rightLength) {
        if (value == null) {
            return null;
        }
        if (value.length <= leftLength + rightLength) {
            return (T[])Array.newInstance(clazz, 0);
        }
        T[] buffer = (T[])Array.newInstance(clazz, value.length - leftLength - rightLength);
        System.arraycopy(value, leftLength, buffer, 0, buffer.length);
        return buffer;
    }

    public static <T> T[] ArraySelectMiddle(Class<T> clazz, T[] value, int index, int length) {
        if (value == null) {
            return null;
        }
        T[] buffer = (T[])Array.newInstance(clazz, Math.min(value.length, length));
        System.arraycopy(value, index, buffer, 0, buffer.length);
        return buffer;
    }

    public static <T> T[] ArraySelectBegin(Class<T> clazz, T[] value, int length) {
        if (value == null) {
            return null;
        }
        T[] buffer = (T[])Array.newInstance(clazz, Math.min(value.length, length));
        if (buffer.length > 0) {
            System.arraycopy(value, 0, buffer, 0, buffer.length);
        }
        return buffer;
    }

    public static <T> T[] ArraySelectLast(Class<T> clazz, T[] value, int length) {
        if (value == null) {
            return null;
        }
        T[] buffer = (T[])Array.newInstance(clazz, Math.min(value.length, length));
        System.arraycopy(value, value.length - length, buffer, 0, buffer.length);
        return buffer;
    }
}

