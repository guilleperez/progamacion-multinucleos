/*---------------------------------------------------------
 * Práctica #1: Calculando Pi con threads en Java
 * Fecha: 27-Ene-2019
 * Autores:
 *          A01370880 Rubén Escalante Chan
 *          A01377162 Guillermo Pérez Trueba
 *---------------------------------------------------------*/

import java.math.BigDecimal;

public class PiParalelo implements Runnable{
    
    private int start, upperLimit;
    private BigDecimal sum = new BigDecimal(0);
    
        @Override
    public void run() {
        double res = 0;
        double a, b, c, d;
        for (int i = start; i <= upperLimit; i++) {
            a = 4.0 / ((8.0 * i) + 1.0);
            b = 2.0 / ((8.0 * i) + 4.0);
            c = 1.0 / ((8.0 * i) + 5.0);
            d = 1.0 / ((8.0 * i) + 6.0);
            res = (1 / Math.pow(16, i)) * (a - b - c - d);
            
            sum = sum.add(BigDecimal.valueOf(res));
        }
    }

    public PiParalelo(int start, int upperLimit) {
        this.start = start;
        this.upperLimit = upperLimit;
    }
    
    public static BigDecimal calcularPi(int n) {
        PiParalelo p1 = new PiParalelo(0, n / 2);
        PiParalelo p2 = new PiParalelo(n / 2 + 1, n);
        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);  
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e){
            //Nunca pasa        
        }
        
        return p1.sum.add(p2.sum);
    }
    public static void main(String[] args) {
        long inicio = System.nanoTime();
        BigDecimal pi = calcularPi(1_000_000);
        long fin = System.nanoTime();
        System.out.printf("Bits = %d, ", pi.movePointRight(pi.precision() - 1).toBigInteger().bitCount());
        System.out.printf("Pi = %.8f, ", pi);
        System.out.printf("Time = %.4f\n", (fin - inicio) / 1E9);
    }
}
