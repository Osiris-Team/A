package com.osiris.a;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.osiris.a.utils.DownloaderThread;
import com.osiris.autoplug.core.json.JsonTools;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;
import net.lingala.zip4j.ZipFile;
import org.fusesource.jansi.Ansi;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.OSUtils;
import org.jline.widget.AutosuggestionWidgets;
import org.jline.widget.TailTipWidgets;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.*;

public class Main {

    public static LineReader reader;
    public static File dirProject, dirCompiler, dirBinaries, fileFasmExe, fileSourceC, fileBinary;
    public static Action action;

    public static void println(String text){
        println(Ansi.Color.WHITE, text);
    }
    public static void println(Ansi.Color color, String text){
        System.out.println(Ansi.ansi().fg(color).a(text).reset());
    }

    public static void main(String[] args) throws IOException {
        reader = LineReaderBuilder.builder().build();

        // Create autosuggestion widgets
        AutosuggestionWidgets autosuggestionWidgets = new AutosuggestionWidgets(reader);
        autosuggestionWidgets.enable();

        // Create tailtips
        Map<String, CmdDesc> tailTips = new HashMap<>();
        Map<String, List<AttributedString>> widgetOpts = new HashMap<>();
        List<AttributedString> mainDesc = Arrays.asList(new AttributedString("widget -N new-widget [function-name]")
                , new AttributedString("widget -D widget ...")
                , new AttributedString("widget -A old-widget new-widget")
                , new AttributedString("widget -U string ...")
                , new AttributedString("widget -l [options]")
        );
        widgetOpts.put("-N", Arrays.asList(new AttributedString("Create new widget")));
        widgetOpts.put("-D", Arrays.asList(new AttributedString("Delete widgets")));
        widgetOpts.put("-A", Arrays.asList(new AttributedString("Create alias to widget")));
        widgetOpts.put("-U", Arrays.asList(new AttributedString("Push characters to the stack")));
        widgetOpts.put("-l", Arrays.asList(new AttributedString("List user-defined widgets")));

        tailTips.put("widget", new CmdDesc(mainDesc, ArgDesc.doArgNames(Arrays.asList("[pN...]")), widgetOpts));
        TailTipWidgets tailtipWidgets = new TailTipWidgets(reader, tailTips, 5, TailTipWidgets.TipType.COMPLETER);
        tailtipWidgets.enable();

        println(Ansi.Color.GREEN, "Started A compiler CLI.");
        System.out.println("Enter 'help' to show a list of all commands.");
        File currentDir = new File(System.getProperty("user.dir"));
        if(currentDir.getName().equals("a")) {
            System.out.println("Determined project dir: "+currentDir.getParentFile());
            updateProjectDir(currentDir.getParentFile());
        } else {
            System.out.println("Determined project dir: "+currentDir);
            updateProjectDir(currentDir);
        }


        if(dirCompiler.listFiles() == null || dirCompiler.listFiles().length == 0){
            try{
                println(Ansi.Color.YELLOW, "Missing C compiler. Downloading and installing...");
                String url = "https://api.github.com/repos/mstorsjo/llvm-mingw/releases/latest";
                System.out.println("Source url: "+url);
                System.out.println("Destination: "+dirCompiler);
                JsonObject release = new JsonTools().getJsonObject(url);
                JsonArray assets = release.getAsJsonArray("assets");
                String downloadUrl = null;
                String downloadName = null;
                long downloadExpectedSize = 0;
                for (JsonElement el : assets) {
                    JsonObject obj = el.getAsJsonObject();
                    String name = obj.get("name").getAsString();
                    downloadName = name;
                    downloadExpectedSize = obj.get("size").getAsLong();
                    if(name.endsWith(".zip") && name.contains("ucrt")){
                        if(OSUtils.IS_WINDOWS){
                            if(name.contains("ucrt-x86_64")) {
                                downloadUrl = obj.get("browser_download_url").getAsString();
                                break;
                            }
                        } else if(OSUtils.IS_OSX){
                            if(name.contains("macos")) {
                                downloadUrl = obj.get("browser_download_url").getAsString();
                                break;
                            }
                        } else{
                            if(name.contains("ubuntu")) {
                                downloadUrl = obj.get("browser_download_url").getAsString();
                                break;
                            }
                        }
                    }
                }

                // Download and unpack the c compiler:
                Objects.requireNonNull(downloadUrl);
                BThreadManager manager = new BThreadManager();
                BThreadPrinter printer = new BThreadPrinter(manager);
                printer.start();
                File downloadDest = new File(dirCompiler+"/"+downloadName);
                DownloaderThread download = new DownloaderThread("Download", manager, downloadUrl, downloadDest);
                download.start();
                download.join();
                if(downloadDest.length() != downloadExpectedSize) throw new Exception("Failed download." +
                        " Expected download file size ("+downloadExpectedSize+") not equal to actual file size ("+downloadDest.length()+").");
                println("Unpacking (this may take a bit, don't abort)...");
                new ZipFile(downloadDest).extractAll(dirCompiler.getPath());
                downloadDest.delete();
                println("Successfully installed the C compiler.");

            } catch (Exception e) {
                throw new RuntimeException("Critical error during C compiler installation! Please report this issue.", e);
            }
        }

        new Thread(() -> {
            try{
                boolean exit = false;
                while(!exit){
                    String line = reader.readLine();
                    if(line.startsWith("help")){
                        System.out.println("help\n" +
                                "Info: Displays this.");
                        System.out.println("exit\n" +
                                "Info: Exits the program/CLI.");
                        System.out.println("build exe\n" +
                                "Info: Compiles and creates an executable from the A code inside project dir.");
                        System.out.println("build c\n" +
                                "Info: Creates C code from the A code inside project dir." +
                                "File will be written to: " + Main.fileSourceC);
                        System.out.println("set project dir <path>\n" +
                                "Info: Absolute or relative path to the directory containing A source code.");
                    }
                    else if(line.startsWith("exit")){
                        System.out.println("Exiting CLI...");
                        exit = true;
                    }
                    else if(line.startsWith("build-exe")){
                        System.out.println("Building executable for project: "+ Main.dirProject);
                        try{
                            buildExe();
                            System.out.println("Success!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("Failed. Details above.");
                        }
                    }
                    else if(line.startsWith("build-c")){
                        System.out.println("Creating C code for project: "+ Main.dirProject);
                        try{
                            buildC();
                            System.out.println("Success!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("Failed. Details above.");
                        }
                    }
                    else if(line.startsWith("set project dir ")){
                        int i = "set project dir ".length();
                        String path = line.substring(i+1);
                        updateProjectDir(new File(path));
                        System.out.println("Updated project dir: "+ new File(path));
                    } else{
                        System.err.println("Unknown command '"+line+"'. Enter 'help' for all commands.");
                    }
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
        if(fileSourceC.length() == 0)
            throw new Exception("Generated C code file is empty! File: " + fileSourceC);
    }

    private static void buildExe() throws Exception {
        // Final step: Create an executable from the generated assembly code.
        if (!fileSourceC.exists())
            throw new Exception("C code must have been generated before! Missing file: " + fileSourceC);
    }

    private static void updateProjectDir(File dir){
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
