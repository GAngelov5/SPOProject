import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apfloat.Apfloat;

public class EulerCalculator {
    private static int precision;
    private static int numberOfThreads;
    private static String filename = "result.txt";
    private static boolean quietState = false;

    public static void main(String[] args) {

        long startTime = Calendar.getInstance().getTimeInMillis();

        try {
            parseArguments(args);
        } catch (Exception e) {
            System.out.println("Wrong format of options");
        }
        if (precision > 0 && numberOfThreads > 0) {

            BigInteger terms = BigInteger.valueOf(precision).divide(
                    BigInteger.valueOf(numberOfThreads));
            while (terms.compareTo(BigInteger.valueOf(100)) >= 0) {
                terms = terms.divide(BigInteger.TEN);
            }

            BigInteger currentTerm = BigInteger.ZERO;
            Apfloat eulerNumber = new Apfloat(0, precision);
            System.out.println("Workload: " + terms);

            while (true) {

                List<EulerTask> tasks = new ArrayList<>();

                for (int i = 0; i < numberOfThreads; i++) {
                    EulerTask task = new EulerTask(precision,
                            currentTerm, currentTerm.add(terms), i, quietState);
                    task.fork();

                    tasks.add(task);

                    currentTerm = currentTerm.add(terms);

                }

                for (EulerTask task : tasks) {
                    eulerNumber = eulerNumber.add(task.join());
                }

                int lastTaskIndex = tasks.size() - 1;
                Apfloat lastSum = tasks.get(lastTaskIndex).getLastSum();
                if (eulerNumber.equals(eulerNumber.add(lastSum))) {
                    break;
                }

            }

            writeToFile(eulerNumber.toString());
            long endTime = Calendar.getInstance().getTimeInMillis();
            System.out.printf("Execution time: %d ms.\n", endTime - startTime);

        } else {
            System.out.printf("Wrong arguments!");
        }

    }

    /**
     * Parse command line args.
     *
     * @param args args which are passed from the command line
     * @throws Exception throws if cannot parse args
     */
    private static void parseArguments(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                precision = new Integer(args[i + 1]);
            }
            if (args[i].equals("-t") || args[i].equals("-tasks")) {
                numberOfThreads = new Integer(args[i + 1]);
            }
            if (args[i].equals("-o")) {
                filename = args[i + 1];
            }
            if (args[i].equals("-q")) {
                quietState = true;
            }
        }
    }

    /**
     * Write result to file
     *
     * @param result euler numbers
     */
    private static void writeToFile(String result) {
        try (PrintStream file = new PrintStream(filename)) {
            file.println("Result: " + result);
        } catch (FileNotFoundException fnf) {
            System.out.println("File " + filename + " not found.");
        }
    }
}
