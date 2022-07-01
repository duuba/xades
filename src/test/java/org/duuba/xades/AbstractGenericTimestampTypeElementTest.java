package org.duuba.xades;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalizationMethod;
import org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI;
import org.duuba.xades.AbstractEncapsulatedPKIDataTypeElement.Encoding;
import org.duuba.xades.test.DOMXMLWriter;
import org.duuba.xades.test.TestCryptoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class AbstractGenericTimestampTypeElementTest {
	
	static String  ID;
	static Include INCLUDE_1; 
	static Include INCLUDE_2;
	static ReferenceInfo REF_1;
	static ReferenceInfo REF_2;
	static DOMCanonicalizationMethod C14N;
	static EncapsulatedTimeStamp ETS_1;
	static EncapsulatedTimeStamp ETS_2;
	static XMLTimeStamp XTS_1;
	static XMLTimeStamp XTS_2;
	
	@BeforeAll
	static void setup() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		Security.addProvider(new XMLDSigRI());

		ID = UUID.randomUUID().toString();		
		INCLUDE_1 = new Include(id(), true);
		INCLUDE_2 = new Include(id(), false);		
		REF_1 = new ReferenceInfo(id(), DigestMethod.SHA512, ID, "DigestValueBytes".getBytes());
		REF_2 = new ReferenceInfo(id(), DigestMethod.SHA512, ID, "MoreDigestValueBytes".getBytes());		
		C14N = (DOMCanonicalizationMethod) XMLSignatureFactory.getInstance("DOM", "ApacheXMLDSig")
						.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);
		ETS_1 = new EncapsulatedTimeStamp(id(), "EncapsulatedTimestampData".getBytes(), Encoding.BER);
		ETS_2 = new EncapsulatedTimeStamp(id(), "AnotherEncapsulatedTimestampData".getBytes(), Encoding.DER);
		XTS_1 = new XMLTimeStamp(null);
		XTS_2 = new XMLTimeStamp(null);
	}

	@Test
	void testInclude() throws ParserConfigurationException {
		final List<Include> includes = new ArrayList<>();		
		includes.add(INCLUDE_1);
		includes.add(INCLUDE_2);
		
		final List<EncapsulatedTimeStamp> ts = new ArrayList<>();
		ts.add(ETS_1);
		
		final String id = id();
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();

		assertDoesNotThrow(() ->
				new TestGenericTimestampTypeElement(id, includes, null, C14N, ts, null).marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertEquals(2, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Include").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ReferenceInfo").getLength());		
	}
	
	@Test
	void testRefInfo() throws ParserConfigurationException {
		final List<ReferenceInfo> refs = new ArrayList<>();		
		refs.add(REF_1);
		refs.add(REF_2);
		
		final List<XMLTimeStamp> ts = new ArrayList<>();
		ts.add(XTS_1);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() ->
		new TestGenericTimestampTypeElement(null, null, refs, null, null, ts).marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();

		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "Include").getLength());
		assertEquals(2, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "ReferenceInfo").getLength());		
	}
	
	@Test
	void testId() throws ParserConfigurationException {
		final String id = id();
		final List<XMLTimeStamp> ts = new ArrayList<>();
		ts.add(XTS_1);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() ->
		new TestGenericTimestampTypeElement(id, null, null, null, null, ts).marshal(writer, "", context));
		
		assertEquals(id, writer.getCreatedElement().getAttribute("Id"));		
	}

	@Test
	void testNoId() throws ParserConfigurationException {		
		final List<XMLTimeStamp> ts = new ArrayList<>();
		ts.add(XTS_1);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() ->
				new TestGenericTimestampTypeElement(null, null, null, null, null, ts).marshal(writer, "", context));
		
		assertNull(writer.getCreatedElement().getAttributeNode("Id"));		
	}
	
	@Test
	void testNoC14N() throws ParserConfigurationException {		
		final List<XMLTimeStamp> ts = new ArrayList<>();
		ts.add(XTS_1);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() ->
		new TestGenericTimestampTypeElement(null, null, null, null, null, ts).marshal(writer, "", context));
		
		assertEquals(0, writer.getCreatedElement().getElementsByTagNameNS(XMLSignature.XMLNS, "CanonicalizationMethod").getLength());		
	}
	
	@Test
	void testC14N() throws ParserConfigurationException {		
		final List<XMLTimeStamp> ts = new ArrayList<>();
		ts.add(XTS_1);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() ->
		new TestGenericTimestampTypeElement(null, null, null, C14N, null, ts).marshal(writer, "", context));
		
		assertEquals(1, writer.getCreatedElement().getElementsByTagNameNS(XMLSignature.XMLNS, "CanonicalizationMethod").getLength());		
	}
	
	@Test
	void testEncapsulatedTS() throws ParserConfigurationException {
		final List<EncapsulatedTimeStamp> ts = new ArrayList<>();
		ts.add(ETS_1);
		ts.add(ETS_2);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();

		assertDoesNotThrow(() ->
				new TestGenericTimestampTypeElement(null, null, null, null, ts, null).marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertEquals(2, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "EncapsulatedTimeStamp").getLength());
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "XMLTimeStamp").getLength());		
	}
	
	@Test
	void testXMLTS() throws ParserConfigurationException {
		final List<XMLTimeStamp> ts = new ArrayList<>();
		ts.add(XTS_1);
		ts.add(XTS_2);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() ->
				new TestGenericTimestampTypeElement(null, null, null, null, null, ts).marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertEquals(0, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "EncapsulatedTimeStamp").getLength());
		assertEquals(2, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "XMLTimeStamp").getLength());		
	}
	
	
	@Test
	void testBothTS() throws ParserConfigurationException {
		final List<EncapsulatedTimeStamp> ets = new ArrayList<>();
		ets.add(ETS_1);
		ets.add(ETS_2);
		final List<XMLTimeStamp> xts = new ArrayList<>();
		xts.add(XTS_1);
		
		DOMXMLWriter writer = new DOMXMLWriter();
		TestCryptoContext context = new TestCryptoContext();
		
		assertDoesNotThrow(() ->
				new TestGenericTimestampTypeElement(null, null, null, null, ets, xts).marshal(writer, "", context));
		
		Element created = writer.getCreatedElement();
		
		assertEquals(2, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "EncapsulatedTimeStamp").getLength());
		assertEquals(1, created.getElementsByTagNameNS(Constants.XADES_132_NS_URI, "XMLTimeStamp").getLength());		
	}
	
	@Test
	void testEquals() throws Exception {
		final List<Include> includes1 = new ArrayList<>();		
		final List<Include> includes2 = new ArrayList<>();		
		includes1.add(INCLUDE_1);
		includes2.add(INCLUDE_1);
		includes1.add(INCLUDE_2);		
		final List<ReferenceInfo> refs1 = new ArrayList<>();		
		final List<ReferenceInfo> refs2 = new ArrayList<>();		
		refs1.add(REF_1);
		refs2.add(REF_1);
		refs1.add(REF_2);
		DOMCanonicalizationMethod c14n2 = (DOMCanonicalizationMethod) 
												XMLSignatureFactory.getInstance("DOM", "ApacheXMLDSig")
														.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, 
																					(C14NMethodParameterSpec) null);
		final List<EncapsulatedTimeStamp> ets1 = new ArrayList<>();
		final List<EncapsulatedTimeStamp> ets2 = new ArrayList<>();
		ets1.add(ETS_1);
		ets2.add(ETS_1);
		ets1.add(ETS_2);
		final List<XMLTimeStamp> xts1 = new ArrayList<>();
		final List<XMLTimeStamp> xts2 = new ArrayList<>();
		xts1.add(XTS_1);
		xts2.add(XTS_1);
		xts2.add(XTS_2);
		
		final String id1 = id();
		final String id2 = id();
		
		TestGenericTimestampTypeElement e1 = new TestGenericTimestampTypeElement(id1, includes1, refs1, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e2 = new TestGenericTimestampTypeElement(id1, includes1, refs1, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e3 = new TestGenericTimestampTypeElement(null, includes1, refs1, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e4 = new TestGenericTimestampTypeElement(id2, includes1, refs1, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e5 = new TestGenericTimestampTypeElement(id1, null, refs1, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e6 = new TestGenericTimestampTypeElement(id1, includes2, refs1, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e7 = new TestGenericTimestampTypeElement(id1, includes1, null, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e8 = new TestGenericTimestampTypeElement(id1, includes1, refs2, C14N, ets1, xts1);
		TestGenericTimestampTypeElement e9 = new TestGenericTimestampTypeElement(id1, includes1, refs1, null, ets1, xts1);
		TestGenericTimestampTypeElement e10 = new TestGenericTimestampTypeElement(id1, includes1, refs1, c14n2, ets1, xts1);
		TestGenericTimestampTypeElement e11 = new TestGenericTimestampTypeElement(id1, includes1, refs1, C14N, null, xts1);
		TestGenericTimestampTypeElement e12 = new TestGenericTimestampTypeElement(id1, includes1, refs1, C14N, ets2, xts1);
		TestGenericTimestampTypeElement e13 = new TestGenericTimestampTypeElement(id1, includes1, refs1, C14N, ets1, null);
		TestGenericTimestampTypeElement e14 = new TestGenericTimestampTypeElement(id1, includes1, refs1, C14N, ets1, xts2);
		
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertFalse(e1.equals(e4));
		assertFalse(e1.equals(e5));
		assertFalse(e1.equals(e6));
		assertFalse(e1.equals(e7));
		assertFalse(e1.equals(e8));
		assertFalse(e1.equals(e9));
		assertFalse(e1.equals(e10));
		assertFalse(e1.equals(e11));
		assertFalse(e1.equals(e12));
		assertFalse(e1.equals(e13));
		assertFalse(e1.equals(e14));
	}
	
	
	
	private static String id() { return "#" + UUID.randomUUID().toString(); }
	
	static class TestGenericTimestampTypeElement extends AbstractGenericTimestampTypeElement {

		protected TestGenericTimestampTypeElement(String id, List<Include> includes, List<ReferenceInfo> referenceInfos,
				DOMCanonicalizationMethod c14nMethod, List<EncapsulatedTimeStamp> encapsulatedTS,
				List<XMLTimeStamp> xmlTS) {
			super(id, includes, referenceInfos, c14nMethod, encapsulatedTS, xmlTS);
		}

		@Override
		protected QName getName() {
			return new QName("Test");
		}
		
	}
}
