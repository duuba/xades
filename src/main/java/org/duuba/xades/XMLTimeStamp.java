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
 * A representation of the <code>XMLTimeStamp</code> element defined in the <code>GenericTimestampType</code> 
 * complex type as specified in  <i>ETSI TS 101 903 V1.4.1</i> and <i>ETSI EN 319 132 V1.1.1</i>. 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 * @see AbstractGenericTimestampTypeElement
 */ 
public class XMLTimeStamp extends AbstractAnyTypeElement {

	private final static QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "XMLTimeStamp", 
														Constants.XADES_132_NS_PREFIX);


	XMLTimeStamp(List<Node> content) {
		super(content);
	}

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

}
