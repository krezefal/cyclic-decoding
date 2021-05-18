package coding;

/**
 * @author krezefal
 */

import java.util.ArrayList;
import java.util.Random;

import static consts.Definitions.*;

public class PolynomialGF2 {

    private final ArrayList<Integer> coefficients;
    private final int degree;

    public PolynomialGF2() {
        coefficients = new ArrayList<>();
        degree = -1;
    }

    public PolynomialGF2(int size) throws IllegalArgumentException {

        if (size < 0) { throw new IllegalArgumentException("Negative size"); }

        coefficients = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            coefficients.add(0);
        }

        degree = -1;
    }

    public PolynomialGF2(int size, double probability) throws IllegalArgumentException {

        if (size < 0) { throw new IllegalArgumentException("Negative size"); }
        if (probability < 0.0 || probability > 1.0) { throw new IllegalArgumentException("Incorrect probability"); }

        coefficients = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            double randVal = random.nextDouble();
            if (randVal < probability) coefficients.add(1);
            else coefficients.add(0);
        }

        degree = calcDegree();
    }

    public PolynomialGF2(ArrayList<Integer> coefficients) throws IllegalArgumentException {
        this.coefficients = new ArrayList<>();
        for (Integer coef : coefficients) {
            if (coef != 0 && coef != 1) { throw new IllegalArgumentException("Polynomial coefficients go beyond the boundaries of GF(2)"); }
            this.coefficients.add(coef);
        }

        degree = calcDegree();
    }

    public PolynomialGF2(PolynomialGF2 other) {
        coefficients = new ArrayList<>();
        for (int i = other.getDegree(); i >= 0; i--)
            coefficients.add(other.getCoefficient(i));

        degree = other.getDegree();
    }

    public int getCoefficient(int i) throws IllegalArgumentException {

        if (i < 0 || i > degree) { throw new IllegalArgumentException("Invalid coefficient number"); }

        return coefficients.get(coefficients.size() - 1 - i);
    }

    public int getDegree() {
        return degree;
    }

    private int calcDegree() {
        for (int i = 0; i < coefficients.size(); i++) {
            if (coefficients.get(i) == 1) {
                return coefficients.size() - 1 - i;
            }
        }

        return -1;
    }

    public void printBinary() {
        for (int i = this.getDegree(); i >= 0; i--)
            System.out.print(this.getCoefficient(i));
        System.out.print("\n");
    }

    public void printHuman() {
        if (this.getDegree() == -1) System.out.print("0");
        for (int i = this.getDegree(); i >= 0; i--) {
            if (this.getCoefficient(i) != 0) {
                if (i != this.getDegree())
                    System.out.print(" + ");
                System.out.print("x^" + i);
            }
        }
        System.out.print("\n");
    }

    public PolynomialGF2 xor(PolynomialGF2 originalOther, boolean mode) {

        PolynomialGF2 other;

        if (mode == TRUNCATE_RANKS && this.getDegree() < originalOther.getDegree()) {
            ArrayList<Integer> truncatedSequence = new ArrayList<>();
            for (int i = this.getDegree(); i >= 0; i--) {
                truncatedSequence.add(originalOther.getCoefficient(i));
            }
            other = new PolynomialGF2(truncatedSequence);
        }
        else {
            other = new PolynomialGF2(originalOther);
        }

        if (this.getDegree() == -1) return other;
        if (other.getDegree() == -1) return this;

        ArrayList<Integer> resultCoefficients = new ArrayList<>();

        if (this.getDegree() > other.getDegree()) {
            for (int i = this.getDegree(); i > other.getDegree(); i--) {
                resultCoefficients.add(this.getCoefficient(i));
            }
            for (int i = other.getDegree(); i >= 0; i--) {
                resultCoefficients.add((this.getCoefficient(i) + other.getCoefficient(i)) % 2);
            }
        }
        else {
            for (int i = other.getDegree(); i > this.getDegree(); i--) {
                resultCoefficients.add(other.getCoefficient(i));
            }
            for (int i = this.getDegree(); i >= 0; i--) {
                resultCoefficients.add((this.getCoefficient(i) + other.getCoefficient(i)) % 2);
            }
        }

        return new PolynomialGF2(resultCoefficients);
    }

    public PolynomialGF2 divideForRemainder(PolynomialGF2 divider) throws IllegalArgumentException {

        if (divider.getDegree() == -1) { throw new IllegalArgumentException("Zero divider"); }
        if (this.getDegree() < divider.getDegree()) return this;

        PolynomialGF2 dividend = new PolynomialGF2(this);

        while (dividend.getDegree() >= divider.getDegree()) {
            ArrayList<Integer> subtrahend = new ArrayList<>();
            int wholePartDegree = dividend.getDegree() - divider.getDegree();
            for (int i = divider.getDegree(); i >= 0 ; i--) {
                subtrahend.add(divider.getCoefficient(i));
            }
            for (int i = 0; i < wholePartDegree; i++) {
                subtrahend.add(0);
            }
            dividend = dividend.xor(new PolynomialGF2(subtrahend), TRUNCATE_RANKS);
        }

        return dividend; // remainder
    }

    public PolynomialGF2 multiply(PolynomialGF2 multiplier) {

        if (this.getDegree() == -1  || multiplier.getDegree() == -1) return new PolynomialGF2();

        PolynomialGF2 multiplicand = new PolynomialGF2(this);

        for (int i = multiplier.getDegree(); i >= 0 ; i--) {
            if (multiplier.getCoefficient(i) == 1) {
                ArrayList<Integer> addendum = new ArrayList<>();
                for (int j = multiplicand.getDegree(); j >= 0 ; j--) {
                    addendum.add(multiplicand.getCoefficient(j));
                }
                for (int j = 0; j < i; j++) {
                    addendum.add(0);
                }
                if (i != multiplier.getDegree())
                    multiplicand = multiplicand.xor(new PolynomialGF2(addendum), TRUNCATE_RANKS);
            }
        }

        return multiplicand; // result
    }

    public PolynomialGF2 multiply(int monomialDegree) {

        if (this.getDegree() == -1  || monomialDegree < 0) return new PolynomialGF2();

        ArrayList<Integer> multiplicand = new ArrayList<>();

        for (int i = this.getDegree(); i >= 0 ; i--) {
            multiplicand.add(this.getCoefficient(i));
        }
        for (int i = 0; i < monomialDegree; i++) {
            multiplicand.add(0);
        }

        return new PolynomialGF2(multiplicand);
    }

    public PolynomialGF2 discard(boolean ranks, int rankNumber) {

        if (rankNumber >= this.getDegree()) return new PolynomialGF2();
        if (rankNumber <= 0) return this;

        ArrayList<Integer> truncatedSequence = new ArrayList<>();

        if (ranks == LEAST_SIGNIFICANT) {
            for (int i = this.getDegree(); i >= rankNumber; i--) {
                truncatedSequence.add(this.getCoefficient(i));
            }
        }
        if (ranks == MOST_SIGNIFICANT) {
            for (int i = this.getDegree() - rankNumber; i >= 0; i--) {
                truncatedSequence.add(this.getCoefficient(i));
            }
        }

        return new PolynomialGF2(truncatedSequence);
    }

    public int hammingWeight() {
        int weight = 0;
        for (Integer coefficient : coefficients) {
            if (coefficient.equals(1)) weight++;
        }

        return weight;
    }
}
