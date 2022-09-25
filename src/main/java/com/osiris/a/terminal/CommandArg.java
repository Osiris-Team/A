package com.osiris.a.terminal;

public class CommandArg {
    public Command command;
    public String name;
    public String description;
    public boolean isOptional = false;

    public CommandArg() {
    }

    public CommandArg(Command command, String name, String description) {
        this.command = command;
        this.name = name;
        this.description = description;
    }

    public String getFormattedName() {
        return (isOptional ? "(<" + name + ">)" : "<" + name + ">");
    }

    public CommandArg setCommand(Command command) {
        this.command = command;
        return this;
    }

    public CommandArg setName(String name) {
        this.name = name;
        return this;
    }

    public CommandArg setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommandArg setOptional(boolean optional) {
        isOptional = optional;
        return this;
    }
}
