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

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;

/**
 * A representation of the <code>SigningCertificate</code> element as defined in the <i>ETSI TS 101 903 V1.4.1</i> 
 * standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SigningCertificate" type="CertIDListType"/&gt;
 * &lt;xsd:complexType name="CertIDListType"&gt; 
 *	&lt;xsd:sequence&gt;
 *		&lt;xsd:element name="Cert" type="CertIDType" maxOccurs="unbounded"/&gt;
 *   &lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SigningCertificate</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newSigningCertificate} methods on a factory instance configured for Xades version {@link 
 * XadesVersion#TS_101_903_V141}.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SigningCertificate extends XadesElement {
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SigningCertificate", 
			Constants.XADES_132_NS_PREFIX);

	private static final QName CERT_ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "Cert");


	private List<AbstractCertIDTypeElement>		certInfo;
	
	SigningCertificate(final List<X509Certificate> certs, String digestMethod) throws NoSuchAlgorithmException {
		if (certs.isEmpty())
			throw new IllegalArgumentException("At least one certificate is required");
		
		certInfo = new ArrayList<>(certs.size());
		try {
			for (X509Certificate c : certs)
				certInfo.add(createCertElement(c, digestMethod));
		} catch (CertificateEncodingException invalidCert) {
			throw new IllegalArgumentException("Invalid certificate supplied", invalidCert);
		}		
	}
	
	/**
	 * Creates a new <code>Cert</code> element that should be added to this <code>SigningCertificate</code> element.
	 *  
	 * @param c				the certificate which information should be captured in the new element
	 * @param digestMethod	digest method to use for creating hash of the certificate
	 * @return	a new <code>Cert</code> element
	 * @throws CertificateEncodingException	when the certificate information could not be encoded as required 
	 * @throws NoSuchAlgorithmException		when the specified digest algorithm is not available
	 */
	protected AbstractCertIDTypeElement createCertElement(X509Certificate c, String digestMethod) 
														throws CertificateEncodingException, NoSuchAlgorithmException {
		return new Cert(c, digestMethod);
	}

	/**
	 * Returns a list of certificate meta-data to assist in the validation of the signature. The list shall at least
	 * contain information on the signing certificate and may contain other certificates from CAs on the cert chain.
	 *    
	 * @return	list of certificate meta-data 
	 */
	public List<AbstractCertIDTypeElement> getCertificates() {
		return certInfo;
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {

		// create and append the Cert elements
		for(AbstractCertIDTypeElement c : certInfo)
			c.marshal(xwriter, dsPrefix, context);
	}
	
	/**
	 * A representation of the <code>Cert</code> child element. 
	 */
	public class Cert extends AbstractCertIDTypeElement {
		
		Cert(X509Certificate cert, String digestMethod) throws CertificateEncodingException, NoSuchAlgorithmException {
			super(cert, digestMethod);			
		}

		@Override
		protected QName getName() {
			return CERT_ELEMENT_NAME;
		}
	}	
}
