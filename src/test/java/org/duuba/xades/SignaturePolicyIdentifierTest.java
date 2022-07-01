package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI;
import org.duuba.xades.SignaturePolicyIdentifier.SigPolicyQualifier;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class SignaturePolicyIdentifierTest {

	static ObjectIdentifier T_POL_ID = new ObjectIdentifier("pol-id-1", null, null, null);
	static byte[] T_DIGEST_VALUE = "Not-a-real-digest-value-but-fine-for-test".getBytes();
	
	@Test
	void testImplied() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() -> new SignaturePolicyIdentifier().marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignaturePolicyIdentifier", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(1, created.getChildNodes().getLength());		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SignaturePolicyImplied").getLength());
	}

	@Test
	void testPolicyWithoutTransformsAndQualifiers() throws ParserConfigurationException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		final SignaturePolicyIdentifier signaturePolicyIdentifier =
							new SignaturePolicyIdentifier(T_POL_ID, null, DigestMethod.SHA512, T_DIGEST_VALUE, null);
		assertDoesNotThrow(() -> signaturePolicyIdentifier.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignaturePolicyIdentifier", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(1, created.getChildNodes().getLength());		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SignaturePolicyId").getLength());
		
		Element sigPolId = (Element) 
								created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SignaturePolicyId").item(0);
		
		assertEquals(2, sigPolId.getChildNodes().getLength());
		assertEquals(1, sigPolId.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SigPolicyId").getLength());
		assertEquals(1, sigPolId.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SigPolicyHash").getLength());
		
		assertEquals(DigestMethod.SHA512, signaturePolicyIdentifier.getDigestAlgorithm());
		assertArrayEquals(T_DIGEST_VALUE, signaturePolicyIdentifier.getHashValue());
	}	
	
	@Test
	void testPolicyWithTransformsAndQualifiers() throws ParserConfigurationException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
		DOMXMLWriter xwriter = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		Security.addProvider(new XMLDSigRI());
		XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", "ApacheXMLDSig");
		
		List<Transform> transforms = new ArrayList<>();
		transforms.add(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
		transforms.add(factory.newTransform(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, (TransformParameterSpec) null));
		
		List<SigPolicyQualifier> qualifiers = new ArrayList<>();
		qualifiers.add(new SigPolicyQualifier(null));
			
		final SignaturePolicyIdentifier signaturePolicyIdentifier =
				new SignaturePolicyIdentifier(T_POL_ID, transforms, DigestMethod.SHA512, T_DIGEST_VALUE, qualifiers);
		assertDoesNotThrow(() -> signaturePolicyIdentifier.marshal(xwriter, "", context));
		
		Element created = xwriter.getCreatedElement();
		
		assertEquals("SignaturePolicyIdentifier", created.getLocalName());
		assertEquals(Constants.XADES_132_NS_URI, created.getNamespaceURI());
		
		assertEquals(1, created.getChildNodes().getLength());		
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SignaturePolicyId").getLength());
		
		Element sigPolId = (Element) 
				created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SignaturePolicyId").item(0);
		
		assertEquals(4, sigPolId.getChildNodes().getLength());
		assertEquals(1, sigPolId.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SigPolicyId").getLength());
		assertEquals(1, sigPolId.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SigPolicyHash").getLength());
		assertEquals(1, sigPolId.getElementsByTagNameNS(XMLSignature.XMLNS, "Transforms").getLength());
		
		NodeList transformEl = ((Element) sigPolId.getElementsByTagNameNS(XMLSignature.XMLNS, "Transforms").item(0))
														.getElementsByTagNameNS(XMLSignature.XMLNS, "Transform");
		assertEquals(transforms.size(), transformEl.getLength());
		for(int i = 0; i < transforms.size(); i++) 
			assertEquals(transforms.get(i).getAlgorithm(), ((Element) transformEl.item(i)).getAttribute("Algorithm"));
		
		assertEquals(1, sigPolId.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SigPolicyQualifiers").getLength());
		
		NodeList qualiEl = ((Element) sigPolId.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SigPolicyQualifiers").item(0))
												.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "SigPolicyQualifier");
		assertEquals(qualifiers.size(), qualiEl.getLength());
		
	}
	
	@Test
	void testEquals() throws Exception {
		Security.addProvider(new XMLDSigRI());
		XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", "ApacheXMLDSig");
		
		ObjectIdentifier oid2 = new ObjectIdentifier("pol-id-2", null, null, null);
		byte[] dv2 = "Another-fake-digest-value-but-fine-for-test".getBytes();
		
		List<Transform> transforms1 = new ArrayList<>();
		List<Transform> transforms2 = new ArrayList<>();
		transforms1.add(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
		transforms2.add(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
		transforms1.add(factory.newTransform(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, (TransformParameterSpec) null));
		
		List<SigPolicyQualifier> qualifiers1 = new ArrayList<>();
		List<SigPolicyQualifier> qualifiers2 = new ArrayList<>();
		qualifiers1.add(new SigPolicyQualifier(null));
		qualifiers2.add(new SigPolicyQualifier(null));
		qualifiers2.add(new SigPolicyQualifier(null));
			
		SignaturePolicyIdentifier spi1 =
				new SignaturePolicyIdentifier(T_POL_ID, transforms1, DigestMethod.SHA512, T_DIGEST_VALUE, qualifiers1);
		SignaturePolicyIdentifier spi2 =
				new SignaturePolicyIdentifier(T_POL_ID, transforms1, DigestMethod.SHA512, T_DIGEST_VALUE, qualifiers1);
		SignaturePolicyIdentifier spi3 =
				new SignaturePolicyIdentifier(oid2, transforms1, DigestMethod.SHA512, T_DIGEST_VALUE, qualifiers1);
		SignaturePolicyIdentifier spi4 =
				new SignaturePolicyIdentifier(T_POL_ID, null, DigestMethod.SHA512, T_DIGEST_VALUE, qualifiers1);
		SignaturePolicyIdentifier spi5 =
				new SignaturePolicyIdentifier(T_POL_ID, transforms2, DigestMethod.SHA512, T_DIGEST_VALUE, qualifiers1);
		SignaturePolicyIdentifier spi6 =
				new SignaturePolicyIdentifier(T_POL_ID, transforms1, DigestMethod.SHA256, T_DIGEST_VALUE, qualifiers1);
		SignaturePolicyIdentifier spi7 =
				new SignaturePolicyIdentifier(T_POL_ID, transforms1, DigestMethod.SHA512, dv2, qualifiers1);
		SignaturePolicyIdentifier spi8 =
				new SignaturePolicyIdentifier(T_POL_ID, transforms1, DigestMethod.SHA512, T_DIGEST_VALUE, null);
		SignaturePolicyIdentifier spi9 =
				new SignaturePolicyIdentifier(T_POL_ID, transforms1, DigestMethod.SHA512, T_DIGEST_VALUE, qualifiers2);
		SignaturePolicyIdentifier spi10 = new SignaturePolicyIdentifier();

		assertTrue(spi1.equals(spi2));
		assertTrue(spi10.equals(new SignaturePolicyIdentifier()));
		assertFalse(spi1.equals(spi3));
		assertFalse(spi1.equals(spi4));
		assertFalse(spi1.equals(spi5));
		assertFalse(spi1.equals(spi6));
		assertFalse(spi1.equals(spi7));
		assertFalse(spi1.equals(spi8));
		assertFalse(spi1.equals(spi9));
		assertFalse(spi1.equals(spi10));		
	}
}
