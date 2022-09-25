package com.osiris.a;

import com.osiris.a.terminal.Command;
import com.osiris.a.terminal.CommandArg;
import com.osiris.a.utils.DownloaderThread;
import com.osiris.autoplug.core.search.Search;
import com.osiris.autoplug.core.search.SearchResult;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;
import net.lingala.zip4j.ZipFile;
import org.fusesource.jansi.Ansi;
import org.jline.console.CmdDesc;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.utils.OSUtils;
import org.jline.widget.AutosuggestionWidgets;
import org.jline.widget.TailTipWidgets;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static LineReader reader;
    public static File dirProject, dirCompiler, dirBinaries, fileFasmExe, fileSourceC, fileBinary;
    public static Action action;
    public static List<Command> commands = new ArrayList<>();

    public static void println(String text) {
        println(Ansi.Color.WHITE, text);
    }

    public static void println(Ansi.Color color, String text) {
        System.out.println(Ansi.ansi().fg(color).a(text).reset());
    }

    public static void main(String[] args) throws IOException {        // Init ANSI terminal
        // Determine project directory
        File currentDir = new File(System.getProperty("user.dir"));
        if (currentDir.getName().equals("a")) {
            System.out.println("Determined project dir: " + currentDir.getParentFile());
            updateProjectDir(currentDir.getParentFile());
        } else {
            System.out.println("Determined project dir: " + currentDir);
            updateProjectDir(currentDir);
        }

        // Register commands
        commands.add(new Command("help", "Displays all available commands.",
                null, cmdArgs -> {
            for (Command command : commands) {
                System.out.println(command.name + " - " + command.description);
            }
        }));
        commands.add(new Command("exit", "Exits the program/CLI.",
                null, cmdArgs -> {
            System.out.println("Exiting CLI...");
            System.exit(0);
        }));
        commands.add(new Command("build exe", "Compiles and creates an executable from the A code inside project dir.",
                null, cmdArgs -> {
            System.out.println("Building executable for project: " + Main.dirProject);
            try {
                buildExe();
                System.out.println("Success!");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed. Details above.");
            }
        }));
        commands.add(new Command("build c", "Creates C code from the A code inside project dir." +
                " File will be written to: " + Main.fileSourceC,
                null, cmdArgs -> {
            System.out.println("Creating C code for project: " + Main.dirProject);
            try {
                buildC();
                System.out.println("Success!");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed. Details above.");
            }
        }));
        commands.add(new Command("set project dir", "Update the project directory.",
                Arrays.asList(new CommandArg(null, "path", "Absolute or relative path to the directory containing A source code.")),
                cmdArgs -> {
                    StringBuilder path = new StringBuilder();
                    for (String arg : cmdArgs) {
                        path.append(arg);
                    }
                    updateProjectDir(new File(path.toString()));
                    System.out.println("Updated project dir: " + new File(path.toString()));
                }));

        List<Completer> _completer = new ArrayList<>();
        for (Command command : commands) {
            _completer.add(command.toCompleter());
        }
        AggregateCompleter completer = new AggregateCompleter(_completer);
        reader = LineReaderBuilder.builder().completer(completer).build();
        println(Ansi.Color.GREEN, "Started A compiler CLI.");

        // Create autosuggestion widgets
        AutosuggestionWidgets autosuggestionWidgets = new AutosuggestionWidgets(reader);
        autosuggestionWidgets.enable();

        // Create tailtips
        Map<String, CmdDesc> tailTips = new HashMap<>();
        for (Command command : commands) {
            tailTips.put(command.name, command.toCmdDesc());
        }
        TailTipWidgets tailtipWidgets = new TailTipWidgets(reader, tailTips, 5, TailTipWidgets.TipType.COMBINED);
        tailtipWidgets.enable();

        System.out.println("Enter 'help' to show a list of all commands.");

        if (dirCompiler.listFiles() == null || dirCompiler.listFiles().length == 0) {
            try {
                println(Ansi.Color.YELLOW, "Missing C compiler. Downloading and installing latest...");
                System.out.println("URL: https://github.com/mstorsjo/llvm-mingw");
                System.out.println("Destination: " + dirCompiler);
                SearchResult result;
                if (OSUtils.IS_WINDOWS) {
                    result = Search.github("mstorsjo/llvm-mingw", "0.0", name ->
                            name.endsWith(".zip") && name.contains("ucrt") && name.contains("ucrt-x86_64"));
                } else if (OSUtils.IS_OSX) {
                    result = Search.github("mstorsjo/llvm-mingw", "0.0", name ->
                            name.endsWith(".zip") && name.contains("ucrt") && name.contains("macos"));
                } else {
                    result = Search.github("mstorsjo/llvm-mingw", "0.0", name ->
                            name.endsWith(".zip") && name.contains("ucrt") && name.contains("ubuntu"));
                }

                // Download and unpack the c compiler:
                if (!result.isUpdateAvailable) throw new Exception("Failed to find downloadable asset!");
                BThreadManager manager = new BThreadManager();
                BThreadPrinter printer = new BThreadPrinter(manager);
                printer.start();
                File downloadDest = new File(dirCompiler + "/" + result.assetFileName);
                DownloaderThread download = new DownloaderThread("Download", manager, result.downloadUrl, downloadDest);
                download.start();
                download.join();
                println("Unpacking (this may take a bit, don't abort)...");
                new ZipFile(downloadDest).extractAll(dirCompiler.getPath());
                downloadDest.delete();
                println("Successfully installed the C compiler.");

            } catch (Exception e) {
                throw new RuntimeException("Critical error during C compiler installation! Please report this issue.", e);
            }
        }

        new Thread(() -> {
            try {
                while (true) {
                    String line = reader.readLine();
                    boolean match = false;
                    for (Command command : commands) {
                        if (command.matcher.test(line)) {
                            match = true;
                            String[] _commandArgs = line.split(" ");
                            command.code.accept(com.osiris.a.utils.Arrays.toList(_commandArgs, 1, _commandArgs.length));
                            break;
                        }
                    }
                    if (!match) System.err.println("Unknown command '" + line + "'. Enter 'help' for all commands.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }).start();
    }

    private static void buildC() throws Exception {
        // Final step: Create an executable from the generated assembly code.
        if (!fileSourceC.exists())
            throw new Exception("C code must have been generated before! Missing file: " + fileSourceC);
        if (fileSourceC.length() == 0)
            throw new Exception("Generated C code file is empty! File: " + fileSourceC);
    }

    private static void buildExe() throws Exception {
        // Final step: Create an executable from the generated assembly code.
        if (!fileSourceC.exists())
            throw new Exception("C code must have been generated before! Missing file: " + fileSourceC);
    }

    private static void updateProjectDir(File dir) {
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
