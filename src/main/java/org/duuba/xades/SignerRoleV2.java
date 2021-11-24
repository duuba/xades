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

import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;

/**
 * A representation of the <code>SignerRoleV2</code> element as defined in the <i>ETSI EN 319 132 V1.1.1</i>  standard. 
 * The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SignerRoleV2" type="SignerRoleV2Type"/&gt;
 * &lt;xsd:complexType name="SignerRoleV2Type"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element ref="ClaimedRoles" minOccurs="0"/&gt;
 * 		&lt;xsd:element ref="CertifiedRolesV2" minOccurs="0"/&gt;
 * 		&lt;xsd:element ref="SignedAssertions" minOccurs="0"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:element name="ClaimedRoles" type="ClaimedRolesListType"/&gt;
 * &lt;xsd:element name="CertifiedRolesV2" type="CertifiedRolesListTypeV2"/&gt;
 * &lt;xsd:element name="SignedAssertions" type="SignedAssertionsListType"/&gt;
 * &lt;xsd:complexType name="SignedAssertionsListType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element ref="SignedAssertion" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SignerRoleV2</code> instance may be created by invoking one of the {@link 
 * XadesSignatureFactory#newSignerRole} methods on a factory instance configured for Xades version {@link 
 * XadesVersion#EN_319_132_V111}.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SignerRoleV2 extends SignerRole {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignerRole", 
															Constants.XADES_132_NS_PREFIX);
	
	protected List<SignedAssertion>	assertions;
	
	SignerRoleV2(final List<ClaimedRole> claimedRoles, final List<CertifiedRole> certifiedRoles, 
				 final List<SignedAssertion> assertions) {
		super(claimedRoles, certifiedRoles);
		this.assertions = assertions;
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
									throws MarshalException {
		
		// write ClaimedRoles element
		if (claimed != null && !claimed.isEmpty()) {
			xwriter.writeStartElement(nsPrefix, "ClaimedRoles", Constants.XADES_132_NS_URI);
			for (ClaimedRole r : claimed)
				r.marshal(xwriter, dsPrefix, context);
			xwriter.writeEndElement();
		}
		// write CertifiedRoles element
		if (claimed != null && !claimed.isEmpty()) {
			xwriter.writeStartElement(nsPrefix, "CertifiedRolesV2", Constants.XADES_132_NS_URI);
			for (CertifiedRole r : certified)
				((CertifiedRoleV2) r).marshal(xwriter, dsPrefix, context);
			xwriter.writeEndElement();
		}
		// write SignedAssertions element
		if (assertions != null && !assertions.isEmpty()) {
			xwriter.writeStartElement(nsPrefix, "SignedAssertions", Constants.XADES_132_NS_URI);
			for (SignedAssertion a : assertions)
				a.marshal(xwriter, dsPrefix, context);
			xwriter.writeEndElement();
		}		
	}
}
