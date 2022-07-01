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

/**
 * Is an abstraction of the <code>ObjectIdentifierType</code> as defined in the Xades specifications. The interface is
 * introduced to allow easy transfer of object identifiers between the Xades and external classes. 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 * @see AbstractObjectIdentifierTypeElement
 */
public interface IObjectIdentifier {

	/**
	 * @return the identifier value
	 */
	String getIdentifier();
	
	/**
	 * @return the identifier's qualifier 
	 */
	QualifierType getQualifier();
	
	/**
	 * @return the object identifier's description 
	 */
	String getDescription();
	
	/**
	 * @return the references to the documentation of the object identfier 
	 */
	List<String> getDocumentationReferences();
}
