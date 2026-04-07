package io.github.epi155.emsql.runtime;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.util.Set;

public class EmSQL {
    private EmSQL() {}

    private static final char C_SPACE = ' ';

    /**
     * Right PAD Space
     *
     * @param value string input value
     * @param length pad/truncate length
     * @return padded/truncated value
     */
    public static String rpads(String value, int length) {
        if (value==null) return null;
        int valueLen = value.length();
        if (valueLen >= length) {
            return value.substring(0, length);
        }

        char[] result = new char[length];
        value.getChars(0, valueLen, result, 0);
        for (int i = valueLen; i < length; i++) {
            result[i] = C_SPACE;
        }
        return new String(result);
    }

    public static class Mul {
        private final int nth;
        private final int rows;
        private final int cols;

        public Mul(int nth, int rows, int cols) {
            this.nth = nth;
            this.rows = rows;
            this.cols = cols;
        }

        public String replace(String query) {
            String placeholder = "[#" + nth + "]";
            int ks = query.indexOf(placeholder);
            if (ks<0)
                return query;   // dead branch
            StringBuilder sb = new StringBuilder();
            sb.append(query, 0, ks);
            if (cols==1) {
                for(int kr = 1; kr<= rows; kr++) {
                    sb.append('?');
                    if (kr< rows)sb.append(',');
                }
            } else {
                for(int kr = 1; kr<= rows; kr++) {
                    sb.append('(');
                    for(int kc=1; kc<=cols; kc++) {
                        sb.append('?');
                        if (kc< cols)sb.append(',');
                    }
                    sb.append(')');
                    if (kr< rows)sb.append(',');
                }
            }
            sb.append(query.substring(ks+placeholder.length()));
            return sb.toString();
        }
    }
    public static String expandQueryParameters(String query, Mul...muls) {
        for(Mul mul: muls) {
            query = mul.replace(query);
        }
        return query;
    }

    public static <T> EConsumer<T> getDummyConsumer() {
        return new EConsumer<T>() {
            @Override
            public void accept(T o) {

            }
        };
    }
    public static <T> ESupplier<T> getDummySupplier() {
        return new ESupplier<T>() {
            @Override
            public T get() {
                return null;
            }
        };
    }

