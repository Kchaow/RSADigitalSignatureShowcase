package org.letgabr.RSADigitalSignatureShowcase.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class MathCrypto
{
    static final private List<BigInteger> primeNumbers;

    static
    {
        primeNumbers = sieveOfEratosthenes((int) Math.pow(10, 4));
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
