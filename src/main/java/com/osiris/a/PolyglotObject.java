package com.osiris.a;

import java.util.function.Function;


/**
 * Object that can be represented in multiple programming languages. <br>
 * It also may have multiple representations depending on the context and time,
 * that's why:<br>
 * - functions contain arguments which hold context details. <br>
 * - functions are modifiable to represent the object in different time states. <br>
 */
@Deprecated
public abstract class PolyglotObject {
    public Function<Object[], String> toCString;
    public Function<Object[], String> toAString;

    public PolyglotObject(Function<Object[], String> toCString,
                          Function<Object[], String> toAString) {
        this.toCString = toCString;
        this.toAString = toAString;
    }

    /**
     * String representation of this object in the C language.
     */
    public String toCString(Object[] args) {
        return toCString.apply(args);
    }

    /**
     * String representation of this object in the A language.
     */
    public String toAString(Object[] args) {
        return toAString.apply(args);
    }
}
