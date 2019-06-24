package com.luo.log.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <P>
 *     反射工具
 * </P>
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/8/25.17:09
 * @see
 */
public class ReflectUtil {

    /**
     * 获取对象及对象除{@link Object}外所有父类的{@link Field}
     * @param type
     * @return
     */
    public static List<Field> getFields(Class<?> type) {
        List<Field> fieldList = new ArrayList<>();
        do {
            fieldList.addAll(Arrays.asList(type.getDeclaredFields()));
        } while ((type = type.getSuperclass()) != Object.class);
        return fieldList;
    }

    /**
     * 获取指定fieldName和指定class的{@link Field}对象
     * @param filedName
     * @param type
     * @return
     */
    public static Field getField(String filedName, Class<?> type) {
        Field field = null;
        do {
            try {
                field = type.getDeclaredField(filedName);
            } catch (NoSuchFieldException e) {
            }
        } while (((type = type.getSuperclass()) != Object.class) && field == null);
        return field;

    }

    /**
     *
     * @param field
     * @param srcs
     * @return
     * @see #getFieldValues(String, Object...)
     */
    public static List<Object> getFieldValues(Field field, Object... srcs) {
        return getFieldValues(field.getName(), srcs);
    }

    /**
     * 获取对象数组的对象指定{@link Field}的值的列表
     * @param fieldName
     * @param srcs
     * @return
     */
    public static List<Object> getFieldValues(String fieldName, Object... srcs) {
        List<Object> fieldValueList = new ArrayList<>();
        for (Object src : srcs)
            fieldValueList.add(getFieldValue(fieldName, src));
        return fieldValueList;
    }

    /**
     * 获取{@link Field}对应的值，需要依赖getter方法
     * @param field
     * @param src
     * @return
     * @see #getFieldValue(String, Object)
     */
    public static Object getFieldValue(Field field, Object src) {
        return getFieldValue(field.getName(), src);
    }

    /**
     * 获取{@link Field}对应的值，需要依赖getter方法
     * @param fieldName
     * @param src
     * @return
     */
    public static Object getFieldValue(String fieldName, Object src) {
        try {
            Method getter = getter(fieldName, src.getClass());
            if (getter != null) {
                return getter.invoke(src);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param field
     * @param src
     * @param param
     * @see #setFieldValue(String, Object, Object)
     */
    public static void setFieldValue(Field field, Object src, Object param) {
        setFieldValue(field.getName(), src, param);
    }

    /**
     * 不需要setter和getter，直接赋值，特殊情况
     * @param field
     * @param src
     * @param param
     */
    public static void setFieldValueF(Field field, Object src, Object param) {
        try {
            field.setAccessible(true);
            field.set(src, param);
            field.setAccessible(false);
        } catch (Exception e) {
        }
    }

    /**
     * 给指定的{@link Field}赋值
     * @param fieldName
     * @param src
     * @param param
     */
    public static void setFieldValue(String fieldName, Object src, Object param) {
        try {
            Method setter = setter(fieldName, src.getClass());
            if (setter != null) setter.invoke(src, param);
        } catch (Exception e) {
        }
    }

    /**
     * 根据{@link Field}的名称获取对应的setter方法
     * @param fieldName
     * @param clazz
     * @return
     */
    public static Method setter(String fieldName, Class clazz) {
        try {
            Field field = getField(fieldName, clazz);
            return setter(field, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static Method setter(Field field, Class clazz) {
        try {
            String methodName = newStr("set", field.getName());
            return clazz.getMethod(methodName, field.getType());
        } catch (NoSuchMethodException | NullPointerException e) {
            return null;
        }
    }

    /**
     * 根据{@link Field}的名称获取对应的getter方法
     * @param fieldName
     * @param clazz
     * @return
     */
    public static Method getter(String fieldName, Class clazz) {
        try {
            String methodName = newStr("get", fieldName);
            return clazz.getMethod(methodName);
        } catch (NoSuchMethodException | NullPointerException e) {
            return null;
        }
    }

    public static String newStr(String begin, String src) {
        return begin + src.substring(0, 1).toUpperCase() + src.substring(1);
    }
}
