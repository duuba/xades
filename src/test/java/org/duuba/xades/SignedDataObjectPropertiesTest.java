package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class SignedDataObjectPropertiesTest {

	@Test
	void testAll() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String id = UUID.randomUUID().toString();
		
		final List<DataObjectFormat> objFormats = new ArrayList<>();
		objFormats.add(new DataObjectFormat("#obj-1", null, null, null, null));
		objFormats.add(new DataObjectFormat("#obj-2", null, null, null, null));
				
		final List<CommitmentTypeIndication> commInd = new ArrayList<>();
		commInd.add(new CommitmentTypeIndication(new ObjectIdentifier("obj-1", null, null, null), null, null));
		
		// Currently there is no support for time stamp and custom elements, so this is all to test
		
		assertDoesNotThrow(() -> new SignedDataObjectProperties(id, objFormats, commInd).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedDataObjectProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(id, created.getAttribute("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(3, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());

		assertEquals("DataObjectFormat", children.item(0).getLocalName());
		assertEquals("DataObjectFormat", children.item(1).getLocalName());
		assertEquals("CommitmentTypeIndication", children.item(2).getLocalName());
	}
	
	@Test
	void testFormatOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		
		final List<DataObjectFormat> objFormats = new ArrayList<>();
		objFormats.add(new DataObjectFormat("#obj-1", null, null, null, null));
				
		assertDoesNotThrow(() -> new SignedDataObjectProperties(null, objFormats, null).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedDataObjectProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(1, children.getLength());
		assertEquals("DataObjectFormat", children.item(0).getLocalName());
	}
		
	@Test
	void testCommitmentOnly() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final List<CommitmentTypeIndication> commInd = new ArrayList<>();
		commInd.add(new CommitmentTypeIndication(new ObjectIdentifier("obj-1", null, null, null), null, null));
		commInd.add(new CommitmentTypeIndication(new ObjectIdentifier("obj-2", null, null, null), null, null));
		
		// Currently there is no support for time stamp and custom elements, so this is all to test
		
		assertDoesNotThrow(() -> new SignedDataObjectProperties(null, null, commInd).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignedDataObjectProperties", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertNull(created.getAttributeNode("Id"));
		
		NodeList children = created.getChildNodes();		
		assertEquals(2, children.getLength());
		for(int i = 0; i < children.getLength(); i++) {
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
			assertEquals("CommitmentTypeIndication", children.item(i).getLocalName());
		}		
	}
	
	@Test
	void testEquals() {
		final String id1 = UUID.randomUUID().toString();
		final String id2 = UUID.randomUUID().toString();
		
		final List<DataObjectFormat> objFormats1 = new ArrayList<>();
		final List<DataObjectFormat> objFormats2 = new ArrayList<>();
		objFormats1.add(new DataObjectFormat("#obj-1", null, null, null, null));
		objFormats2.add(new DataObjectFormat("#obj-1", null, null, null, null));
		objFormats1.add(new DataObjectFormat("#obj-2", null, null, null, null));
	
		final List<CommitmentTypeIndication> commInd1 = new ArrayList<>();
		final List<CommitmentTypeIndication> commInd2 = new ArrayList<>();
		commInd1.add(new CommitmentTypeIndication(new ObjectIdentifier("obj-1", null, null, null), null, null));
		commInd2.add(new CommitmentTypeIndication(new ObjectIdentifier("obj-1", null, null, null), null, null));
		commInd2.add(new CommitmentTypeIndication(new ObjectIdentifier("obj-2", null, null, null), null, null));
		
		SignedDataObjectProperties sdp1 = new SignedDataObjectProperties(id1, objFormats1, commInd1);
		SignedDataObjectProperties sdp2 = new SignedDataObjectProperties(id1, objFormats1, commInd1);
		SignedDataObjectProperties sdp3 = new SignedDataObjectProperties(null, objFormats1, commInd1);
		SignedDataObjectProperties sdp4 = new SignedDataObjectProperties(id2, objFormats1, commInd1);
		SignedDataObjectProperties sdp5 = new SignedDataObjectProperties(id1, null, commInd1);
		SignedDataObjectProperties sdp6 = new SignedDataObjectProperties(id1, objFormats2, commInd1);
		SignedDataObjectProperties sdp7 = new SignedDataObjectProperties(id1, objFormats1, null);
		SignedDataObjectProperties sdp8 = new SignedDataObjectProperties(id1, objFormats1, commInd2);
		
		assertTrue(sdp1.equals(sdp2));
		assertFalse(sdp1.equals(sdp3));
		assertFalse(sdp1.equals(sdp4));
		assertFalse(sdp1.equals(sdp5));
		assertFalse(sdp1.equals(sdp6));
		assertFalse(sdp1.equals(sdp7));
		assertFalse(sdp1.equals(sdp8));
	}
}
