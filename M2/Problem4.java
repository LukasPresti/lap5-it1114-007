package M2;

public class Problem4 extends BaseClass {
    private static String[] array1 = { "hello world!", "java programming", "special@#$%^&characters", "numbers 123 456",
            "mIxEd CaSe InPut!" };
    private static String[] array2 = { "hello world", "java programming", "this is a title case test",
            "capitalize every word", "mixEd CASE input" };
    private static String[] array3 = { "  hello   world  ", "java    programming  ",
            "  extra    spaces  between   words   ",
            "      leading and trailing spaces      ", "multiple      spaces" };
    private static String[] array4 = { "hello world", "java programming", "short", "a", "even" };

    private static void transformText(String[] arr, int arrayNumber) {
        // Only make edits between the designated "Start" and "End" comments
        printArrayInfoBasic(arr, arrayNumber);

        // Challenge 1: Remove non-alphanumeric characters except spaces
        // Challenge 2: Convert text to Title Case
        // Challenge 3: Trim leading/trailing spaces and remove duplicate spaces
        // Result 1-3: Assign final phrase to `placeholderForModifiedPhrase`
        // Challenge 4 (extra credit): Extract up to middle 3 characters when possible (beginning starts at middle of phrase excluding the first and last characters),
        // assign to 'placeholderForMiddleCharacters'
        
        // if not enough characters assign "Not enough characters"
 
        // Step 1: sketch out plan using comments (include ucid and date)
        // Step 2: Add/commit your outline of comments (required for full credit)
        // Step 3: Add code to solve the problem (add/commit as needed)
        String placeholderForModifiedPhrase = "";
        String placeholderForMiddleCharacters = "";
        
        for(int i = 0; i <arr.length; i++){
            // Start Solution Edits
            // UCID: lap5, Date: 9/29/2025
            // Plan:
            // 1. Remove all non-alphanumeric characters except spaces using regex
            // 2. Convert to Title Case (capitalize first letter of each word)
            // 3. Trim leading/trailing spaces and replace multiple spaces with single space
            // 4. For extra credit, extract up to 3 middle characters if the phrase is long enough

            // Step 1-3: Clean and Title Case
            String cleaned = arr[i].replaceAll("[^a-zA-Z0-9 ]", "").trim().replaceAll("\\s+", " ");
            String[] words = cleaned.split(" ");
            StringBuilder titleCase = new StringBuilder();
            for(String w : words){
                if(w.length() > 0){
                    titleCase.append(Character.toUpperCase(w.charAt(0)))
                             .append(w.substring(1).toLowerCase())
                             .append(" ");
                }
        }
placeholderForModifiedPhrase = titleCase.toString().trim();

// Step 4: Extract middle 3 characters if possible
int len = placeholderForModifiedPhrase.length();
if(len >= 5){
    int mid = len / 2;
    int start = Math.max(1, mid - 1);
    int end = Math.min(len - 1, start + 3);
    placeholderForMiddleCharacters = placeholderForModifiedPhrase.substring(start, end);
}else{
    placeholderForMiddleCharacters = "Not enough characters";
}
// End Solution Edits

            System.out.println(String.format("Index[%d] \"%s\" | Middle: \"%s\"",i, placeholderForModifiedPhrase, placeholderForMiddleCharacters));
        }

       

        
        System.out.println("\n______________________________________");
    }

    public static void main(String[] args) {
        final String ucid = "lap5"; // <-- change to your UCID
        // No edits below this line
        printHeader(ucid, 4);

        transformText(array1, 1);
        transformText(array2, 2);
        transformText(array3, 3);
        transformText(array4, 4);
        printFooter(ucid, 4);
    }

}
