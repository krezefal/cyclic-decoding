package main;

/**
 * @author krezefal
 */

import java.util.ArrayList;
import java.util.Scanner;

import static consts.ColorsANSI.*;
import static consts.Definitions.*;
import coding.PolynomialGF2;
import coding.Coder;

public class Main {

    static boolean choose(String decision) {

        boolean decodeMode;

        if (decision.equals("1")) decodeMode = ALTERNATIVE;
        else if (decision.equals("0")) decodeMode = STANDARD;
        else { throw new IllegalArgumentException("Incorrect input. Only '0' and '1'"); }

        return decodeMode;
    }

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

                System.out.print("Choose decode mode ('0' for standard; '1' for alternative): ");
                boolean decodeMode = choose(in.nextLine());

                Coder test = new Coder(generator, messageLength);

                System.out.println("\n[Coding ...]");
                System.out.print("Check sum (c(x)): ");
                PolynomialGF2 codeWord = test.code(message, CODE_WORD, VERBOSE);
                System.out.print("Codeword (a): ");
                codeWord.printHuman();

                System.out.println("\n[Noising ...]");
                PolynomialGF2 noiseWord = codeWord.xor(errorVector, TRUNCATE_RANKS);
                System.out.print("Noisy codeword (b): ");
                noiseWord.printHuman();

                if (decodeMode == STANDARD) {
                    System.out.println("\n[Calculating symptom ...]");
                    System.out.print("Symptom (S(x)): ");
                }
                if (decodeMode == ALTERNATIVE) {
                    System.out.println("\n[Calculating alternative check sum ...]");
                    System.out.print("Alternative check sum (c'(x)): ");
                }
                boolean E = test.decode(noiseWord, decodeMode, VERBOSE);
                if (E) System.out.println("E = 1");
                else System.out.println("E = 0");

                System.out.print("\nEnter 'y' if you want to repeat: ");
                String decision = in.nextLine();
                if (decision.equals("y")) System.out.println("\n*****************************************************\n");
                else break;
            }
            in.close();

        } catch (RuntimeException runtimeException) {
            System.out.println(RED + "ERROR: " + runtimeException.getMessage() + RESET_COLOR);
        }
    }
}

