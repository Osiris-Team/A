package com.osiris.a;

import com.osiris.a.c.C;
import com.osiris.a.c.Types;
import com.osiris.a.var.obj;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CTest {
    C c = new C();

    @Test
    void startFunction() throws Exception {
        String expected = "void test(int* a, int* b){}";
        String actual = c.startFunction(null, "test", new obj("a", Types._int), new obj("b", Types._int))
                + c.endFunction(null);
        assertEquals(expected, actual);
        expected = "int* test(int* a, int* b){return a;}";
        actual = c.startFunction(Types._int, "test", new obj("a", Types._int), new obj("b", Types._int))
                + c.endFunction(new obj("a", Types._int));
        assertEquals(expected, actual);
    }

    @Test
    void endFunction() {
        assertEquals("}", c.endFunction(null));
        assertEquals("return a;}", c.endFunction(new obj("a", Types._int)));
    }

    @Test
    void defineVariable() {
        assertEquals("int* a;", c.defineVariable(new obj("a", Types._int)));
    }

    @Test
    void setVariable() {
        assertEquals("*a = *b;", c.setVariable(new obj("a", Types._int), new obj("b", Types._int)));
    }

    @Test
    void setVariable2() {
        assertEquals("*a = 10;", c.setVariable(new obj("a", Types._int), "10"));
    }

    @Test
    void pretty() {
        String expected = "void main(){\n" +
                "int a = 10;\n" +
                "}\n";
        assertEquals(expected, c.pretty("void main(){int a = 10;}"));
    }
}