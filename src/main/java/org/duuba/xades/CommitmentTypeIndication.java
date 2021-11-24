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
 * A representation of the <code>CommitmentTypeIndication</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1 </i>
 * standard. The XML schema is defined as: <code><pre>
 * &lt;xsd:element name="CommitmentTypeIndication" type="CommitmentTypeIndicationType"/&gt;
 * &lt;xsd:complexType name="CommitmentTypeIndicationType"&gt;
 * 	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="CommitmentTypeId" type="ObjectIdentifierType"/&gt;
 * 		&lt;xsd:choice&gt;
 * 			&lt;xsd:element name="ObjectReference" type="xsd:anyURI" maxOccurs="unbounded"/&gt;
 * 			&lt;xsd:element name="AllSignedDataObjects"/&gt;
 * 		&lt;/xsd:choice&gt;
 * 		&lt;xsd:element name="CommitmentTypeQualifiers" type="CommitmentTypeQualifiersListType" minOccurs="0"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code>
 * 
 * <p>
 * A <code>CommitmentTypeIndication</code> instance may be created by invoking one of the
 * {@link XadesSignatureFactory#newCommitmentTypeIndication} methods.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class CommitmentTypeIndication extends XadesElement {
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "CommitmentTypeIndication", 
															Constants.XADES_132_NS_PREFIX);
	private static final QName TYPE_ID_QNAME = new QName(Constants.XADES_132_NS_URI, "CommitmentTypeId", 
															Constants.XADES_132_NS_PREFIX);
	private CommitmentTypeId	typeId;
	private List<String>		objectRefs;
	private List<CommitmentTypeQualifier> qualifiers;
	
	CommitmentTypeIndication(final IObjectIdentifier typeId, final List<String> objectRefs,
							 final List<CommitmentTypeQualifier> qualifiers) {
		this.typeId = new CommitmentTypeId(typeId);
		this.objectRefs = objectRefs;
		this.qualifiers = qualifiers;
	}

	/**
	 * @return the object identifier identifying the commitment type
	 */
	public CommitmentTypeId getCommitmentTypeId() {
		return typeId;
	}

	/**
	 * @return	whether this commitment type indication applies to all data objects of the signature
	 */
	public boolean appliesToAllObjects() {
		return objectRefs == null || objectRefs.isEmpty();
	}

	/**
	 * @return references to the <code>ds:Reference</code> elements to which this commitment type applies
	 */
	public List<String> getObjectReferences() {
		return objectRefs;
	}

	/**
	 * @return the commitment type qualifiers
	 */
	public List<CommitmentTypeQualifier> getCommitmentTypeQualifiers() {
		return qualifiers;
	}

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {

		// Write child elements
		typeId.marshal(xwriter, dsPrefix, context); 
		
		if (appliesToAllObjects()) {
			xwriter.writeStartElement(nsPrefix, "AllSignedDataObjects", Constants.XADES_132_NS_URI);
			xwriter.writeEndElement();
		} else {
			for (String ref : objectRefs) 
				xwriter.writeTextElement(nsPrefix, "ObjectReference", Constants.XADES_132_NS_URI, ref);						
		}
		if (qualifiers != null && !qualifiers.isEmpty()) {
			xwriter.writeStartElement(nsPrefix, "CommitmentTypeQualifiers", Constants.XADES_132_NS_URI);
			for(CommitmentTypeQualifier q : qualifiers)
				q.marshal(xwriter, dsPrefix, context);
			xwriter.writeEndElement();		
		}
	}

	/**
	 * A representation of the <code>CommitmentTypeId</code> element 
	 */
	public class CommitmentTypeId extends AbstractObjectIdentifierTypeElement {

		CommitmentTypeId(IObjectIdentifier oid) {
			super(oid);
		}

		@Override
		protected QName getName() {
			return TYPE_ID_QNAME;
		}
		
	}
}
