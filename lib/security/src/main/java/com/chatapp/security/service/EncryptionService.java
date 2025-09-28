package com.chatapp.security.service;

import com.chatapp.security.exception.EncryptionException;
import com.chatapp.security.model.EncryptionResult;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    public EncryptionResult encrypt(final String plaintext, final SecretKey key) throws EncryptionException {
        try {
            final byte[] iv = generateIv();
            final GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            final byte[] cipherText = cipher.doFinal(plaintext.getBytes());

            return new EncryptionResult(cipherText, iv);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt message", e);
        }
    }

    public String decrypt(final byte[] cipherText, final SecretKey key, final byte[] iv) throws EncryptionException {
        try {
            final GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            final byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText);
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt message", e);
        }
    }

    private static byte[] generateIv() {
        final byte[] iv = new byte[IV_LENGTH_BYTE];
        final SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(iv);

        return iv;
    }
}
