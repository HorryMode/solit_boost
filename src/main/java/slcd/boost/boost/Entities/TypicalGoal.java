package slcd.boost.boost.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
@Entity
public class TypicalGoal {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String goal;
    @NotNull
    private String critery;
    @NotNull
    private Float currentScore;
    @NotNull
    private String goalForScoreThree;
    @NotNull
    private String goalForScoreFour;
    @NotNull
    private String goalForScoreFive;
    @NotNull
    private Date endDate;

    public TypicalGoal() {
    }

    public TypicalGoal(String goal, String critery, Float currentScore, String goalForScoreThree, String goalForScoreFour, String goalForScoreFive, Date endDate) {
        this.goal = goal;
        this.critery = critery;
        this.currentScore = currentScore;
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
