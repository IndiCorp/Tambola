package com.childstudy.tambola.model;

public class PlayerModel {

    public PlayerModel () {
    }

    public PlayerModel ( String playerName , String totalPrice , String ticketQuantity , String playerImageUrl ) {
        this.playerName = playerName;
        this.totalPrice = totalPrice;
        this.ticketQuantity = ticketQuantity;
        this.playerImageUrl = playerImageUrl;
    }


    public String getPlayerId () {
        return playerId;
    }

    public void setPlayerId ( String playerId ) {
        this.playerId = playerId;
    }

    private String playerId;

    private String playerName;

    public String getTotalPrice () {
        return totalPrice;
    }

    public void setTotalPrice ( String totalPrice ) {
        this.totalPrice = totalPrice;
    }

    private String totalPrice;

    public String getPlayerName () {
        return playerName;
    }

    public void setPlayerName ( String playerName ) {
        this.playerName = playerName;
    }

    public String getTicketQuantity () {
        return ticketQuantity;
    }

    public void setTicketQuantity ( String ticketQuantity ) {
        this.ticketQuantity = ticketQuantity;
    }

    public String getPlayerImageUrl () {
        return playerImageUrl;
    }

    public void setPlayerImageUrl ( String playerImageUrl ) {
        this.playerImageUrl = playerImageUrl;
    }

    private String ticketQuantity;
    private String playerImageUrl;



}
