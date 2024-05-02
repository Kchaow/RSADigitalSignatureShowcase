package org.letgabr.RSADigitalSignatureShowcase.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RSACryptoSystem
{
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger n;
    private BigInteger q;
    private BigInteger p;

    public RSACryptoSystem(int pLength, int qLength)
    {
        this.p = MathCrypto.getPrimeBigInteger(pLength);
        this.q = MathCrypto.getPrimeBigInteger(qLength);
        this.n = p.multiply(q);
        BigInteger euler = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator
                = new BigIntegerCongruentLinearGenerator(euler.bitLength()+1);
        BigInteger e = bigIntegerCongruentLinearGenerator
                .nextBigInteger(BigInteger.valueOf(3), euler);
        while (!MathCrypto.gcd(euler, e).equals(BigInteger.ONE))
            e = bigIntegerCongruentLinearGenerator
                    .nextBigInteger(BigInteger.valueOf(3), euler);
        BigInteger d = MathCrypto.getModularMultiplicativeInverse(e, euler);
        this.publicKey = e;
        this.privateKey = d;
    }

    public RSACryptoSystem(BigInteger p, BigInteger q)
    {
        PrimeTester primeTester = new MillerRabinPrimeTester();
        if (!primeTester.isPrime(p) || !primeTester.isPrime(q))
            throw new RuntimeException("p or q isn't prime");
        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
        BigInteger euler = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator
                = new BigIntegerCongruentLinearGenerator(euler.bitLength()+1);
        BigInteger e = bigIntegerCongruentLinearGenerator
                .nextBigInteger(BigInteger.valueOf(3), euler);
        while (!MathCrypto.gcd(euler, e).equals(BigInteger.ONE))
            e = bigIntegerCongruentLinearGenerator
                    .nextBigInteger(BigInteger.valueOf(3), euler);
        BigInteger d = MathCrypto.getModularMultiplicativeInverse(e, euler);
        this.publicKey = e;
        this.privateKey = d;
    }

    static public String encode(String text, BigInteger publicKey, BigInteger n) {
        List<BigInteger> bigIntegers = text.chars().mapToObj(BigInteger::valueOf).toList();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bigIntegers.size(); i++) {
            if (i == 0)
                stringBuilder.append(bigIntegers.get(i).modPow(publicKey, n).toString());
            else
                stringBuilder.append(" ").append(bigIntegers.get(i).modPow(publicKey, n).toString());
        }
        return stringBuilder.toString();
    }

    static public String encode(BigInteger number, BigInteger publicKey, BigInteger n) {
        return number.modPow(publicKey, n).toString();
    }

    static public String decodeNumber(String number, BigInteger privateKey, BigInteger n) {
        return new BigInteger(number).modPow(privateKey, n).toString();
    }

    static public String decodeText(String text, BigInteger privateKey, BigInteger n) {
        List<BigInteger> bigIntegers = Arrays.stream(text.split(" ")).map(BigInteger::new).toList();
        StringBuilder stringBuilder = new StringBuilder();
        for (BigInteger bigInteger : bigIntegers) {
            stringBuilder.append(Character.toChars(bigInteger.modPow(privateKey, n).intValue()));
        }
        return  stringBuilder.toString();
    }
}
