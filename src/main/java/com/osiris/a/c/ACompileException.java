package com.osiris.a.c;

import com.osiris.a.Main;

import java.io.File;

public class ACompileException extends Exception {
    public ACompileException(File sourceCode, int line, String message) {
        super(("" + sourceCode).replace("" + Main.dir, "").replaceAll("\\\\", "") + ":" + line + " " + message);
    }
}
