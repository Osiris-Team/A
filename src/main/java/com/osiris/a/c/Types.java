package com.osiris.a.c;

import java.util.Objects;

public enum Types {
    _byte("byte", "char*"), // TODO value need to look like this in C: 0b11111111
    _short("short", "short*"),
    _int("int", "int*"),
    _long("long", "long long*"), // Use long long to make sure that its 64bit on Windows too.
    _custom(null, null),
    code("code", null); // No direct representation in C

    public String inA;
    public String inC;

    Types(String inA, String inC) {
        this.inA = inA;
        this.inC = inC;
    }

    public static Types custom(String inA, String inC) {
        Types t = Types._custom;
        t.inA = inA;
        t.inC = inC;
        return t;
    }

    @Override
    public String toString() {
        return inC;
    }
}
