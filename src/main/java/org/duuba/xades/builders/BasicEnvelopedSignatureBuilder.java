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
package org.duuba.xades.builders;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.duuba.xades.CertifiedRole;
import org.duuba.xades.ClaimedRole;
import org.duuba.xades.CommitmentTypeIndication;
import org.duuba.xades.CommitmentTypeIndication.CommitmentTypeQualifier;
import org.duuba.xades.DataObjectFormat;
import org.duuba.xades.IObjectIdentifier;
import org.duuba.xades.QualifyingProperties;
import org.duuba.xades.SignatureProductionPlace;
import org.duuba.xades.SignedAssertion;
import org.duuba.xades.SignedDataObjectProperties;
import org.duuba.xades.SignedProperties;
import org.duuba.xades.SignedSignatureProperties;
import org.duuba.xades.SignerRole;
import org.duuba.xades.SigningCertificate;
import org.duuba.xades.XadesSignature;
import org.duuba.xades.XadesSignatureFactory;
import org.duuba.xades.XadesVersion;
import org.holodeckb2b.commons.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Is a builder to assist in the creation of a Xades baseline signature which is enveloped by the XML document that is 
 * being signed. It supports both the B-B profile as defined <i>ETSI EN 319 132 v1.1.1</i> and the BES profile defined 
 * in <i>ETSI TS 101 903 V1.4.1</i>. The main difference between these two versions is that for some of the qualifying 
 * properties EN 319 132 defines additional data or uses another representation of the same data. 
 * <p>At least the signer's private key and certificate must be provided. All other information items are optional. When
 * creating a BES level signature as defined TS 101 903, values set for not supported qualifying attributes are ignored.  
 * On successful completion of the {@link #build()} method a new Xades signature is added as last element to the given
 * XML document.     
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class BasicEnvelopedSignatureBuilder {
	
	public static final XadesVersion DEFAULT_VERSION = XadesVersion.EN_319_132_V111; 
	public static final String		 DEFAULT_C14N_ALG = CanonicalizationMethod.EXCLUSIVE;
	public static final String		 DEFAULT_DIGEST_ALG = DigestMethod.SHA256;
	public static final String		 DEFAULT_SIGNING_ALG = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
	
	// Generic signature properties
	private XadesVersion	version;
	private Document		doc2sign;
	private PrivateKey		keyForSigning;
	private X509Certificate signingCertificate;
	private List<X509Certificate>	certPath;
	private String			c14nAlg;
	private String			digestAlg;  
	private String			signingAlg; 
	
	// Qualifying properties
	private String			docDescription;
	private IObjectIdentifier docOID;
	private String			signersCountry;
	private String			signersState;
	private String			signersCity;
	private String			signersPostalCode;
	private String			signersStreet;
	private List<Element>	claimedRoles;
	private List<X509Certificate> x509certRoles;
	private List<Element> 	otherCertRoles;
	private List<Element> 	roleAssertions;
	private List<CommitmentIndication> commitments;
	
	// The built signature
	private XadesSignature	signature;
	
	/**
	 * Sets the Xades of the signature that is to be build. 
	 * <p>If the version is not set or set to <code>null</code> using this method a signature conforming to 
	 * <i>EN 319 132 V1.1.1</i> will be created. 
	 * 
	 * @param v	the Xades version
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setXadesVersion(XadesVersion v) {
		this.version = v;
		return this;
	}
	
	/**
	 * Sets the XML document that needs to be signed.
	 * 
	 * @param docToSign	the document to be signed
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setDocumentToSign(Document docToSign) {
		this.doc2sign = docToSign;
		return this;
	}
	
	/**
	 * Sets the key pair, i.e. the private key and signing certificate that should be used to sign the document. 
	 * If the key pair contains a certificate path it will automatically be included in the signature. 
	 * 
	 * @param keypair the key pair to use for signing
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder setKeyPair(KeyStore.PrivateKeyEntry keypair) {
		return setKeyPair(keypair, true);
	}
	
	/**
	 * Sets the key pair, i.e. the private key and signing certificate that should be used to sign the document. If the 
	 * key pair contains a certificate path the the second argument indicate whether the paht needs to be included in 
	 * the signature. 
	 * 
	 * @param keypair	the key pair to use for signing
	 * @param includeCertPath	indicator whether the cert path included in the key pair (if any) must be included in 
	 * 							the signature
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder setKeyPair(KeyStore.PrivateKeyEntry keypair, boolean includeCertPath) {
		this.keyForSigning = keypair.getPrivateKey();
		X509Certificate[] certificateChain;
		try {
			certificateChain = (X509Certificate[]) keypair.getCertificateChain();
		} catch (ClassCastException notX509) {
			throw new IllegalArgumentException("Key pair must contain X509 Certificate(s)");
		}
		if (certificateChain != null) {
			signingCertificate = certificateChain[0];
			if (certificateChain.length > 1 && includeCertPath) {
				certPath = new ArrayList<X509Certificate>(certificateChain.length - 1);
				for (int i = 1; i < certificateChain.length; i++)
					certPath.add(certificateChain[i]);
			}
		}
		return this;
	}
	
	/**
	 * Sets the private key that should be used for signing the document. 
	 * 
	 * @param key	the key to use for signing
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setPrivateKey(PrivateKey key) {
		this.keyForSigning = key;
		return this;
	}
	
	/**
	 * Sets the signing certificate that should be included in the signature. 
	 * 
	 * @param key	the signing certificate
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setSigningCertificate(X509Certificate cert) {
		this.signingCertificate = cert;
		return this;
	}
	
	/**
	 * Sets the certificate path that should be included in the signature. The path should only contain the certificates
	 * of the certificate authorities and MUST NOT include the leaf certificate used for signing. 
	 * 
	 * @param key	the certificate path
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setCertificatePath(List<X509Certificate> certPath) {
		this.certPath = certPath;
		return this;
	}
	
	/**
	 * Sets the canonicalisation algorithm. The algorithm must be specified as the URI defined in the XML-DSIG 
	 * specification. 
	 * <p>If the algorithm is not set, or set to <code>null</code> or an empty string using this method the default 
	 * exclusive without comments algorithm will be used. 
	 * 
	 * @param algorithm		canonicalisation algorithm to use
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder setC14nAlgorithm(String algorithm) {
		this.c14nAlg = algorithm;
		return this;
	}
	
	/**
	 * Sets the digest algorithm. The algorithm must be specified as the URI defined in the XML-DSIG specification. 
	 * <p>If the algorithm is not set, or set to <code>null</code> or an empty string using this method the default 
	 * SHA256 algorithm will be used. 
	 * 
	 * @param algorithm		digest algorithm to use
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder setDigestAlgorithm(String algorithm) {
		this.digestAlg = algorithm;
		return this;
	}

	/**
	 * Sets the signing algorithm. The algorithm must be specified as the URI defined in the XML-DSIG specification. 
	 * <p>If the algorithm is not set, or set to <code>null</code> or an empty string using this method the default 
	 * RSA-SHA256 algorithm will be used. 
	 * 
	 * @param algorithm		signing algorithm to use
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder setSigningAlgorithm(String algorithm) {
		this.signingAlg = algorithm;
		return this;
	}
	
	/**
	 * Sets the signed document's description which will be contained in the <code>DataObjectFormat</code> qualifying
	 * property of the signature.
	 * 
	 * @param description	signed document's description
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setSignedDocumentDescription(String description) {
		this.docDescription = description;
		return this;
	}
	
	/**
	 * Sets the object identifier for the document. 
	 * 
	 * @param oid	the object identifier
	 * @return this builder  
	 */
	public BasicEnvelopedSignatureBuilder setSignedDocumentOID(IObjectIdentifier oid) {
		this.docOID = oid;
		return this;
	}
	
	/**
	 * Sets the information on the location of signer. The data will be include in the <code>SignatureProductionPlace
	 * </code> qualifying property of the signature.
	 * <p>NOTE: the street attribute of the location is only supported for Xades signatures conforming to 
	 * <i>EN 319 132</i>. When the street is provided for a <i>TS 101 903</i> signature is will be ignored.  
	 * 
	 * @param street			street of signer's location
	 * @param postalCode		postal code of signer's location
	 * @param city				city of signer's location
	 * @param stateOrProvince   state or province of signer's location
	 * @param country			country of signer's location
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setSignersLocation(String street, String postalCode, String city, 
															 String stateOrProvince, String country) {
		this.signersStreet = street;
		this.signersPostalCode = postalCode;
		this.signersCity = city;
		this.signersState = stateOrProvince;
		this.signersCountry = country;
		return this;
	}
	
	/**
	 * Sets the roles claimed by the signer. The structure of a claimed role is not defined by the Xades specification
	 * other then that it must be an XML element. Therefore it's the caller's responsibility to ensure that the provided
	 * elements conform to the specification as agreed in the domain. 
	 * 
	 * @param roles 	a list of XML element representing the claimed roles. Note that only the element content is used 
	 * 					and the element name is ignored
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setClaimedRoles(List<Element> roles) {
		this.claimedRoles = roles;
		return this;
	}
	
	/**
	 * Sets the certified roles of the signer as proven by the given X509 certificates. 
	 * <p>ETSI standard <i>EN 319 132</i> also allows other kind of certificates to certify the signer's role. These
	 * can be set using the {@link #setOtherCertifiedRoles(List)}. Both methods can be used and the builder will ensure 
	 * the correct signature components are created.
	 *      
	 * @param x509Certs	list of X509 certificates about the signer's role
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder setX509CertifiedRoles(List<X509Certificate> x509Certs) {
		this.x509certRoles = x509Certs;
		return this;
	}
	
	/**
	 * Sets the certified roles of the signer as proven by the given non X509 certificates. The structure of these
	 * certificates is not defined by the Xades specification other then that it must be an XML element. Therefore it's
	 * the caller's responsibility to ensure that the provided elements conform to the specification as agreed in the 
	 * domain.
	 * <p>NOTE: This option to include information on the signer's role is only available for signatures conforming to 
	 * <i>EN 319 132</i>. When calling this method when building a <i>TS 101 903</i> signature the information will be 
	 * ignored.<br>  
	 * It is also possible to use X509 certificate to certify the signer's role. These can be set using the {@link 
	 * #setX509CertifiedRoles(List)}. Both methods can be used and the builder will ensure the correct signature 
	 * components are created.
	 *      
	 * @param otherRoleCerts	list of elements containing the information about the signer's role. Note that only the 
	 * 							element content is used and the element name is ignored
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder setOtherCertifiedRoles(List<Element> otherRoleCerts) {
		this.otherCertRoles = otherRoleCerts;
		return this;
	}
	
	/**
	 * Sets the assertions on the roles of the signer. The structure of these assertions is not defined by the Xades 
	 * specification other then that it must be an XML element. Therefore it's the caller's responsibility to ensure 
	 * that the provided elements conform to the specification as agreed in the domain.
	 * <p>NOTE: This option to include information on the signer's role is only available for signatures conforming to 
	 * <i>EN 319 132</i>. When calling this method when building a <i>TS 101 903</i> signature the information will be 
	 * ignored. 
	 * 
	 * @param assertions	list of elements containing the assertions on the signer's role. Note that only the element
	 * 					    content is used and the element name is ignored
	 * @return	this builder
	 */
	public BasicEnvelopedSignatureBuilder setRoleAssertions(List<Element> assertions) {
		this.roleAssertions = assertions;
		return this;
	}
	
	/**
	 * Adds a commitment indication.  
	 * 
	 * @param commitmentOID		the object identifier of the commitment
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder addCommitmentIndication(IObjectIdentifier commitmentOID) {
		return this.addCommitmentIndication(commitmentOID, null);
	}
	
	/**
	 * Adds a commitment indication. This may include a set of qualifiers which are not specified by the Xades 
	 * specification other then that it must be XML elements. Therefore it's the caller's responsibility to ensure 
	 * that the provided elements conform to the specification as agreed in the domain.
	 * 
	 * @param commitmentOID		the object identifier of the commitment
	 * @param commitmentQualifiers list of XML elements containing the qualifiers. Note that only the element content is 
	 * 							   used	and the element name is ignored
	 * @return this builder
	 */
	public BasicEnvelopedSignatureBuilder addCommitmentIndication(IObjectIdentifier commitmentOID, 
																  List<Element> commitmentQualifiers) {
		if (commitments == null)
			commitments = new ArrayList<>();
		commitments.add(new CommitmentIndication(commitmentOID, commitmentQualifiers));
		return this;
	}
	
	/**
	 * Creates a new Xades signature based on the provided input. The build process will be executed only once. When 
	 * called repeatedly the already built signature will be returned and the document will be unchanged. 
	 * 
	 * @return the signed Xades signature
	 * @throws IllegalStateException when the builder is in a state that it is not possible to build a valid Xades 
	 * 								 signature. Probably caused by missing or conflicting values.    
	 * @throws XMLSignatureException 
	 */
	public XadesSignature build() throws IllegalStateException, XMLSignatureException {
		if (signature != null)
			return signature;
		
		checkInput();
		
		// Create the Xades signature
		try {
			final XadesSignatureFactory xadesFactory = new XadesSignatureFactory(version);
			final XMLSignatureFactory xmldsigFactory = xadesFactory.getXMLSignatureFactory(); 
			
			// Create Id values for the main ds:Reference and ds:Signature as these are needed by the qualifying 
			// properties
			final String docRefId = "DR-" + UUID.randomUUID().toString();
			final String signatureId = "xadessig-" + UUID.randomUUID().toString();
			// Create a Reference to the enveloped document 
			Reference docRef = xmldsigFactory.newReference("", 
									xmldsigFactory.newDigestMethod(digestAlg, null),
									Arrays.asList(new Transform[] { 
										xmldsigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
										xmldsigFactory.newTransform(c14nAlg, (TransformParameterSpec) null)
		  							}),
									null, docRefId);
			
			// Create the KeyInfo containing the X509Data
			final List<X509Certificate> certs = new ArrayList<>(certPath != null ? certPath.size() + 1 : 1);
			certs.add(signingCertificate);
			if (certPath != null)
				certs.addAll(certPath);
			final KeyInfo ki = xadesFactory.newKeyInfo(certs);
							
			// Create the qualifying properties, starting with the one applying to the Signature
			final SigningCertificate certInfo = xadesFactory.newSigningCertificate(certs, digestAlg);
			
			SignatureProductionPlace signersLocation = null;
			if (!Utils.isNullOrEmpty(signersStreet)
			 || !Utils.isNullOrEmpty(signersPostalCode)
			 || !Utils.isNullOrEmpty(signersCity)
			 || !Utils.isNullOrEmpty(signersState)
			 || !Utils.isNullOrEmpty(signersCountry))
				signersLocation = xadesFactory.newSignatureProductionPlace(signersCity, signersStreet, 
																		   signersPostalCode, signersState, 
																		   signersCountry);			
			
			List<ClaimedRole> claimed = null;
			if (claimedRoles != null) {
				claimed = new ArrayList<>();
				for (Element r : claimedRoles) 
					claimed.add(xadesFactory.newClaimedRole(getElementNodes(r)));
			}
			List<CertifiedRole> certifiedRoles = null;
			if (x509certRoles != null) {
				certifiedRoles = new ArrayList<>();
				for (X509Certificate c : x509certRoles)
					certifiedRoles.add(xadesFactory.newCertifiedRole(null, c));
			}
			if (otherCertRoles != null) {
				if (certifiedRoles == null)
					certifiedRoles = new ArrayList<>();
				for (Element e : otherCertRoles)
					certifiedRoles.add(xadesFactory.newCertifiedRole(getElementNodes(e)));
			}
			List<SignedAssertion> assertions = null;
			if (version == XadesVersion.EN_319_132_V111 && roleAssertions != null) {
				assertions = new ArrayList<>();
				for (Element e : roleAssertions)
					assertions.add(xadesFactory.newSignedAssertion(getElementNodes(e)));
			}
			final SignerRole signerRole = (claimed != null || certifiedRoles != null || assertions != null) ?	
												xadesFactory.newSignerRole(claimed, certifiedRoles, assertions) : null;
			
			final SignedSignatureProperties sigProps = xadesFactory.newSignedSignatureProperties(
																			ZonedDateTime.now(ZoneOffset.UTC), 
																			certInfo,
																			null, signersLocation,
																			signerRole, null);			

			// Create the qualifying properties related to the signed document
			final DataObjectFormat dataFormat = xadesFactory.newDataObjectFormat("#" + docRefId, docDescription, docOID, 
																				"text/xml", null);
			List<CommitmentTypeIndication> commitmentIndications = null; 
			if (commitments != null) {
				commitmentIndications = new ArrayList<>(commitments.size());
				for (CommitmentIndication c : commitments) {
					List<CommitmentTypeQualifier> qualifiers = null;
					if (c.qualifiers != null) {
						qualifiers = new ArrayList<>(c.qualifiers.size());
						for (Element e : c.qualifiers)
							qualifiers.add(xadesFactory.newCommitmentTypeQualifier(getElementNodes(e)));					
					}
					commitmentIndications.add(xadesFactory.newCommitmentTypeIndication(c.oid, qualifiers));
				}
			}
			final SignedDataObjectProperties dataProps = xadesFactory.newSignedDataObjectProperties(
																			Collections.singletonList(dataFormat),
																			commitmentIndications);
			
			// Create the SignedProperties element
			final String signedPropsId = "SP-" + UUID.randomUUID().toString();
			final SignedProperties signedProps = xadesFactory.newSignedProperties(signedPropsId, sigProps, dataProps);
			
			// As there are no unsigned properties, we can now create the QualifyingProperties element
			final QualifyingProperties qProps = xadesFactory.newQualifyingProperties("#" + signatureId, signedProps, null);
			
			signature = xadesFactory.newXadesSignature(signatureId, digestAlg, signingAlg, c14nAlg, ki, 
													   Collections.singletonList(docRef), qProps, null);
		} catch (CertificateEncodingException cee) {
			throw new IllegalStateException("A configured X509 certificate could not be DER encoded");
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ae) {		
			// These exceptions indicate that there was an issue with one of the specified algorithms
			throw new IllegalStateException("A specified algorithm was invalid");
		}

		// And finally sign it
		final DOMSignContext dsc = new DOMSignContext(keyForSigning, doc2sign.getDocumentElement());		
		try {
			signature.sign(dsc);		
		} catch (Exception signingFailure) {
			signature = null;
			throw new XMLSignatureException("The sign operation on the Xades signature failed", signingFailure);
		}
		
		return signature;
	}
	
	/**
	 * Checks that all information needed to create a Xades signature is available. Also sets default values for the
	 * Xades version and algorithms to use.   
	 * 
	 * @throws IllegalStateException if the data provided to create the signature is not valid 
	 */
	protected void checkInput() throws IllegalStateException {
		// Set defaults if no values specified
		if (version == null)
			version = DEFAULT_VERSION;
		if (!Utils.isNullOrEmpty(c14nAlg))
			c14nAlg = DEFAULT_C14N_ALG;
		if (!Utils.isNullOrEmpty(digestAlg))
			digestAlg = DEFAULT_DIGEST_ALG;
		if (!Utils.isNullOrEmpty(signingAlg))
			signingAlg = DEFAULT_SIGNING_ALG;
		
		// Check required settings
		if (doc2sign == null)
			throw new IllegalStateException("No document to sign set");
		if (keyForSigning == null)
			throw new IllegalStateException("Private key not set");
		if (signingCertificate == null)
			throw new IllegalStateException("Signing certificate not set");
		
		if (docOID != null && !Utils.isNullOrEmpty(docOID.getIdentifier()))
			throw new IllegalStateException("The document OID must have an identifier value");
		
		if (commitments != null)
			for (CommitmentIndication c : commitments) 
				if (c.oid == null || c.oid.getIdentifier() == null || c.oid.getIdentifier().isEmpty())
					throw new IllegalStateException("A commitment indication must have an identifier value");				
		
		if (version == XadesVersion.TS_101_903_V141 && roleAssertions != null)
			throw new IllegalStateException("Role assertions are not allowed in TS 101 903 signatures");
		if (version == XadesVersion.TS_101_903_V141 && otherCertRoles != null)
			throw new IllegalStateException("Non X509 certified roles are not allowed in TS 101 903 signatures");
		
		if (claimedRoles != null)
			for (Element e : claimedRoles)
				if (e == null || e.getChildNodes().getLength() == 0)
					throw new IllegalStateException("A ClaimedRole role element must have content");
		if (otherCertRoles != null)
			for (Element e : otherCertRoles)
				if (e == null || e.getChildNodes().getLength() == 0)
					throw new IllegalStateException("A \"Other\" type certified role element must have content");
		if (roleAssertions != null)
			for (Element e : roleAssertions)
				if (e == null || e.getChildNodes().getLength() == 0)
					throw new IllegalStateException("A SignedAssertion element must have content");
			
	}
	
	/**
	 * Gets the content of an {@link Element} object as a list of {@list Node}s.
	 * 
	 * @param e element to get content of
	 * @return	list of nodes contained in the element, 
	 */
	protected List<Node> getElementNodes(Element e) {
		if (e == null)
			return null;
		
		final NodeList elContent = e.getChildNodes();
		List<Node> list = new ArrayList<>(elContent.getLength());
		for (int i = 0; i < elContent.getLength(); i++)
			list.add(elContent.item(i));
		
		return list;
	}
	
	class CommitmentIndication {
		private IObjectIdentifier oid;
		private List<Element> 	  qualifiers;
		
		public CommitmentIndication(IObjectIdentifier oid, List<Element> qualifiers) {
			this.oid = oid;
			this.qualifiers = qualifiers;
		}
	}
}

