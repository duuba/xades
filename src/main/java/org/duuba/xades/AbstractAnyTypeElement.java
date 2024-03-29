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

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.holodeckb2b.commons.util.Utils;
import org.w3c.dom.Node;

/**
 * Is a base class for the representation of elements that are of type <code>AnyType</code> as defined in respectively 
 * <i>ETSI TS 101 903 V1.4.1</i> and<i>ETSI EN 319 132 v1.1.1</i> standards. The type is defined in the XML schema as:
 * <code>
 * &lt;xsd:complexType name="AnyType" mixed="true"&gt; 
 * 	&lt;xsd:sequence minOccurs="0" maxOccurs="unbounded"&gt; 
 * 		&lt;xsd:any namespace="##any" processContents="lax"/&gt; 
 * 	&lt;/xsd:sequence&gt; 
 * 	&lt;xsd:anyAttribute namespace="##any"/&gt; 
 * &lt;/xsd:complexType&gt; 
 * </code> 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public abstract class AbstractAnyTypeElement extends XadesElement {
	
	private List<Node> 	content;
	
	AbstractAnyTypeElement(final List<Node> content) {
		this.content = content;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context) 
																							throws MarshalException {
		if (!Utils.isNullOrEmpty(content))
			for (Node n : content) 
				xwriter.marshalStructure(new javax.xml.crypto.dom.DOMStructure(n), dsPrefix, context);		
	}
	
	/**
	 * Determines whether the other object is an instance of the same class and represents the same element, i.e. has
	 * the same content.
	 * <p>NOTE: As the other of the child elements may be semantically relevant the child elements must be in the same
	 * order to be considered equal. 
	 * 
	 * @param o 	the other object
	 * @return 		<code>true</code> iff <code>o</code> represents the same element, i.e. has the same qualified name
	 * 				and list of child elements.
	 */
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		
		AbstractAnyTypeElement other = (AbstractAnyTypeElement) o;		
		if (this.content == null && other.content == null)
			return true;
		else if (this.content != null && other.content != null) {
			boolean isEqual = this.content.size() == other.content.size();
			for (int i = 0; i < this.content.size() && isEqual; i++)
				isEqual = this.content.get(i).isEqualNode(other.content.get(i));
			return isEqual;
		} else
			return false;
	}
}
