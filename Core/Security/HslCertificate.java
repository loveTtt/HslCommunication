/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Security;

import HslCommunication.Core.Security.RSACryptoServiceProvider;
import HslCommunication.Core.Security.RSAHelper;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Utilities;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HslCertificate {
    public String From = "";
    public String To = "";
    public Date NotBefore = null;
    public Date NotAfter = null;
    public byte[] PublicKey = null;
    public Date CreateTime = new Date();
    public String KeyWord = "";
    public String UniqueID = null;
    public int EffectiveHours = 0;
    public HashMap<String, String> Descriptions = null;
    private RSACryptoServiceProvider privateRsa;
    private RSACryptoServiceProvider publicRsa;

    public HslCertificate() {
    }

    public HslCertificate(RSACryptoServiceProvider pubKey, RSACryptoServiceProvider priKey) {
        this.publicRsa = pubKey;
        this.privateRsa = priKey;
        this.PublicKey = pubKey.GetPEMPublicKey();
    }

    public HslCertificate(byte[] pubKey, byte[] priKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (pubKey != null) {
            this.publicRsa = RSAHelper.CreateRsaProviderFromPublicKey(Convert.ToBase64String(pubKey));
        }
        if (priKey != null) {
            this.privateRsa = RSAHelper.CreateRsaProviderFromPrivateKey(Convert.ToBase64String(priKey), Convert.ToBase64String(pubKey));
        }
        this.PublicKey = pubKey;
    }

    public void LoadFrom(byte[] hslCertificate) {
        int[] index = new int[]{4};
        this.PublicKey = this.ExtraBytes(hslCertificate, index);
        this.From = this.ExtraString(hslCertificate, index);
        this.To = this.ExtraString(hslCertificate, index);
        this.NotBefore = this.ExtraDateTime(hslCertificate, index);
        this.NotAfter = this.ExtraDateTime(hslCertificate, index);
        this.CreateTime = this.ExtraDateTime(hslCertificate, index);
        this.KeyWord = this.ExtraString(hslCertificate, index);
        this.UniqueID = this.ExtraString(hslCertificate, index);
        this.EffectiveHours = BitConverter.ToInt32(hslCertificate, index[0]);
        index[0] = index[0] + 4;
        int count = this.ExtraShort(hslCertificate, index);
        this.Descriptions = new HashMap();
        for (int i = 0; i < count; ++i) {
            String key = this.ExtraString(hslCertificate, index);
            String value = this.ExtraString(hslCertificate, index);
            this.Descriptions.put(key, value);
        }
    }

    private void AddDateTime(MemoryStream ms, Date data) {
        byte[] buffer = BitConverter.GetBytes(data.getTime() * 1000L);
        this.AddBytes(ms, buffer);
    }

    private void AddBytes(MemoryStream ms, short data) {
        byte[] buffer = BitConverter.GetBytes(data);
        ms.Write(buffer);
    }

    private void AddString(MemoryStream ms, String data) {
        byte[] buffer = Utilities.IsStringNullOrEmpty(data) ? null : Encoding.UTF8.GetBytes(data);
        this.AddBytes(ms, buffer);
    }

    private void AddBytes(MemoryStream ms, byte[] data) {
        int len = data == null ? 0 : data.length;
        ms.Write(BitConverter.GetBytes((short)len), 0, 2);
        if (data != null && len > 0) {
            ms.Write(data, 0, data.length);
        }
    }

    public static Date retainYMD(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    private Date ExtraDateTime(byte[] buffer, int[] index) {
        byte[] data = this.ExtraBytes(buffer, index);
        long value = BitConverter.ToInt64(data, 0);
        Date date = new Date((value - 621355968000000000L) / 10000L);
        return HslCertificate.retainYMD(date);
    }

    private int ExtraShort(byte[] buffer, int[] index) {
        int value = BitConverter.ToUInt16(buffer, index[0]);
        index[0] = index[0] + 2;
        return value;
    }

    private byte[] ExtraBytes(byte[] buffer, int[] index) {
        int len = BitConverter.ToUInt16(buffer, index[0]);
        index[0] = index[0] + 2;
        if (len > 0) {
            byte[] data = Arrays.copyOfRange(buffer, index[0], index[0] + len);
            index[0] = index[0] + len;
            return data;
        }
        return new byte[0];
    }

    private String ExtraString(byte[] buffer, int[] index) {
        byte[] data = this.ExtraBytes(buffer, index);
        if (data == null || data.length == 0) {
            return "";
        }
        return Encoding.UTF8.GetString(data);
    }

    public static boolean VerifyCer(byte[] publicKey, byte[] hslCertificate) {
        if (hslCertificate == null) {
            return false;
        }
        int keyLen = BitConverter.ToUInt16(hslCertificate, 4);
        if (publicKey != null) {
            if (publicKey.length != keyLen) {
                return false;
            }
            for (int i = 0; i < publicKey.length; ++i) {
                if (publicKey[i] == hslCertificate[i + 6]) continue;
                return false;
            }
        }
        int len = BitConverter.ToUInt16(hslCertificate, 0);
        int actlen = BitConverter.ToUInt16(hslCertificate, len + 4);
        try {
            RSACryptoServiceProvider rsa = RSAHelper.CreateRsaProviderFromPublicKey(Convert.ToBase64String(Arrays.copyOfRange(hslCertificate, 6, 6 + keyLen)));
            return rsa.VerifyData(Arrays.copyOfRange(hslCertificate, 4, 4 + len), Arrays.copyOfRange(hslCertificate, len + 6, len + 6 + actlen));
        }
        catch (Exception e) {
            return false;
        }
    }

    public static HslCertificate CreateFrom(byte[] hslCertificate, byte[] pubKey, byte[] priKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        HslCertificate certificate = new HslCertificate(pubKey, priKey);
        certificate.LoadFrom(hslCertificate);
        return certificate;
    }
}

