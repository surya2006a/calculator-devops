package com.example.calculator;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public OperationResult add(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return new OperationResult("add", a, b, service.add(a, b));
    }

    @GetMapping("/subtract")
    public OperationResult subtract(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return new OperationResult("subtract", a, b, service.subtract(a, b));
    }

    @GetMapping("/multiply")
    public OperationResult multiply(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return new OperationResult("multiply", a, b, service.multiply(a, b));
    }

    @GetMapping("/divide")
    public OperationResult divide(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return new OperationResult("divide", a, b, service.divide(a, b));
    }
}
