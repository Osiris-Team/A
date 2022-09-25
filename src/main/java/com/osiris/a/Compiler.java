package com.osiris.a;

import com.osiris.a.c.C;
import com.osiris.a.c.Types;
import com.osiris.a.var.code;
import com.osiris.a.var.obj;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The A to C converter, compiles/parses A source code into C source code.
 */
public class Compiler {
    File projectDir = new File(System.getProperty("user.dir"));
    C c = new C();
    ExecutorService executorService = Executors.newFixedThreadPool(16); // TODO determine OS threads
    List<Future<A>> activeFutures = new ArrayList<>();
    List<A> parsedFiles = new CopyOnWriteArrayList<>();

    /**
     * @param projectDir the root directory that contains all A source code.
     * @throws IOException
     */
    public void parseProject(File projectDir) throws IOException, InterruptedException, ExecutionException {

        parseFiles(projectDir);
        fetchResults();

        // Write to the final c source file containing all the code:
        PrintWriter cWriter = new PrintWriter(new BufferedWriter(new FileWriter(Main.fileSourceC)));

        // 1. All includes here (split by \n):
        cWriter.println("#include <stdio.h>");
        cWriter.println("#include <stdlib.h>");

        // 2. Object type definitions here:
        for (A a : parsedFiles) {
            cWriter.print(a.cCodeStructDefinition);
        }

        // 3. Object structs, with its members here:
        for (A a : parsedFiles) {
            cWriter.print(a.cCodeStruct);
        }

        // 4. All function definitions here:
        for (A a : parsedFiles) {
            cWriter.print(a.cCodeFunctionDefinitions);
        }

        // 5. All functions (constructors included) here:
        for (A a : parsedFiles) {
            cWriter.print(a.cCodeFunctions);
        }

        // TODO:
        cWriter.print("int main(){");
        cWriter.print("return 0;}");
    }

