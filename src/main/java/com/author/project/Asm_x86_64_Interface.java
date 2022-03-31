package com.author.project;

public interface Asm_x86_64_Interface {

    /**
     * Creates function declaration assembly code.
     * @param name the functions' name.
     * @param types the functions' parameter types.
     */
    String startFunction(String name, Asm_x86_64_Types... types) throws Exception;

}
