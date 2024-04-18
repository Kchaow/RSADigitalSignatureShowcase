package org.letgabr.RSADigitalSignatureShowcase.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class MillerRabinPrimeTester extends PrimeTester
{
    @Override
    public boolean isPrime(BigInteger number)
    {
        if (number.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return false;
        int witnessCount = 5;
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator =
                new BigIntegerCongruentLinearGenerator(number.bitLength()+1);

//        int s = 0;
//        BigInteger r = BigInteger.ONE;
          BigInteger numberMinusOne = number.subtract(BigInteger.ONE);
//        while (!BigInteger.TWO.pow(s).multiply(r).equals(numberMinusOne))
//        {
//            s++;
//            if (BigInteger.TWO.pow(s).multiply(r).compareTo(numberMinusOne) > 0)
//            {
//                s = 0;
//                r = r.add(BigInteger.ONE);
//            }
//        }

        int s = 0;
        BigInteger r = number.subtract(BigInteger.ONE);
        while ((r.and(BigInteger.ONE)).equals(BigInteger.ZERO))
        {
            r = r.shiftRight(1);
            s++;
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
