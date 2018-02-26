package sample;
/**
 * Created by Francisco Enriquez on 10/20/17.
 */

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordVerifyField;

    @FXML
    private TextArea dumpTextArea;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private void login() {
        String tUser = usernameField.getText();
        if (accountMap.containsKey(tUser)) {
            String tUserSalt = accountMap.get(tUser).getSalt();
            String tUserHash = accountMap.get(tUser).getPasswordHash();
            String tPassword = tUserSalt.concat(passwordField.getText());

            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException ex) {
                System.out.println("Failed to get Message Digest Instance.");
                return;
            }

            byte [] hash = digest.digest(tPassword.getBytes(StandardCharsets.UTF_8));
            String hashString = Base64.getEncoder().encodeToString(hash);

            Dialog<String> dialog = new Dialog<>();
            if (tUserHash.compareTo(hashString) == 0) {
                ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType);
                dialog.setContentText("Login success!");
                System.out.println("Login Success");
            } else {
                ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType);
                dialog.setContentText("Login failed!");
                System.out.println("Login Failed");
            }
            dialog.showAndWait();
            Platform.runLater(() -> {
                usernameField.clear();
                passwordField.clear();
                passwordVerifyField.clear();
            });
        }
    }

    @FXML
    private void createAccount() {
        Dialog<String> dialog = new Dialog<>();
        if (accountMap.containsKey(usernameField.getText())) {
            ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(buttonType);
            dialog.setContentText("Failed to create account.\nUsername not available!");
            dialog.showAndWait();
            System.out.println("User " + usernameField.getText() + " already exists");
            return;
        }
        if(passwordField.getText().compareTo(passwordVerifyField.getText()) != 0) {
            ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(buttonType);
            dialog.setContentText("Failed to create account.\nPassword Mismatch!");
            dialog.showAndWait();
            return;
        }
        byte[] saltByteArray = new byte[16];
        this.secureRandom.nextBytes(saltByteArray);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Failed to get Message Digest Instance.");
            return;
        }

        String saltString = saltByteArray.toString();
        String saltAndPasswordString = saltString.concat(passwordField.getText());

        byte[] hash = digest.digest(saltAndPasswordString.getBytes(StandardCharsets.UTF_8));
        String hashString = Base64.getEncoder().encodeToString(hash);
        accountMap.put(usernameField.getText(), new Account(usernameField.getText(), saltString, hashString));
        ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        dialog.setContentText("Create account " + usernameField.getText() + " success!");
        System.out.println("Account created. username  = " + usernameField.getText() + " salt = " + saltString + " hash = " + hashString);
        dialog.showAndWait();
        Platform.runLater(() -> {
            usernameField.clear();
            passwordField.clear();
            passwordVerifyField.clear();
        });
    }

    @FXML
    private void changePassword() {
        Dialog<String> dialog = new Dialog<>();
        ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        if (!accountMap.containsKey(usernameField.getText())) {
            System.out.println("User could not be verified");
            dialog.setContentText("User could not be verified.");
            return;
        } else if (passwordField.getText().compareTo(passwordVerifyField.getText()) != 0) {
            System.out.println("Password mismatch");
            dialog.setContentText("Password mismatch!");
        } else {
            String tUser = usernameField.getText();
            String tUserSaltString = accountMap.get(tUser).getSalt();
            String saltAndPasswordString = tUserSaltString.concat(passwordField.getText());

            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException ex) {
                System.out.println("Failed to get Message Digest Instance.");
                return;
            }

            byte[] hash = digest.digest(saltAndPasswordString.getBytes(StandardCharsets.UTF_8));
            String hashString = Base64.getEncoder().encodeToString(hash);
            accountMap.get(usernameField.getText()).setPasswordHash(hashString);
            Platform.runLater(() -> {
                usernameField.clear();
                passwordField.clear();
                passwordVerifyField.clear();
            });
            dialog.setContentText("Password changed successfully!");
            System.out.println("Password changed successfully");
        }
        dialog.showAndWait();

    }

    @FXML
    private void deleteAccount() {
        String tUser = usernameField.getText();
        Dialog<String> dialog = new Dialog<>();
        if (accountMap.containsKey(tUser)) {
            String tUserSalt = accountMap.get(tUser).getSalt();
            String tUserHash = accountMap.get(tUser).getPasswordHash();
            String tPassword = tUserSalt.concat(passwordField.getText());

            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException ex) {
                System.out.println("Failed to get Message Digest Instance.");
                return;
            }

            byte [] hash = digest.digest(tPassword.getBytes(StandardCharsets.UTF_8));
            String hashString = Base64.getEncoder().encodeToString(hash);
            if (tUserHash.compareTo(hashString) == 0) {
                ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType);
                dialog.setContentText("Account " + usernameField.getText() + " deleted success!");
                accountMap.remove(tUser);
                Platform.runLater(() -> {
                    usernameField.clear();
                    passwordField.clear();
                    passwordVerifyField.clear();
                });
            } else {
                ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType);
                dialog.setContentText("Delete account failed!\nUsername and password could not be verified.");
                System.out.println("Delete account failed!\nUsername and password could not be verified.");
            }
        } else {
            ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(buttonType);
            dialog.setContentText("Account '" + usernameField.getText() + "' does not exist!");
        }
        dialog.showAndWait();
    }

    @FXML
    private void debug() {
        Platform.runLater(() -> {
            dumpTextArea.clear();
            dumpTextArea.appendText("Username\t\tSalt\t\t\tPassword Hash\n");
        });
        for (Account a: accountMap.values()) {
            Platform.runLater(() -> dumpTextArea.appendText(a.getUsername() + "\t\t\t" + a.getSalt() + "\t\t\t" + a.getPasswordHash() + "\n"));
        }
    }

    private HashMap<String, Account> accountMap;
    private final SecureRandom secureRandom;

    public Controller() {
        secureRandom = new SecureRandom();
        accountMap = new HashMap<>(10);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteAccountButton.setDisable(true);
        changePasswordButton.setDisable(true);
        usernameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!usernameField.getText().isEmpty()) {
                    deleteAccountButton.setDisable(false);
                } else {
                    deleteAccountButton.setDisable(true);
                }
            }
        });
        passwordVerifyField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!passwordField.getText().isEmpty() && !passwordVerifyField.getText().isEmpty()) {
                    changePasswordButton.setDisable(false);
                } else {
                    changePasswordButton.setDisable(true);
                }
            }
        });
    }

}
