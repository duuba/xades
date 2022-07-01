package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.util.encoders.Base64;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class AbstractDigestAlgAndValueTypeElementTest {

	@Test
	void testContent() throws ParserConfigurationException {
		final byte[] value = "HelloWorldStringToGetSomeBytes".getBytes();
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new TestDigestAlgAndValueTypeElement(DigestMethod.SHA256, value)
																						.marshal(writer, "", context));
		
		final Element created = writer.getCreatedElement();
		
		NodeList dm = created.getElementsByTagNameNS(XMLSignature.XMLNS, "DigestMethod");
		assertEquals(1, dm.getLength());
		assertNotNull(dm.item(0).getAttributes());
		assertNotNull(dm.item(0).getAttributes().getNamedItem("Algorithm"));
		assertEquals(DigestMethod.SHA256, dm.item(0).getAttributes().getNamedItem("Algorithm").getNodeValue());

		NodeList val = created.getElementsByTagNameNS(XMLSignature.XMLNS, "DigestValue");
		assertEquals(1, val.getLength());
		assertArrayEquals(value, Base64.decode(val.item(0).getTextContent()));
	}
	
	@Test
	void testEquals() {
		final byte[] value1 = "HelloWorldStringToGetSomeBytes".getBytes();
		final byte[] value2 = "HelloAgainStringToGetSomeMoreBytes".getBytes();
		
		AbstractDigestAlgAndValueTypeElement e1 = new TestDigestAlgAndValueTypeElement(DigestMethod.SHA256, value1);
		AbstractDigestAlgAndValueTypeElement e2 = new TestDigestAlgAndValueTypeElement(DigestMethod.SHA256, value1);
		AbstractDigestAlgAndValueTypeElement e3 = new TestDigestAlgAndValueTypeElement(DigestMethod.SHA256, value2);
		AbstractDigestAlgAndValueTypeElement e4 = new TestDigestAlgAndValueTypeElement(DigestMethod.SHA512, value1);
		
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));
	}
	
	class TestDigestAlgAndValueTypeElement extends AbstractDigestAlgAndValueTypeElement {

		public TestDigestAlgAndValueTypeElement(String digestAlg, byte[] digestVal) {
			super(digestAlg, digestVal);
		}

		@Override
		protected QName getName() {
			return new QName("Test");
		}
	}
}
