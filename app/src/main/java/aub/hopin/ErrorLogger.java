package aub.hopin;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.util.Calendar;

public class ErrorLogger {
    private static final String logFileName = "log.txt";
    private static PrintWriter logFileWriter = null;

    // Ensure that the log file print writer exists.
    private static void ensureWriter() {
        if (logFileWriter == null) {
            try {
                logFileWriter = new PrintWriter(new File(logFileName));
            } catch (IOException ioe) {}
        }
    }

    public static void info(String msg) {
        logFileWriter.printf("[%tr] Info: %s\n", Calendar.getInstance(), msg);
    }

    public static void error(String msg) {
        logFileWriter.printf("[%tr] Error: %s\n", Calendar.getInstance(), msg);
    }
}
