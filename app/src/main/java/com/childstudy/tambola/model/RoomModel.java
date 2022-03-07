package com.childstudy.tambola.model;

import java.io.Serializable;

public class RoomModel implements Serializable {

    private String ticketPrice;
    private String firstLinePrice;
    private String MiddleLinePrice;
    private String LastLinePrice;
    private String first_LastPrice;

    public String getRoomId () {
        return roomId;
    }

    public RoomModel () {
    }

    public void setRoomId ( String roomId ) {
        this.roomId = roomId;
    }

    private String roomId;

    public RoomModel ( String ticketPrice ,String firstLinePrice , String middleLinePrice , String lastLinePrice , String first_LastPrice , String fullHousePrice,String roomId ) {
        this.ticketPrice = ticketPrice;
        this.firstLinePrice = firstLinePrice;
        MiddleLinePrice = middleLinePrice;
        LastLinePrice = lastLinePrice;
        this.first_LastPrice = first_LastPrice;
        this.fullHousePrice = fullHousePrice;
        this.roomId=roomId;
    }

    public String getTicketPrice () {
        return ticketPrice;
    }

    public void setTicketPrice ( String players ) {
        this.ticketPrice = players;
    }

    public String getFirstLinePrice () {
        return firstLinePrice;
    }

    public void setFirstLinePrice ( String firstLinePrice ) {
        this.firstLinePrice = firstLinePrice;
    }

    public String getMiddleLinePrice () {
        return MiddleLinePrice;
    }

    public void setMiddleLinePrice ( String middleLinePrice ) {
        MiddleLinePrice = middleLinePrice;
    }

    public String getLastLinePrice () {
        return LastLinePrice;
    }

    public void setLastLinePrice ( String lastLinePrice ) {
        LastLinePrice = lastLinePrice;
    }

    public String getFirst_LastPrice () {
        return first_LastPrice;
    }

    public void setFirst_LastPrice ( String first_LastPrice ) {
        this.first_LastPrice = first_LastPrice;
    }

    public String getFullHousePrice () {
        return fullHousePrice;
    }

    public void setFullHousePrice ( String fullHousePrice ) {
        this.fullHousePrice = fullHousePrice;
    }

    private String fullHousePrice;
}
