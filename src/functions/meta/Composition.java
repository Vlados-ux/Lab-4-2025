package functions.meta;

import functions.Function;

public class Composition implements Function {
    private Function f1;
    private Function f2;

    public Composition(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public double getLeftDomainBorder() {
        return f1.getLeftDomainBorder();
    }

    public double getRightDomainBorder() {
        return f1.getRightDomainBorder();
    }

    public double getFunctionValue(double x) {
        double innerValue = f1.getFunctionValue(x);
        if (Double.isNaN(innerValue)) {
            return Double.NaN;
        }
        return f2.getFunctionValue(innerValue);
    }
}