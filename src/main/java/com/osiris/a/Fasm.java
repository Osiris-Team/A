package com.osiris.a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Fasm {
    public Fasm() throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.environment().put("INCLUDE", Main.fileFasmExe.getParent()+"/INCLUDE");
        Process process = builder.command("\""+Main.fileFasmExe+"\"", ""+Main.fileSourceC, ""+Main.fileBinary)
                .start();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
            String line = null;
            while ((line = reader.readLine()) != null){
                System.out.println(line);
            }
            if(process.exitValue() != 0){
                try(BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
                    String line2 = null;
                    while ((line2 = reader2.readLine()) != null){
                        System.err.println(line2);
                    }
                }
                throw new RuntimeException("FASM failed to create executable binary.");
            }
        }
        System.out.println("Created executable at: "+Main.fileBinary);
    }
}
