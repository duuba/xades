package org.duuba.xades.test;

import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Is an implementation of {@link XmlWriter} which builds a DOM object model which can be evaluated later by tests.
 *  
 * @author Sander Fieten (sander at chasquis-messaging.com)
 *
 */
public class DOMXMLWriter implements XmlWriter {

	/**
	 * The Document acting as factory for nodes
	 */
	private Document	factory;
	/**
	 * The root (=first) element that is created
	 */
	private Element		root;
	/**
	 * The current element in the tree being created
	 */
	private Element		current;
	
	/**
	 * Creates a new writer instance
	 * 
	 * @throws ParserConfigurationException when no XML library is available for creating DOM nodes
	 */
	public DOMXMLWriter() throws ParserConfigurationException {
		factory = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}
	
	/**
	 * Gets the root element of the created tree
	 * 
	 * @return	the root element
	 */
	public Element getCreatedElement() {
		return root;
	}
	
	/**
	 * Resets the writer so it can be reused for new test
	 */
	public void reset() {
		root = null;
		current = null;			
	}
	
	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) {
		Element newElement = factory.createElementNS(("".equals(namespaceURI) ? null : namespaceURI),
													 DOMUtils.getQNameString(prefix, localName));
		if (current != null) {
			current.appendChild(newElement);
			current = newElement;
		} else if (root == null)
			root = current = newElement;
		else
			throw new IllegalStateException("Adding element outside of tree");					
	}

	@Override
	public void writeEndElement() {
		if (current != root) 
			current = (Element) current.getParentNode();
		else 
			current = null;
	}

	@Override
	public void writeTextElement(String prefix, String localName, String namespaceURI, String value) {
		writeStartElement(prefix, localName, namespaceURI);
		current.setTextContent(value);
		writeEndElement();
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) {
        if ("".equals(prefix) || prefix == null) {
            writeAttribute(null, XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", namespaceURI);
        }
        else {
            writeAttribute("xmlns", XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix, namespaceURI);
        }
	}

	@Override
	public void writeCharacters(String text) {
		current.appendChild(factory.createTextNode(text));
	}

	@Override
	public void writeComment(String text) {
		current.appendChild(factory.createComment(text));
	}

	@Override
	public Attr writeAttribute(String prefix, String namespaceURI, String localName, String value) {
		if (value == null)
			return null;
		
		Attr attr = factory.createAttributeNS("".equals(namespaceURI) ? null : namespaceURI, 
												DOMUtils.getQNameString(prefix, localName));
        attr.setTextContent(value);
        current.setAttributeNodeNS(attr);
        
        return attr;		
	}

	@Override
	public void writeIdAttribute(String prefix, String namespaceURI, String localName, String value) {
		Attr idAttr = writeAttribute(prefix, namespaceURI, localName, value);
		if (idAttr != null)
			current.setIdAttributeNode(idAttr, true);
	}

	@Override
	public String getCurrentLocalName() {
		return current.getLocalName();
	}

	@Override
	public XMLStructure getCurrentNodeAsStructure() {
		return new DOMStructure(current);
	}

	@Override
	public void marshalStructure(XMLStructure toMarshal, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		if (toMarshal instanceof DOMStructure)
			marshalGenericNode(((DOMStructure) toMarshal).getNode());
		else
			throw new UnsupportedOperationException();
	}
	
	private void marshalGenericNode(Node node) {

        short nodeType = node.getNodeType();
        if (DOMUtils.isNamespace(node)) {
            writeNamespace(node.getLocalName(), node.getTextContent());
        }
        else if (nodeType == Node.ATTRIBUTE_NODE) {
            // if it is an attribute, make a copy.
        	writeAttribute((Attr) node);
        }
        else {
            switch (nodeType) {
            case Node.ELEMENT_NODE:
                writeStartElement(node.getPrefix(), node.getLocalName(), node.getNamespaceURI());

                // emit all the namespaces and attributes.
                NamedNodeMap nnm = node.getAttributes();
                for (int idx = 0 ; idx < nnm.getLength() ; idx++) {
                    Attr attr = (Attr) nnm.item(idx);
                    // is this a namespace node?
                    if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI())) {
                        writeNamespace(attr.getLocalName(), attr.getValue());
                    }
                    else {
                        // nope - standard attribute.
                        writeAttribute(attr);
                    }
                }
                // now loop through all the children.
                for (Node child = node.getFirstChild() ; child != null ; child = child.getNextSibling()) {
                    marshalGenericNode(child);
                }
                writeEndElement();
                break;
            case Node.COMMENT_NODE:
                writeComment(node.getTextContent());
                break;
            case Node.TEXT_NODE:
                writeCharacters(node.getTextContent());
                break;
            default:
                // unhandled - don't care to deal with processing instructions.
                break;
            }
        }
    }

    private void writeAttribute(Attr attr) {
        if (attr.isId())
            writeIdAttribute(attr.getPrefix(), attr.getNamespaceURI(), attr.getLocalName(), attr.getTextContent());
        else {
            if (attr.getNamespaceURI() == null && attr.getLocalName() == null) {
                // Level 1 DOM attribute
                writeAttribute(null, null, attr.getName(), attr.getTextContent());
            } else {
                writeAttribute(attr.getPrefix(), attr.getNamespaceURI(), attr.getLocalName(),
                                       attr.getTextContent());
            }
        }
    }	
}
