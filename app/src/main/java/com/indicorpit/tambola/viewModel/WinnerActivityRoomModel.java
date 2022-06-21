package com.indicorpit.tambola.viewModel;

import androidx.lifecycle.ViewModel;

public class WinnerActivityRoomModel extends ViewModel {

    public String getRoom_code () {
        return room_code;
    }

    public void setRoom_code ( String room_code ) {
        this.room_code = room_code;
    }

    private String room_code;


}
