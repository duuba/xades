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

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalizationMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMXMLSignatureFactory;
import org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI;
import org.apache.xml.security.Init;
import org.duuba.xades.AbstractEncapsulatedPKIDataTypeElement.Encoding;
import org.w3c.dom.Node;

/**
 * A factory for creating {@link XadesSignature} objects from scratch or for unmarshalling one from an <code>
 * ds:Signature</code> XML element. As Xades signatures are an extension of a regular XML signatures this factory class
 * only provides methods to create components which are specific to Xades signatures. Regular XML signature components 
 * must be created using the {@link XMLSignatureFactory} instance provided by this factory through the {@link 
 * #getXMLSignatureFactory()} method. Note that the factory just creates the structures as specified in the ETSI 
 * specifications but does not check on the additional requirements.    
 * <p>
 * <b></b>
 * <p>
 * <p>
 * <b>XAdES versions</b>
 * <p>This factory supports both ETSI specifications of XAdES, TS 101 903 and EN 319 132. The main difference between
 * these two versions is that for some of the qualifying properties EN 319 132 contains additional data or uses another 
 * representation of the same data. It therefore has included new versions of the corresponding XML element declarations
 * in the XML schema (the elements with "V2" suffix). There are also different classes to represent the different 
 * versions of these properties. This factory will return the correct implementation based on the set version when 
 * creating the factory instance.  
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @see XMLSignatureFactory
 */
public class XadesSignatureFactory {

	private DOMXMLSignatureFactory 	factory;
	private XadesVersion			version;

	/**
	 * Creates a new <code>XadesSignatureFactory</code> instance.
	 * 
	 * @param version	the Xades version to be used 
	 * @throws RuntimeException when the required Apache Santuario is not installed
	 */
	public XadesSignatureFactory(final XadesVersion version) {
		try {
			if (Security.getProvider("ApacheXMLDSig") == null) {
				// Try to install the Apache lib
				Security.addProvider(new XMLDSigRI());
				Init.init();
			}
			this.factory = (DOMXMLSignatureFactory) XMLSignatureFactory.getInstance("DOM", "ApacheXMLDSig");
		} catch (Exception noSantuario) {
			throw new RuntimeException("Required Apache Santuario library not available");
		}
		this.version = version;
	}		
	
	/**
	 * Returns the {@link XMLSignatureFactory} instance that must be used to create the "common" signature components
	 * like <code>ds:Reference</code> and <code>ds:Transform</code>.
	 *  
	 * @return	the factory to create common signature components
	 */
	public XMLSignatureFactory	getXMLSignatureFactory() {
		return factory;
	}
	
