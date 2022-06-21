package com.indicorpit.tambola.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.indicorpit.tambola.R;
import com.indicorpit.tambola.databinding.CustomRawForBoardBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterForTicket extends RecyclerView.Adapter {

    public AdapterForTicket (  List<List<Integer>> selectedIntegerList, List<Integer> allIntegerList,  List<Boolean> booleansList,int position ) {
        this.selectedIntegerList = selectedIntegerList;
        this.allIntegerList=allIntegerList;
        this.booleansList=booleansList;
        itemPosition=position;
        selectedIntegerList.add ( new ArrayList<> (  ) );
    }
    List<List<Integer>>  selectedIntegerList;
    List<Integer> allIntegerList;
    List<Boolean> booleansList;
    int itemPosition;

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {
        CustomRawForBoardBinding customRawForBoardBinding=CustomRawForBoardBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false) ;

       if(booleansList.size ()<5 )
       {
           for(int ignored :allIntegerList)
               booleansList.add ( false );
       }
        return new MyHolder (customRawForBoardBinding);
    }


    @Override
    public void onBindViewHolder ( @NonNull RecyclerView.ViewHolder holder , int position ) {
            MyHolder myHolder= (MyHolder) holder;

           myHolder.binding.numberTextView.setText(String.valueOf ( allIntegerList.get ( position ) ));

           if( allIntegerList.get ( position ) ==0)
               myHolder.binding.numberTextView.setText(" ");


        if( booleansList.get ( holder.getAdapterPosition ( ) ))
        {
                myHolder.binding.numberTextView.setBackground (
                        ContextCompat.getDrawable(myHolder.binding.getRoot ().getContext (), R.drawable.circle_green));
            } else
        {
                myHolder.binding.numberTextView.setBackground (
                        ContextCompat.getDrawable(myHolder.binding.getRoot ().getContext (), R.drawable.circle_blue));

            }

           myHolder.binding.numberTextView.setOnClickListener ( view -> {
               if( booleansList.get ( holder.getAdapterPosition ( ) ))
               {
                   String temp= myHolder.binding.numberTextView.getText ( ).toString ( ) ;
                   if( !temp.equals ( " " ) )
                   {
                       myHolder.binding.numberTextView.setBackground (
                               ContextCompat.getDrawable(myHolder.binding.getRoot ().getContext (), R.drawable.circle_blue));

                       booleansList.set (holder.getAdapterPosition (), false );
                       selectedIntegerList.get ( itemPosition ).remove (Integer.valueOf ( myHolder.binding.numberTextView.getText ().toString ()  ) );
                   }
               }else
               {
                   String temp= myHolder.binding.numberTextView.getText ( ).toString ( ) ;
                   if( !temp.equals ( " " ) )
                   {
                       myHolder.binding.numberTextView.setBackground (
                           ContextCompat.getDrawable(myHolder.binding.getRoot ().getContext (), R.drawable.circle_green));

                       booleansList.set (holder.getAdapterPosition (), true );
                       selectedIntegerList.get ( itemPosition ).add (Integer.valueOf ( myHolder.binding.numberTextView.getText ().toString ()  ) );
                   }

               }
           } );

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
}
