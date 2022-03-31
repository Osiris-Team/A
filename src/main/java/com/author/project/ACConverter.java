package com.author.project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The A to C converter, compiles/parses A source code into C source code.
 */
public class ACConverter {
    C c = new C();
    ExecutorService executorService = Executors.newFixedThreadPool(16); // TODO determine OS threads
    List<Future<String>> activeFutures = new ArrayList<>();

    /**
     * @param projectDir the root directory that contains all A source code.
     * @throws IOException
     */
    public void parseProject(File projectDir) throws IOException, InterruptedException {
        parseFiles(projectDir);

        List<A> results = new ArrayList<>();
        while (!activeFutures.isEmpty()) {
            Thread.sleep(100);

        }

        PrintWriter cWriter = new PrintWriter(new BufferedWriter(new FileWriter(Main.fileSourceC)));
        // Create the global context, aka the root code block
        cWriter.print("int main(){");
        cWriter.print("return 0;}");
    }

    public void parseFiles(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) parseFiles(file);
            else {
                if (!file.getName().contains("."))
                    activeFutures.add(executorService.submit(() -> parseFile(file)));
            }
        }
    }

    public String parseFile(File aSourceFile) throws IOException {
        return parseReader(new BufferedReader(new FileReader(aSourceFile)));
    }

    public String parseString(String aSourceString) throws IOException {
        return parseReader(new BufferedReader(new StringReader(aSourceString)));
    }

    public String parseReader(BufferedReader reader) throws IOException {
        return parseReader(null, reader);
    }

    public String parseReader(File aSourceFile, BufferedReader reader) throws IOException {
        StringBuilder generated = new StringBuilder();
        try{
            if(aSourceFile==null) aSourceFile = new File("no-file");
            int lineCount = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] statements = line.split(";");
                for (String statement : statements) {
                    // Remove comment and trim
                    if(statement.contains("//")) statement = statement.substring(0, statement.indexOf("//"));
                    statement = statement.trim();
                    // Check for file path
                    if (statement.startsWith("/")){
                        if(statement.contains(" ")){
                            if(statement.indexOf(" ") != statement.lastIndexOf(" "))
                                throw new ACompileException(aSourceFile, lineCount, "File path cannot contain more than one space.");
                            // Expect variable
                            // TODO /folder/Math math1 = Math();
                        }
                        else if (statement.endsWith("/"))
                            throw new ACompileException(aSourceFile, lineCount, "File path cannot end with '/'.");
                    }
                    else if (statement.contains("/"))
                        throw new ACompileException(aSourceFile, lineCount, "File path must start with '/'.");
                    else if (statement.startsWith("int ")){
                        if(statement.contains("=")){
                            if(statement.endsWith(";"))
                                c.defineVariable(new CVar(statement.substring(0, 3), CTypes._int, statement.substring(statement.indexOf("=")+1, statement.length()-1)));
                            else
                                c.defineVariable(new CVar(statement.substring(0, 3), CTypes._int, statement.substring(statement.indexOf("=")+1)));
                        }
                    }
                }
                lineCount++;
            }
        } catch (Exception e){
            reader.close();
            throw new RuntimeException(e);
        }
        reader.close();
        return generated.toString();
    }
}