	/**
	 * Creates a Xades signature with the specified parameters. 
	 * 
	 * @param id				value to use for the Id attribute of the <code>Signature</code> and with "sv-" prefix
	 * 							for the Id attribute of the <code>SignatureValue</code> 
	 * @param digestAlg			digest algorithm to calculate the hash value of <code>SignedProperties</code> 
	 * @param signingAlg		signing algorithm
	 * @param c14nAlg			canonicalisation algorithm to use for both <code>SignedInfo</code> and 
	 * 							<code>SignedProperties</code>
	 * @param keyInfo			the <code>KeyInfo</code> to include
	 * @param dataReferences	list of <code>Reference</code>s to the signed data objects
	 * @param qProperties		the <code>QualifyingProperties</code> to include in the signature
	 * @param otherObjects		list of other <code>Object</code>s to include in the signature
	 * @return	a <code>XadesSignature</code> instance
	 * @throws NoSuchAlgorithmException when one of the specified algorithm is not available
	 */
	public XadesSignature newXadesSignature(final String id, final String digestAlg, final String signingAlg, 
											final String c14nAlg,
											final KeyInfo keyInfo,
											final List<Reference> dataReferences, 
											final QualifyingProperties qProperties, 
											final List<XMLObject> otherObjects) throws NoSuchAlgorithmException {
		if (id == null || id.isEmpty())
			throw new IllegalArgumentException("A id for the Signature must be provided");
		if (digestAlg == null || digestAlg.isEmpty())
			throw new IllegalArgumentException("A digest algorithm must be provided");
		if (signingAlg == null || signingAlg.isEmpty())
			throw new IllegalArgumentException("A signing algorithm must be provided");
		if (c14nAlg == null || c14nAlg.isEmpty())
			throw new IllegalArgumentException("A canonicalisation algorithm must be provided");
		if (dataReferences == null || dataReferences.isEmpty())
			throw new IllegalArgumentException("A list of references to sign must be provided");
		if (qProperties == null)
			throw new IllegalArgumentException("Qualifying properties must be provided");
		
		try {
			// Create and add a new XMLObject containing the qualifying properties
			List<XMLObject> sigObjects = new ArrayList<XMLObject>();
			if (otherObjects != null) 
				sigObjects.addAll(otherObjects);
			final XMLObject qPropObject = factory.newXMLObject(Collections.singletonList(qProperties), null, null, null);
			sigObjects.add(qPropObject);
			// Create and add a reference to the signed properties so they get included in the signature
			List<Reference> refsToSign = new ArrayList<Reference>(dataReferences);		
			final SignedProperties signedProps = qProperties.getSignedProperties();
			if (signedProps != null) {			
				final Reference qPropsRef = factory.newReference("#" + signedProps.getId(), 
															  factory.newDigestMethod(digestAlg, null),
															  Collections.singletonList(factory.newTransform(c14nAlg,
																	  					(TransformParameterSpec) null)), 
															  Constants.SIGNED_PROPS_REF_TYPE, 
															  null);
				refsToSign.add(qPropsRef);
			}
			// Now create the SignedInfo of the signature
			final SignedInfo si = factory.newSignedInfo(
											factory.newCanonicalizationMethod(c14nAlg, (C14NMethodParameterSpec) null),
											factory.newSignatureMethod(signingAlg, null),
											refsToSign);
			// And finally create the XMLSignature object itself
			final XMLSignature xmlSignature = factory.newXMLSignature(si, keyInfo, sigObjects, id, "SV-" + id);
			
			return new XadesSignature(xmlSignature, qProperties, otherObjects);
		} catch (InvalidAlgorithmParameterException e) {
			// This exception occurs when a c14n algorithm requiring parameters is specified. This is however not
			// supported by this method
			throw new UnsupportedOperationException("Parameterised c14n algorithm is not supported");
		}
	}
	
	/**
	 * Creates a <code>QualifyingProperties</code> with the specified parameters.
  	 *
	 * @param target URI identifying the <code>ds:Signature</code> the properties apply to
	 * @param signedProperties	the signed properties
	 * @return	a <code>QualifyingProperties</code> instance
	 */
	public QualifyingProperties	newQualifyingProperties(final String target, final SignedProperties signedProperties) {
		return new QualifyingProperties(null, target, signedProperties, null);
	}
	
	/**
	 * Creates a new <code>KeyInfo</code> with the given certificate. 
	 * <p>As specified in the Xades specification the certificate will be included in a <code>ds:X509Data</code>
	 * element.
	 * 
	 * @param cert	certificate to be included
	 * @return a <code>KeyInfo</code> instance
	 */
	public KeyInfo newKeyInfo(final X509Certificate cert) {
		return newKeyInfo(Collections.singletonList(cert));
	}
	
	/**
	 * Creates a new <code>KeyInfo</code> for the given set of certificates. 
	 * <p>As specified in the Xades specification the certificates will be included in a single <code>ds:X509Data</code>
	 * element. The order of the certificates will be maintained in the XML structure.   
	 * 
	 * @param certs	list of certificates to be included
	 * @return a <code>KeyInfo</code> instance
	 */
	public KeyInfo newKeyInfo(final List<X509Certificate> certs) {
		final KeyInfoFactory kif = factory.getKeyInfoFactory();
		return kif.newKeyInfo(Collections.singletonList(kif.newX509Data(certs)));
	}
	
	/**
	 * Creates a <code>SignedProperties</code> with the specified parameters.
	 *
	 * @param id the identifier to use for the new object, used in the <code>ds:Reference</code> 
	 * @param signatureProperties	the properties applying to the whole signature
	 * @return	a <code>SignedProperties</code> instance
	 */
	public SignedProperties	newSignedProperties(final String id, final SignedSignatureProperties signatureProperties) {
		return new SignedProperties(id, signatureProperties, null);
	}

