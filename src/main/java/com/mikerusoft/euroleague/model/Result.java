package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class Result {
    private int id;
    private Date date;
    private Integer attempts3Points;
    private Integer scored3Points;
    private Integer attempts2Points;
    private Integer scored2Points;
    private Integer attempts1Points;
    private Integer scored1Points;
    private Integer scoreIn;
    private Integer scoreOut;
    private String season;
    private boolean homeMatch;
    private Command command;
    private Tournament tournament;
}
