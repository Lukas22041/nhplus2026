package de.hitec.nhplus.model;

/**
 * Represents an application user who can authenticate against the NHPlus system.
 * A user is persisted with a unique id, a username, a password hash and the salt
 * that was used to calculate the hash.
 */
public class User {

    private long id;
    private String username;
    private String passwordHash;
    private String salt;

    /**
     * Creates a new user object that is not persisted yet.
     *
     * @param username Username of the user.
     * @param passwordHash Hashed password of the user.
     * @param salt Salt that was used to hash the password.
     */
    public User(String username, String passwordHash, String salt) {
        this(0, username, passwordHash, salt);
    }

    /**
     * Creates a persisted user object.
     *
     * @param id Database id of the user.
     * @param username Username of the user.
     * @param passwordHash Hashed password of the user.
     * @param salt Salt that was used to hash the password.
     */
    public User(long id, String username, String passwordHash, String salt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}

