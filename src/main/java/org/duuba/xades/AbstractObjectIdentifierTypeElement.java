/*******************************************************************************
 * Copyright (C) 2021 The Duuba team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.duuba.xades;

import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;

/**
 * Is a base class for the representation of elements that are of type <code>ObjectIdentifierType</code> as defined in 
 * the <i>ETSI EN 319 132 v1.1.1</i> standard. The type is defined in the XML schema as:
 * <pre><code>
 * &lt;xsd:complexType name="ObjectIdentifierType"&gt;
 *	&lt;xsd:sequence&gt;
 *		&lt;xsd:element name="Identifier" type="IdentifierType"/&gt;
 *		&lt;xsd:element name="Description" type="xsd:string" minOccurs="0"/&gt;
 *		&lt;xsd:element name="DocumentationReferences" type="DocumentationReferencesType" minOccurs="0"/&gt;
 *	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:complexType name="IdentifierType"&gt;
 *	&lt;xsd:simpleContent&gt;
 *		&lt;xsd:extension base="xsd:anyURI"&gt;
 *			&lt;xsd:attribute name="Qualifier" type="QualifierType" use="optional"/&gt;
 *		&lt;/xsd:extension&gt;
 *	&lt;/xsd:simpleContent&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:simpleType name="QualifierType"&gt;
 *	&lt;xsd:restriction base="xsd:string"&gt;
 *		&lt;xsd:enumeration value="OIDAsURI"/&gt;
 *		&lt;xsd:enumeration value="OIDAsURN"/&gt;
 *	&lt;/xsd:restriction&gt;
 * &lt;/xsd:simpleType&gt;
 * &lt;xsd:complexType name="DocumentationReferencesType"&gt;
 *	&lt;xsd:sequence maxOccurs="unbounded"&gt;
 *		&lt;xsd:element name="DocumentationReference" type="xsd:anyURI"/&gt;
 *	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </code></pre>
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public abstract class AbstractObjectIdentifierTypeElement extends XadesElement implements IObjectIdentifier {

	private	String			identifier;
	private QualifierType	qualifier;
	private String			description;
	private List<String>	docReferences;
	
	AbstractObjectIdentifierTypeElement(final String identifier, final QualifierType qualifier, final String description,
								final List<String> docReferences) {
		this.identifier = identifier;
		this.qualifier = qualifier;
		this.description = description;
		this.docReferences = docReferences;
	}
	
	AbstractObjectIdentifierTypeElement(final IObjectIdentifier source) {
		this.identifier = source.getIdentifier();
		this.qualifier = source.getQualifier();
		this.description = source.getDescription();
		this.docReferences = source.getDocumentationReferences();		
	}
	
	/**
	 * @return the identifier value
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * @return the identifier's qualifier 
	 */
	public QualifierType getQualifier() {
		return qualifier;
	}
	
	/**
	 * @return the identifier's description 
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the documentation references 
	 */
	public List<String> getDocumentationReferences() {
		return docReferences;
	}
		
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
	
        // create and append Identifier element
		xwriter.writeStartElement(nsPrefix, "Identifier", Constants.XADES_132_NS_URI);        
        if (qualifier != null)
        	xwriter.writeAttribute("", Constants.XADES_132_NS_URI, "Qualifier", qualifier.toString());
        
		// create and append Description element
        if (description != null && !description.isEmpty())
        	xwriter.writeTextElement(nsPrefix, "Description", Constants.XADES_132_NS_URI, description);	
		
        // create and append DocumentReferences and child elements
        if (docReferences != null && !docReferences.isEmpty()) {
        	xwriter.writeStartElement(nsPrefix, "DocumentationReferences", Constants.XADES_132_NS_URI);
        	for (String dr : docReferences)
        		xwriter.writeTextElement(nsPrefix, "DocumentationReference", Constants.XADES_132_NS_URI, dr);
        	xwriter.writeEndElement(); 
        }        
	}

}
