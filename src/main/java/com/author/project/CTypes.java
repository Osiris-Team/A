package com.author.project;

public enum CTypes {
    _int("int*")
    ;

    private String type;

    CTypes(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
