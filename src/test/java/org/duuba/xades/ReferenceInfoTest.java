package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class ReferenceInfoTest {

	static final byte[] T_DIGEST = "HelloWorldStringToGetSomeBytes".getBytes();

	@Test
	void testWithIdWithoutURI() throws ParserConfigurationException {
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		
		assertDoesNotThrow(() -> new ReferenceInfo(id, null, DigestMethod.SHA256, T_DIGEST).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("ReferenceInfo", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		assertNull(created.getAttributeNode("URI"));
	}
	
	@Test
	void testWithOutIdWithURI() throws ParserConfigurationException {
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String uri = "#just-a-local-ref-for-testing";
		
		assertDoesNotThrow(() -> new ReferenceInfo(null, uri, DigestMethod.SHA256, T_DIGEST).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("ReferenceInfo", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(uri, created.getAttribute("URI"));
		assertNull(created.getAttributeNode("Id"));
	}

	@Test
	void testWithIdWithURI() throws ParserConfigurationException {
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();		
		final String uri = "#just-a-local-ref-for-testing";
		
		assertDoesNotThrow(() -> new ReferenceInfo(id, uri, DigestMethod.SHA256, T_DIGEST).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("ReferenceInfo", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		assertEquals(uri, created.getAttribute("URI"));
	}	
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();		
		final String id2 = UUID.randomUUID().toString();		
		final String uri1 = "#just-a-local-ref-for-testing";
		final String uri2 = "#just-another-ref-for-testing";
		final byte[] digest2 = "ADifferentStringToGetSomeBytes".getBytes();
		
		ReferenceInfo ri1 = new ReferenceInfo(id1, uri1, DigestMethod.SHA256, T_DIGEST);
		ReferenceInfo ri2 = new ReferenceInfo(id1, uri1, DigestMethod.SHA256, T_DIGEST);
		ReferenceInfo ri3 = new ReferenceInfo(null, uri1, DigestMethod.SHA256, T_DIGEST);
		ReferenceInfo ri4 = new ReferenceInfo(id2, uri1, DigestMethod.SHA256, T_DIGEST);
		ReferenceInfo ri5 = new ReferenceInfo(id1, null, DigestMethod.SHA256, T_DIGEST);
		ReferenceInfo ri6 = new ReferenceInfo(id1, uri2, DigestMethod.SHA256, T_DIGEST);
		ReferenceInfo ri7 = new ReferenceInfo(id1, uri1, DigestMethod.SHA512, T_DIGEST);
		ReferenceInfo ri8 = new ReferenceInfo(id1, uri1, DigestMethod.SHA256, digest2);
		
		assertTrue(ri1.equals(ri2));
		assertFalse(ri1.equals(ri3));
		assertFalse(ri1.equals(ri4));
		assertFalse(ri1.equals(ri5));
		assertFalse(ri1.equals(ri6));
		assertFalse(ri1.equals(ri7));
		assertFalse(ri1.equals(ri8));
	}
}
