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

/**
 * A representation of the <code>QbjectIdentifier</code> element as defined in the <i>ETSI EN 319 132-1 V1.1.1</i>
 * standard. The XML schema is defined as: 
 * <code><pre>
 * &lt;xsd:element name="QbjectIdentifier" type="QbjectIdentifierType"/&gt;
 * </pre></code>
 * As the <code>QbjectIdentifierType</code> is re-used by other element declarations the <code>ObjectIdentifierType
 * </code> has its own abstract base class implementation.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */ 
public class ObjectIdentifier extends AbstractObjectIdentifierTypeElement {
	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "ObjectIdentifier", 
			Constants.XADES_132_NS_PREFIX);
	
	ObjectIdentifier(IObjectIdentifier source) {
		super(source);
	}
	
	ObjectIdentifier(String identifier, QualifierType qualifier, String description, List<String> docReferences) {
		super(identifier, qualifier, description, docReferences);
	}

	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

}
