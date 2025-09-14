package com.chatapp.authservice.model.converter;

import com.chatapp.authservice.model.UserStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(final UserStatus userStatus) {
        if (userStatus == null) {
            return null;
        }
        return userStatus.name();
    }

    @Override
    public UserStatus convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return UserStatus.valueOf(dbData);
    }
}
