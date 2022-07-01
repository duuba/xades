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

import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalizationMethod;

/**
 * A representation of the <code>XAdESTimeStamp</code> element as defined in respectively <i>ETSI TS 101 903 
 * V1.4.1</i> and<i>ETSI EN 319 132 v1.1.1</i> standards. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="XAdESTimeStamp" type="XAdESTimeStampType"/&gt;
 * &lt;xsd:complexType name="XAdESTimeStampType"&gt;
 *    &lt;xsd:complexContent&gt;
 *       &lt;xsd:restriction base="GenericTimeStampType"&gt;
 *          &lt;xsd:sequence&gt;
 *             &lt;xsd:element ref="Include" minOccurs="0" maxOccurs="unbounded"/&gt;
 *             &lt;xsd:element ref="ds:CanonicalizationMethod" minOccurs="0"/&gt;
 *             &lt;xsd:choice maxOccurs="unbounded"&gt;
 *                &lt;xsd:element name="EncapsulatedTimeStamp" type="EncapsulatedPKIDataType"/&gt;
 *                &lt;xsd:element name="XMLTimeStamp" type="AnyType"/&gt;
 *             &lt;/xsd:choice&gt;
 *          &lt;/xsd:sequence&gt;
 *          &lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 *       &lt;/xsd:restriction&gt;
 *    &lt;/xsd:complexContent&gt;
 * </xsd:complexType>
 * </pre></code> 
 * <p>NOTE: Although this class represents the <code>XAdESTimeStamp</code> element it is also used to provide the time
 * stamp data to the factory when creating elements that contain differently named elements of the same type, such as
 * for example <code>UnsignedSignatureProperties//SigAndRefsTimeStamp</code>.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class XadesTimeStamp extends AbstractGenericTimestampTypeElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "XAdESTimeStamp", 
														Constants.XADES_132_NS_PREFIX);

	XadesTimeStamp(final String id, final List<Include> includes, final DOMCanonicalizationMethod c14nMethod,
			  		final List<EncapsulatedTimeStamp> encapsulatedTS, final List<XMLTimeStamp> xmlTS) {
		super(id, includes, null, c14nMethod, encapsulatedTS, xmlTS);
	}
	
	/*
	 * This constructor can be used by descendant classes that represent other elements of type <code>XAdESTimeStampType
	 * </code>.
	 */
	protected XadesTimeStamp(final XadesTimeStamp src) {
		this(src.getId(), src.getIncludes(), (DOMCanonicalizationMethod) src.getCanonicalizationMethod(), 
			 src.getEncapsulatedTimeStamps(), src.getXMLTimeStamps());
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

}
