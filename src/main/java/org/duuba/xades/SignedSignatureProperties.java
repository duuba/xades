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

import java.time.ZonedDateTime;
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.w3c.dom.Node;

/**
 * A representation of the <code>SignedSignatureProperties</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1
 * </i> standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SignedSignatureProperties" type="SignedSignaturePropertiesType"/&gt;
 * &lt;xsd:complexType name="SignedSignaturePropertiesType"&gt; 
 *	&lt;xsd:sequence&gt;
 *		&lt;xsd:element ref="SigningTime" minOccurs="0"/&gt;
 *		&lt;xsd:element ref="SigningCertificate" minOccurs="0"/&gt;
 *		&lt;xsd:element ref="SigningCertificateV2" minOccurs="0"/&gt; 
 *		&lt;xsd:element ref="SignaturePolicyIdentifier" minOccurs="0"/&gt; 
 *		&lt;xsd:element ref="SignatureProductionPlace" minOccurs="0"/&gt; 
 *		&lt;xsd:element ref="SignatureProductionPlaceV2" minOccurs="0"/&gt; 
 *		&lt;xsd:element ref="SignerRole" minOccurs="0"/&gt;
 *		&lt;xsd:element ref="SignerRoleV2" minOccurs="0"/&gt;
 *		&lt;xsd:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 *	&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt; 
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SignedSignatureProperties</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newSignedSignatureProperties} methods.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SignedSignatureProperties extends XadesElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignedSignatureProperties", 
																	Constants.XADES_132_NS_PREFIX);
	private String				id;
	private ZonedDateTime 		signingTime;
	private SigningCertificate 	certInfo;
	private SignaturePolicyIdentifier signaturePolicy;
	private SignatureProductionPlace  productionPlace;
	private SignerRole			signerRole;
	private List<Node>			otherContent;
	
	SignedSignatureProperties(final String id, final ZonedDateTime signingTime, 
							  final SigningCertificate certificateInfo,
							  final SignaturePolicyIdentifier signaturePolicy,
							  final SignatureProductionPlace productionPlace,
							  final SignerRole signerRole,
							  final List<Node> other) {
		this.id = id;
		this.signingTime = signingTime;
		this.certInfo = certificateInfo;
		this.signaturePolicy = signaturePolicy;
		this.productionPlace = productionPlace;
		this.signerRole = signerRole;
		this.otherContent = other;
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
	 * @return the time at which the signer claims to having performed the signing process.
	 */
	public ZonedDateTime getSigningTime() {
		return signingTime;
	}
	
	/**
	 * Returns the meta-data on the certificate used to create the signature. If needed this includes certificates of
	 * CA's to enable the receiver of the signature to validate the trust. 
	 * 
	 * @return depending on the Xades version used, either a {@link SigningCertificate} (for TS 101 903) or
	 * 			{@link SigningCertificateV2} (for EN 319 132) instance
	 */
	public SigningCertificate getSigningCertificate() {
		return certInfo;
	}
	
	/**
	 * @return information on the policy that applies to this signature
	 */
	public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
		return signaturePolicy;
	}

	/**
	 * Returns the address where the signer was located  
	 * 
	 * @return depending on the Xades version used, either a {@link SignatureProductionPlace} (for TS 101 903) or
	 * 			{@link SignatureProductionPlaceV2} (for EN 319 132) instance
	 */
	SignatureProductionPlace getSignatureProductionPlace() {
		return productionPlace;
	}
	
	/**
	 * Returns information about the signer's role(s)
	 * 
	 * @return depending on the Xades version used, either a {@link SignerRole} (for TS 101 903) or 
	 * 		   {@link SignerRoleV2} (for EN 319 132) instance
	 */
	SignerRole getSignerRole() {
		return signerRole;
	}
	
	/**
	 * @return	other elements that may be included in the signed signature properties
	 */
	List<Node> getOtherContent() {
		return otherContent;
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
		if (signingTime != null)
			xwriter.writeTextElement(nsPrefix, "SigningTime", Constants.XADES_132_NS_URI, 
										XadesSignatureFactory.convertToXMLString(signingTime));
		if (certInfo != null)
			certInfo.marshal(xwriter, dsPrefix, context);
		if (signaturePolicy != null)
			signaturePolicy.marshal(xwriter, dsPrefix, context);
		if (productionPlace != null)
			productionPlace.marshal(xwriter, dsPrefix, context);
		if (signerRole != null)
			signerRole.marshal(xwriter, dsPrefix, context);
		if (otherContent != null && !otherContent.isEmpty())
			for (Node n : otherContent)
				xwriter.marshalStructure(new javax.xml.crypto.dom.DOMStructure(n), dsPrefix, context);										
	}
}
