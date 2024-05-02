package org.letgabr.RSADigitalSignatureShowcase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;

@SpringBootTest
class RsaDigitalSignatureShowcaseApplicationTests {

	@Test
	void contextLoads() throws NoSuchAlgorithmException {
		BigInteger bigInteger = new BigInteger("5712562798613896077051396994053672103838833082070348216490854253380281836405136881435463488174483602517078743382871442479356298857698517417967097718074801976455060886437604087736734326656113058282316231029166671086667432184223220136235310831871927936431852760174590273235212783966100372563339413612455737064669142519234601100795266653032");
		System.out.println(bigInteger.toString().length());
		System.out.println(bigInteger.toString(16).length());
	}

}
