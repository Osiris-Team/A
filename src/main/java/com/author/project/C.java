package com.author.project;

import java.io.BufferedReader;
import java.io.StringReader;

public class C implements CInterface {

    @Override
    public String startFunction(CTypes returnType, String name, CVar... parameters) throws Exception {
        String params = "";
        for (int i = 0; i < parameters.length; i++) {
            params  += parameters[i].type +" "+parameters[i].name;
            if(parameters.length-1 != i){
                params += ", ";
            }
        }

        //TODO if(types.length > paramPointers.length)
        //    throw new Exception("Too many function parameters ("+params.length()+"). Maximum allowed: "+paramPointers.length+".");

        if(returnType==null)
            return "void "+name+"("+params+"){";
        else
            return returnType +" "+name+"("+params+"){";
    }

    @Override
    public String endFunction(CVar returnVar) {
        if(returnVar == null)
            return "}";
        else
            return "return "+ returnVar.name+";}";
    }

    @Override
    public String defineVariable(CVar var) {
        if(var.value == null)
            return var.type +" "+var.name +";";
        else
            return var.type.toString().replace("*", "") + " temp_"+var.name+"="+var.value+";"+
                    var.type +" "+var.name +"=&temp_"+var.name+";";
    }

    @Override
    public String setVariable(CVar var1, CVar var2) {
        return "*"+var1.name+" = *"+var2.name+";";
    }

    @Override
    public String pretty(String s) {
        return s.replaceAll("\\{", "{\n").replaceAll("}", "}\n").replaceAll(";",";\n");
    }
}
