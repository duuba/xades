package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.UnsignedDataObjectProperties.UnsignedDataObjectProperty;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class UnsignedDataObjectPropertiesTest {

	@Test
	void testWithIdAndSingleProperty() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();

		final String id = UUID.randomUUID().toString();		
		List<UnsignedDataObjectProperty> p = Collections.singletonList(new UnsignedDataObjectProperty(null));
		
		assertDoesNotThrow(() -> new UnsignedDataObjectProperties(id, p).marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("UnsignedDataObjectProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(p.size(), children.getLength());
		for (int i = 0; i < children.getLength(); i++) {
			assertEquals("UnsignedDataObjectProperty", children.item(i).getLocalName());
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		}			
	}

	@Test
	void testWithoutIdAndMultipleProperties() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();

		final String id = UUID.randomUUID().toString();		
		List<UnsignedDataObjectProperty> p = new ArrayList<>(); 
		p.add(new UnsignedDataObjectProperty(null));
		p.add(new UnsignedDataObjectProperty(null));
		p.add(new UnsignedDataObjectProperty(null));
		
		assertDoesNotThrow(() -> new UnsignedDataObjectProperties(id, p).marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("UnsignedDataObjectProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();
		
		assertEquals(p.size(), children.getLength());
		for (int i = 0; i < children.getLength(); i++) {
			assertEquals("UnsignedDataObjectProperty", children.item(i).getLocalName());
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		}			
	}

	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();		
		final String id2 = UUID.randomUUID().toString();		
		List<UnsignedDataObjectProperty> p1 = new ArrayList<>(); 
		List<UnsignedDataObjectProperty> p2 = new ArrayList<>(); 
		p1.add(new UnsignedDataObjectProperty(null));
		p2.add(new UnsignedDataObjectProperty(null));
		p2.add(new UnsignedDataObjectProperty(null));
		
		UnsignedDataObjectProperties udop1 = new UnsignedDataObjectProperties(id1, p1);
		UnsignedDataObjectProperties udop2 = new UnsignedDataObjectProperties(id1, p1);
		UnsignedDataObjectProperties udop3 = new UnsignedDataObjectProperties(null, p1);
		UnsignedDataObjectProperties udop4 = new UnsignedDataObjectProperties(id2, p1);
		UnsignedDataObjectProperties udop5 = new UnsignedDataObjectProperties(id1, p2);
		
		assertTrue(udop1.equals(udop2));
		assertFalse(udop1.equals(udop3));
		assertFalse(udop1.equals(udop4));
		assertFalse(udop1.equals(udop5));
	}
}
