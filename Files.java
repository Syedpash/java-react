import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileReader;

public class Files {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        File file = new File("output.txt");
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter("output.txt")) {
                for (int i = 0; i < 3; i++) {
                    writer.write(input.nextLine() + "\n");
                }

            } catch (IOException e) {
                System.out.println("Write Error" + e.getMessage());
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                System.out.println(lineNumber + ". " + line);
            }
        } catch (IOException e) {
            System.out.println("Read Error" + e.getMessage());
        }
    }
}
