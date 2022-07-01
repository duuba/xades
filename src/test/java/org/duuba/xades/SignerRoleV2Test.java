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

class SignerRoleV2Test {

	@Test
	void testAll() throws ParserConfigurationException, CertificateEncodingException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final List<ClaimedRole> claimed = new ArrayList<>();
		claimed.add(new ClaimedRole(null));
		claimed.add(new ClaimedRole(null));
		
		final List<CertifiedRole> cert = new ArrayList<>();
		cert.add(new CertifiedRoleV2("certify-1", context.getCertificate()));
		
		final List<SignedAssertion> asserts = new ArrayList<>();
		asserts.add(new SignedAssertion(null));		
		asserts.add(new SignedAssertion(null));
		asserts.add(new SignedAssertion(null));
		
		assertDoesNotThrow(() -> new SignerRoleV2(claimed, cert, asserts).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignerRoleV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(3, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("ClaimedRoles", children.item(0).getLocalName());
		NodeList rClaimed = children.item(0).getChildNodes();
		assertEquals(claimed.size(), rClaimed.getLength());
		for(int i = 0; i < rClaimed.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rClaimed.item(i).getNamespaceURI());
			assertEquals("ClaimedRole", rClaimed.item(i).getLocalName());
		}
		
		assertEquals("CertifiedRolesV2", children.item(1).getLocalName());
		NodeList rCertfd = children.item(1).getChildNodes();
		assertEquals(cert.size(), rCertfd.getLength());
		for(int i = 0; i < rCertfd.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rCertfd.item(i).getNamespaceURI());
			assertEquals("CertifiedRole", rCertfd.item(i).getLocalName());
		}

		assertEquals("SignedAssertions", children.item(2).getLocalName());
		NodeList rAsserts = children.item(2).getChildNodes();
		assertEquals(asserts.size(), rAsserts.getLength());
		for(int i = 0; i < rAsserts.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rAsserts.item(i).getNamespaceURI());
			assertEquals("SignedAssertion", rAsserts.item(i).getLocalName());
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
		
		final List<SignedAssertion> asserts = null;
		
		assertDoesNotThrow(() -> new SignerRoleV2(claimed, cert, asserts).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignerRoleV2", created.getLocalName());
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
	void testCertifiedOnly() throws ParserConfigurationException, CertificateEncodingException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final List<ClaimedRole> claimed = null;
		
		final List<CertifiedRole> cert = new ArrayList<>();
		cert.add(new CertifiedRoleV2("certify-1", context.getCertificate()));
		
		final List<SignedAssertion> asserts = new ArrayList<>();
		
		assertDoesNotThrow(() -> new SignerRoleV2(claimed, cert, asserts).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignerRoleV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		
		assertEquals("CertifiedRolesV2", children.item(0).getLocalName());
		NodeList rCertfd = children.item(0).getChildNodes();
		assertEquals(cert.size(), rCertfd.getLength());
		for(int i = 0; i < rCertfd.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rCertfd.item(i).getNamespaceURI());
			assertEquals("CertifiedRole", rCertfd.item(i).getLocalName());
		}

	}

	@Test
	void testAssertsOnly() throws ParserConfigurationException, CertificateEncodingException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final List<ClaimedRole> claimed = new ArrayList<>();
		
		final List<CertifiedRole> cert = null;
		
		final List<SignedAssertion> asserts = new ArrayList<>();
		asserts.add(new SignedAssertion(null));		
		
		assertDoesNotThrow(() -> new SignerRoleV2(claimed, cert, asserts).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignerRoleV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("SignedAssertions", children.item(0).getLocalName());
		NodeList rAsserts = children.item(0).getChildNodes();
		assertEquals(asserts.size(), rAsserts.getLength());
		for(int i = 0; i < rAsserts.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, rAsserts.item(i).getNamespaceURI());
			assertEquals("SignedAssertion", rAsserts.item(i).getLocalName());
		}
	}

	@Test
	void testEquals() throws CertificateEncodingException {
		X509Certificate certificate = new TestCryptoContext().getCertificate();
		final List<ClaimedRole> claimed = new ArrayList<>();
		claimed.add(new ClaimedRole(null));
		
		final List<CertifiedRole> cert = new ArrayList<>();
		cert.add(new CertifiedRoleV1("certify-1", certificate));
		cert.add(new CertifiedRoleV1("certify-2", certificate));
		
		final List<SignedAssertion> asserts1 = new ArrayList<>();
		final List<SignedAssertion> asserts2 = new ArrayList<>();
		asserts1.add(new SignedAssertion(null));		
		asserts2.add(new SignedAssertion(null));		
		asserts1.add(new SignedAssertion(null));				
		
		SignerRoleV2 sr1 = new SignerRoleV2(claimed, cert, asserts1);
		SignerRoleV2 sr2 = new SignerRoleV2(claimed, cert, asserts1);
		SignerRoleV2 sr3 = new SignerRoleV2(claimed, cert, null);
		SignerRoleV2 sr4 = new SignerRoleV2(claimed, cert, asserts2);
		
		assertTrue(sr1.equals(sr2));
		assertFalse(sr1.equals(sr3));
		assertFalse(sr1.equals(sr4));
	}
}
