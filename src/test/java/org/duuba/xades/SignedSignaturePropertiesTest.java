package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.algorithms.JCEMapper;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SignedSignaturePropertiesTest {

	@BeforeAll
	static void setupJCEMapper() {
		JCEMapper.registerDefaultAlgorithms();
	}

	@Test
	void testAll() throws ParserConfigurationException, NoSuchAlgorithmException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final ZonedDateTime signTime = ZonedDateTime.now();
		final SigningCertificate cert = new SigningCertificate(Collections.singletonList(context.getCertificate()), DigestMethod.SHA256);
		final SignaturePolicyIdentifier polId = new SignaturePolicyIdentifier();
		final SignatureProductionPlace place = new SignatureProductionPlace("Leiden", null, null, null);
		final SignerRole role = new SignerRole(null, null);
		
		final List<Node> custom = new ArrayList<>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		Document builder = builderFactory.newDocumentBuilder().newDocument();		
		custom.add(builder.createElementNS(null, "CustomSignatureProperty"));
		custom.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
				
		assertDoesNotThrow(() ->
							new SignedSignatureProperties(id, signTime, cert, polId, place, role, custom)
														.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedSignatureProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(7, children.getLength());
		int i = 0;
		assertEquals("SigningTime", children.item(i++).getLocalName());
		assertEquals("SigningCertificate", children.item(i++).getLocalName());
		assertEquals("SignaturePolicyIdentifier", children.item(i++).getLocalName());
		assertEquals("SignatureProductionPlace", children.item(i++).getLocalName());
		assertEquals("SignerRole", children.item(i++).getLocalName());
		assertEquals("CustomSignatureProperty", children.item(i++).getLocalName());
		assertEquals("AnotherCustomSignatureProperty", children.item(i++).getLocalName());
	}

	@Test
	void testNoIdNoCustomProps() throws ParserConfigurationException, NoSuchAlgorithmException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final ZonedDateTime signTime = ZonedDateTime.now();
		final SigningCertificate cert = new SigningCertificate(Collections.singletonList(context.getCertificate()), DigestMethod.SHA256);
		final SignaturePolicyIdentifier polId = new SignaturePolicyIdentifier();
		final SignatureProductionPlace place = new SignatureProductionPlaceV2("Leiden", null, null, null, "NL");
		final SignerRole role = new SignerRoleV2(null, null, null);
				
		assertDoesNotThrow(() -> new SignedSignatureProperties(null, signTime, cert, polId, place, role, null)
										.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedSignatureProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(5, children.getLength());		
		assertEquals("SigningTime", children.item(0).getLocalName());
		assertEquals("SigningCertificate", children.item(1).getLocalName());
		assertEquals("SignaturePolicyIdentifier", children.item(2).getLocalName());
		assertEquals("SignatureProductionPlaceV2", children.item(3).getLocalName());
		assertEquals("SignerRoleV2", children.item(4).getLocalName());
	}	
	
	@Test
	void testNoSigningTime() throws ParserConfigurationException, NoSuchAlgorithmException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final ZonedDateTime signTime = null;
		final SigningCertificate cert = new SigningCertificate(Collections.singletonList(context.getCertificate()), DigestMethod.SHA256);
		final SignaturePolicyIdentifier polId = new SignaturePolicyIdentifier();
		final SignatureProductionPlace place = new SignatureProductionPlace("Leiden", null, null, null);
		final SignerRole role = new SignerRole(null, null);
		
		final List<Node> custom = new ArrayList<>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		Document builder = builderFactory.newDocumentBuilder().newDocument();		
		custom.add(builder.createElementNS(null, "CustomSignatureProperty"));
		custom.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
				
		assertDoesNotThrow(() ->
							new SignedSignatureProperties(id, signTime, cert, polId, place, role, custom)
														.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		NodeList children = created.getChildNodes();		
		assertEquals(6, children.getLength());
		int i = 0;
		assertEquals("SigningCertificate", children.item(i++).getLocalName());
		assertEquals("SignaturePolicyIdentifier", children.item(i++).getLocalName());
		assertEquals("SignatureProductionPlace", children.item(i++).getLocalName());
		assertEquals("SignerRole", children.item(i++).getLocalName());
		assertEquals("CustomSignatureProperty", children.item(i++).getLocalName());
		assertEquals("AnotherCustomSignatureProperty", children.item(i++).getLocalName());
	}	
	
	@Test
	void testNoSigningCert() throws ParserConfigurationException, NoSuchAlgorithmException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final ZonedDateTime signTime = ZonedDateTime.now();
		final SigningCertificate cert = null;
		final SignaturePolicyIdentifier polId = new SignaturePolicyIdentifier();
		final SignatureProductionPlace place = new SignatureProductionPlace("Leiden", null, null, null);
		final SignerRole role = new SignerRole(null, null);
		
		final List<Node> custom = new ArrayList<>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		Document builder = builderFactory.newDocumentBuilder().newDocument();		
		custom.add(builder.createElementNS(null, "CustomSignatureProperty"));
		custom.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
				
		assertDoesNotThrow(() ->
							new SignedSignatureProperties(id, signTime, cert, polId, place, role, custom)
														.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedSignatureProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(6, children.getLength());
		int i = 0;
		assertEquals("SigningTime", children.item(i++).getLocalName());
		assertEquals("SignaturePolicyIdentifier", children.item(i++).getLocalName());
		assertEquals("SignatureProductionPlace", children.item(i++).getLocalName());
		assertEquals("SignerRole", children.item(i++).getLocalName());
		assertEquals("CustomSignatureProperty", children.item(i++).getLocalName());
		assertEquals("AnotherCustomSignatureProperty", children.item(i++).getLocalName());
	}
	
	@Test
	void testNoSigPolicy() throws ParserConfigurationException, NoSuchAlgorithmException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final ZonedDateTime signTime = ZonedDateTime.now();
		final SigningCertificate cert = new SigningCertificate(Collections.singletonList(context.getCertificate()), DigestMethod.SHA256);
		final SignaturePolicyIdentifier polId = null;
		final SignatureProductionPlace place = new SignatureProductionPlace("Leiden", null, null, null);
		final SignerRole role = new SignerRole(null, null);
		
		final List<Node> custom = new ArrayList<>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		Document builder = builderFactory.newDocumentBuilder().newDocument();		
		custom.add(builder.createElementNS(null, "CustomSignatureProperty"));
		custom.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
				
		assertDoesNotThrow(() ->
							new SignedSignatureProperties(id, signTime, cert, polId, place, role, custom)
														.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedSignatureProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(6, children.getLength());
		int i = 0;
		assertEquals("SigningTime", children.item(i++).getLocalName());
		assertEquals("SigningCertificate", children.item(i++).getLocalName());
		assertEquals("SignatureProductionPlace", children.item(i++).getLocalName());
		assertEquals("SignerRole", children.item(i++).getLocalName());
		assertEquals("CustomSignatureProperty", children.item(i++).getLocalName());
		assertEquals("AnotherCustomSignatureProperty", children.item(i++).getLocalName());
	}
	
	@Test
	void testNoSigningPlace() throws ParserConfigurationException, NoSuchAlgorithmException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final ZonedDateTime signTime = ZonedDateTime.now();
		final SigningCertificate cert = new SigningCertificate(Collections.singletonList(context.getCertificate()), DigestMethod.SHA256);
		final SignaturePolicyIdentifier polId = new SignaturePolicyIdentifier();
		final SignatureProductionPlace place = null;
		final SignerRole role = new SignerRole(null, null);
		
		final List<Node> custom = new ArrayList<>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		Document builder = builderFactory.newDocumentBuilder().newDocument();		
		custom.add(builder.createElementNS(null, "CustomSignatureProperty"));
		custom.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
				
		assertDoesNotThrow(() ->
							new SignedSignatureProperties(id, signTime, cert, polId, place, role, custom)
														.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedSignatureProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(6, children.getLength());
		int i = 0;
		assertEquals("SigningTime", children.item(i++).getLocalName());
		assertEquals("SigningCertificate", children.item(i++).getLocalName());
		assertEquals("SignaturePolicyIdentifier", children.item(i++).getLocalName());
		assertEquals("SignerRole", children.item(i++).getLocalName());
		assertEquals("CustomSignatureProperty", children.item(i++).getLocalName());
		assertEquals("AnotherCustomSignatureProperty", children.item(i++).getLocalName());
	}
	
	@Test
	void testNoRole() throws ParserConfigurationException, NoSuchAlgorithmException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final ZonedDateTime signTime = ZonedDateTime.now();
		final SigningCertificate cert = new SigningCertificate(Collections.singletonList(context.getCertificate()), DigestMethod.SHA256);
		final SignaturePolicyIdentifier polId = new SignaturePolicyIdentifier();
		final SignatureProductionPlace place = new SignatureProductionPlace("Leiden", null, null, null);
		final SignerRole role = null;
		
		final List<Node> custom = new ArrayList<>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		Document builder = builderFactory.newDocumentBuilder().newDocument();		
		custom.add(builder.createElementNS(null, "CustomSignatureProperty"));
		custom.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
				
		assertDoesNotThrow(() ->
							new SignedSignatureProperties(id, signTime, cert, polId, place, role, custom)
														.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedSignatureProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(6, children.getLength());
		int i = 0;
		assertEquals("SigningTime", children.item(i++).getLocalName());
		assertEquals("SigningCertificate", children.item(i++).getLocalName());
		assertEquals("SignaturePolicyIdentifier", children.item(i++).getLocalName());
		assertEquals("SignatureProductionPlace", children.item(i++).getLocalName());
		assertEquals("CustomSignatureProperty", children.item(i++).getLocalName());
		assertEquals("AnotherCustomSignatureProperty", children.item(i++).getLocalName());
	}	
	
	@Test
	void testEquals() throws NoSuchAlgorithmException, ParserConfigurationException {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		final ZonedDateTime signTime1 = ZonedDateTime.now();
		final ZonedDateTime signTime2 = ZonedDateTime.now().minusDays(1);
		final List<X509Certificate> certList = Collections.singletonList(new TestCryptoContext().getCertificate());
		final SigningCertificate cert1 = new SigningCertificate(certList, DigestMethod.SHA256);
		final SigningCertificate cert2 = new SigningCertificate(certList, DigestMethod.SHA512);
		final SignaturePolicyIdentifier polId1 = new SignaturePolicyIdentifier();
		final SignaturePolicyIdentifier polId2 = new SignaturePolicyIdentifier(SignaturePolicyIdentifierTest.T_POL_ID, 
				null, DigestMethod.SHA512, SignaturePolicyIdentifierTest.T_DIGEST_VALUE, null);
		final SignatureProductionPlace place1 = new SignatureProductionPlace("Leiden", null, null, null);
		final SignatureProductionPlace place2 = new SignatureProductionPlace("Leiden", null, null, "NL");
		final SignerRole role1 = new SignerRole(Collections.singletonList(new ClaimedRole(null)), null);
		final SignerRole role2 = new SignerRoleV2(Collections.singletonList(new ClaimedRole(null)), null, null);
		
		final List<Node> custom1 = new ArrayList<>();
		final List<Node> custom2 = new ArrayList<>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		Document builder = builderFactory.newDocumentBuilder().newDocument();		
		custom1.add(builder.createElementNS(null, "CustomSignatureProperty"));
		custom1.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
		custom2.add(builder.createElementNS(null, "AnotherCustomSignatureProperty"));
		custom2.add(builder.createElementNS(null, "CustomSignatureProperty"));
		
		SignedSignatureProperties sp1 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, place1, role1, custom1);
		SignedSignatureProperties sp2 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, place1, role1, custom1);
		SignedSignatureProperties sp3 = new SignedSignatureProperties(null, signTime1, cert1, polId1, place1, role1, custom1);
		SignedSignatureProperties sp4 = new SignedSignatureProperties(id2, signTime1, cert1, polId1, place1, role1, custom1);
		SignedSignatureProperties sp5 = new SignedSignatureProperties(id1, null, cert1, polId1, place1, role1, custom1);
		SignedSignatureProperties sp6 = new SignedSignatureProperties(id1, signTime2, cert1, polId1, place1, role1, custom1);
		SignedSignatureProperties sp7 = new SignedSignatureProperties(id1, signTime1, null, polId1, place1, role1, custom1);
		SignedSignatureProperties sp8 = new SignedSignatureProperties(id1, signTime1, cert2, polId1, place1, role1, custom1);
		SignedSignatureProperties sp9 = new SignedSignatureProperties(id1, signTime1, cert1, null, place1, role1, custom1);
		SignedSignatureProperties sp10 = new SignedSignatureProperties(id1, signTime1, cert1, polId2, place1, role1, custom1);
		SignedSignatureProperties sp11 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, null, role1, custom1);
		SignedSignatureProperties sp12 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, place2, role1, custom1);
		SignedSignatureProperties sp13 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, place1, null, custom1);
		SignedSignatureProperties sp14 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, place1, role2, custom1);
		SignedSignatureProperties sp15 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, place1, role2, null);
		SignedSignatureProperties sp16 = new SignedSignatureProperties(id1, signTime1, cert1, polId1, place1, role2, custom2);
		
		assertTrue(sp1.equals(sp2));
		assertFalse(sp1.equals(sp3));
		assertFalse(sp1.equals(sp4));
		assertFalse(sp1.equals(sp5));
		assertFalse(sp1.equals(sp6));
		assertFalse(sp1.equals(sp7));
		assertFalse(sp1.equals(sp8));
		assertFalse(sp1.equals(sp9));
		assertFalse(sp1.equals(sp10));
		assertFalse(sp1.equals(sp11));
		assertFalse(sp1.equals(sp12));
		assertFalse(sp1.equals(sp13));
		assertFalse(sp1.equals(sp14));
		assertFalse(sp1.equals(sp15));
		assertFalse(sp1.equals(sp16));
	}
}
