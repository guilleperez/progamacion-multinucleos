/*---------------------------------------------------------
 * Práctica #1: Calculando Pi con threads en Java
 * Fecha: 27-Ene-2019
 * Autores:
 *          A01370880 Rubén Escalante Chan
 *          A01377162 Guillermo Pérez Trueba
 *---------------------------------------------------------*/

import java.lang.Math;
import java.math.BigDecimal;

public class PiSecuencial {

    public static BigDecimal calcularPi(int upperLimit) {
        BigDecimal sum = new BigDecimal(0);
        double res = 0;
        for (int i = 0; i <= upperLimit; i++) {
            double a, b, c, d;
            a = 4.0 / ((8.0 * i) + 1.0);
            b = 2.0 / ((8.0 * i) + 4.0);
            c = 1.0 / ((8.0 * i) + 5.0);
            d = 1.0 / ((8.0 * i) + 6.0);
            res = (1 / Math.pow(16, i)) * (a - b - c - d);
            
            sum = sum.add(BigDecimal.valueOf(res));
        }

        return sum;
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
