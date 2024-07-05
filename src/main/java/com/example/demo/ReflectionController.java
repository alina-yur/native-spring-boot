package com.example.demo;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class ReflectionController {

    @GetMapping("/reflection")
    public String message() {
        return getMessage();
    }

    @RegisterReflectionForBinding(Message.class)
    private String getMessage() {
        try {
            String className = Arrays.asList("com", "example", "demo", "Message").stream()
                    .collect(Collectors.joining("."));
            return (String) Class.forName(className).getDeclaredField("MESSAGE").get(null);
        } catch (Exception e) {
            return "Got an error: " + e.getMessage();
        }
    }

}