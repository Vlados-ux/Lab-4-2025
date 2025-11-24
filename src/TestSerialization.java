import functions.*;
import functions.basic.*;
import java.io.*;

public class TestSerialization {
    private static final double EPSILON = 1e-10;

    public static void main(String[] args) {
        System.out.println("Тестирование сериализации\n");

        testSerializableVsExternalizable();

        System.out.println("\nТестирование завершено");
    }

    private static void testSerializableVsExternalizable() {
        System.out.println("Создание и сериализация ln(exp(x)) на [0, 10] с 11 точками:");

        try {
            Exp exp = new Exp();
            Log log = new Log(Math.E);
            Function composition = Functions.composition(exp, log);

            FunctionPoint[] points = new FunctionPoint[11];
            double step = 10.0 / 10;

            System.out.println("Создаем точки функции ln(exp(x)) = x:");
            for (int i = 0; i < 11; i++) {
                double x = i * step;
                double y = composition.getFunctionValue(x);
                points[i] = new FunctionPoint(x, y);
                System.out.printf("  Точка %2d: (%.1f, %.1f)%n", i, x, y);
            }

            System.out.println("\nСоздание ArrayTabulatedFunction:");
            ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(points);
            System.out.println("Array функция создана, точек: " + arrayFunc.getPointsCount());

            System.out.println("Создание LinkedListTabulatedFunction:");
            LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(points);
            System.out.println("LinkedList функция создана, точек: " + linkedListFunc.getPointsCount());

            System.out.println("\nТест: ArrayTabulatedFunction");
            testSerialization(arrayFunc, "array_serializable.dat");

            System.out.println("\nТест: LinkedListTabulatedFunction");
            testSerialization(linkedListFunc, "linkedlist_externalizable.dat");

            compareFiles();

        } catch (Exception e) {
            System.out.println("Ошибка при тестировании: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSerialization(TabulatedFunction function, String filename) {
        try {
            String typeName = function.getClass().getSimpleName();
            System.out.println(typeName + " - Сериализация в файл: " + filename);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(function);
            }

            System.out.println(typeName + " - Десериализация из файла: " + filename);

            TabulatedFunction deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                deserialized = (TabulatedFunction) ois.readObject();
            }

            System.out.println(typeName + " - Проверка корректности данных");
            boolean allMatch = true;
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint origPoint = function.getPoint(i);
                FunctionPoint restPoint = deserialized.getPoint(i);

                boolean matches = Math.abs(origPoint.getX() - restPoint.getX()) < EPSILON &&
                        Math.abs(origPoint.getY() - restPoint.getY()) < EPSILON;
                allMatch &= matches;

                if (!matches) {
                    System.out.printf("  Ошибка в точке %d: (%.1f,%.1f) vs (%.1f,%.1f)%n",
                            i, origPoint.getX(), origPoint.getY(), restPoint.getX(), restPoint.getY());
                }
            }

            File file = new File(filename);
            System.out.println(typeName + " - Результат: " + (allMatch ? "Успешно" : "Ошибка"));
            System.out.println(typeName + " - Размер файла: " + file.length() + " байт");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void compareFiles() {
        System.out.println("\nСравнение размеров файлов");

        File arrayFile = new File("array_serializable.dat");
        File linkedFile = new File("linkedlist_externalizable.dat");

        if (arrayFile.exists() && linkedFile.exists()) {
            long arraySize = arrayFile.length();
            long linkedSize = linkedFile.length();

            System.out.println("ArrayTabulatedFunction: " + arraySize + " байт");
            System.out.println("LinkedListTabulatedFunction: " + linkedSize + " байт");

            long difference = arraySize - linkedSize;
            System.out.println("Разница: " + Math.abs(difference) + " байт");

            if (difference > 0) {
                System.out.println("Вывод: LinkedList (Externalizable) эффективнее на " + difference + " байт");
            } else if (difference < 0) {
                System.out.println("Вывод: Array (Serializable) эффективнее на " + Math.abs(difference) + " байт");
            } else {
                System.out.println("Вывод: Оба метода одинаково эффективны");
            }
        } else {
            System.out.println("Файлы не найдены!");
        }
    }
}