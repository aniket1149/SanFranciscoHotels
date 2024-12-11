package server.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {
    private static final Logger logger = LogManager.getLogger(PasswordUtil.class);
    /**
     * Generates Salt required for hashing
     * */
    public static String generateSalt(){
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        logger.info("Salt Generated Successfully.");
        return bytesToHex(salt);
    }

    private static String bytesToHex(byte[] salt) {
        StringBuilder buf = new StringBuilder();
        for (byte b : salt) {
            buf.append(String.format("%02x", b));
        }
        logger.info("Converted bytes to hex and then to string success.");
        return buf.toString();
    }

    /**
     * Generates a random hashed password using salt.
     * @param password
     * @param salt
     * @return hashedString
     */
    public static String hashPassword(String password, String salt) {
        logger.info("Hashing the password now with provided password and salt");
        try {
            String saltedPwd = salt + password;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(saltedPwd.getBytes());
            return bytesToHex(hashBytes);
        }catch (NoSuchAlgorithmException e){
            logger.error(e);
            return "";
        }
    }

    /**
     * Verifies password and user authenticity
     * @param password
     * @param storedHash
     * @param salt
     * @return boolean
     */
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String hashedPassword = hashPassword(password, salt);
        return hashedPassword != null && hashedPassword.equals(storedHash);
    }
}
