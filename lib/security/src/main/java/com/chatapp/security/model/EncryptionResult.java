package com.chatapp.security.model;

import java.util.Base64;

public record EncryptionResult(byte[] cipherText,
                               byte[] iv) {

    public String getCipherTextBase64() {
        return Base64.getEncoder().encodeToString(this.cipherText);
    }

    public String getIvBase64() {
        return Base64.getEncoder().encodeToString(this.iv);
    }
}
