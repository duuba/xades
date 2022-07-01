package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.xml.security.algorithms.JCEMapper;
import org.duuba.xades.AbstractCertIDTypeElement;
import org.duuba.xades.Constants;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class CertIDTypeElementTest {

	@BeforeAll 
	static void setupJCEMapper() {
		JCEMapper.registerDefaultAlgorithms();
	}
	
	@Test
	void testContentCreated() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		try {
			new AbstractCertIDTypeElement(ctx.getCertificate(), DigestMethod.SHA256) {
				
				@Override
				protected QName getName() {				
					return new QName("TestCert");
				}
			}.marshal(xwriter, null, ctx);
		} catch (Throwable t) {
			fail(t);
		}
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("TestCert", created.getLocalName());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CertDigest").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "IssuerSerial").getLength());
	}

	@Test
	void testCertDigest() {		
		try {
			X509Certificate certificate = new TestCryptoContext().getCertificate();
			AbstractCertIDTypeElement certId =
					new AbstractCertIDTypeElement(certificate, DigestMethod.SHA256) {
				
						@Override
						protected QName getName() {				
							return new QName("TestCert");
						}
					};

			assertEquals(DigestMethod.SHA256, certId.getCertDigest().getDigestMethod());
			
			assertArrayEquals(MessageDigest.getInstance("SHA-256").digest(certificate.getEncoded()), 
							  certId.getCertDigest().getDigestValue());
			
		} catch (Throwable t) {
			fail(t);
		}				
	}
	
	@Test
	void testUnknownDigestMethod() {
		try {			
			new AbstractCertIDTypeElement(new TestCryptoContext().getCertificate(), 
									"http://test.xades.holodeck-b2b.org/notreal") {
				
				@Override
				protected QName getName() {				
					return new QName("TestCert");
				}
			};
			fail("Should reject unknown digest method");
		} catch (NoSuchAlgorithmException unknownAlg) {
			// Expected
		} catch (Throwable t) {
			fail(t);
		}		
	}	
	
}
