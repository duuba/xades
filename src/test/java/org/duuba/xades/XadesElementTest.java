package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.*;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.duuba.xades.XadesElement;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class XadesElementTest {
	
	@Test
	void testLocalNameOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		try {
			new TestElement(new QName("Hello")).marshal(xwriter, null, context);
		} catch (Throwable t) {
			fail(t);
		}
		
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
		
		try {
			new TestElement(new QName("http://test.xades.holodeck-b2b.org/schemas", "Hello"))
							.marshal(xwriter, null, context);
		} catch (Throwable t) {
			fail(t);
		}
		
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
		
		try {
			new TestElement(new QName("http://test.xades.holodeck-b2b.org/schemas", "Hello", "test"))
							.marshal(xwriter, null, context);
		} catch (Throwable t) {
			fail(t);
		}
		
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
		
		try {
			new TestElement(new QName("http://test.xades.holodeck-b2b.org/schemas", "Hello", "test"))
							.marshal(xwriter, null, context);
		} catch (Throwable t) {
			fail(t);
		}
		
		Element created = xwriter.getCreatedElement();
		
		assertNotNull(created);
		assertEquals("http://test.xades.holodeck-b2b.org/schemas", created.getNamespaceURI());
		assertEquals("ctx", created.getPrefix());
		assertEquals("Hello", created.getLocalName());
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
