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

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;

/**
 * A representation of the <code>IssuerSerial</code> element from the complex type <code>CertIDType</code> defined in 
 * <i>ETSI TS 101 903 V1.4.1</i>.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class IssuerSerial extends XadesElement {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "IssuerSerial", 
														Constants.XADES_132_NS_PREFIX);;
	
	protected String		issuerName;
	protected BigInteger	serialNo;
	
	IssuerSerial(X509Certificate cert) {
		this.issuerName = cert.getIssuerX500Principal().getName();
		this.serialNo = cert.getSerialNumber();
	}
	
	/**
	 * @return the name of the certificate issuer
	 */
	public String getIssuerName() {
		return issuerName;
	}
	
	/**
	 * @return the serial number of the certificate
	 */
	public BigInteger getSerialNo() {
		return serialNo;
	}
	
	@Override
	protected QName getName() {
		return ELEMENT_NAME;
	}
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {
		
        xwriter.writeTextElement(dsPrefix, "X509IssuerName", XMLSignature.XMLNS, issuerName);
        xwriter.writeTextElement(dsPrefix, "X509SerialNumber", XMLSignature.XMLNS, serialNo.toString());
    }
}
