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

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.xml.namespace.QName;

/**
 * A representation of the <code>CertifiedRole</code> element part of <code>SignerRole</code>as defined in the <i>ETSI 
 * TS 101 903 V1.4.1</i>  standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:complexType name="CertifiedRolesListType"&gt;
 *	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="CertifiedRole" type="EncapsulatedPKIDataType" maxOccurs="unbounded"/&gt;
 *	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code>
 *  
 * <p>A <code>CertifiedRoleV1</code> instance may be created by invoking one of the {@link 
 * XadesSignatureFactory#newCertifiedRole} methods on a factory instance configured for Xades version {@link 
 * XadesVersion#TS_101_903_V141}.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class CertifiedRoleV1 extends AbstractEncapsulatedPKIDataTypeElement implements CertifiedRole {
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "CertifiedRole", 
														Constants.XADES_132_NS_PREFIX);
	
	private X509Certificate attrCert;
	
	CertifiedRoleV1(String id, X509Certificate cert) throws CertificateEncodingException {
		super(id, cert.getEncoded(), Encoding.DER);
		this.attrCert = cert;
	}

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

	@Override
	public X509Certificate getX509AttributeCertificate() {
		return this.attrCert;
	}
}
