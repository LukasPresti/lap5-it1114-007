// UCID: lp55 - 10/20/2025
package M3;

import java.util.Random;
import java.util.Scanner;

public class SlashCommandHandler extends BaseClass {
    private static String ucid = "lap5"; 

    public static void main(String[] args) {
        printHeader(ucid, 2, "Objective: Implement a simple slash command parser.");

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();

            // skip empty input
            if (input.isEmpty()) {
                System.out.println("Error: No command entered.");
                continue;
            }

            // make it case-insensitive
            String lowerInput = input.toLowerCase();

            // /quit command
            if (lowerInput.equals("/quit")) {
                System.out.println("Exiting program...");
                break;
            }

            // /greet <name>
            else if (lowerInput.startsWith("/greet")) {
                String[] parts = input.split("\\s+", 2);
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    System.out.println("Error: Missing name. Usage: /greet <name>");
                } else {
                    System.out.println("Hello, " + parts[1].trim() + "!");
                }
            }

            // /roll <num>d<sides>
            else if (lowerInput.startsWith("/roll")) {
                String[] parts = input.split("\\s+", 2);
                if (parts.length < 2) {
                    System.out.println("Error: Missing dice format. Usage: /roll <num>d<sides>");
                    continue;
                }

                String diceFormat = parts[1].trim();
                if (!diceFormat.matches("\\d+d\\d+")) {
                    System.out.println("Error: Invalid format. Usage: /roll <num>d<sides> (e.g. /roll 2d6)");
                    continue;
                }

                try {
                    String[] diceParts = diceFormat.toLowerCase().split("d");
                    int num = Integer.parseInt(diceParts[0]);
                    int sides = Integer.parseInt(diceParts[1]);

                    if (num <= 0 || sides <= 0) {
                        System.out.println("Error: Dice numbers and sides must be positive integers.");
                        continue;
                    }

                    int total = 0;
                    for (int i = 0; i < num; i++) {
                        total += random.nextInt(sides) + 1;
                    }

                    System.out.println("Rolled " + num + "d" + sides + " and got " + total + "!");
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format.");
                }
            }

            // /echo <message>
            else if (lowerInput.startsWith("/echo")) {
                String[] parts = input.split("\\s+", 2);
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    System.out.println("Error: Missing message. Usage: /echo <message>");
                } else {
                    System.out.println(parts[1]);
                }
            }

            // unknown command
            else {
                System.out.println("Error: Unrecognized command.");
            }
        }

        printFooter(ucid, 2);
        scanner.close();
    }
}
