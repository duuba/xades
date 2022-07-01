package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.algorithms.JCEMapper;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class SigningCertificateTest {

	@BeforeAll
	static void setupJCEMapper() {
		JCEMapper.registerDefaultAlgorithms();
	}
	
	@Test
	void testContent() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		List<X509Certificate> certs = new ArrayList<>();
		certs.add(context.getCertificate());
		certs.add(context.getCertificate());
		
		assertDoesNotThrow(() -> new SigningCertificate(certs, DigestMethod.SHA256).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SigningCertificate", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());

		NodeList children = created.getChildNodes();
		
		assertEquals(certs.size(), children.getLength());
		for(int i = 0; i < children.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
			assertEquals("Cert", children.item(i).getLocalName());
			assertTrue(((Element) children.item(i)).getElementsByTagNameNS(Constants.XADES_132_NS_URI, "IssuerSerial")
														.getLength() > 0);

		}
	}

	@Test
	void testRejectEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new SigningCertificate(null, null));

		assertThrows(IllegalArgumentException.class, () -> new SigningCertificate(Collections.emptyList(), null));
	}
	
	@Test
	void testEquals() throws NoSuchAlgorithmException {
		X509Certificate cert = new TestCryptoContext().getCertificate();
		
		List<X509Certificate> certs1 = new ArrayList<>();
		List<X509Certificate> certs2 = new ArrayList<>();
		certs1.add(cert);
		certs2.add(cert);
		certs1.add(cert);
		
		SigningCertificate sc1 = new SigningCertificate(certs1, DigestMethod.SHA256);
		SigningCertificate sc2 = new SigningCertificate(certs1, DigestMethod.SHA256);
		SigningCertificate sc3 = new SigningCertificate(certs1, DigestMethod.SHA512);
		SigningCertificate sc4 = new SigningCertificate(certs2, DigestMethod.SHA256);
		
		assertTrue(sc1.equals(sc2));
		assertFalse(sc1.equals(sc3));
		assertFalse(sc1.equals(sc4));
	}
}
