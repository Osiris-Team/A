package com.osiris.a.c;

public enum CTypes {
    _int("int*"),
    code(null); // No direct representation in C

    private String type;

    CTypes(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
