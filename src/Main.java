import functions.*;

public class Main {
    private static final double EPSILON = 1e-10;

    public static void main(String[] args) {
        System.out.println("Тестирование классов табулированной функции\n");

        testArrayTabulatedFunction();

        System.out.println("\n" + "=".repeat(80) + "\n");

        testLinkedListTabulatedFunction();

        System.out.println("\n" + "=".repeat(80) + "\n");

        testAllExceptions();
    }

    private static void testArrayTabulatedFunction() {
        System.out.println("Тестирование ArrayTabulatedFunction (f(x) = x + 5):");

        try {
            double[] values = new double[]{5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0};
            TabulatedFunction func = new ArrayTabulatedFunction(0.0, 10.0, values);

            System.out.printf("Область определения: [%.1f, %.1f]\n", func.getLeftDomainBorder(), func.getRightDomainBorder());
            System.out.println("Кол-во точек: " + func.getPointsCount());

            System.out.println("\nТочки табулированной функции:");
            for(int i = 0; i < func.getPointsCount(); ++i) {
                FunctionPoint p = func.getPoint(i);
                System.out.printf("Точка %d: (%.1f; %.1f)\n", i, p.getX(), p.getY());
            }

            System.out.println("\nВычисленные значения функции:");
            double[] testX = new double[]{-5.0, -2.0, 0.0, 1.0, 2.5, 3.0, 4.5, 5.0, 6.5, 7.0, 8.5, 10.0, 12.0, 15.0};
            for(double x : testX) {
                double y = func.getFunctionValue(x);
                double expected = x + 5.0;
                if (Double.isNaN(y)) {
                    System.out.printf("f(%.1f) = не определено (не входит в область определения)\n", x);
                } else {
                    System.out.printf("f(%.1f) = %.1f (должно быть: %.1f)\n", x, y, expected);
                }
            }

            System.out.println("\nПроверка модификации точек:");
            System.out.println("Исправление некорректного значения ординаты:");
            System.out.printf("До изменения: (%.1f; %.1f) ", func.getPointX(2), func.getPointY(2));

            func.setPointY(2, 100.0);
            System.out.printf("После установки 100: (%.1f; %.1f) ", func.getPointX(2), func.getPointY(2));

            double correctY = func.getPointX(2) + 5.0;
            func.setPointY(2, correctY);
            System.out.printf("После исправления: (%.1f; %.1f)\n", func.getPointX(2), func.getPointY(2));

            System.out.println("\nКорректное изменение абсциссы с обновлением Y:");
            System.out.printf("До: (%.1f; %.1f) ", func.getPointX(4), func.getPointY(4));
            double newX = 4.2;
            func.setPointX(4, newX);
            func.setPointY(4, newX + 5.0);
            System.out.printf("После: (%.1f; %.1f)\n", func.getPointX(4), func.getPointY(4));

            System.out.println("\nДобавление точек:");
            System.out.println("Количество точек до добавления: " + func.getPointsCount());
            double addX = 3.3;
            double addY = addX + 5.0;
            func.addPoint(new FunctionPoint(addX, addY));
            System.out.println("Количество точек после добавления: " + func.getPointsCount());
            System.out.printf("Значение в добавленной точке: f(%.1f) = %.1f\n", addX, func.getFunctionValue(addX));

            System.out.println("Точки после добавления:");
            for(int i = 0; i < func.getPointsCount(); ++i) {
                FunctionPoint p = func.getPoint(i);
                System.out.printf("(%.1f; %.1f) ", p.getX(), p.getY());
            }
            System.out.println();

            System.out.println("\nУдаление точек:");
            System.out.println("Количество точек до удаления: " + func.getPointsCount());
            func.deletePoint(2);
            System.out.println("Количество точек после удаления: " + func.getPointsCount());

            System.out.println("\nОбновление всех значений y в соответсвии с f(x) = x + 5:");
            for(int i = 0; i < func.getPointsCount(); ++i) {
                double x = func.getPointX(i);
                func.setPointY(i, x + 5.0);
                System.out.printf("Точка %d: (%.1f; %.1f)\n", i, x, func.getPointY(i));
            }

            System.out.println("\nИнтерполяция:");
            double[] interpolationX = new double[]{0.3, 1.1, 2.8, 3.9, 5.2, 6.7, 8.1, 9.4};
            for(double x : interpolationX) {
                double y = func.getFunctionValue(x);
                double expected = x + 5.0;
                boolean isCorrect = Math.abs(y - expected) < EPSILON;
                System.out.printf("f(%.1f) = %.1f (должно быть: %.1f) %s\n",
                        x, y, expected, isCorrect ? "Правильно" : "Не правильно");
            }

            System.out.println("\nГраницы:");
            System.out.printf("Левая граница: f(%.1f) = %.1f\n",
                    func.getLeftDomainBorder(), func.getFunctionValue(func.getLeftDomainBorder()));
            System.out.printf("Правая граница: f(%.1f) = %.1f\n",
                    func.getRightDomainBorder(), func.getFunctionValue(func.getRightDomainBorder()));

        } catch (Exception e) {
            System.out.println("Ошибка при тестировании ArrayTabulatedFunction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testLinkedListTabulatedFunction() {
        System.out.println("Тестирование LinkedListTabulatedFunction (f(x) = x + 5):");

        try {
            double[] values = new double[]{5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0};
            TabulatedFunction func = new LinkedListTabulatedFunction(0.0, 10.0, values);

            System.out.printf("Область определения: [%.1f, %.1f]\n", func.getLeftDomainBorder(), func.getRightDomainBorder());
            System.out.println("Кол-во точек: " + func.getPointsCount());

            System.out.println("\nТочки перед операциями:");
            for(int i = 0; i < func.getPointsCount(); ++i) {
                FunctionPoint p = func.getPoint(i);
                System.out.printf("(%.1f; %.1f) ", p.getX(), p.getY());
            }
            System.out.println();

            System.out.println("\nВычисленные значения функции:");
            double[] testX = new double[]{-5.0, -2.0, 0.0, 1.0, 2.5, 3.0, 4.5, 5.0, 6.5, 7.0, 8.5, 10.0, 12.0, 15.0};
            for(double x : testX) {
                double y = func.getFunctionValue(x);
                double expected = x + 5.0;
                if (Double.isNaN(y)) {
                    System.out.printf("f(%.1f) = не определено (не входит в область определения)\n", x);
                } else {
                    System.out.printf("f(%.1f) = %.1f (должно быть: %.1f)\n", x, y, expected);
                }
            }

            System.out.println("\nТестирование добавления и удаления:");
            System.out.println("Количество точек до добавления: " + func.getPointsCount());
            func.addPoint(new FunctionPoint(3.3, 8.3));
            System.out.println("Количество точек после добавления: " + func.getPointsCount());

            System.out.println("Точки после добавления (3.3, 8.3):");
            for(int i = 0; i < func.getPointsCount(); ++i) {
                FunctionPoint p = func.getPoint(i);
                System.out.printf("(%.1f; %.1f) ", p.getX(), p.getY());
            }
            System.out.println();

            func.deletePoint(5);
            System.out.println("Количество точек после удаления: " + func.getPointsCount());

            System.out.println("Точки после удаления точки с индексом 5:");
            for(int i = 0; i < func.getPointsCount(); ++i) {
                FunctionPoint p = func.getPoint(i);
                System.out.printf("(%.1f; %.1f) ", p.getX(), p.getY());
            }
            System.out.println();

            System.out.println("\nПроверка интерполяции:");
            double[] interpolationX = new double[]{0.3, 1.1, 2.8, 3.9, 5.2, 6.7, 8.1, 9.4};
            for(double x : interpolationX) {
                double y = func.getFunctionValue(x);
                double expected = x + 5.0;
                boolean isCorrect = Math.abs(y - expected) < EPSILON;
                System.out.printf("f(%.1f) = %.1f (ожидалось: %.1f) %s\n",
                        x, y, expected, isCorrect ? "✓" : "✗");
            }

            System.out.println("\nПроверка точного совпадения:");
            double exactX = 3.0;
            double exactY = func.getFunctionValue(exactX);
            System.out.printf("f(%.1f) = %.1f (должно быть точно 8.0 без интерполяции)\n", exactX, exactY);

        } catch (Exception e) {
            System.out.println("Ошибка при тестировании LinkedListTabulatedFunction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testAllExceptions() {
        System.out.println("Тестирование исключений:");

        System.out.println("\n Неверные параметры конструктора:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(10, 0, 5);
            System.out.println("Ошибка: Исключение не было выброшено!");
        } catch (IllegalArgumentException e) {
            System.out.println("Исключение выброшено: " + e.getMessage());
        }

        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, 1);
            System.out.println("Ошибка: Исключение не было выброшено!");
        } catch (IllegalArgumentException e) {
            System.out.println("Исключение выброшено: " + e.getMessage());
        }

        System.out.println("\n Выход за границы индекса:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, new double[]{1, 2, 3});
            func.getPoint(10);
            System.out.println("Ошибка: Исключение не было выброшено!");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Исключение выброшено: " + e.getMessage());
        }

        System.out.println("\n Нарушение порядка точек:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, new double[]{1, 2, 3, 4, 5});
            func.setPointX(2, 0.5);
            System.out.println("Ошибка: Исключение не было выброшено!");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Исключение выброшено: " + e.getMessage());
        }

        System.out.println("\n Дублирование x координат:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, new double[]{1, 2, 3, 4, 5});
            func.addPoint(new FunctionPoint(2.5, 10.0));
            System.out.println("Ошибка: Исключение не было выброшено!");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Исключение выброшено: " + e.getMessage());
        }

        System.out.println("\n Удаление при минимальном количестве точек:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, new double[]{1, 2});
            func.deletePoint(0);
            System.out.println("Ошибка: Исключение не было выброшено!");
        } catch (IllegalStateException e) {
            System.out.println("Исключение выброшено: " + e.getMessage());
        }
    }
}