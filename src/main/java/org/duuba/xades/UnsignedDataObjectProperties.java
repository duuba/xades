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
import org.w3c.dom.Node;

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
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class UnsignedDataObjectProperties extends XadesElement {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "UnsignedDataObjectProperties", 
																Constants.XADES_132_NS_PREFIX);

	private String	id;
	private List<UnsignedDataObjectProperty>	properties;
	
	UnsignedDataObjectProperties(String id, List<UnsignedDataObjectProperty> props) {
		this.id = id;
		this.properties = props;
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
		
		UnsignedDataObjectProperties other = (UnsignedDataObjectProperties) o;
		return Utils.nullSafeEqual(this.id, other.id) && Utils.areEqual(this.properties, other.properties);
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
		// Write attribute
		if (!Utils.isNullOrEmpty(id))
			xwriter.writeIdAttribute("", Constants.XADES_132_NS_URI, "Id", id);         		
		
		if (!Utils.isNullOrEmpty(properties)) {
			for(UnsignedDataObjectProperty p : properties)
				p.marshal(xwriter, dsPrefix, context);
		}		
	}

	/**
	 * A representation of the <code>UnsignedDataObjectProperty</code> element.
	 */
	public static class UnsignedDataObjectProperty extends AbstractAnyTypeElement {
		
		private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "UnsignedDataObjectProperty", 
															Constants.XADES_132_NS_PREFIX);	
		
		UnsignedDataObjectProperty(List<Node> content) {
			super(content);
		}

		@Override
		protected QName getName() {			
			return ELEMENT_NAME;
		}		
	}
}
