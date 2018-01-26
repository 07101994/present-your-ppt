package com.arrowsappstudios.pptviewer.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tushar on 22-Jan-18.
 */

public class FileHelper implements IFileHelper {

    private static final FileHelper ourInstance = new FileHelper();

    public static FileHelper getInstance() {
        return ourInstance;
    }

    private FileHelper() {
    }

    /**
     * Copy file from input stream to the provided path
     *
     * @param inputStream
     * @param path
     * @throws IOException
     */
    @Override
    public void copyFile(InputStream inputStream, String path) throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = inputStream.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            out.close();
        }
    }
}
