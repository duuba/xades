package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class DataObjectFormatTest {

	static final String 	T_REF_URI = "#this-is-a-test-reference";
	
	@Test
	void testDescription() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();

		final String description = "This is just a test";
		
		assertDoesNotThrow(() -> new DataObjectFormat(T_REF_URI, description, null, null, null)
													.marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("DataObjectFormat", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(T_REF_URI, created.getAttribute("ObjectReference"));
		
		NodeList elements = created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Description");
		assertEquals(1, elements.getLength());
		assertEquals(description, elements.item(0).getTextContent());
		
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ObjectIdentifier").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "MimeType").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Encoding").getLength());
	}
	
	@Test
	void testObjectId() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();

		final ObjectIdentifier oid = new ObjectIdentifier("object-id-1", QualifierType.OIDAsURN, null, null);
		
		assertDoesNotThrow(() -> new DataObjectFormat(T_REF_URI, null, oid, null, null)
													.marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("DataObjectFormat", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(T_REF_URI, created.getAttribute("ObjectReference"));
		
		NodeList elements = created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ObjectIdentifier");
		assertEquals(1, elements.getLength());
		assertEquals(oid.getIdentifier(), elements.item(0).getTextContent());
		
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Description").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "MimeType").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Encoding").getLength());
	}
	
	@Test
	void testMimeType() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();

		final String mimeType = "application/pdf";
		
		assertDoesNotThrow(() -> new DataObjectFormat(T_REF_URI, null, null, mimeType, null)
													.marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("DataObjectFormat", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(T_REF_URI, created.getAttribute("ObjectReference"));
		
		NodeList elements = created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "MimeType");
		assertEquals(1, elements.getLength());
		assertEquals(mimeType, elements.item(0).getTextContent());
		
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ObjectIdentifier").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Description").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Encoding").getLength());
	}	
	
	@Test
	void testEncoding() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		final String encoding = "nothing-special";
		
		assertDoesNotThrow(() -> new DataObjectFormat(T_REF_URI, null, null, null, encoding)
				.marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("DataObjectFormat", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(T_REF_URI, created.getAttribute("ObjectReference"));
		
		NodeList elements = created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Encoding");
		assertEquals(1, elements.getLength());
		assertEquals(encoding, elements.item(0).getTextContent());
		
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ObjectIdentifier").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Description").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "MimeType").getLength());
	}	
	
	@Test
	void testEquals() {
		final ObjectIdentifier oid1 = new ObjectIdentifier("object-id-1", QualifierType.OIDAsURN, null, null);
		final ObjectIdentifier oid2 = new ObjectIdentifier("object-id-2", QualifierType.OIDAsURN, null, null);
		
		DataObjectFormat dof1 = new DataObjectFormat(T_REF_URI, "A data object", oid1, "text/ascii", "ascii");
		DataObjectFormat dof2 = new DataObjectFormat(T_REF_URI, "A data object", oid1, "text/ascii", "ascii");
		DataObjectFormat dof3 = new DataObjectFormat(T_REF_URI, null, oid1, "text/ascii", "ascii");
		DataObjectFormat dof4 = new DataObjectFormat(T_REF_URI, "Another object", oid1, "text/ascii", "ascii");
		DataObjectFormat dof5 = new DataObjectFormat(T_REF_URI, "A data object", null, "text/ascii", "ascii");
		DataObjectFormat dof6 = new DataObjectFormat(T_REF_URI, "A data object", oid2, "text/ascii", "ascii");
		DataObjectFormat dof7 = new DataObjectFormat(T_REF_URI, "A data object", oid1, null, "ascii");
		DataObjectFormat dof8 = new DataObjectFormat(T_REF_URI, "A data object", oid1, "application/text", "ascii");
		DataObjectFormat dof9 = new DataObjectFormat(T_REF_URI, "A data object", oid1, "text/ascii", null);
		DataObjectFormat dof10 = new DataObjectFormat(T_REF_URI, "A data object", oid1, "text/ascii", "utf-8");
		
		assertTrue(dof1.equals(dof2));
		assertFalse(dof1.equals(dof3));
		assertFalse(dof1.equals(dof3));
		assertFalse(dof1.equals(dof4));
		assertFalse(dof1.equals(dof5));
		assertFalse(dof1.equals(dof6));
		assertFalse(dof1.equals(dof7));
		assertFalse(dof1.equals(dof8));
		assertFalse(dof1.equals(dof9));
		assertFalse(dof1.equals(dof10));		
	}
}
