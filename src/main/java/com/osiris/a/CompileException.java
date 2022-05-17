package com.osiris.a;

import java.io.File;

public class CompileException extends Exception {
    public CompileException(File sourceCode, int line, String message) {
        super(("" + sourceCode).replace("" + Main.dirProject, "").replaceAll("\\\\", "") + ":" + line + " " + message);
    }
}