	/**
	 * Creates a <code>SignedProperties</code> with the specified parameters.
	 *
	 * @param id the identifier to use for the new object, used in the <code>ds:Reference</code> 
	 * @param signatureProperties	the properties applying to the whole signature
	 * @param dataObjectProperties	the properties applying to specific data objects
	 * @return	a <code>SignedProperties</code> instance
	 */
	public SignedProperties	newSignedProperties(final String id, final SignedSignatureProperties signatureProperties,
												final SignedDataObjectProperties dataObjectProperties) {
		return new SignedProperties(id, signatureProperties, dataObjectProperties);
	}
	
	/**
	 * Creates a <code>SignedSignatureProperties</code> with the specified parameters.
	 *
	 * @param signingTime	time the signature was created
	 * @param certInfo 		the certificate information to include
	 * @return	a <code>SignedSignatureProperties</code> instance
	 */
	public SignedSignatureProperties newSignedSignatureProperties(final ZonedDateTime signingTime,
																  final SigningCertificate certInfo) {
		return new SignedSignatureProperties(null, signingTime, certInfo, null, null, null, null);
	}
	
	/**
	 * Creates a <code>SignedSignatureProperties</code> with the specified parameters.
	 *
	 * @param signingTime	 time the signature was created
	 * @param certInfo 		 the certificate information to include
	 * @param policy		 information on the signature policy that applies 
	 * @param signaturePlace signer's location 
	 * @param signerRoles 	 information about role of signer
	 * @param other 		 other information to include
	 * @return	a <code>SignedSignatureProperties</code> instance
	 */
	public SignedSignatureProperties newSignedSignatureProperties(final ZonedDateTime signingTime,
			final SigningCertificate certInfo, final SignaturePolicyIdentifier policy, 
			final SignatureProductionPlace signaturePlace, SignerRole signerRoles, final List<Node> other) {
		return new SignedSignatureProperties(null, signingTime, certInfo, policy, signaturePlace, signerRoles, other);
	}
	
	/**
	 * Creates a <code>SignedDataObjectProperties</code> with the specified parameters.
	 * 
	 * @param dataObjectFormats		meta-data on the format of the signed objects
	 * @return	a <code>SignedDataObjectProperties</code> instance
	 */
	public SignedDataObjectProperties newSignedDataObjectProperties(final List<DataObjectFormat> dataObjectFormats) {
		return new SignedDataObjectProperties(null, dataObjectFormats, null);
	}
	
	/**
	 * Creates a <code>SignedDataObjectProperties</code> with the specified parameters.
	 * 
	 * @param dataObjectFormats		meta-data on the format of the signed objects
	 * @param commitments 			information on the signer's commitments on the signed objects
	 * @return	a <code>SignedDataObjectProperties</code> instance
	 */
	public SignedDataObjectProperties newSignedDataObjectProperties(final List<DataObjectFormat> dataObjectFormats, 
																	final List<CommitmentTypeIndication> commitments) {
		return new SignedDataObjectProperties(null, dataObjectFormats, commitments);
	}
	
	/**
	 * Creates a <code>SigningCertificate</code> with the specified parameters. Depending on the Xades version the
	 * factory ensures the correct representation of the element is created.
	 *
	 * @param certs			the certificates to include
	 * @param digestMethod	the digest method to use for creating the hash values
	 * @return	a <code>SigningCertificate</code> instance
	 * @throws NoSuchAlgorithmException  when the request digest method is not available
	 */
	public SigningCertificate newSigningCertificate(final List<X509Certificate> certs, final String digestMethod) 
																					throws NoSuchAlgorithmException {
		return version == XadesVersion.TS_101_903_V141 ? new SigningCertificate(certs, digestMethod)
													   : new SigningCertificateV2(certs, digestMethod); 
	}	

	/**
	 * Creates a <code>SignaturePolicyIdentifier</code> for an implied signature policy.
	 * 
	 * @return a <code>SignaturePolicyIdentifier</code> instance
	 */
	public SignaturePolicyIdentifier newSignaturePolicyIdentifier() {
		return new SignaturePolicyIdentifier(null, null, null, null, null);
	}
	
	/**
	 * Creates a <code>SignaturePolicyIdentifier</code> that contains an explicit reference to a signature policy.
	 * 
	 * @param policyId
	 * @param policyDigestAlgoritm
	 * @param policyHash
	 * @return a <code>SignaturePolicyIdentifier</code> instance
	 */
	public SignaturePolicyIdentifier newSignaturePolicyIdentifier(final IObjectIdentifier policyId, 
															      final String policyDigestAlgoritm,
															      final byte[] policyHash) {
		return new SignaturePolicyIdentifier(policyId, null, policyDigestAlgoritm, policyHash, null);
	}
	
