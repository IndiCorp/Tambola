package com.childstudy.tambola.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.childstudy.tambola.R;
import com.childstudy.tambola.databinding.CustomRawForBoardBinding;
import com.childstudy.tambola.databinding.CustomRawForDoneNumbersListBinding;

import java.util.List;

public class AdapterForBoardRecycleView extends RecyclerView.Adapter {

    public AdapterForBoardRecycleView ( List<Integer> selectedIntegerList,List<Integer> allIntegerList ,boolean isDoneNUmberList) {
        this.selectedIntegerList = selectedIntegerList;
        this.allIntegerList=allIntegerList;
        this.isDoneNUmberList=isDoneNUmberList;
    }
    List<Integer>  selectedIntegerList;
    List<Integer> allIntegerList;
    boolean isDoneNUmberList;

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {

        if(isDoneNUmberList)
        {
            CustomRawForDoneNumbersListBinding customRawForDoneNumbersListBinding=CustomRawForDoneNumbersListBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false) ;
            return new DoneNumberMyHolder(customRawForDoneNumbersListBinding);
        }

        CustomRawForBoardBinding customRawForBoardBinding=CustomRawForBoardBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false) ;
        return new MyHolder (customRawForBoardBinding);

    }

    @Override
    public void onBindViewHolder ( @NonNull RecyclerView.ViewHolder holder , int position ) {

        if(isDoneNUmberList)
        {
            DoneNumberMyHolder myHolder= (DoneNumberMyHolder) holder;
            myHolder.binding.numberTextView.setText(String.valueOf ( allIntegerList.get ( position ) ));
        }else
        {
            MyHolder myHolder= (MyHolder) holder;

            myHolder.binding.numberTextView.setText(String.valueOf ( allIntegerList.get ( position ) ));

            for(int selectNum : selectedIntegerList)
                if(selectNum==allIntegerList.get ( position ))
                {
                    myHolder.binding.numberTextView.setBackground (
                            ContextCompat.getDrawable(myHolder.binding.getRoot ().getContext (), R.drawable.circle_green));
                }
        }


    }

    @Override
    public int getItemCount () {
        return allIntegerList.size ();
    }

    static class MyHolder extends RecyclerView.ViewHolder
    {
        CustomRawForBoardBinding binding;
        public MyHolder(CustomRawForBoardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    static class DoneNumberMyHolder extends RecyclerView.ViewHolder
    {
        CustomRawForDoneNumbersListBinding binding;
        public DoneNumberMyHolder(CustomRawForDoneNumbersListBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }


}
