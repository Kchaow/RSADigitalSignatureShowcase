package org.letgabr.RSADigitalSignatureShowcase.util;

import lombok.Getter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
public class RSACryptoSystem
{
    private final BigInteger privateKey;
    private final BigInteger publicKey;
    private final BigInteger n;
    private final BigInteger q;
    private final BigInteger p;
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

    static public String encode(String text, BigInteger publicKey, BigInteger n)
    {
        List<BigInteger> bigIntegers = text.chars().mapToObj(BigInteger::valueOf).toList();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bigIntegers.size(); i++)
        {
            if (i == 0)
                stringBuilder.append(bigIntegers.get(i).modPow(publicKey, n).toString());
            else
                stringBuilder.append(" ").append(bigIntegers.get(i).modPow(publicKey, n).toString());
        }
        return stringBuilder.toString();
    }

    static public String decode(String text, BigInteger privateKey, BigInteger n)
    {
        List<BigInteger> bigIntegers = Arrays.stream(text.split(" ")).map(BigInteger::new).toList();
        StringBuilder stringBuilder = new StringBuilder();
        for (BigInteger bigInteger : bigIntegers) {
            stringBuilder.append(Character.toChars(bigInteger.modPow(privateKey, n).intValue()));
        }
        return  stringBuilder.toString();
    }
}
