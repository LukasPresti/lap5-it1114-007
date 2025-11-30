package M3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


//UCID: lap5
//date: 2025-10-20
//Task: Challenge 3 - Mad Libs Generator

public class MadLibsGenerator extends BaseClass {
    private static final String STORIES_FOLDER = "M3/stories";
    private static String ucid = "lap5"; 

    public static void main(String[] args) {
        printHeader(ucid, 3,
                "Objective: Implement a Mad Libs generator that replaces placeholders dynamically.");

        Scanner scanner = new Scanner(System.in);
        File folder = new File(STORIES_FOLDER);

        if (!folder.exists() || !folder.isDirectory() || folder.listFiles().length == 0) {
            System.out.println("Error: No stories found in the 'stories' folder.");
            printFooter(ucid, 3);
            scanner.close();
            return;
        }

        List<String> lines = new ArrayList<>();

        File[] stories = folder.listFiles();
        Random rand = new Random();
        File storyFile = stories[rand.nextInt(stories.length)];

        System.out.println("Loaded story: " + storyFile.getName());

        try (Scanner fileScanner = new Scanner(storyFile)) {
            while (fileScanner.hasNextLine()) {
                lines.add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading story file: " + e.getMessage());
            printFooter(ucid, 3);
            scanner.close();
            return;
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            while (line.contains("<") && line.contains(">")) {
                int start = line.indexOf("<");
                int end = line.indexOf(">", start);

                if (start == -1 || end == -1) break;

                String placeholder = line.substring(start + 1, end);
                String displayPlaceholder = placeholder.replace("_", " ");

                System.out.print("Enter a(n) " + displayPlaceholder + ": ");
                String userWord = scanner.nextLine();

                line = line.substring(0, start) + userWord + line.substring(end + 1);
            }

            lines.set(i, line);
        }

        System.out.println("\nYour Completed Mad Libs Story:\n");
        StringBuilder finalStory = new StringBuilder();
        for (String line : lines) {
            finalStory.append(line).append("\n");
        }
        System.out.println(finalStory.toString());

        printFooter(ucid, 3);
        scanner.close();
    }
}
