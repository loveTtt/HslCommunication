/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Serial;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;

public class SoftLRC {
    public static byte[] LRC(byte[] value) {
        if (value == null) {
            return null;
        }
        int sum = 0;
        for (int i = 0; i < value.length; ++i) {
            sum += value[i] & 0xFF;
        }
        sum %= 256;
        sum = 256 - sum;
        byte[] LRC = new byte[]{(byte)sum};
        return SoftBasic.SpliceTwoByteArray(value, LRC);
    }

    public static boolean CheckLRC(byte[] value) {
        if (value == null) {
            return false;
        }
        int length = value.length;
        byte[] buf = new byte[length - 1];
        System.arraycopy(value, 0, buf, 0, buf.length);
        byte[] LRCbuf = SoftLRC.LRC(buf);
        return LRCbuf[length - 1] == value[length - 1];
    }

    public static int CalculateAcc(byte[] buffer, int headCount, int lastCount) {
        int count = 0;
        for (int i = headCount; i < buffer.length - lastCount; ++i) {
            count += buffer[i];
        }
        return count;
    }

    public static void CalculateAccAndFill(byte[] buffer, int headCount, int lastCount) {
        byte acc = (byte)SoftLRC.CalculateAcc(buffer, headCount, lastCount);
        Utilities.ByteArrayCopyTo(String.format("%02X", acc).getBytes(StandardCharsets.US_ASCII), buffer, buffer.length - lastCount);
    }

    public static boolean CalculateAccAndCheck(byte[] buffer, int headCount, int lastCount) {
        byte acc = (byte)SoftLRC.CalculateAcc(buffer, headCount, lastCount);
        return String.format("%02X", acc).equals(new String(buffer, buffer.length - lastCount, 2, StandardCharsets.US_ASCII));
    }
}

