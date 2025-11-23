import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;
import java.util.*;

public class TestMain {
    private static final double EPSILON = 1e-10;
    private static final double PI = Math.PI;

    public static void main(String[] args) {
        System.out.println("Тестирование\n");

        testBasicFunctions();
        testTabulatedFunctions();
        testMetaFunctions();
        testInputOutput();

        System.out.println("\nКонец тестирования");
    }

    private static void testBasicFunctions() {
        System.out.println("Тестирование базовых функций:");

        Sin sin = new Sin();
        Cos cos = new Cos();

        System.out.println("Синус и косинус на [0, π]:");
        for (double x = 0; x <= PI + EPSILON; x += 0.1) {
            System.out.printf("sin(%.1f) = %.4f, cos(%.1f) = %.4f%n",
                    x, sin.getFunctionValue(x), x, cos.getFunctionValue(x));
        }

        Exp exp = new Exp();
        Log log = new Log(Math.E);

        System.out.println("\nЭкспонента и натуральный логарифм:");
        for (double x = 0.5; x <= 3.0; x += 0.5) {
            System.out.printf("exp(%.1f) = %.4f, ln(%.1f) = %.4f%n",
                    x, exp.getFunctionValue(x), x, log.getFunctionValue(x));
        }
    }

    private static void testTabulatedFunctions() {
        System.out.println("\nТестирование табулированных функций:");

        Sin sin = new Sin();
        Cos cos = new Cos();

        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, PI, 10);

        System.out.println("Сравнение исходного и табулированного синуса:");
        for (double x = 0; x <= PI + EPSILON; x += 0.1) {
            double original = sin.getFunctionValue(x);
            double tabulated = tabulatedSin.getFunctionValue(x);
            double error = Math.abs(original - tabulated);
            System.out.printf("x=%.1f: исходный=%.4f, табулированный=%.4f, ошибка=%.6f%n",
                    x, original, tabulated, error);
        }

        Function sumOfSquares = Functions.sum(
                Functions.power(tabulatedSin, 2),
                Functions.power(tabulatedCos, 2)
        );

        System.out.println("\nСумма квадратов табулированных синуса и косинуса:");
        for (double x = 0; x <= PI + EPSILON; x += 0.1) {
            double value = sumOfSquares.getFunctionValue(x);
            System.out.printf("x=%.1f: sin²+cos²=%.4f (теоретически=1.0000)%n", x, value);
        }

        System.out.println("\nИсследование влияния количества точек:");
        for (int points : new int[]{5, 10, 20, 50}) {
            TabulatedFunction sinDense = TabulatedFunctions.tabulate(sin, 0, PI, points);
            TabulatedFunction cosDense = TabulatedFunctions.tabulate(cos, 0, PI, points);
            Function sumSquaresDense = Functions.sum(
                    Functions.power(sinDense, 2),
                    Functions.power(cosDense, 2)
            );
            double maxError = 0;
            for (double x = 0; x <= PI; x += 0.1) {
                double error = Math.abs(1.0 - sumSquaresDense.getFunctionValue(x));
                if (error > maxError) maxError = error;
            }
            System.out.printf("Точек: %d, максимальная ошибка: %.6f%n", points, maxError);
        }
    }

    private static void testMetaFunctions() {
        System.out.println("\nТестирование meta функций:");

        Sin sin = new Sin();
        Cos cos = new Cos();

        Function composition = Functions.composition(cos, sin);
        System.out.println("Композиция sin(cos(x)):");
        for (double x = 0; x <= PI; x += 0.5) {
            System.out.printf("x=%.1f: sin(cos(x))=%.4f%n", x, composition.getFunctionValue(x));
        }

        Function sum = Functions.sum(sin, cos);
        System.out.println("\nСумма sin(x) + cos(x):");
        for (double x = 0; x <= PI; x += 0.5) {
            System.out.printf("x=%.1f: sin+cos=%.4f%n", x, sum.getFunctionValue(x));
        }

        Function scaled = Functions.scale(sin, 2, 3);
        System.out.println("\nМасштабированный синус 3*sin(2x):");
        for (double x = 0; x <= PI; x += 0.5) {
            System.out.printf("x=%.1f: 3*sin(2x)=%.4f%n", x, scaled.getFunctionValue(x));
        }
    }

    private static void testInputOutput() {
        System.out.println("\nТестирование ввода-вывода:");

        try {
            testByteStreams();
            testCharacterStreams();
        } catch (Exception e) {
            System.out.println("Ошибка при тестировании ввода/вывода: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testByteStreams() throws IOException {
        System.out.println("Байтовые потоки (экспонента):");

        Exp exp = new Exp();
        TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);

        try (FileOutputStream fos = new FileOutputStream("exp_binary.dat")) {
            TabulatedFunctions.outputTabulatedFunction(tabulatedExp, fos);
        }

        TabulatedFunction readExp;
        try (FileInputStream fis = new FileInputStream("exp_binary.dat")) {
            readExp = TabulatedFunctions.inputTabulatedFunction(fis);
        }

        System.out.println("Сравнение исходной и прочитанной экспоненты:");
        boolean allMatch = true;
        for (double x = 0; x <= 10; x += 1) {
            double original = tabulatedExp.getFunctionValue(x);
            double read = readExp.getFunctionValue(x);
            boolean matches = Math.abs(original - read) < EPSILON;
            allMatch &= matches;
            System.out.printf("x=%.1f: исходная=%.4f, прочитанная=%.4f, %b%n",
                    x, original, read, matches);
        }
        System.out.println("Все значения совпадают: " + allMatch);

        System.out.println("\nРазмер бинарного файла: " + new File("exp_binary.dat").length() + " байт");
    }

    private static void testCharacterStreams() throws IOException {
        System.out.println("\nСимвольные потоки (логарифм):");

        Log log = new Log(Math.E);
        TabulatedFunction tabulatedLog = TabulatedFunctions.tabulate(log, 0.1, 10, 11);

        try (FileWriter fw = new FileWriter("log_text.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabulatedLog, fw);
        }

        TabulatedFunction readLog;
        try (FileReader fr = new FileReader("log_text.txt")) {
            readLog = TabulatedFunctions.readTabulatedFunction(fr);
        }

        System.out.println("Сравнение исходного и прочитанного логарифма:");
        boolean allMatch = true;
        for (double x = 0.1; x <= 10; x += 1) {
            double original = tabulatedLog.getFunctionValue(x);
            double read = readLog.getFunctionValue(x);
            boolean matches = Math.abs(original - read) < EPSILON;
            allMatch &= matches;
            System.out.printf("x=%.1f: исходный=%.4f, прочитанный=%.4f, %b%n",
                    x, original, read, matches);
        }
        System.out.println("Все значения совпадают: " + allMatch);

        File textFile = new File("log_text.txt");
        System.out.println("Размер текстового файла: " + textFile.length() + " байт");
        System.out.println("Содержимое файла:");
        try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
            }
        }
    }
}