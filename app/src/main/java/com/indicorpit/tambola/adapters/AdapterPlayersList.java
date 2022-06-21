package com.indicorpit.tambola.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.indicorpit.tambola.database.Database;
import com.indicorpit.tambola.databinding.CustomRawJoinedPlayersBinding;
import com.indicorpit.tambola.model.PlayerModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPlayersList extends RecyclerView.Adapter {

    public AdapterPlayersList ( List<PlayerModel> playerModelList ) {
        this.playerModelList = playerModelList;
    }

    public AdapterPlayersList ( List<PlayerModel> playerModelList ,boolean isHostAdapter,String roomCode,AlertDialog alertDialog) {
        this.playerModelList = playerModelList;
        this.isHostAdapter=isHostAdapter;
        this.roomCode=roomCode;
        this.alertDialog=alertDialog;
    }

    List<PlayerModel>  playerModelList;
    boolean isHostAdapter;
    String roomCode;
    AlertDialog alertDialog;

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {
        CustomRawJoinedPlayersBinding customRawJoinedPlayersBinding=CustomRawJoinedPlayersBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false) ;
        return new MyHolder (customRawJoinedPlayersBinding);
    }

    @Override
    public void onBindViewHolder ( @NonNull RecyclerView.ViewHolder holder , int position ) {
        MyHolder myHolder=(MyHolder) holder;

        Picasso.get().load(playerModelList.get ( position ).getPlayerImageUrl ()).into(myHolder.binding.playerImageIcon);
        myHolder.binding.playerName.setText ( playerModelList.get ( position ).getPlayerName () );
        myHolder.binding.ticketQuantity.setText ( "Qt : "+playerModelList.get ( position ).getTicketQuantity () );
        myHolder.binding.ticketTotalPrice.setText ( "Total : "+playerModelList.get ( position ).getTotalPrice () );

        if(isHostAdapter)
        {
            myHolder.binding.blockButton.setVisibility ( View.VISIBLE );
            myHolder.binding.blockButton.setOnClickListener ( view -> {
                String blockedButtonText=myHolder.binding.blockButton.getText ().toString ();


                if(blockedButtonText.equalsIgnoreCase ( "Blocked" ))
                    Toast.makeText ( myHolder.itemView.getContext () , "Already Blocked" , Toast.LENGTH_SHORT ).show ( );
                else
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(holder.itemView.getContext ( ));
                    builder1.setTitle ( "Confirmation" );
                    builder1.setMessage("Are you sure to Block "+playerModelList.get ( holder.getAdapterPosition ( ) ).getPlayerName ());

                    builder1.setPositiveButton(
                            "Yes",
                            ( dialog , id ) -> {
                                dialog.cancel ( );
                                Database.getDatabaseReference ( ).child ( "Room" ).child ( roomCode ).child ( "blocks" ).child ( "players" ).child ( playerModelList.get ( holder.getAdapterPosition ( ) ).getPlayerId ( ) ).setValue ( "Blocked" ).addOnCompleteListener ( task121 -> {
                                    if ( task121.isSuccessful ( ) )
                                        Toast.makeText ( holder.itemView.getContext ( ) , "Blocked Success" , Toast.LENGTH_SHORT ).show ( );
                                    else
                                        Toast.makeText ( holder.itemView.getContext ( ) , "Blocked Failed" , Toast.LENGTH_SHORT ).show ( );

                                 alertDialog.dismiss ();
                                } );
                            } );

                    builder1.setNegativeButton( "No", null);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            } );

            Database.getDatabaseReference ( ).child ( "Room" ).child ( roomCode ).child ( "blocks" ).child ( "players" )
                    .child ( playerModelList.get ( holder.getAdapterPosition ( ) ).getPlayerId ()).addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                @Override
                public void onDataChange ( @NonNull DataSnapshot snapshot ) {

                    String blocked=snapshot.getValue ( String.class );
                    if(blocked!=null)
                        if(blocked.equalsIgnoreCase ( "Blocked" ))
                            myHolder.binding.blockButton.setText ( blocked );
                }

                @Override
                public void onCancelled ( @NonNull DatabaseError error ) {

                }
            } );

        }
        }

    @Override
    public int getItemCount () {
        return playerModelList.size ();
    }

    static class MyHolder extends RecyclerView.ViewHolder
    {
        CustomRawJoinedPlayersBinding binding;
        public MyHolder(CustomRawJoinedPlayersBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
