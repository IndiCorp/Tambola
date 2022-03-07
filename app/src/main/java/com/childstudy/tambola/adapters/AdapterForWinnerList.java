package com.childstudy.tambola.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.childstudy.tambola.databinding.CustomRawWinnerListBinding;
import com.childstudy.tambola.model.WinnersModel;

import java.util.List;

public class AdapterForWinnerList extends RecyclerView.Adapter {

    public AdapterForWinnerList ( List<WinnersModel> winnersModelList) {
        this.winnersModelList=winnersModelList;
    }


    List<WinnersModel> winnersModelList;
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
        myHolder.binding.claimTypePoint.setText ( winnersModel.getClaimPoints () );
        myHolder.binding.claimTypeWinner.setText ( winnersModel.getWinnerName () );
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
