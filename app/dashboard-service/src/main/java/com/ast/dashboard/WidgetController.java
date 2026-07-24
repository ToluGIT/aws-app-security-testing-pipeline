package com.ast.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Sample microservice endpoints for the AST pipeline project.
 *
 * /widget1 is DELIBERATELY vulnerable to SQL injection: the "name" query
 * parameter is concatenated directly into a raw SQL string instead of using
 * a parameterized query. This is intentional. This project exists to prove
 * that a vulnerability like this gets caught twice: once by OWASP ZAP during
 * the pipeline's DAST stage, and again by OpenRASP blocking the actual
 * exploit attempt at runtime, in Phase 4's adversarial test. Do not "fix"
 * this endpoint by parameterizing the query; that would remove the exact
 * vulnerability class this project is built to demonstrate defense against.
 *
 * /widget2 shows the parameterized, non-vulnerable equivalent for contrast.
 */
@RestController
public class WidgetController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WidgetRepository widgetRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of(
            "service", "dashboard-service",
            "widgets", widgetRepository.count(),
            "status", "up"
        );
    }

    // Intentionally vulnerable to SQL injection. See class-level Javadoc.
    @GetMapping("/widget1")
    public List<Map<String, Object>> widget1(@RequestParam String name) {
        String sql = "SELECT id, name, owner FROM widget WHERE name = '" + name + "'";
        return jdbcTemplate.queryForList(sql);
    }

    // Parameterized equivalent, not vulnerable to the same injection.
    @GetMapping("/widget2")
    public List<Widget> widget2(@RequestParam String owner) {
        return widgetRepository.findAll().stream()
            .filter(w -> w.getOwner().equals(owner))
            .toList();
    }
}
