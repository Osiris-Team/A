package com.osiris.a.utils;

import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BWarning;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class DownloaderThread extends BThread {
    private String url;
    private File dest;
    private boolean ignoreContentType;
    private String[] allowedSubContentTypes;

    /**
     * Downloads a file from an url to the cache first and then
     * to its final destination.
     *
     * @param name    This processes name.
     * @param manager the parent process manager.
     * @param url     the download-url.
     * @param dest    the downloads final destination.
     */
    public DownloaderThread(String name, BThreadManager manager, String url, File dest) {
        this(name, manager, url, dest, false, (String[]) null);
    }

    public DownloaderThread(String name, BThreadManager manager, String url, File dest, boolean ignoreContentType, String... allowedSubContentTypes) {
        this(name, manager);
        this.url = url;
        this.dest = dest;
        this.ignoreContentType = ignoreContentType;
        this.allowedSubContentTypes = allowedSubContentTypes;
    }

    private DownloaderThread(String name, BThreadManager manager) {
        super(name, manager);
    }

    @Override
    public void runAtStart() throws Exception {
        super.runAtStart();

        final String fileName = dest.getName();
        setStatus("Downloading " + fileName + "... (0mb/0mb)");


        Request request = new Request.Builder().url(url)
                .header("User-Agent", "A-Compiler")
                .build();

        Response response = new OkHttpClient.Builder().followRedirects(true).build().newCall(request).execute();
        ResponseBody body = null;
        try {
            if (response.code() != 200)
                throw new Exception("Download of '" + dest.getName() + "' failed! Code: " + response.code() + " Message: " + response.message() + " Url: " + url);

            body = response.body();
            if (body == null)
                throw new Exception("Download of '" + dest.getName() + "' failed because of null response body!");
            else if (!ignoreContentType && body.contentType() == null)
                throw new Exception("Download of '" + dest.getName() + "' failed due to null content type!");
            else if (!ignoreContentType && !body.contentType().type().equals("application"))
                throw new Exception("Download of '" + dest.getName() + "' failed because of invalid content type: " + body.contentType().type());
            else if (!ignoreContentType && !body.contentType().subtype().equals("java-archive")
                    && !body.contentType().subtype().equals("jar")
                    && !body.contentType().subtype().equals("octet-stream")) {
                if (allowedSubContentTypes == null)
                    throw new Exception("Download of '" + dest.getName() + "' failed because of invalid sub-content type: " + body.contentType().subtype());
                if (!Arrays.asList(allowedSubContentTypes).contains(body.contentType().subtype()))
                    throw new Exception("Download of '" + dest.getName() + "' failed because of invalid sub-content type: " + body.contentType().subtype());
            }

            long completeFileSize = body.contentLength();
            setMax(completeFileSize);

            BufferedInputStream in = new BufferedInputStream(body.byteStream());
            FileOutputStream fos = new FileOutputStream(dest);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[1024];
            long downloadedFileSize = 0;
            int x = 0;
            while ((x = in.read(data, 0, 1024)) >= 0) {
                downloadedFileSize += x;

                setStatus("Downloading " + fileName + "... (" + downloadedFileSize / (1024 * 1024) + "mb/" + completeFileSize / (1024 * 1024) + "mb)");
                setNow(downloadedFileSize);

                bout.write(data, 0, x);
            }

            setStatus("Downloaded " + fileName + " (" + downloadedFileSize / (1024 * 1024) + "mb/" + completeFileSize / (1024 * 1024) + "mb)");
            bout.close();
            in.close();
            body.close();
            response.close();
        } catch (Exception e) {
            if (body != null) body.close();
            response.close();
            throw e;
        }
    }

    public boolean compareWithMD5(String expectedMD5) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(dest.toPath()));
        byte[] digest = md.digest();
        return bytesToHex(digest).equalsIgnoreCase(expectedMD5);
    }

    /**
     * Only use this method after finishing the download.
     * It will get the hash for the newly downloaded file and
     * compare it with the given hash.
     *
     * @param sha256
     * @return true if the hashes match
     */
    public boolean compareWithSHA256(String sha256) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    Files.readAllBytes(dest.toPath()));
            final String hashResult = bytesToHex(encodedhash);
            return hashResult.equals(sha256);
        } catch (Exception e) {
            getWarnings().add(new BWarning(this, e));
            return false;
        }

    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
