package ru.ylab.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {
    private static final String LOG_FILE_PATH = "audit.log";

    /**
     * The `log` method records the provided action to the log file along with the current date and time.
     *
     * @param action The action to be logged in the audit log.
     */
    public static void log(String action) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = now.format(formatter);
            printWriter.println(formattedDate + " Action: " + action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
