package sample;

import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by Max on 21.04.2016.
 */
public class Account {
    private final long id;
    private volatile int balance = 0;
    private volatile Date lastModified;
    private List<Entry> entries = new Vector<Entry>();

    public Account(long id) {
        this.lastModified = new Date();
        this.id = id;
    }

    public synchronized void deposit(int a, String text) {
        balance += a;
        entries.add(new Entry(a, text, Entry.EntryType.DEPOSIT));
        lastModified = new Date();
    }

    public synchronized void withdraw(int a, String text) {
        balance -= a;
        entries.add(new Entry(-a, text, Entry.EntryType.WITHDRAW));
        lastModified = new Date();
    }

    public int getBalance() {
        return balance;
    }

    public String toString() {
        String string = "Dies ist der Auszug für die KNr.:" + id;
        for (Entry entry : entries) {
            string += "\n" + entry.toString();
        }
        return string;
    }
}
