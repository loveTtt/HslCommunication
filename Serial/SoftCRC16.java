/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Serial;

public class SoftCRC16 {
    public static boolean CheckCRC16(byte[] value) {
        return SoftCRC16.CheckCRC16(value, (byte)-96, (byte)1);
    }

    public static boolean CheckCRC16(byte[] value, byte CH, byte CL) {
        if (value == null) {
            return false;
        }
        if (value.length < 2) {
            return false;
        }
        int length = value.length;
        byte[] buf = new byte[length - 2];
        System.arraycopy(value, 0, buf, 0, buf.length);
        byte[] CRCbuf = SoftCRC16.CRC16(buf, CH, CL);
        return CRCbuf[length - 2] == value[length - 2] && CRCbuf[length - 1] == value[length - 1];
    }

    public static byte[] CRC16(byte[] value) {
        return SoftCRC16.CRC16(value, (byte)-96, (byte)1);
    }

    public static byte[] CRC16(byte[] value, byte CH, byte CL) {
        return SoftCRC16.CRC16(value, CH, CL, 255, 255);
    }

    public static byte[] CRC16(byte[] value, byte CH, byte CL, int preH, int preL) {
        byte[] buf = new byte[value.length + 2];
        System.arraycopy(value, 0, buf, 0, value.length);
        int CRC16Lo = preH;
        int CRC16Hi = preL;
        byte[] tmpData = value;
        for (int i = 0; i < tmpData.length; ++i) {
            CRC16Lo = tmpData[i] >= 0 ? CRC16Lo ^ tmpData[i] : CRC16Lo ^ tmpData[i] + 256;
            for (int Flag = 0; Flag <= 7; ++Flag) {
                int SaveHi = CRC16Hi;
                int SaveLo = CRC16Lo;
                CRC16Hi >>= 1;
                CRC16Lo >>= 1;
                if ((SaveHi & 1) == 1) {
                    CRC16Lo |= 0x80;
                }
                if ((SaveLo & 1) != 1) continue;
                CRC16Hi = CH >= 0 ? CRC16Hi ^ CH : CRC16Hi ^ CH + 256;
                CRC16Lo = CL >= 0 ? CRC16Lo ^ CL : CRC16Lo ^ CL + 256;
            }
        }
        buf[buf.length - 2] = (byte)CRC16Lo;
        buf[buf.length - 1] = (byte)CRC16Hi;
        return buf;
    }
}

