package com.childstudy.tambola.model;

public class ClaimSuccessModel {

    public String getPlayerName () {
        return playerName;
    }

    public void setPlayerName ( String playerName ) {
        this.playerName = playerName;
    }

    public String getPlayerId () {
        return playerId;
    }

    public void setPlayerId ( String playerId ) {
        this.playerId = playerId;
    }

    public String getTicketId () {
        return ticketId;
    }

    public void setTicketId ( String ticketId ) {
        this.ticketId = ticketId;
    }

    public ClaimSuccessModel ( String playerName , String playerId , String ticketId ) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.ticketId = ticketId;
    }

    public ClaimSuccessModel () {
    }

    private String playerName;
    private String playerId;
    private String ticketId;

}
