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
import org.apache.xml.security.utils.XMLUtils;
import org.bouncycastle.util.Arrays;
import org.holodeckb2b.commons.util.Utils;

/**
 * A representation of the <code>SignaturePolicyStore</code> element as defined in the <i>ETSI EN 319 132 v1.1.1</i> 
 * standard. The XML schema is defined as (note this element is in the "new" 1.4.1 namespace):
 * <code><pre>
 * &lt;xsd:element name="SignaturePolicyStore" type="SignaturePolicyStoreType"/&gt;
 * &lt;xsd:complexType name="SignaturePolicyStoreType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element ref="SPDocSpecification"/&gt;
 * 		&lt;xsd:choice&gt;
 * 			&lt;xsd:element name="SignaturePolicyDocument" type="xsd:base64Binary"/&gt;
 * 			&lt;xsd:element name="SigPolDocLocalURI" type="xsd:anyURI"/&gt;
 * 		&lt;/xsd:choice&gt;
 * 	&lt;/xsd:sequence&gt;
 * 	&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:element name="SPDocSpecification" type="xades:ObjectIdentifierType"/&gt;
 * </pre></code> 
 * 
 * <p>A <code>SignaturePolicyStore</code> instance may be created by invoking one of the {@link 
 * XadesSignatureFactory#newSignaturePolicyStore} methods on a factory instance configured for Xades version 
 * {@link XadesVersion#EN_319_132_V111}.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class SignaturePolicyStore extends XadesElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_141_NS_URI, "SignaturePolicyStore", 
															Constants.XADES_141_NS_PREFIX);
	
	private static final QName SPDOC_ELEMENT_NAME = new QName(Constants.XADES_141_NS_URI, "SPDocSpecification",
															Constants.XADES_141_NS_PREFIX);

	
	private SPDocSpecification	spDocId;
	private byte[]				spDocContent;
	private String				spDocReference;
	private String				id;
	
	SignaturePolicyStore(IObjectIdentifier spDocId, byte[] spDocContent, String spDocReference, String id) {
		this.spDocId = new SPDocSpecification(spDocId);
		this.spDocContent = spDocContent;
		this.spDocReference = spDocReference;
		this.id = id;
	}
	
    /**
     * @return the <code>Id</code> attribute (may be <code>null</code> if not specified)
     */
	public String getId() {
		return id;
	}

	/**
	 * @return	the object identifier of the technical specification that defines the syntax used for producing the 
	 * 			signature policy document.
	 */
	public SPDocSpecification getSPDocSpecification() {
		return spDocId;
	}
	
	/**
	 * @return	the signature policy document, <code>null</code> when the policy document is only referenced
	 */
	public byte[] getSignaturePolicyDocument() {
		return spDocContent;
	}
	
	/**
	 * @return URI referencing the signature policy document, <code>null</code> when the policy document is included  
	 */
	public String getSigPolDocLocalURI() {
		return spDocReference;
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
		if(!super.equals(o))
			return false;
		
		SignaturePolicyStore other = (SignaturePolicyStore) o;
		
		return Utils.nullSafeEqual(this.id, other.id)
			&& Utils.nullSafeEqual(this.spDocReference, other.spDocReference)
			&& Utils.nullSafeEqual(this.spDocId, other.spDocId)
			&& Arrays.areEqual(this.spDocContent, other.spDocContent);
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
			xwriter.writeIdAttribute("", Constants.XADES_141_NS_URI, "Id", id);         
		
		// Write child elements
		spDocId.marshal(xwriter, dsPrefix, context);		
		if (spDocContent != null)
			xwriter.writeTextElement(nsPrefix, "SignaturePolicyDocument",
										Constants.XADES_141_NS_URI, XMLUtils.encodeToString(spDocContent));
		else {
			xwriter.writeTextElement(nsPrefix, "SigPolDocLocalURI", Constants.XADES_141_NS_URI, spDocReference);
		}
	}

	/**
	 * A representation of the <code>SPDocSpecification</code> element.
	 */
	public class SPDocSpecification extends AbstractObjectIdentifierTypeElement {

		SPDocSpecification(IObjectIdentifier source) {
			super(source);
		}			

		@Override
		protected QName getName() {
			return SPDOC_ELEMENT_NAME;
		}		
	}
}
