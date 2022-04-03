package com.osiris.a;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds data of a parsed A source code file.
 */
public class A {
    public boolean success = false;
    public List<File> importedFiles = new ArrayList<>();
    public String cCodeFunctionDefinitions;
    public String cCode;
}
