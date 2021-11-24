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
import org.duuba.xades.AbstractCertIDTypeV2Element;
import org.duuba.xades.IssuerSerialV2;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class CertIDTypeV2ElementTest {

	@BeforeAll 
	static void setupJCEMapper() {
		JCEMapper.registerDefaultAlgorithms();
	}
	
	@Test
	void testContentCreated() throws ParserConfigurationException {
		TestCryptoContext ctx = new TestCryptoContext();
		
		try {
			AbstractCertIDTypeV2Element certId = new AbstractCertIDTypeV2Element(ctx.getCertificate(), DigestMethod.SHA256) {
				
				@Override
				protected QName getName() {				
					return new QName("TestCert");
				}
			};
			
			assertTrue(certId.getIssuerSerial() instanceof IssuerSerialV2);
		} catch (Throwable t) {
			fail(t);
		}
	}
}