    /**
     * Done asynchronously. Do {@link #fetchResults()} to get the results.
     */
    public synchronized void parseFiles(File dir) throws InterruptedException, ExecutionException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) parseFiles(file);
            else {
                if (!file.getName().contains("."))
                    activeFutures.add(executorService.submit(() -> parseFile(file)));
            }
        }
    }

    /**
     * Waits until all tasks are done and returns the results ({@link #parsedFiles}).
     */
    public synchronized List<A> fetchResults() throws InterruptedException, ExecutionException {
        List<Future<A>> results = new ArrayList<>();
        while (!activeFutures.isEmpty()) {
            Thread.sleep(100);
            for (Future<A> result :
                    activeFutures) {
                if (result.isDone()) {
                    results.add(result);
                    parsedFiles.add(result.get());
                }
            }
            activeFutures.removeAll(results);
        }
        return parsedFiles;
    }

    public A parseFile(File aSourceFile) throws IOException {
        for (A a : parsedFiles) { // Check already parsed files
            if (a.aSourceFile == aSourceFile) {
                return a;
            }
        }
        return parseReader(new BufferedReader(new FileReader(aSourceFile)));
    }

    public A parseString(String aSourceString) throws IOException {
        return parseReader(new BufferedReader(new StringReader(aSourceString)));
    }

    public A parseReader(BufferedReader reader) throws IOException {
        return parseReader(null, reader);
    }

    public A parseReader(File aSourceFile, BufferedReader reader) throws IOException {
        A a = new A();
        if (aSourceFile == null) aSourceFile = new File("Unknown");
        a.aSourceFile = aSourceFile;
        String structName = genStructName(aSourceFile, projectDir);
        a.cCodeStructDefinition = "typedef struct " + structName + " " + structName + ";";
        // Generated C code:
        // The C struct contains the current object/files private stuff
        StringBuilder genCStruct = new StringBuilder();
        genCStruct.append("struct " + structName + "{");
        StringBuilder genCVariableDefinitions = new StringBuilder();
        StringBuilder genCConstructor = new StringBuilder();
        obj thisObj = new obj("this", Types.custom(structName, structName + "*"));
        StringBuilder genCFunctionsDefinitions = new StringBuilder();
        StringBuilder genCFunctions = new StringBuilder();
        code currentCode = new code(null, null, null);
        try {
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

                    } else if (statement.startsWith("short ")) {
                        obj var = determineVar(currentCode, aSourceFile, lineCount, statement, Types._short);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);

                    } else if (statement.startsWith("int ")) {
                        obj var = determineVar(currentCode, aSourceFile, lineCount, statement, Types._int);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);

                    } else if (statement.startsWith("long ")) {
                        obj var = determineVar(currentCode, aSourceFile, lineCount, statement, Types._long);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);

                    } else if (statement.startsWith("code ")) {
                        code var = (code) determineVar(currentCode, aSourceFile, lineCount, statement, Types.code);
                        addToCurrentCode(aSourceFile, lineCount, var, currentCode);
                        if (statement.contains("{")) {
                            countOpenBrackets++;
                            var.parentCode = currentCode;
                            currentCode = var;
                        }

                        genCFunctionsDefinitions.append(c.defineFunction(var.returnType,
                                (var.name = genFunctionName(var.name, aSourceFile, projectDir)),
                                var.parameters.toArray(new obj[0])));
                        //generatedC.append(c.defineVariable(var));

                    } else if (isAvailableObject(statement, aSourceFile)) {

                    } else { // Must be a variable name.
                        if (statement.contains("=")) {
                            String name = statement.substring(0, statement.indexOf("=")).trim();
                            obj o = findObj(name, currentCode);
                            if (o == null)
                                throw new CompileException(aSourceFile, lineCount, "No declaration of variable '" + name + "' in the current or parent code blocks found.");
                            if (o instanceof code) { // Special case when code

                            } else { // Regular variable
                                String newValue;
                                if (statement.endsWith(";"))
                                    newValue = statement.substring(statement.indexOf("=") + 1, statement.length() - 1).trim();
                                else
                                    newValue = statement.substring(statement.indexOf("=") + 1).trim();
                                if (newValue.isEmpty())
                                    throw new CompileException(aSourceFile, lineCount, "Usage of = even though no value is being assigned.");
                                if (o.isFinal && o.value != null)
                                    throw new CompileException(aSourceFile, lineCount, "Cannot change final variable '" + o.name + "' value since it was already set.");
                                obj existingO = isValidValue(currentCode, o.type, newValue, aSourceFile, lineCount);
                                if (existingO == null) // new Value is actual value and matches the type
                                {
                                    currentCode.cCode.append(c.setVariable(o, newValue)); // Update the value
                                    o.value = newValue;
                                } else // newValue is another variable name
                                {
                                    currentCode.cCode.append(c.setVariable(o, existingO));  // Update the value
                                    o.value = existingO.name;
                                }
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

        // Create pre-constructor method, that initializes the member variables
        List<obj> initDefsParams = new ArrayList<>();
        initDefsParams.add(thisObj);
        code initDefs = new code(currentCode, "init_defaults_" + structName, initDefsParams);
        currentCode.variables.add(0, initDefs);
        for (obj var : currentCode.variables) {
            if (var.type != Types.code) {
                String cCodeVar = c.defineAndSetVariable(var);
                initDefs.cCode.append(cCodeVar);
                genCVariableDefinitions.append(cCodeVar);
                initDefs.cCode.append("this->" + var.name + "=" + var.name + ";");
            }
        }

        for (obj var : currentCode.variables) {
            if (var.type == Types.code) {
                code function = (code) var;
                if (function.isStatic) function.parameters.add(0, thisObj);
                obj[] params = function.parameters.toArray(new obj[0]);
                genCFunctionsDefinitions.append(c.defineFunction(function.returnType, function.name, params));
                genCFunctions.append(c.openFunction(function.returnType, function.name, params));
                genCFunctions.append(function.cCode);
                genCFunctions.append("}");
            }
        }


        a.cCodeStructVarDefinitions = genCVariableDefinitions.toString();
        genCStruct.append(a.cCodeStructVarDefinitions);
        genCStruct.append("}");
        reader.close();

        a.cCodeStruct = genCStruct.toString();
        a.cCodeFunctionDefinitions = genCFunctionsDefinitions.toString();
        a.cCodeFunctions = genCFunctions.toString();
        a.cCode = a.cCodeStructDefinition + a.cCodeStruct + a.cCodeFunctionDefinitions + a.cCodeFunctions;
        return a;
    }

    private boolean isAvailableObject(String statement, File aSourceFile) {
        String objName;
        if (statement.contains(" ")) objName = statement.split(" ")[0];
        else objName = statement;

        if (aSourceFile.getParentFile() != null) { // Search files in current dir
            for (File f : aSourceFile.getParentFile().listFiles()) {
                if (f.getName().equals(objName)) ;
            }
        }
        return false;
    }

    public String genStructName(File sourceFile, File projectDir) {
        return sourceFile.getAbsolutePath().replace(projectDir.getAbsolutePath(), "")
                .replaceAll("\\\\", "_")
                .replaceAll(":", "")
                .replaceAll("/", "_")
                .trim();
    }

    /**
     * Generates a unique function name with the variable name,
     * source file and project dir. <br>
     * Example: varName=myFunction <br>
     * sourceFile=C:\User\project\lib\MyObject <br>
     * projectDir=C:\User\project <br>
     * Output/Returns: _lib_MyObject_myFunction <br>
     */
    public String genFunctionName(String varName, File sourceFile, File projectDir) {
        return sourceFile.getAbsolutePath().replace(projectDir.getAbsolutePath(), "")
                .replaceAll("\\\\", "_")
                .replaceAll(":", "")
                .replaceAll("/", "_")
                .trim() + "_"
                + varName;
    }

    private obj determineVar(code currentCode, File aSourceFile, int lineCount, String statement, Types type) throws CompileException {
        obj o = new obj();
        boolean isFinal = false;
        if (statement.contains(" final ")) {
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
        if (value != null) {
            if (value.contains(" "))
                throw new CompileException(aSourceFile, lineCount, "Variable value cannot contain spaces.");
            if (value.trim().isEmpty()) value = null;
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