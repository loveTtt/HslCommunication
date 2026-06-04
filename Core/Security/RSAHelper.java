/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Security;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Security.RSACryptoServiceProvider;
import HslCommunication.Core.Types.Convert;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAHelper {
    private static final String privateKeyHead = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String privateKeyEnd = "-----END RSA PRIVATE KEY-----";
    private static final String publicKeyHead = "-----BEGIN PUBLIC KEY-----";
    private static final String publicKeyEnd = "-----END PUBLIC KEY-----";
    private static final byte[] SeqOID = new byte[]{48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0};

    public static RSACryptoServiceProvider CreateRsaProviderFromPublicKey(String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if ((publicKeyString = publicKeyString.trim()).startsWith(publicKeyHead)) {
            publicKeyString = publicKeyString.replace(publicKeyHead, "");
        }
        if (publicKeyString.endsWith(publicKeyEnd)) {
            publicKeyString = publicKeyString.replace(publicKeyEnd, "");
        }
        return new RSACryptoServiceProvider(null, Convert.FromBase64String(publicKeyString));
    }

    public static RSACryptoServiceProvider CreateRsaProviderFromPrivateKey(String priviteKeyString, String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if ((priviteKeyString = priviteKeyString.trim()).startsWith(privateKeyHead)) {
            priviteKeyString = priviteKeyString.replace(privateKeyHead, "");
        }
        if (priviteKeyString.endsWith(privateKeyEnd)) {
            priviteKeyString = priviteKeyString.replace(privateKeyEnd, "");
        }
        if ((publicKeyString = publicKeyString.trim()).startsWith(publicKeyHead)) {
            publicKeyString = publicKeyString.replace(publicKeyHead, "");
        }
        if (publicKeyString.endsWith(publicKeyEnd)) {
            publicKeyString = publicKeyString.replace(publicKeyEnd, "");
        }
        return new RSACryptoServiceProvider(Convert.FromBase64String(priviteKeyString), Convert.FromBase64String(publicKeyString));
    }

    public static byte[] EncryptLargeDataByRSA(byte[] publicKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        return RSAHelper.EncryptLargeDataByRSA(keyFactory.generatePublic(keySpec), data);
    }

    public static byte[] EncryptLargeDataByRSA(PublicKey publicKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, publicKey);
        ArrayList<byte[]> splits = SoftBasic.ArraySplitByLength(data, 110);
        for (int i = 0; i < splits.size(); ++i) {
            byte[] encrypt = cipher.doFinal(splits.get(i));
            ms.write(encrypt, 0, encrypt.length);
        }
        return ms.toByteArray();
    }

    public static byte[] DecryptLargeDataByRSA(byte[] privateKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return RSAHelper.DecryptLargeDataByRSA(keyFactory.generatePrivate(keySpec), data);
    }

    public static byte[] DecryptLargeDataByRSA(PrivateKey privateKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, privateKey);
        ArrayList<byte[]> splits = SoftBasic.ArraySplitByLength(data, 128);
        for (int i = 0; i < splits.size(); ++i) {
            byte[] decrypt = cipher.doFinal(splits.get(i));
            ms.write(decrypt, 0, decrypt.length);
        }
        return ms.toByteArray();
    }
}

