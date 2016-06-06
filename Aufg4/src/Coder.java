import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by max on 06.06.16.
 */
public class Coder {
    private List<String> words = new ArrayList<>();
    private Map<Integer, String> mnemonics = new HashMap<>();
    private Map<String, Integer> charCode = new HashMap<>();
    private Map<Integer, ArrayList<String>> wordCode= new HashMap<>();


    /*
    * Contructor which creates the necessary components.
    * Each letter is mapped to the given number in the Mnemoics HashMap
    * and saved in a new HashMap called CharCode
     */
    public Coder(List<String> list) {
        words = list;
        mnemonics.put(2, "ABC");
        mnemonics.put(3, "DEF");
        mnemonics.put(4, "GHI");
        mnemonics.put(5, "JKL");
        mnemonics.put(6, "MNO");
        mnemonics.put(7, "PQRS");
        mnemonics.put(8, "TUV");
        mnemonics.put(9, "WXYZ");

        for (int i = 2; i <= 9; i++) {

            String[] tmp = mnemonics.get(i).split("");
            for (int j = 0; j < tmp.length; j++) {
                charCode.put(tmp[j], i);
            }

        }

        mapWordsToNums();

    }


    /*
    * This function maps all given strings from the word List
    * into a HashMap<Integer, ArrayList<String>> by using the
    * createNumFromWord function.
    * The KeyValue is the number that represents the String
     */
    public void mapWordsToNums() {                      //Mit Streams machbar ??

        for (String word : words) {
            int keyValue = createNumFromWord(word);
            if (wordCode.containsKey(keyValue)) {
                wordCode.get(keyValue).add(word);
            } else {
                ArrayList<String> list = new ArrayList<>();
                list.add(word);
                wordCode.put(keyValue, list);
            }
        }

    }


    /*
    *function that returns the number to a given string
    *by maping each letter to the specified number in the charCode Hashmap
     */
    public int createNumFromWord(String string) {

        string = string.toUpperCase();
        String[] splitted = string.split("");
        String value = "";
        for (String s : splitted) {
            value += charCode.get(s);
        }
        return Integer.parseInt(value);

    }

    public Map<Integer, ArrayList<String>> getMap() { return wordCode; }


    public static void main(String[] args) {
        List<String> test = new ArrayList<String>();
        test.add("Java");
        test.add("Lava");
        test.add("Max");
        Coder coder = new Coder(test);
        Map<Integer, ArrayList<String>> map = coder.getMap();
        System.out.println(map.toString());

    }
}
