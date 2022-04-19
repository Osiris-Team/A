package com.osiris.a;

import com.osiris.a.utils.Arrays;
import com.osiris.a.utils.Files;

import java.io.File;
import java.io.IOException;

public class Main {

    public static File dir, dirCompiler, dirBinaries, fileFasmExe, fileSourceC, fileBinary;
    public static Action action;

    public static void main(String[] args) throws IOException {
        System.out.println("Running A compiler in " + dir);
        System.out.println("Execute with -help to show a list of all args.");
        if (args.length == 0){
            args = new String[]{"action=build-exe", "source=./"};
        }
        System.out.println("Args: " + Arrays.toString(args));
        for (String arg : args) {
            if(arg.startsWith("help")){
                System.out.println("If no args are provided, default args get added: action=build-exe source=./");
                System.out.println("help\n" +
                        " > Displays this and exits.");
                System.out.println("action=<build-c/build-exe>\n" +
                        " > The action to execute." +
                        " To compile A code into C code execute the 'build-c' action." +
                        " To compile and create an executable execute the 'build-exe' action.");
                System.out.println("source=<path>\n" +
                        " > Absolute or relative path to the directory containing A source code." +
                        "If null, current dir path gets used.");
                return;
            } else if(arg.startsWith("action")){
                int i = arg.indexOf("=");
                if(i == -1){
                    System.err.println("Argument '"+arg+"' must contain a '='.");
                    System.exit(-1);
                    return;
                }
                String action = arg.substring(i+1);
                if(action.equals("build-c")) Main.action = Action.BUILD_C;
                else if(action.equals("build-exe")) Main.action = Action.BUILD_EXE;
                else{
                    System.err.println("Argument '"+arg+"' value is wrong (not allowed).");
                    System.exit(-1);
                    return;
                }
            } else if(arg.startsWith("source")){
                int i = arg.indexOf("=");
                if(i == -1){
                    System.err.println("Argument '"+arg+"' must contain a '='.");
                    System.exit(-1);
                    return;
                }
                String path = arg.substring(i+1);
                Main.dir = Files.toFile(path);
                Main.dirCompiler = new File(dir + "/a/compiler");
                Main.dirBinaries = new File(dir + "/a/binaries");
                Main.fileFasmExe = new File(dir + "/a/compiler/fasm/FASM.exe"); // TODO cross-platform
                Main.fileSourceC = new File(dir + "/a/compiler/source.c");
                Main.fileBinary = new File(dir + "/a/binaries/my-program.exe"); // TODO cross-platform
            } else{
                System.err.println("Argument '"+arg+"' key is wrong (does not exist).");
                System.exit(-1);
                return;
            }
        }
        dir.mkdirs();
        dirCompiler.mkdirs();
        dirBinaries.mkdirs();
        if(dirCompiler.listFiles() == null || dirCompiler.listFiles().length == 0){
            // TODO download gcc for current OS
            throw new RuntimeException("Missing C compiler! Make sure gcc is installed at: "+dirCompiler);
        }



        // Final step: Create an executable from the generated assembly code.
        if (!fileSourceC.exists())
            throw new RuntimeException("C code must have been generated before! Missing file: " + fileSourceC);
        new Fasm();
    }

}
