package org.letgabr.RSADigitalSignatureShowcase.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class PrimeTester
{
    private final String ALGORITHM;
    private List<Integer> fermatTestWitnesses;
    private List<BigInteger> fermatTestWitnessesBigInteger;
    public PrimeTester(String algorithm)
    {
        final int fermatTestWitnessCount = 16;
        this.ALGORITHM = algorithm;
        if (ALGORITHM.equals("fermat"))
        {
            List<Integer> numbers = Stream.iterate(2, (x) -> x + 1).limit(fermatTestWitnessCount).toList();
            fermatTestWitnesses = Collections.unmodifiableList(numbers);
            List<BigInteger> numbersBigInteger = numbers.stream().map(BigInteger::valueOf).toList();
            fermatTestWitnessesBigInteger = Collections.unmodifiableList(numbersBigInteger);
        }
        else if (!ALGORITHM.equals("millerRabin"))
        {
            throw new RuntimeException("unknown algorithm");
        }
    }

    public boolean isPrime(BigInteger number)
    {
        if (ALGORITHM.equals("fermat"))
            return fermatTest(number);
        else
            return millerRabinTest(number);
    }

//    public boolean isPrime(int number)
//    {
//        if (ALGORITHM.equals("fermat"))
//            return fermatTest(number);
//        else
//            return false;
//    }

    private boolean fermatTest(BigInteger number)
    {
        if (number.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return false;
        for (BigInteger witness : fermatTestWitnessesBigInteger)
        {
            if (witness.mod(number).equals(BigInteger.ZERO) || witness.compareTo(number) >= 0)
                continue;
            if (!witness.modPow(number.subtract(BigInteger.ONE), number).equals(BigInteger.ONE))
                return false;
        }
        return true;
    }

//    private boolean fermatTest(int number)
//    {
//        if (number % 2 == 0)
//            return false;
//        for (int witness : fermatTestWitnesses)
//        {
//            if (witness % number == 0 || witness >= number)
//                continue;
//            log.info(Math.pow(witness, number-1)+ "");
//            if ((int) Math.pow(witness, number-1) % number != 1)
//                return false;
//        }
//        return true;
//    }

    private boolean millerRabinTest(BigInteger number)
    {
        if (number.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return false;
        int witnessCount = 5;
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator =
                new BigIntegerCongruentLinearGenerator(number.bitLength()+1);
        int s = 0;
        BigInteger r = BigInteger.ONE;
        BigInteger numberMinusOne = number.subtract(BigInteger.ONE);
        while (!BigInteger.TWO.pow(s).multiply(r).equals(numberMinusOne))
        {
            s++;
            if (BigInteger.TWO.pow(s).multiply(r).compareTo(numberMinusOne) > 0)
            {
                s = 0;
                r = r.add(BigInteger.ONE);
            }
        }
        for (int i = 0; i < witnessCount; i++)
        {
            List<BigInteger> row = new ArrayList<>();
            for (int j = 0; j <= s; j++)
            {
                BigInteger witnessCandidate = bigIntegerCongruentLinearGenerator
                      .nextBigInteger(BigInteger.ONE, number);
                BigInteger power = BigInteger.TWO.pow(s).multiply(r);
                row.add(witnessCandidate.modPow(power ,number));
            }
            if (!row.get(0).equals(BigInteger.ONE)
                && !(row.contains(numberMinusOne) && row.get(row.size()-1).equals(BigInteger.ONE)))
                return false;
        }
        return true;
    }
}
