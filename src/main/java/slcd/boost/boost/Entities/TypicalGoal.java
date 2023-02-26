package slcd.boost.boost.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Entity
public class TypicalGoal {
    @Id
    @GeneratedValue
    private Long id;

    private String goal;

    private String critery;

    private Float currentScore;

    private String goalForScoreThree;

    private String goalForScoreFour;

    private String goalForScoreFive;

    private Date endDate;

    public TypicalGoal() {
    }

    public TypicalGoal(String goal, String critery, String goalForScoreThree, String goalForScoreFour, String goalForScoreFive, Date endDate) {
        this.goal = goal;
        this.critery = critery;
        this.goalForScoreThree = goalForScoreThree;
        this.goalForScoreFour = goalForScoreFour;
        this.goalForScoreFive = goalForScoreFive;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getCritery() {
        return critery;
    }

    public void setCritery(String critery) {
        this.critery = critery;
    }

    public Float getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Float currentScore) {
        this.currentScore = currentScore;
    }

    public String getGoalForScoreThree() {
        return goalForScoreThree;
    }

    public void setGoalForScoreThree(String goalForScoreThree) {
        this.goalForScoreThree = goalForScoreThree;
    }

    public String getGoalForScoreFour() {
        return goalForScoreFour;
    }

    public void setGoalForScoreFour(String goalForScoreFour) {
        this.goalForScoreFour = goalForScoreFour;
    }

    public String getGoalForScoreFive() {
        return goalForScoreFive;
    }

    public void setGoalForScoreFive(String goalForScoreFive) {
        this.goalForScoreFive = goalForScoreFive;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
