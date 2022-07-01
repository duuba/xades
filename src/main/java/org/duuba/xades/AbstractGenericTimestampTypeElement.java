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
import javax.xml.crypto.dsig.CanonicalizationMethod;

import org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalizationMethod;
import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.holodeckb2b.commons.util.Utils;

/**
 * Is a base class for the representation of elements that are of a type derived from the <code>GenericTimestampType
 * </code> as defined in respectively <i>ETSI TS 101 903 V1.4.1</i> and <i>ETSI EN 319 132 V1.1.1</i>. The type is 
 * defined in the XML schema as:
 * <code><pre>
 * &lt;xsd:complexType name="GenericTimeStampType" abstract="true"&gt;
 *    &lt;xsd:sequence&gt;
 *       &lt;xsd:choice minOccurs="0"&gt;
 *          &lt;xsd:element ref="Include" minOccurs="0" maxOccurs="unbounded"/&gt;
 *          &lt;xsd:element ref="ReferenceInfo" maxOccurs="unbounded"/&gt;
 *       &lt;/xsd:choice&gt;
 *       &lt;xsd:element ref="ds:CanonicalizationMethod" minOccurs="0"/&gt;
 *       &lt;xsd:choice maxOccurs="unbounded"&gt;
 *          &lt;xsd:element name="EncapsulatedTimeStamp" type="EncapsulatedPKIDataType"/&gt;
 *          &lt;xsd:element name="XMLTimeStamp" type="AnyType"/&gt;
 *       &lt;/xsd:choice&gt;
 *    &lt;/xsd:sequence&gt;
 *    &lt;xsd:attribute name="Id" type="xsd:ID" use="optional"/&gt;
 * </xsd:complexType>
 * </pre></code> 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public abstract class AbstractGenericTimestampTypeElement extends XadesElement {
	
	private String id;
	private List<Include>				includes;
	private List<ReferenceInfo>			referenceInfos;
	private DOMCanonicalizationMethod	c14nMethod;
	private List<EncapsulatedTimeStamp> encapsulatedTS;
	private List<XMLTimeStamp> 			xmlTS;
	
	protected AbstractGenericTimestampTypeElement(final String id,
												  final List<Include> includes, 
												  final List<ReferenceInfo> referenceInfos,
												  final DOMCanonicalizationMethod c14nMethod,
												  final List<EncapsulatedTimeStamp> encapsulatedTS,
												  final List<XMLTimeStamp> xmlTS) {
		this.id = id;
		this.includes = includes;
		this.referenceInfos = referenceInfos;
		this.c14nMethod = c14nMethod;
		this.encapsulatedTS = encapsulatedTS;
		this.xmlTS = xmlTS;
	}

	/**
	 * @return the Id assigned to this element
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the list of objects explicitly included in this time stamp. If the result is <code>null</code> or an 
	 * 			empty list it depends on the context which objects are included in the signature.   
	 */
	public List<Include> getIncludes() {
		return includes;
	}
	
	/**
	 * @return the list of objects explicitly included in this time stamp. If the result is <code>null</code> or an 
	 * 			empty list it depends on the context which objects are included in the signature.   
	 */
	public List<ReferenceInfo> getReferenceInfos() {
		return referenceInfos;
	}
	
	/**
	 * @return the canonicalization method that is used to get the octets from the XML objects that are included in the
	 * 			time stamp 
	 */
	public CanonicalizationMethod getCanonicalizationMethod() {
		return c14nMethod;
	}
	
	/**
	 * @return the time stamps included in the signature in PKI encapsulated format
	 */
	public List<EncapsulatedTimeStamp> getEncapsulatedTimeStamps() {
		return encapsulatedTS;
	}

	/**
	 * @return the time stamps included in the signature in XML format
	 */
	public List<XMLTimeStamp> getXMLTimeStamps() {
		return xmlTS;
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
		
		AbstractGenericTimestampTypeElement other = (AbstractGenericTimestampTypeElement) o;
		
		return Utils.nullSafeEqual(this.id, other.id)
			&& Utils.areEqual(this.includes, other.includes)
			&& Utils.areEqual(this.referenceInfos, other.referenceInfos)
			&& Utils.nullSafeEqual(this.c14nMethod, other.c14nMethod)
			&& Utils.areEqual(this.encapsulatedTS, other.encapsulatedTS)
			&& Utils.areEqual(this.xmlTS, other.xmlTS);
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
																							throws MarshalException {

		// Write id attribute
		if (!Utils.isNullOrEmpty(id))
			xwriter.writeIdAttribute("", null, "Id", id);
		
		// Write child elements
		if (!Utils.isNullOrEmpty(includes)) {
			for(Include i : includes)
				i.marshal(xwriter, dsPrefix, context);
		}
		if (!Utils.isNullOrEmpty(referenceInfos)) {
			for(ReferenceInfo r : referenceInfos)
				r.marshal(xwriter, dsPrefix, context);
		}
		if (c14nMethod != null)
			c14nMethod.marshal(xwriter, dsPrefix, context);
		if (!Utils.isNullOrEmpty(encapsulatedTS)) {
			for(EncapsulatedTimeStamp ts : encapsulatedTS)
				ts.marshal(xwriter, dsPrefix, context);
		}
		if (!Utils.isNullOrEmpty(xmlTS)) {
			for(XMLTimeStamp ts : xmlTS)
				ts.marshal(xwriter, dsPrefix, context);
		}		
	}
}
