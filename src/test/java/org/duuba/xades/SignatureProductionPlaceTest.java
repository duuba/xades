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

class SignatureProductionPlaceTest {

	@Test
	void testCompleteAddress() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String state = "ZH";
		final String zip = "2300";
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlace(city, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlace", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(4, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("StateOrProvince", children.item(1).getLocalName());
		assertEquals(state, children.item(1).getTextContent());
		assertEquals("PostalCode", children.item(2).getLocalName());
		assertEquals(zip, children.item(2).getTextContent());
		assertEquals("CountryName", children.item(3).getLocalName());
		assertEquals(country, children.item(3).getTextContent());
	}
	
	@Test
	void testNoCity() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = null;
		final String state = "ZH";
		final String zip = "2300";
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlace(city, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlace", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(3, children.getLength());
		
		assertEquals("StateOrProvince", children.item(0).getLocalName());
		assertEquals(state, children.item(0).getTextContent());
		assertEquals("PostalCode", children.item(1).getLocalName());
		assertEquals(zip, children.item(1).getTextContent());
		assertEquals("CountryName", children.item(2).getLocalName());
		assertEquals(country, children.item(2).getTextContent());
	}
	
	@Test
	void testNoState() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String state = null;
		final String zip = "2300";
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlace(city, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlace", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(3, children.getLength());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("PostalCode", children.item(1).getLocalName());
		assertEquals(zip, children.item(1).getTextContent());
		assertEquals("CountryName", children.item(2).getLocalName());
		assertEquals(country, children.item(2).getTextContent());
	}
	
	@Test
	void testNoZip() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String state = "ZH";
		final String zip = null;
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlace(city, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlace", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(3, children.getLength());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("StateOrProvince", children.item(1).getLocalName());
		assertEquals(state, children.item(1).getTextContent());
		assertEquals("CountryName", children.item(2).getLocalName());
		assertEquals(country, children.item(2).getTextContent());
	}
	
	@Test
	void testNoCountry() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String state = "ZH";
		final String zip = "2300";
		final String country = null;
		
		assertDoesNotThrow(() -> new SignatureProductionPlace(city, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlace", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(3, children.getLength());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("StateOrProvince", children.item(1).getLocalName());
		assertEquals(state, children.item(1).getTextContent());
		assertEquals("PostalCode", children.item(2).getLocalName());
		assertEquals(zip, children.item(2).getTextContent());
	}
	
	@Test
	void testNothing() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = null;
		final String state = null;
		final String zip = null;
		final String country = null;
		
		assertDoesNotThrow(() -> new SignatureProductionPlace(city, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlace", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(0, children.getLength());
	}
	
	@Test
	void testEquals() {
		SignatureProductionPlace spp1 = new SignatureProductionPlace("Leiden", "ZH", "2300", "NL");
		SignatureProductionPlace spp2 = new SignatureProductionPlace("Leiden", "ZH", "2300", "NL");
		
		assertTrue(spp1.equals(spp2));
		assertFalse(spp1.equals(new SignatureProductionPlace(null, "ZH", "2300", "NL")));
		assertFalse(spp1.equals(new SignatureProductionPlace("Rotterdam", "ZH", "2300", "NL")));
		assertFalse(spp1.equals(new SignatureProductionPlace("Leiden", null, "2300", "NL")));
		assertFalse(spp1.equals(new SignatureProductionPlace("Leiden", "Zuid-Holland", "2300", "NL")));
		assertFalse(spp1.equals(new SignatureProductionPlace("Leiden", "ZH", null, "NL")));
		assertFalse(spp1.equals(new SignatureProductionPlace("Leiden", "ZH", "2311TX", "NL")));
		assertFalse(spp1.equals(new SignatureProductionPlace("Leiden", "ZH", "2300", null)));
		assertFalse(spp1.equals(new SignatureProductionPlace("Leiden", "ZH", "2300", "The Netherlands")));
	}
}
