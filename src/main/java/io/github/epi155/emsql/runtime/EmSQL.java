package io.github.epi155.emsql.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EmSQL {
    private EmSQL() {}

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

    public static Boolean box(boolean v, boolean isNull) {
        return isNull ? null : v;
    }
    public static Short box(short v, boolean isNull) {
        return isNull ? null : v;
    }
    public static Integer box(int v, boolean isNull) {
        return isNull ? null : v;
    }
    public static Long box(long v, boolean isNull) {
        return isNull ? null : v;
    }
    public static Double box(double v, boolean isNull) {
        return isNull ? null : v;
    }
    public static Float box(float v, boolean isNull) {
        return isNull ? null : v;
    }
    public static LocalDate toLocalDate(Date d) {
        return d==null ? null : d.toLocalDate();
    }
    public static LocalDateTime toLocalDateTime(Timestamp d) {
        return d==null ? null : d.toLocalDateTime();
    }
    public static LocalTime toLocalTime(Time d) {
        return d==null ? null : d.toLocalTime();
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
//        return claz.cast(passesThrough(target, pieces, IS_GET, is));
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

}
