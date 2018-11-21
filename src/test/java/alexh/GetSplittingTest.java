package alexh;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import alexh.weak.Dynamic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetSplittingTest {

    private Dynamic dy;

    @BeforeEach
    public void setupMap() {
        dy = Dynamic.from(new Fluent.HashMap<>()
            .append("key1", new Fluent.HashMap<>()
                .append("key2", "hello")
                .append("key3", new Fluent.HashMap<>()
                        .append("key4", 123)
                        .append("key6", null)
                        .append("key7", new Fluent.HashMap<>()
                            .append("hello", "world"))
                )
            )
            .append("key5", asList(1, 2, 3, new Fluent.HashMap<>()
                .append(55, "blah")))
            .append("key8", emptyList())
            .append("key9", asList("hello", null, null, null)));
        assertNotNull(dy);
    }

    @Test
    public void stringPath() {
        assertEquals("hello", dy.get("key1:key2", ":").asObject());
    }

    @Test
    public void mixedPath() {
        assertEquals("blah", dy.get("key5~3~55", "~").asObject());
    }

    @Test
    public void mixedPath_nonChar() {
        assertEquals("blah", dy.get("key5--->3--->55", "--->").asObject());
    }

    @Test
    public void asciiCharacter() {
        assertEquals("blah", dy.get("key5⇀3⇀55", "⇀").asObject());
    }

    @Test
    public void separateCallsSplitDifferently() {
        assertEquals("world", dy.get("key1.key3", ".").get("key7<>hello", "<>").asObject());
    }

    @Test
    public void absentPath() {
        assertFalse(dy.get("foo.bar.blah.blah", ".").isPresent(), "Unexpected: " + dy.get("foo.bar.blah.blah", "."));
    }

    @Test
    public void unusedSplitter_absent() {
        assertFalse(dy.get("foo", "~").isPresent());
    }

    @Test
    public void unusedSplitter_present() {
        assertTrue(dy.get("key5", ".").isPresent());
    }

    @Test
    public void dgetConvenienceMethod() {
        assertThat(dy.dget("key5.3.55").asObject()).isEqualTo("blah");
    }
}
