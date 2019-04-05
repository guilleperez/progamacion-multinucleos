/*---------------------------------------------------------
 * Práctica #3: Streams en Java
 * Fecha: 24-Feb-2019
 * Autores:
 *          A01370880 Rubén Escalante Chan
 *          A01377162 Guillermo Pérez Trueba
 *---------------------------------------------------------*/

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class OrdenamientoPorConteo {
    
    private static final int NUM_RECTS = 100_000;
    private static int[]a, result;
    private static int n;
 
    private static void mapper(int i) {

        int count = 0;
        for (int j = 0; j < n; j++) {
            if (a[j] < a[i]) {
                count++;
            } else if (a[i] == a[j] && j < i) {
                count++;
            }
        }
        result[count] = a[i];
    }
    
    public static void parallelCountSort() {
        IntStream.range(0, n).parallel().forEach(OrdenamientoPorConteoStreams::mapper); //for (int i = 0; i < n; i++);
    }
    
    private static void sequentialCountSort() {
        IntStream.range(0, n).forEach(OrdenamientoPorConteoStreams::mapper); //for (int i = 0; i < n; i++);
    }
    
    public static void main(String[] args) {
        
        a = new int[NUM_RECTS];
        
        for (int i = 0; i < NUM_RECTS; i++) 
            a[i] = new Random().nextInt(100);
        
        n = a.length;

        result = new int[NUM_RECTS];
        long inicio = System.nanoTime();
        sequentialCountSort();
        long fin = System.nanoTime();
        double time = (fin - inicio)/1E9;
        System.out.printf("Array = %s T1 = %.4f%n", Arrays.toString(result), time); 
        
        result = new int[NUM_RECTS];
        inicio = System.nanoTime();
        parallelCountSort();
        fin = System.nanoTime();
        double time8 = (fin - inicio)/1E9;
        System.out.printf("Array = %s T8 = %.4f%n", Arrays.toString(result), time8);
        
    }    
}
