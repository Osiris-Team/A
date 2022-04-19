package com.osiris.a;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    Compiler converter = new Compiler();

    private static void throwsException(Executable executable, String expectedMessage) throws Throwable {
        boolean thrown = false;
        try {
            executable.execute();
        } catch (Throwable throwable) {
            thrown = true;
            if (expectedMessage != null) {
                if (throwable.getMessage() == null) {
                    System.err.println("Exception was thrown but message is null even though expected: " + expectedMessage);
                    throw throwable;
                }
                if (!throwable.getMessage().contains(expectedMessage)) {
                    System.err.println("Exception was thrown but message (" + throwable.getMessage() + ") does not contain expected: " + expectedMessage);
                    throw throwable;
                }
            }
        }
        if (!thrown) throw new Exception("Exception was expected but not thrown!");
    }

    @Test
    void comments() throws IOException {
        assertEquals("", converter.parseString("//comment").cCode);
        assertEquals("", converter.parseString("// comment").cCode);
        assertEquals("", converter.parseString("//              comment").cCode);
        assertEquals("", converter.parseString("                     //comment").cCode);
        assertEquals("", converter.parseString("                     //              comment").cCode);
    }

    @Test
    void filePath() throws Throwable {
        converter.parseString("/file/path");
        throwsException(() -> {
            converter.parseString("file/path");
        }, "File path must start with '/'");
    }

    @Test
    void semicolonAndNextLine() throws IOException {
        String actual;
        actual = converter.parseString("int a; int b; a = b;").cCode;
        assertEquals("int* a;int* b;*a = *b;", actual);
        actual = converter.parseString("int a=10; int b=5; a = b;").cCode;
        assertEquals("int* a;*a=10;int* b;*b=5;*a = *b;", actual);
        actual = converter.parseString("int a\n int b\n a = b\n").cCode;
        assertEquals("int* a;int* b;*a = *b;", actual);
        actual = converter.parseString("int a=10\n int b=5\n a = b\n").cCode;
        assertEquals("int* a;*a=10;int* b;*b=5;*a = *b;", actual);
    }

    @Test
    void variables() throws Throwable {
        String actual = converter.parseString("int a = 10;").cCode;
        assertEquals("int* a;*a=10;", actual);
        actual = converter.parseString("int a;").cCode;
        assertEquals("int* a;", actual);

        actual = converter.parseString("int a = 10").cCode;
        assertEquals("int* a;*a=10;", actual);
        actual = converter.parseString("int a").cCode;
        assertEquals("int* a;", actual);

        actual = converter.parseString("int a; int b; a = b;").cCode;
        assertEquals("int* a;int* b;*a = *b;", actual);
        actual = converter.parseString("int a=10; int b=5; a = b;").cCode;
        assertEquals("int* a;*a=10;int* b;*b=5;*a = *b;", actual);
        actual = converter.parseString("int a\n int b\n a = b\n").cCode;
        assertEquals("int* a;int* b;*a = *b;", actual);
        actual = converter.parseString("int a=10\n int b=5\n a = b\n").cCode;
        assertEquals("int* a;*a=10;int* b;*b=5;*a = *b;", actual);

        throwsException(() -> converter.parseString("int a a;"), "Variable name cannot contain spaces.");
        throwsException(() -> converter.parseString("int a a"), "Variable name cannot contain spaces.");
        throwsException(() -> converter.parseString("int a = 1 0;"), "Variable value cannot contain spaces.");
        throwsException(() -> converter.parseString("int a = 1 0"), "Variable value cannot contain spaces.");
        throwsException(() -> converter.parseString("a = 10;"), "No declaration of variable");
        throwsException(() -> converter.parseString("a = 10"), "No declaration of variable");
        throwsException(() -> converter.parseString("int a = ;"), "Usage of = even though no value is being assigned.");
        throwsException(() -> converter.parseString("int a ="), "Usage of = even though no value is being assigned.");
        throwsException(() -> converter.parseString("a;"), "Not a statement.");
        throwsException(() -> converter.parseString("a"), "Not a statement.");
        throwsException(() -> converter.parseString("au8asß dßßa8 dßz89231ß9husa sd8791;"), "Not a statement.");
        throwsException(() -> converter.parseString("au8asß dßßa8 dßz89231ß9husa sd8791"), "Not a statement.");
        throwsException(() -> converter.parseString("int a = 1000; byte b = a;"), "Wrong value type.");
        throwsException(() -> converter.parseString("byte a = 1000;"), "Wrong value format.");
        throwsException(() -> converter.parseString("byte a = abcdef1000;"), "Wrong value format.");
    }

    @Test
    void variableAttributes() throws Throwable {
        // final
        throwsException(() -> converter.parseString("int final a = 10; a = 20;"), "Cannot change final variable");
        throwsException(() -> converter.parseString("int final a = 10; a = 20;"), "Cannot change final variable");
        converter.parseString("int final a; a = 10");
        throwsException(() -> converter.parseString("int final a; a = 10; a = 20;"), "Cannot change final variable");
    }

    @Test
    void scopes() {

    }

    @Test
    void genFunctionName() {
        String result = converter.genFunctionName("myFunction",
                new File("C:\\User\\project\\lib\\MyObject"),
                new File("C:\\User\\project"));
        assertEquals("_lib_MyObject_myFunction", result);
        result = converter.genFunctionName("myFunction",
                new File("C:/User/project/lib/MyObject"),
                new File("C:/User/project"));
        assertEquals("_lib_MyObject_myFunction", result);
        result = converter.genFunctionName("myFunction",
                new File("C:/User/project/MyObject"),
                new File("C:/User/project"));
        assertEquals("_MyObject_myFunction", result);
    }
}