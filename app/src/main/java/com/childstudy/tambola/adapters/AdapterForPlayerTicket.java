package com.childstudy.tambola.adapters;


import android.util.Log;
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

import com.childstudy.tambola.GameStartPlayerScreen;
import com.childstudy.tambola.R;
import com.childstudy.tambola.database.Database;
import com.childstudy.tambola.databinding.CustomRawForPlayersTicketBinding;
import com.childstudy.tambola.model.ClaimModelClass;
import com.childstudy.tambola.model.PlayerModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterForPlayerTicket extends RecyclerView.Adapter {

    public AdapterForPlayerTicket (  List<List<Integer>> selectedIntegerList, List<Integer> allIntegerList,List<String>  ticketList,String roomId,String playerId, List<String>  ticketListId) {
        this.selectedIntegerList = selectedIntegerList;
        this.allIntegerList=allIntegerList;
        this.ticketList=ticketList;
        this.roomId=roomId;
        this.playerId=playerId;
        this.ticketListId=  ticketListId;
    }


    List<List<Integer>>  selectedIntegerList;
    List<Integer> allIntegerList;
    List<String>  ticketList;
    String roomId;
    String playerId;
    List<String>  ticketListId;


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
            List<Boolean> booleansList=new ArrayList<> (  );
            myHolder.binding.playersTicketRecycleView.setAdapter ( new AdapterForTicket ( selectedIntegerList,tempIntegerList,booleansList, holder.getAdapterPosition ( ) ));

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

            myHolder.binding.highLowClaimButton.setOnClickListener  ( view -> claimedClick ( view,myHolder ));

            myHolder.binding.middleLineClaimButton.setOnClickListener  ( view -> claimedClick ( view,myHolder ));

            myHolder.binding.lastLineClaimButton.setOnClickListener  ( view -> claimedClick ( view,myHolder ));

            myHolder.binding.fullHouseClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ));

            myHolder.binding.firstLineClaimButton.setOnClickListener ( view -> claimedClick ( view,myHolder ) );


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
                        case "H&L":
                              setClaimedButton (  myHolder.binding.highLowClaimButton,myHolder );
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
                String blockPlayer= snapshot.getKey ();
                Log.d ( "MyTag" , "onDataChange: blockPlayer="+blockPlayer );
                Log.d ( "MyTag" , "onDataChange: blockPlayer="+snapshot.getValue (  ) );


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

                        if(!myHolder.binding.highLowClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.highLowClaimButton  );

                        if(!myHolder.binding.fullHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.fullHouseClaimButton  );
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

                        if(!myHolder.binding.highLowClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.highLowClaimButton  );

                        if(!myHolder.binding.fullHouseClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" ))
                            setBlockedButton (myHolder.binding.fullHouseClaimButton  );
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
                else if(button.getId ()==myHolder.binding.highLowClaimButton.getId ())
                    firstLastClaimButtonClaimClicked ( myHolder );
                else if(button.getId ()==myHolder.binding.fullHouseClaimButton.getId ())
                    fullHouseClaimButtonClaimClicked ( myHolder );
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
                myHolder.binding.highLowClaimButton.getText ().toString ().equalsIgnoreCase ( "Claimed" )  )
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
