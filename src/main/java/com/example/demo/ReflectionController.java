package com.example.demo;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;

@RestController
public class ReflectionController {

    @GetMapping("/reflection")
    public String message() {
        return getMessage();
    }

    @RegisterReflectionForBinding(Message.class)
    private String getMessage() {
        try {
            String className = String.join(".", Arrays.asList("com", "example", "demo", "Message"));
            return (String) Class.forName(className).getDeclaredField("MESSAGE").get(null);
        } catch (Exception e) {
            return "Got an error: " + e.getMessage();
        }
    }

}