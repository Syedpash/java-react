public class Programs {

    public void evenNumbers() {
        for (int i = 0; i <= 20; i++) {
            if ((i % 2) == 0) {
                System.out.println(i);
            }
        }
    }

    public static String evenOdd(int num) {
        if ((num % 2) == 0) {
            return "Even";
        } else {
            return "Odd";
        }
    }

    public static int getMax(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public static void main(String[] args) {

    }
}