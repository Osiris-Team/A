package com.osiris.a.var;

import com.osiris.a.c.Types;

public class obj {
    public String name;
    public Types type;
    public String value;
    public boolean isFinal;

    public obj() {
    }

    public obj(String name, Types type) {
        this.name = name;
        this.type = type;
    }

    public obj(String name, Types type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
