package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class Match {
    private String id;
    private Date date;
    private String season;
    private Tournament tournament;

    private CommandMatchStat awayCommand;
    private CommandMatchStat homeCommand;
}
