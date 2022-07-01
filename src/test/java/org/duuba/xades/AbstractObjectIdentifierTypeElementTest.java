package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class AbstractObjectIdentifierTypeElementTest {

	@Test
	void testIdWithQualifier() throws ParserConfigurationException {
		final String id = UUID.randomUUID().toString();
		final QualifierType qt = QualifierType.OIDAsURI;
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();

		assertDoesNotThrow(() -> new TestObjectIdentifierTypeElement(id, qt, null, null).marshal(xwriter, "", ctx));
		
		NodeList ids = xwriter.getCreatedElement().getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Identifier");
		assertEquals(1, ids.getLength());
		assertEquals(id, ids.item(0).getTextContent());
		assertNotNull(ids.item(0).getAttributes());
		assertEquals(qt.name(), ids.item(0).getAttributes().getNamedItem("Qualifier").getNodeValue());		
	}
	
	@Test
	void testIdWithoutQualifier() throws ParserConfigurationException {
		final String id = UUID.randomUUID().toString();
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestObjectIdentifierTypeElement(id, null, null, null).marshal(xwriter, "", ctx));
		
		NodeList ids = xwriter.getCreatedElement().getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Identifier");
		assertEquals(1, ids.getLength());
		assertEquals(id, ids.item(0).getTextContent());
		assertEquals(0, ids.item(0).getAttributes().getLength());
	}
	
	@Test
	void testDescription() throws ParserConfigurationException {
		final String id = UUID.randomUUID().toString();
		final String descr = "Just a simple test";
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestObjectIdentifierTypeElement(id, null, descr, null).marshal(xwriter, "", ctx));
		
		NodeList descrs = xwriter.getCreatedElement().getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Description");
		assertEquals(1, descrs.getLength());
		assertEquals(descr, descrs.item(0).getTextContent());
	}
	
	@Test
	void testNoDescription() throws ParserConfigurationException {
		final String id = UUID.randomUUID().toString();
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestObjectIdentifierTypeElement(id, null, null, null).marshal(xwriter, "", ctx));
		
		NodeList descrs = xwriter.getCreatedElement().getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Description");
		assertNotNull(descrs);
		assertEquals(0, descrs.getLength());
	}
	
	@Test
	void testDocRefs() throws ParserConfigurationException {
		final String id = UUID.randomUUID().toString();
		final List<String> refs = new ArrayList<>();
		refs.add("DocumentReference_1");
		refs.add("DocumentReference_2");
		refs.add("DocumentReference_3");
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestObjectIdentifierTypeElement(id, null, null, refs).marshal(xwriter, "", ctx));
		
		NodeList refE = xwriter.getCreatedElement().getElementsByTagNameNS(Constants.XADES_132_NS_URI, "DocumentationReferences").item(0)
								   				.getChildNodes();
		assertEquals(3, refE.getLength());
		for(int i = 0; i < 3; i++) {
			Element r = (Element) refE.item(i);
			assertEquals("DocumentationReference", r.getLocalName());
			assertEquals(Constants.XADES_132_NS_URI, r.getNamespaceURI());
			assertEquals("DocumentReference_" + (i+1), r.getTextContent());
		}
	}
	
	@Test
	void testNoRefs() throws ParserConfigurationException {
		final String id = UUID.randomUUID().toString();
		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestObjectIdentifierTypeElement(id, null, null, null).marshal(xwriter, "", ctx));
		
		assertEquals(0, xwriter.getCreatedElement().getElementsByTagNameNS(Constants.XADES_132_NS_URI, "DocumentationReferences").getLength());
	}
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		final List<String> refs1 = new ArrayList<>();
		final List<String> refs2 = new ArrayList<>();
		refs1.add("DocumentReference_1");
		refs1.add("DocumentReference_2");
		refs2.add("DocumentReference_2");
		refs1.add("DocumentReference_3");
		refs2.add("DocumentReference_3");

		final String descr1 = "Just a simple test";
		final String descr2 = "Another description to test correct detection of differences";
		
		final QualifierType qf1 = QualifierType.OIDAsURI;
		final QualifierType qf2 = QualifierType.OIDAsURN;
		
		TestObjectIdentifierTypeElement e1 = new TestObjectIdentifierTypeElement(id1, qf1, descr1, refs1);
		TestObjectIdentifierTypeElement e2 = new TestObjectIdentifierTypeElement(id1, qf1, descr1, refs1);
		TestObjectIdentifierTypeElement e3 = new TestObjectIdentifierTypeElement(null, qf1, descr1, refs1);
		TestObjectIdentifierTypeElement e4 = new TestObjectIdentifierTypeElement(id2, qf1, descr1, refs1);
		TestObjectIdentifierTypeElement e5 = new TestObjectIdentifierTypeElement(id1, null, descr1, refs1);
		TestObjectIdentifierTypeElement e6 = new TestObjectIdentifierTypeElement(id1, qf2, descr1, refs1);
		TestObjectIdentifierTypeElement e7 = new TestObjectIdentifierTypeElement(id1, qf1, null, refs1);
		TestObjectIdentifierTypeElement e8 = new TestObjectIdentifierTypeElement(id1, qf1, descr2, refs1);
		TestObjectIdentifierTypeElement e9 = new TestObjectIdentifierTypeElement(id1, qf1, descr1, null);
		TestObjectIdentifierTypeElement e10 = new TestObjectIdentifierTypeElement(id1, qf1, descr1, refs2);
		
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));
		assertFalse(e1.equals(e5));
		assertFalse(e1.equals(e6));
		assertFalse(e1.equals(e7));
		assertFalse(e1.equals(e8));
		assertFalse(e1.equals(e9));
		assertFalse(e1.equals(e10));
	}
	
	static class TestObjectIdentifierTypeElement extends AbstractObjectIdentifierTypeElement {

		TestObjectIdentifierTypeElement(String identifier, QualifierType qualifier, String description,
				List<String> docReferences) {
			super(identifier, qualifier, description, docReferences);
		}

		@Override
		protected QName getName() {
			return new QName("Test");
		}
		
	}
}
