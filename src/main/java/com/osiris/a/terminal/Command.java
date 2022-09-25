package com.osiris.a.terminal;

import org.jline.builtins.Completers;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.utils.AttributedString;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Command {
    public String name;
    public String description;
    public List<CommandArg> args;
    /**
     * Code that gets executed on command execution. <br>
     * Consumer contains a String array with the command execution arguments. <br>
     */
    public Consumer<List<String>> code;
    /**
     * Predicate that contains the read line and returns true if
     * this command should be executed.
     */
    public Predicate<String> matcher;

    public Command(String name, String description, List<CommandArg> args,
                   Consumer<List<String>> code) {
        this(name, description, args, code, line -> line.startsWith(name));
    }

    public Command(String name, String description, List<CommandArg> args,
                   Consumer<List<String>> code, Predicate<String> matcher) {
        this.name = name;
        this.description = description;
        if (args == null) this.args = new ArrayList<>();
        else this.args = args;
        this.code = code;
        this.matcher = matcher;
    }

    public CmdDesc toCmdDesc() {
        StringBuilder _mainDesc = new StringBuilder(name + " ");
        Map<String, List<AttributedString>> widgetOpts = new HashMap<>();
        for (CommandArg arg : args) {
            String finalName = arg.getFormattedName();
            _mainDesc.append(finalName + " ");
            widgetOpts.put(finalName, Arrays.asList(new AttributedString(arg.description)));
        }
        if (description != null) _mainDesc.append(description);
        return new CmdDesc(
                Arrays.asList(new AttributedString(_mainDesc.toString())),
                ArgDesc.doArgNames(Collections.emptyList()), widgetOpts);
    }

    public ArgumentCompleter toCompleter() {
        List<Completer> formattedArgs = new ArrayList<>();
        List<Completers.OptDesc> optDescs = new ArrayList<>();
        for (CommandArg arg : args) {
            formattedArgs.add(new StringsCompleter(arg.getFormattedName()));
            optDescs.add(new Completers.OptDesc(arg.getFormattedName(), arg.getFormattedName(), arg.description));
        }
        return new ArgumentCompleter(new StringsCompleter(name),
                new Completers.OptionCompleter(formattedArgs, optDescs, 1)); // == 0, otherwise index out of bounds somehow
    }
}
