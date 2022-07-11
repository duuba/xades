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
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.holodeckb2b.commons.util.Utils;
import org.w3c.dom.Node;

/**
 * A representation of the <code>CertifiedRole</code> element part of <code>SignerRoleV2</code>as defined in the 
 * <i>ETSI EN 319 132 V1.1.1</i> standard. The XML schema is defined as:
 * <code>
 * &lt;xsd:complexType name="CertifiedRolesListTypeV2"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="CertifiedRole" type="CertifiedRoleTypeV2" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:complexType name="CertifiedRoleTypeV2"&gt;
 * 	&lt;xsd:choice&gt;
 * 		&lt;xsd:element ref="X509AttributeCertificate"/&gt;
 * 		&lt;xsd:element ref="OtherAttributeCertificate"/&gt;
 * 	&lt;/xsd:choice&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:element name="X509AttributeCertificate" type="EncapsulatedPKIDataType"/&gt;
 * &lt;xsd:element name="OtherAttributeCertificate" type="AnyType"/&gt;	
 * </code>
 *  
 * <p>A <code>CertifiedRoleV2</code> instance may be created by invoking one of the {@link 
 * XadesSignatureFactory#newCertifiedRole} methods on a factory instance configured for Xades version {@link 
 * XadesVersion#EN_319_132_V111}.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class CertifiedRoleV2 extends XadesElement implements CertifiedRole {
	private final static QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "CertifiedRole");
	private static final QName X509_DATA_ELEMENT = new QName(Constants.XADES_132_NS_URI, "X509AttributeCertificate",
																Constants.XADES_132_NS_PREFIX);
	private static final QName OTHER_DATA_ELEMENT = new QName(Constants.XADES_132_NS_URI, "OtherAttributeCertificate",
																Constants.XADES_132_NS_PREFIX);

	private X509AttributeCertificate	x509Cert;
	private OtherAttributeCertificate	otherCert;
	
	CertifiedRoleV2(final String id, final X509Certificate attrCert) throws CertificateEncodingException {
		this.x509Cert = new X509AttributeCertificate(id, attrCert);
	}
	
	CertifiedRoleV2(List<Node> otherData) {
		this.otherCert = new OtherAttributeCertificate(otherData);
	}
	
	@Override
	public X509Certificate getX509AttributeCertificate() {
		return x509Cert.attrCert;
	}
	
	public AbstractAnyTypeElement getOtherAttributeCertificate() {
		return otherCert;
	}	

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
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
		
		CertifiedRoleV2 other = (CertifiedRoleV2) o;
		return Utils.nullSafeEqual(this.x509Cert, other.x509Cert) 
				&& Utils.nullSafeEqual(this.otherCert, other.otherCert);
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
							throws MarshalException {

		// write either the X509AttributeCertificate or OtherAttributeCertificate element depending on provided data
		if (x509Cert != null)
			x509Cert.marshal(xwriter, dsPrefix, context);
		if (otherCert != null)
			otherCert.marshal(xwriter, dsPrefix, context);
	}

	/**
	 * A representation of the <code>X509AttributeCertificate</code> element. 
	 */
	public class X509AttributeCertificate extends AbstractEncapsulatedPKIDataTypeElement {
		private X509Certificate attrCert;
		
		X509AttributeCertificate(String id, X509Certificate cert) throws CertificateEncodingException {			
			super(id, cert.getEncoded(), Encoding.DER);
			this.attrCert = cert;
		}

		@Override
		protected QName getName() {
			return X509_DATA_ELEMENT;
		}
	}
	
	/**
	 * A representation of the <code>OtherAttributeCertificate</code> element. 
	 */
	public class OtherAttributeCertificate extends AbstractAnyTypeElement {

		OtherAttributeCertificate(List<Node> content) {
			super(content);
		}

		@Override
		protected QName getName() {
			return OTHER_DATA_ELEMENT;
		}
		
	}
}
