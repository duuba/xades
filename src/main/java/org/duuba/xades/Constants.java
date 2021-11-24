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
 * Defines constants used in the processing of the Xades signatures.  
 * 
 */
public interface Constants {
	/**
	 * The namespace URI for the "main" Xades schema
	 */
	String	XADES_132_NS_URI = "http://uri.etsi.org/01903/v1.3.2#";
	/**
	 * The preferred namespace prefix for the "main" Xades schema
	 */
	String	XADES_132_NS_PREFIX = "xades";
	/**
	 * The namespace URI for the "extended" Xades schema
	 */	
	String	XADES_141_NS_URI = "http://uri.etsi.org/01903/v1.4.1#";
	/**
	 * The preferred namespace prefix for the "extended" Xades schema
	 */
	String	XADES_141_NS_PREFIX = "xades141";
	/**
	 * The reference type to use for the signed qualifying properties
	 */
	String	SIGNED_PROPS_REF_TYPE = "http://uri.etsi.org/01903#SignedProperties";
}
