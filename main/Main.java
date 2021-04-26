package main;

/**
 * @author krezefal
 */

import java.util.ArrayList;
import java.util.Scanner;

import static consts.ColorsANSI.*;
import static consts.Definitions.*;
import cyclic_coding.PolynomialGF2;
import cyclic_coding.Coder;

public class Main {

    static ArrayList<Integer> convert(String coefString) {

        ArrayList<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i < coefString.length(); i++) {
            char symbol = coefString.charAt(i);
            if (symbol == '1') { coefficients.add(1); }
            else if (symbol == '0') { coefficients.add(0); }
            else { throw new IllegalArgumentException("Incorrect input. Only '0' and '1'"); }
        }

        return coefficients;
    }

    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in);

            while (true) {
                System.out.print("Input generator (g(x)): ");
                PolynomialGF2 generator = new PolynomialGF2(convert(in.nextLine()));

                System.out.print("Input message length (k): ");
                int messageLength = Integer.parseInt(in.nextLine());

                System.out.print("Input error vector (e): ");
                PolynomialGF2 errorVector = new PolynomialGF2(convert(in.nextLine()));

                System.out.print("Input message: ");
                PolynomialGF2 message = new PolynomialGF2(convert(in.nextLine()));

                Coder test = new Coder(generator, messageLength);

                System.out.println("\n[Coding ...]");
                System.out.print("Check sum (c(x)): ");
                PolynomialGF2 codeWord = test.code(message, VERBOSE);
                System.out.print("Codeword (a): ");
                codeWord.printHuman();

                System.out.println("\n[Noising ...]");
                PolynomialGF2 noiseWord = codeWord.xor(errorVector);
                System.out.print("Noisy codeword (b): ");
                noiseWord.printHuman();

                System.out.println("\n[Calculating symptom ...]");
                PolynomialGF2 symptom = test.calcSymptom(noiseWord);
                System.out.print("Symptom (S(x)): ");
                symptom.printHuman();
                if (symptom.getDegree() != -1) System.out.println("E = 1");
                else System.out.println("E = 0");

                System.out.print("Enter 'e' to exit, enter anything to continue: ");
                if (in.nextLine().equals("e")) break;
                else System.out.print("*****************************************************");
            }
            in.close();

        } catch (RuntimeException runtimeException) {
            System.out.println(RED + runtimeException.getMessage() + RESET_COLOR);
        }
    }
}

