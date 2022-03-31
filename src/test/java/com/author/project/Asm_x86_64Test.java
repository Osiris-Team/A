package com.author.project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Asm_x86_64Test {
    Asm_x86_64 asm = new Asm_x86_64();

    @Test
    void startFunction() throws Exception {
        String expected = "test(int, int):\n" +
                "        push    rbp\n" +
                "        mov     rbp, rsp\n" +
                "        mov     QWORD PTR [rbp-8], rdi\n" +
                "        mov     QWORD PTR [rbp-16], rsi\n";
        String actual = asm.startFunction("test", Asm_x86_64_Types._int, Asm_x86_64_Types._int);
        assertEquals(expected, actual);
        System.out.println(actual);
    }
}