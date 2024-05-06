package com.example.demo;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.FileCopyUtils;

import com.example.demo.ResourceController.ResourceControllerHints;

import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@ImportRuntimeHints(ResourceControllerHints.class)
public class ResourceController {

    @GetMapping("/resource")
    public String getResource() {
        Resource xml = new ClassPathResource("message.xml");
        try (InputStreamReader reader = new InputStreamReader(xml.getInputStream())) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            return "Failed to load resource";
        }
    }

    static class ResourceControllerHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerResource(new ClassPathResource("message.xml"));
        }
    }
}

