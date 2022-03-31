package com.author.project;

import java.util.HashMap;
import java.util.Map;

public class Asm_x86_64 implements Asm_x86_64_Interface{

    private static String[] paramPointers = {
            "QWORD PTR [rbp-8]",  "QWORD PTR [rbp-16]", "QWORD PTR [rbp-24]", "QWORD PTR [rbp-32]", "QWORD PTR [rbp-40]", "QWORD PTR [rbp-48]"
    };
    private static String[] paramRegisters = {
            "rdi", "rsi", "rdx", "rcx", "r8", "r9"
    };

    @Override
    public String startFunction(String name, Asm_x86_64_Types... types) throws Exception {
        String params = "";
        for (int i = 0; i < types.length; i++) {
            params  += types[i].assembly;
            if(types.length-1 != i){
                params += ", ";
            }
        }

        if(types.length > paramPointers.length)
            throw new Exception("Too many function parameters ("+params.length()+"). Maximum allowed: "+paramPointers.length+".");

        String paramsMov = "";
        for (int i = 0; i < types.length; i++) {
            paramsMov  += "        mov     "+paramPointers[i]+", "+paramRegisters[i]+"\n";
        }
        return name+"("+params+"):\n"+
                "        push    rbp\n" +
                "        mov     rbp, rsp\n" +
                paramsMov;
    }
}
