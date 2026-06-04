/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Utilities;

public class BitConverter {
    public static byte[] GetBytes(int value) {
        return Utilities.getBytes(value);
    }

    public static byte[] GetBytes(short value) {
        return Utilities.getBytes(value);
    }

    public static byte[] GetBytes(float value) {
        return Utilities.getBytes(value);
    }

    public static byte[] GetBytes(double value) {
        return Utilities.getBytes(value);
    }

    public static short ToInt16(byte[] value, int startIndex) {
        return Utilities.getShort(value, startIndex);
    }

    public static int ToInt32(byte[] value, int startIndex) {
        return Utilities.getInt(value, startIndex);
    }

    public static long ToInt64(byte[] value, int startIndex) {
        return Utilities.getLong(value, startIndex);
    }

    public static int ToUInt16(byte[] value, int startIndex) {
        return Utilities.getUShort(value, startIndex);
    }

    public static long ToUInt32(byte[] value, int startIndex) {
        return Utilities.getUInt(value, startIndex);
    }
}

