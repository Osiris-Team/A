package com.osiris.a;

import com.osiris.a.c.C;
import com.osiris.a.c.CTypes;
import com.osiris.a.c.CVar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CTest {
    C c = new C();

    @Test
    void startFunction() throws Exception {
        String expected = "void test(int* a, int* b){}";
        String actual = c.startFunction(null, "test", new CVar("a", CTypes._int), new CVar("b", CTypes._int))
                + c.endFunction(null);
        assertEquals(expected, actual);
        expected = "int* test(int* a, int* b){return a;}";
        actual = c.startFunction(CTypes._int, "test", new CVar("a", CTypes._int), new CVar("b", CTypes._int))
                + c.endFunction(new CVar("a", CTypes._int));
        assertEquals(expected, actual);
    }

    @Test
    void endFunction() {
        assertEquals("}", c.endFunction(null));
        assertEquals("return a;}", c.endFunction(new CVar("a", CTypes._int)));
    }

    @Test
    void defineVariable() {
        assertEquals("int* a;", c.defineVariable(new CVar("a", CTypes._int)));
    }

    @Test
    void setVariable() {
        assertEquals("*a = *b;", c.setVariable(new CVar("a", CTypes._int), new CVar("b", CTypes._int)));
    }

    @Test
    void setVariable2() {
        assertEquals("*a = 10;", c.setVariable(new CVar("a", CTypes._int), "10"));
    }

    @Test
    void pretty() {
        String expected = "void main(){\n" +
                "int a = 10;\n" +
                "}\n";
        assertEquals(expected, c.pretty("void main(){int a = 10;}"));
    }
}