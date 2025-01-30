package agh.ics.oop;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Vector2dTest {

    @Test
    void testConstructor() {
        Vector2d vector = new Vector2d(3, 5);
        assertEquals(3, vector.getX());
        assertEquals(5, vector.getY());
    }

    @Test
    void testToString() {
        Vector2d vector = new Vector2d(3, 5);
        assertEquals("(3,5)", vector.toString());
    }

    @Test
    void testPrecedes() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(4, 6);
        Vector2d vector3 = new Vector2d(2, 4);
        Vector2d vector4 = new Vector2d(3, 5);

        assertTrue(vector1.precedes(vector2));
        assertFalse(vector2.precedes(vector1));
        assertTrue(vector3.precedes(vector1));
        assertTrue(vector1.precedes(vector4));
    }

    @Test
    void testFollows() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(4, 6);
        Vector2d vector3 = new Vector2d(2, 4);
        Vector2d vector4 = new Vector2d(3, 5);

        assertTrue(vector2.follows(vector1));
        assertFalse(vector1.follows(vector2));
        assertTrue(vector1.follows(vector3));
        assertTrue(vector1.follows(vector4));
    }

    @Test
    void testAdd() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(2, 4);
        Vector2d result = vector1.add(vector2);

        assertEquals(5, result.getX());
        assertEquals(9, result.getY());
    }

    @Test
    void testSubtract() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(2, 4);
        Vector2d result = vector1.subtract(vector2);

        assertEquals(1, result.getX());
        assertEquals(1, result.getY());
    }

    @Test
    void testUpperRight() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(2, 6);
        Vector2d result = vector1.upperRight(vector2);

        assertEquals(3, result.getX());
        assertEquals(6, result.getY());
    }

    @Test
    void testLowerLeft() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(2, 6);
        Vector2d result = vector1.lowerLeft(vector2);

        assertEquals(2, result.getX());
        assertEquals(5, result.getY());
    }

    @Test
    void testOpposite() {
        Vector2d vector = new Vector2d(3, 5);
        Vector2d result = vector.opposite();

        assertEquals(-3, result.getX());
        assertEquals(-5, result.getY());
    }

    @Test
    void testEquals() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(3, 5);
        Vector2d vector3 = new Vector2d(4, 6);

        assertTrue(vector1.equals(vector2));
        assertFalse(vector1.equals(vector3));
    }

    @Test
    void testHashCode() {
        Vector2d vector1 = new Vector2d(3, 5);
        Vector2d vector2 = new Vector2d(3, 5);
        Vector2d vector3 = new Vector2d(4, 6);

        assertEquals(vector1.hashCode(), vector2.hashCode());
        assertNotEquals(vector1.hashCode(), vector3.hashCode());
    }

    @Test
    void testEqualityWithDifferentObjectType() {
        Vector2d vector = new Vector2d(3, 5);
        String str = "Not a Vector2d";

        assertFalse(vector.equals(str));
    }

    @Test
    void testZeroVector() {
        Vector2d vector1 = new Vector2d(0, 0);
        Vector2d vector2 = new Vector2d(0, 0);

        assertTrue(vector1.equals(vector2));
    }

    @Test
    void testNegativeCoordinates() {
        Vector2d vector1 = new Vector2d(-3, -5);
        Vector2d vector2 = new Vector2d(-2, -4);
        Vector2d result = vector1.add(vector2);

        assertEquals(-5, result.getX());
        assertEquals(-9, result.getY());
    }
}