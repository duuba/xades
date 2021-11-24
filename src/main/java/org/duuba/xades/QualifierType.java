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

/**
 * Represents the values as defined by the simple <code>QualiferType</code> as defined in the <i>ETSI EN 319 132 v1.1.1
 * </i> standard. The type is defined in the XML schema as: 
 * <pre><code>
 * &lt;xsd:simpleType name="QualifierType"&gt;
 *	&lt;xsd:restriction base="xsd:string"&gt;
 *		&lt;xsd:enumeration value="OIDAsURI"/&gt;
 *		&lt;xsd:enumeration value="OIDAsURN"/&gt;
 *	&lt;/xsd:restriction&gt;
 * </code></pre>  
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public enum QualifierType {
	OIDAsURI, OIDAsURN
}
