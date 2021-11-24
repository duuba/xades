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

/**
 * A representation of the <code>SignedDataObjectProperties</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1
 * </i> standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="SignedDataObjectProperties" type="SignedDataObjectPropertiesType"/&gt;
 * &lt;xsd:complexType name="SignedDataObjectPropertiesType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element ref="DataObjectFormat" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;xsd:element ref="CommitmentTypeIndication" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;xsd:element ref="AllDataObjectsTimeStamp" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;xsd:element ref="IndividualDataObjectsTimeStamp" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;xsd:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * 	&lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SignedDataObjectProperties</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newSignedDataObjectProperties} methods.
 * <p><b>NOTE:</b> Currently there is no support for the inclusion of the time stamp related elements!
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SignedDataObjectProperties extends XadesElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "SignedDataObjectProperties", 
																Constants.XADES_132_NS_PREFIX);
	
	private String	id;
	private List<DataObjectFormat>	dataObjectFormats;
	private List<CommitmentTypeIndication> commitments;
	
	SignedDataObjectProperties(final String id, final List<DataObjectFormat> dataObjectFormats, 
							   final List<CommitmentTypeIndication> commitments) {
		this.id = id;
		this.dataObjectFormats = dataObjectFormats;
		this.commitments = commitments;
	}

	/**
	 * @return value of the Id attribute 
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return information on the format of the signed data objects
	 */
	public List<DataObjectFormat> getDataObjectFormats() {
		return dataObjectFormats;
	}

	/**
	 * @return information about the signer's commitment type
	 */
	public List<CommitmentTypeIndication> getCommitments() {
		return commitments;
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
		// Write attribute
		if (id != null && !id.isEmpty())
			xwriter.writeIdAttribute("", Constants.XADES_132_NS_URI, "Id", id);         

		// Write child elements
		if (dataObjectFormats != null && !dataObjectFormats.isEmpty()) {
			for(DataObjectFormat df : dataObjectFormats)
				df.marshal(xwriter, dsPrefix, context);
		}
		if (commitments != null && !commitments.isEmpty()) {
			for(CommitmentTypeIndication c : commitments)
				c.marshal(xwriter, dsPrefix, context);
		}
	}
}
