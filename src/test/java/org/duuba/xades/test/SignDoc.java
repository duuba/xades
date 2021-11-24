package org.duuba.xades.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.duuba.xades.XadesVersion;
import org.duuba.xades.builders.BasicEnvelopedSignatureBuilder;
import org.w3c.dom.Document;

public class SignDoc {

	public static void main(String[] args) {

		PrivateKeyEntry key = null;
		try {
			final String keyFile = SignDoc.class.getClassLoader().getResource("signkey.p12").getPath();
			final char[] keyPwd = "signer".toCharArray();
            final KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new java.io.FileInputStream(keyFile)) {
                keyStore.load(fis, keyPwd);
            }
            final Enumeration<String> aliases = keyStore.aliases();            
            final String alias = aliases.nextElement();
            if (aliases.hasMoreElements())
            	throw new KeyStoreException("More than one keypair in file!");
            key = (PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(keyPwd));
        } catch (NullPointerException | IOException | KeyStoreException | NoSuchAlgorithmException
                | CertificateException | UnrecoverableEntryException ex) {
            System.err.println("Cannot load keypair from specified PKCS#12 file");
            return;
        }

		Document doc2sign = null;
		String doc2signFile = SignDoc.class.getClassLoader().getResource("example_sed.xml").getPath();
		try (FileInputStream fis = new FileInputStream(doc2signFile)) {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);        
	        doc2sign = factory.newDocumentBuilder().parse(fis);
		} catch (Throwable t) {
			System.err.println("Could not read document to sign!");
			t.printStackTrace(System.err);
			return;
		}
		
		try {
			System.out.println("Signing the document");		
			new BasicEnvelopedSignatureBuilder()
					.setXadesVersion(XadesVersion.EN_319_132_V111)
					.setDocumentToSign(doc2sign)
					.setKeyPair(key)
					.build();
		} catch (Throwable t) {
			System.err.println("Error during signing of the document!");
			t.printStackTrace(System.err);
			return;
		}
		
		System.out.println("Write signed doc to file");
		String signedFile = doc2signFile.substring(0, doc2signFile.indexOf(".xml")) + "_signed.xml";
		try (FileOutputStream fos = new FileOutputStream(signedFile)) {
			Transformer transf = TransformerFactory.newInstance().newTransformer();		        
	        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");	
	        transf.setOutputProperty(OutputKeys.INDENT, "true");	       
	        transf.transform(new DOMSource(doc2sign), new StreamResult(fos));
		} catch (Throwable t) {
			System.err.println("Error writing signed document to file!");
			t.printStackTrace(System.err);
		}
	}

}
