package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.util.encoders.Base64;
import org.duuba.xades.AbstractEncapsulatedPKIDataTypeElement.Encoding;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.w3c.dom.Element;

class AbstractEncapsulatedPKIDataTypeElementTest {

	@Test
	void testWithId() throws ParserConfigurationException {
		final String id = UUID.randomUUID().toString();
		final byte[] data = "HelloWorldStringToGetSomeBytes".getBytes();
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
	
		assertDoesNotThrow(() -> new TestEncapsulatedPKIDataTypeElement(id, data, Encoding.DER)
										.marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertNotNull(created.getAttributes());
		assertNotNull(created.getAttributes().getNamedItem("Id"));
		assertEquals(id, created.getAttributes().getNamedItem("Id").getNodeValue());

		assertArrayEquals(data, Base64.decode(created.getTextContent()));
	}

	@Test
	void testNoId() throws ParserConfigurationException {
		final byte[] data = "HelloWorldStringToGetSomeBytes".getBytes();
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestEncapsulatedPKIDataTypeElement(null, data, Encoding.DER)
										.marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertNotNull(created.getAttributes());
		assertNull(created.getAttributes().getNamedItem("Id"));		
	}

	@ParameterizedTest
	@EnumSource(Encoding.class)
	void testEncodings(Encoding enc) throws ParserConfigurationException {
		final byte[] data = "HelloWorldStringToGetSomeBytes".getBytes();
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestEncapsulatedPKIDataTypeElement(null, data, enc)
										.marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertNotNull(created.getAttributes());
		assertNotNull(created.getAttributes().getNamedItem("Encoding"));
		String encVal = created.getAttributes().getNamedItem("Encoding").getNodeValue();
		switch(enc) {
		case BER : assertEquals("http://uri.etsi.org/01903/v1.2.2#BER", encVal); break;		
		case DER : assertEquals("http://uri.etsi.org/01903/v1.2.2#DER", encVal); break;		
		case CER : assertEquals("http://uri.etsi.org/01903/v1.2.2#CER", encVal); break;		
		case PER : assertEquals("http://uri.etsi.org/01903/v1.2.2#PER", encVal); break;		
		case XER : assertEquals("http://uri.etsi.org/01903/v1.2.2#XER", encVal); break;					
		}
	}
	
	@Test
	void testEquals() {
		final byte[] data1 = "HelloWorldStringToGetSomeBytes".getBytes();
		final byte[] data2 = "HelloWorldStringToGetSomeMoreBytes".getBytes();
		
		TestEncapsulatedPKIDataTypeElement e1 = new TestEncapsulatedPKIDataTypeElement(null, data1, Encoding.DER);
		TestEncapsulatedPKIDataTypeElement e2 = new TestEncapsulatedPKIDataTypeElement(null, data1, Encoding.DER);
		TestEncapsulatedPKIDataTypeElement e3 = new TestEncapsulatedPKIDataTypeElement(null, data1, Encoding.PER);
		TestEncapsulatedPKIDataTypeElement e4 = new TestEncapsulatedPKIDataTypeElement("id", data1, Encoding.DER);
		TestEncapsulatedPKIDataTypeElement e5 = new TestEncapsulatedPKIDataTypeElement("id", data1, Encoding.CER);
		TestEncapsulatedPKIDataTypeElement e6 = new TestEncapsulatedPKIDataTypeElement(null, null, null);
		TestEncapsulatedPKIDataTypeElement e7 = new TestEncapsulatedPKIDataTypeElement(null, data2, Encoding.DER);
		
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));
		assertFalse(e1.equals(e5));
		assertFalse(e1.equals(e6));
		assertFalse(e1.equals(e7));		
	}
	
	static class TestEncapsulatedPKIDataTypeElement extends AbstractEncapsulatedPKIDataTypeElement {
		public TestEncapsulatedPKIDataTypeElement(String id, byte[] data, Encoding encoding) {
			super(id, data, encoding);
		}

		@Override
		protected QName getName() {
			return new QName("Test");
		}
		
	}
}
