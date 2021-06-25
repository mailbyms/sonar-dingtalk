package com.example.ding.sonar;

import lombok.Data;

@Data
public class Conditions {
    String metric;
    String operator;
    String value;
    String status;
    String errorThreshold;
}