	/**
	 * Creates a <code>SignaturePolicyIdentifier</code> that contains an explicit reference to a signature policy.
	 * 
	 * @param policyId		the policy's identifier
	 * @param transforms	list of transformations applied to policy documents before hash was calculated
	 * @param policyDigestAlgoritm	the digest algorithm used to calculate the hash value
	 * @param policyHash	the calculated hash value
	 * @param qualifiers	the policy qualifiers 
	 * @return a <code>SignaturePolicyIdentifier</code> instance
	 */
	public SignaturePolicyIdentifier newSignaturePolicyIdentifier(final IObjectIdentifier policyId,
																  final List<Transform> transforms,
																  final String policyDigestAlgoritm, 
																  final byte[] policyHash, 
																  final List<SigPolicyQualifier> qualifiers) {
		return new SignaturePolicyIdentifier(policyId, transforms, policyDigestAlgoritm, policyHash, qualifiers);
	}
	
	/**
	 * Creates a <code>SigPolicyQualifier</code> with the specified contents. 
	 * 
	 * @param content	the XML nodes that contain the data of the signature policy qualifier
	 * @return	a <code>SigPolicyQualifier</code> instance
	 */
	public SigPolicyQualifier newSigPolicyQualifier(final List<Node> content) {
		return new SigPolicyQualifier(content);
	}
	
	
	
	/**
	 * Creates a <code>SignerRole</code> with the specified parameters. Depending on the Xades version the factory 
	 * ensures the correct representation of the element is created.
	 * 
	 * @param claimedRoles		list of claimed roles
	 * @param certifiedRoles 	list of certified roles
	 * @return a <code>SignerRole</code> instance
	 */
	public SignerRole newSignerRole(final List<ClaimedRole> claimedRoles, final List<CertifiedRole> certifiedRoles) {
		return version == XadesVersion.TS_101_903_V141 ? 
					  new SignerRole(claimedRoles, certifiedRoles) 
					: new SignerRoleV2(claimedRoles, certifiedRoles, null);
	}
	
	/**
	 * Creates a <code>SignerRole</code> with the specified parameters. 
	 * <p>NOTE: This kind of <code>SignerRole</code> is only supported in <i>EN 319 132 V1.1.1</i> based signatures.
	 * 
	 * @param assertions		list of signed assertions
	 * @return a <code>SignerRole</code> instance
	 * @throws UnsupportedOperationException when called on a factory initialised for <i>TS 101 903 V1.4.1</i>
	 */
	public SignerRole newSignerRole(final List<SignedAssertion> assertions) {
		if (version == XadesVersion.TS_101_903_V141) 
			throw new UnsupportedOperationException();
		else 
			return new SignerRoleV2(null, null, assertions);
	}
	
	/**
	 * Creates a <code>SignerRole</code> with the specified parameters. 
	 * <p>NOTE: This kind of <code>SignerRole</code> is only supported in <i>EN 319 132 V1.1.1</i> based signatures.
	 * 
	 * @param claimedRoles		list of claimed roles
	 * @param certifiedRoles 	list of certified roles
	 * @param assertions		list of signed assertions
	 * @return a <code>SignerRole</code> instance
	 * @throws UnsupportedOperationException when called on a factory initialised for <i>TS 101 903 V1.4.1</i>
	 */
	public SignerRole newSignerRole(final List<ClaimedRole> claimedRoles, final List<CertifiedRole> certifiedRoles,
									final List<SignedAssertion> assertions) {
		if (version == XadesVersion.TS_101_903_V141 && assertions != null) 
			throw new UnsupportedOperationException();
		else 
			return version == XadesVersion.TS_101_903_V141 ? new SignerRole(claimedRoles, certifiedRoles) 
														   : new SignerRoleV2(claimedRoles, certifiedRoles, assertions);
	}	
	
	/**
	 * Creates a <code>ClaimedRole</code> with the specified contents. 
	 * 
	 * @param content	the XML nodes that contain the data of the claimed role 
	 * @return	a <code>ClaimedRole</code> instance
	 */
	public ClaimedRole newClaimedRole(List<Node> content) {
		return new ClaimedRole(content);
	}
	
