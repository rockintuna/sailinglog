package me.rockintuna.sailinglog.config.exception;

public class PasswordNeverContainsUsernameException extends RuntimeException {
    public PasswordNeverContainsUsernameException(String message) {
        super(message);
    }
}
