package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Result {
    private int id;
    private Date date;
    @lombok.Builder.Default
    private Integer attempts3Points = 0;
    @lombok.Builder.Default
    private Integer scored3Points = 0;
    @lombok.Builder.Default
    private Integer attempts2Points = 0;
    @lombok.Builder.Default
    private Integer scored2Points = 0;
    @lombok.Builder.Default
    private Integer attempts1Points = 0;
    @lombok.Builder.Default
    private Integer scored1Points = 0;
    @lombok.Builder.Default
    private Integer scoreIn = 0;
    @lombok.Builder.Default
    private Integer scoreOut = 0;
    private String season;
    private boolean homeMatch;
    private Command command;
    private Tournament tournament;
}
