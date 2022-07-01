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
import org.holodeckb2b.commons.util.Utils;

/**
 * A representation of the <code>SignerRole</code> element as defined in the <i>ETSI TS 101 903 V1.4.1</i>  standard. 
 * The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SignerRole" type="SignerRoleType"/&gt;
 * &lt;xsd:complexType name="SignerRoleType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="ClaimedRoles" type="ClaimedRolesListType" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="CertifiedRoles" type="CertifiedRolesListType" minOccurs="0"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:complexType name="ClaimedRolesListType"&gt; 
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="ClaimedRole" type="AnyType" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * &lt;xsd:complexType name="CertifiedRolesListType"&gt; 
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="CertifiedRole" type="EncapsulatedPKIDataType" maxOccurs="unbounded"/&gt;
 *	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SignerRole</code> instance may be created by invoking one of the {@link 
 * XadesSignatureFactory#newSignerRole} methods on a factory instance configured for Xades version {@link 
 * XadesVersion#TS_101_903_V141}.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class SignerRole extends XadesElement {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignerRole", 
															Constants.XADES_132_NS_PREFIX);
	
	protected List<ClaimedRole>		claimed;
	protected List<CertifiedRole>	certified;
	
	SignerRole(final List<ClaimedRole> claimedRoles, final List<CertifiedRole> certifiedRoles) {
		this.claimed = claimedRoles;
		this.certified = certifiedRoles;
	}
	
	/**
	 * @return	roles claimed by the signer
	 */
	public List<ClaimedRole> getClaimedRoles() {
		return claimed;
	}
	
	/**
	 * @return	certified roles of the signer
	 */
	public List<CertifiedRole> getCertifiedRoles() {
		return certified;
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
		
		SignerRole other = (SignerRole) o;		
		return Utils.areEqual(this.claimed, other.claimed) && Utils.areEqual(this.certified, other.certified);
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
		if (certified != null && !certified.isEmpty()) {
			xwriter.writeStartElement(nsPrefix, "CertifiedRoles", Constants.XADES_132_NS_URI);
			for (CertifiedRole r : certified)
				((CertifiedRoleV1) r).marshal(xwriter, dsPrefix, context);
			xwriter.writeEndElement();
		}
		
	}

}
