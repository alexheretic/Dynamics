package alexh;

import alexh.weak.Dynamic;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

public class DynamicSomethingTest {

    private Dynamic dy;

    @Before
    public void setupMap() {
        dy = Dynamic.from("something");
        assertNotNull(dy);
    }

    @Test
    public void isItself() {
        assertEquals("something", dy.asObject());
    }

    @Test
    public void noChildren() {
        assertFalse(dy.get("anything").isPresent());
        assertFalse(dy.get(1).isPresent());
    }

    @Test
    public void equalsImplementation() {
        assertEquals(Dynamic.from("a string"), Dynamic.from("a string"));
    }

    @Test
    public void hashCodeImplementation() {
        assertEquals(Dynamic.from("a string").hashCode(), Dynamic.from("a string").hashCode());
    }

    @Test
    public void toStringImplementation() {
        Dynamic obj = Dynamic.from(new Object());
        assertThat(obj.toString().toLowerCase(), allOf(containsString("root"), containsString("object")));
        System.out.println("object dynamic toString: "+ obj);
    }

    @Test
    public void childEqualsImplementation() {
        assertEquals(Dynamic.from("a string").get(123), Dynamic.from("a string").get(123));
    }

    @Test
    public void childHashCodeImplementation() {
        assertEquals(Dynamic.from("a string").get(123).hashCode(), Dynamic.from("a string").get(123).hashCode());
    }

    @Test
    public void childToStringImplementation() {
        Dynamic obj = Dynamic.from(new Object()).get(123);
        assertThat(obj.toString().toLowerCase(), allOf(containsString("root"), containsString("123")));
        System.out.println("object-child dynamic toString: "+ obj);
    }
}
