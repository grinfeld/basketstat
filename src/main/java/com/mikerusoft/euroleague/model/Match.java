package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Match {
    private Integer id;
    private Date date;
    private String season;
    private Command awayCommand;
    private Command homeCommand;
    private Tournament tournament;
    private int scoreHome;
    private int scoreAway;
}
