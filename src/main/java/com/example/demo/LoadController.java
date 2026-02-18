package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoadController {

    @GetMapping("/load")
    public Map<String, Object> gcStress(@RequestParam(defaultValue = "1000") int count) {
        long start = System.nanoTime();
        List<Map<String, String>> trash = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, String> obj = new HashMap<>();
            obj.put("id", UUID.randomUUID().toString());
            obj.put("index", String.valueOf(i));
            obj.put("payload", "x".repeat(256));
            trash.add(obj);
        }
        long elapsed = System.nanoTime() - start;

        Runtime rt = Runtime.getRuntime();
        Map<String, Object> result = new HashMap<>();
        result.put("objectsCreated", count);
        result.put("elapsedMs", elapsed / 1_000_000.0);
        result.put("heapUsedMB", (rt.totalMemory() - rt.freeMemory()) / (1024.0 * 1024));
        result.put("heapTotalMB", rt.totalMemory() / (1024.0 * 1024));
        return result;
    }

}
