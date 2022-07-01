package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.CommitmentTypeIndication.CommitmentTypeQualifier;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class CommitmentTypeIndicationTest {

	@Test
	void testAllObjectsNoQualifier() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();

		final ObjectIdentifier cid = new ObjectIdentifier("cmmnt-id-1", QualifierType.OIDAsURN, null, null);
		
		assertDoesNotThrow(() -> new CommitmentTypeIndication(cid, null, null).marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("CommitmentTypeIndication", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CommitmentTypeId").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ObjectReference").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "AllSignedDataObjects").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CommitmentTypeQualifiers").getLength());
	}
	
	@Test
	void testReferencesWithQualifier() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		final ObjectIdentifier cid = new ObjectIdentifier("cmmnt-id-1", QualifierType.OIDAsURN, null, null);
		final List<String> refs = new ArrayList<>();
		refs.add(UUID.randomUUID().toString());
		refs.add(UUID.randomUUID().toString());
		
		final List<CommitmentTypeQualifier> qualifiers = new ArrayList<>();
		qualifiers.add(new CommitmentTypeIndication.CommitmentTypeQualifier(null));
		qualifiers.add(new CommitmentTypeIndication.CommitmentTypeQualifier(null));
		qualifiers.add(new CommitmentTypeIndication.CommitmentTypeQualifier(null));
		
		assertDoesNotThrow(() -> new CommitmentTypeIndication(cid, refs, qualifiers).marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("CommitmentTypeIndication", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CommitmentTypeId").getLength());
		NodeList objRefs = created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ObjectReference");
		assertEquals(2, objRefs.getLength());
		assertEquals(refs.get(0), objRefs.item(0).getTextContent());
		assertEquals(refs.get(1), objRefs.item(1).getTextContent());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "AllSignedDataObjects").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CommitmentTypeQualifiers").getLength());
		assertEquals(3, ((Element) created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CommitmentTypeQualifiers").item(0))
						.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "CommitmentTypeQualifier").getLength());
	}

	@Test
	void testEquals() {
		final ObjectIdentifier cid1 = new ObjectIdentifier("cmmnt-id-1", QualifierType.OIDAsURN, null, null);
		final ObjectIdentifier cid2 = new ObjectIdentifier("cmmnt-id-2", QualifierType.OIDAsURN, null, null);
		
		final List<String> refs1 = new ArrayList<>();
		final List<String> refs2 = new ArrayList<>();
		String r = UUID.randomUUID().toString();
		refs1.add(r);
		refs2.add(r);
		refs2.add(UUID.randomUUID().toString());
		
		final List<CommitmentTypeQualifier> qualifiers1 = new ArrayList<>();
		final List<CommitmentTypeQualifier> qualifiers2 = new ArrayList<>();
		CommitmentTypeQualifier cq = new CommitmentTypeIndication.CommitmentTypeQualifier(null);
		qualifiers1.add(cq);
		qualifiers2.add(cq);
		qualifiers1.add(new CommitmentTypeIndication.CommitmentTypeQualifier(null));

		CommitmentTypeIndication ci1 = new CommitmentTypeIndication(cid1, refs1, qualifiers1);
		CommitmentTypeIndication ci2 = new CommitmentTypeIndication(cid1, refs1, qualifiers1);
		CommitmentTypeIndication ci3 = new CommitmentTypeIndication(cid2, refs1, qualifiers1);
		CommitmentTypeIndication ci4 = new CommitmentTypeIndication(cid1, null, qualifiers1);
		CommitmentTypeIndication ci5 = new CommitmentTypeIndication(cid1, refs2, qualifiers1);
		CommitmentTypeIndication ci6 = new CommitmentTypeIndication(cid1, refs1, null);
		CommitmentTypeIndication ci7 = new CommitmentTypeIndication(cid1, refs1, qualifiers2);
		
		assertTrue(ci1.equals(ci2));
		assertFalse(ci1.equals(ci3));
		assertFalse(ci1.equals(ci4));
		assertFalse(ci1.equals(ci5));
		assertFalse(ci1.equals(ci6));
		assertFalse(ci1.equals(ci7));
	}
}
