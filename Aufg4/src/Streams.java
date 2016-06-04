import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Streams {

    /*
    Returns a list of strings containing all the words found in
    the file at the given path. The words are only recognized, when
    they are separated by either a comma or space.
    */
    public static List<String> getWords(String path) throws IOException {
        return Files.lines(Paths.get(path))                             //get lines
                .flatMap(line -> Arrays.stream(line.split("\\s|,")))    //split lines into words
                .filter(s -> s.length() > 0)                            //sort out empty arrays
                .collect(Collectors.toList());                          //collect words
    }

    /*
    Returns a Map containing all characters found inside the string-list,
    as keys and their occurrence-quantity as values.
    */
    public static Map<Character, Long> getCharOccurrenceMap(List<String> strings) {
        return strings.stream()                                         //turn list of strings to stream
                .reduce(String::concat)                                 //concat all strings via reduce
                .get()                                                  //get "big" string
                .chars()                                                //turn to IntStream (no idea why int...)
//                .filter(i -> i >= 'a'&&i <= 'z' || i >= 'A'&&i <= 'Z')  //filter out non-letter-characters
                .mapToObj(i -> (char) i)                                //map ints to chars
                .collect(Collectors.groupingBy(                         //create map:
                        Function.identity(),                            //every char becomes a key
                        Collectors.counting()));                        //its occurrence becomes its value
    }

    /*
    Returns how often a words represented by the 'lookFor'-String occurs or
    partially occurs inside a list of strings.
    */
    public static long getStringOccurrence(List<String> strings, String lookFor) {
        return strings.stream()                                         //turn list of strings to stream
                .filter(string -> string.contains(lookFor))             //filter out strings not containing 'lookFor'
                .count();                                               //count how many strings are left
    }


    public static void main(String[] args) {
        try {
            String path = Streams.class.getResource("test.txt").getPath();
            List<String> words = getWords(path);
            System.out.println(words);
            Map<Character, Long> map = getCharOccurrenceMap(words);
            System.out.println(map);
            System.out.println(getStringOccurrence(words, "test"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
