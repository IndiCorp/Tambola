package com.indicorp.tambola.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.indicorp.tambola.GameStartPlayerScreen;
import com.indicorp.tambola.R;
import com.indicorp.tambola.database.Database;
import com.indicorp.tambola.databinding.CustomRawForPlayersTicketBinding;
import com.indicorp.tambola.model.ClaimModelClass;
import com.indicorp.tambola.model.RoomModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdapterForPlayerTicket extends RecyclerView.Adapter {

    public AdapterForPlayerTicket ( List<List<Integer>> selectedIntegerList, List<Integer> allIntegerList, List<String>  ticketList,
                                    String roomId, String playerId, List<String>  ticketListId, RoomModel pricesRoomModel,  HashMap<String,List<Boolean>> hashMap ) {
        this.selectedIntegerList = selectedIntegerList;
        this.allIntegerList=allIntegerList;
        this.ticketList=ticketList;
        this.roomId=roomId;
        this.playerId=playerId;
        this.ticketListId=  ticketListId;
        this.pricesRoomModel=pricesRoomModel;
        this.hashMap=hashMap;
    }


    List<List<Integer>>  selectedIntegerList;
    List<Integer> allIntegerList;
    List<String>  ticketList;
    HashMap<String,List<Boolean>> hashMap;
    String roomId;
    String playerId;
    List<String>  ticketListId;
    RoomModel pricesRoomModel;
    Context context;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {
        CustomRawForPlayersTicketBinding customRawForPlayersTicketBinding=CustomRawForPlayersTicketBinding.inflate( LayoutInflater.from(parent.getContext()),parent,false) ;
        return new MyHolder (customRawForPlayersTicketBinding);
    }

    @Override
    public void onBindViewHolder ( @NonNull RecyclerView.ViewHolder holder , int position ) {
            MyHolder myHolder= (MyHolder) holder;
            myHolder.binding.playersTicketRecycleView.setLayoutManager ( new GridLayoutManager ( myHolder.binding.getRoot ().getContext (),9 ) );

            List<Integer> tempIntegerList=allIntegerList.subList (position*27, (position*27)+27 );

            if(hashMap.get ( ""+holder.getAdapterPosition () )==null)
                hashMap.put ( ""+holder.getAdapterPosition (), new ArrayList<> (  ));

            myHolder.binding.playersTicketRecycleView.setAdapter ( new AdapterForTicket ( selectedIntegerList,tempIntegerList,hashMap.get ( ""+holder.getAdapterPosition () ), holder.getAdapterPosition ( ) ));

            myHolder.binding.claimArrow.setOnClickListener ( view -> {
                if(myHolder.binding.claimLayout.getVisibility ()==View.VISIBLE)
                {
                    myHolder.binding.claimLayout.setVisibility ( View.GONE );
                    myHolder.binding.claimArrow.setBackground ( ContextCompat.getDrawable ( myHolder.binding.getRoot ().getContext (), R.drawable.ic_down_arrow ) );
                }else {
                    myHolder.binding.claimLayout.setVisibility ( View.VISIBLE );
                    myHolder.binding.claimArrow.setBackground ( ContextCompat.getDrawable ( myHolder.binding.getRoot ().getContext (), R.drawable.ic_arrow_up ) );
                }
            } );

            myHolder.binding.claimsTextView.setOnClickListener ( view -> {
                if(myHolder.binding.claimLayout.getVisibility ()==View.VISIBLE)
                {
                    myHolder.binding.claimLayout.setVisibility ( View.GONE );
                    myHolder.binding.claimArrow.setBackground ( ContextCompat.getDrawable ( myHolder.binding.getRoot ().getContext (), R.drawable.ic_down_arrow ) );
                }else {
                    myHolder.binding.claimLayout.setVisibility ( View.VISIBLE );
                    myHolder.binding.claimArrow.setBackground ( ContextCompat.getDrawable ( myHolder.binding.getRoot ().getContext (), R.drawable.ic_arrow_up ) );
                }
            } );

            hideNonUsedClaimButton(myHolder);

            myHolder.binding.middleLineClaimButton.setOnClickListener  ( view -> claimedClick ( view,myHolder ));

            myHolder.binding.lastLineClaimButton.setOnClickListener  ( view -> claimedClick ( view,myHolder ));

            myHolder.binding.fullHouseClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ));

            myHolder.binding.firstLineClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ) );

           myHolder.binding.earlyFiveClaimButton.setOnClickListener  ( view -> claimedClick ( view,myHolder ));

           myHolder.binding.bambooClaimButton.setOnClickListener  ( view -> claimedClick ( view,myHolder ));

           myHolder.binding.ladooClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ));

           myHolder.binding.kingCornersClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ) );

           myHolder.binding.queenCornersClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ));

           myHolder.binding.secondHouseClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ) );


        Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "claims" ).addChildEventListener ( new ChildEventListener ( ) {
            @Override
            public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {
                String claimType= snapshot.getKey ();
                if(claimType!=null)
                {
                    switch (claimType) {
                        case "First Line":
                              setClaimedButton (  myHolder.binding.firstLineClaimButton,myHolder );
                              break;
                        case "Full house":
                              setClaimedButton (  myHolder.binding.fullHouseClaimButton,myHolder );
                              break;
                        case "Last Line":
                              setClaimedButton (  myHolder.binding.lastLineClaimButton,myHolder );
                              break;
                        case "Middle Line":
                              setClaimedButton (  myHolder.binding.middleLineClaimButton ,myHolder);
                              break;
                        case  "Early Five":
                            setClaimedButton (  myHolder.binding.earlyFiveClaimButton,myHolder );
                            break;
                        case "King Corners":
                            setClaimedButton (  myHolder.binding.kingCornersClaimButton,myHolder );
                            break;
                        case "Ladoo":
                            setClaimedButton (  myHolder.binding.ladooClaimButton,myHolder );
                            break;
                        case "Queen Corners":
                            setClaimedButton (  myHolder.binding.queenCornersClaimButton ,myHolder);
                            break;
                        case "Bamboo":
                            setClaimedButton (  myHolder.binding.bambooClaimButton,myHolder );
                            break;
                        case "Second House":
                            setClaimedButton (  myHolder.binding.secondHouseClaimButton,myHolder );
                            break;

                    }
                }

            }

            @Override
            public void onChildChanged ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

            }

            @Override
            public void onChildRemoved ( @NonNull DataSnapshot snapshot ) {

            }

            @Override
            public void onChildMoved ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

            }

            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {

            }
        } );

        Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "blocks" ).child("players").addValueEventListener ( new ValueEventListener ( ) {
            @Override
            public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                String blockPlayer;
                boolean checkPlayer=false;

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                  blockPlayer=postSnapshot.getKey ();
                    if(blockPlayer!=null)
                    {
                        if ( blockPlayer.equals ( playerId ) )
                            checkPlayer=true;
                }

                    if ( checkPlayer ) {
                        if(!myHolder.binding.firstLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.firstLineClaimButton  );

                        if(!myHolder.binding.middleLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.middleLineClaimButton  );

                        if(!myHolder.binding.lastLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.lastLineClaimButton  );

                        if(!myHolder.binding.fullHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.fullHouseClaimButton  );

                        if(!myHolder.binding.earlyFiveClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.earlyFiveClaimButton  );

                        if(!myHolder.binding.secondHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.secondHouseClaimButton  );

                        if(!myHolder.binding.kingCornersClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.kingCornersClaimButton  );

                        if(!myHolder.binding.queenCornersClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.queenCornersClaimButton  );

                        if(!myHolder.binding.bambooClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.bambooClaimButton  );

                        if(!myHolder.binding.ladooClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.ladooClaimButton  );
                    }
                    Toast.makeText ( myHolder.itemView.getContext () , blockPlayer+" Blocked by host" , Toast.LENGTH_SHORT ).show ( );
                }

            }

            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {

            }
        } );

        Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "blocks" ).child("tickets").addChildEventListener ( new ChildEventListener ( ) {
            @Override
            public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {
                String blockTicketId= snapshot.getKey ();
                if(blockTicketId!=null)
                {
                    if ( ticketListId.get (myHolder.getAdapterPosition ()  ).equals ( blockTicketId ) ) {
                        if(!myHolder.binding.firstLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.firstLineClaimButton  );

                        if(!myHolder.binding.middleLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.middleLineClaimButton  );

                        if(!myHolder.binding.lastLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.lastLineClaimButton  );

                        if(!myHolder.binding.fullHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.fullHouseClaimButton  );

                        if(!myHolder.binding.earlyFiveClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.earlyFiveClaimButton  );

                        if(!myHolder.binding.secondHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.secondHouseClaimButton  );

                        if(!myHolder.binding.kingCornersClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.kingCornersClaimButton  );

                        if(!myHolder.binding.queenCornersClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.queenCornersClaimButton  );

                        if(!myHolder.binding.bambooClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.bambooClaimButton  );

                        if(!myHolder.binding.ladooClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.ladooClaimButton  );
                    }

                    Toast.makeText ( myHolder.itemView.getContext () , blockTicketId+" Ticket Blocked" , Toast.LENGTH_SHORT ).show ( );
                }
            }

            @Override
            public void onChildChanged ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

            }

            @Override
            public void onChildRemoved ( @NonNull DataSnapshot snapshot ) {

            }

            @Override
            public void onChildMoved ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

            }

            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {

            }
        } );
    }

    @Override
    public int getItemCount () {
        return ticketList.size ();
    }

    static class MyHolder extends RecyclerView.ViewHolder
    {
        CustomRawForPlayersTicketBinding binding;
        public MyHolder(CustomRawForPlayersTicketBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    void hideNonUsedClaimButton(MyHolder myHolder)
    {
        int claimsCount=10;

            if(pricesRoomModel.getFirstLinePrice ()==null ||Integer.parseInt (  pricesRoomModel.getFirstLinePrice ())==0)
            {
                myHolder.binding.firstLineClaimButton.setVisibility ( View.GONE );
                myHolder.binding.firstLineClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.firstLineIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getMiddleLinePrice ()==null || Integer.parseInt (  pricesRoomModel.getMiddleLinePrice ())==0)
            {
                myHolder.binding.middleLineClaimButton.setVisibility ( View.GONE );
                myHolder.binding.middleLineClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.middleLineIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getLastLinePrice ()==null || Integer.parseInt (  pricesRoomModel.getLastLinePrice ())==0)
            {
                myHolder.binding.lastLineClaimButton.setVisibility ( View.GONE );
                myHolder.binding.lastLineClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.lastLineIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getEarlyfivePrice ()==null ||  Integer.parseInt (  pricesRoomModel.getEarlyfivePrice ())==0)
            {
                myHolder.binding.earlyFiveClaimButton.setVisibility ( View.GONE );
                myHolder.binding.earlyFiveClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.earlyFiveIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getBambooPrice ()==null || Integer.parseInt (  pricesRoomModel.getBambooPrice ())==0)
            {
                myHolder.binding.bambooClaimButton.setVisibility ( View.GONE );
                myHolder.binding.bambooClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.bambooIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }


            if(pricesRoomModel.getFullHousePrice ()==null || Integer.parseInt (  pricesRoomModel.getFullHousePrice ())==0)
            {
                myHolder.binding.fullHouseClaimButton.setVisibility ( View.GONE );
                myHolder.binding.fullHouseClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.fullHouseIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getSecondHousePrice ()==null || Integer.parseInt (  pricesRoomModel.getSecondHousePrice ())==0)
            {
                myHolder.binding.secondHouseClaimButton.setVisibility ( View.GONE );
                myHolder.binding.secondHouseClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.secondHouseIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getLadooPrice ()==null || Integer.parseInt (  pricesRoomModel.getLadooPrice ())==0)
            {
                myHolder.binding.ladooClaimButton.setVisibility ( View.GONE );
                myHolder.binding.ladooClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.ladooIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getKingCornersPrice ()==null || Integer.parseInt (  pricesRoomModel.getKingCornersPrice ())==0)
            {
                myHolder.binding.kingCornersClaimButton.setVisibility ( View.GONE );
                myHolder.binding.kingCornersClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.kingCornersIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }

            if(pricesRoomModel.getQueenCornersPrice ()==null || Integer.parseInt (  pricesRoomModel.getQueenCornersPrice ())==0)
            {
                myHolder.binding.queenCornersClaimButton.setVisibility ( View.GONE );
                myHolder.binding.queenCornersClaimTextView.setVisibility ( View.GONE );
                myHolder.binding.queenCornersIcon.setVisibility ( View.GONE );
                claimsCount=claimsCount-1;
            }
        Database.getDatabaseReference ().child ( "Room" ).child(roomId ).child("totalClaimsCount").setValue(claimsCount);
    }

    void claimedClick( View view,MyHolder myHolder) {
        AppCompatButton button=(AppCompatButton) view;
        if(button.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
        {
            showAlertOnEmptyClaim(myHolder,"Already claimed");
        }else if(button.getText ().toString ().equalsIgnoreCase ( "Blocked" ))
        {
            showAlertOnEmptyClaim(myHolder,"Yor are blocked");
        }else
        {
            if(selectedIntegerList.get ( myHolder.getAdapterPosition () ).size ()>0)
            {
                if(button.getId ()==myHolder.binding.firstLineClaimButton.getId ())
                      firstLineClaimButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.middleLineClaimButton.getId ())
                    middleLineClaimButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.lastLineClaimButton.getId ())
                    lastLineClaimButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.fullHouseClaimButton.getId ())
                    fullHouseClaimButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.earlyFiveClaimButton.getId ())
                    earlyFiveButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.ladooClaimButton.getId ())
                    ladooButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.kingCornersClaimButton.getId ())
                    kingCornersButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.queenCornersClaimButton.getId ())
                    queenCornersButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.bambooClaimButton.getId ())
                    bambooButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.secondHouseClaimButton.getId ())
                    secondHouseButtonClaimClicked ( myHolder );
            }
            else
                showAlertOnEmptyClaim(myHolder,"Please select atLeast one number");
        }
    }

    void setClaimedButton( AppCompatButton button,MyHolder myHolder) {
       button.setText ( button.getContext ().getResources().getString(R.string.claimed));
       button.setBackgroundColor ( ContextCompat.getColor(button.getContext (), R.color.light_grey) );
       checkAllClaimed(myHolder);
    }

    void setBlockedButton( AppCompatButton button) {
        button.setText ( button.getContext ().getResources().getString(R.string.blocked));
        button.setBackgroundColor ( ContextCompat.getColor(button.getContext (), R.color.red) );
    }

    void checkAllClaimed(MyHolder myHolder)
    {
        if(myHolder.binding.fullHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.firstLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.middleLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.lastLineClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.earlyFiveClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.bambooClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.ladooClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.queenCornersClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.secondHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ) &&
                myHolder.binding.kingCornersClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" )  )
        {
            Database.getDatabaseReference ().child ( "Room" ).child ( roomId ).child("gameOver").setValue ( true );
        }
    }

    String integerListToString(int position) {
        StringBuilder stringBuilder = new StringBuilder ( );
        for (int i=0;i<selectedIntegerList.get ( position ).size ();i++)
        {
            stringBuilder.append ( selectedIntegerList.get ( position ).get ( i ) );
            stringBuilder.append ( "-" );
        }
        return stringBuilder.toString ();
    }

    void firstLastClaimButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("High and Low")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    String claimType="H&L";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim "+claimStatus;
                    }else
                        claimStatus="wrong claim "+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "H&L claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "H&L claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void middleLineClaimButtonClaimClicked(MyHolder myHolder) {
         new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Middle Line")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    String claimType="Middle Line";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="Delayed pending";


                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="Right claim "+claimStatus;
                    }else
                        claimStatus="Wrong claim "+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Middle Line claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Middle line claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void lastLineClaimButtonClaimClicked(MyHolder myHolder) {
          new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Last Line")
                .setPositiveButton("Yes", ( dialog , which ) -> {


                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    String claimType="Last Line";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Last Line claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Last line claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void fullHouseClaimButtonClaimClicked(MyHolder myHolder) {
          new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Full House")
                .setPositiveButton("Yes", ( dialog , which ) -> {


                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    String claimType="Full house";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Full house claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Full house claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void firstLineClaimButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("First Line")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    String claimType="First Line";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "First Line claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "First Line claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void earlyFiveButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Early Five")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    context=myHolder.binding.getRoot ().getContext ();
                    String claimType="Early Five";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Early Five claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Early Five claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void kingCornersButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("King Corners")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    context=myHolder.binding.getRoot ().getContext ();
                    String claimType="King Corners";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void ladooButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Ladoo")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    context=myHolder.binding.getRoot ().getContext ();
                    String claimType="Ladoo";
                    if(checkClaim (myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Ladoo claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , "Ladoo claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void queenCornersButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Queen Corners")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    context=myHolder.binding.getRoot ().getContext ();
                    String claimType="Queen Corners";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void secondHouseButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Second House")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    context=myHolder.binding.getRoot ().getContext ();
                    String claimType="Second House";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    void bambooButtonClaimClicked(MyHolder myHolder) {
        new AlertDialog.Builder(myHolder.binding.getRoot ().getContext ())
                .setTitle("Are you sure to claim")
                .setMessage("Bamboo")
                .setPositiveButton("Yes", ( dialog , which ) -> {

                    String claimStatus="pending";
                    if(isDelayedClaim ( myHolder.getAdapterPosition () ))
                        claimStatus="delayed pending";

                    context=myHolder.binding.getRoot ().getContext ();
                    String claimType="Bamboo";
                    if(checkClaim ( myHolder.getAdapterPosition (),claimType ))
                    {
                        claimStatus="right claim"+claimStatus;
                    }else
                        claimStatus="wrong claim"+claimStatus;


                    ClaimModelClass claimModelClass=new ClaimModelClass ( integerListToString (myHolder.getAdapterPosition ()),claimType ,ticketList.get ( myHolder.getAdapterPosition ( ) ),claimStatus,playerId);

                    Database.getDatabaseReference ().child ( "Room" ).child(roomId).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).child ( ticketListId.get ( myHolder.getAdapterPosition ( ) ) ).child ( "claims" ).setValue ( claimModelClass)
                            .addOnCompleteListener ( task -> {
                                if( task.isSuccessful ( ) )
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim success" , Toast.LENGTH_SHORT ).show ( );
                                else
                                    Toast.makeText ( myHolder.binding.getRoot ().getContext () , claimType+" claim failed" , Toast.LENGTH_SHORT ).show ( );
                            } );
                } ).setNegativeButton("No", null).show();
    }

    boolean isDelayedClaim( int position) {
        boolean check=true;
        if(GameStartPlayerScreen.doneNumbersList.size ()!=0)
        {
            int currentNumberInt=GameStartPlayerScreen.doneNumbersList.get ( GameStartPlayerScreen.doneNumbersList.size ()-1 );
            for (int i=0;i<selectedIntegerList.get ( position ).size ();i++)
            {
                if(selectedIntegerList.get ( position ).get ( i )== currentNumberInt)
                {
                    check=false;
                }
            }
        }
        return check;
    }

    boolean checkClaim(int position,String claimType) {
        String ticket = ticketList.get ( position );
        List<Integer> ticketInteger = stringToIntegerList ( ticket );
        List<Integer> tempTicketInteger=new ArrayList<> (  );

        for(int temp:ticketInteger)
        {
         if(temp!=0)
             tempTicketInteger.add ( temp );
        }


        boolean check=true;

        switch (claimType) {
            case "H&L":

                //ticket high and low
                int ticketHigh = 0, ticketLow = 0;
                for (int i = 0; i < tempTicketInteger.size ( ); i++) {
                    if ( i == 0 ) {
                        ticketHigh = tempTicketInteger.get ( i );
                        ticketLow = ticketHigh;
                    } else {
                        if ( ticketHigh < tempTicketInteger.get ( i ) )
                            ticketHigh = tempTicketInteger.get ( i );
                        if ( ticketLow > tempTicketInteger.get ( i ) )
                            ticketLow = tempTicketInteger.get ( i );
                    }
                }

                if ( !selectedIntegerList.get ( position ).contains ( ticketHigh ) || !selectedIntegerList.get ( position ).contains ( ticketLow ) )
                    check = false;

                if ( !GameStartPlayerScreen.doneNumbersList.contains ( ticketHigh ) || !GameStartPlayerScreen.doneNumbersList.contains ( ticketLow ) )
                    check = false;
                break;

            case "Middle Line":
                List<Integer> middleLine = tempTicketInteger.subList ( 5 , 9 );
                for (int num : middleLine) {
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                }
                break;

            case "Last Line":
                List<Integer> lastLine = tempTicketInteger.subList ( 10 , 14 );
                for (int num : lastLine) {
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                }
                break;

            case  "Full house":
                for (int num : tempTicketInteger) {
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                }
                break;

            case  "First Line":
                List<Integer> firstLine = tempTicketInteger.subList ( 0, 4 );
                for (int num : firstLine) {
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                }
                break;

            case  "Early Five":
                if(selectedIntegerList.size ()<5)
                {
                    Toast.makeText ( context , "Please select atLeast 5 Numbers" , Toast.LENGTH_SHORT ).show ( );
                }else
                {
                    List<Integer> earlyFive = selectedIntegerList.get ( position ).subList ( 0, 4 );
                    for (int num : earlyFive) {
                        if ( !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                            check = false;
                            break;
                        }
                    }
                }
                break;


            case  "Ladoo":
                    int  ladooNumber = tempTicketInteger.get ( 7 );
                        if ( !selectedIntegerList.get ( position ).contains ( ladooNumber ) || !GameStartPlayerScreen.doneNumbersList.contains ( ladooNumber ) ) {
                            check = false;
                        }
                break;

            case  "King Corners":

                List<Integer> kingCornersNumbers=new ArrayList<> (  );
                kingCornersNumbers.add ( tempTicketInteger.get ( 4 ) );
                kingCornersNumbers.add ( tempTicketInteger.get ( 9 ) );
                kingCornersNumbers.add ( tempTicketInteger.get ( 14 ) );

                for(int num:kingCornersNumbers)
                {
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                }
                break;

            case  "Queen Corners":
                List<Integer> queenCornersNumbers=new ArrayList<> (  );
                queenCornersNumbers.add ( tempTicketInteger.get ( 0 ) );
                queenCornersNumbers.add ( tempTicketInteger.get ( 5 ) );
                queenCornersNumbers.add ( tempTicketInteger.get ( 10 ) );

                for(int num:queenCornersNumbers)
                {
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                }
                break;

            case "Second House":
                for (int num : tempTicketInteger)
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                break;

            case "Bamboo":
                List<Integer> bambooNumbers=new ArrayList<> (  );
                bambooNumbers.add ( tempTicketInteger.get ( 2) );
                bambooNumbers.add ( tempTicketInteger.get ( 7 ) );
                bambooNumbers.add ( tempTicketInteger.get ( 12 ) );

                for(int num:bambooNumbers)
                {
                    if ( !selectedIntegerList.get ( position ).contains ( num ) || !GameStartPlayerScreen.doneNumbersList.contains ( num ) ) {
                        check = false;
                        break;
                    }
                }
                break;
        }

        return check;
        }

    List<Integer> stringToIntegerList(String ticket) {
        List<Integer> integerList=new ArrayList<> (  );
        String[] str =  ticket.split ( "-" );
        for(String st:str)
        {
            Integer temp=Integer.parseInt ( st );
            integerList.add ( temp );
        }
        return integerList; }

    void showAlertOnEmptyClaim(MyHolder myHolder,String message) {
        new AlertDialog.Builder ( myHolder.binding.getRoot ().getContext () )
                .setMessage ( message )
                .setPositiveButton ( "OK",null )
                .show ();
    }

}
