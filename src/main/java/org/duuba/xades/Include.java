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
 * A representation of the <code>Include</code> element as defined in respectively <i>ETSI TS 101 903 
 * V1.4.1</i> and<i>ETSI EN 319 132 v1.1.1</i> standards. The XML schema is defined as:
 * <code><pre>
 *    &lt;xsd:element name="Include" type="IncludeType"/&gt;
 *    &lt;xsd:complexType name="IncludeType"&gt;
 * 		 &lt;xsd:attribute name="URI" type="xsd:anyURI" use="required"/&gt;
 *       &lt;xsd:attribute name="referencedData" type="xsd:boolean" use="optional"/&gt;
 *    </xsd:complexType>
 * </pre></code> 
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class Include extends XadesElement {
	
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "Include", 
														Constants.XADES_132_NS_PREFIX);

	private String	uri;
	private Boolean referencedData;
	
	Include(final String uri, final Boolean referencedData) {
		this.uri = uri;
		this.referencedData = referencedData;
	}
	
	/**
	 * @return the X-Pointer to the object included in the timestamp. 
	 */
	public String getURI() {
		return uri;
	}
	
	/**
	 * @return indication whether the actual data was used in the timestamp calculation when the URI points to a 
	 * 			<code>ds:Reference</code> element  
	 */
	public Boolean getReferencedData() {
		return referencedData;
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {

		// Write attributes
		xwriter.writeIdAttribute("", Constants.XADES_132_NS_URI, "URI", uri);         
        
		if (referencedData != null)
			xwriter.writeIdAttribute("", Constants.XADES_132_NS_URI, "referencedData", referencedData.toString());         
			
	}
	
	
}
