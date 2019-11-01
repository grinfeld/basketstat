package com.mikerusoft.euroleague.controllers.model;

import com.mikerusoft.euroleague.model.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class ResultAvg {
    private double points3;
    private double points2;
    private double points1;
    private double scoreIn;
    private double scoreOut;

    public static ResultAvg aggregateResults(List<Result> lastResults) {
        ResultStat rs = new ResultStat();
        for (Result r : lastResults) {
            rs.increaseAttempts1Points(r.getAttempts1Points());
            rs.increaseScored1Points(r.getScored1Points());
            rs.increaseAttempts2Points(r.getAttempts2Points());
            rs.increaseScored2Points(r.getScored2Points());
            rs.increaseAttempts3Points(r.getAttempts3Points());
            rs.increaseScored3Points(r.getScored3Points());
            rs.increaseScoreIn(r.getScoreIn());
            rs.increaseScoreOut(r.getScoreOut());
        }

        return builder().points1(Math.round(10000d * rs.getScored1Points() / rs.getAttempts1Points()) / 100d)
                .points2(Math.round(10000d * rs.getScored2Points() / rs.getAttempts2Points()) / 100d)
                .points3(Math.round(10000d * rs.getScored3Points() / rs.getAttempts3Points()) / 100d)
                .scoreIn(Math.round(10d * rs.getScoreIn() / lastResults.size()) / 10d)
                .scoreOut(Math.round(10d * rs.getScoreOut() / lastResults.size()) / 10d)
                .build();
    }
}
