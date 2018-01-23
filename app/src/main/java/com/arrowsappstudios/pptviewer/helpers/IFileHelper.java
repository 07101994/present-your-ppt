package com.arrowsappstudios.pptviewer.helpers;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tushar on 22-Jan-18.
 */

public interface IFileHelper {
    /**
     * Copy file from input stream to the provided path
     * @param inputStream
     * @param path
     * @throws IOException
     */
    void CopyFile(InputStream inputStream, String path) throws IOException;
}