	/**
	 * Creates a <code>CertifiedRole</code> based on a X509 attribute certificate with the specified parameters. 
	 * Depending on the Xades version the factory ensures the correct representation of the element is created.
	 * 
	 * @param id 		the id to identify the certificate included in the role instance
	 * @param attrCert 	the X509 attribute certificate 
	 * @return	a <code>CertifiedRole</code> instance
	 * @throws CertificateEncodingException when the given certificate cannot be DER encoded
	 */
	public CertifiedRole newCertifiedRole(final String id, final X509Certificate attrCert) 
																				throws CertificateEncodingException {
		return version == XadesVersion.TS_101_903_V141 ? new CertifiedRoleV1(id, attrCert) 
													   : new CertifiedRoleV2(id, attrCert);
	}
	
	/**
	 * Creates a <code>CertifiedRole</code> based on a non X509 based certificate.  
	 * <p>NOTE: This kind of <code>CertifiedRole</code> is only supported in <i>EN 319 132 V1.1.1</i> based signatures. 
	 *  
	 * @param content  the XML nodes that contain the data of the certified role
	 * @return	a <code>CertifiedRole</code> instance
	 * @throws UnsupportedOperationException when called on a factory initialised for <i>TS 101 903 V1.4.1</i>  
	 */
	public CertifiedRole newCertifiedRole(List<Node> content) {
		if (version == XadesVersion.TS_101_903_V141)
			throw new UnsupportedOperationException();
		else 
			return new CertifiedRoleV2(content);
	}
	
	/**
	 * Creates a <code>SignedAssertion</code> based on a non X509 based certificate.  
	 * <p>NOTE: <code>SignedAssertion</code>s are only supported in <i>EN 319 132 V1.1.1</i> based signatures. 
	 *  
	 * @param content  the XML nodes that contain the data of the assertion
	 * @return	a <code>SignedAssertion</code> instance
	 * @throws UnsupportedOperationException when called on a factory initialised for <i>TS 101 903 V1.4.1</i>  
	 */
	public SignedAssertion newSignedAssertion(List<Node> content) {
		if (version == XadesVersion.TS_101_903_V141)
			throw new UnsupportedOperationException();
		else 
			return new SignedAssertion(content);
	}	
	
	/**
	 * Creates a <code>SignatureProductionPlace</code> with the specified parameters. 
	 * Depending on the Xades version the factory ensures the correct representation of the element is created.
	 * 
	 * @param city				the city where of the signer's location
	 * @param street			the street name of the signer's location. NOTE: as this field is not supported in 
	 * 							{@link XadesVersion#TS_101_903_V141} it is ignored when factory is set to this version
	 * @param postalCode		the postal code of the signer's location
	 * @param stateOrProvince	the state or province of the signer's location
	 * @param countryName		the name of the country of the signer's location
	 * @return a <code>SignatureProductionPlace</code> instance
	 */
	public SignatureProductionPlace newSignatureProductionPlace(final String city, final String street,
															    final String postalCode, 
															    final String stateOrProvince, 
															    final String countryName) {
		return version == XadesVersion.TS_101_903_V141 ? 
						new SignatureProductionPlace(city, stateOrProvince, postalCode, countryName)
					:	new SignatureProductionPlaceV2(city, street, stateOrProvince, postalCode, countryName);
	}
	
	/**
	 * Creates a <code>DataObjectFormat</code> with the specified parameters.
	 * 
	 * @param objectRef		URI pointing to the <code>ds:Reference</code> this element applies to 
	 * @param description	the description of the data object
	 * @param objectId		the object identifier of the data object
	 * @param mimeType		the MIME type of the data object
	 * @param encoding		the encoding of the data object
	 * @return a <code>DataObjectFormat</code> instance
	 */
	public DataObjectFormat newDataObjectFormat(final String objectRef, final String description, 
												final IObjectIdentifier objectId, final String mimeType, 
												final String encoding) {
		return new DataObjectFormat(objectRef, description, objectId, mimeType, encoding);
	}
	
