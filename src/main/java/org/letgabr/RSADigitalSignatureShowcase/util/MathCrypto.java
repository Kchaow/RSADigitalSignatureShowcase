package org.letgabr.RSADigitalSignatureShowcase.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MathCrypto
{
    static final private List<BigInteger> primeNumbers;

    static
    {
        primeNumbers = sieveOfEratosthenes((int) Math.pow(10, 4));
    }

    static public BigInteger gcd(BigInteger a, BigInteger b)
    {
        if (a.compareTo(b) < 0)
            throw new RuntimeException("value a cannot be lower value b");
        if (a.mod(b).equals(BigInteger.ZERO))
            return b;
        return gcd(b, a.mod(b));
    }

    static public BigInteger getModularMultiplicativeInverse(BigInteger number, BigInteger modulo)
    {
        if (number.compareTo(modulo) > 0)
            throw new RuntimeException("number cannot be equal or greater modulo");

        List<BigInteger> r = new ArrayList<>();
        r.add(modulo); r.add(number);
        List<BigInteger> y = new ArrayList<>();
        y.add(BigInteger.ZERO); y.add(BigInteger.ONE);
        List<BigInteger> x = new ArrayList<>();
        x.add(BigInteger.ONE); x.add(BigInteger.ZERO);
        List<BigInteger> g = new ArrayList<>();
        int i = 2;
        do {
            g.add(r.get(i-2).divide(r.get(i-1)));
            r.add(r.get(i-2).subtract(g.get(g.size()-1).multiply(r.get(i-1))));
            y.add(y.get(i-2).subtract(g.get(g.size()-1).multiply(y.get(i-1))));
            x.add(x.get(i-2).subtract(g.get(g.size()-1).multiply(x.get(i-1))));
            i++;
        } while (!r.get(r.size()-1).equals(BigInteger.ZERO));
        if (r.get(r.size()-2).equals(BigInteger.ONE))
            return y.get(i-2).mod(modulo);
        else
            return null;
    }

    static public BigInteger getPrimeBigInteger(int length)
    {
        PrimeTester primeTester = new MillerRabinPrimeTester();
        BigInteger result = getLowLevelPrime(length);
        while (!primeTester.isPrime(result))
            result = getLowLevelPrime(length);
        return result;
    }

    static private BigInteger getLowLevelPrime(int length)
    {
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator = new BigIntegerCongruentLinearGenerator(length+1);
        BigInteger minValue = BigInteger.TWO.pow(length-1);
        BigInteger maxValue = BigInteger.TWO.pow(length-1).subtract(BigInteger.ONE).shiftLeft(1).or(BigInteger.ONE);
        while (true)
        {
            BigInteger candidate
                    = bigIntegerCongruentLinearGenerator.nextBigInteger(minValue, maxValue);
            if (candidate.mod(BigInteger.TWO).equals(BigInteger.ZERO))
                candidate = candidate.add(BigInteger.ONE);
            for (BigInteger divisor : primeNumbers)
            {
                if (candidate.mod(divisor).equals(BigInteger.ZERO) &&
                                divisor.pow(2).compareTo(candidate) < 1)
                    break;
                else
                    return candidate;
            }
        }
    }

    static private List<BigInteger> sieveOfEratosthenes(int n)
    {
        boolean[] prime = new boolean[n+1];
        Arrays.fill(prime, true);

        for (int p = 2; p*p <= n; p++)
        {
            if (prime[p])
            {
                for (int i = p*p; i <= n; i += p)
                {
                    prime[i] = false;
                }
            }
        }

        List<BigInteger> result = new ArrayList<>();
        for (int i = 2; i <= n; i++)
        {
            if (prime[i])
                result.add(BigInteger.valueOf(i));
        }
        return result;
    }
}
