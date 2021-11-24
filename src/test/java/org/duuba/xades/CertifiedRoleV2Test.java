package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.CertifiedRoleV2;
import org.duuba.xades.Constants;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class CertifiedRoleV2Test {

	@Test
	void testAttrCert() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		try {
			new CertifiedRoleV2(null, ctx.getCertificate()).marshal(xwriter, null, ctx);
		} catch (Throwable t) {
			fail(t);
		}
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("CertifiedRole", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "X509AttributeCertificate").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "OtherAttributeCertificate").getLength());		
	}
	
	@Test
	void testOtherCert() throws ParserConfigurationException, SAXException, IOException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext ctx = new TestCryptoContext();
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();	
		String srcFile = this.getClass().getClassLoader().getResource("fragments/element_tree.xml").getPath();
		final Element source = builder.parse(new FileInputStream(srcFile)).getDocumentElement();

		NodeList childNodes = source.getChildNodes();
		List<Node> nodeList = new ArrayList<>(childNodes.getLength());
		for(int i = 0; i < childNodes.getLength(); i++)
			nodeList.add(childNodes.item(i));
		
		try {
			new CertifiedRoleV2(nodeList).marshal(xwriter, null, ctx);
		} catch (Throwable t) {
			fail(t);
		}
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("CertifiedRole", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "X509AttributeCertificate").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "OtherAttributeCertificate").getLength());		
	}

}
