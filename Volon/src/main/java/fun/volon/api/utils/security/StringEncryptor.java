package fun.volon.api.utils.security;

/**
 * Utility for encrypting string constants at build time.
 * Use this to generate encrypted versions of sensitive strings.
 * 
 * Usage:
 * 1. Run: java StringEncryptor "your sensitive string"
 * 2. Copy the output encrypted string
 * 3. Replace in code: NativeCrypto.loadString("encrypted output")
 */
public class StringEncryptor {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java StringEncryptor <string to encrypt>");
            System.out.println("Example: java StringEncryptor \"My Secret API Key\"");
            return;
        }

        String plaintext = String.join(" ", args);
        String encrypted = NativeCrypto.encrypt(plaintext);
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("String Encryption Tool");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Plaintext:  " + plaintext);
        System.out.println("Encrypted:  " + encrypted);
        System.out.println("");
        System.out.println("Usage in code:");
        System.out.println("String value = NativeCrypto.loadString(\"" + encrypted + "\");");
        System.out.println("═══════════════════════════════════════");
        
        // Verify decryption works
        String decrypted = NativeCrypto.decrypt(encrypted);
        if (decrypted.equals(plaintext)) {
            System.out.println("✓ Verification successful");
        } else {
            System.out.println("✗ Verification failed!");
        }
    }
}
