package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.holodeckb2b.commons.security.CertificateUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class CertifiedRoleV2Test {

	static List<Node> OTHER_CERT1_XML;
	static List<Node> OTHER_CERT2_XML;
	
	@BeforeAll
	static void createOtherCertInfo() throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();	
		String srcFile = CertifiedRoleV2.class.getClassLoader().getResource("fragments/element_tree.xml").getPath();
		final Element source = builder.parse(new FileInputStream(srcFile)).getDocumentElement();

		NodeList childNodes = source.getChildNodes();
		OTHER_CERT1_XML = new ArrayList<>(childNodes.getLength());
		OTHER_CERT2_XML = new ArrayList<>(childNodes.getLength());
		for(int i = 0; i < childNodes.getLength(); i++) {
			OTHER_CERT1_XML.add(childNodes.item(i));	
			OTHER_CERT2_XML.add(0, childNodes.item(i));	
		}
	}
	
	@Test
	void testAttrCert() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new CertifiedRoleV2(null, ctx.getCertificate()).marshal(xwriter, null, ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("CertifiedRole", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "X509AttributeCertificate").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "OtherAttributeCertificate").getLength());		
	}
	
	@Test
	void testOtherCert() throws ParserConfigurationException, SAXException, IOException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();	
		
		assertDoesNotThrow(() -> new CertifiedRoleV2(OTHER_CERT1_XML).marshal(xwriter, null, ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("CertifiedRole", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "X509AttributeCertificate").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "OtherAttributeCertificate").getLength());		
	}

	@Test
	void testEquals() throws CertificateException {
		final X509Certificate cert1 = new TestCryptoContext().getCertificate();
		final X509Certificate cert2 = CertificateUtils.getCertificate(Paths.get(
									CertifiedRoleV2Test.class.getClassLoader().getResource("other.cert").getPath()));
		
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		
		CertifiedRoleV2 e1 = assertDoesNotThrow(() -> new CertifiedRoleV2(id1, cert1));
		CertifiedRoleV2 e2 = assertDoesNotThrow(() -> new CertifiedRoleV2(id1, cert1));
		CertifiedRoleV2 e3 = assertDoesNotThrow(() -> new CertifiedRoleV2(null, cert1));
		CertifiedRoleV2 e4 = assertDoesNotThrow(() -> new CertifiedRoleV2(id2, cert1));
		CertifiedRoleV2 e5 = assertDoesNotThrow(() -> new CertifiedRoleV2(id1, cert2));
		
		CertifiedRoleV2 e6 = assertDoesNotThrow(() -> new CertifiedRoleV2(OTHER_CERT1_XML));
		CertifiedRoleV2 e7 = assertDoesNotThrow(() -> new CertifiedRoleV2(OTHER_CERT1_XML));
		CertifiedRoleV2 e8 = assertDoesNotThrow(() -> new CertifiedRoleV2(OTHER_CERT2_XML));
		
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));
		assertFalse(e1.equals(e5));
		
		assertTrue(e6.equals(e7));
		assertFalse(e6.equals(e8));		
	}
}
