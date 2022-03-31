package com.osiris.a.c;

public interface CInterface {

    // FUNCTIONS

    /**
     * Creates function declaration C code.
     * @param returnType can be null.
     * @param name the functions' name.
     * @param parameters the functions' parameter types.
     */
    String startFunction(CTypes returnType, String name, CVar... parameters) throws Exception;

    /**
     * @param returnVar return variable can be null.
     */
    String endFunction(CVar returnVar);

    // VARIABLES

    String defineVariable(CVar var);

    /**
     * Sets var1 value to var2 value. <br>
     * var1, var2 are expected to be pointers.
     */
    String setVariable(CVar var1, CVar var2);

    /**
     * Sets var1 value to var2 value. <br>
     * var1 is expected to be pointer, var2 a value. <br>
     */
    String setVariable(CVar var1, String var2);

    /**
     * Returns a prettified string.
     */
    String pretty(String s);

}
