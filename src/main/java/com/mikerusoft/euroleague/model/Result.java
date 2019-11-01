package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;

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

    public static void assertResult(Result result) {
        int sunOfSplitScoreIn = result.getScored1Points() + result.getScored2Points() * 2 + result.getScored3Points() * 3;
        if (sunOfSplitScoreIn != result.getScoreIn()) {
            throw new IllegalArgumentException("Sum of scored points should be the same as score in");
        }

        if (result.getScored1Points() > result.getAttempts1Points()) {
            throw new IllegalArgumentException("Scored 1 point should be less or equal to attempts");
        }

        if (result.getScored2Points() > result.getAttempts2Points()) {
            throw new IllegalArgumentException("Scored 2 point should be less or equal to attempts");
        }

        if (result.getScored3Points() > result.getAttempts3Points()) {
            throw new IllegalArgumentException("Scored 3 point should be less or equal to attempts");
        }
    }

    public static Result normalizeData(Result result) {
        Date date = result.getDate();
        Calendar instance = Calendar.getInstance(TimeZone.getDefault());
        instance.setTime(date);

        return result.toBuilder().date(new Date(instance.getTime().getTime())).build();
    }
}
