package com.osiris.a.utils;

import java.io.File;

public class Files {
    /**
     * Can convert relative paths to absolute ones.
     * Example allowed inputs: <br>
     * hello.txt (in current dir) <br>
     * ./hello.txt (.=current dir)<br>
     * ../john.txt (..=parent dir)<br>
     * .../john.txt (...=parent of parent dir)<br>
     * etc... <br>
     */
    public static final File toFile(String path) {
        if (path.startsWith(".")) {
            if (!path.contains("/") || !path.contains("\\")) {
                // Some files can look like this: ....hello.txt
                return new File(System.getProperty("user.dir") + "/" + path);
            }
            File parentDir = new File(System.getProperty("user.dir"));
            int i = 1;
            for (; i < path.length(); i++) {
                if (path.charAt(i) == '.') parentDir = parentDir.getParentFile();
                else break;
            }
            path = path.substring(i);
            if (path.startsWith("/") || path.startsWith("\\"))
                return new File(parentDir + path);
            else
                return new File(parentDir + "/" + path);
        } else if (path.contains("/") || path.contains("\\")) {
            // /files/hello.txt or C:\files\hello.txt
            return new File(path);
        } else {
            return new File(System.getProperty("user.dir") + "/" + path);
        }
    }
}
