package org.letgabr.RSADigitalSignatureShowcase.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

public class BigIntegerCongruentLinearGenerator
{
    private BigInteger m;
    private final BigInteger a;
    private BigInteger c;
    private BigInteger precedingNumber;
    public BigIntegerCongruentLinearGenerator(int maxLength)
    {
        m = BigInteger.TWO.pow(maxLength-1).add(BigInteger.ONE);
        PrimeTester fermatTester = new PrimeTester("fermat");
        while (!fermatTester.isPrime(m))
            m = m.add(BigInteger.TWO);
        c = BigInteger.TWO.pow(maxLength / 2);
        while (m.mod(c).equals(BigInteger.ZERO))
            c = c.add(BigInteger.ONE);
        a = BigInteger.TWO.pow(maxLength / 3).add(BigInteger.ONE);
        precedingNumber = BigInteger.valueOf(Date.from(Instant.now()).getTime());
    }

    public BigInteger nextBigInteger()
    {
        return precedingNumber = (precedingNumber.multiply(a).add(c)).mod(m);
    }
    public BigInteger nextBigInteger(BigInteger maxNumber)
    {
        if (maxNumber.compareTo(m) > 0)
            throw new RuntimeException("maxNumber greater than max value, that current generator can provide");
        return precedingNumber = (precedingNumber.multiply(a).add(c)).mod(m).mod(maxNumber);
    }
    public BigInteger nextBigInteger(BigInteger minNumber ,BigInteger maxNumber)
    {
        if (maxNumber.compareTo(minNumber) < 1)
            throw new RuntimeException("maxNumber less or equals minNumber");
        if (maxNumber.compareTo(m) > 0)
            throw new RuntimeException("maxNumber greater than max value, that current generator can provide");
        return precedingNumber = (precedingNumber.multiply(a).add(c)).mod(m).mod(maxNumber.subtract(minNumber)).add(minNumber);
    }
}
