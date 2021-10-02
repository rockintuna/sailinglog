package me.rockintuna.sailinglog.config.exception;

public class UsernameExistException extends RuntimeException {
    public UsernameExistException(String message) {
        super(message);
    }
}
