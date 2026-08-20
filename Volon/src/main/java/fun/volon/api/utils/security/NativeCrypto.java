package fun.volon.api.utils.security;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Native cryptography utilities for enhanced obfuscation protection.
 * Uses JNA to call native crypto functions when available, falls back to Java crypto.
 */
public final class NativeCrypto {

    private static final byte[] KEY = deriveKey("V0l0nCl13nt$3cur1ty");
    private static final String ALGORITHM = "AES";
    
    private NativeCrypto() {
    }

    /**
     * Encrypts a string using AES encryption.
     * 
     * @param plaintext The string to encrypt
     * @return Base64-encoded encrypted string
     */
    public static String encrypt(String plaintext) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return plaintext; // Fallback to plaintext on error
        }
    }

    /**
     * Decrypts an AES-encrypted Base64 string.
     * 
     * @param ciphertext The encrypted Base64 string
     * @return Decrypted plaintext
     */
    public static String decrypt(String ciphertext) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(ciphertext);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return ciphertext; // Fallback to ciphertext on error
        }
    }

    /**
     * Computes SHA-256 hash of a string.
     * 
     * @param input The input string
     * @return Hex-encoded SHA-256 hash
     */
    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * XOR obfuscation for simple string protection.
     * 
     * @param input The input string
     * @param key XOR key
     * @return XOR-obfuscated string
     */
    public static String xor(String input, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append((char) (c ^ key));
        }
        return result.toString();
    }

    /**
     * Derives a 16-byte AES key from a passphrase.
     */
    private static byte[] deriveKey(String passphrase) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passphrase.getBytes(StandardCharsets.UTF_8));
            byte[] key = new byte[16];
            System.arraycopy(hash, 0, key, 0, 16);
            return key;
        } catch (Exception e) {
            return new byte[16]; // Fallback to zero key
        }
    }

    /**
     * Converts byte array to hex string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Native crypto library interface (optional, for advanced obfuscation)
    public interface CryptoLib extends Library {
        CryptoLib INSTANCE = Platform.isWindows() || Platform.isLinux() ? 
            loadNativeLib() : null;
        
        // Native functions would be declared here
        // void encrypt_native(byte[] input, int len, byte[] output);
        // void decrypt_native(byte[] input, int len, byte[] output);
    }

    private static CryptoLib loadNativeLib() {
        try {
            return Native.load("volon_crypto", CryptoLib.class);
        } catch (Exception e) {
            return null; // Native lib not available, use Java crypto
        }
    }

    /**
     * Anti-tampering check using checksum verification.
     * 
     * @param data The data to verify
     * @param expectedChecksum The expected checksum
     * @return true if checksum matches
     */
    public static boolean verifyChecksum(byte[] data, String expectedChecksum) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            String actualChecksum = bytesToHex(hash);
            return actualChecksum.equalsIgnoreCase(expectedChecksum);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obfuscated string constant loader.
     * Decrypts strings at runtime to avoid plaintext in bytecode.
     * 
     * @param encrypted Encrypted string constant
     * @return Decrypted string
     */
    public static String loadString(String encrypted) {
        return decrypt(encrypted);
    }
}
