package org.duuba.xades.examples;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.duuba.xades.XadesVersion;
import org.duuba.xades.builders.BasicEnvelopedSignatureBuilder;
import org.holodeckb2b.commons.security.KeystoreUtils;
import org.w3c.dom.Document;

/**
 * Example showing how the {@link BasicEnvelopedSignatureBuilder} can be used to create a XAdES B-B signature. Use
 * the other setter methods of the builder to specify values for qualifying properties or algorithms to use. 
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com) 
 */
public class EnvelopedBBExample {

	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			System.err.print("Missing arguments! You must supply the key pair to use, its password and document to sign.");
			System.exit(-1);
		}
		
		PrivateKeyEntry keyPair = null;
		try {
			keyPair = KeystoreUtils.readKeyPairFromPKCS12(Paths.get(args[0]), args[1]);
		} catch (CertificateException e) {
			System.err.println("Could not read the key pair from the specified file " + args[0]);
			System.exit(-1);
		}
		
		Document doc2sign = null;
		try (FileInputStream fis = new FileInputStream(args[2])) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			System.out.println("Reading document to sign");
			doc2sign = builderFactory.newDocumentBuilder().parse(fis);		
		} catch (Throwable t) {
			System.err.println("Could not read the document to sign from the specified file " + args[2]);
			t.printStackTrace();
			System.exit(-1);
		}
		
		try {
			System.out.println("Preparing XAdES signature");
			BasicEnvelopedSignatureBuilder builder = new BasicEnvelopedSignatureBuilder();
			builder.setXadesVersion(XadesVersion.EN_319_132_V111)
					.setKeyPair(keyPair)
					.setDocumentToSign(doc2sign)
					.setSignersLocation("Endless road", null, "Nowhere", null, "Universe");
			System.out.println("Creating XAdES signature");
			long start = System.currentTimeMillis();
			builder.build();
			long end = System.currentTimeMillis();
			System.out.println("Created XAdES signature in " + (end - start) + "ms");
		} catch (Exception e) {
			System.err.println("An error occurred while signing the document");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
