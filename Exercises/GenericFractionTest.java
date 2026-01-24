import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Scanner;

public class GenericFractionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double n1 = scanner.nextDouble();
        double d1 = scanner.nextDouble();
        float n2 = scanner.nextFloat();
        float d2 = scanner.nextFloat();
        int n3 = scanner.nextInt();
        int d3 = scanner.nextInt();
        try {
            GenericFraction<Double, Double> gfDouble = new GenericFraction<Double, Double>(n1, d1);
            GenericFraction<Float, Float> gfFloat = new GenericFraction<Float, Float>(n2, d2);
            GenericFraction<Integer, Integer> gfInt = new GenericFraction<Integer, Integer>(n3, d3);
            System.out.printf("%.2f\n", gfDouble.toDouble());
            System.out.println(gfDouble.add(gfFloat));
            System.out.println(gfInt.add(gfFloat));
            System.out.println(gfDouble.add(gfInt));
            gfInt = new GenericFraction<Integer, Integer>(n3, 0);
        } catch(ZeroDenominatorException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

}

// вашиот код овде
class GenericFraction<T extends Number, U extends Number> {

    private T numerator;
    private U denominator;

    public GenericFraction(T numerator, U denominator) throws ZeroDenominatorException {
        if (denominator.doubleValue() == 0) {
            throw new ZeroDenominatorException("Denominator cannot be zero");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public GenericFraction<Double, Double> add(
            GenericFraction<? extends Number, ? extends Number> gf) throws ZeroDenominatorException {

        double a = numerator.doubleValue();
        double b = denominator.doubleValue();
        double c = gf.numerator.doubleValue();
        double d = gf.denominator.doubleValue();

        double newNum = a * d + c * b;
        double newDen = b * d;

        return new GenericFraction<>(newNum, newDen);
    }

    public double toDouble() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    @Override
    public String toString() {
        double a = numerator.doubleValue();
        double b = denominator.doubleValue();

        // нормализација
        double gcd = gcd(a, b);
        a /= gcd;
        b /= gcd;

        return String.format("%.2f / %.2f", a, b);
    }

    private double gcd(double a, double b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            double tmp = b;
            b = a % b;
            a = tmp;
        }
        return a;
    }
}

class ZeroDenominatorException extends Exception {
    public ZeroDenominatorException(String message) {
        super(message);
    }
}