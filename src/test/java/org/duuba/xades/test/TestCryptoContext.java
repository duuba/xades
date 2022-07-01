package org.duuba.xades.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.xml.crypto.dom.DOMCryptoContext;

/**
 * Sub class of {@link DOMCryptoContext} which does not take any required parameters. Also has two additional methods
 * to get the test key pair or certificate.
 * 
 * @author Sander Fieten (sander at chasquis-messaging.com)
 */
public class TestCryptoContext extends DOMCryptoContext {
	
	public PrivateKeyEntry getKeyPair() {
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
            return (PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(keyPwd));
        } catch (NullPointerException | IOException | KeyStoreException | NoSuchAlgorithmException
                | CertificateException | UnrecoverableEntryException ex) {
            throw new IllegalStateException("Test keypair not available");
        }		
	}
	
	public X509Certificate getCertificate() {
		return (X509Certificate) getKeyPair().getCertificate();
	}
}
