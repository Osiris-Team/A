package com.osiris.a;

import java.io.File;
import java.io.IOException;

public class Main {

    public static File dir = new File(System.getProperty("user.dir"));
    public static File dirCompiler = new File(dir+"/a/compiler");
    public static File dirBinaries = new File(dir+"/a/binaries");
    public static File fileFasmExe = new File(dir+"/a/compiler/fasm/FASM.exe"); // TODO cross-platform
    public static File fileSourceC = new File(dir+"/a/compiler/source.c");
    public static File fileBinary = new File(dir+"/a/binaries/my-program.exe"); // TODO cross-platform

    public static void main(String[] args) throws IOException {
        System.out.println("Running A compiler in "+dir);
        dir.mkdirs();
        dirCompiler.mkdirs();
        dirBinaries.mkdirs();
        // TODO use fasm depending on current OS
        // TODO convert A code to assembly
        // TODO expects fasm in current dir

        // Final step: Create an executable from the generated assembly code.
        if(!fileFasmExe.exists()) throw new RuntimeException("FASM must be installed! Missing file: "+ fileFasmExe);
        if(!fileSourceC.exists()) throw new RuntimeException("C code must have been generated before! Missing file: "+ fileSourceC);
        new Fasm();
    }

}
