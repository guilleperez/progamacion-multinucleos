/*---------------------------------------------------------
 * Práctica #2: Fork/Join en Java
 * Fecha: 17-Feb-2019
 * Autores:
 *          A01370880 Rubén Escalante Chan
 *          A01377162 Guillermo Pérez Trueba
 *---------------------------------------------------------*/

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

public class MonteCarlo {

    public static class MonteCarloTask extends RecursiveTask<Double> {
        
        public static final long UMBRAL = 100_000;
        private final int n;
        private double c;

        public MonteCarloTask(int n) {
            this.n = n;
            this.c = 0.0;
        }

        @Override
        protected Double compute() {
            if (n <= UMBRAL) {
                for (int i = 0; i < n; i++) {
                    // Generar dos números aleatorios entre -1 y 1.
                    double x = ThreadLocalRandom.current().nextDouble() * 2 - 1;
                    double y = ThreadLocalRandom.current().nextDouble() * 2 - 1;

                    // Aplicar teorema de Pitágoras.
                    double h = x * x + y * y;

                    // Verificar si el tiro cayó dentro del círculo.
                    if (h <= 1) {
                        this.c++;
                    }
                }
                return this.c;
            } else {
                MonteCarloTask t1 = new MonteCarloTask(n / 2);
                MonteCarloTask t2 = new MonteCarloTask(n / 2);
                t1.fork();
                double r2 = t2.compute();
                double r1 = t1.join();
                return r1 + r2;
            }
        }
        
    } 
    
    public static double parallelMonteCarlo(int n) {
        ForkJoinPool pool = new ForkJoinPool();
        MonteCarloTask t = new MonteCarloTask(n);
        double c = pool.invoke(t);
        return 4 * (c / n);
    }
    
    public static double sequentialMonteCarlo(int n) {
        int c = 0;
        for (int i = 0; i < n; i++) {
            // Generar dos números aleatorios entre -1 y 1.
            double x = ThreadLocalRandom.current().nextDouble() * 2 - 1;
            double y = ThreadLocalRandom.current().nextDouble() * 2 - 1;

            // Aplicar teorema de Pitágoras.
            double h = x * x + y * y;

            // Verificar si el tiro cayó dentro del círculo.
            if (h <= 1) {
                c++;
            }
        }
        return 4 * ((double) c / n);
    }
    
    public static void main(String[] args) {
        
        final int N = 1_000_000_000;
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
