package com.osiris.a;

import java.io.File;

public class ACompileException extends Exception{
    public ACompileException(File sourceCode, int line, String message) {
        super(sourceCode.getName()+":"+line+" "+message);
    }
}
