package org.letgabr.RSADigitalSignatureShowcase.unit;

import org.junit.jupiter.api.Test;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimeTesterTest
{
    @Test
    public void isPrimeByFermat()
    {
        PrimeTester fermatTester = new PrimeTester("fermat");
        List<Integer> testNumbers = getTestNumbers();
        AtomicInteger iter = new AtomicInteger(2);
        List<Boolean> correctAnswers = Stream.generate(() -> (iter.getAndIncrement() % 2 == 0)).limit(testNumbers.size()).toList();

        List<Boolean> answersBigInteger = testNumbers.stream().map(x -> fermatTester.isPrime(BigInteger.valueOf(x))).toList();

        assertEquals(correctAnswers, answersBigInteger);
    }

    @Test
    public void isPrimeByMillerRabin()
    {
        PrimeTester millerRabinTester = new PrimeTester("millerRabin");
        List<Integer> testNumbers = getTestNumbers();
        AtomicInteger iter = new AtomicInteger(2);
        List<Boolean> correctAnswers = Stream.generate(() -> (iter.getAndIncrement() % 2 == 0)).limit(testNumbers.size()).toList();

        List<Boolean> answersBigInteger = testNumbers.stream().map(x -> millerRabinTester.isPrime(BigInteger.valueOf(x))).toList();

        assertEquals(correctAnswers, answersBigInteger);
    }

    private static List<Integer> getTestNumbers()
    {
        List<Integer> testNumbers = new ArrayList<>();
        testNumbers.add(3);
        testNumbers.add(4);
        testNumbers.add(5);
        testNumbers.add(6);
        testNumbers.add(7);
        testNumbers.add(9);
        testNumbers.add(11);
        testNumbers.add(12);
        testNumbers.add(13);
        testNumbers.add(15);
        testNumbers.add(17);
        testNumbers.add(18);
        testNumbers.add(19);
        testNumbers.add(21);
        testNumbers.add(23);
        testNumbers.add(25);
        testNumbers.add(29);
        testNumbers.add(30);
        testNumbers.add(31);
        testNumbers.add(33);
        testNumbers.add(37);
        testNumbers.add(39);
        testNumbers.add(41);
        testNumbers.add(42);
        testNumbers.add(43);
        testNumbers.add(44);
        testNumbers.add(47);
        testNumbers.add(51);
        testNumbers.add(53);
        testNumbers.add(55);
        testNumbers.add(59);
        testNumbers.add(60);
        testNumbers.add(61);
        testNumbers.add(63);
        testNumbers.add(67);
        testNumbers.add(69);
        testNumbers.add(71);
        testNumbers.add(72);
        testNumbers.add(73);
        testNumbers.add(76);
        testNumbers.add(79);
        testNumbers.add(81);
        testNumbers.add(83);
        return testNumbers;
    }
}
