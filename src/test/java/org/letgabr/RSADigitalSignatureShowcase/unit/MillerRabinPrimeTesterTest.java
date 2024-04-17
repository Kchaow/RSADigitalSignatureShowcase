package org.letgabr.RSADigitalSignatureShowcase.unit;

import org.junit.jupiter.api.Test;
import org.letgabr.RSADigitalSignatureShowcase.util.MillerRabinPrimeTester;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class MillerRabinPrimeTesterTest
{
    @Test
    public void isPrimeByMillerRabin()
    {
        PrimeTester millerRabinTester = new MillerRabinPrimeTester();
        List<Integer> testNumbers = getTestNumbers();
        AtomicInteger iter = new AtomicInteger(2);
        List<Boolean> correctAnswers = Stream.generate(() -> (iter.getAndIncrement() % 2 == 0)).limit(testNumbers.size()).toList();

        List<Boolean> answersBigInteger = testNumbers.stream().map(x -> millerRabinTester.isPrime(BigInteger.valueOf(x))).toList();

        assertEquals(correctAnswers, answersBigInteger);
    }

    @Test
    public void performanceTest()
    {
        BigInteger number = new BigInteger("179769313486231590772930519078902473361797697" +
                "89423065727343008115773267580550096313270847732240753602112011387987139335765" +
                "87897688144166224928474306394741243777678934248654852763022196012460941194530" +
                "82952085005768838150682342462881473913110540827237163350510684586298239947245" +
                "938479716304835356329624224137217");
        BigInteger h = new BigInteger("633825314831863038897452935939");
        PrimeTester millerRabinTester = new MillerRabinPrimeTester();
        assertTimeout(Duration.ofSeconds(2), () -> millerRabinTester.isPrime(number));
        assertTimeout(Duration.ofSeconds(2), () -> millerRabinTester.isPrime(h));
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
