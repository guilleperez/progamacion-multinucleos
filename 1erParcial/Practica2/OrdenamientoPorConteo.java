/*---------------------------------------------------------
 * Práctica #2: Fork/Join en Java
 * Fecha: 17-Feb-2019
 * Autores:
 *          A01370880 Rubén Escalante Chan
 *          A01377162 Guillermo Pérez Trueba
 *---------------------------------------------------------*/
 
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;


public class OrdenamientoPorConteo {
    private static final int NUM_RECTS = 100;
    public static class SortTask extends RecursiveAction {

        private static final long serialVersionUID = 1L; //permite revisar si son errores cmpatibles tras la deserelizacion

        public static final int UMBRAL = 1_000;
        private long lo, hi;
        private static int[] a, temp;
        public SortTask(long lo, long hi, int[] a){
            this.lo = lo;
            this.hi = hi;
            this.a = a;
        }

        @Override
        protected void compute() {
            if (hi - lo < UMBRAL) {
                int[] temp = new int[NUM_RECTS];
                for (int i = (int) lo; i <  hi; i++) {
                    int count = 0;
                    for (int j = (int) lo; j < hi; j++) {
                        if (a[j] < a[i]) {
                            count++;
                        } else if (a[i] == a[j] && j < i) {
                            count++;
                        }
                    }
                    temp[count + (int) lo] = a[i];
                }
                 System.arraycopy(temp, 0, a, 0, (int) hi);
            } else {

                long mid = (hi + lo) >>> 1;
                SortTask t2 = new SortTask(mid, hi, a);
                SortTask t1 = new SortTask(lo, mid, a);
                invokeAll(t2, t1);
            }
        }
    }
    
    public static void parallelCountSort(int[] a) {
        ForkJoinPool pool = new ForkJoinPool();
        SortTask t = new SortTask(0, NUM_RECTS, a);
        pool.invoke(t);
    }
    
    public static void sequentialCountSort(int a[]) {
        final int n = a.length;
        final int[] temp = new int[n];
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (a[j] < a[i]) {
                    count++;
                } else if (a[i] == a[j] && j < i) {
                    count++;
                }
            }
            temp[count] = a[i];
        }
        System.arraycopy(temp, 0, a, 0, n);

    }
    
    public static void main(String[] args) {
        int[] a = new int[NUM_RECTS];
        int[] b = new int[NUM_RECTS];
        for (int i = 0; i < NUM_RECTS; i++) {
            int rnd =  new Random().nextInt(100);
            a[i] = rnd;
            b[i] = rnd;
            
        }
  
        long inicio = System.nanoTime();
        sequentialCountSort(a);
        long fin = System.nanoTime();
        double time = (fin - inicio)/1E9;
        System.out.printf("Array = %s T1 = %.4f%n", Arrays.toString(a), time); 
        
        inicio = System.nanoTime();
        parallelCountSort(b);
        fin = System.nanoTime();
        double time8 = (fin - inicio)/1E9;
        System.out.printf("Array = %s T8 = %.4f%n", Arrays.toString(b), time8); 
        
        double speedup = time / time8;
        System.out.printf("Speed Up = %.4f%n", speedup );
   
    }    
}
