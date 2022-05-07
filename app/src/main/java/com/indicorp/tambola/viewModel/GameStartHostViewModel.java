package com.indicorp.tambola.viewModel;

import androidx.lifecycle.ViewModel;

import com.indicorp.tambola.adapters.AdapterForBoardRecycleView;
import com.indicorp.tambola.model.ClaimModelClass;

import java.util.ArrayList;
import java.util.List;

public class GameStartHostViewModel extends ViewModel {

    public String getRoomCode () {
        return roomCode;
    }

    public void setRoomCode ( String roomCode ) {
        this.roomCode = roomCode;
    }

    String roomCode;

    public ClaimModelClass getClaimModelClass () {
        return claimModelClass;
    }

    public void setClaimModelClass ( ClaimModelClass claimModelClass ) {
        this.claimModelClass = claimModelClass;
    }

    ClaimModelClass claimModelClass;

    public List<Integer> getAllIntegerList () {
        return allIntegerList;
    }

    public void setAllIntegerList ( List<Integer> allIntegerList ) {
        this.allIntegerList = allIntegerList;
    }

    public List<Integer> getSelectedIntegerList () {
        return selectedIntegerList;
    }

    public void setSelectedIntegerList ( List<Integer> selectedIntegerList ) {
        this.selectedIntegerList = selectedIntegerList;
    }

    public AdapterForBoardRecycleView getAdapterForBoardRecycleView () {
        return adapterForBoardRecycleView;
    }

    public void setAdapterForBoardRecycleView ( AdapterForBoardRecycleView adapterForBoardRecycleView ) {
        this.adapterForBoardRecycleView = adapterForBoardRecycleView;
    }

    public AdapterForBoardRecycleView getAdapterForDoneNumbersRecycleView () {
        return adapterForDoneNumbersRecycleView;
    }

    public void setAdapterForDoneNumbersRecycleView ( AdapterForBoardRecycleView adapterForDoneNumbersRecycleView ) {
        this.adapterForDoneNumbersRecycleView = adapterForDoneNumbersRecycleView;
    }

    List<Integer> allIntegerList=new ArrayList<> (  );
    List<Integer> selectedIntegerList=new ArrayList<> (  );
    AdapterForBoardRecycleView adapterForBoardRecycleView=new AdapterForBoardRecycleView (selectedIntegerList,allIntegerList,false  );;
    AdapterForBoardRecycleView adapterForDoneNumbersRecycleView=new AdapterForBoardRecycleView (selectedIntegerList,selectedIntegerList,true  );;

    public String getKeyForPending () {
        return keyForPending;
    }

    public void setKeyForPending ( String keyForPending ) {
        this.keyForPending = keyForPending;
    }

    String keyForPending;

    public String getPlayerName () {
        return playerName;
    }

    public void setPlayerName ( String playerName ) {
        this.playerName = playerName;
    }


    String playerName;


    public String getMyNumber () {
        return myNumber;
    }

    public void setMyNumber ( String myNumber ) {
        this.myNumber = myNumber;
    }

    String myNumber;

    public int getApprovedClaimsCount () {
        return approvedClaimsCount;
    }

    public void setApprovedClaimsCount ( int approvedClaimsCount ) {
        this.approvedClaimsCount = approvedClaimsCount;
    }

    int approvedClaimsCount;

    public List<Integer> getCurrentNumberList () {
        return currentNumberList;
    }

    public void setCurrentNumberList ( List<Integer> currentNumberList ) {
        this.currentNumberList = currentNumberList;
    }

    List<Integer> currentNumberList=new ArrayList<> (  );

}
