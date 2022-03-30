package com.author.project;

import java.io.File;
import java.io.IOException;

public class Main {

    public static File dir = new File(System.getProperty("user.dir"));
    public static File dirCompiler = new File(dir+"/a/compiler");
    public static File dirBinaries = new File(dir+"/a/binaries");
    public static File fileFasmExe = new File(dir+"/a/compiler/fasm/FASM.exe"); // TODO cross-platform
    public static File fileSourceAssembly = new File(dir+"/a/compiler/source.asm");
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
        if(!fileSourceAssembly.exists()) throw new RuntimeException("Assembly code must have been generated before! Missing file: "+fileSourceAssembly);
        new Fasm();
    }

}
