package M3;

import java.util.Scanner;

public class ScannerCloseIssue {
    public static void main(String[] args) {
        Scanner scanner1 = new Scanner(System.in);

        // First input works fine
        System.out.print("Enter your name: ");
        String name = scanner1.nextLine();
        System.out.println("Hello, " + name + "!");

        // Closing the first Scanner (which closes System.in)
        scanner1.close();

        // Attempting to read input again
        Scanner scanner2 = new Scanner(System.in);
        System.out.print("Enter your age: ");
        int age = scanner2.nextInt(); // This will fail after scanner1.close()
        System.out.println("You are " + age + " years old.");

        scanner2.close(); // This line may not even execute
    }
}
