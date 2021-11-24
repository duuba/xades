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

import javax.xml.namespace.QName;

/**
 * A representation of the <code>CertDigest</code> element defined in the complex types <code>CertIDType</code> and
 * <code>CertIDTypeV2</code> as defined in respectively <i>ETSI TS 101 903 V1.4.1</i> and <i>ETSI EN 319 132</i>.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class CertDigest extends AbstractDigestAlgAndValueTypeElement {
	final static QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "CertDigest");
	
	CertDigest(String digestAlg, byte[] digestVal) {
		super(digestAlg, digestVal);
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}

}
