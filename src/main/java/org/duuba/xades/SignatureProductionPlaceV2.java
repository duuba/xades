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

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.holodeckb2b.commons.util.Utils;

/**
 * A representation of the <code>SignatureProductionPlaceV2</code> element as defined in the <i>ETSI EN 319 132 V1.1.1</i> 
 * standard. The XML schema is defined as:
 * <code>
 * &lt;xsd:element name="SignatureProductionPlaceV2" type="SignatureProductionPlaceType"/&gt;
 * &lt;xsd:complexType name="SignatureProductionPlaceV2Type"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="City" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="StreetAddress" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="StateOrProvince" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="PostalCode" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="CountryName" type="xsd:string" minOccurs="0"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </code> 
 * 
 * <p>A <code>SignatureProductionPlaceV2</code> instance may be created by invoking one of the {@link 
 * XadesSignatureFactory#newSignatureProductionPlace} methods on a factory instance configured for Xades version 
 * {@link XadesVersion#EN_319_132_V111}.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class SignatureProductionPlaceV2 extends SignatureProductionPlace {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignatureProductionPlaceV2", 
																	Constants.XADES_132_NS_PREFIX);
	private String street;
	
	SignatureProductionPlaceV2(final String city, final String streetAddress, final String stateOrProvince, 
							   final String postalCode, final String countryName) {
		super(city, stateOrProvince, postalCode, countryName);
		this.street = streetAddress; 
	}
		
	/**
	 * @return	the street address of the signature's production place
	 */
	public String getStreetAddress() {
		return street;
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
		
		return Utils.nullSafeEqual(this.street, ((SignatureProductionPlaceV2) o).street);
	}
	
	@Override
	protected QName getName() {
		return 	ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
		if(city != null && !city.isEmpty())
			xwriter.writeTextElement(nsPrefix, "City", Constants.XADES_132_NS_URI, city);
		if(street != null && !street.isEmpty())
			xwriter.writeTextElement(nsPrefix, "StreetAddress", Constants.XADES_132_NS_URI, street);
		if(state != null && !state.isEmpty())
			xwriter.writeTextElement(nsPrefix, "StateOrProvince", Constants.XADES_132_NS_URI, state);
		if(postalCode != null && !postalCode.isEmpty())
			xwriter.writeTextElement(nsPrefix, "PostalCode", Constants.XADES_132_NS_URI, postalCode);
		if(country != null && !country.isEmpty())
			xwriter.writeTextElement(nsPrefix, "CountryName", Constants.XADES_132_NS_URI, country);
				
	}
}
