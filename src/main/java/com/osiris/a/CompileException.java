package com.osiris.a;

import com.osiris.a.Main;

import java.io.File;

public class CompileException extends Exception {
    public CompileException(File sourceCode, int line, String message) {
        super(("" + sourceCode).replace("" + Main.dir, "").replaceAll("\\\\", "") + ":" + line + " " + message);
    }
}