	/**
	 * Creates a <code>CommitmentTypeIndication</code> that applies to all the signed objects with the specified 
	 * parameters.  
	 * 
	 * @param typeId		object identifier of the commitment
	 * @param qualifiers 	additional qualifiers for the commitment
	 * @return a <code>CommitmentTypeIndication</code> instance
	 */
	public CommitmentTypeIndication newCommitmentTypeIndication(final IObjectIdentifier typeId, 
																final List<CommitmentTypeQualifier> qualifiers) {
		return new CommitmentTypeIndication(typeId, null, qualifiers);
	}
	
	/**
	 * Creates a <code>CommitmentTypeIndication</code> with the specified parameters that applies only to the referenced
	 * signed data objects.
	 * 
	 * @param objectRefs	list of URIs pointing to the <code>ds:Reference</code>s the commitment applies to 
	 * @param typeId		object identifier of the commitment
	 * @param qualifiers 	additional qualifiers for the commitment
	 * @return a <code>CommitmentTypeIndication</code> instance
	 */
	public CommitmentTypeIndication newCommitmentTypeIndication(final List<String> objectRefs, 
																final IObjectIdentifier typeId, 
																final List<CommitmentTypeQualifier> qualifiers) {
		return new CommitmentTypeIndication(typeId, objectRefs, qualifiers);
	}
	
	/**
	 * Creates a <code>CommitmentTypeQualifier</code> with the specified contents. 
	 * 
	 * @param content	the XML nodes that contain the data of the commitment type qualifier 
	 * @return	a <code>CommitmentTypeQualifier</code> instance
	 */
	public CommitmentTypeQualifier newCommitmentTypeQualifier(final List<Node> content) {
		return new CommitmentTypeQualifier(content);
	}
	
	/**
	 * Creates a <code>UnsignedProperties</code> with the specified parameters.
	 *
	 * @param id the identifier to use for the new object 
	 * @param signatureProperties	the properties applying to the whole signature
	 * @param dataObjectProperties  the properties applying to specific data objects
	 * @return	a <code>UnsignedProperties</code> instance
	 */
	public UnsignedProperties newUnsignedProperties(final String id, 
													final UnsignedSignatureProperties signatureProperties,
													final UnsignedDataObjectProperties dataObjectProperties) {
		return new UnsignedProperties(id, signatureProperties, dataObjectProperties);
	}	
	
	/**
	 * Creates a <code>UnsignedSignatureProperties</code> with the specified parameters.
	 *
	 * @param id 	the identifier to use for the new object 
	 * @param policyStore	information on the signature policy storage
	 * @return	a <code>UnsignedSignatureProperties</code> instance
	 */
	public UnsignedSignatureProperties newUnsignedSignatureProperties(final String id, 
																	  final SignaturePolicyStore policyStore) {
		return new UnsignedSignatureProperties(id, policyStore);
	}	
	
	/**
	 * Creates a <code>SignaturePolicyStore</code> with the specified parameters.
	 * <p>NOTE: This qualifying attribute is only supported in <i>EN 319 132 V1.1.1</i> based signatures.
	 *  
	 * @param specId 		  the object identifier of the policy document's specification
	 * @param policyDocument  the policy document 
	 * @return	a <code>SignaturePolicyStore</code> instance
	 * @throws UnsupportedOperationException when called on a factory initialised for <i>TS 101 903 V1.4.1</i>  
	 */
	public SignaturePolicyStore newSignaturePolicyStore(final IObjectIdentifier specId, final byte[] policyDocument) {
		if (version == XadesVersion.TS_101_903_V141)
			throw new UnsupportedOperationException();
		else 
			return new SignaturePolicyStore(specId, policyDocument, null, null);
	}
	
	/**
	 * Creates a <code>SignaturePolicyStore</code> with the specified parameters.
	 * <p>NOTE: This qualifying attribute is only supported in <i>EN 319 132 V1.1.1</i> based signatures. 
	 * 
	 * @param specId 		      the object identifier of the policy document's specification
	 * @param localRefToDocument  reference to the policy document on local storage
	 * @return	a <code>SignaturePolicyStore</code> instance
	 * @throws UnsupportedOperationException when called on a factory initialised for <i>TS 101 903 V1.4.1</i>  
	 */
	public SignaturePolicyStore newSignaturePolicyStore(final IObjectIdentifier specId, final String localRefToDocument){
		if (version == XadesVersion.TS_101_903_V141)
			throw new UnsupportedOperationException();
		else 
			return new SignaturePolicyStore(specId, null, localRefToDocument, null);
	}
	
