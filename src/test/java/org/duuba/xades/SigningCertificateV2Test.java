package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class SigningCertificateV2Test {

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
		
		assertDoesNotThrow(() -> new SigningCertificateV2(certs, DigestMethod.SHA256).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SigningCertificateV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());

		NodeList children = created.getChildNodes();
		
		assertEquals(certs.size(), children.getLength());
		for(int i = 0; i < children.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
			assertEquals("Cert", children.item(i).getLocalName());
			assertTrue(((Element) children.item(i)).getElementsByTagNameNS(Constants.XADES_132_NS_URI, "IssuerSerialV2")
														.getLength() > 0);
		}
	}

	@Test
	void testRejectEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new SigningCertificateV2(null, null));

		assertThrows(IllegalArgumentException.class, () -> new SigningCertificateV2(Collections.emptyList(), null));
	}
}
