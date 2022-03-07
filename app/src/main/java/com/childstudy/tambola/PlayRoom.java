package com.childstudy.tambola;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.childstudy.tambola.databinding.ActivityPlayRoomBinding;
import com.childstudy.tambola.databinding.WaitingLayoutBinding;
import com.childstudy.tambola.model.PlayerModel;
import com.childstudy.tambola.model.RoomModel;
import com.childstudy.tambola.util.StorageClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PlayRoom extends AppCompatActivity {

    ActivityPlayRoomBinding binding;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding=ActivityPlayRoomBinding.inflate ( getLayoutInflater () );
        setContentView (binding.getRoot () );

        String roomId=getIntent ().getStringExtra ( "ROOM_ID" );
        Log.d ( "MyTag" , "onCreate: "+roomId );
        Query myTopPostsQuery =  FirebaseDatabase.getInstance().getReference ( ).child("Room").child(roomId).child("prices");

        myTopPostsQuery.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot) {
                // TODO: handle the post
                RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);

                if(roomModel!=null)
                {
                    binding.firstLinePriceEdittext.setText ( roomModel.getFirstLinePrice () );
                    binding.middleLinePriceEdittext.setText ( roomModel.getMiddleLinePrice () );
                    binding.lastLinePriceEdittext.setText ( roomModel.getLastLinePrice () );
                    binding.firstLastPriceEdittext.setText ( roomModel.getFirst_LastPrice () );
                    binding.fullHouseLinePriceEdittext.setText ( roomModel.getFullHousePrice () );
                    binding.ticketPriceEdittext.setText ( roomModel.getTicketPrice () );
                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText ( PlayRoom.this , "exception="+databaseError.toException() , Toast.LENGTH_SHORT ).show ( );
                Log.w("MyTag", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        binding.ticketPriceIncrementButton.setOnClickListener ( view -> {

            int newValuesInt=Integer.parseInt (binding.ticketQuantityEdittext.getText ().toString () )+1;
            if(newValuesInt==4)
                Toast.makeText ( PlayRoom.this , "Already reach maximum quantity" , Toast.LENGTH_SHORT ).show ( );
            else
            {
                String newValue=newValuesInt+"";
                binding.ticketQuantityEdittext.setText (newValue );
            }
        } );
        binding.ticketPriceDecrementButton.setOnClickListener ( view -> {
            int newValuesInt=Integer.parseInt (binding.ticketQuantityEdittext.getText ().toString () )-1;
            if(newValuesInt==0)
                Toast.makeText ( PlayRoom.this , "Already reach minimum quantity" , Toast.LENGTH_SHORT ).show ( );
            else
            {
                String newValue=newValuesInt+"";
                binding.ticketQuantityEdittext.setText (newValue );
            }
        } );
        binding.buyTicketButton.setOnClickListener ( view -> {
            binding.progressBar.setVisibility ( View.VISIBLE );

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference ( );

            int temp= (int) (Math.random()*1000000000);

            int totalPrice=Integer.parseInt ( binding.ticketPriceEdittext.getText ().toString () ) * Integer.parseInt ( binding.ticketQuantityEdittext.getText ().toString () );
            PlayerModel playerModel=new PlayerModel ( StorageClass.getName ( PlayRoom.this ) , Integer.toString ( totalPrice ) ,binding.ticketQuantityEdittext.getText ().toString (),
                    "https://st.depositphotos.com/2101611/3925/v/600/depositphotos_39258143-stock-illustration-businessman-avatar-profile-picture.jpg"  );
            myRef.child ( "Room" ).child(roomId).child ( "players" ).child ("player"+temp).setValue ( playerModel ).addOnCompleteListener ( task -> {
                if(task.isSuccessful ())
                {
                    Toast.makeText ( PlayRoom.this , "successfully buying" , Toast.LENGTH_SHORT ).show ( );
                    setContentView ( WaitingLayoutBinding.inflate ( PlayRoom.this.getLayoutInflater () ).getRoot () );

                    myRef.child ( "Room" ).child ( roomId ).child ( "status" ).addValueEventListener ( new ValueEventListener ( ) {
                        @Override
                        public void onDataChange ( @NonNull DataSnapshot snapshot ) {

                            Boolean roomStatus=snapshot.getValue ( Boolean.class );
                            if ( roomStatus!=null )
                            if(roomStatus)
                            {
                                Intent intent=new Intent (PlayRoom.this,GameStartPlayerScreen.class  );
                                intent.putExtra ( "ROOM_ID",roomId );
                                intent.putExtra ( "PLAYER_ID","player"+temp);
                                startActivity ( intent);
                                PlayRoom.this.finish ();
                            }
                        }

                        @Override
                        public void onCancelled ( @NonNull DatabaseError error ) {
                            Toast.makeText ( PlayRoom.this , error.toString (), Toast.LENGTH_SHORT ).show ( );
                        }
                    } );
                }
                else
                {
                    Toast.makeText ( PlayRoom.this , "Problem Occurred" , Toast.LENGTH_SHORT ).show ( );
                }
            } );
        } );

    }
}