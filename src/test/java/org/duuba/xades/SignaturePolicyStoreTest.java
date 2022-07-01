package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class SignaturePolicyStoreTest {

	private static ObjectIdentifier T_ID = new ObjectIdentifier("doc-spec-id-1", null, null, null);
	
	@Test
	void testWithDoc() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final byte[] docBytes = "This is a very short policy document".getBytes();
		
		assertDoesNotThrow(() -> new SignaturePolicyStore(T_ID, docBytes, null, null).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignaturePolicyStore", created.getLocalName());
		assertEquals(Constants.XADES_141_NS_URI, created.getNamespaceURI());

		assertEquals(2, created.getChildNodes().getLength());
		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_141_NS_URI, "SPDocSpecification").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_141_NS_URI, "SignaturePolicyDocument").getLength());
	}

	@Test
	void testWithRef() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String docRef = "https://policy.store.for.test/docRef";
		
		assertDoesNotThrow(() -> new SignaturePolicyStore(T_ID, null, docRef, null).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignaturePolicyStore", created.getLocalName());
		assertEquals(Constants.XADES_141_NS_URI, created.getNamespaceURI());

		assertEquals(2, created.getChildNodes().getLength());
		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_141_NS_URI, "SPDocSpecification").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_141_NS_URI, "SigPolDocLocalURI").getLength());
	}

	@Test
	void testWithId() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		
		assertDoesNotThrow(() -> new SignaturePolicyStore(T_ID, null, null, id).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignaturePolicyStore", created.getLocalName());
		assertEquals(Constants.XADES_141_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
	}
	
	@Test
	void testWithoutId() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new SignaturePolicyStore(T_ID, null, null, null).marshal(xwriter, "", context));
		
		assertNull(xwriter.getCreatedElement().getAttributeNode("Id"));
	}
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		
		final ObjectIdentifier oid2 = new ObjectIdentifier("doc-spec-id-2", null, null, null);
		
		final byte[] docBytes1 = "This is a very short policy document".getBytes();
		final byte[] docBytes2 = "This is a slightly longer policy document".getBytes();
		final String docRef1 = "https://policy.store.for.test/docRef";
		final String docRef2 = "https://policy.store.for.test/docRef/2";
		
		SignaturePolicyStore sps1 = new SignaturePolicyStore(T_ID, docBytes1, null, id1);
		SignaturePolicyStore sps2 = new SignaturePolicyStore(T_ID, docBytes1, null, null);
		SignaturePolicyStore sps3 = new SignaturePolicyStore(T_ID, docBytes1, null, id2);

		SignaturePolicyStore sps4 = new SignaturePolicyStore(T_ID, docBytes2, null, id1);
		SignaturePolicyStore sps5 = new SignaturePolicyStore(T_ID, docBytes2, null, null);
		
		SignaturePolicyStore sps6 = new SignaturePolicyStore(oid2, null, docRef1, id1);		
		SignaturePolicyStore sps7 = new SignaturePolicyStore(oid2, null, docRef1, id2);		
		SignaturePolicyStore sps8 = new SignaturePolicyStore(oid2, null, docRef1, null);		
		SignaturePolicyStore sps9 = new SignaturePolicyStore(T_ID, null, docRef1, id1);		
		
		assertTrue(sps1.equals(new SignaturePolicyStore(T_ID, docBytes1, null, id1)));
		assertFalse(sps1.equals(sps2));
		assertFalse(sps1.equals(sps3));
		assertFalse(sps1.equals(sps4));
		assertFalse(sps1.equals(sps5));
		assertFalse(sps1.equals(sps9));
		
		assertTrue(sps6.equals(new SignaturePolicyStore(oid2, null, docRef1, id1)));
		assertFalse(sps6.equals(sps7));
		assertFalse(sps6.equals(sps8));
		assertFalse(sps6.equals(sps9));		
	}
}
