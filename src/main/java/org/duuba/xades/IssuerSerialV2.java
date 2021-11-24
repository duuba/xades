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

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;

import org.apache.jcp.xml.dsig.internal.dom.XmlWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;

/**
 * A representation of the <code>IssuerSerialV2</code> element from the complex type <code>CertIDTypeV2</code> defined 
 * in <i>ETSI EN 319 132 v1.1.1</i>.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class IssuerSerialV2 extends org.duuba.xades.IssuerSerial {

	private static final QName ELEMENT_NAME = new QName(Constants.XADES_132_NS_URI, "IssuerSerialV2", 
																				Constants.XADES_132_NS_PREFIX);
	
	private	String	b64encoded;
	
	IssuerSerialV2(X509Certificate cert) throws CertificateEncodingException {
		super(cert);
		
		try {
			final GeneralName generalName = new GeneralName(GeneralName.directoryName, issuerName);
			final GeneralNames generalNames = new GeneralNames(generalName);
			final IssuerSerial issuerSerial = new org.bouncycastle.asn1.x509.IssuerSerial(generalNames, serialNo);		
			b64encoded = XMLUtils.encodeToString(issuerSerial.getEncoded("DER"));
		} catch (Exception e) {
			throw new CertificateEncodingException("Could not extract Issuer / SerialNo from certificate");
		}
	}
	
	@Override
	protected QName getName() {		
		return 	ELEMENT_NAME;
	}
	
	@Override
	protected void writeContent(XmlWriter xwriter, String nsPrefix, String dsPrefix, XMLCryptoContext context)
			throws MarshalException {

		xwriter.writeCharacters(b64encoded);
	}
}
