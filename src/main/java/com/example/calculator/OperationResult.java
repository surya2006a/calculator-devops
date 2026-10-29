package com.example.calculator;

import java.math.BigDecimal;

public record OperationResult(String operation, BigDecimal a, BigDecimal b, BigDecimal result) {
}
