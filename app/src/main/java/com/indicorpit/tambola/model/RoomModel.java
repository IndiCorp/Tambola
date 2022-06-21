package com.indicorpit.tambola.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RoomModel implements Serializable {

    private String ticketPrice;
    private String firstLinePrice;
    private String middleLinePrice;
    private String lastLinePrice;
    private String earlyfivePrice;
    private String ladooPrice;
    private String kingCornersPrice;
    private String queenCornersPrice;
    private String bambooPrice;
    private String fullHousePrice;
    private String secondHousePrice;
    private String roomId;

    public RoomModel () {
    }

//    public RoomModel ( String ticketPrice ,String firstLinePrice , String middleLinePrice , String lastLinePrice , String first_LastPrice , String fullHousePrice,String roomId ) {
//        this.ticketPrice = ticketPrice;
//        this.firstLinePrice = firstLinePrice;
//        middleLinePrice = middleLinePrice;
//        LastLinePrice = lastLinePrice;
//        this.first_LastPrice = first_LastPrice;
//        this.fullHousePrice = fullHousePrice;
//        this.roomId=roomId;
//    }

    public RoomModel ( String ticketPrice , HashMap<String,Integer> eventPriceList,String roomId ) {
        this.ticketPrice = ticketPrice;
        this.roomId=roomId;
        setEventPrices(eventPriceList);
    }

    void setEventPrices(HashMap<String,Integer> eventPriceList)
    {
        for (Map.Entry<String,Integer> entry : eventPriceList.entrySet())
        {
            switch (entry.getKey())
            {
                case "first_line":
                    if(entry.getValue()!=null)
                      firstLinePrice=String.valueOf ( entry.getValue() );
                    break;
                case "middle_line":
                    middleLinePrice=String.valueOf ( entry.getValue() );
                    break;
                case "last_line":
                    lastLinePrice=String.valueOf ( entry.getValue() );
                    break;
                case "early_five":
                    earlyfivePrice=String.valueOf ( entry.getValue() );
                    break;
                case "ladoo":
                    ladooPrice=String.valueOf ( entry.getValue() );
                    break;
                case "king_corners":
                    kingCornersPrice=String.valueOf ( entry.getValue() );
                    break;
                case "queen_corners":
                    queenCornersPrice=String.valueOf ( entry.getValue() );
                    break;
                case "bamboo":
                    bambooPrice=String.valueOf ( entry.getValue() );
                    break;
                case "full_house":
                    fullHousePrice=String.valueOf ( entry.getValue() );
                    break;
                case "second_house":
                    secondHousePrice=String.valueOf ( entry.getValue() );
                    break;
            }
        }
    }


    public String getTicketPrice () {
        return ticketPrice;
    }

    public void setTicketPrice ( String ticketPrice ) {
        this.ticketPrice = ticketPrice;
    }

    public String getFirstLinePrice () {
        return firstLinePrice;
    }

    public void setFirstLinePrice ( String firstLinePrice ) {
        this.firstLinePrice = firstLinePrice;
    }

    public String getMiddleLinePrice () {
        return middleLinePrice;
    }

    public void setMiddleLinePrice ( String middleLinePrice ) {
        this.middleLinePrice = middleLinePrice;
    }

    public String getLastLinePrice () {
        return lastLinePrice;
    }

    public void setLastLinePrice ( String lastLinePrice ) {
        this.lastLinePrice = lastLinePrice;
    }

    public String getEarlyfivePrice () {
        return earlyfivePrice;
    }

    public void setEarlyfivePrice ( String earlyfivePrice ) {
        this.earlyfivePrice = earlyfivePrice;
    }

    public String getLadooPrice () {
        return ladooPrice;
    }

    public void setLadooPrice ( String ladooPrice ) {
        this.ladooPrice = ladooPrice;
    }

    public String getKingCornersPrice () {
        return kingCornersPrice;
    }

    public void setKingCornersPrice ( String kingCornersPrice ) {
        this.kingCornersPrice = kingCornersPrice;
    }

    public String getQueenCornersPrice () {
        return queenCornersPrice;
    }

    public void setQueenCornersPrice ( String queenCornersPrice ) {
        this.queenCornersPrice = queenCornersPrice;
    }

    public String getBambooPrice () {
        return bambooPrice;
    }

    public void setBambooPrice ( String bambooPrice ) {
        this.bambooPrice = bambooPrice;
    }

    public String getFullHousePrice () {
        return fullHousePrice;
    }

    public void setFullHousePrice ( String fullHousePrice ) {
        this.fullHousePrice = fullHousePrice;
    }

    public String getSecondHousePrice () {
        return secondHousePrice;
    }

    public void setSecondHousePrice ( String secondHousePrice ) {
        this.secondHousePrice = secondHousePrice;
    }

    public String getRoomId () {
        return roomId;
    }

    public void setRoomId ( String roomId ) {
        this.roomId = roomId;
    }


}
