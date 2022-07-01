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

class UnsignedSignaturePropertiesTest {

	@Test
	void testSigPolicyStore() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		
		assertDoesNotThrow(() -> new UnsignedSignatureProperties(id, 
											new SignaturePolicyStore(new ObjectIdentifier("oid", null, null, null), 
																	 null, "#specRef", null))
									.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("UnsignedSignatureProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_141_NS_URI, "SignaturePolicyStore").getLength());		
	}

	@Test
	void testNoId() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new UnsignedSignatureProperties(null, 
											new SignaturePolicyStore(new ObjectIdentifier("oid", null, null, null), 
																	 null, "#specRef", null))
									.marshal(xwriter, "", context));
		
		assertNull(xwriter.getCreatedElement().getAttributeNode("Id"));
	}
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		
		final SignaturePolicyStore sps1 = new SignaturePolicyStore(new ObjectIdentifier("oid-1", null, null, null), 
																	null, "#specRef", null);
		final SignaturePolicyStore sps2 = new SignaturePolicyStore(new ObjectIdentifier("oid-2", null, null, null), 
																	null, "#specRef-2", null);
		
		UnsignedSignatureProperties usp1 = new UnsignedSignatureProperties(id1, sps1);
		UnsignedSignatureProperties usp2 = new UnsignedSignatureProperties(id1, sps1);
		UnsignedSignatureProperties usp3 = new UnsignedSignatureProperties(null, sps1);
		UnsignedSignatureProperties usp4 = new UnsignedSignatureProperties(id2, sps1);
		UnsignedSignatureProperties usp5 = new UnsignedSignatureProperties(id1, null);
		UnsignedSignatureProperties usp6 = new UnsignedSignatureProperties(id1, sps2);
		UnsignedSignatureProperties usp7 = new UnsignedSignatureProperties(null, sps2);
		UnsignedSignatureProperties usp8 = new UnsignedSignatureProperties(null, sps2);
		
		assertTrue(usp1.equals(usp2));
		assertTrue(usp7.equals(usp8));
		assertFalse(usp1.equals(usp3));
		assertFalse(usp1.equals(usp4));
		assertFalse(usp1.equals(usp5));
		assertFalse(usp1.equals(usp6));
	}
}
