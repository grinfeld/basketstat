package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class Aggregation {
    private String command;
    private String field;
    private double aggregatedValue;
}
