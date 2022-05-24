package com.osiris.a.c;

import com.osiris.a.var.obj;

public interface CInterface {

    // OBJECTS

    String openObject();

    // FUNCTIONS

    String defineFunction(Types returnType, String name, obj... parameters);

    /**
     * Creates function declaration C code.
     *
     * @param returnType can be null.
     * @param name       the functions' name.
     * @param parameters the functions' parameter types.
     */
    String openFunction(Types returnType, String name, obj... parameters);

    /**
     * @param returnVar return variable can be null.
     */
    String closeFunction(obj returnVar);

    // VARIABLES

    String defineVariable(obj var);

    String defineAndSetVariable(obj var);

    /**
     * Sets var1 value to var2 value. <br>
     * var1, var2 are expected to be pointers.
     */
    String setVariable(obj var1, obj var2);

    /**
     * Sets var1 value to var2 value. <br>
     * var1 is expected to be pointer, var2 a value. <br>
     */
    String setVariable(obj var1, String var2);

    /**
     * Returns a prettified string.
     */
    String pretty(String s);

}
