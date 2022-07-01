package org.duuba.xades;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AbstractAnyTypeElementTest {

	private static DocumentBuilder builder;
	
	@BeforeAll
	static void setupBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builder = builderFactory.newDocumentBuilder();		
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "element_no_attr.xml", "element_with_attr.xml" , "element_tree.xml" , 
							 "mixed_content.xml" })
	void testXMLFragments(String xmlFragment) throws Throwable {
		String srcFile = this.getClass().getClassLoader().getResource("fragments/" + xmlFragment).getPath();
		final Element source = builder.parse(new FileInputStream(srcFile)).getDocumentElement();

		NodeList childNodes = source.getChildNodes();
		List<Node> nodeList = new ArrayList<>(childNodes.getLength());
		for(int i = 0; i < childNodes.getLength(); i++)
			nodeList.add(childNodes.item(i));
		
		AbstractAnyTypeElement anyTypeElement = new TestAnyTypeElement(source, nodeList);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> anyTypeElement.marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertNotNull(created);
		assertEqualNodes(source, created);		
	}	
	
	@Test
	void testEquals() throws Throwable {
		String srcFile = this.getClass().getClassLoader().getResource("fragments/mixed_content.xml").getPath();
		final Element source = builder.parse(new FileInputStream(srcFile)).getDocumentElement();

		NodeList childNodes = source.getChildNodes();
		List<Node> nodeList = new ArrayList<>(childNodes.getLength());
		for(int i = 0; i < childNodes.getLength(); i++)
			nodeList.add(childNodes.item(i));
		
		AbstractAnyTypeElement e1 = new TestAnyTypeElement(source, nodeList);
		AbstractAnyTypeElement e2 = new TestAnyTypeElement(source, nodeList);
		
		assertTrue(e1.equals(e2));
	}
	
	@Test
	void testDifferentContent() throws Throwable {
		String srcFile = this.getClass().getClassLoader().getResource("fragments/mixed_content.xml").getPath();
		final Element source = builder.parse(new FileInputStream(srcFile)).getDocumentElement();
		final NodeList childNodes = source.getChildNodes();
		
		final int nChilds = childNodes.getLength();
		List<Node> nodeList1 = new ArrayList<>(nChilds);
		List<Node> nodeList2 = new ArrayList<>(nChilds);
		for(int i = 0; i < nChilds; i++) {
			nodeList1.add(childNodes.item(i));
			nodeList2.add(0, childNodes.item(i));			
		}
		
		AbstractAnyTypeElement e1 = new TestAnyTypeElement(source, nodeList1);
		AbstractAnyTypeElement e2 = new TestAnyTypeElement(source, nodeList2);
		AbstractAnyTypeElement e3 = new TestAnyTypeElement(source, null);
		
		assertFalse(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e3.equals(e2));
	}
	
	private void assertEqualNodes(Node exp, Node act) {
		if (exp == act)
			return;
		
		assertEquals(exp.getNodeType(), act.getNodeType());		
		assertEquals(exp.getNodeName(), act.getNodeName());
		
		String expNodeVal = null, actNodeValue = null;
		try {
			expNodeVal = exp.getNodeValue();
			actNodeValue = act.getNodeValue();
		} catch (DOMException e) {}		
		assertEquals(expNodeVal, actNodeValue);
		
		NodeList expChildren = exp.getChildNodes();
		NodeList actChildren = act.getChildNodes();
		
		assertEquals(expChildren.getLength(), actChildren.getLength());
		
		for (int i = 0; i < expChildren.getLength(); i++)
			assertEqualNodes(expChildren.item(i), actChildren.item(i));
	}
	
	class TestAnyTypeElement extends AbstractAnyTypeElement {

		private QName name;
		
		TestAnyTypeElement(Element source, List<Node> content) {
			super(content);
			name = new QName(source.getNamespaceURI(), source.getLocalName());
		}

		@Override
		protected QName getName() {
			return name;
		}		
	}
}
