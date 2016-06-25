import java.math.BigInteger;
import java.util.Calendar;
import java.util.concurrent.RecursiveTask;

import org.apfloat.Apfloat;
import org.apfloat.ApintMath;

public class EulerTask extends RecursiveTask<Apfloat> {

    private static final long serialVersionUID = 6280257936325326701L;
    private int precision;
    private BigInteger firstTerm;
    private BigInteger lastTerm;
    private Apfloat lastSum;
    private int threadIndex;
    private boolean quietState;

    public EulerTask(int precision, BigInteger startTerm,
                     BigInteger lastTerm, int threadIndex, boolean quietState) {
        this.precision = precision;
        this.firstTerm = startTerm;
        this.lastTerm = lastTerm;
        this.threadIndex = threadIndex;
        this.quietState = quietState;
    }

    @Override
    protected Apfloat compute() {

        if (!quietState) {
            System.out.printf("Thread %d start executing terms in [%s, %s).\n",
                    threadIndex, firstTerm.toString(), lastTerm.toString());
        }

        long startOfExecutionTime = Calendar.getInstance().getTimeInMillis();

        Apfloat eulerNumber = new Apfloat(0, precision);

        Apfloat num = new Apfloat(BigInteger.valueOf(2)
                .multiply(firstTerm).add(BigInteger.ONE), precision);
        BigInteger factorial = ApintMath.factorial(2*firstTerm.intValue()).toBigInteger();
        Apfloat denum = new Apfloat(factorial, precision);

        eulerNumber = eulerNumber.add(num.divide(denum));

        for (BigInteger term = firstTerm.add(BigInteger.ONE); term
                .compareTo(lastTerm) < 0; term = term.add(BigInteger.ONE)) {

            num = num.add(new Apfloat(2, precision));

            denum = denum
                    .multiply(new Apfloat((term.multiply(BigInteger.valueOf(2))
                            .subtract(BigInteger.ONE)).multiply(term
                            .multiply(BigInteger.valueOf(2))), precision));

            eulerNumber = eulerNumber.add(num.divide(denum));

        }

        num = num.add(new Apfloat(2, precision));
        denum = denum
                .multiply(new Apfloat((lastTerm.multiply(BigInteger.valueOf(2))
                        .subtract(BigInteger.ONE)).multiply(lastTerm
                        .multiply(BigInteger.valueOf(2))), precision));

        lastSum = eulerNumber.add(num.divide(denum));

        long endOfExecutionTime = Calendar.getInstance().getTimeInMillis();

        if (!quietState) {
            System.out.printf("Thread %d execution time: %d ms.\n", threadIndex,
                    (endOfExecutionTime - startOfExecutionTime));
        }

        return eulerNumber;
    }

    public Apfloat getLastSum() {
        return lastSum;
    }

}
