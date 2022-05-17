package com.osiris.a;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static File dirProject, dirCompiler, dirBinaries, fileFasmExe, fileSourceC, fileBinary;
    public static Action action;

    public static void main(String[] args) throws IOException {
        System.out.println("Started A compiler CLI.");
        System.out.println("Enter 'help' to show a list of all commands.");
        File currentDir = new File(System.getProperty("user.dir"));
        if(currentDir.getName().equals("a")) {
            System.out.println("Determined project dir: "+currentDir.getParentFile());
            updateProjectDir(currentDir.getParentFile());
        } else {
            System.out.println("Determined project dir: "+currentDir);
            updateProjectDir(currentDir);
        }
        new Thread(() -> {
            try{
                boolean exit = false;
                while(!exit){
                    String line = new Scanner(System.in).nextLine();
                    if(line.startsWith("help")){
                        System.out.println("help\n" +
                                "Info: Displays this.");
                        System.out.println("exit\n" +
                                "Info: Exits the program/CLI.");
                        System.out.println("build exe\n" +
                                "Info: Compiles and creates an executable from the A code inside project dir.");
                        System.out.println("build c\n" +
                                "Info: Creates C code from the A code inside project dir." +
                                "File will be written to: " + Main.fileSourceC);
                        System.out.println("set project dir <path>\n" +
                                "Info: Absolute or relative path to the directory containing A source code.");
                    }
                    else if(line.startsWith("exit")){
                        System.out.println("Exiting CLI...");
                        exit = true;
                    }
                    else if(line.startsWith("build-exe")){
                        System.out.println("Building executable for project: "+ Main.dirProject);
                        try{
                            buildExe();
                            System.out.println("Success!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("Failed. Details above.");
                        }
                    }
                    else if(line.startsWith("build-c")){
                        System.out.println("Creating C code for project: "+ Main.dirProject);
                        try{
                            buildC();
                            System.out.println("Success!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("Failed. Details above.");
                        }
                    }
                    else if(line.startsWith("set project dir ")){
                        int i = "set project dir ".length();
                        String path = line.substring(i+1);
                        updateProjectDir(new File(path));
                        System.out.println("Updated project dir: "+ new File(path));
                    } else{
                        System.err.println("Unknown command '"+line+"'. Enter 'help' for all commands.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }).start();


        //if(dirCompiler.listFiles() == null || dirCompiler.listFiles().length == 0){
        //    System.out.println("Missing C compiler. Downloading and installing...");
        //    // TODO download gcc for current OS
        //    throw new RuntimeException("Missing C compiler! Make sure gcc is installed at: "+dirCompiler);
        //}
    }

    private static void buildC() throws Exception {
        // Final step: Create an executable from the generated assembly code.
        if (!fileSourceC.exists())
            throw new Exception("C code must have been generated before! Missing file: " + fileSourceC);
        if(fileSourceC.length() == 0)
            throw new Exception("Generated C code file is empty! File: " + fileSourceC);
    }

    private static void buildExe() throws Exception {
        // Final step: Create an executable from the generated assembly code.
        if (!fileSourceC.exists())
            throw new Exception("C code must have been generated before! Missing file: " + fileSourceC);
    }

    private static void updateProjectDir(File dir){
        Main.dirProject = dir;
        Main.dirCompiler = new File(dir + "/a/compiler");
        Main.dirBinaries = new File(dir + "/a/binaries");
        Main.dirProject.mkdirs();
        Main.dirCompiler.mkdirs();
        Main.dirBinaries.mkdirs();

        Main.fileFasmExe = new File(dir + "/a/compiler/fasm/FASM.exe"); // TODO cross-platform
        Main.fileSourceC = new File(dir + "/a/compiler/source.c");
        Main.fileBinary = new File(dir + "/a/binaries/my-program.exe"); // TODO cross-platform
    }

}
