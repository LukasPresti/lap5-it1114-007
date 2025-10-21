package M3;
// UCID: lap5
// Date: 2025-10-13
public class CommandLineCalculator extends BaseClass {
    private static String ucid = "lap5"; 

    public static void main(String[] args) {
        printHeader(ucid, 1, "Objective: Implement a calculator using command-line arguments.");

        if (args.length != 3) {
            System.out.println("Usage: java M3.CommandLineCalculator <num1> <operator> <num2>");
            printFooter(ucid, 1);
            return;
        }

        String num1Str = args[0];
        String operator = args[1];
        String num2Str = args[2];

        try {
            double num1 = Double.parseDouble(num1Str);
            double num2 = Double.parseDouble(num2Str);
            double result;

            // Determine operator
            if (operator.equals("+")) {
                result = num1 + num2;
            } else if (operator.equals("-")) {
                result = num1 - num2;
            } else {
                System.out.println("Error: Unsupported operator. Only + and - are allowed.");
                printFooter(ucid, 1);
                return;
            }

            // Determine number of decimal places
            int decimalPlaces = 0;
            if (num1Str.contains(".") || num2Str.contains(".")) {
                int dec1 = num1Str.contains(".") ? num1Str.split("\\.")[1].length() : 0;
                int dec2 = num2Str.contains(".") ? num2Str.split("\\.")[1].length() : 0;
                decimalPlaces = Math.max(dec1, dec2);
            }

            // Print formatted result
            System.out.printf("%." + decimalPlaces + "f\n", result);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please ensure correct format and valid numbers.");
        }

        printFooter(ucid, 1);
    }
}
