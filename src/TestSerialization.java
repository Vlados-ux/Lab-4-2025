import functions.*;
import functions.basic.*;
import java.io.*;

public class TestSerialization {
    private static final double EPSILON = 1e-10;

    public static void main(String[] args) {
        System.out.println("Тестирование сериализации\n");

        testSerializable();
        testExternalizable();
        compareFiles();

        System.out.println("\nТестирование завершено");
    }

    private static void testSerializable() {
        System.out.println("Тестирование Serializable:");

        try {
            Exp exp = new Exp();
            Log log = new Log(Math.E);
            Function composition = Functions.composition(exp, log);

            TabulatedFunction tabulated = TabulatedFunctions.tabulate(composition, 0, 10, 11);

            System.out.println("Исходная функция (ln(exp(x))):");
            for (int i = 0; i < tabulated.getPointsCount(); i++) {
                FunctionPoint point = tabulated.getPoint(i);
                System.out.printf("  (%.1f, %.1f)", point.getX(), point.getY());
            }
            System.out.println();

            System.out.println("Сериализация");
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("serializable.dat"))) {  // ← ИМЯ ФАЙЛА ИСПРАВЛЕНО
                oos.writeObject(tabulated);
            }

            System.out.println("Десериализация");
            TabulatedFunction deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("serializable.dat"))) {  // ← ИМЯ ФАЙЛА ИСПРАВЛЕНО
                deserialized = (TabulatedFunction) ois.readObject();
            }

            System.out.println("Сравнение значений:");
            boolean allMatch = true;
            for (double x = 0; x <= 10; x += 1) {
                double original = tabulated.getFunctionValue(x);
                double deser = deserialized.getFunctionValue(x);
                boolean matches = Math.abs(original - deser) < EPSILON;
                allMatch &= matches;
                System.out.printf("  x=%.1f: %.1f и %.1f - %s%n",
                        x, original, deser, matches ? "Совпало" : "ERROR");
            }

            System.out.println("Результат: " + (allMatch ? "Правильно" : "ОШИБКА"));
            System.out.println("Размер файла: " + new File("serializable.dat").length() + " байт");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testExternalizable() {
        System.out.println("\nТестирование Externalizable:");

        try {
            Sin sin = new Sin();
            TabulatedFunction tabulated = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);

            System.out.println("Исходная функция sin(x):");
            for (int i = 0; i < tabulated.getPointsCount(); i++) {
                FunctionPoint point = tabulated.getPoint(i);
                System.out.printf("  (%.2f, %.4f)", point.getX(), point.getY());
            }
            System.out.println();

            System.out.println("Сериализация с Externalizable");
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("externalizable.dat"))) {  // ← ИМЯ ФАЙЛА ИСПРАВЛЕНО
                oos.writeObject(tabulated);
            }

            System.out.println("Десериализация");
            TabulatedFunction deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("externalizable.dat"))) {  // ← ИМЯ ФАЙЛА ИСПРАВЛЕНО
                deserialized = (TabulatedFunction) ois.readObject();
            }

            System.out.println("Сравнение значений:");
            boolean allMatch = true;
            for (double x = 0; x <= Math.PI; x += 0.5) {
                double original = tabulated.getFunctionValue(x);
                double deser = deserialized.getFunctionValue(x);
                boolean matches = Math.abs(original - deser) < EPSILON;
                allMatch &= matches;
                System.out.printf("  x=%.1f: %.4f и %.4f - %s%n",
                        x, original, deser, matches ? "Совпало" : "ERROR");
            }

            System.out.println("Результат: " + (allMatch ? "Правильно" : "ОШИБКА"));
            System.out.println("Размер файла: " + new File("externalizable.dat").length() + " байт");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void compareFiles() {
        System.out.println("\nСравнение файлов:");

        File serFile = new File("serializable.dat");
        File extFile = new File("externalizable.dat");

        if (serFile.exists() && extFile.exists()) {
            System.out.println("Serializable: " + serFile.length() + " байт");
            System.out.println("Externalizable: " + extFile.length() + " байт");
            long difference = Math.abs(serFile.length() - extFile.length());
            System.out.println("Разница: " + difference + " байт");

            if (serFile.length() < extFile.length()) {
                System.out.println("Serializable создает меньшие файлы");
            } else if (serFile.length() > extFile.length()) {
                System.out.println("Externalizable создает меньшие файлы");
            } else {
                System.out.println("Файлы одинакового размера");
            }
        } else {
            System.out.println("Один или оба файла не созданы:");
            System.out.println("serializable.dat существует: " + serFile.exists());
            System.out.println("externalizable.dat существует: " + extFile.exists());
        }

        System.out.println("\nВсе файлы в директории:");
        File currentDir = new File(".");
        String[] files = currentDir.list();
        if (files != null) {
            for (String file : files) {
                if (file.endsWith(".dat") || file.endsWith(".txt")) {
                    File f = new File(file);
                    System.out.println("  " + file + " - " + f.length() + " байт");
                }
            }
        }
    }
}