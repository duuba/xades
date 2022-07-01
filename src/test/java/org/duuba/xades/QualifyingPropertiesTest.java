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

class QualifyingPropertiesTest {

	private static final SignedProperties T_SIG_PROPS = new SignedProperties(UUID.randomUUID().toString(),
			new SignedSignatureProperties("sig-props", ZonedDateTime.now(), null, null, null, null, null),
			new SignedDataObjectProperties("data-props", null, null));
	
	private static final UnsignedProperties T_USIG_PROPS = new UnsignedProperties(UUID.randomUUID().toString(),
			new UnsignedSignatureProperties(null, new SignaturePolicyStore(new ObjectIdentifier("oid", null, null, null), 
											null, "#specRef", null)),
			new UnsignedDataObjectProperties(null, null));
				
	@Test
	void testAll() throws ParserConfigurationException {		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final String target = "#example-target-uri";
		
		assertDoesNotThrow(() -> new QualifyingProperties(id, target, T_SIG_PROPS, T_USIG_PROPS)
										.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();		
		assertEquals("QualifyingProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(2, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("SignedProperties", children.item(0).getLocalName());
		assertEquals("UnsignedProperties", children.item(1).getLocalName());
	}

	@Test
	void testUnSignedOnly() throws ParserConfigurationException {		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String target = "#example-target-uri";
		
		assertDoesNotThrow(() -> new QualifyingProperties(null, target, null, T_USIG_PROPS).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();		
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());
		assertEquals("UnsignedProperties", children.item(0).getLocalName());
	}
	
	@Test
	void testSignedOnly() throws ParserConfigurationException {		
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		final String target = "#example-target-uri";
		
		assertDoesNotThrow(() -> new QualifyingProperties(id, target, T_SIG_PROPS, null).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();		
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());		
		assertEquals("SignedProperties", children.item(0).getLocalName());
	}
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		final String target1 = "#example-target-uri-1";
		final String target2 = "#example-target-uri-2";
		
		final SignedProperties sigProps = new SignedProperties(UUID.randomUUID().toString(),
				new SignedSignatureProperties("sig-props", ZonedDateTime.now(), null, null, null, null, null),
				new SignedDataObjectProperties("data-props-2", null, null));
		
		final UnsignedProperties usigProps = new UnsignedProperties(UUID.randomUUID().toString(),
				new UnsignedSignatureProperties(null, new SignaturePolicyStore(new ObjectIdentifier("oid1", null, null, null), 
												null, "#specRef", null)),
				new UnsignedDataObjectProperties(null, null));
			
		QualifyingProperties qp1 = new QualifyingProperties(id1, target1, T_SIG_PROPS, T_USIG_PROPS);
		QualifyingProperties qp2 = new QualifyingProperties(id1, target1, T_SIG_PROPS, T_USIG_PROPS);
		QualifyingProperties qp3 = new QualifyingProperties(null, target1, T_SIG_PROPS, T_USIG_PROPS);
		QualifyingProperties qp4 = new QualifyingProperties(id2, target1, T_SIG_PROPS, T_USIG_PROPS);
		QualifyingProperties qp5 = new QualifyingProperties(id1, null, T_SIG_PROPS, T_USIG_PROPS);
		QualifyingProperties qp6 = new QualifyingProperties(id1, target2, T_SIG_PROPS, T_USIG_PROPS);
		QualifyingProperties qp7 = new QualifyingProperties(id1, target1, null, T_USIG_PROPS);
		QualifyingProperties qp8 = new QualifyingProperties(id1, target1, sigProps, T_USIG_PROPS);
		QualifyingProperties qp9 = new QualifyingProperties(id1, target1, T_SIG_PROPS, null);
		QualifyingProperties qp10 = new QualifyingProperties(id1, target1, T_SIG_PROPS, usigProps);
		
		assertTrue(qp1.equals(qp2));
		assertFalse(qp1.equals(qp3));
		assertFalse(qp1.equals(qp4));
		assertFalse(qp1.equals(qp5));
		assertFalse(qp1.equals(qp6));
		assertFalse(qp1.equals(qp7));
		assertFalse(qp1.equals(qp8));
		assertFalse(qp1.equals(qp9));
		assertFalse(qp1.equals(qp10));
	}
}
