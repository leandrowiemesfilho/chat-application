package com.chatapp.authservice.model.converter;

import com.chatapp.authservice.model.AuthProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AuthProviderConverter implements AttributeConverter<AuthProvider, String> {

    @Override
    public String convertToDatabaseColumn(final AuthProvider authProvider) {
        if (authProvider == null) {
            return null;
        }

        return authProvider.name();
    }

    @Override
    public AuthProvider convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }

        return AuthProvider.valueOf(dbData);
    }
}
