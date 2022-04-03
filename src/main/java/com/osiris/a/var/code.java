package com.osiris.a.var;

import com.osiris.a.c.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * A code block or scope.
 */
public class code extends obj {
    public Types returnType;
    public code parentCode;
    public List<obj> parameters;
    public List<obj> variables = new ArrayList<>();

    public code(code parentCode, String name, List<obj> parameters) {
        super(name, Types.code);
        this.parentCode = parentCode;
        this.parameters = parameters;
    }
}
