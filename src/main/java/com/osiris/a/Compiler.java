package com.osiris.a;

import com.osiris.a.c.C;
import com.osiris.a.c.Types;
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
public class Compiler {
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
        StringBuilder generatedCFunctionsDefinitions = new StringBuilder();
        StringBuilder generatedC = new StringBuilder();
        try {
            if (aSourceFile == null) aSourceFile = new File("unknown");
            code currentCode = new code(null, null, null);
            int countOpenBrackets = 0;
            int lineCount = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] statements = line.split(";");
                for (String statement : statements) {
                    // Remove comment and trim
                    if (statement.contains("//")) statement = statement.substring(0, statement.indexOf("//"));
                    statement = statement.trim();
                    if (statement.isEmpty()) continue;

                    // Check for file path
                    if (statement.startsWith("/")) {
                        if (statement.contains(" ")) {
                            if (statement.indexOf(" ") != statement.lastIndexOf(" "))
                                throw new CompileException(aSourceFile, lineCount, "File path cannot contain more than one space.");
                            // Expect variable
                            // TODO /folder/Math math1 = Math();
                        } else if (statement.endsWith("/"))
                            throw new CompileException(aSourceFile, lineCount, "File path cannot end with '/'.");
                        // TODO somehow turn this into C code

                    } else if (statement.contains("/"))
                        throw new CompileException(aSourceFile, lineCount, "File path must start with '/'.");

                    else if (statement.startsWith("{")) {
                        countOpenBrackets++;

                    } else if (statement.startsWith("byte ")) {
                        obj var = determineVar(currentCode, aSourceFile, lineCount, statement, Types._byte);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);
                        generatedC.append(c.defineVariable(var));

                    } else if (statement.startsWith("short ")) {
                        obj var = determineVar(currentCode, aSourceFile, lineCount, statement, Types._short);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);
                        generatedC.append(c.defineVariable(var));

                    } else if (statement.startsWith("int ")) {
                        obj var = determineVar(currentCode, aSourceFile, lineCount, statement, Types._int);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);
                        generatedC.append(c.defineVariable(var));

                    } else if (statement.startsWith("long ")) {
                        obj var = determineVar(currentCode, aSourceFile, lineCount, statement, Types._long);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);
                        generatedC.append(c.defineVariable(var));

                    } else if (statement.startsWith("code ")) {
                        if (!statement.endsWith("{")) ;
                        //throw new ACompileException("Missing ")

                    } else { // Must be a variable name.
                        if (statement.contains("=")) {
                            String name = statement.substring(0, statement.indexOf("=")).trim();
                            obj o = findObj(name, currentCode);
                            if (o == null)
                                throw new CompileException(aSourceFile, lineCount, "No declaration of variable '" + name + "' in the current or parent code blocks found.");
                            String newValue;
                            if (statement.endsWith(";"))
                                newValue = statement.substring(statement.indexOf("=") + 1, statement.length() - 1).trim();
                            else
                                newValue = statement.substring(statement.indexOf("=") + 1).trim();
                            if (newValue.isEmpty())
                                throw new CompileException(aSourceFile, lineCount, "Usage of = even though no value is being assigned.");
                            if(o.isFinal && o.value!=null)
                                throw new CompileException(aSourceFile, lineCount, "Cannot change final variable '"+o.name+"' value since it was already set.");
                            obj existingO = isValidValue(currentCode, o.type, newValue, aSourceFile, lineCount);
                            if (existingO == null) // new Value is actual value and matches the type
                            {
                                generatedC.append(c.setVariable(o, newValue)); // Update the value
                                o.value = newValue;
                            }
                            else // newValue is another variable name
                            {
                                generatedC.append(c.setVariable(o, existingO));  // Update the value
                                o.value = existingO.name;
                            }
                        } else
                            throw new CompileException(aSourceFile, lineCount, "Not a statement."); // TODO add + - and so on
                    }
                }
                lineCount++;
            }
        } catch (Exception e) {
            reader.close();
            throw new RuntimeException(e);
        }
        reader.close();
        return generatedC.toString();
    }

    private obj determineVar(code currentCode, File aSourceFile, int lineCount, String statement, Types type) throws CompileException {
        obj o = new obj();
        boolean isFinal = false;
        if (statement.contains(" final ")){
            statement = statement.replace(" final", "");
            isFinal = true;
        }

        String name, value = null;
        if (statement.contains("=")) {
            name = statement.substring(4, statement.indexOf("=")).trim();
            if (statement.endsWith(";"))
                value = statement.substring(statement.indexOf("=") + 1, statement.length() - 1).trim();
            else
                value = statement.substring(statement.indexOf("=") + 1).trim();
            if (value.isEmpty())
                throw new CompileException(aSourceFile, lineCount, "Usage of = even though no value is being assigned.");
        } else {
            name = statement.substring(4).trim();
        }
        if (name.contains(" "))
            throw new CompileException(aSourceFile, lineCount, "Variable name cannot contain spaces.");
        if (value != null){
            if(value.contains(" "))
                throw new CompileException(aSourceFile, lineCount, "Variable value cannot contain spaces.");
            if(value.trim().isEmpty()) value = null;
        }
        isValidValue(currentCode, type, value, aSourceFile, lineCount);
        o.name = name;
        o.type = type;
        o.value = value;
        o.isFinal = isFinal;
        return o;
    }

    /**
     * Val can be an actual value or a variable name. <br>
     * This method covers both cases, by searching for an existing variable first
     * and returning it if it does and has the same type. <br>
     */
    private obj isValidValue(code currentCode, Types type, String val, File aSourceFile, int lineCount) throws CompileException {
        if (val == null) return null;
        obj existingObj = findObj(val, currentCode); // Check if the value is a variable name first and if it is compare value types.
        if (existingObj != null)
            if (existingObj.type != type)
                throw new CompileException(aSourceFile, lineCount, "Wrong value type. Provided " + existingObj.type.inA + " '" + val + "', but required " + type.inA + ".");
            else
                return existingObj;
        try {
            switch (type) {
                case _byte:
                    if (val.startsWith("0b") || val.startsWith("0B")) // To allow binary like 0b10101001
                        if (!val.substring(2).matches("[0-1]"))
                            throw new Exception("Binary can only contain ones and zeros.");
                        else return null;
                    Byte.parseByte(val);
                    return null;
                case _short:
                    Short.parseShort(val);
                    return null;
                case _int:
                    Integer.parseInt(val);
                    return null;
                case _long:
                    Long.parseLong(val);
                    return null;
                case code:
                    if (!val.endsWith("{")) throw new Exception("Code definition must end with '{'.");
                    return null;
            }
        } catch (Exception e) { // To catch runtime exceptions
            throw new CompileException(aSourceFile, lineCount, "Wrong value format. Provided value '" + val + "' is not of type " + type.inA + ". Details: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks current & previous code blocks for already existing variables with the same name,
     * throws {@link CompileException} if it finds one, otherwise adds the provided variable to the current block.
     */
    private void addToCurrentCode(File aSourceFile, int lineCount, obj var, code currentCode) throws CompileException {
        for (obj o : currentCode.variables) {
            if (o.name.equals(var.name))
                throw new CompileException(aSourceFile, lineCount, "Variable '" + var.name + "' was already declared in the current or parent code blocks.");
        }
        code parentCode = currentCode.parentCode;
        while (parentCode != null) {
            for (obj o : parentCode.variables) {
                if (o.name.equals(var.name))
                    throw new CompileException(aSourceFile, lineCount, "Variable '" + var.name + "' was already declared in the current or parent code blocks.");
            }
            parentCode = parentCode.parentCode;
        }
        currentCode.variables.add(var);
    }

    private obj findObj(String name, code currentCode) {
        for (obj o : currentCode.variables) {
            if (o.name.equals(name))
                return o;
        }
        code parentCode = currentCode.parentCode;
        while (parentCode != null) {
            for (obj o : parentCode.variables) {
                if (o.name.equals(name))
                    return o;
            }
            parentCode = parentCode.parentCode;
        }
        return null;
    }
}