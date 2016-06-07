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
    Contructor which creates the necessary components.
    Each letter is mapped to the given number in the Mnemoics Map
    and saved in a new Map called CharCode
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
    This function maps all given strings from the word List
    into a Map<Integer, List<String>> by using the
    createNumFromWord function.
    The key is the number that represents the String
     */
    public void mapWordsToNums() {

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
    function that returns the number that represents the given string
    by maping each letter to the specified number in the CharCode Map
     */
    public int createNumFromWord(String string) {

        string = string.toUpperCase();
        String[] stringSplit = string.split("");
        String value = "";
        for (String s : stringSplit) {
            value += charCode.get(s);
        }
        return Integer.parseInt(value);

    }


    /*
    return all ways to encode a number as a list of words
    in the words that are given from the List<String> Words
     */
    public List<String> encode(String number) {

        List<String> list = new ArrayList<>();
        if (wordCode.containsKey(Integer.parseInt(number))) {
            list = wordCode.get(Integer.parseInt(number));
        }
        return list;

    }


    public static void main(String[] args) {
        List<String> test = new ArrayList<String>();
        test.add("Java");
        test.add("Lava");
        test.add("Max");
        Coder coder = new Coder(test);
        System.out.println(coder.encode("5282"));
    }
}