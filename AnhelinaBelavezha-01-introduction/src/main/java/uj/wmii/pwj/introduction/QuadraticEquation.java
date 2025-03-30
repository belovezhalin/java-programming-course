package uj.wmii.pwj.introduction;

public class QuadraticEquation {
    public double[] findRoots(double a, double b, double c) {
        double determinant = Math.pow(b, 2) - 4*a*c;
        if (determinant > 0.0) {
            double x1 = (-b + Math.pow(determinant, 0.5)) / (2.0 * a);
            double x2 = (-b - Math.pow(determinant, 0.5)) / (2.0 * a);
            return new double[]{x1, x2};
        } else if (determinant == 0.0) {
            double x = -b / (2.0 * a);
            return new double[]{x};
        } else {
            return new double[0];
        }
    }
}

