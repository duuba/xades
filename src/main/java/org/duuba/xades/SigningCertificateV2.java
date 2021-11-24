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
import java.util.List;

import javax.xml.namespace.QName;

/**
 * A representation of the <code>SigningCertificateV2</code> element as defined in the <i>ETSI EN 319 132 V1.1.1</i> 
 * standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SigningCertificateV2" type="CertIDListType"/&gt;
 * &lt;xsd:complexType name="CertIDListTypeV2"&gt; 
 *	&lt;xsd:sequence&gt;
 *		&lt;xsd:element name="Cert" type="CertIDTypeV2" maxOccurs="unbounded"/&gt;
 *   &lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SigningCertificate</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newSigningCertificate} methods on a factory instance configured for Xades version {@link 
 * XadesVersion#EN_319_132_V111}.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SigningCertificateV2 extends SigningCertificate {
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SigningCertificateV2", 
			Constants.XADES_132_NS_PREFIX);

	private static final QName CERT_ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "Cert");

	
	SigningCertificateV2(final List<X509Certificate> certs, String digestMethod) throws NoSuchAlgorithmException {
		super(certs, digestMethod);
	}
	
	/**
	 * Creates a new <code>Cert</code> element that should be added to this <code>SigningCertificateV2</code> element.
	 *  
	 * @param c				the certificate which information should be captured in the new element
	 * @param digestMethod	digest method to use for creating hash of the certificate
	 * @return	a new <code>Cert</code> element
	 * @throws CertificateEncodingException	when the certificate information could not be encoded as required 
	 * @throws NoSuchAlgorithmException		when the specified digest algorithm is not available
	 */
	@Override
	protected AbstractCertIDTypeElement createCertElement(X509Certificate c, String digestMethod) 
														throws CertificateEncodingException, NoSuchAlgorithmException {
		return new Cert(c, digestMethod);
	}	
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	/**
	 * A representation of the <code>Cert</code> child element. 
	 */
	public class Cert extends AbstractCertIDTypeV2Element {
		
		Cert(X509Certificate cert, String digestMethod) throws CertificateEncodingException, NoSuchAlgorithmException {
			super(cert, digestMethod);			
		}

		@Override
		protected QName getName() {
			return CERT_ELEMENT_NAME;
		}
	}	
}
