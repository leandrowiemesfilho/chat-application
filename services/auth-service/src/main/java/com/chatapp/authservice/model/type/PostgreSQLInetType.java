package com.chatapp.authservice.model.type;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Objects;

public class PostgreSQLInetType implements UserType<String> {

    @Override
    public int getSqlType() {
        return SqlTypes.INET;
    }

    @Override
    public Class<String> returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(final String s, final String j1) {
        return Objects.equals(s, j1);
    }

    @Override
    public int hashCode(final String s) {
        return Objects.hashCode(s);
    }

    @Override
    public String nullSafeGet(final ResultSet resultSet, final int position,
                              final SharedSessionContractImplementor sharedSessionContractImplementor, final Object o)
            throws SQLException {
        final String value = resultSet.getString(position);

        return resultSet.wasNull() ? null : value;
    }

    @Override
    public void nullSafeSet(final PreparedStatement preparedStatement, final String value, final int index,
                            final SharedSessionContractImplementor sharedSessionContractImplementor)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.OTHER);
        } else {
            // Validate it's a proper IP address
            try {
                InetAddress.getByName(value);
                preparedStatement.setObject(index, value, Types.OTHER);
            } catch (UnknownHostException e) {
                throw new SQLException("Invalid IP address: " + value, e);
            }
        }
    }

    @Override
    public String deepCopy(final String value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final String value) {
        return value;
    }

    @Override
    public String assemble(final Serializable cached, final Object owner) {
        return (String) cached;
    }
}
