package com.author.project;

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
     * Sets var1 value to var2 value.
     */
    String setVariable(CVar var1, CVar var2);

    /**
     * Returns a prettified string.
     */
    String pretty(String s);

}
