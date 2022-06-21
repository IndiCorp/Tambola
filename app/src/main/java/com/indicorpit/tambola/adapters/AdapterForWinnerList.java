package com.indicorpit.tambola.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indicorpit.tambola.databinding.CustomRawWinnerListBinding;
import com.indicorpit.tambola.model.RoomModel;
import com.indicorpit.tambola.model.WinnersModel;

import java.util.List;

public class AdapterForWinnerList extends RecyclerView.Adapter {

    public AdapterForWinnerList ( List<WinnersModel> winnersModelList,RoomModel pricesRoomModel) {
        this.winnersModelList=winnersModelList;
        this.pricesRoomModel=pricesRoomModel;
    }

    List<WinnersModel> winnersModelList;
    RoomModel pricesRoomModel;
    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {
        CustomRawWinnerListBinding customRawWinnerListBinding=CustomRawWinnerListBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false) ;
        return new MyHolder (customRawWinnerListBinding);
    }


    @Override
    public void onBindViewHolder ( @NonNull RecyclerView.ViewHolder holder , int position ) {
        MyHolder myHolder= (MyHolder) holder;
        WinnersModel winnersModel=winnersModelList.get ( holder.getAdapterPosition () );
        myHolder.binding.claimType.setText ( winnersModel.getClaimType () );
        myHolder.binding.claimTypePoint.setText ( getPrice(winnersModel.getClaimType ()));
        myHolder.binding.claimTypeWinner.setText ( winnersModel.getWinnerName () );
    }

    private String getPrice (String claimType) {
        switch (claimType)
        {
            case "Middle Line":
                  return pricesRoomModel.getMiddleLinePrice ();

            case "Last Line":
                return pricesRoomModel.getLastLinePrice ();

            case  "Full house":
                return pricesRoomModel.getFullHousePrice ();

            case  "First Line":
                return pricesRoomModel.getFirstLinePrice ();

            case  "Early Five":
                return pricesRoomModel.getEarlyfivePrice ();

            case  "Ladoo":
                return pricesRoomModel.getLadooPrice ();

            case  "King Corners":
                return pricesRoomModel.getKingCornersPrice ();

            case  "Queen Corners":
                return pricesRoomModel.getQueenCornersPrice ();

            case "Second House":
                return pricesRoomModel.getSecondHousePrice ();

            case "Bamboo":
                return pricesRoomModel.getBambooPrice ();
        }
        return "00";
        }

    @Override
    public int getItemCount () {
        return winnersModelList.size ();
    }

    static class MyHolder extends RecyclerView.ViewHolder
    {
        CustomRawWinnerListBinding binding;
        public MyHolder(CustomRawWinnerListBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
