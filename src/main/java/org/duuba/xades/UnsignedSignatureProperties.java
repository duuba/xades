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
 * A representation of the <code>UnsignedSignatureProperties</code> element as defined in the <i>ETSI EN 319 132-1
 * V1.1.1</i> standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="UnsignedSignatureProperties" type="UnsignedSignaturePropertiesType"/&gt;
 * &lt;xsd:complexType name="UnsignedSignaturePropertiesType"&gt;
 * 	&lt;xsd:choice maxOccurs="unbounded"&gt;
 * 		&lt;xsd:element ref="CounterSignature"/&gt;
 * 		&lt;xsd:element ref="SignatureTimeStamp"/&gt;
 * 		&lt;xsd:element ref="CompleteCertificateRefs"/&gt;
 * 		&lt;xsd:element ref="CompleteRevocationRefs"/&gt;
 * 		&lt;xsd:element ref="AttributeCertificateRefs"/&gt;
 * 		&lt;xsd:element ref="AttributeRevocationRefs"/&gt;
 * 		&lt;xsd:element ref="SigAndRefsTimeStamp"/&gt;
 * 		&lt;xsd:element ref="RefsOnlyTimeStamp"/&gt;
 * 		&lt;xsd:element ref="CertificateValues"/&gt;
 * 		&lt;xsd:element ref="RevocationValues"/&gt;
 * 		&lt;xsd:element ref="AttrAuthoritiesCertValues"/&gt;
 * 		&lt;xsd:element ref="AttributeRevocationValues"/&gt;
 * 		&lt;xsd:element ref="ArchiveTimeStamp"/&gt;
 * 		&lt;xsd:any namespace="##other"/&gt;
 * 	&lt;/xsd:choice&gt;
 * 	&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 * &lt;/xsd:complexType&gt;</pre></code> 
 * Beside the child elements defined by the complex type the specification defines additional child elements in the
 * <i>1.4.1</i> schema that can be part of this element (of course only when a EN 319 132 conformant signature is 
 * created).   
 * 
 * <p><b>NOTE</b>: Currently only the <code>SignaturePolicyStore</code> child element is supported as this is the only
 * element that may be used in "basic" signatures. 
 * 
 * <p>A <code>UnsignedSignatureProperties</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newUnsignedSignatureProperties} methods. 
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class UnsignedSignatureProperties extends XadesElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "UnsignedSignatureProperties", 
																Constants.XADES_132_NS_PREFIX);
	
	private String					id;
	private	SignaturePolicyStore	sigPolicyStore;
	
	public UnsignedSignatureProperties(final String id, final SignaturePolicyStore policyStore) {
		this.id = id;
		this.sigPolicyStore = policyStore;
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
     * Returns either the content of the signature policy document or a pointer to local file storage where the document
     * can be found.
     *  
     * @return	the signature policy document information 
     */
    public SignaturePolicyStore getSignaturePolicyStore() {
    	return sigPolicyStore;
    }
    
    @Override
    protected QName getName() {
    	return ELEMENT_NAME;
    }
	
    @Override
    protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
    		throws MarshalException {
    	
		// Write attribute
		if (id != null && !id.isEmpty())
			xwriter.writeIdAttribute("", Constants.XADES_132_NS_URI, "Id", id);         

		// Write child elements
		if (sigPolicyStore != null)
			sigPolicyStore.marshal(xwriter, dsPrefix, context);
	}

}
