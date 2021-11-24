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

import java.security.cert.X509Certificate;

/**
 * A abstraction of the <code>CertifiedRole</code> child element of the <code>SignerRole</code> and <code>SignerRoleV2
 * </code> as defined in respectively <i>ETSI TS 101 903 V1.4.1</i>  and <i>ETSI EN 319 132 V1.1.1</i>. Both versions
 * support a X509 certificate to provided information about the certified role, but in <i>ETSI EN 319 132 V1.1.1</i> the
 * information can also be captured in custom XML elements.
 * 
 * <p>Using one of the {@link XadesSignatureFactory#newCertifiedRole} methods a concrete implementation can be created.
 * The factory will automatically return the correct implementation based on the {@link XadesVersion} used on 
 * initialisation of the factory.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public interface CertifiedRole {

	/**
	 * Returns the information about the certified role as a X509 Attribute Certificate.
	 * 
	 * @return X509 Attribute Certificate data  
	 */
	X509Certificate getX509AttributeCertificate();
}
