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
import org.w3c.dom.NodeList;

class UnSignedPropertiesTest {

	@Test
	void testBoth() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final UnsignedSignatureProperties usigProps = new UnsignedSignatureProperties(id, 
												new SignaturePolicyStore(new ObjectIdentifier("oid", null, null, null), 
														null, "#specRef", null));
		final UnsignedDataObjectProperties dataProps = new UnsignedDataObjectProperties(null, null);
				
		assertDoesNotThrow(() -> new UnsignedProperties(id, usigProps, dataProps).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("UnsignedProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(2, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("UnsignedSignatureProperties", children.item(0).getLocalName());
		assertEquals("UnsignedDataObjectProperties", children.item(1).getLocalName());

	}

	@Test
	void testSignatureOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final UnsignedSignatureProperties usigProps = new UnsignedSignatureProperties(null, 
												new SignaturePolicyStore(new ObjectIdentifier("oid", null, null, null), 
														null, "#specRef", null));
		
		assertDoesNotThrow(() -> new UnsignedProperties(null, usigProps, null).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("UnsignedProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		
		assertEquals("UnsignedSignatureProperties", children.item(0).getLocalName());		
	}

	@Test
	void testDataOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final UnsignedDataObjectProperties dataProps = new UnsignedDataObjectProperties(null, null);
				
		assertDoesNotThrow(() -> new UnsignedProperties(null, null, dataProps).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("UnsignedProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("UnsignedDataObjectProperties", children.item(0).getLocalName());
	}	
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		final UnsignedSignatureProperties usigProps1 = new UnsignedSignatureProperties(id1, 
												new SignaturePolicyStore(new ObjectIdentifier("oid", null, null, null), 
														null, "#specRef", null));
		final UnsignedSignatureProperties usigProps2 = new UnsignedSignatureProperties(id1, 
											new SignaturePolicyStore(new ObjectIdentifier("oid-2", null, null, null), 
													null, "#specRef-2", null));
		final UnsignedDataObjectProperties dataProps1 = new UnsignedDataObjectProperties(null, null);
		final UnsignedDataObjectProperties dataProps2 = new UnsignedDataObjectProperties("dop-2", null);
		
		UnsignedProperties usp1 = new UnsignedProperties(id1, usigProps1, dataProps1);
		UnsignedProperties usp2 = new UnsignedProperties(id1, usigProps1, dataProps1);
		UnsignedProperties usp3 = new UnsignedProperties(null, usigProps1, dataProps1);
		UnsignedProperties usp4 = new UnsignedProperties(id2, usigProps1, dataProps1);
		UnsignedProperties usp5 = new UnsignedProperties(id1, null, dataProps1);
		UnsignedProperties usp6 = new UnsignedProperties(id1, usigProps2, dataProps1);
		UnsignedProperties usp7 = new UnsignedProperties(id1, usigProps1, null);
		UnsignedProperties usp8 = new UnsignedProperties(id1, usigProps1, dataProps2);
		
		assertTrue(usp1.equals(usp2));
		assertFalse(usp1.equals(usp3));
		assertFalse(usp1.equals(usp4));
		assertFalse(usp1.equals(usp5));
		assertFalse(usp1.equals(usp6));
		assertFalse(usp1.equals(usp7));
		assertFalse(usp1.equals(usp8));
	}
}
