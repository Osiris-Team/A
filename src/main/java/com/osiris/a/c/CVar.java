package com.osiris.a.c;

public class CVar {
    public String name;
    public CTypes type;
    public String value;

    public CVar(String name, CTypes type) {
        this.name = name;
        this.type = type;
    }

    public CVar(String name, CTypes type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
