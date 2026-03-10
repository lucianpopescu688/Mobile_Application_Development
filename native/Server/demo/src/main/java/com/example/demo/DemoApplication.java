package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootApplication
@RestController
public class DemoApplication {

	private final List<Map<String, Object>> events = new CopyOnWriteArrayList<>();
	private static final int DELAY = 500;

	public static void main(String[] args) {
		System.setProperty("server.port", "3000");
		SpringApplication.run(DemoApplication.class, args);
	}

	private void simulateDelay() {
		try { Thread.sleep(DELAY); } catch (InterruptedException e) {}
	}

	// CHANGED: Accepts an action string
	private void notifyClients(String action) {
		System.out.println("[SERVER] Broadcasting: " + action);
		MyWebSocketHandler.broadcast(action);
	}

	@GetMapping("/events")
	public List<Map<String, Object>> getEvents() {
		simulateDelay();
		return events;
	}

	@PostMapping("/event")
	public ResponseEntity<Map<String, Object>> addEvent(@RequestBody Map<String, Object> event) {
		simulateDelay();
		events.add(event);

		notifyClients("created"); // <--- Specific Message

		return ResponseEntity.ok(event);
	}

	@DeleteMapping("/event/{id}")
	public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable String id) {
		simulateDelay();
		boolean removed = events.removeIf(e -> id.equals(e.get("id")));
		if (removed) {
			notifyClients("deleted"); // <--- Specific Message
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Not found"));
	}

	@PutMapping("/event/{id}")
	public ResponseEntity<Map<String, Object>> updateEvent(@PathVariable String id, @RequestBody Map<String, Object> newData) {
		simulateDelay();
		for (int i = 0; i < events.size(); i++) {
			if (id.equals(events.get(i).get("id"))) {
				events.set(i, newData);

				notifyClients("updated"); // <--- Specific Message

				return ResponseEntity.ok(newData);
			}
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Not found"));
	}
}