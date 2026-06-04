/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

public class Array {
    public static void Copy(byte[] source, int sourceIndex, byte[] target, int targetIndex, int length) {
        System.arraycopy(source, sourceIndex, target, targetIndex, length);
    }

    public static void Copy(boolean[] source, int sourceIndex, boolean[] target, int targetIndex, int length) {
        System.arraycopy(source, sourceIndex, target, targetIndex, length);
    }

    public static byte[] GetByteFromBoolArray(boolean[] values, int trueValue, int falseValue) {
        if (values == null) {
            return null;
        }
        if (values.length == 0) {
            return new byte[0];
        }
        byte[] buffer = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            buffer[i] = values[i] ? (byte)trueValue : (byte)falseValue;
        }
        return buffer;
    }

    public static byte[] GetByteFromBoolArray(boolean[] values) {
        return Array.GetByteFromBoolArray(values, 1, 0);
    }

    public static boolean[] GetBoolArrayFromBytes(byte[] values, int trueValue) {
        if (values == null) {
            return null;
        }
        if (values.length == 0) {
            return new boolean[0];
        }
        boolean[] buffer = new boolean[values.length];
        for (int i = 0; i < values.length; ++i) {
            buffer[i] = values[i] == trueValue;
        }
        return buffer;
    }
}

