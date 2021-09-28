package me.rockintuna.sailinglog.config.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalController {

    @ExceptionHandler
    public ResponseEntity<?> eventErrorHandler(ArticleNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

}
