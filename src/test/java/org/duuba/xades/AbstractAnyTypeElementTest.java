package org.duuba.xades;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.duuba.xades.AbstractAnyTypeElement;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AnyTypeElementTest {

	private static DocumentBuilder builder;
	
	@BeforeAll
	public static void setupBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builder = builderFactory.newDocumentBuilder();		
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "element_no_attr.xml", "element_with_attr.xml" , "element_tree.xml" , 
							 "mixed_content.xml" })
	public void testXMLFragments(String xmlFragment) throws Throwable {
		String srcFile = this.getClass().getClassLoader().getResource("fragments/" + xmlFragment).getPath();
		final Element source = builder.parse(new FileInputStream(srcFile)).getDocumentElement();

		NodeList childNodes = source.getChildNodes();
		List<Node> nodeList = new ArrayList<>(childNodes.getLength());
		for(int i = 0; i < childNodes.getLength(); i++)
			nodeList.add(childNodes.item(i));
		
		AbstractAnyTypeElement anyTypeElement = new AbstractAnyTypeElement(nodeList) {		
			@Override
			protected QName getName() {				
				return new QName(source.getNamespaceURI(), source.getLocalName(), source.getPrefix() != null ?
																						source.getPrefix() : "");
			}
		};
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		try {
			anyTypeElement.marshal(writer, "", context);
		} catch (Throwable t) {
			fail(t);
		}
		
		Element created = writer.getCreatedElement();
		
		assertNotNull(created);
		assertEqualNodes(source, created);		
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
}
