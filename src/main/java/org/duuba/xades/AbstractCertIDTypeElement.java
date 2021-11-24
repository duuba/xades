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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.apache.xml.security.algorithms.JCEMapper;

/**
 * Is a base class for the representation of elements that are of type <code>CertIDType</code> as defined in 
 * the <i>ETSI TS 101 903 V1.4.1</i> standard. The type is defined in the XML schema as:
 * <code><pre>
 * &lt;xsd:complexType name="CertIDType"&gt; 
 * 	&lt;xsd:sequence&gt;
 *		&lt;xsd:element name="CertDigest" type="DigestAlgAndValueType"/&gt;
 *		&lt;xsd:element name="IssuerSerial" type="ds:X509IssuerSerialType"/&gt;
 *	&lt;/xsd:sequence&gt;
 *	&lt;xsd:attribute name="URI" type="xsd:anyURI" use="optional"/&gt; 
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public abstract class AbstractCertIDTypeElement extends XadesElement {
	
	protected CertDigest	 certDigest;
	protected IssuerSerial issuerSerial;
	
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
}
