import java.util.Scanner;

public class Exceptions {

    public static int devide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot devide by zero");
        } else {
            return a / b;
        }
    }

    public static void main(String[] argc) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println(devide(6, 2));
            System.out.println(devide(6, 0));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        try {
            int x = Integer.parseInt(sc.nextLine());
            System.out.println(x);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number ");
        }
    }
}
