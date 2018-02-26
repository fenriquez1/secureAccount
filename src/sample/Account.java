package sample;

/**
 * Created by Francisco Enriquez on 10/20/17.
 */
public class Account {

    private String username;
    private String salt;
    private String passwordHash;

    public Account(String pUsername, String pSalt, String pPasswordHash) {
        this.username = pUsername;
        this.salt = pSalt;
        this.passwordHash = pPasswordHash;
    }

    public String getUsername() {
        return username;
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
