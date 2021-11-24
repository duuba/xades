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

/**
 * Is a base class for the representation of elements that are of type <code>CertIDType</code> as defined in 
 * the <i>ETSI EN 319 132-1 V1.1.1</i> standard. The type is defined in the XML schema as:
 * <code><pre>
 * &lt;xsd:complexType name="CertIDTypeV2"&gt; 
 * 	&lt;xsd:sequence&gt;
 *		&lt;xsd:element name="CertDigest" type="DigestAlgAndValueType"/&gt;
 *		&lt;xsd:element name="IssuerSerialV2" type="xsd:base64Binary"/&gt;
 *	&lt;/xsd:sequence&gt;
 *	&lt;xsd:attribute name="URI" type="xsd:anyURI" use="optional"/&gt; 
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public abstract class AbstractCertIDTypeV2Element extends AbstractCertIDTypeElement {

	public AbstractCertIDTypeV2Element(X509Certificate cert, String digestMethod)
			throws CertificateEncodingException, NoSuchAlgorithmException {
		super(cert, digestMethod);
	}

	@Override
	protected void setIssuerSerial(X509Certificate cert) throws CertificateEncodingException {
		this.issuerSerial = new IssuerSerialV2(cert);
	}
}
