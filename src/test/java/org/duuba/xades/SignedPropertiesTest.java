package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class SignedPropertiesTest {

	@Test
	void testBoth() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final SignedSignatureProperties sigProps = 
						new SignedSignatureProperties("sig-props", ZonedDateTime.now(), null, null, null, null, null);
		final SignedDataObjectProperties dataProps = new SignedDataObjectProperties("data-props", null, null);
				
		assertDoesNotThrow(() -> new SignedProperties(id, sigProps, dataProps).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(2, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("SignedSignatureProperties", children.item(0).getLocalName());
		assertEquals("SignedDataObjectProperties", children.item(1).getLocalName());

	}

	@Test
	void testSignatureOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final SignedSignatureProperties sigProps = 
				new SignedSignatureProperties("sig-props", ZonedDateTime.now(), null, null, null, null, null);
		
		assertDoesNotThrow(() -> new SignedProperties(null, sigProps, null).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		
		assertEquals("SignedSignatureProperties", children.item(0).getLocalName());		
	}

	@Test
	void testDataOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final SignedDataObjectProperties dataProps = new SignedDataObjectProperties("data-props", null, null);
				
		assertDoesNotThrow(() -> new SignedProperties(null, null, dataProps).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("SignedDataObjectProperties", children.item(0).getLocalName());

	}	
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		final SignedSignatureProperties sigProps1 = 
						new SignedSignatureProperties("sig-props", ZonedDateTime.now(), null, null, null, null, null);
		final SignedSignatureProperties sigProps2 = 
						new SignedSignatureProperties("sig-props-2", ZonedDateTime.now(), null, null, null, null, null);
		final SignedDataObjectProperties dataProps1 = new SignedDataObjectProperties("data-props", null, null);
		final SignedDataObjectProperties dataProps2 = new SignedDataObjectProperties("data-props-2", null, null);
		
		SignedProperties sp1 = new SignedProperties(id1, sigProps1, dataProps1);
		SignedProperties sp2 = new SignedProperties(id1, sigProps1, dataProps1);
		SignedProperties sp3 = new SignedProperties(null, sigProps1, dataProps1);
		SignedProperties sp4 = new SignedProperties(id2, sigProps1, dataProps1);
		SignedProperties sp5 = new SignedProperties(id1, null, dataProps1);
		SignedProperties sp6 = new SignedProperties(id1, sigProps2, dataProps1);
		SignedProperties sp7 = new SignedProperties(id1, sigProps1, null);
		SignedProperties sp8 = new SignedProperties(id1, sigProps1, dataProps2);
		
		assertTrue(sp1.equals(sp2));
		assertFalse(sp1.equals(sp3));
		assertFalse(sp1.equals(sp4));
		assertFalse(sp1.equals(sp5));
		assertFalse(sp1.equals(sp6));
		assertFalse(sp1.equals(sp7));
		assertFalse(sp1.equals(sp8));
	}
}
