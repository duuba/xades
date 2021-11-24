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

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

/**
 * A representation of the <code>SigPolicyQualifier</code> element part of the <code>SignaturePolicyIdentifier</code> as 
 * defined in the <i>ETSI EN 319 132-1 V1.1.1</i> standard. The XML schema is defined as:
 * <code><pre>
 * &lt;xsd:complexType name="SigPolicyQualifiersListType"&gt;
 *	&lt;xsd:sequence&gt;
 * 		&lt;xsd:element name="SigPolicyQualifier" type="AnyType" maxOccurs="unbounded"/&gt;
 * 	&lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre></code> 
 * 
 * <p>A <code>SigPolicyQualifier</code> instance may be created by invoking the 
 * {@link XadesSignatureFactory#newSigPolicyQualifier} method.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SigPolicyQualifier extends AbstractAnyTypeElement {
	private QName elName = new QName(Constants.XADES_132_NS_URI, "SigPolicyQualifier", Constants.XADES_132_NS_PREFIX);

	SigPolicyQualifier(List<Node> content) {
		super(content);
	}
	
	@Override
	protected QName getName() {
		return elName;
	}

}
