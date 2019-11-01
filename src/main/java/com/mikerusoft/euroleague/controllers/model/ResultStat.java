package com.mikerusoft.euroleague.controllers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultStat {
    private int attempts3Points;
    private int scored3Points;
    private int attempts2Points;
    private int scored2Points;
    private int attempts1Points;
    private int scored1Points;
    private int scoreIn;
    private int scoreOut;

    public void increaseScoreIn(int scoreIn) {
        this.scoreIn = this.scoreIn + scoreIn;
    }

    public void increaseScoreOut(int scoreOut) {
        this.scoreOut = this.scoreOut + scoreOut;
    }

    public void increaseAttempts3Points(int attempts3Points) {
        this.attempts3Points = this.attempts3Points + attempts3Points;
    }

    public void increaseScored3Points(int scored3Points) {
        this.scored3Points = this.scored3Points + scored3Points;
    }

    public void increaseAttempts2Points(int attempts2Points) {
        this.attempts2Points = this.attempts2Points + attempts2Points;
    }

    public void increaseScored2Points(int scored2Points) {
        this.scored2Points = this.scored2Points + scored2Points;
    }

    public void increaseAttempts1Points(int attempts1Points) {
        this.attempts1Points = this.attempts1Points + attempts1Points;
    }

    public void increaseScored1Points(int scored1Points) {
        this.scored1Points = this.scored1Points + scored1Points;
    }
}
