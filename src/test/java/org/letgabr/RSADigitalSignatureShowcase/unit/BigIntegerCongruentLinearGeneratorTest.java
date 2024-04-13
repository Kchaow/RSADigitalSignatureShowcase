package org.letgabr.RSADigitalSignatureShowcase.unit;

import org.junit.jupiter.api.Test;
import org.letgabr.RSADigitalSignatureShowcase.util.BigIntegerCongruentLinearGenerator;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BigIntegerCongruentLinearGeneratorTest
{
    @Test
    public void nextBigIntegerLengthTest()
    {
        int testsCount = 500;
        int bitLength = 10;
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator
                = new BigIntegerCongruentLinearGenerator(bitLength);
        List<BigInteger> randomBigIntegers = Stream
                .generate(bigIntegerCongruentLinearGenerator::nextBigInteger)
                .limit(testsCount).toList();
        List<Boolean> correctAnswers = Stream.generate(() -> true).limit(testsCount).toList();
        List<Boolean> answers = randomBigIntegers.stream().map(x -> x.bitLength() <= bitLength).toList();
        assertEquals(correctAnswers, answers);
    }

    @Test
    public void nextBigIntegerRangeTest()
    {
        int testsCount = 500;
        int bitLength = 10;
        BigInteger maxNumber = BigInteger.valueOf(45);
        BigInteger minNumber = BigInteger.valueOf(30);
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator
                = new BigIntegerCongruentLinearGenerator(bitLength);

        List<BigInteger> randomBigIntegersWithMax = Stream
                .generate(() -> bigIntegerCongruentLinearGenerator.nextBigInteger(maxNumber))
                .limit(testsCount).toList();
        List<Boolean> correctMaxAnswers = Stream.generate(() -> true).limit(testsCount).toList();
        List<Boolean> answersMax = randomBigIntegersWithMax.stream().map(x -> x.compareTo(maxNumber) < 0).toList();

        List<BigInteger> randomBigIntegersWithMinAndMax = Stream
                .generate(() -> bigIntegerCongruentLinearGenerator.nextBigInteger(minNumber ,maxNumber))
                .limit(testsCount).toList();
        List<Boolean> correctMinAndMaxAnswers = Stream.generate(() -> true).limit(testsCount).toList();
        List<Boolean> answersMinAndMax = randomBigIntegersWithMinAndMax.stream().map(x -> x.compareTo(minNumber)  >= 0 && x.compareTo(maxNumber) < 0).toList();

        assertAll(
                () -> assertEquals(correctMaxAnswers, answersMax),
                () -> assertEquals(correctMinAndMaxAnswers, answersMinAndMax)
        );
    }

    @Test
    public void nextBigIntegerPerformanceTest()
    {
        int bitLength = 1000;
        int testCounts = 50;
        Duration duration = Duration.ofMillis(50);
        BigInteger minNumber = BigInteger.valueOf(7845434354523434212L);
        BigInteger maxNumber = BigInteger.valueOf(Long.MAX_VALUE);
        BigIntegerCongruentLinearGenerator bigIntegerCongruentLinearGenerator
                = new BigIntegerCongruentLinearGenerator(bitLength);

        for (int i = 0; i < testCounts; i++)
        {
            assertAll(
                    () -> assertTimeout(duration, () -> bigIntegerCongruentLinearGenerator.nextBigInteger()),
                    () -> assertTimeout(duration, () -> bigIntegerCongruentLinearGenerator.nextBigInteger(maxNumber)),
                    () -> assertTimeout(duration, () -> bigIntegerCongruentLinearGenerator.nextBigInteger(minNumber, maxNumber))
            );
        }
    }

}
