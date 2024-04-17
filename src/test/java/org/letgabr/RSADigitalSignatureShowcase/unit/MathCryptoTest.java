package org.letgabr.RSADigitalSignatureShowcase.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.letgabr.RSADigitalSignatureShowcase.util.FermatPrimeTester;
import org.letgabr.RSADigitalSignatureShowcase.util.MathCrypto;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MathCryptoTest
{
    @Test
    public void getPrimeBigIntegerTest()
    {
        int firstLength = 10;
        int secondValueLength = 100;
        int thirdValueLength = 512;
        int fourthValueLength = 1024;
        PrimeTester fermatPrimeTester = new FermatPrimeTester();
        BigInteger firstValue = MathCrypto.getPrimeBigInteger(firstLength);
        log.info(firstValue.toString());
        BigInteger secondValue = MathCrypto.getPrimeBigInteger(secondValueLength);
        log.info(secondValue.toString());
        BigInteger thirdValue = MathCrypto.getPrimeBigInteger(thirdValueLength);
        log.info(thirdValue.toString());
        BigInteger fourthValue = MathCrypto.getPrimeBigInteger(fourthValueLength);
        log.info(fourthValue.toString());

        assertAll(
                () -> assertTrue(fermatPrimeTester.isPrime(firstValue)),
                () -> assertTrue(fermatPrimeTester.isPrime(secondValue)),
                () -> assertTrue(fermatPrimeTester.isPrime(thirdValue)),
                () -> assertTrue(fermatPrimeTester.isPrime(fourthValue)),
                () -> assertEquals(firstLength, firstValue.bitLength()),
                () -> assertEquals(secondValueLength, secondValue.bitLength()),
                () -> assertEquals(thirdValueLength, thirdValue.bitLength()),
                () -> assertEquals(fourthValueLength, fourthValue.bitLength())
        );
    }

    @Test
    public void getModularMultiplicativeInverseTest()
    {
        BigInteger firstNumber = BigInteger.valueOf(17); BigInteger firstModulo = BigInteger.valueOf(79);
        BigInteger secondNumber = BigInteger.valueOf(421); BigInteger secondModule = BigInteger.valueOf(883);
        BigInteger thirdNumber = BigInteger.valueOf(36); BigInteger thirdModulo = BigInteger.valueOf(136);

        assertAll(
                () -> assertEquals(BigInteger.valueOf(14), MathCrypto.getModularMultiplicativeInverse(firstNumber, firstModulo)),
                () -> assertEquals(BigInteger.valueOf(323), MathCrypto.getModularMultiplicativeInverse(secondNumber, secondModule)),
                () -> assertNull(MathCrypto.getModularMultiplicativeInverse(thirdNumber, thirdModulo))
        );
    }

    @Test
    public void gcdTest()
    {
        BigInteger a = BigInteger.valueOf(56);
        BigInteger b = BigInteger.valueOf(35);

        assertEquals(BigInteger.valueOf(7), MathCrypto.gcd(a, b));
    }
}
