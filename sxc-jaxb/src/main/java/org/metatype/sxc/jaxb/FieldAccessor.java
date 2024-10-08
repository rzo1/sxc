/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.metatype.sxc.jaxb;

import jakarta.xml.bind.JAXBException;
import org.metatype.sxc.util.XoXMLStreamReader;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class FieldAccessor<BeanType, FieldType> {
    private static final Unsafe unsafe;
    static {
        Unsafe theUnsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            theUnsafe = (Unsafe) field.get(null);
        } catch (Throwable ignored) {
        }
        unsafe = theUnsafe;
    }

    public final Field field;
    public final long offset;

    public FieldAccessor(Field field) {
        if (field == null) throw new NullPointerException("field is null");
        this.field = field;
        if (unsafe != null) {
            offset = unsafe.objectFieldOffset(field);
        } else {
            offset = -1;
        }
    }

    public FieldAccessor(Class<BeanType> clazz, String fieldName) {
        if (clazz == null) throw new NullPointerException("clazz is null");
        if (fieldName == null) throw new NullPointerException("fieldName is null");

        try {
            this.field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }

        if (unsafe != null) {
            offset = unsafe.objectFieldOffset(field);
        } else {
            offset = -1;
            try {
                field.setAccessible(true);
            } catch (SecurityException e) {
                throw new IllegalStateException("Unable to access non-public fields");
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public FieldType getObject(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return reflectionGet(location, context, instance);
        }
        return (FieldType) unsafe.getObject(instance, offset);
    }

    public void setObject(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, FieldType value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putObject(instance, offset, value);
    }

    public boolean getBoolean(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Boolean) reflectionGet(location, context, instance);
        }
        return unsafe.getBoolean(instance, offset);
    }

    public void setBoolean(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, boolean value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putBoolean(instance, offset, value);
    }

    public byte getByte(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Byte) reflectionGet(location, context, instance);
        }
        return unsafe.getByte(instance, offset);
    }

    public void setByte(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, byte value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putByte(instance, offset, value);
    }

    public char getChar(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Character) reflectionGet(location, context, instance);
        }
        return unsafe.getChar(instance, offset);
    }

    public void setChar(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, char value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putChar(instance, offset, value);
    }

    public short getShort(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Short) reflectionGet(location, context, instance);
        }
        return unsafe.getShort(instance, offset);
    }

    public void setShort(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, short value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putShort(instance, offset, value);
    }

    public int getInt(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Integer) reflectionGet(location, context, instance);
        }
        return unsafe.getInt(instance, offset);
    }

    public void setInt(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, int value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putInt(instance, offset, value);
    }

    public long getLong(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Long) reflectionGet(location, context, instance);
        }
        return unsafe.getLong(instance, offset);
    }

    public void setLong(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, long value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putLong(instance, offset, value);
    }

    public float getFloat(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Float) reflectionGet(location, context, instance);
        }
        return unsafe.getFloat(instance, offset);
    }

    public void setFloat(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, float value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putFloat(instance, offset, value);
    }

    public double getDouble(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        if (unsafe == null) {
            return (Double) reflectionGet(location, context, instance);
        }
        return unsafe.getDouble(instance, offset);
    }

    public void setDouble(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, double value) throws JAXBException {
        if (unsafe == null) {
            reflectionSet(reader, context, instance, value);
            return;
        }
        unsafe.putDouble(instance, offset, value);
    }

    @SuppressWarnings({"unchecked"})
    private FieldType reflectionGet(Object location, RuntimeContext context, BeanType instance) throws JAXBException {
        try {
            return (FieldType) field.get(instance);
        } catch (IllegalAccessException e) {
            if (location instanceof XoXMLStreamReader) {
                XoXMLStreamReader reader = (XoXMLStreamReader) location;
                context.fieldGetError(reader, field.getDeclaringClass(), field.getName(), e);
            } else {
                context.fieldGetError(location, field.getName(), field.getDeclaringClass(), field.getName(), e);
            }
            if (field.getType().isPrimitive()) {
                Class<?> fieldType = field.getType();
                if (Boolean.TYPE.equals(fieldType)) {
                    return (FieldType) Boolean.FALSE;
                } else if (Byte.TYPE.equals(fieldType)) {
                    return (FieldType) new Byte((byte) 0);
                } else if (Character.TYPE.equals(fieldType)) {
                    return (FieldType) new Character((char) 0);
                } else if (Short.TYPE.equals(fieldType)) {
                    return (FieldType) new Short((short) 0);
                } else if (Integer.TYPE.equals(fieldType)) {
                    return (FieldType) new Integer(0);
                } else if (Long.TYPE.equals(fieldType)) {
                    return (FieldType) new Long(0);
                } else if (Float.TYPE.equals(fieldType)) {
                    return (FieldType) new Float(0);
                } else if (Double.TYPE.equals(fieldType)) {
                    return (FieldType) new Double(0);
                }
            }
            return null;
        }
    }

    private void reflectionSet(XoXMLStreamReader reader, RuntimeContext context, BeanType instance, Object value) throws JAXBException {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            if (context == null) throw new JAXBException(e);
            context.fieldSetError(reader, field.getDeclaringClass(), field.getName(), e);
        }
    }
}
