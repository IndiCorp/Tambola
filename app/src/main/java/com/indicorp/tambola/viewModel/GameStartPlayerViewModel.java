package com.indicorp.tambola.viewModel;

import androidx.lifecycle.ViewModel;

import com.indicorp.tambola.adapters.AdapterForPlayerTicket;
import com.indicorp.tambola.model.RoomModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameStartPlayerViewModel extends ViewModel {
    public List<Integer> getAllIntegerList () {
        return allIntegerList;
    }

    public void setAllIntegerList ( List<Integer> allIntegerList ) {
        this.allIntegerList = allIntegerList;
    }

    public List<List<Integer>> getSelectedIntegerList () {
        return selectedIntegerList;
    }

    public void setSelectedIntegerList ( List<List<Integer>> selectedIntegerList ) {
        this.selectedIntegerList = selectedIntegerList;
    }

    public HashMap<String, List<Boolean>> getHashMap () {
        return hashMap;
    }

    HashMap<String,List<Boolean>> hashMap=new HashMap<> (  );
    List<Integer> allIntegerList=new ArrayList<> (  );
    List<List<Integer>> selectedIntegerList=new ArrayList<> (  );

    public String getRoomCode () {
        return roomCode;
    }

    public void setRoomCode ( String roomCode ) {
        this.roomCode = roomCode;
    }

    String roomCode;

    public String getPlayerId () {
        return playerId;
    }

    public void setPlayerId ( String playerId ) {
        this.playerId = playerId;
    }

    String playerId;

    public RoomModel getPricesRoomModel () {
        return pricesRoomModel;
    }

    public void setPricesRoomModel ( RoomModel pricesRoomModel ) {
        this.pricesRoomModel = pricesRoomModel;
    }

    RoomModel pricesRoomModel=new RoomModel (  );

    public List<String> getTicketList () {
        return ticketList;
    }

    public void setTicketList ( List<String> ticketList ) {
        this.ticketList = ticketList;
    }

    public List<String> getTicketListId () {
        return ticketListId;
    }

    public void setTicketListId ( List<String> ticketListId ) {
        this.ticketListId = ticketListId;
    }

    List<String>  ticketList=new ArrayList<> (  );
    List<String>  ticketListId=new ArrayList<> (  );

    public AdapterForPlayerTicket getAdapterForPlayerTicket () {
        return adapterForPlayerTicket;
    }

    public void setAdapterForPlayerTicket ( AdapterForPlayerTicket adapterForPlayerTicket ) {
        this.adapterForPlayerTicket = adapterForPlayerTicket;
    }

    AdapterForPlayerTicket adapterForPlayerTicket;


}
