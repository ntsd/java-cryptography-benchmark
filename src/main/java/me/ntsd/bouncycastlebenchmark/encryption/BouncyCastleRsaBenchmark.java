package me.ntsd.bouncycastlebenchmark.encryption;

import me.ntsd.bouncycastlebenchmark.benchmark.BenchmarkAlgorithm;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;


public class BouncyCastleRsaBenchmark implements BenchmarkAlgorithm {

    private AsymmetricBlockCipher encryptEngine;
    private AsymmetricBlockCipher decryptEngine;

    private AsymmetricCipherKeyPair GenerateKeys() throws NoSuchAlgorithmException {
        RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
        generator.init(new RSAKeyGenerationParameters(
                BigInteger.valueOf(0x10001), // public exponent
                new SecureRandom(), // random number generator
                1024, // key size
                80 // certainty
        ));

        return generator.generateKeyPair();
    }

    public BouncyCastleRsaBenchmark() throws NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleProvider());

        // RSA Init
        AsymmetricCipherKeyPair keyPair = GenerateKeys();
        AsymmetricKeyParameter privateKey = keyPair.getPrivate();
        AsymmetricKeyParameter publicKey = keyPair.getPublic();

        encryptEngine = new RSAEngine();
        encryptEngine.init(true, publicKey); // true for encrypt with publicKey

        decryptEngine = new RSAEngine();
        decryptEngine.init(false, privateKey); // false for decryption with privateKey
    }

    private byte[] encryptRsa(byte[] data) throws Exception {
        return encryptEngine.processBlock(data, 0, data.length);
    }

    private byte[] decryptRsa(byte[] encryptedBytes) throws InvalidCipherTextException {
        return decryptEngine.processBlock(encryptedBytes, 0, encryptedBytes.length);
    }

    @Override
    public String getAlgorithmName() {
        return "Bouncy Castle RSA";
    }

    @Override
    public void run(String text) throws Exception {
        byte[] encryptedBytes = encryptRsa(text.getBytes());
        String decryptedMessage = new String(decryptRsa(encryptedBytes));

        if (!decryptedMessage.equals(text)) {
            throw new Exception("not match");
        }
    }
}
