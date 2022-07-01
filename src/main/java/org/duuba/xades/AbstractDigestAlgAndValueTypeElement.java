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
import javax.xml.crypto.dsig.XMLSignature;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.bouncycastle.util.Arrays;

/**
 * Is a base class for the representation of elements that are of type <code>DigestAlgAndValueType</code> as defined in 
 * the <i>ETSI TS 101 903 V1.4.1</i> standard. The type is defined in the XML schema as:
 * <code><pre>
 * &lt;xsd:complexType name="DigestAlgAndValueType"&gt; 
 * 	&lt;xsd:sequence&gt;
 *		&lt;xsd:element ref="ds:DigestMethod"/&gt;
 *		&lt;xsd:element ref="ds:DigestValue"/&gt;
 *	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public abstract class AbstractDigestAlgAndValueTypeElement extends XadesElement {
	
	private String	algorithm;
	private byte[]	value;
	
	public AbstractDigestAlgAndValueTypeElement(final String digestAlg, final byte[] digestVal) {
		this.algorithm = digestAlg;
		this.value = digestVal;
	}
	
	/**
	 * @return the digest algorithm that is used
	 */
	public String getDigestMethod() {
		return algorithm;
	}
	
	/**
	 * @return the digest value as contained in the element
	 */
	public byte[] getDigestValue() {
		return value;
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
		else  {
			AbstractDigestAlgAndValueTypeElement other = (AbstractDigestAlgAndValueTypeElement) o;
			return this.algorithm.equals(other.algorithm) && Arrays.areEqual(this.value, other.value);
		}
	}

	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context) 
				throws MarshalException {
	
        // create and append DigestMethod element
		xwriter.writeStartElement(dsPrefix, "DigestMethod", XMLSignature.XMLNS);
        xwriter.writeAttribute("", "", "Algorithm", algorithm);
        xwriter.writeEndElement(); // "DigestMethod"
        
        // create and append DigestValue element
        xwriter.writeStartElement(dsPrefix, "DigestValue", XMLSignature.XMLNS);
        if (value != null) {
            xwriter.writeCharacters(XMLUtils.encodeToString(value));
        }
        xwriter.writeEndElement(); // "DigestValue"
	}
}
