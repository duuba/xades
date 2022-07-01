package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class SignerRoleTest {

	@Test
	void testBoth() throws ParserConfigurationException, CertificateEncodingException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final List<ClaimedRole> claimed = new ArrayList<>();
		claimed.add(new ClaimedRole(null));
		claimed.add(new ClaimedRole(null));
		
		final List<CertifiedRole> cert = new ArrayList<>();
		cert.add(new CertifiedRoleV1("certify-1", context.getCertificate()));
		
		assertDoesNotThrow(() -> new SignerRole(claimed, cert).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignerRole", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(2, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("ClaimedRoles", children.item(0).getLocalName());
		NodeList rClaimed = children.item(0).getChildNodes();
		assertEquals(claimed.size(), rClaimed.getLength());
		for(int i = 0; i < rClaimed.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rClaimed.item(i).getNamespaceURI());
			assertEquals("ClaimedRole", rClaimed.item(i).getLocalName());
		}
		
		assertEquals("CertifiedRoles", children.item(1).getLocalName());
		NodeList rCertfd = children.item(1).getChildNodes();
		assertEquals(cert.size(), rCertfd.getLength());
		for(int i = 0; i < rCertfd.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rCertfd.item(i).getNamespaceURI());
			assertEquals("CertifiedRole", rCertfd.item(i).getLocalName());
		}
	}
	
	@Test
	void testCertifiedOnly() throws ParserConfigurationException, CertificateEncodingException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final List<ClaimedRole> claimed = new ArrayList<>();
		
		final List<CertifiedRole> cert = new ArrayList<>();
		cert.add(new CertifiedRoleV1("certify-1", context.getCertificate()));
		
		assertDoesNotThrow(() -> new SignerRole(claimed, cert).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignerRole", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		
		assertEquals("CertifiedRoles", children.item(0).getLocalName());
		NodeList rCertfd = children.item(0).getChildNodes();
		assertEquals(cert.size(), rCertfd.getLength());
		for(int i = 0; i < rCertfd.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rCertfd.item(i).getNamespaceURI());
			assertEquals("CertifiedRole", rCertfd.item(i).getLocalName());
		}
	}

	@Test
	void testClaimedOnly() throws ParserConfigurationException, CertificateEncodingException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final List<ClaimedRole> claimed = new ArrayList<>();
		claimed.add(new ClaimedRole(null));
		claimed.add(new ClaimedRole(null));
		
		final List<CertifiedRole> cert = new ArrayList<>();
		
		assertDoesNotThrow(() -> new SignerRole(claimed, cert).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignerRole", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		
		assertEquals("ClaimedRoles", children.item(0).getLocalName());
		NodeList rClaimed = children.item(0).getChildNodes();
		assertEquals(claimed.size(), rClaimed.getLength());
		for(int i = 0; i < rClaimed.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rClaimed.item(i).getNamespaceURI());
			assertEquals("ClaimedRole", rClaimed.item(i).getLocalName());
		}
	}
	
	@Test
	void testEquals() throws CertificateEncodingException {
		X509Certificate certificate = new TestCryptoContext().getCertificate();
		final List<ClaimedRole> claimed1 = new ArrayList<>();
		final List<ClaimedRole> claimed2 = new ArrayList<>();
		claimed1.add(new ClaimedRole(null));
		claimed2.add(new ClaimedRole(null));
		claimed2.add(new ClaimedRole(null));
		
		final List<CertifiedRole> cert1 = new ArrayList<>();
		final List<CertifiedRole> cert2 = new ArrayList<>();
		cert1.add(new CertifiedRoleV1("certify-1", certificate));
		cert2.add(new CertifiedRoleV1("certify-1", certificate));
		cert1.add(new CertifiedRoleV1("certify-2", certificate));
		
		SignerRole sr1 = new SignerRole(claimed1, cert1);
		SignerRole sr2 = new SignerRole(claimed1, cert1);
		SignerRole sr3 = new SignerRole(null, cert1);
		SignerRole sr4 = new SignerRole(claimed2, cert1);
		SignerRole sr5 = new SignerRole(claimed1, null);
		SignerRole sr6 = new SignerRole(claimed1, cert2);
		
		assertTrue(sr1.equals(sr2));
		assertFalse(sr1.equals(sr3));
		assertFalse(sr1.equals(sr4));
		assertFalse(sr1.equals(sr5));
		assertFalse(sr1.equals(sr6));
	}
 
}
