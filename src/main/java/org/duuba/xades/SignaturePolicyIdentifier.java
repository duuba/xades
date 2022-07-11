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
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.DOMTransform;
import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.holodeckb2b.commons.util.Utils;
import org.w3c.dom.Node;

/**
 * A representation of the <code>SignaturePolicyIdentifier</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1
 * </i> standard. The XML schema is defined as:
 * <code>
 * &lt;xsd:element name="SignaturePolicyIdentifier" type="SignaturePolicyIdentifierType"/&gt;
 * &lt;xsd:complexType name="SignaturePolicyIdentifierType"&gt;
 * 	&lt;xsd:choice&gt;
 * 		&lt;xsd:element name="SignaturePolicyId" type="SignaturePolicyIdType"/&gt;
 * 		&lt;xsd:element name="SignaturePolicyImplied"/&gt;
 * 	&lt;/xsd:choice&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:complexType name="SignaturePolicyIdType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="SigPolicyId" type="ObjectIdentifierType"/&gt;
 * 		&lt;xsd:element ref="ds:Transforms" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="SigPolicyHash" type="DigestAlgAndValueType"/&gt;
 * 		&lt;xsd:element name="SigPolicyQualifiers" type="SigPolicyQualifiersListType" minOccurs="0"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:complexType name="SigPolicyQualifiersListType"&gt;
 *	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="SigPolicyQualifier" type="AnyType" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </code> 
 *
 * <p>A <code>SignaturePolicyIdentifier</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newSignaturePolicyIdentifier} methods.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class SignaturePolicyIdentifier extends XadesElement {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignaturePolicyIdentifier", 
			Constants.XADES_132_NS_PREFIX);
	
	private SigPolicyId	signaturePolicyId;
	private List<Transform> transforms;
	private SigPolicyHash	policyHash;
	private List<SigPolicyQualifier> qualifiers;
	
	SignaturePolicyIdentifier() {		
	}
	
	SignaturePolicyIdentifier(final IObjectIdentifier policyId, final List<Transform> transforms, 
							  final String policyDigestAlgoritm, final byte[] policyHash, 
							  final List<SigPolicyQualifier> qualifiers) {
		this.signaturePolicyId = new SigPolicyId(policyId);
		this.transforms = transforms;
		this.policyHash = new SigPolicyHash(policyDigestAlgoritm, policyHash);
		this.qualifiers = qualifiers;
	}
	
	/**
	 * @return	whether the applicable policy is implicit  or if specific policy information is provided
	 */
	public boolean isImpliedPolicy() {
		return signaturePolicyId == null;
	}
	
	/**
	 * @return	the object identifier of the applicable signing policy
	 */
	public SigPolicyId	getSigPolicyId() {
		return signaturePolicyId;
	}
	
	/**
	 * Returns the transformations performed on the signature policy document. Note that the transformations and the
	 * actual referencing to the policy document is not specified in the Xades specification but depends on additional
	 * specifications and/or profiles. 
	 *  
	 * @return	list of transformations to be performed on the policy document before computing the hash value
	 */
	public List<Transform> getTransforms() {
		return transforms;
	}
	
	/**
	 * @return	the digest algorithm used to calculate the hash value of the policy document
	 */
	public String getDigestAlgorithm() {
		return policyHash.getDigestMethod();
	}
	
	/**
	 * @return	the calculated hash value of the policy document
	 */
	public byte[] getHashValue() {
		return policyHash.getDigestValue();
	}
	
	/**
	 * @return	additional information qualifying the signature policy identifier 
	 */
	public List<SigPolicyQualifier> getPolicyQualifiers() {
		return qualifiers;
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
		
		SignaturePolicyIdentifier other = (SignaturePolicyIdentifier) o;
		return Utils.nullSafeEqual(this.policyHash, other.policyHash)
			&& Utils.nullSafeEqual(this.signaturePolicyId, other.signaturePolicyId)
			&& Utils.areEqual(this.qualifiers, other.qualifiers)
			&& Utils.areEqual(this.transforms, other.transforms);
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
		if (signaturePolicyId == null) {
			// write the empty SignaturePolicyImplied element
			xwriter.writeStartElement(nsPrefix, "SignaturePolicyImplied", Constants.XADES_132_NS_URI);
			xwriter.writeEndElement();
		} else {
			// write the SignaturePolicyId element and its children
			xwriter.writeStartElement(nsPrefix, "SignaturePolicyId", Constants.XADES_132_NS_URI);
			// write the SigPolicyId element
			signaturePolicyId.marshal(xwriter, dsPrefix, context);
			// write the ds:Transforms element
			if (transforms != null && !transforms.isEmpty()) {
				xwriter.writeStartElement(dsPrefix, "Transforms", XMLSignature.XMLNS);
				for (Transform t : transforms)
					((DOMTransform) t).marshal(xwriter, dsPrefix, context);
				xwriter.writeEndElement();
			}
			// write the SigPolicyHash element
			policyHash.marshal(xwriter, dsPrefix, context);
			// write the SigPolicyQualifiers and child elements
			if (qualifiers != null && !qualifiers.isEmpty()) {
				xwriter.writeStartElement(nsPrefix, "SigPolicyQualifiers", Constants.XADES_132_NS_URI);
				for (SigPolicyQualifier q : qualifiers)
					q.marshal(xwriter, dsPrefix, context);
				xwriter.writeEndElement();				
			}			
			xwriter.writeEndElement();			
		}					
	}

	/**
	 * A representation of the <code>SigPolicyId</code> element.
	 */
	public static class SigPolicyId extends AbstractObjectIdentifierTypeElement {
		private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SigPolicyId", 
				Constants.XADES_132_NS_PREFIX);
		
		SigPolicyId(IObjectIdentifier source) {
			super(source);
		}

		@Override
		protected QName getName() {
			return ELEMENT_NAME;
		}
	}
	
	/**
	 * A representation of the <code>SigPolicyHash</code> element.
	 */
	public static class SigPolicyHash extends AbstractDigestAlgAndValueTypeElement {
		private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SigPolicyHash", 
				Constants.XADES_132_NS_PREFIX);

		SigPolicyHash(String digestAlg, byte[] digestVal) {
			super(digestAlg, digestVal);
		}

		@Override
		protected QName getName() {
			return ELEMENT_NAME;
		}		
	}
	
	public static class SigPolicyQualifier extends AbstractAnyTypeElement {
		private static final QName ELEMENT_NAME = 
							new QName(Constants.XADES_132_NS_URI, "SigPolicyQualifier", Constants.XADES_132_NS_PREFIX);

		SigPolicyQualifier(List<Node> content) {
			super(content);
		}
		
		@Override
		protected QName getName() {
			return ELEMENT_NAME;
		}

	}
	
}
