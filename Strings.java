import java.util.Scanner;

public class Strings {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a string");
        String input = sc.nextLine();
        System.out.println(input.toUpperCase());
        System.out.println(input.length());
        System.out.println(input.split(" ").length);
        System.out.println(input.contains("java"));

        StringBuilder sb = new StringBuilder();
        for (int c = input.length() - 1; c >= 0; c--) {
            sb.append(input.charAt(c));
        }
        System.out.println(sb.toString());
    }
}
