/*
 * Copyright (C) 2012 Department of Molecular Genetics, University of Toronto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.jweile.yogiutil;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public abstract class MainWrapper {
        
    private String logFileName;
    
    /**
     * Starts the main run routine
     * @param args 
     */
    public void start(String[] args) {
        try {
                        
            setupLogging();
            
            run(args);
            
        } catch (Throwable t) {
            processError(t);
        }
    }
        
    /**
     * compile error message and log it.
     * @param t the error
     */
    private void processError(Throwable t) {
        StringBuilder b = new StringBuilder(256);

        b.append(t.getMessage());
        Throwable cause = t;
        while ((cause = cause.getCause()) != null) {
            b.append("\nReason: ").append(cause.getMessage());
        }

        Logger.getAnonymousLogger().log(Level.SEVERE, b.toString(), t);
    }

    /**
     * simple CLI output + detailed log file.
     * @throws IOException if log file cannot be written.
     */
    private void setupLogging() throws IOException {
        //setup CLI output
        for (Handler h : Logger.getLogger("").getHandlers()) {
            if (h instanceof ConsoleHandler) {
                ConsoleHandler ch = ((ConsoleHandler)h);
                ch.setLevel(Level.INFO);
                ch.setFormatter(new Formatter() {

                    @Override
                    public String format(LogRecord lr) {

                        StringBuilder b = new StringBuilder();

                        b.append(lr.getLevel().toString())
                                .append(": ");

                        b.append(lr.getMessage())
                                .append("\n");

                        return b.toString();
                    }

                });
                break;
            }
        }
        
        //setup log file writer
        File logFile = new File(logFileName);
        FileHandler fh = new FileHandler(logFile.getAbsolutePath());
        fh.setLevel(Level.ALL);
        fh.setFormatter(new SimpleFormatter());
        Logger.getLogger("").addHandler(fh);
    }
    
    public abstract void run(String[] args);
    
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

}
