package com.osiris.a.c;

public class CVar {
    public String name;
    public Types type;
    public String value;

    public CVar(String name, Types type) {
        this.name = name;
        this.type = type;
    }

    public CVar(String name, Types type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
