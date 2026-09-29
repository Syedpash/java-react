public class Variables {
    public static void main(String[] args) {
        int age = 28;
        double height = 5.9;
        char section = 'A';
        String name = "Syed";
        boolean isStudent = true;
        System.out.println("My name is " +name+ " Age " +age+ " Height " +height+ " Section " +section+ " " +isStudent);
        System.out.printf("My name is %s, Age %d, Height %.1f, Section %c%n", name, age, height, section);
    }
}