package bank1;

/**
 * Created by Max on 21.04.2016.
 */
public class NumberGenerator {
    private volatile long current = 0;

    public synchronized long genNext() {
        return current++;
    }
}
