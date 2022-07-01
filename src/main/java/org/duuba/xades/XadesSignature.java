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

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignature.SignatureValue;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents a Xades signature structure, i.e. a <code>ds:Signature</code> that includes a <code>ds:Object<code> with a
 * <code>xades:QualifyingProperties</code> child. Because the Xades signature is an extension of the regular XML 
 * signature this class' method signature is also the same as the {@link XMLSignature} interface with an additional
 * method to get the qualifying properties. 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class XadesSignature {	
	private XMLSignature			xmlSignature;
	private QualifyingProperties 	qProperties;
	private List<XMLObject>			otherObjects;
	private Element					signatureElement;
	
	XadesSignature(final XMLSignature baseSignature, final QualifyingProperties qualifyingProps, 
					final List<XMLObject> otherObjects) {
		this.xmlSignature = baseSignature;
		this.qProperties = qualifyingProps;
		this.otherObjects = otherObjects;
	}
	
	/**
	 * Get the <code>Signature</code> XML element representing this Xades signature. Note that this element is only 
	 * available after the {@link #sign()} method has been called when creating the signature.   
	 * 
	 * @return	the <code>Signature</code> XML element 
	 */
	public Element getElement() {
		return signatureElement;
	}
	
    /**
     * @return the key info of this Xades signature (may be <code>null</code> if not specified)
     */
	public KeyInfo getKeyInfo() {
		return xmlSignature.getKeyInfo();
	}

    /**
     * @return the signed info of this Xades Signature (never <code>null</code>)
     */
	public SignedInfo getSignedInfo() {
		return xmlSignature.getSignedInfo();
	}

	/**
	 * @return the qualifying properties of this Xades Signature 
	 */
	public QualifyingProperties getQualifyingProperties() {
		return qProperties;
	}
	
	/**
	 * Return the list of other <code>XMLObject</code>s, i.e. those elements not having the <code>QualifyingProperties
	 * </code> as child, contained in this Xades signature.
	 * 
     * @return a list of <code>XMLObject</code>s (may be empty but never <code>null</code>)
     */
	public List<XMLObject> getOtherObjects() {
		return otherObjects;
	}

	/**
	 * @return the Id of the Xades signature 
	 */
	public String getId() {
		return xmlSignature.getId();
	}

	/**
	 * @return the signature value of this Xades signature
	 */
	public SignatureValue getSignatureValue() {
		return xmlSignature.getSignatureValue();
	}

	/**
	 * Signs the Xades signature. As the Xades signature is just a regular XML signature containing additional signed
	 * information this method just calls {@link XMLSignature#sign(XMLSignContext)}. Its behaviour therefore is the
	 * same as regular XML signing.  
     * <p>If this method throws an exception, this <code>XadesSignature</code> and the <code>signContext</code> 
     * parameter will be left in the state that it was in prior to the invocation.
     *
     * @param signContext the signing context
     * @throws ClassCastException 	 if the type of <code>signContext</code> is not a {@link DOMSignContext}
     * @throws NullPointerException  if <code>signContext</code> is <code>null</code>
     * @throws MarshalException 	 if an exception occurs while marshalling
     * @throws XMLSignatureException if an unexpected exception occurs while generating the signature
     */
	public void sign(DOMSignContext signContext) throws MarshalException, XMLSignatureException {
		if (signatureElement == null) { 			
			if (signContext.getNamespacePrefix(XMLSignature.XMLNS, signContext.getDefaultNamespacePrefix()) == null)
				signContext.putNamespacePrefix(XMLSignature.XMLNS, "ds");
			if (signContext.getNamespacePrefix(Constants.XADES_132_NS_URI, null) == null)
				signContext.putNamespacePrefix(Constants.XADES_132_NS_URI, Constants.XADES_132_NS_PREFIX);
			if (signContext.getNamespacePrefix(Constants.XADES_141_NS_URI, null) == null)
				signContext.putNamespacePrefix(Constants.XADES_141_NS_URI, Constants.XADES_141_NS_PREFIX);
			
			xmlSignature.sign(signContext);
			// Get the Signature element of this Signature
			final Node sibling = signContext.getNextSibling();
			if (sibling != null) 
				signatureElement = (Element) sibling.getPreviousSibling();
			else
				signatureElement = (Element) signContext.getParent().getLastChild();			
		}
	}

	/**
	 * Validates the Xades signature.
	 * <p>Currently this only performs the default XML signature validation. The implementation will be extended to
	 * in future versions to include Xades specific validation. 
	 * <p><b>Therefore the specifications of this method will change!</b>
	 *   
	 * @param validateContext
	 * @return
	 * @throws XMLSignatureException
	 */
	public boolean validate(XMLValidateContext validateContext) throws XMLSignatureException {
		return xmlSignature.validate(validateContext);
	}

}