    private static final int IS_SET = 1;
    private static final int IS_GET = 0;
    public static void set(Object target, String path, Object value) {
        String[] pieces = path.split("[.]");
        Object currentObject = passesThrough(target, pieces, IS_SET, false);
        String setterName = "set" + capitalize(pieces[pieces.length-1]);
        Method[] methods = currentObject.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(setterName) &&
                    method.getParameterCount() == 1 && (
                    value == null ||
                            canAssign(method.getParameterTypes()[0], value.getClass())
            )) {
                try {
                    method.invoke(currentObject, value);
                } catch (IllegalArgumentException e) {
                    throw new SqlReflectException("IllegalArgument in " +currentObject.getClass() + "." + setterName + "("+ method.getParameterTypes()[0] + ")", e);
                } catch (IllegalAccessException e) {
                    throw new SqlReflectException("IllegalAccess in " +currentObject.getClass() + "." + setterName + "("+ method.getParameterTypes()[0] + ")", e);
                } catch (InvocationTargetException e) {
                    throw new SqlReflectException("InvocationTarget in " +currentObject.getClass() + "." + setterName + "("+ method.getParameterTypes()[0] + ")", e);
                }
                return;
            }
        }
        throw new SqlReflectException("No method " +currentObject.getClass() + "." + setterName + "("+value.getClass()+")");
    }
    public static <T> T get(Object target, String path, Class<T> claz) {
        String[] pieces = path.split("[.]");
        boolean is = (claz == boolean.class);
        //noinspection unchecked
        return (T) passesThrough(target, pieces, IS_GET, is);
    }
    private static Object passesThrough(Object target, String[] pieces, int limit, boolean is) {
        Object currentObject = target;
        for (int k = 0; k < pieces.length - limit; k++) {
            String getterName;
            if (limit == IS_GET && k+1 == pieces.length - limit && is) {
                getterName = "is" + capitalize(pieces[k]);
            } else {
                getterName = "get" + capitalize(pieces[k]);
            }
            try {
                Method getter = currentObject.getClass().getMethod(getterName);
                currentObject = nextObject(getter, currentObject, limit==IS_SET);
                if (currentObject==null)    // IS_GET
                    break;
            } catch (NoSuchMethodException e) {
                throw new SqlReflectException("No method " +currentObject.getClass() + "." + getterName + "()", e);
            }
        }
        return currentObject;
    }

    private static Object nextObject(Method getter, Object object, boolean create) {
        try {
            getter.setAccessible(true);
            Object result = getter.invoke(object);
            if (result == null && create) {
                Class<?> type = getter.getReturnType();
                result = createResult(type);
                storeResult(result, type, object, getter.getName().replace('g','s'));
            }
            return result;
        } catch (IllegalAccessException e) {
            throw new SqlReflectException("IllegalAccess in " +object.getClass() + "." + getter.getName() + "()", e);
        } catch (InvocationTargetException e) {
            throw new SqlReflectException("InvocationTarget in " +object.getClass() + "." + getter.getName() + "()", e);
        }
    }

    private static void storeResult(Object result, Class<?> type, Object object, String setterName) {
        try {
            Method setter = object.getClass().getMethod(setterName, type);
            setter.setAccessible(true);
            setter.invoke(object, result);
        } catch (NoSuchMethodException e) {
            throw new SqlReflectException("No setter - "+object.getClass().getName() + "." + setterName + "(" + type.getName() + ")", e);
        } catch (InvocationTargetException e) {
            throw new SqlReflectException("Invocation "+object.getClass().getName() + "." + setterName + "(" + type.getName() + ")", e);
        } catch (IllegalAccessException e) {
            throw new SqlReflectException("Access "+object.getClass().getName() + "." + setterName + "(" + type.getName() + ")", e);
        }
    }

    private static Object createResult(Class<?> type) {
        try {
            Constructor<?> ctor = type.getConstructor();
            return  ctor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new SqlReflectException("No Constructor for "+type.getName(), e);
        } catch (InvocationTargetException e) {
            throw new SqlReflectException("Invocation Constructor for "+type.getName(), e);
        } catch (InstantiationException e) {
            throw new SqlReflectException("Instantiation Constructor for "+type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new SqlReflectException("Access Constructor for "+type.getName(), e);
        }
    }

    private static boolean canAssign(Class<?> src, Class<?> dst) {
        if (dst.isAssignableFrom(src))
            return true;
        if (src == int.class)
            return dst == Integer.class;
        if (src == long.class)
            return dst == Long.class;
        if (src == short.class)
            return dst == Short.class;
        if (src == boolean.class)
            return dst == Boolean.class;
        if (src == byte.class)
            return dst == Byte.class;
        if (src == char.class)
            return dst == Character.class;
        if (src == double.class)
            return dst == Double.class;
        if (src == float.class)
            return dst == Float.class;
        return false;
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    //_____________________________________________________________
    //
    public static Boolean getBoolean(ResultSet rs, int i) throws SQLException {
        boolean it=rs.getBoolean(i);
        return rs.wasNull() ? null : it;
    }
    public static Boolean getNumBool(ResultSet rs, int i) throws SQLException {
        byte it=rs.getByte(i);
        return rs.wasNull() ? null : (it==1);
    }
    public static Byte getByte(ResultSet rs, int i) throws SQLException {
        byte it=rs.getByte(i);
        return rs.wasNull() ? null : it;
    }
    public static Short getShort(ResultSet rs, int i) throws SQLException {
        short it=rs.getShort(i);
        return rs.wasNull() ? null : it;
    }
    public static Integer getInt(ResultSet rs, int i) throws SQLException {
        int it=rs.getInt(i);
        return rs.wasNull() ? null : it;
    }
    public static Long getLong(ResultSet rs, int i) throws SQLException {
        long it=rs.getLong(i);
        return rs.wasNull() ? null : it;
    }
    public static Double getDouble(ResultSet rs, int i) throws SQLException {
        double it=rs.getDouble(i);
        return rs.wasNull() ? null : it;
    }
    public static Float getFloat(ResultSet rs, int i) throws SQLException {
        float it=rs.getFloat(i);
        return rs.wasNull() ? null : it;
    }
    //_____________________________________________________________
    //
    public static void setBoolean(PreparedStatement ps, int i, Boolean it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.BOOLEAN);
        } else {
            ps.setBoolean(i, it);
        }
    }
    public static void setNumBool(PreparedStatement ps, int i, Boolean it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TINYINT);
        } else {
            ps.setByte(i, (byte) (it ? 1 : 0));
        }
    }
    public static void setByte(PreparedStatement ps, int i, Byte it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TINYINT);
        } else {
            ps.setByte(i, it);
        }
    }
    public static void setShort(PreparedStatement ps, int i, Short it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.SMALLINT);
        } else {
            ps.setShort(i, it);
        }
    }
    public static void setInt(PreparedStatement ps, int i, Integer it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.INTEGER);
        } else {
            ps.setInt(i, it);
        }
    }
    public static void setLong(PreparedStatement ps, int i, Long it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.BIGINT);
        } else {
            ps.setLong(i, it);
        }
    }
    public static void setDouble(PreparedStatement ps, int i, Double it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.DOUBLE);
        } else {
            ps.setDouble(i, it);
        }
    }
    public static void setFloat(PreparedStatement ps, int i, Float it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.REAL); // FLOAT ?
        } else {
            ps.setFloat(i, it);
        }
    }
    public static void setChar(PreparedStatement ps, int i, String it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.CHAR);
        } else {
            ps.setString(i, it);
        }
    }
    public static void setVarchar(PreparedStatement ps, int i, String it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.VARCHAR);
        } else {
            ps.setString(i, it);
        }
    }
    public static void setDate(PreparedStatement ps, int i, Date it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.DATE);
        } else {
            ps.setDate(i, it);
        }
    }
    public static void setTimestamp(PreparedStatement ps, int i, Timestamp it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(i, it);
        }
    }
    public static void setTime(PreparedStatement ps, int i, Time it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIME);
        } else {
            ps.setTime(i, it);
        }
    }
    public static void setBigInteger(PreparedStatement ps, int i, BigInteger it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.NUMERIC);
        } else {
            ps.setBigDecimal(i, new BigDecimal(it));
        }
    }
    public static void setBigDecimal(PreparedStatement ps, int i, BigDecimal it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.NUMERIC);
        } else {
            ps.setBigDecimal(i, it);
        }
    }
    public static void setBytes(PreparedStatement ps, int i, byte[] it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.BINARY);
        } else {
            ps.setBytes(i, it);
        }
    }
    public static void setNChar(PreparedStatement ps, int i, String it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.NCHAR);
        } else {
            ps.setNString(i, it);
        }
    }
    public static void setNVarChar(PreparedStatement ps, int i, String it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.NVARCHAR);
        } else {
            ps.setNString(i, it);
        }
    }
    public static void setLongVarBinary(PreparedStatement ps, int i, byte[] it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.LONGVARBINARY);
        } else {
            ps.setBytes(i, it);
        }
    }
    public static void setBinaryStream(PreparedStatement ps, int i, InputStream it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.LONGVARBINARY);
        } else {
            ps.setBinaryStream(i, it);
        }
    }
    public static void setLongVarChar(PreparedStatement ps, int i, String it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.LONGVARCHAR);
        } else {
            ps.setString(i, it);
        }
    }
    public static void setCharacterStream(PreparedStatement ps, int i, Reader it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.LONGVARCHAR);
        } else {
            ps.setCharacterStream(i, it);
        }
    }
    public static void setLongNVarChar(PreparedStatement ps, int i, String it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.LONGNVARCHAR);
        } else {
            ps.setNString(i, it);
        }
    }
    public static void setNCharacterStream(PreparedStatement ps, int i, Reader it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.LONGNVARCHAR);
        } else {
            ps.setNCharacterStream(i, it);
        }
    }
    public static void setBlob(PreparedStatement ps, int i, Blob it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.BLOB);
        } else {
            ps.setBlob(i, it);
        }
    }
    public static void setBlobStream(PreparedStatement ps, int i, InputStream it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.BLOB);
        } else {
            ps.setBlob(i, it);
        }
    }
    public static void setClob(PreparedStatement ps, int i, Clob it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.CLOB);
        } else {
            ps.setClob(i, it);
        }
    }
    public static void setClobStream(PreparedStatement ps, int i, Reader it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.CLOB);
        } else {
            ps.setClob(i, it);
        }
    }
    public static void setNClob(PreparedStatement ps, int i, NClob it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.NCLOB);
        } else {
            ps.setNClob(i, it);
        }
    }
    public static void setNClobStream(PreparedStatement ps, int i, Reader it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.NCLOB);
        } else {
            ps.setNClob(i, it);
        }
    }
    public static void setRef(PreparedStatement ps, int i, Ref it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.REF);
        } else {
            ps.setRef(i, it);
        }
    }
    public static void setRowId(PreparedStatement ps, int i, RowId it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.ROWID);
        } else {
            ps.setRowId(i, it);
        }
    }
    public static void setSQLXML(PreparedStatement ps, int i, SQLXML it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.SQLXML);
        } else {
            ps.setSQLXML(i, it);
        }
    }
    public static void setURL(PreparedStatement ps, int i, URL it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.DATALINK);
        } else {
            ps.setURL(i, it);
        }
    }
    public static void setArray(PreparedStatement ps, int i, Array it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.ARRAY);
        } else {
            ps.setArray(i, it);
        }
    }
    //_____________________________________________________________
    //
    public static BigInteger toBigInteger(BigDecimal it) {
        if (it==null) {
            return null;
        } else {
            return it.toBigInteger();
        }
    }
    //_____________________________________________________________
    //
    public static Boolean getBoolean(CallableStatement cs, int i) throws SQLException {
        boolean it=cs.getBoolean(i);
        return cs.wasNull() ? null : it;
    }
    public static Boolean getNumBool(CallableStatement cs, int i) throws SQLException {
        byte it=cs.getByte(i);
        return cs.wasNull() ? null : (it==1);
    }
    public static Byte getByte(CallableStatement cs, int i) throws SQLException {
        byte it=cs.getByte(i);
        return cs.wasNull() ? null : it;
    }
    public static Short getShort(CallableStatement cs, int i) throws SQLException {
        short it=cs.getShort(i);
        return cs.wasNull() ? null : it;
    }
    public static Integer getInt(CallableStatement cs, int i) throws SQLException {
        int it=cs.getInt(i);
        return cs.wasNull() ? null : it;
    }
    public static Long getLong(CallableStatement cs, int i) throws SQLException {
        long it=cs.getLong(i);
        return cs.wasNull() ? null : it;
    }
    public static Double getDouble(CallableStatement cs, int i) throws SQLException {
        double it=cs.getDouble(i);
        return cs.wasNull() ? null : it;
    }
    public static Float getFloat(CallableStatement cs, int i) throws SQLException {
        float it=cs.getFloat(i);
        return cs.wasNull() ? null : it;
    }

    public static String buildQuery(String ante, Map<String, String> optMap, String post, Set<String> options) {
        StringBuilder sb = new StringBuilder();
        sb.append(ante);
        final boolean where = (ante.toUpperCase().contains("WHERE "));
        int c = 0;
        for(Map.Entry<String, String> e: optMap.entrySet()) {
            if (options.contains(e.getKey())) {
                if (c++==0 && !where) {
                    sb.append(" WHERE ");
                } else {
                    sb.append(" AND ");
                }
                sb.append(e.getValue());
            }
        }
        sb.append(" ").append(post);
        return sb.toString();
    }
}
