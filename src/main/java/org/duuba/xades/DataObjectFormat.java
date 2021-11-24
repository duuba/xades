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
 * A representation of the <code>DataObjectFormat</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1 </i>
 * standard. The XML schema is defined as: <code><pre>
 * &lt;xsd:element name="DataObjectFormat" type="DataObjectFormatType"/&gt;
 * &lt;xsd:complexType name="DataObjectFormatType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="Description" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="ObjectIdentifier" type="ObjectIdentifierType" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="MimeType" type="xsd:string" minOccurs="0"/&gt;
 * 		&lt;xsd:element name="Encoding" type="xsd:anyURI" minOccurs="0"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * 	&lt;xsd:attribute name="ObjectReference" type="xsd:anyURI" use="required"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code>
 * 
 * <p>
 * A <code>DataObjectFormat</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newDataObjectFormat} methods.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class DataObjectFormat extends XadesElement {
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "DataObjectFormat", 
																				Constants.XADES_132_NS_PREFIX);
	
	private String objectRef;
	private String description;
	private ObjectIdentifier objectId;
	private String mimeType;
	private String encoding;

	DataObjectFormat(String objectRef, String description, IObjectIdentifier objectId, String mimeType,
			String encoding) {
		this.objectRef = objectRef;
		this.description = description;
		this.objectId = objectId != null ? new ObjectIdentifier(objectId) : null;
		this.mimeType = mimeType;
		this.encoding = encoding;
	}

	/**
	 * @return URI pointing to the <code>ds:Reference</code> this element applies to
	 */
	public String getObjectRef() {
		return objectRef;
	}

	/**
	 * @return the description of the data object
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the object identifier of the data object
	 */
	public ObjectIdentifier getObjectId() {
		return objectId;
	}

	/**
	 * @return the MIME type of the data object
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @return the encoding of the data object
	 */
	public String getEncoding() {
		return encoding;
	}

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
		// Write attribute
		xwriter.writeAttribute("", null, "ObjectReference", objectRef);

		// Write child elements
		if (description != null && !description.isEmpty())
			xwriter.writeTextElement(nsPrefix, "Description", Constants.XADES_132_NS_URI, description);
		if (objectId != null)
			objectId.marshal(xwriter, dsPrefix, context);
		if (mimeType != null && !mimeType.isEmpty())
			xwriter.writeTextElement(nsPrefix, "MimeType", Constants.XADES_132_NS_URI, mimeType);
		if (encoding != null && !encoding.isEmpty())
			xwriter.writeTextElement(nsPrefix, "Encoding", Constants.XADES_132_NS_URI, encoding);
		
	}

}
