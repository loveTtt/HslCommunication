/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Security;

import java.nio.charset.StandardCharsets;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

public class AesCryptography {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;
    private String key;

    public AesCryptography(String key) {
        this.key = key;
        try {
            SecretKeySpec key1 = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            this.encryptCipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            this.encryptCipher.init(1, key1);
            this.decryptCipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            this.decryptCipher.init(2, key1);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public byte[] Encrypt(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return this.encryptCipher.doFinal(data);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] Decrypt(byte[] data) throws BadPaddingException, IllegalBlockSizeException {
        if (data == null) {
            return null;
        }
        return this.decryptCipher.doFinal(data);
    }

    public String getKey() {
        return this.key;
    }
}

