package cyclic_coding;

/**
 * @author krezefal
 */

import java.util.ArrayList;

import static consts.Definitions.*;

public class Coder {

    private final PolynomialGF2 generator;
    private final int messageLength;

    public Coder() {
        generator = new PolynomialGF2();
        messageLength = 0;
    }

    public Coder(int messageLength) {
        generator = new PolynomialGF2(messageLength);
        this.messageLength = messageLength;
    }

    public Coder(ArrayList<Integer> coefficients, int messageLength) {
        generator = new PolynomialGF2(coefficients);
        this.messageLength = messageLength;
    }

    public Coder(PolynomialGF2 generator, int messageLength) {
        this.generator = new PolynomialGF2(generator);
        this.messageLength = messageLength;
    }

    public Coder(Coder other) {
        generator = new PolynomialGF2(other.getGenerator());
        messageLength = other.getMessageLength();
    }

    public PolynomialGF2 getGenerator() {
        ArrayList<Integer> generatorCopy = new ArrayList<>();
        for (int i = generator.getDegree(); i >= 0 ; i--) {
            generatorCopy.add(generator.getCoefficient(i));
        }

        return new PolynomialGF2(generatorCopy);
    }

    public int getMessageLength() {
        return messageLength;
    }

    public PolynomialGF2 code(PolynomialGF2 message, boolean mode) {
        PolynomialGF2 checkSum = message.multiply(generator.getDegree()).divideForRemainder(generator);
        PolynomialGF2 codeWord = message.multiply(generator.getDegree()).xor(checkSum);

        if (mode != SILENT) checkSum.printHuman();

        return codeWord;
    }

    public PolynomialGF2 calcSymptom(PolynomialGF2 codeWord) {
        PolynomialGF2 symptom = codeWord.divideForRemainder(generator);

        return symptom;
    }
}
