package com.childstudy.tambola.model;

public class WinnersModel {

    private String claimType;

    public String getClaimType () {
        return claimType;
    }

    public void setClaimType ( String claimType ) {
        this.claimType = claimType;
    }

    public String getClaimPoints () {
        return claimPoints;
    }

    public void setClaimPoints ( String claimPoints ) {
        this.claimPoints = claimPoints;
    }

    public String getWinnerName () {
        return winnerName;
    }

    public void setWinnerName ( String winnerName ) {
        this.winnerName = winnerName;
    }

    private String claimPoints;

    public WinnersModel ( String claimType , String claimPoints , String winnerName ) {
        this.claimType = claimType;
        this.claimPoints = claimPoints;
        this.winnerName = winnerName;
    }

    private String winnerName;


}
