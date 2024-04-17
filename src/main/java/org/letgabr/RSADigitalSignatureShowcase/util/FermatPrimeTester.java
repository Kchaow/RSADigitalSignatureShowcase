package org.letgabr.RSADigitalSignatureShowcase.util;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FermatPrimeTester extends PrimeTester
{
    private List<Integer> fermatTestWitnesses;
    private List<BigInteger> fermatTestWitnessesBigInteger;

    public FermatPrimeTester()
    {
        final int fermatTestWitnessCount = 16;
        List<Integer> numbers = Stream.iterate(2, (x) -> x + 1).limit(fermatTestWitnessCount).toList();
        fermatTestWitnesses = Collections.unmodifiableList(numbers);
        List<BigInteger> numbersBigInteger = numbers.stream().map(BigInteger::valueOf).toList();
        fermatTestWitnessesBigInteger = Collections.unmodifiableList(numbersBigInteger);
    }

    @Override
    public boolean isPrime(BigInteger number)
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


}
