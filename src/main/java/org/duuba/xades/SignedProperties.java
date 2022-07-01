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
import org.holodeckb2b.commons.util.Utils;

/**
 * A representation of the <code>SignedProperties</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1</i>
 * standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SignedProperties" type="SignedPropertiesType"/&gt;
 * &lt;xsd:complexType name="SignedPropertiesType"&gt; 
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element ref="SignedSignatureProperties" minOccurs="0"/&gt;
 *		&lt;xsd:element ref="SignedDataObjectProperties" minOccurs="0"/&gt; 
 *	&lt;/xsd:sequence&gt;
 * 	&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt; 
 * &lt;/xsd:complexType&gt;
 * </pre></code>
 *
 * <p>A <code>SignedProperties</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newSignedProperties} methods.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class SignedProperties extends XadesElement {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignedProperties", 
																Constants.XADES_132_NS_PREFIX);
	
	private String						id;
	private SignedSignatureProperties	signatureProps;
	private SignedDataObjectProperties	dataObjectProps;

	SignedProperties(final String id, final SignedSignatureProperties signatureProperties, 
					 final SignedDataObjectProperties dataObjectProperties) {
		this.id = id;
		this.signatureProps = signatureProperties;
		this.dataObjectProps = dataObjectProperties;
	}
	
    /**
     * Returns the optional <code>Id</code> attribute of this <code>SignedProperties</code>.
     *
     * @return the <code>Id</code> attribute (may be <code>null</code> if not specified)
     */
    public String getId() {
    	return id;
    }
    
    /**
     * @return the signed properties that apply to the signature as a whole. 
     */
    public SignedSignatureProperties getSignatureProperties() {
    	return signatureProps;
    }

    /**
     * @return the signed properties that apply to specific data objects part of the signature. 
     */
    public SignedDataObjectProperties getDataObjectProperties() {
    	return dataObjectProps;
    }

	/**
	 * Determines whether the other object is an instance of the same class and represents the same element, i.e. has
	 * the same content.
	 * 
	 * @param o 	the other object
	 * @return 		<code>true</code> iff <code>o</code> represents the same element, i.e. has the same qualified name
	 * 				and list of child elements.
	 */	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		
		SignedProperties other = (SignedProperties) o;
		
		return Utils.nullSafeEqual(this.id, other.id)
			&& Utils.nullSafeEqual(this.signatureProps, other.signatureProps)
			&& Utils.nullSafeEqual(this.dataObjectProps, other.dataObjectProps);
	}

    
    @Override
    protected QName getName() {
    	return ELEMENT_NAME;
    }
    
    @Override
    protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
    										throws MarshalException {

    	// Write Id attribute
		if (id != null && !id.isEmpty())
			xwriter.writeIdAttribute("", null, "Id", id);         
		
		// Write child elements
		if (signatureProps != null)
			signatureProps.marshal(xwriter, dsPrefix, context);
		if (dataObjectProps != null)
			dataObjectProps.marshal(xwriter, dsPrefix, context);
	} 
}
