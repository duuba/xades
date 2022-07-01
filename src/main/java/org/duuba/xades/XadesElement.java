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

import org.apache.jcp.xml.dsig.internal.dom.DOMStructure;
import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;

/**
 * Is a base class for the Xades specific elements in a signature to ensure consistent namespace prefixes when the DOM
 * tree is created.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public abstract class XadesElement extends DOMStructure {

	/**
	 * Gets the qualified name of the element. 
	 * <p>NOTE: When the returned qualified name includes a namespace prefix it is only used if the context does not
	 * already specify a prefix for the URI of the element's namespace.  
	 * 
	 * @return	QName of the element
	 */
	protected abstract QName getName();
	
	/**
	 * Writes the element's content, i.e. attributes and child elements to the document tree. The element start and end
	 * are done by this base class. 
	 * 
	 * @param xwriter		XML Writer to create the document tree 
	 * @param nsPrefix		namespace prefix of this element
	 * @param dsPrefix		prefix for the XML Signature namespace
	 * @param context		the current context
	 * @throws MarshalException when the object tree for the content of this element cannot be created
	 */
	protected abstract void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
											throws MarshalException;	
	
	@Override
	public void marshal(XmlWriter xwriter, String dsPrefix, XMLCryptoContext context) throws MarshalException {
		final QName elementName = getName();
		final String nsURI = elementName.getNamespaceURI();
		final String nsPrefix = context.getNamespacePrefix(nsURI, elementName.getPrefix());
		
		xwriter.writeStartElement(nsPrefix, elementName.getLocalPart(), nsURI);		
		writeContent(xwriter, nsPrefix, dsPrefix, context);		
		xwriter.writeEndElement();
	}
	
	/**
	 * Base implementation of {@link Object#equals(Object)} that checks if the other object is an instance of the same 
	 * class (and hence of <code>XadesElement</code>) and has the same QName as the current instance. 
	 * 
	 * @param o		the other object to compare with
	 * @return		<code>true<code> iff <code>o</code> is of the same type and has the same qualified name
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!this.getClass().equals(o.getClass()))
			return false;
		else if (!getName().equals(((XadesElement) o).getName()))
			return false;
		else
			return true;
	}
}
