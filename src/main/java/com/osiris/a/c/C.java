package com.osiris.a.c;

import com.osiris.a.var.code;
import com.osiris.a.var.obj;

public class C implements CInterface {

    @Override
    public String defineFunction(Types returnType, String name, obj... parameters) throws Exception {
        String params = "";
        for (int i = 0; i < parameters.length; i++) {
            params += parameters[i].type + " " + parameters[i].name;
            if (parameters.length - 1 != i) {
                params += ", ";
            }
        }

        if (returnType == null)
            return "void " + name + "(" + params + ");";
        else
            return returnType + " " + name + "(" + params + ");";
    }

    @Override
    public String openFunction(Types returnType, String name, obj... parameters) throws Exception {
        String params = "";
        for (int i = 0; i < parameters.length; i++) {
            params += parameters[i].type + " " + parameters[i].name;
            if (parameters.length - 1 != i) {
                params += ", ";
            }
        }

        //TODO if(types.length > paramPointers.length)
        //    throw new Exception("Too many function parameters ("+params.length()+"). Maximum allowed: "+paramPointers.length+".");

        if (returnType == null)
            return "void " + name + "(" + params + "){";
        else
            return returnType + " " + name + "(" + params + "){";
    }

    @Override
    public String closeFunction(obj returnVar) {
        if (returnVar == null)
            return "}";
        else
            return "return " + returnVar.name + ";}";
    }

    @Override
    public String defineVariable(obj var) {
        if (var instanceof code){
            if (var.value == null)
                return var.type + " " + var.name + ";";
            else
                return var.type.toString().replace("*", "") + " _" + var.name + "=" + var.value + ";" +
                        var.type + " " + var.name + "=&_" + var.name + ";";
        }else{
            if (var.value == null)
                return var.type + " " + var.name + ";";
            else
                return var.type.toString().replace("*", "") + " _" + var.name + "=" + var.value + ";" +
                        var.type + " " + var.name + "=&_" + var.name + ";";
        }
    }

    @Override
    public String setVariable(obj var1, obj var2) {
        return "*" + var1.name + " = *" + var2.name + ";";
    }

    @Override
    public String setVariable(obj var1, String var2) {
        return "*" + var1.name + " = " + var2 + ";";
    }

    @Override
    public String pretty(String s) {
        return s.replaceAll("\\{", "{\n").replaceAll("}", "}\n").replaceAll(";", ";\n");
    }
}
