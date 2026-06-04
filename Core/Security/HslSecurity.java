/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Security;

public class HslSecurity {
    public static byte[] ByteEncrypt(byte[] enBytes) {
        if (enBytes == null) {
            return null;
        }
        byte[] result = new byte[enBytes.length];
        for (int i = 0; i < enBytes.length; ++i) {
            result[i] = (byte)(enBytes[i] ^ 0xB5);
        }
        return result;
    }

    public static byte[] ByteDecrypt(byte[] deBytes) {
        return HslSecurity.ByteEncrypt(deBytes);
    }
}

