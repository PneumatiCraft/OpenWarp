package com.lithium3141.OpenWarp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Logging handler for custom debugging messages.
 */
public class OWDebugHandler extends Handler {

    /**
     * Output stream for printing debug information.
     */
    private PrintWriter out;

    /**
     * Construct a new debug logging handler that writes to the specified file.
     *
     * @param filename The file to open for writing debug information. Existing
     *                 file contents may be cleared, if the file exists already.
     */
    public OWDebugHandler(String filename) {
        this(new File(filename));
    }

    /**
     * Construct a new debug logging handler that writes to the specified file.
     *
     * @param file The File object to write to. Existing file contents may be
     *             cleared, if the file exists already.
     */
    public OWDebugHandler(File file) {
        super();

        try {
            this.out = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            OpenWarp.LOG.warning(OpenWarp.LOG_PREFIX + "Couldn't open debug log at " + file.getPath() + "; OpenWarp may not print debug messages.");
        }
    }

    @Override
    public void close() {
        this.flush();
        this.out.close();
        this.out = null;
    }

    @Override
    public void flush() {
        this.out.flush();
    }

    @Override
    public void publish(LogRecord record) {
        this.out.println(record.getMessage());
        this.flush();
    }
}
