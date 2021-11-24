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
 * A representation of the <code>UnsignedDataObjectProperties</code> element as defined in the <i>ETSI EN 319 132-1
 * V1.1.1</i> standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="UnsignedDataObjectProperties" type="UnsignedDataObjectPropertiesType"/&gt;
 * &lt;xsd:complexType name="UnsignedDataObjectPropertiesType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="UnsignedDataObjectProperty" type="AnyType" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * 	&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p><b>NOTE:</b> As the are currently no unsigned qualifying properties specific to the signed data objects this
 * class is just an empty placeholder and there are no factory methods defined.  
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class UnsignedDataObjectProperties extends XadesElement {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "UnsignedDataObjectProperties", 
																Constants.XADES_132_NS_PREFIX);

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		// TODO Auto-generated method stub
		
	}

}
