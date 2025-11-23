package functions;

import java.io.*;

public final class TabulatedFunctions {
    private TabulatedFunctions() {
        throw new UnsupportedOperationException("Нельзя создавать объекты служебного класса");
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табуляции выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Требуется не менее 2 точек");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }


    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeInt(function.getPointsCount());
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                dos.writeDouble(point.getX());
                dos.writeDouble(point.getY());
            }
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи табулированной функции в поток", e);
        }
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointsCount = dis.readInt();
            FunctionPoint[] points = new FunctionPoint[pointsCount];

            for (int i = 0; i < pointsCount; i++) {
                double x = dis.readDouble();
                double y = dis.readDouble();
                points[i] = new FunctionPoint(x, y);
            }

            return new ArrayTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения табулированной функции из потока", e);
        }
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(out))) {
            pw.print(function.getPointsCount());

            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                pw.print(" " + point.getX() + " " + point.getY());
            }
            pw.flush();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи табулированной функции в writer", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(in);
            tokenizer.parseNumbers();


            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалось количество точек");
            }
            int pointsCount = (int) tokenizer.nval;

            FunctionPoint[] points = new FunctionPoint[pointsCount];


            for (int i = 0; i < pointsCount; i++) {
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата X");
                }
                double x = tokenizer.nval;

                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата Y");
                }
                double y = tokenizer.nval;

                points[i] = new FunctionPoint(x, y);
            }

            return new ArrayTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения табулированной функции из reader", e);
        }
    }
}