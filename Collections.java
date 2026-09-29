import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class Collections {
    public static void main(String[] args) {

        HashMap<String, List<String>> subjects = new HashMap<>();
        subjects.put("Maths", new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie")));
        subjects.put("Science", new ArrayList<>(Arrays.asList("Bob", "David")));
        subjects.put("English", new ArrayList<>(Arrays.asList("Alice", "Charlie", "Eve")));

        TreeMap<String, List<String>> sortedSubjects = new TreeMap<>();
        for (String subject : subjects.keySet()) {
            System.out.println(subject + ": " + subjects.get(subject));

            if (subjects.get(subject).contains("Alice")) {
                System.out.println("Alice is studying " + subject + "\n");

            }

            sortedSubjects.put(subject, subjects.get(subject));

        }
        System.out.println(sortedSubjects);
        // ArrayList<Integer> numbers = new ArrayList<>();
        // numbers.add(10);
        // numbers.add(20);
        // numbers.add(30);
        // numbers.add(40);

        // int total = 0;
        // for (int i = 0; i < numbers.size(); i++) {
        // total += numbers.get(i);
        // }

        // System.out.println(total);

        // HashMap<String, String> countries = new HashMap<>();
        // countries.put("England", "London");
        // countries.put("India", "Delhi");
        // countries.put("Bangladesh", "Dhaka");

        // for (String country : countries.keySet()) {
        // System.out.println(country + " " + countries.get(country));
        // }

        // Scanner sc = new Scanner(System.in);
        // System.out.println("Enter a country");
        // String input = sc.nextLine();
        // System.out.println("Capital of " + input + " is " + countries.get(input));

    }
}
