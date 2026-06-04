/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import java.util.Base64;

public class Convert {
    public static String ToBase64String(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    public static byte[] FromBase64String(String value) {
        return Base64.getDecoder().decode(value);
    }

    public static int ToInt32(String value) {
        return Integer.parseInt(value);
    }

    public static int ToInt32(double value) {
        return (int)Math.round(value);
    }

    public static double ToDouble(String value) {
        return Double.parseDouble(value);
    }

    public static boolean ToBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    public static int ToInt32(String value, int fromBase) {
        return Integer.parseInt(value, fromBase);
    }

    public static String ToString(int value, int toBase) {
        if (toBase == 2) {
            return Integer.toBinaryString(value);
        }
        if (toBase == 8) {
            return Integer.toOctalString(value);
        }
        if (toBase == 10) {
            return String.valueOf(value);
        }
        if (toBase == 16) {
            return Integer.toHexString(value);
        }
        return String.valueOf(value);
    }
}

