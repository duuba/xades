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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.apache.xml.security.algorithms.JCEMapper;

/**
 * Is a base class for the representation of elements that are of type <code>CertIDType</code> as defined in <i>ETSI TS 
 * 101 903 V1.4.1</i>.
 * <p>The type is defined in the XML schema as:
 * <code>
 * &lt;xsd:complexType name="CertIDType"&gt; 
 * 	&lt;xsd:sequence&gt;
 *		&lt;xsd:element name="CertDigest" type="DigestAlgAndValueType"/&gt;
 *		&lt;xsd:element name="IssuerSerial" type="ds:X509IssuerSerialType"/&gt;
 *	&lt;/xsd:sequence&gt;
 *	&lt;xsd:attribute name="URI" type="xsd:anyURI" use="optional"/&gt; 
 * &lt;/xsd:complexType&gt;
 * 
 * &lt;complexType name="X509IssuerSerialType"&gt; 
 *   &lt;sequence&gt;
 *       &lt;element name="X509IssuerName" type="string"/&gt;
 *       &lt;element name="X509SerialNumber" type="integer"/&gt;
 *   &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </code> 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public abstract class AbstractCertIDTypeElement extends XadesElement {
	
	protected CertDigest	certDigest;
	protected IssuerSerial 	issuerSerial;
	
	AbstractCertIDTypeElement(X509Certificate cert, String digestMethod) throws CertificateEncodingException, 
																		NoSuchAlgorithmException {
		final String algId = JCEMapper.translateURItoJCEID(digestMethod);
		if (algId == null)
			throw new NoSuchAlgorithmException();
		this.certDigest = new CertDigest(digestMethod, MessageDigest.getInstance(algId).digest(cert.getEncoded()));
		setIssuerSerial(cert);
	}

	protected void setIssuerSerial(X509Certificate cert) throws CertificateEncodingException {
		this.issuerSerial = new IssuerSerial(cert);
	}

	/**
	 * @return the digest algorithm that is used
	 */
	public CertDigest getCertDigest() {
		return certDigest;
	}
	
	/**
	 * @return the digest value as contained in the element
	 */
	public IssuerSerial getIssuerSerial() {
		return issuerSerial;
	}

	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {		
        // create and append CertDigest element
		certDigest.marshal(xwriter, dsPrefix, context);        
        // create and append IssuerSerial element
		issuerSerial.marshal(xwriter, dsPrefix, context);
	}
	
	/**
	 * Determines whether the other object is an instance of the same class and represents the same element, i.e. has
	 * the same content.
	 * 
	 * @param o 	the other object
	 * @return 		<code>true</code> iff <code>o</code> represents the same element, i.e. has the same qualified name
	 * 				and list of child elements.
	 */
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		
		AbstractCertIDTypeElement other = (AbstractCertIDTypeElement) o;
		return this.certDigest.equals(other.certDigest) && this.issuerSerial.equals(other.issuerSerial);
	}
	
	/**
	 * A representation of the <code>CertDigest</code> element. 
	 */
	public static class CertDigest extends AbstractDigestAlgAndValueTypeElement {
		final static QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "CertDigest");
		
		CertDigest(String digestAlg, byte[] digestVal) {
			super(digestAlg, digestVal);
		}
		
		@Override
		protected QName getName() {
			return ELEMENT_NAME;
		}
	}	
	
	/**
	 * A representation of the <code>IssuerSerial</code> element. 
	 */
	public static class IssuerSerial extends XadesElement {

		private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "IssuerSerial", 
															Constants.XADES_132_NS_PREFIX);;
		
		protected String		issuerName;
		protected BigInteger	serialNo;
		
		IssuerSerial(X509Certificate cert) {
			this.issuerName = cert.getIssuerX500Principal().getName();
			this.serialNo = cert.getSerialNumber();
		}
		
		/**
		 * @return the name of the certificate issuer
		 */
		public String getIssuerName() {
			return issuerName;
		}
		
		/**
		 * @return the serial number of the certificate
		 */
		public BigInteger getSerialNo() {
			return serialNo;
		}
				
		@Override
		public boolean equals(Object o) {
			if (!super.equals(o))
				return false;
			else {
				IssuerSerial other = (IssuerSerial) o;
				return this.issuerName.equals(other.issuerName) && this.serialNo.equals(other.serialNo);
			}
		}
		
		@Override
		protected QName getName() {
			return ELEMENT_NAME;
		}
		@Override
		protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
				throws MarshalException {
			
	        xwriter.writeTextElement(dsPrefix, "X509IssuerName", XMLSignature.XMLNS, issuerName);
	        xwriter.writeTextElement(dsPrefix, "X509SerialNumber", XMLSignature.XMLNS, serialNo.toString());
	    }
	}	
}
