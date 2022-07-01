package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class XadesElementTest {
	
	@Test
	void testLocalNameOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestElement(new QName("Hello")).marshal(xwriter, null, context));
		
		Element created = xwriter.getCreatedElement();
		
		assertNotNull(created);
		assertNull(created.getNamespaceURI());
		assertNull(created.getPrefix());
		assertEquals("Hello", created.getLocalName());
		assertEquals("Hello World!", created.getTextContent());
		assertEquals(1, created.getChildNodes().getLength());
	}

	@Test
	void testQNameNoPrefix() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestElement(new QName("http://test.xades.holodeck-b2b.org/schemas", "Hello"))
												.marshal(xwriter, null, context));
		
		Element created = xwriter.getCreatedElement();
		
		assertNotNull(created);
		assertEquals("http://test.xades.holodeck-b2b.org/schemas", created.getNamespaceURI());
		assertNull(created.getPrefix());
		assertEquals("Hello", created.getLocalName());
	}
	
	@Test
	void testQNamePrefix() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestElement(new QName("http://test.xades.holodeck-b2b.org/schemas", "Hello", "test"))
							.marshal(xwriter, null, context));
		
		Element created = xwriter.getCreatedElement();
		
		assertNotNull(created);
		assertEquals("http://test.xades.holodeck-b2b.org/schemas", created.getNamespaceURI());
		assertEquals("test", created.getPrefix());
		assertEquals("Hello", created.getLocalName());
	}
	
	
	@Test
	void testContextPrefix() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		context.putNamespacePrefix("http://test.xades.holodeck-b2b.org/schemas", "ctx");
		
		assertDoesNotThrow(() -> new TestElement(new QName("http://test.xades.holodeck-b2b.org/schemas", "Hello", "test"))
												.marshal(xwriter, null, context));
		
		Element created = xwriter.getCreatedElement();
		
		assertNotNull(created);
		assertEquals("http://test.xades.holodeck-b2b.org/schemas", created.getNamespaceURI());
		assertEquals("ctx", created.getPrefix());
		assertEquals("Hello", created.getLocalName());
	}
	
	@Test
	void testEquals() {
		final TestElement e1 = new TestElement(new QName("http://namespace1", "Element"));
		final TestElement e2 = new TestElement(new QName("http://namespace1", "Element"));
		final TestElement e3 = new TestElement(new QName("http://namespace1", "AnotherElement"));
		final TestElement e4 = new TestElement(new QName("http://namespace2", "AnotherElement"));
		final XadesElement e5 = new XadesElement() {
			@Override
			protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
					throws MarshalException {				
			}
			
			@Override
			protected QName getName() {
				return new QName("http://namespace1", "Element");
			}
		};
		
		assertTrue(e1.equals(e1));
		assertFalse(e1.equals(null));
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));		
		assertFalse(e1.equals(e5));		
	}
	
	class TestElement extends XadesElement {
		private QName	name;
		
		TestElement(QName name) {
			this.name = name;
		}
		
		@Override
		protected QName getName() {
			return name;
		}

		@Override
		protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
				throws MarshalException {
			xwriter.writeCharacters("Hello World!");			
		}		
	}
	

}
