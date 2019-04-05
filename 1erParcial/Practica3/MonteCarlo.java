/*---------------------------------------------------------
 * Práctica #3: Streams en Java
 * Fecha: 24-Feb-2019
 * Autores:
 *          A01370880 Rubén Escalante Chan
 *          A01377162 Guillermo Pérez Trueba
 *---------------------------------------------------------*/

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class StreamMonteCarlo {
       
    private static double mapper(int unused) {
        double x = ThreadLocalRandom.current().nextDouble() * 2 - 1;
        double y = ThreadLocalRandom.current().nextDouble() * 2 - 1;

        // Aplicar teorema de Pitágoras.
        return (x * x + y * y);
    }
    
    public static double sequentialMonteCarlo(int n) {
        long c = IntStream.range(0, n)
                .mapToDouble(StreamMonteCarlo::mapper)
                .filter(x -> x <= 1)
                .count();
                
        return 4 * ((double) c / n);
    }
    
    public static double parallelMonteCarlo(int n) {
        long c = IntStream.range(0, n)
                .parallel()
                .mapToDouble(StreamMonteCarlo::mapper)
                .filter(x -> x <= 1)
                .count();
        
        return 4 * ((double) c / n);
    }
    
    public static void main(String[] args) {
        
        final int N = 1_000_000;
        long inicio, fin;
        double result, t1, t8;
        
        // Secuencial
        inicio = System.nanoTime();
        result = sequentialMonteCarlo(N);
        fin = System.nanoTime();
        t1 = (fin - inicio) / 1.0e9;
        System.out.println("\nSecuencial");
        System.out.printf("Pi = %.10f T1 = %.2f%n", result, t1);
        
        // Paralelo
        inicio = System.nanoTime();
        result = sequentialMonteCarlo(N);
        fin = System.nanoTime();
        t8 = (fin - inicio) / 1.0e9;
        System.out.println("\nParalelo");
        System.out.printf("Pi = %.10f T1 = %.2f%n", result, t8);
        
        double speedup = t1 / t8;
        System.out.printf("\nSpeed Up = %.4f%n", speedup );
    }
    
}