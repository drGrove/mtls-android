package com.dannygrove.mtls;

// Based off: https://stackoverflow.com/questions/37850134/certificate-enrollment-process

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.pkcs.PKCS10CertificationRequest;
import org.spongycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.spongycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

public class CsrHelper {
    private final static String DEFAULT_SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static class JCESigner implements ContentSigner {
        private static Map<String, AlgorithmIdentifier> ALGOS = new HashMap<String, AlgorithmIdentifier>();

        static {
            ALGOS.put("SHA256withRSA".toLowerCase(), new AlgorithmIdentifier(
                    new ASN1ObjectIdentifier("1.2.840.113549.1.1.11")));
            ALGOS.put("SHA1withRSA".toLowerCase(), new AlgorithmIdentifier(
                    new ASN1ObjectIdentifier("1.2.840.113549.1.1.5")));

        }

        private String mAlgo;
        private Signature signature;
        private ByteArrayOutputStream outputStream;

        public JCESigner(PrivateKey privateKey, String sigAlgo) {
            mAlgo = sigAlgo.toLowerCase();
            try {
                this.outputStream = new ByteArrayOutputStream();
                this.signature = Signature.getInstance(sigAlgo);
                this.signature.initSign(privateKey);
            } catch (InvalidKeyException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            AlgorithmIdentifier id = ALGOS.get(mAlgo);
            if (id == null) {
                throw new IllegalArgumentException("Does not support algo: " + mAlgo);
            }
            return id;
        }

        @Override
        public OutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public byte[] getSignature() {
            try {
                signature.update(outputStream.toByteArray());
                return signature.sign();
            } catch (GeneralSecurityException gse) {
                gse.printStackTrace();
                return null;
            }
        }
    }

    public static PKCS10CertificationRequest generateCRS(KeyPair keyPair, String cn) {
        ContentSigner signer = new JCESigner(keyPair.getPrivate(), DEFAULT_SIGNATURE_ALGORITHM);
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(
                new X500Name(cn),
                keyPair.getPublic()
        );
        PKCS10CertificationRequest csr = csrBuilder.build(signer);
        return csr;
    }

    public static String getCRSPublicBytesInPEM(PKCS10CertificationRequest csr) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(outputStream))) {
            pemWriter.writeObject(new PemObject("CERTIFICATE REQUEST", csr.getEncoded()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(outputStream.toByteArray());
    }
}
