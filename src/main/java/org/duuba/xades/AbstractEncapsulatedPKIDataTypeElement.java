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

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.bouncycastle.util.Arrays;
import org.holodeckb2b.commons.util.Utils;

/**
 * Is a base class for the representation of elements that are of type <code>EncapsulatedPKIDataType</code> as defined 
 * in the <i>ETSI EN 319 132 V1.1.1</i> standard. The type is defined in the XML schema as:
 * <code><pre>
 * &lt;xsd:complexType name="EncapsulatedPKIDataType"&gt;
 * 	&lt;xsd:simpleContent&gt;
 * 		&lt;xsd:extension base="xsd:base64Binary"&gt;
 * 			&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 * 			&lt;xsd:attribute name="Encoding" type="xsd:anyURI" use="optional"/&gt;
 * 		&lt;/xsd:extension&gt;
 * 	&lt;/xsd:simpleContent&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public abstract class AbstractEncapsulatedPKIDataTypeElement extends XadesElement {

	/**
	 * Enumerates the allowed values for the <code>Encoding</code> attribute. 
	 */
	public static enum Encoding {
		
		DER("http://uri.etsi.org/01903/v1.2.2#DER"),
		BER("http://uri.etsi.org/01903/v1.2.2#BER"),
		CER("http://uri.etsi.org/01903/v1.2.2#CER"),
		PER("http://uri.etsi.org/01903/v1.2.2#PER"),
		XER("http://uri.etsi.org/01903/v1.2.2#XER");
		
		private String uri;
		
		Encoding(String uri) {
			this.uri = uri;
		}
		
		public String uri() {
			return uri;
		}
	}
	
	protected String	id;
	protected byte[]	data;
	protected Encoding	encoding;
	
	public AbstractEncapsulatedPKIDataTypeElement(final String id, final byte[] data, final Encoding encoding) {
		this.id = id;
		this.data = data;
		this.encoding = encoding;
	}
	
	/**
	 * @return the Id assigned to this element
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the encoded data 
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * @return the encoding of the data
	 */
	public Encoding getEncoding() {
		return encoding;
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
		
		AbstractEncapsulatedPKIDataTypeElement other = (AbstractEncapsulatedPKIDataTypeElement) o;
		return Arrays.areEqual(this.data, other.data) && this.encoding == other.encoding
				&& Utils.nullSafeEqual(this.id, other.id);
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
	
		// Write attributes
		if (!Utils.isNullOrEmpty(id))
			xwriter.writeIdAttribute("", Constants.XADES_132_NS_URI, "Id", id);    
		if (encoding != null)
			xwriter.writeAttribute("", Constants.XADES_132_NS_URI, "Encoding", encoding.uri());
		// write the base64 encoded bytes
		xwriter.writeCharacters(XMLUtils.encodeToString(data));		
	}

}
