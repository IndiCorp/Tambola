package com.indicorpit.tambola;

import static com.indicorpit.tambola.MainActivity.MUSIC_PLAY;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indicorpit.tambola.adapters.PlayRoomEventRecycleViewAdapter;
import com.indicorpit.tambola.database.Database;
import com.indicorpit.tambola.databinding.ActivityPlayRoomBinding;
import com.indicorpit.tambola.databinding.WaitingLayoutBinding;
import com.indicorpit.tambola.model.PlayerModel;
import com.indicorpit.tambola.model.RoomModel;
import com.indicorpit.tambola.util.StorageClass;
import com.indicorpit.tambola.viewModel.PlayRoomViewMOdel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class PlayRoom extends AppCompatActivity {

    ActivityPlayRoomBinding binding;
    PlayRoomViewMOdel playRoomViewMOdel;
    ValueEventListener valueChangeEventListener;
    ValueEventListener roomStartedValueEventListener;


    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        initializeScreen();

        AdView mAdView = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(MUSIC_PLAY)
            binding.speakerIcon.setImageResource ( R.drawable.ic_speaker_icon );
        else
            binding.speakerIcon.setImageResource ( R.drawable.ic_off_speaker );


        binding.eventRecycleView.setLayoutManager ( new LinearLayoutManager ( this ) );
        binding.eventRecycleView.setAdapter ( playRoomViewMOdel.getEventRecycleViewAdapter () );


        binding.speakerIcon.setOnClickListener ( view -> {
            if(MUSIC_PLAY)
            {
                MainActivity.mp.pause ();
                MUSIC_PLAY=false;
                binding.speakerIcon.setImageResource ( R.drawable.ic_off_speaker );
            }
            else
            {
                MainActivity.mp.start ();
                MUSIC_PLAY=true;
                binding.speakerIcon.setImageResource ( R.drawable.ic_speaker_icon );
            }
        } );


        Query myTopPostsQuery =  Database.getDatabaseReference ().child("Room").child(playRoomViewMOdel.getRoomId ()).child("prices");
        valueChangeEventListener = myTopPostsQuery.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot) {
                // TODO: handle the post
                RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);

                if(roomModel!=null)
                {
                    playRoomViewMOdel.getEventList ().clear ();
                    playRoomViewMOdel.getEventPriceList ().clear ();
                    getPriceValues(roomModel);
                    setEventList();
                    playRoomViewMOdel.getEventRecycleViewAdapter ().notifyDataSetChanged ();
                    playRoomViewMOdel.setTicketPrice (Integer.parseInt (roomModel.getTicketPrice ()));
                    binding.ticketPriceEdittext.setText (String.valueOf (  playRoomViewMOdel.getTicketPrice ()) );
                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText ( PlayRoom.this , "exception="+databaseError.toException() , Toast.LENGTH_SHORT ).show ( );
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
                binding.ticketQuantityEdittext.setText (newValue);
                playRoomViewMOdel.setTicketPrice ( newValuesInt );
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
                playRoomViewMOdel.setTicketPrice ( newValuesInt );
            }
        } );

        binding.buyTicketButton.setOnClickListener ( view -> {
            binding.progressBar.setVisibility ( View.VISIBLE );
            int temp= (int) (Math.random()*1000000000);
            playRoomViewMOdel.setPlayerId ("player"+temp);

            int totalPrice=Integer.parseInt (binding.ticketPriceEdittext.getText ().toString () ) * Integer.parseInt ( binding.ticketQuantityEdittext.getText ().toString () );
            PlayerModel playerModel=new PlayerModel ( StorageClass.getName ( PlayRoom.this ) , Integer.toString ( totalPrice ) ,binding.ticketQuantityEdittext.getText ().toString (),
                    "https://st.depositphotos.com/2101611/3925/v/600/depositphotos_39258143-stock-illustration-businessman-avatar-profile-picture.jpg"  );
            Database.getDatabaseReference ().child ( "Room" ).child(playRoomViewMOdel.getRoomId ()).child ( "players" ).child ( playRoomViewMOdel.getPlayerId ( ) ).setValue ( playerModel ).addOnCompleteListener ( task -> {
                if(task.isSuccessful ())
                {
                    Toast.makeText ( PlayRoom.this , "successfully buying" , Toast.LENGTH_SHORT ).show ( );

                    WaitingLayoutBinding waitingLayoutBinding=WaitingLayoutBinding.inflate ( PlayRoom.this.getLayoutInflater () );
                    setContentView ( waitingLayoutBinding.getRoot () );

                    if(MUSIC_PLAY)
                        waitingLayoutBinding.speakerIcon.setImageResource ( R.drawable.ic_speaker_icon );
                    else
                        waitingLayoutBinding.speakerIcon.setImageResource ( R.drawable.ic_off_speaker );

                    waitingLayoutBinding.speakerIcon.setOnClickListener ( view1 -> {
                        if(MUSIC_PLAY)
                        {
                            MainActivity.mp.pause ();
                            MUSIC_PLAY=false;
                            waitingLayoutBinding.speakerIcon.setImageResource ( R.drawable.ic_off_speaker );
                        }
                        else
                        {
                            MainActivity.mp.start ();
                            MUSIC_PLAY=true;
                            waitingLayoutBinding.speakerIcon.setImageResource ( R.drawable.ic_speaker_icon );
                        }
                    } );
                    roomStartedValueEventListener =Database.getDatabaseReference ().child ( "Room" ).child ( playRoomViewMOdel.getRoomId () ).child ( "status" ).addValueEventListener ( new ValueEventListener ( ) {
                        @Override
                        public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                            String roomStatus=snapshot.getValue ( String.class );
                            if ( roomStatus!=null )
                            if(roomStatus.equalsIgnoreCase ( "started" ))
                            {
                                Intent intent=new Intent (PlayRoom.this,GameStartPlayerScreen.class  );
                                intent.putExtra ( "ROOM_ID",playRoomViewMOdel.getRoomId () );
                                intent.putExtra ( "PLAYER_ID",playRoomViewMOdel.getPlayerId ());
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

    private void initializeScreen () {
        binding=ActivityPlayRoomBinding.inflate ( getLayoutInflater () );
        setContentView (binding.getRoot () );

        playRoomViewMOdel=new ViewModelProvider (this).get( PlayRoomViewMOdel.class);

        if(playRoomViewMOdel.getRoomId ()==null)
            playRoomViewMOdel.setRoomId ( getIntent ().getStringExtra ( "ROOM_ID" ));

        if(playRoomViewMOdel.getEventRecycleViewAdapter ()==null)
            playRoomViewMOdel.setEventRecycleViewAdapter (  new PlayRoomEventRecycleViewAdapter (  playRoomViewMOdel.getEventList (),playRoomViewMOdel.getEventPriceList () ));

        binding.ticketPriceEdittext.setText (String.valueOf ( playRoomViewMOdel.getTicketPrice ()  ) );

        if(playRoomViewMOdel.getWaiting ())
        {
            setContentView ( WaitingLayoutBinding.inflate ( PlayRoom.this.getLayoutInflater () ).getRoot () );

            Database.getDatabaseReference ().child ( "Room" ).child ( playRoomViewMOdel.getRoomId () ).child ( "status" ).addValueEventListener ( new ValueEventListener ( ) {
                @Override
                public void onDataChange ( @NonNull DataSnapshot snapshot ) {

                    String roomStatus=snapshot.getValue ( String.class );
                    if ( roomStatus!=null )
                        if(roomStatus.equalsIgnoreCase ( "started" ))
                        {
                            Intent intent=new Intent (PlayRoom.this,GameStartPlayerScreen.class  );
                            intent.putExtra ( "ROOM_ID",playRoomViewMOdel.getRoomId () );
                            intent.putExtra ( "PLAYER_ID","player"+playRoomViewMOdel.getPlayerId ());
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


    }

    private void setEventList () {
        for (Map.Entry<String,Integer> entry : playRoomViewMOdel.getEventPriceList ().entrySet())
        {
            if(entry.getValue ()!=0)
            {
                playRoomViewMOdel.getEventList ().add (entry.getKey() );
            }
        }
    }

    private void getPriceValues (RoomModel roomModel) {

        if(roomModel.getFirstLinePrice ()!=null)
            playRoomViewMOdel.getEventPriceList ().put ( "first_line",Integer.parseInt ( roomModel.getFirstLinePrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put ( "first_line",0);

        if(roomModel.getMiddleLinePrice ()!=null)
            playRoomViewMOdel.getEventPriceList ().put ( "middle_line",Integer.parseInt ( roomModel.getMiddleLinePrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put ( "middle_line",0);


        if(roomModel.getLastLinePrice ()!=null)
            playRoomViewMOdel.getEventPriceList ().put ( "last_line",Integer.parseInt ( roomModel.getLastLinePrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put ( "last_line",0);



        if(roomModel.getEarlyfivePrice ()!=null)
            playRoomViewMOdel.getEventPriceList ().put (  "early_five",Integer.parseInt ( roomModel.getEarlyfivePrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put (  "early_five",0);

        if(roomModel.getLadooPrice ()!=null)
            playRoomViewMOdel.getEventPriceList ().put (  "ladoo",Integer.parseInt ( roomModel.getLadooPrice  () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put (  "ladoo",0);


        if(roomModel.getKingCornersPrice  ()!=null)
            playRoomViewMOdel.getEventPriceList ().put (  "king_corners",Integer.parseInt ( roomModel.getKingCornersPrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put (  "king_corners",0);


        if(roomModel.getQueenCornersPrice  ()!=null)
            playRoomViewMOdel.getEventPriceList ().put (  "queen_corners",Integer.parseInt ( roomModel.getQueenCornersPrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put ( "queen_corners",0);


        if(roomModel.getBambooPrice  ()!=null)
            playRoomViewMOdel.getEventPriceList ().put (  "bamboo",Integer.parseInt ( roomModel.getBambooPrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put ( "bamboo",0);

        if(roomModel.getFullHousePrice ()!=null)
            playRoomViewMOdel.getEventPriceList ().put (  "full_house",Integer.parseInt ( roomModel.getFullHousePrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put ( "full_house",0);

        if(roomModel.getSecondHousePrice ()!=null)
            playRoomViewMOdel.getEventPriceList ().put (  "second_house",Integer.parseInt ( roomModel.getSecondHousePrice () ) );
        else
            playRoomViewMOdel.getEventPriceList ().put ( "second_house",0);

    }

    @Override
    protected void onResume () {
        super.onResume ( );
        if(MUSIC_PLAY)
        {
            MainActivity.mp.start();
            MainActivity.mp.setLooping ( true );
        }
    }

    @Override
    protected void onPause () {
        MainActivity.mp.pause ();
        super.onPause ( );
    }

    @Override
    protected void onDestroy () {
            if(roomStartedValueEventListener!= null)
            Database.getDatabaseReference ().removeEventListener ( roomStartedValueEventListener );
            if(valueChangeEventListener!= null)
            Database.getDatabaseReference ().removeEventListener ( valueChangeEventListener );

        super.onDestroy ( );
    }
}