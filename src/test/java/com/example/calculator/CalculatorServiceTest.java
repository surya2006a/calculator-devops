package com.example.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService();

    @Test
    void addsTwoNumbers() {
        assertEquals(0, new BigDecimal("5").compareTo(service.add(new BigDecimal("2"), new BigDecimal("3"))));
    }

    @Test
    void addsDecimalsPrecisely() {
        assertEquals(0, new BigDecimal("0.3").compareTo(service.add(new BigDecimal("0.1"), new BigDecimal("0.2"))));
    }

    @Test
    void subtractsTwoNumbers() {
        assertEquals(0, new BigDecimal("-1").compareTo(service.subtract(new BigDecimal("2"), new BigDecimal("3"))));
    }

    @Test
    void multipliesTwoNumbers() {
        assertEquals(0, new BigDecimal("42").compareTo(service.multiply(new BigDecimal("6"), new BigDecimal("7"))));
    }

    @Test
    void dividesTwoNumbers() {
        assertEquals(0, new BigDecimal("2.5").compareTo(service.divide(new BigDecimal("10"), new BigDecimal("4"))));
    }

    @Test
    void divideByZeroThrows() {
        ArithmeticException ex = assertThrows(ArithmeticException.class,
                () -> service.divide(BigDecimal.ONE, BigDecimal.ZERO));
        assertEquals("Division by zero is not allowed", ex.getMessage());
    }
}
