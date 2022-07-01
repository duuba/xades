package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Element;

class IncludeTest {

	@ParameterizedTest
	@MethodSource("getRefdDataAttrValue")
	void test(Boolean refdData) throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		final String URI = "#this-is-a-local-ref";
		
		assertDoesNotThrow(() -> new Include(URI, refdData).marshal(xwriter, "", ctx));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("Include", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(URI, created.getAttribute("URI"));
		
		if (refdData != null) 
			assertEquals(refdData, Boolean.valueOf(created.getAttribute("referencedData")));
		else
			assertNull(created.getAttributeNode("referencedData"));
	}

	static Stream<Boolean> getRefdDataAttrValue() {
		return Stream.of(Boolean.TRUE, Boolean.FALSE, (Boolean) null);
	}
	
	@Test
	void testEquals() {
		final String uri1 = "#this-is-a-local-ref";
		final String uri2 = "http://this.is.a.remote.ref";
		
		Include inc1 = new Include(uri1, Boolean.TRUE);
		Include inc2 = new Include(uri1, Boolean.TRUE);
		Include inc3 = new Include(uri2, Boolean.TRUE);
		Include inc4 = new Include(uri1, Boolean.FALSE);
		Include inc5 = new Include(uri1, null);
		
		assertTrue(inc1.equals(inc2));
		assertFalse(inc1.equals(inc3));
		assertFalse(inc1.equals(inc4));
		assertFalse(inc1.equals(inc5));
	}
}
