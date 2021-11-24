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

/**
 * A representation of the <code>SigningCertificate</code> element as defined in the <i>ETSI TS 101 903 V1.4.1</i> 
 * standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SignatureProductionPlace" type="SignatureProductionPlaceType"/&gt;
 * &lt;xsd:complexType name="SignatureProductionPlaceType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="City" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="StateOrProvince" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="PostalCode" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="CountryName" type="xsd:string" minOccurs="0"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SignatureProductionPlace</code> instance may be created by invoking one of the {@link 
 * XadesSignatureFactory#newSignatureProductionPlace} methods on a factory instance configured for Xades version 
 * {@link XadesVersion#TS_101_903_V141}.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SignatureProductionPlace extends XadesElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignatureProductionPlace", 
																			Constants.XADES_132_NS_PREFIX);
	
	protected String city;
	protected String state;
	protected String postalCode;
	protected String country;
	
	SignatureProductionPlace(final String city, final String stateOrProvince, final String postalCode, 
									final String countryName) {
		this.city = city;
		this.state = stateOrProvince;
		this.postalCode = postalCode;
		this.country = countryName;
	}
	
	/**
	 * @return	city of the signature's production place
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * @return	state or province of the signature's production place
	 */
	public String getStateOrProvince() {
		return state;
	}
	
	/**
	 * @return	postal code of the signature's production place
	 */
	public String getPostalCode() {
		return postalCode;
	}
	
	/**
	 * @return	country of the signature's production place
	 */
	public String getCountryName() {
		return country;
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;	
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
		if(city != null && !city.isEmpty())
			xwriter.writeTextElement(nsPrefix, "City", Constants.XADES_132_NS_URI, city);
		if(state != null && !state.isEmpty())
			xwriter.writeTextElement(nsPrefix, "StateOrProvince", Constants.XADES_132_NS_URI, state);
		if(postalCode != null && !postalCode.isEmpty())
			xwriter.writeTextElement(nsPrefix, "PostalCode", Constants.XADES_132_NS_URI, postalCode);
		if(country != null && !country.isEmpty())
			xwriter.writeTextElement(nsPrefix, "CountryName", Constants.XADES_132_NS_URI, country);
	}

}
