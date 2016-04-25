package sample;

/**
 * Created by Max on 21.04.2016.
 */
public class Entry {
    private final String text;
    private final int amount;
    private final EntryType type;

    public Entry(int amount, String text, EntryType type) {
        this.amount = amount;
        this.text = text;
        this.type = type;
    }

    public String toString() {
        return text + " | " + amount;
    }


    public enum EntryType {
        DEPOSIT,
        WITHDRAW;
    }
}
