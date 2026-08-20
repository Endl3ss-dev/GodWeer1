package fun.volon.api.utils.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * MILITARY-GRADE bytecode encryption system.
 * Multi-layer encryption with AES-256 + XOR + compression + obfuscation.
 * Designed to resist AI decompilation tools.
 */
public final class BytecodeEncryption {

    private static final byte[] MASTER_KEY = deriveKey("V0l0n$M1l1t4ry#3ncrypt10n@2024", 32);
    private static final byte[] SECONDARY_KEY = deriveKey("$3c0nd4ry&L4y3r^Pr0t3ct!0n", 32);
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final SecureRandom SECURE_RNG = new SecureRandom();

    private BytecodeEncryption() {
    }

    /**
     * Multi-layer encryption:
     * 1. Compress data (DEFLATE)
     * 2. XOR with rotating key
     * 3. AES-256-CBC encryption (layer 1)
     * 4. XOR with secondary key
     * 5. AES-256-CBC encryption (layer 2)
     * 6. Base64 encode
     */
    public static String encryptMultiLayer(byte[] data) {
        try {
            // Layer 1: Compression
            byte[] compressed = compress(data);
            
            // Layer 2: XOR obfuscation (rotating key)
            byte[] xored1 = xorRotate(compressed, 0x42);
            
            // Layer 3: First AES encryption
            byte[] encrypted1 = aesEncrypt(xored1, MASTER_KEY);
            
            // Layer 4: Second XOR with different pattern
            byte[] xored2 = xorRotate(encrypted1, 0xAB);
            
            // Layer 5: Second AES encryption (different key)
            byte[] encrypted2 = aesEncrypt(xored2, SECONDARY_KEY);
            
            // Layer 6: Base64 encoding
            return Base64.getEncoder().encodeToString(encrypted2);
            
        } catch (Exception e) {
            throw new SecurityException("Encryption failed", e);
        }
    }

    /**
     * Multi-layer decryption (reverse of encryption)
     */
    public static byte[] decryptMultiLayer(String encrypted) {
        try {
            // Layer 1: Base64 decode
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            
            // Layer 2: Second AES decryption
            byte[] decrypted2 = aesDecrypt(decoded, SECONDARY_KEY);
            
            // Layer 3: Second XOR
            byte[] unxored2 = xorRotate(decrypted2, 0xAB);
            
            // Layer 4: First AES decryption
            byte[] decrypted1 = aesDecrypt(unxored2, MASTER_KEY);
            
            // Layer 5: First XOR
            byte[] unxored1 = xorRotate(decrypted1, 0x42);
            
            // Layer 6: Decompress
            return decompress(unxored1);
            
        } catch (Exception e) {
            throw new SecurityException("Decryption failed", e);
        }
    }

    /**
     * AES-256-CBC encryption with random IV
     */
    private static byte[] aesEncrypt(byte[] data, byte[] key) throws Exception {
        // Generate random IV
        byte[] iv = new byte[16];
        SECURE_RNG.nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        
        byte[] encrypted = cipher.doFinal(data);
        
        // Prepend IV to encrypted data
        ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
        buffer.put(iv);
        buffer.put(encrypted);
        return buffer.array();
    }

    /**
     * AES-256-CBC decryption with IV extraction
     */
    private static byte[] aesDecrypt(byte[] data, byte[] key) throws Exception {
        // Extract IV (first 16 bytes)
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[data.length - 16];
        System.arraycopy(data, 0, iv, 0, 16);
        System.arraycopy(data, 16, encrypted, 0, encrypted.length);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        
        return cipher.doFinal(encrypted);
    }

    /**
     * XOR with rotating key pattern
     */
    private static byte[] xorRotate(byte[] data, int initialKey) {
        byte[] result = new byte[data.length];
        int key = initialKey;
        
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key);
            // Rotate key: Fibonacci-like sequence
            key = ((key * 31) + (i % 256)) & 0xFF;
        }
        
        return result;
    }

    /**
     * DEFLATE compression
     */
    private static byte[] compress(byte[] data) throws Exception {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        
        byte[] buffer = new byte[data.length * 2];
        int compressedSize = deflater.deflate(buffer);
        deflater.end();
        
        byte[] compressed = new byte[compressedSize];
        System.arraycopy(buffer, 0, compressed, 0, compressedSize);
        return compressed;
    }

    /**
     * DEFLATE decompression
     */
    private static byte[] decompress(byte[] data) throws Exception {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        
        byte[] buffer = new byte[data.length * 10];
        int decompressedSize = inflater.inflate(buffer);
        inflater.end();
        
        byte[] decompressed = new byte[decompressedSize];
        System.arraycopy(buffer, 0, decompressed, 0, decompressedSize);
        return decompressed;
    }

    /**
     * Derives encryption key from passphrase using SHA-256
     */
    private static byte[] deriveKey(String passphrase, int length) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passphrase.getBytes(StandardCharsets.UTF_8));
            
            if (length == 32) {
                return hash; // SHA-256 produces 32 bytes
            }
            
            byte[] key = new byte[length];
            System.arraycopy(hash, 0, key, 0, Math.min(length, hash.length));
            return key;
            
        } catch (Exception e) {
            return new byte[length];
        }
    }

    /**
     * Polymorphic string encryption - different result each time
     */
    public static String encryptPolymorphic(String plaintext) {
        try {
            // Add random salt
            byte[] salt = new byte[8];
            SECURE_RNG.nextBytes(salt);
            
            byte[] data = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] combined = new byte[salt.length + data.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(data, 0, combined, salt.length, data.length);
            
            return encryptMultiLayer(combined);
            
        } catch (Exception e) {
            return plaintext;
        }
    }

    /**
     * Polymorphic string decryption
     */
    public static String decryptPolymorphic(String encrypted) {
        try {
            byte[] decrypted = decryptMultiLayer(encrypted);
            
            // Remove salt (first 8 bytes)
            byte[] data = new byte[decrypted.length - 8];
            System.arraycopy(decrypted, 8, data, 0, data.length);
            
            return new String(data, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            return encrypted;
        }
    }

    /**
     * Constant-time string comparison (anti-timing-attack)
     */
    public static boolean secureEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        if (aBytes.length != bBytes.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        
        return result == 0;
    }

    /**
     * Generate cryptographically secure random string
     */
    public static String generateSecureToken(int length) {
        byte[] bytes = new byte[length];
        SECURE_RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Integrity check using HMAC-SHA256
     */
    public static String generateHMAC(byte[] data, byte[] key) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] hmac = mac.doFinal(data);
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            return "";
        }
    }
}
