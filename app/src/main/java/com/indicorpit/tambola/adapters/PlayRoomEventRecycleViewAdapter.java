package com.indicorpit.tambola.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indicorpit.tambola.databinding.CustomRawEventBinding;

import java.util.HashMap;
import java.util.List;

public class PlayRoomEventRecycleViewAdapter extends RecyclerView.Adapter {



    List<String> eventList;
    HashMap<String,Integer> eventPriceList;

    public PlayRoomEventRecycleViewAdapter ( List<String> eventList, HashMap<String,Integer> eventPriceList) {
        this.eventList = eventList;
        this.eventPriceList=eventPriceList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {
        CustomRawEventBinding customRawEventBinding=CustomRawEventBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false) ;
        return new MyHolder (customRawEventBinding);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder ( @NonNull RecyclerView.ViewHolder holder , int position ) {
       MyHolder myHolder= (MyHolder) holder;

       String str=eventList.get ( myHolder.getAdapterPosition ());
       myHolder.binding.firstLineTextView.setText ( getHeadingString ( str ));


        myHolder.binding.firstLinePriceValueEdittext.setEnabled ( false );

        String price= eventPriceList.get ( str )+"";
        myHolder.binding.firstLinePriceValueEdittext.setText ( price);

    }

    public static String getHeadingString(String keyValue)
    {
        String str=null;
        switch (keyValue)
        {
            case "first_line":
                str="First Line";
                break;
            case "middle_line":
                str="Middle Line";
                break;
            case "last_line":
                str="Last Line";
                break;
            case "early_five":
                str="Early Five";
                break;
            case "ladoo":
                str="Ladoo";
                break;
            case "king_corners":
                str="King Corners";
                break;
            case "queen_corners":
                str="Queen Corners";
                break;
            case "bamboo":
                str="Bamboo";
                break;
            case "full_house":
                str="Full House";
                break;
            case "second_house":
                str="Second House";
                break;
        }
        return str;
    }


    @Override
    public int getItemCount () {
        return eventList.size ();
    }

    static class MyHolder extends RecyclerView.ViewHolder
    {
        CustomRawEventBinding binding;
        public MyHolder(CustomRawEventBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

}
