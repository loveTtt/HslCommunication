/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.List;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HslExtension {
    public static boolean GetBoolValue(byte[] bytes, int bytIndex, int boolIndex) {
        return SoftBasic.BoolOnByteIndex(bytes[bytIndex], boolIndex);
    }

    public static boolean GetBoolByIndex(byte[] bytes, int boolIndex) {
        return SoftBasic.BoolOnByteIndex(bytes[boolIndex / 8], boolIndex % 8);
    }

    public static boolean GetBoolByIndex(byte byt, int boolIndex) {
        return SoftBasic.BoolOnByteIndex(byt, boolIndex % 8);
    }

    public static boolean GetBoolByIndex(short value, int boolIndex) {
        return HslExtension.GetBoolByIndex(BitConverter.GetBytes(value), boolIndex);
    }

    public static boolean GetBoolByIndex(int value, int boolIndex) {
        return HslExtension.GetBoolByIndex(BitConverter.GetBytes(value), boolIndex);
    }

    public static boolean GetBoolByIndex(long value, int boolIndex) {
        return HslExtension.GetBoolByIndex(BitConverter.GetBytes(value), boolIndex);
    }

    private static String[] CreateSplits(String value) {
        if (value.startsWith("[")) {
            value = value.substring(1);
        }
        if (value.endsWith("]")) {
            value = value.substring(0, value.length() - 1);
        }
        return value.split("[,;]");
    }

    public static boolean[] StringToBoolArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        boolean[] array = new boolean[splits.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = splits[i].trim().equals("1") ? true : (splits[i].trim().equals("0") ? false : Boolean.parseBoolean(splits[i].trim()));
        }
        return array;
    }

    public static byte[] StringToByteArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        byte[] array = new byte[splits.length];
        for (int i = 0; i < array.length; ++i) {
            short tmp = Short.parseShort(splits[i]);
            array[i] = (byte)tmp;
        }
        return array;
    }

    public static short[] StringToShortArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        short[] array = new short[splits.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Short.parseShort(splits[i]);
        }
        return array;
    }

    public static short[] StringToUShortArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        short[] array = new short[splits.length];
        for (int i = 0; i < array.length; ++i) {
            int tmp = Integer.parseInt(splits[i]);
            array[i] = (short)tmp;
        }
        return array;
    }

    public static int[] StringToIntArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        int[] array = new int[splits.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Integer.parseInt(splits[i]);
        }
        return array;
    }

    public static int[] StringToUIntArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        int[] array = new int[splits.length];
        for (int i = 0; i < array.length; ++i) {
            long tmp = Long.parseLong(splits[i]);
            array[i] = (int)tmp;
        }
        return array;
    }

    public static long[] StringToLongArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        long[] array = new long[splits.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Long.parseLong(splits[i]);
        }
        return array;
    }

    public static float[] StringToFloatArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        float[] array = new float[splits.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Float.parseFloat(splits[i]);
        }
        return array;
    }

    public static double[] StringToDoubleArray(String value) {
        String[] splits = HslExtension.CreateSplits(value);
        double[] array = new double[splits.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Double.parseDouble(splits[i]);
        }
        return array;
    }

    public static boolean StartsWithAndNumber(String value, String[] heads) {
        if (heads == null) {
            return false;
        }
        for (int i = 0; i < heads.length; ++i) {
            if (!value.matches("^" + heads[i] + "[0-9]+[\\S\\s]+")) continue;
            return true;
        }
        return false;
    }

    public static String[] GetStringArray(List<String> list) {
        if (list == null) {
            return new String[0];
        }
        String[] array = new String[list.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (String)list.get(i);
        }
        return array;
    }

    public static String DateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date StringToDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date, new ParsePosition(0));
    }
}

