package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.algorithms.JCEMapper;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.util.encoders.Base64;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class AbstractCertIDTypeV2ElementTest {

	@BeforeAll 
	static void setupJCEMapper() {
		JCEMapper.registerDefaultAlgorithms();
	}
	
	@Test
	void testIssuerSerialV2Content() throws ParserConfigurationException, CertificateEncodingException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		final X509Certificate cert = ctx.getCertificate();
		
		assertDoesNotThrow(() -> new TestCertIDTypeV2Element(ctx.getCertificate(), DigestMethod.SHA256)
															.marshal(xwriter, "", ctx));
		
		Element issuerSerial = (Element) xwriter.getCreatedElement()
										.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "IssuerSerialV2").item(0);
		
		byte[] b64IssuerSerial;
		try {
			b64IssuerSerial = new org.bouncycastle.asn1.x509.IssuerSerial(
												new GeneralNames(new GeneralName(GeneralName.directoryName, 
																			cert.getIssuerX500Principal().getName())), 
												cert.getSerialNumber()).getEncoded("DER");
		} catch (Exception e) {
			throw new CertificateEncodingException("Could not extract Issuer / SerialNo from certificate");
		}

		assertArrayEquals(b64IssuerSerial, Base64.decode(issuerSerial.getTextContent()));
	}
	
	@Test
	void testEquals() throws Throwable {
		X509Certificate cert1 = new TestCryptoContext().getCertificate();		
		X509Certificate cert2;
		try (FileInputStream fis = 
				new FileInputStream(AbstractCertIDTypeElementTest.class.getClassLoader().getResource("other.cert").getPath())) {
			cert2 = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(fis);
		} 
		
		TestCertIDTypeV2Element e1 = new TestCertIDTypeV2Element(cert1, DigestMethod.SHA256);
		TestCertIDTypeV2Element e2 = new TestCertIDTypeV2Element(cert1, DigestMethod.SHA256);
		TestCertIDTypeV2Element e3 = new TestCertIDTypeV2Element(cert1, DigestMethod.SHA512);
		TestCertIDTypeV2Element e4 = new TestCertIDTypeV2Element(cert2, DigestMethod.SHA256);
		
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));
	}
	
	class TestCertIDTypeV2Element extends AbstractCertIDTypeV2Element {

		TestCertIDTypeV2Element(X509Certificate cert, String digestMethod)
				throws CertificateEncodingException, NoSuchAlgorithmException {
			super(cert, digestMethod);
		}

		@Override
		protected QName getName() {
			return new QName("TestCertV2");
		}
		
	}
}
