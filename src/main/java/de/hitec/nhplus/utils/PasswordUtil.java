package de.hitec.nhplus.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Hilfsklasse für das Hashing von Passwörtern mit SHA-256 und Salt.
 *
 * <p>Diese Klasse wird im Login-System von NHPlus (AB 04) eingesetzt, damit
 * Passwörter nicht im Klartext in der Datenbank landen. Speicherung von
 * Klartext-Passwörtern wäre ein Verstoß gegen Art. 32 DSGVO (Stand der
 * Technik beim Zugangsschutz).</p>
 *
 * <h3>Warum ein Salt?</h3>
 * <p>Ohne Salt hätten zwei Nutzer mit gleichem Passwort den gleichen Hash
 * in der Datenbank. Ein Angreifer könnte mit einer vorberechneten Tabelle
 * (<em>Rainbow Table</em>) gängige Passwörter nachschlagen. Der Salt macht
 * jeden Hash einzigartig — auch bei identischem Klartext.</p>
 *
 * <h3>Lehrhinweis</h3>
 * <p>In Produktivsystemen sollten für Passwort-Speicherung <em>Key
 * Derivation Functions</em> wie <b>bcrypt</b>, <b>scrypt</b> oder
 * <b>Argon2</b> verwendet werden. SHA-256 ist eine schnelle Hash-Funktion;
 * moderne GPUs können Milliarden SHA-256-Hashes pro Sekunde berechnen, was
 * Brute-Force-Angriffe beschleunigt. KDFs sind bewusst rechenintensiv
 * (Kosten-Faktor) und dadurch deutlich resistenter.</p>
 *
 * <p>Für die Lernsituation ist SHA-256 mit Salt ausreichend, um das
 * grundlegende Prinzip zu zeigen. Ein echtes System würde bcrypt nutzen.</p>
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH_BYTES = 16;

    private PasswordUtil() {
        // Utility-Klasse — keine Instanziierung
    }

    /**
     * Erzeugt einen neuen zufälligen Salt als Hex-String.
     *
     * <p>Beim Anlegen eines Nutzers wird einmal ein Salt erzeugt und
     * zusammen mit dem berechneten Hash in der Datenbank gespeichert.</p>
     *
     * @return 32 Hex-Zeichen (16 Byte Zufallsdaten)
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        return toHex(salt);
    }

    /**
     * Berechnet den SHA-256-Hash eines Passworts mit Salt.
     *
     * @param password Klartext-Passwort
     * @param salt     Salt als Hex-String (aus {@link #generateSalt()})
     * @return Hash als Hex-String
     * @throws IllegalStateException falls SHA-256 in der JVM nicht verfügbar ist
     */
    public static String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 ist in dieser JVM nicht verfügbar.", e);
        }
    }

    /**
     * Prüft, ob ein eingegebenes Passwort zum gespeicherten Hash passt.
     *
     * <p>Typische Nutzung im Login: Benutzername aus der DB holen, Salt
     * und erwarteten Hash lesen, dann diese Methode aufrufen.</p>
     *
     * <p><b>Sicherheitshinweis:</b> {@code equalsIgnoreCase} ist <em>nicht
     * konstant in der Laufzeit</em> — bei einem ungleichen Vergleich bricht
     * Java ab, sobald das erste unterschiedliche Zeichen gefunden ist. Ein
     * Angreifer könnte daraus über sehr genaue Zeitmessungen Rückschlüsse
     * ziehen (<em>Timing-Attack</em>). Produktivsysteme nutzen deshalb einen
     * konstant-zeit-Vergleich wie {@link java.security.MessageDigest#isEqual(byte[], byte[])}.
     * Für die Lernsituation reicht der einfache Vergleich.</p>
     *
     * @param password     Klartext-Passwort aus dem Login-Formular
     * @param salt         Salt, der beim Anlegen des Nutzers gespeichert wurde
     * @param expectedHash Hash-Wert aus der Datenbank
     * @return {@code true}, wenn Passwort + Salt den erwarteten Hash ergeben
     */
    public static boolean verify(String password, String salt, String expectedHash) {
        if (password == null || salt == null || expectedHash == null) {
            return false;
        }
        byte[] actualBytes = hash(password, salt).getBytes(StandardCharsets.UTF_8);
        byte[] expectedBytes = expectedHash.toLowerCase().getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(actualBytes, expectedBytes);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
