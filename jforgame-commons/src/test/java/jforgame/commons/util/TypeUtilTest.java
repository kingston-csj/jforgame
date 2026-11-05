package jforgame.commons.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link TypeUtil}
 */
public class TypeUtilTest {

    // --- isPrimitiveOrString tests ---

    @Test
    public void testIsPrimitiveOrString() {
        assertTrue(TypeUtil.isPrimitiveOrString(int.class));
        assertTrue(TypeUtil.isPrimitiveOrString(long.class));
        assertTrue(TypeUtil.isPrimitiveOrString(double.class));
        assertTrue(TypeUtil.isPrimitiveOrString(float.class));
        assertTrue(TypeUtil.isPrimitiveOrString(boolean.class));
        assertTrue(TypeUtil.isPrimitiveOrString(byte.class));
        assertTrue(TypeUtil.isPrimitiveOrString(short.class));
        assertTrue(TypeUtil.isPrimitiveOrString(char.class));
    }

    @Test
    public void testIsPrimitiveOrStringWrappers() {
        assertTrue(TypeUtil.isPrimitiveOrString(Integer.class));
        assertTrue(TypeUtil.isPrimitiveOrString(Long.class));
        assertTrue(TypeUtil.isPrimitiveOrString(Double.class));
        assertTrue(TypeUtil.isPrimitiveOrString(Boolean.class));
        assertTrue(TypeUtil.isPrimitiveOrString(Byte.class));
        assertTrue(TypeUtil.isPrimitiveOrString(Short.class));
        assertTrue(TypeUtil.isPrimitiveOrString(Character.class));
        assertTrue(TypeUtil.isPrimitiveOrString(Float.class));
    }

    @Test
    public void testIsPrimitiveOrStringString() {
        assertTrue(TypeUtil.isPrimitiveOrString(String.class));
    }

    @Test
    public void testIsPrimitiveOrStringNull() {
        assertFalse(TypeUtil.isPrimitiveOrString(null));
    }

    @Test
    public void testIsPrimitiveOrStringObject() {
        assertFalse(TypeUtil.isPrimitiveOrString(Object.class));
        assertFalse(TypeUtil.isPrimitiveOrString(java.util.ArrayList.class));
    }

    // --- isCompatibleType tests ---

    @Test
    public void testIsCompatibleTypeNullValue() {
        assertTrue(TypeUtil.isCompatibleType(null, String.class));
    }

    @Test
    public void testIsCompatibleTypeSameType() {
        assertTrue(TypeUtil.isCompatibleType(42, Integer.class));
        assertTrue(TypeUtil.isCompatibleType("hello", String.class));
        assertTrue(TypeUtil.isCompatibleType(3.14, Double.class));
    }

    @Test
    public void testIsCompatibleTypePrimitive() {
        assertTrue(TypeUtil.isCompatibleType(42, int.class));
        assertTrue(TypeUtil.isCompatibleType(42L, long.class));
        assertTrue(TypeUtil.isCompatibleType(3.14, double.class));
    }

    @Test
    public void testIsCompatibleTypeParentClass() {
        assertTrue(TypeUtil.isCompatibleType("hello", CharSequence.class));
    }

    @Test
    public void testIsCompatibleTypeIncompatible() {
        assertFalse(TypeUtil.isCompatibleType("hello", Integer.class));
        assertFalse(TypeUtil.isCompatibleType(42, String.class));
    }

    // --- getBoxedType tests ---

    @Test
    public void testGetBoxedType() {
        assertEquals(Integer.class, TypeUtil.getBoxedType(int.class));
        assertEquals(Long.class, TypeUtil.getBoxedType(long.class));
        assertEquals(Double.class, TypeUtil.getBoxedType(double.class));
        assertEquals(Float.class, TypeUtil.getBoxedType(float.class));
        assertEquals(Boolean.class, TypeUtil.getBoxedType(boolean.class));
        assertEquals(Character.class, TypeUtil.getBoxedType(char.class));
        assertEquals(Short.class, TypeUtil.getBoxedType(short.class));
        assertEquals(Byte.class, TypeUtil.getBoxedType(byte.class));
    }

    @Test
    public void testGetBoxedTypeNonPrimitive() {
        // Returns the input if not a primitive
        assertEquals(String.class, TypeUtil.getBoxedType(String.class));
        assertEquals(Object.class, TypeUtil.getBoxedType(Object.class));
    }
}
