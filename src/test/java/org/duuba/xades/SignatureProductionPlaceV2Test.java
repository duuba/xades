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

class SignatureProductionPlaceV2Test {

	@Test
	void testCompleteAddress() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String street = "Stadhuisplein 1";
		final String state = "ZH";
		final String zip = "2300";
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlaceV2(city, street, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlaceV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(5, children.getLength());
		for(int i = 0; i < children.getLength(); i++)
			assertEquals(Constants.XADES_132_NS_URI, children.item(i).getNamespaceURI());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("StreetAddress", children.item(1).getLocalName());
		assertEquals(street, children.item(1).getTextContent());
		assertEquals("StateOrProvince", children.item(2).getLocalName());
		assertEquals(state, children.item(2).getTextContent());
		assertEquals("PostalCode", children.item(3).getLocalName());
		assertEquals(zip, children.item(3).getTextContent());
		assertEquals("CountryName", children.item(4).getLocalName());
		assertEquals(country, children.item(4).getTextContent());
	}
	
	@Test
	void testNoCity() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = null;
		final String street = "Stadhuisplein 1";
		final String state = "ZH";
		final String zip = "2300";
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlaceV2(city, street, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlaceV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(4, children.getLength());
		
		assertEquals("StreetAddress", children.item(0).getLocalName());
		assertEquals(street, children.item(0).getTextContent());
		assertEquals("StateOrProvince", children.item(1).getLocalName());
		assertEquals(state, children.item(1).getTextContent());
		assertEquals("PostalCode", children.item(2).getLocalName());
		assertEquals(zip, children.item(2).getTextContent());
		assertEquals("CountryName", children.item(3).getLocalName());
		assertEquals(country, children.item(3).getTextContent());
	}
	
	@Test
	void testNoStreet() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String street = null;
		final String state = "ZH";
		final String zip = "2300";
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlaceV2(city, street, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlaceV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(4, children.getLength());
		
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
	void testNoState() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String street = "Stadhuisplein 1";
		final String state = null;
		final String zip = "2300";
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlaceV2(city, street, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlaceV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(4, children.getLength());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("StreetAddress", children.item(1).getLocalName());
		assertEquals(street, children.item(1).getTextContent());
		assertEquals("PostalCode", children.item(2).getLocalName());
		assertEquals(zip, children.item(2).getTextContent());
		assertEquals("CountryName", children.item(3).getLocalName());
		assertEquals(country, children.item(3).getTextContent());
	}
	
	@Test
	void testNoZip() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String street = "Stadhuisplein 1";
		final String state = "ZH";
		final String zip = null;
		final String country = "NL";
		
		assertDoesNotThrow(() -> new SignatureProductionPlaceV2(city, street, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlaceV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(4, children.getLength());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("StreetAddress", children.item(1).getLocalName());
		assertEquals(street, children.item(1).getTextContent());
		assertEquals("StateOrProvince", children.item(2).getLocalName());
		assertEquals(state, children.item(2).getTextContent());
		assertEquals("CountryName", children.item(3).getLocalName());
		assertEquals(country, children.item(3).getTextContent());
	}
	
	@Test
	void testNoCountry() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = "Leiden";
		final String street = "Stadhuisplein 1";
		final String state = "ZH";
		final String zip = "2300";
		final String country = null;
		
		assertDoesNotThrow(() -> new SignatureProductionPlaceV2(city, street, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlaceV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(4, children.getLength());
		
		assertEquals("City", children.item(0).getLocalName());
		assertEquals(city, children.item(0).getTextContent());
		assertEquals("StreetAddress", children.item(1).getLocalName());
		assertEquals(street, children.item(1).getTextContent());
		assertEquals("StateOrProvince", children.item(2).getLocalName());
		assertEquals(state, children.item(2).getTextContent());
		assertEquals("PostalCode", children.item(3).getLocalName());
		assertEquals(zip, children.item(3).getTextContent());
	}	
	
	@Test
	void testNothing() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final String city = null;
		final String street = null;
		final String state = null;
		final String zip = null;
		final String country = null;
		
		assertDoesNotThrow(() -> new SignatureProductionPlaceV2(city, street, state, zip, country).marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignatureProductionPlaceV2", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		NodeList children = created.getChildNodes();
		
		assertEquals(0, children.getLength());
	}
	
	@Test
	void testEquals() {
		SignatureProductionPlaceV2 spp1 = new SignatureProductionPlaceV2("Leiden", "Stadhuisplein 1", "ZH", "2300", "NL");
		SignatureProductionPlaceV2 spp2 = new SignatureProductionPlaceV2("Leiden", "Stadhuisplein 1", "ZH", "2300", "NL");
		SignatureProductionPlaceV2 spp3 = new SignatureProductionPlaceV2("Leiden", null, "ZH", "2300", "NL");
		SignatureProductionPlaceV2 spp4 = new SignatureProductionPlaceV2("Leiden", "Breestraat 1", "ZH", "2300", "NL");
		
		assertTrue(spp1.equals(spp2));
		assertFalse(spp2.equals(spp3));
		assertFalse(spp2.equals(spp4));
	}
}
