package org.raisercostin.utils.hibernate;

import java.io.Serializable;
import java.sql.*;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class EnumUserType implements UserType, ParameterizedType {

    private static final int[] SQL_TYPES = { Types.VARCHAR };
    @SuppressWarnings("rawtypes")
	private Class clazz = null;

    public EnumUserType() {
    }

    @Override
	public void setParameterValues(Properties parameters) {
        String className = (String) parameters.get("type");
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't get the class for name [" + className + "].", e);
        }
    }

    @Override
	public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
	public Class<?> returnedClass() {
        return clazz;
    }

    @Override
	@SuppressWarnings("unchecked")
	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException,
            SQLException {
        String name = resultSet.getString(names[0]);
        Object result = null;
        if (!resultSet.wasNull()) {
            result = Enum.valueOf(clazz, name);
        }
        return result;
    }

    @Override
	public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException,
            SQLException {
        if (null == value) {
            preparedStatement.setNull(index, Types.VARCHAR);
        } else {
            preparedStatement.setString(index, ((Enum<?>) value).name());
        }
    }

    @Override
	public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
	public boolean isMutable() {
        return false;
    }

    @Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
	public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
	public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
	public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if ((null == x) || (null == y)) {
            return false;
        }
        return x.equals(y);
    }
}
