package com.indicorpit.tambola.viewModel;

import androidx.lifecycle.ViewModel;
import com.indicorpit.tambola.adapters.AdapterPlayersList;
import com.indicorpit.tambola.model.PlayerModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HostScreenViewModel extends ViewModel {

    public String getRoomCode () {
        return roomCode;
    }

    public void setRoomCode ( String roomCode ) {
        this.roomCode = roomCode;
    }

    public int getTotalPrice () {
        return totalPrice;
    }

    public void setTotalPrice ( int totalPrice ) {
        this.totalPrice = totalPrice;
    }

    public int getNumOfTickets () {
        return numOfTickets;
    }

    public void setNumOfTickets ( int numOfTickets ) {
        this.numOfTickets = numOfTickets;
    }

    public HashMap<String, Boolean> getEventList () {
        return eventList;
    }

    public void setEventList ( HashMap<String, Boolean> eventList ) {
        this.eventList = eventList;
    }

    public HashMap<String, Integer> getEventPriceList () {
        return eventPriceList;
    }

    public void setEventPriceList ( HashMap<String, Integer> eventPriceList ) {
        this.eventPriceList = eventPriceList;
    }

    public List<PlayerModel> getPlayerModelList () {
        return playerModelList;
    }

    public void setPlayerModelList ( List<PlayerModel> playerModelList ) {
        this.playerModelList = playerModelList;
    }

    public AdapterPlayersList getAdapterPlayersList () {
        return adapterPlayersList;
    }

    public void setAdapterPlayersList ( AdapterPlayersList adapterPlayersList ) {
        this.adapterPlayersList = adapterPlayersList;
    }

    private String roomCode;
    private int totalPrice;
    private int numOfTickets;
    private HashMap<String,Boolean> eventList=new HashMap<> (  );
    private HashMap<String,Integer> eventPriceList=new HashMap<> (  );
    private List<PlayerModel> playerModelList=new ArrayList<> (  );
    private AdapterPlayersList adapterPlayersList;

}
