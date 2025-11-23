package functions;
import java.io.*;
public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 1L;
    private class FunctionNode implements Serializable  {
        private static final long serialVersionUID = 1L;
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;

        public FunctionNode(FunctionPoint point) {
            this.point = point;
            this.prev = null;
            this.next = null;
        }

        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }
    }

    private FunctionNode head;
    private FunctionNode currentNode;
    private int currentIndex;
    private int pointsCount;
    private static final double EPSILON = 1e-10;


    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        if (pointsCount < 2) throw new IllegalArgumentException("Количество точек должно быть не менее 2");

        initializeList();
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            addNodeToTail().point = new FunctionPoint(x, 0);
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        if (values.length < 2) throw new IllegalArgumentException("Количество точек должно быть не менее 2");

        initializeList();
        double step = (rightX - leftX) / (values.length - 1);

        for (int i = 0; i < values.length; i++) {
            double x = leftX + step * i;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Нужно не меньше 2 точек");
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() - points[i-1].getX() <= -EPSILON) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по координате x");
            }
        }

        initializeList();
        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point);
        }
    }

    private void initializeList() {
        head = new FunctionNode(null);
        head.prev = head;
        head.next = head;
        pointsCount = 0;
        currentNode = head;
        currentIndex = -1;
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }

        FunctionNode node;
        if (currentIndex != -1 && Math.abs(index - currentIndex) < Math.min(index, pointsCount - index)) {
            node = currentNode;
            if (index > currentIndex) {
                for (int i = currentIndex; i < index; i++) node = node.next;
            } else {
                for (int i = currentIndex; i > index; i--) node = node.prev;
            }
        } else {
            node = head.next;
            for (int i = 0; i < index; i++) node = node.next;
        }

        currentNode = node;
        currentIndex = index;
        return node;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null, head.prev, head);
        head.prev.next = newNode;
        head.prev = newNode;
        pointsCount++;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + pointsCount + "]");
        }

        FunctionNode nextNode = (index == pointsCount) ? head : getNodeByIndex(index);
        FunctionNode prevNode = nextNode.prev;

        FunctionNode newNode = new FunctionNode(null, prevNode, nextNode);
        prevNode.next = newNode;
        nextNode.prev = newNode;

        pointsCount++;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: минимальное количество точек - 2");
        }
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;

        pointsCount--;
        currentNode = head;
        currentIndex = -1;

        return nodeToDelete;
    }

    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() -EPSILON || x > getRightDomainBorder() + EPSILON) {
            return Double.NaN;
        }

        FunctionNode node = head.next;
        while (node != head) {
            if (Math.abs(node.point.getX() - x) < EPSILON) {
                return node.point.getY();
            }
            node = node.next;
        }

        node = head.next;
        while (node != head && node.next != head) {
            double x1 = node.point.getX();
            double x2 = node.next.point.getX();

            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                double y1 = node.point.getY();
                double y2 = node.next.point.getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            node = node.next;
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        if ((index > 0 && point.getX() <= node.prev.point.getX() + EPSILON) ||
                (index < pointsCount-1 && point.getX() >= node.next.point.getX() - EPSILON)) {
            throw new InappropriateFunctionPointException("Новая точка нарушает порядок X координат");
        }

        node.point = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        if ((index > 0 && x <= node.prev.point.getX() + EPSILON) ||
                (index < pointsCount-1 && x >= node.next.point.getX() - EPSILON)) {
            throw new InappropriateFunctionPointException("Новая координата X нарушает порядок точек");
        }

        node.point.setX(x);
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = head.next;
        while (node != head) {
            if (Math.abs(node.point.getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
            }
            node = node.next;
        }

        int pos = 0;
        node = head.next;
        while (node != head && node.point.getX() < point.getX()) {
            pos++;
            node = node.next;
        }

        FunctionNode newNode = addNodeByIndex(pos);
        newNode.point = new FunctionPoint(point);
    }
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);

        FunctionNode node = head.next;
        while (node != head) {
            out.writeDouble(node.point.getX());
            out.writeDouble(node.point.getY());
            node = node.next;
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int count = in.readInt();
        initializeList();

        for (int i = 0; i < count; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            addNodeToTail().point = new FunctionPoint(x, y);
        }
    }
}

