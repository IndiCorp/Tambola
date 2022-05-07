package com.indicorp.tambola.viewModel;

import androidx.lifecycle.ViewModel;

import com.indicorp.tambola.adapters.PlayRoomEventRecycleViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayRoomViewMOdel extends ViewModel {

    public String getRoomId () {
        return roomId;
    }

    public void setRoomId ( String roomId ) {
        this.roomId = roomId;
    }


    public int getTicketPrice () {
        return ticketPrice;
    }

    public void setTicketPrice ( int ticketPrice ) {
        this.ticketPrice = ticketPrice;
    }

    private int ticketPrice;

    private String roomId;

    public String getPlayerId () {
        return playerId;
    }

    public void setPlayerId ( String playerId ) {
        this.playerId = playerId;
    }

    private String playerId;

    public boolean getWaiting () {
        return isWaiting;
    }

    public void setWaiting ( boolean waiting ) {
        isWaiting = waiting;
    }


    private boolean isWaiting;

    public PlayRoomEventRecycleViewAdapter getEventRecycleViewAdapter () {
        return eventRecycleViewAdapter;
    }

    public void setEventRecycleViewAdapter ( PlayRoomEventRecycleViewAdapter eventRecycleViewAdapter ) {
        this.eventRecycleViewAdapter = eventRecycleViewAdapter;
    }

    public List<String> getEventList () {
        return eventList;
    }

    public void setEventList ( List<String> eventList ) {
        this.eventList = eventList;
    }

    public HashMap<String, Integer> getEventPriceList () {
        return eventPriceList;
    }

    public void setEventPriceList ( HashMap<String, Integer> eventPriceList ) {
        this.eventPriceList = eventPriceList;
    }

    private PlayRoomEventRecycleViewAdapter eventRecycleViewAdapter;
    private List<String> eventList=new ArrayList<> (  );
    private HashMap<String,Integer> eventPriceList=new HashMap<> (  );
}
