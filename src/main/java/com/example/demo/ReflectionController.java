package com.example.demo;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.example.demo.ReflectionController.ReflectionControllerHints;

@RestController
@ImportRuntimeHints(ReflectionControllerHints.class)
public class ReflectionController {

    @GetMapping("/reflection")
    public String message() {
        return getMessage();
    }

    private String getMessage() {
        try {
            String className = Arrays.asList("com", "example", "demo", "Message").stream().collect(Collectors.joining("."));
            return (String) Class.forName(className).getDeclaredField("MESSAGE").get(null);
        } catch (Exception e) {
            return "Got an error: " + e.getMessage();
        }
    }

    static class ReflectionControllerHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(Message.class, type -> type.withField("MESSAGE"));
        }
    }

}