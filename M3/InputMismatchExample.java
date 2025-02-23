package M3;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputMismatchExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter a number: ");
            int number = scanner.nextInt(); // May throw InputMismatchException
            System.out.println("You entered: " + number);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a valid integer.");
            e.printStackTrace();
        } finally {
            scanner.close(); // Safe to close here since the program is terminating
        }
    }
}
