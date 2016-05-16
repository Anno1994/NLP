import java.util.Arrays;

public class RadixSort {

    //sequential
    public static int[] sequentialRadixSort(int[] a) {
        int RADIX = 1;
        for (int bits = 0; bits < 32; bits += RADIX) {
            int[] sortedData = new int[a.length];

            // 1st step: Calculate histogram with RADICES entries (RADICES = 1<<RADIX)
            int[] histogram = new int[2];
            for (int i = 0; i < a.length; i++) {
                int digit = a[i] & 1 << bits;
                digit = digit >= 1 ? 1 : 0;
                histogram[digit]++;
            }

            // 2nd step: Prescan the histogram bucket */
            int sum = 0;
            for (int i = 0; i < histogram.length; ++i) {
                int val = histogram[i];
                sum += val;
                histogram[i] = sum;
            }

            // 3rd step: Rearrange the elements based on prescaned histogram */
            for (int i = a.length - 1; i >= 0; i--) {
                int digit = a[i] & 1 << bits;
                digit = digit >= 1 ? 1 : 0;
                int sortedPos = --histogram[digit];
                sortedData[sortedPos] = a[i];
            }
            a = sortedData;
        }
        return a;
    }

    //parallel

    // zu a.)
    // Die Parallelisierung des ersten Schritts würde darin bestehen, das zu sortierende Array
    // in mehrere Bereiche aufzuteilen, und diese von Threads zur gleichen Zeit analysieren zu
    // lassen. Hierbei ist zu beachten, dass es ein Synchronisations-Mechanismus für das Schreiben
    // der Werte ins Histogramm-Array zu verwenden ist.
    // Falls man shared-Memory einsetzt, sollte dies dazu führen, dass eine Parallelisierung
    // von Schritt 1 nur zu wenig Performance-Gewinn bringt, da das Histogramm-Array nur 2 Einträge hat und
    // deshalb maximal 2 Threads zur gleichen Zeit schreiben könnten.
    // Falls man jeder Thread sein eigenes Array hätte, könnten alle zur selben Zeit schreiben, und der
    // Master-Thread könnte alle Teil-Ergebnise am Ende zu einem GesamtErgebnis zusammenfügen.

    // zu b.)
    // Schritt 2 lässt sich nicht parallelisierten, da die Werte aufaddier werden, sind diese
    // stets vom nächsten Wert abhängig und können so nicht auf mehrere Threads verteilt werden.
    // Außerdem hat das Histogramm (weil auf binärer Ebene geprüft wird) nur zwei Einträge: 0, 1

    // zu c.)
    //

    public static int[] parallelRadixSort(int[] a) {
        int RADIX = 1;
        for (int bits = 0; bits < 32; bits += RADIX) {
            int[] sortedData = new int[a.length];

            int[] histogram = new int[2];
            for (int i = 0; i < a.length; i++) {
                int digit = a[i] & 1 << bits;
                digit = digit >= 1 ? 1 : 0;
                histogram[digit]++;
            }

            int sum = 0;
            for (int i = 0; i < histogram.length; ++i) {
                int val = histogram[i];
                sum += val;
                histogram[i] = sum;
            }

            for (int i = a.length - 1; i >= 0; i--) {
                int digit = a[i] & 1 << bits;
                digit = digit >= 1 ? 1 : 0;
                int sortedPos = --histogram[digit];
                sortedData[sortedPos] = a[i];
            }
            a = sortedData;
        }
        return a;
    }

    public static void main(String[] args) {
        int[] ints = {1, 2, 3, 4, 6, 7, 5, 9};
        int[] sorted = sequentialRadixSort(ints);
        System.out.println(Arrays.toString(sorted));
    }
}
