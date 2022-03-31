package com.osiris.a.c;

import com.osiris.a.A;
import com.osiris.a.Main;
import com.osiris.a.var.code;
import com.osiris.a.var.obj;

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
        StringBuilder generatedC = new StringBuilder();
        try{
            if(aSourceFile==null) aSourceFile = new File("unknown");
            code currentCode = new code(null, null, null);
            int countOpenBrackets = 0;
            int lineCount = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] statements = line.split(";");
                for (String statement : statements) {
                    // Remove comment and trim
                    if(statement.contains("//")) statement = statement.substring(0, statement.indexOf("//"));
                    statement = statement.trim();
                    if(statement.isEmpty()) continue;

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
                        // TODO somehow turn this into C code
                    }
                    else if (statement.contains("/"))
                        throw new ACompileException(aSourceFile, lineCount, "File path must start with '/'.");
                    else if(statement.startsWith("{")){
                        countOpenBrackets++;
                    }
                    else if(statement.startsWith("code ")){
                        if (!statement.endsWith("{"));
                            //throw new ACompileException("Missing ")
                    }
                    else if (statement.startsWith("int ")){
                        String name, value = null;
                        if(statement.contains("=")){
                            name = statement.substring(4, statement.indexOf("=")).trim();
                            if(statement.endsWith(";"))
                                value = statement.substring(statement.indexOf("=")+1, statement.length()-1).trim();
                            else
                                value = statement.substring(statement.indexOf("=")+1).trim();
                            if (value.isEmpty()) throw new ACompileException(aSourceFile, lineCount, "Usage of = even though no value is being assigned.");
                        } else{
                            name = statement.substring(4).trim();
                        }
                        if(name.contains(" ")) throw new ACompileException(aSourceFile, lineCount, "Variable name cannot contain spaces.");
                        if(value != null && value.contains(" ")) throw new ACompileException(aSourceFile, lineCount, "Variable value cannot contain spaces.");
                        CVar var = new CVar(name, CTypes._int, value);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);
                        generatedC.append(c.defineVariable(var));
                    } else{ // Must be a variable name.
                        if(statement.contains("=")){
                            String name = statement.substring(0, statement.indexOf("=")).trim();
                            obj o = findObj(name, currentCode);
                            if (o == null) throw new ACompileException(aSourceFile, lineCount, "No declaration of variable '"+name+"' in the current or parent code blocks found.");
                            String newValue;
                            if(statement.endsWith(";"))
                                newValue = statement.substring(statement.indexOf("=")+1, statement.length()-1).trim();
                            else
                                newValue = statement.substring(statement.indexOf("=")+1).trim();
                            if (newValue.isEmpty()) throw new ACompileException(aSourceFile, lineCount, "Usage of = even though no value is being assigned.");
                            if (newValue.matches("^[0-9]")){ // new Value is actual value // TODO also check new other primitive types when added
                                generatedC.append(c.setVariable(o.cVar, newValue));
                            }
                            else{
                                obj newO = findObj(newValue, currentCode); // newValue is a variable name
                                if(newO == null) throw new ACompileException(aSourceFile, lineCount, "No declaration of variable '"+newValue+"' in the current or parent code blocks found.");
                                generatedC.append(c.setVariable(o.cVar, newO.cVar));
                            }
                        } else
                            throw new ACompileException(aSourceFile, lineCount, "Not a statement."); // TODO add + - and so on
                    }
                }
                lineCount++;
            }
        } catch (Exception e){
            reader.close();
            throw new RuntimeException(e);
        }
        reader.close();
        return generatedC.toString();
    }

    private boolean findPrimitive(String s, int iStart){
        return false; // TODO
    }

    /**
     * Checks current & previous code blocks for already existing variables with the same name,
     * throws {@link ACompileException} if it finds one, otherwise adds the provided variable to the current block.
     */
    private void addToCurrentCode(File aSourceFile, int lineCount, CVar var, code currentCode) throws ACompileException {
        for (obj o : currentCode.variables) {
            if(o.cVar.name.equals(var.name))
                throw new ACompileException(aSourceFile, lineCount, "Variable '"+var.name+"' was already declared in the current or parent code blocks.");
        }
        code temp = currentCode;
        code parentCode;
        while ((parentCode = temp.parentCode) != null){
            for (obj o : parentCode.variables) {
                if(o.cVar.name.equals(var.name))
                    throw new ACompileException(aSourceFile, lineCount, "Variable '"+var.name+"' was already declared in the current or parent code blocks.");
            }
            temp = parentCode.parentCode;
        }
        currentCode.variables.add(new obj(var));
    }

    private obj findObj(String name, code currentCode){
        for (obj o : currentCode.variables) {
            if(o.cVar.name.equals(name))
                return o;
        }
        code temp = currentCode;
        code parentCode;
        while ((parentCode = temp.parentCode) != null){
            for (obj o : parentCode.variables) {
                if(o.cVar.name.equals(name))
                    return o;
            }
            temp = parentCode.parentCode;
        }
        return null;
    }
}