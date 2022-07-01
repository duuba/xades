package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.algorithms.JCEMapper;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.holodeckb2b.commons.security.CertificateUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class AbstractCertIDTypeElementTest {

	@BeforeAll 
	static void setupJCEMapper() {
		JCEMapper.registerDefaultAlgorithms();
	}
	
	@Test
	void testContentCreated() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		final X509Certificate cert = ctx.getCertificate();
		
		assertDoesNotThrow(() -> new TestCertIDTypeElement(cert, DigestMethod.SHA256).marshal(xwriter, null, ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("TestCert", created.getLocalName());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CertDigest").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "IssuerSerial").getLength());
		
		Element issuerSerial = (Element) 
									created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "IssuerSerial").item(0);
		
		assertEquals(cert.getIssuerX500Principal().getName(), 
					issuerSerial.getElementsByTagNameNS(XMLSignature.XMLNS, "X509IssuerName").item(0).getTextContent());
		assertEquals(cert.getSerialNumber().toString(), 
				  issuerSerial.getElementsByTagNameNS(XMLSignature.XMLNS, "X509SerialNumber").item(0).getTextContent());
	}

	@Test
	void testCertDigest() throws CertificateEncodingException, NoSuchAlgorithmException {		
		X509Certificate certificate = new TestCryptoContext().getCertificate();
		TestCertIDTypeElement certId = assertDoesNotThrow(() -> new TestCertIDTypeElement(certificate, DigestMethod.SHA256));
		
		assertEquals(DigestMethod.SHA256, certId.getCertDigest().getDigestMethod());			
		assertArrayEquals(MessageDigest.getInstance("SHA-256").digest(certificate.getEncoded()), 
						  certId.getCertDigest().getDigestValue());			
	}
	
	@Test
	void testUnknownDigestMethod() {
		assertThrows(NoSuchAlgorithmException.class, 
					() -> new TestCertIDTypeElement(new TestCryptoContext().getCertificate(), 
										"http://test.xades.holodeck-b2b.org/notreal"));	
	}	
	
	@Test
	void testEquals() throws Throwable {
		X509Certificate cert1 = new TestCryptoContext().getCertificate();		
		X509Certificate cert2 = CertificateUtils.getCertificate(Paths.get(
							AbstractCertIDTypeElementTest.class.getClassLoader().getResource("other.cert").getPath()));
		
		TestCertIDTypeElement e1 = new TestCertIDTypeElement(cert1, DigestMethod.SHA256);
		TestCertIDTypeElement e2 = new TestCertIDTypeElement(cert1, DigestMethod.SHA256);
		TestCertIDTypeElement e3 = new TestCertIDTypeElement(cert1, DigestMethod.SHA512);
		TestCertIDTypeElement e4 = new TestCertIDTypeElement(cert2, DigestMethod.SHA256);
		
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));
	}
	
	
	static class TestCertIDTypeElement extends AbstractCertIDTypeElement {
		
		TestCertIDTypeElement(X509Certificate cert, String digestMethod)
				throws CertificateEncodingException, NoSuchAlgorithmException {
			super(cert, digestMethod);
		}

		@Override
		protected QName getName() {
			return new QName("TestCert");
		}		
	}
}
