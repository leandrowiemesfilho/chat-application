package com.chatapp.authservice.model.type;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class PostgreSQLEnumType implements UserType<Enum<?>> {
    @Override
    public int getSqlType() {
        return SqlTypes.ENUM;
    }

    @Override
    public Class<Enum<?>> returnedClass() {
        return (Class<Enum<?>>) (Class<?>) Enum.class;
    }

    @Override
    public boolean equals(final Enum<?> x, final Enum<?> y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(final Enum<?> x) {
        return Objects.hashCode(x);
    }

    @Override
    public Enum<?> nullSafeGet(final ResultSet rs, final int position,
                               final SharedSessionContractImplementor session, final Object owner)
            throws SQLException {
        String value = rs.getString(position);

        if (rs.wasNull()) {
            return null;
        }

        // This will be handled by the specific enum converter
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Enum<?> value, final int index,
                            final SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, java.sql.Types.OTHER);
        } else {
            st.setObject(index, value.name(), java.sql.Types.OTHER);
        }
    }

    @Override
    public Enum<?> deepCopy(final Enum<?> value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Enum<?> value) {
        return (Serializable) value;
    }

    @Override
    public Enum<?> assemble(final Serializable cached, final Object owner) {
        return (Enum<?>) cached;
    }

    @Override
    public Enum<?> replace(final Enum<?> detached, final Enum<?> managed, final Object owner) {
        return detached;
    }
}
