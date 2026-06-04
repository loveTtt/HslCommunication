/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Security;

import HslCommunication.Core.Security.RSAHelper;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSACryptoServiceProvider {
    private byte[] privatePEMKey = null;
    private RSAPrivateKey privateKey = null;
    private byte[] publicPEMKey = null;
    private RSAPublicKey publicKey = null;

    public RSACryptoServiceProvider() {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert (keyPairGen != null);
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        this.privateKey = (RSAPrivateKey)keyPair.getPrivate();
        this.publicKey = (RSAPublicKey)keyPair.getPublic();
        this.privatePEMKey = this.privateKey.getEncoded();
        this.publicPEMKey = this.publicKey.getEncoded();
    }

    public RSACryptoServiceProvider(byte[] privatePEMKey, byte[] publicPEMKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        EncodedKeySpec keySpec;
        this.privatePEMKey = privatePEMKey;
        this.publicPEMKey = publicPEMKey;
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (this.privatePEMKey != null) {
            keySpec = new PKCS8EncodedKeySpec(privatePEMKey);
            this.privateKey = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
        }
        if (this.publicPEMKey != null) {
            keySpec = new X509EncodedKeySpec(publicPEMKey);
            this.publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        }
    }

    public byte[] DecryptLargeData(byte[] data) throws Exception {
        return RSAHelper.DecryptLargeDataByRSA(this.privateKey, data);
    }

    public byte[] EncryptLargeData(byte[] data) {
        try {
            return RSAHelper.EncryptLargeDataByRSA(this.publicKey, data);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public byte[] GetPEMPublicKey() {
        return this.publicPEMKey;
    }

    public byte[] GetPEMPrivateKey() {
        return this.privatePEMKey;
    }

    public boolean VerifyData(byte[] buffer, byte[] signature) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(this.GetPEMPublicKey());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initVerify(pubKey);
            sign.update(buffer, 0, buffer.length);
            return sign.verify(signature);
        }
        catch (Exception e) {
            return false;
        }
    }
}

