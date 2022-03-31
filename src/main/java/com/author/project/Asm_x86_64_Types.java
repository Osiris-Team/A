package com.author.project;

public enum Asm_x86_64_Types {
    _int("int");

    public String assembly;

    /**
     * @param assembly the actual type name used in x86_64 assembly code.
     */
    Asm_x86_64_Types(String assembly) {
        this.assembly = assembly;
    }
}
