package ui;

import java.util.Scanner;

public class ConsoleUtil {
    private ConsoleUtil(){}

    public static String safeReadLine(Scanner scanner, String prompt) {
        System.out.println(prompt);
        if(!scanner.hasNextLine()){
            System.out.println("No input available.");
            return null;
        }

        return scanner.nextLine();
    }
}
