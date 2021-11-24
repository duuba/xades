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

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;

/**
 * A representation of the <code>QualifyingProperties</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1</i>
 * standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="QualifyingProperties" type="QualifyingPropertiesType"/&gt;
 * &lt;xsd:complexType name="QualifyingPropertiesType"&gt; 
 *	&lt;xsd:sequence&gt;
 *		&lt;xsd:element ref="SignedProperties" minOccurs="0"/&gt;
 *		&lt;xsd:element ref="UnsignedProperties" minOccurs="0"/&gt; 
 *	&lt;/xsd:sequence&gt;
 *	&lt;xsd:attribute name="Target" type="xsd:anyURI" use="required"/&gt;
 *	&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt; 
 * &lt;/xsd:complexType&gt;
 * </pre></code>
 *
 * <p>A <code>QualifyingProperties</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newQualifyingProperties} methods.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class QualifyingProperties extends XadesElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "QualifyingProperties", 
																	Constants.XADES_132_NS_PREFIX);
	
	private String				id;
	private String				target;
	private SignedProperties 	signedProps;
	private UnsignedProperties 	unsignedProps;
	
	public QualifyingProperties(final String id, final String target, 
								final SignedProperties signedProps, final UnsignedProperties unsignedProps) {
		this.id = id;
		this.target = target;
		this.signedProps = signedProps;
		this.unsignedProps = unsignedProps;
	}
	
    /**
     * Returns the optional <code>Id</code> attribute of this <code>QualifyingProperties</code>.
     *
     * @return the <code>Id</code> attribute (may be <code>null</code> if not specified)
     */
    public String getId() {
    	return id;
    }
    
    /**
     * Returns the URI pointing to the XML signature to which this <code>QualifyingProperties</code> applies.
     *
     * @return the <code>Target</code> attribute 
     */
    public String getTarget() {
    	return target;
    }
        
	/**
	 * @return the signed properties
	 */
	public SignedProperties getSignedProperties() {
		return signedProps;
	}
	
	/**
	 * @return the properties which are not signed
	 */
	public UnsignedProperties getUnsignedProperties() {
		return unsignedProps;
	}

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {		

		// Write attributes
		if (id != null && !id.isEmpty())
			xwriter.writeIdAttribute("", "", "Id", id);         
		xwriter.writeAttribute("", null, "Target", target);
        
		// Write child elements
		signedProps.marshal(xwriter, dsPrefix, context);
		if (unsignedProps != null)
			unsignedProps.marshal(xwriter, dsPrefix, context);
							
	}
}
