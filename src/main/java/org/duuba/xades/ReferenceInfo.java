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
 * A representation of the <code>ReferenceInfo</code> element as defined in respectively <i>ETSI TS 101 903 
 * V1.4.1</i> and<i>ETSI EN 319 132 v1.1.1</i> standards. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="ReferenceInfo" type="ReferenceInfoType"/&gt;
 * &lt;xsd:complexType name="ReferenceInfoType"&gt;
 *    &lt;xsd:sequence&gt;
 *       &lt;xsd:element ref="ds:DigestMethod"/&gt;
 *       &lt;xsd:element ref="ds:DigestValue"/&gt;
 *    &lt;/xsd:sequence&gt;
 *    &lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 *    &lt;xsd:attribute name="URI" type="xsd:anyURI" use="optional"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class ReferenceInfo extends AbstractDigestAlgAndValueTypeElement {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "ReferenceInfo", 
														Constants.XADES_132_NS_PREFIX);

	private String	id;
	private String	uri;

	
	ReferenceInfo(final String id, final String uri, final String digestAlg, final byte[] digestVal) {
		super(digestAlg, digestVal);
		this.id = id;
		this.uri = uri;
	}
	
	/**
	 * @return the <code>Id</code> attribute (may be <code>null</code> if not specified) 
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return reference to the object included in the timestamp. 
	 */
	public String getURI() {
		return uri;
	}

	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
		// Write attributes
		if (id != null && !id.isEmpty())
			xwriter.writeIdAttribute("", Constants.XADES_141_NS_URI, "Id", id);         
		if (uri != null && !uri.isEmpty())
			xwriter.writeIdAttribute("", Constants.XADES_132_NS_URI, "URI", uri);         
        
		super.writeContent(xwriter, nsPrefix, dsPrefix, context);         	
	}
}