	/**
	 * Creates a <code>ObjectIdentifier</code> with the specified parameters.
	 * 
	 * @param identifier	the identifier value
	 * @return	a <code>ObjectIdentifier</code> instance
	 */
	public ObjectIdentifier newObjectIdentifier(final String identifier) {
		return new ObjectIdentifier(identifier, null, null, null);
	}
	
	/**
	 * Creates a <code>ObjectIdentifier</code> with the specified parameters.
	 * 
	 * @param identifier	the identifier value
	 * @param qualifier		the identifier qualifier
	 * @param description	description of the identifier
	 * @param docReferences list of references to documentation on the identifier
	 * @return	a <code>ObjectIdentifier</code> instance
	 */
	public ObjectIdentifier newObjectIdentifier(final String identifier, final QualifierType qualifier, 
												final String description, final List<String> docReferences) {
		return new ObjectIdentifier(identifier, qualifier, description, docReferences);
	}
	
	/**
	 * Creates a <code>Include</code> with the specified parameters.
	 * 
	 * @param uri				pointer to the data that is included				
	 * @param referencedData	indication whether the actual data was used when the URI points to a 
	 * 							<code>ds:Reference</code> element
	 * @return	a <code>Include</code> instance
	 */
	public Include newInclude(final String uri, final Boolean referencedData) {
		return new Include(uri, referencedData);
	}	
	
	/**
	 * Creates a <code>ReferenceInfo</code> with the specified parameters.
	 * 
	 * @param id		the identifier to use for the new object
	 * @param uri		reference to the data object
	 * @param digestAlg	the digest algorithm applied to the external data object
	 * @param digestVal the calculated digest value of the referenced data object
	 * @return	a <code>ReferenceInfo</code> instance
	 */
	public ReferenceInfo newReferenceInfo(final String id, final String uri, 
										  final String digestAlg, final byte[] digestVal) {
		return new ReferenceInfo(id, uri, digestAlg, digestVal);
	}	
	
	/**
	 * Creates a <code>XMLTimeStamp</code> with the specified contents. 
	 * 
	 * @param content	the XML nodes that contain the data of the XML time stamp 
	 * @return	a <code>XMLTimeStamp</code> instance
	 */
	public XMLTimeStamp newXMLTimeStamp(final List<Node> content) {
		return new XMLTimeStamp(content);
	}
	
	/**
	 * Creates a <code>EncapsulatedTimeStamp</code> with the specified contents. 
	 * 
	 * @param id		identifier to use for the new object
	 * @param data		encoded time stamp data
	 * @param encoding  type of encoding used 
	 * @return	a <code>EncapsulatedTimeStamp</code> instance
	 */
	public EncapsulatedTimeStamp newXEncapsulatedTimeStamp(final String id, final byte[] data, final Encoding encoding) {
		return new EncapsulatedTimeStamp(id, data, encoding);
	}
	
	/**
	 * Create a <code>XAdESTimeStamp</code> with the specified parameters.   
	 * <p>NOTE: The created instance can also be used as input to   
	 * 
	 * @param id				identifier to use for the new object
	 * @param includes			list of references to specific data objects that contribute to the input of the 
	 * 							message imprint's computation. May be <code>null</code> or empty when the input is 
	 * 							implicit
	 * @param c14nMethod		canonicalization method that is used when the data objects are XML
	 * @param encapsulatedTS	the time stamp data in encapsulated PKI format 
	 * @return	a <code>XadesTimeStamp</code> instance
	 */
	public XadesTimeStamp newXAdESTimeStamp(final String id, final List<Include> includes, 
											final DOMCanonicalizationMethod c14nMethod,
											final List<EncapsulatedTimeStamp> encapsulatedTS) {
		return new XadesTimeStamp(id, includes, c14nMethod, encapsulatedTS, null);
	}
	
	/**
	 * Converts a Java {@link ZonedDateTime} object into a XML datetime string.
	 * 
	 * @param datetime	Java datetime object
	 * @return			XML string representation of the datetime
	 */
	public static String convertToXMLString(final ZonedDateTime datetime) {
		try {
			XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
																					GregorianCalendar.from(datetime));
			xmlCalendar.setFractionalSecond(null);
			return xmlCalendar.toXMLFormat(); 
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("Required DatatypeFactory missing");
		}	
	}
	
}
