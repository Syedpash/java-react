import java.util.Arrays;
import java.util.List;

public class Streams {
    public static void main(String[] args) {
        List<String> words = Arrays.asList(
                "java", "stream", "lambda", "generics", "collections", "interface", "abstract");
        words.stream()
                .filter(word -> word.length() > 6)
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .sorted()
                .forEach(word -> System.out.println(word));
    }
}
