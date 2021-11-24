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
 * A representation of the <code>ClaimedRole</code> element part of the <code>SignerRole</code> and <code>SignerRoleV2
 * </code> elements as defined in respectively <i>ETSI TS 101 903 V1.4.1</i> and <i>ETSI EN 319 132 V1.1.1</i>. The XML 
 * schema is defined as:
 * <code><pre>
 * &lt;xsd:element name="ClaimedRole" type="AnyType"/&gt;
 * </pre></code>
 *  
 * <p>A <code>ClaimedRole</code> instance may be created by invoking the {@link XadesSignatureFactory#newClaimedRole} 
 * method.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class ClaimedRole extends AbstractAnyTypeElement {
	private final static QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "ClaimedRole", 
															Constants.XADES_132_NS_PREFIX);
	ClaimedRole(List<Node> content) {
		super(content);
	}

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
}
