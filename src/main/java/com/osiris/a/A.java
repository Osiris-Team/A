package com.osiris.a;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds data of a parsed A source code file.
 */
public class A {
    public boolean success = false;
    public File aSourceFile;
    public List<A> importedFiles = new ArrayList<>();
    public String cCodeStructDefinition;
    public String cCodeStructVarDefinitions;
    public String cCodeStruct;
    public String cCodeFunctionDefinitions;
    public String cCodeFunctions;
    public String cCode;
}
