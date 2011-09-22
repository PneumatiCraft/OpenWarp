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

    private PrintWriter out;

    public OWDebugHandler(String filename) {
        this(new File(filename));
    }
    
    public OWDebugHandler(File file) {
        super();

        try {
            this.out = new PrintWriter(new FileWriter(file));
        } catch(IOException e) {
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
