package com.indicorp.tambola;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indicorp.tambola.adapters.AdapterForWinnerList;
import com.indicorp.tambola.database.Database;
import com.indicorp.tambola.databinding.ActivityWinnerBoardBinding;
import com.indicorp.tambola.model.ClaimSuccessModel;
import com.indicorp.tambola.model.RoomModel;
import com.indicorp.tambola.model.WinnersModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WinnerBoardActivity extends AppCompatActivity {

    ActivityWinnerBoardBinding binding;
    AdapterForWinnerList adapterForWinnerList;
    MediaPlayer mp;
    List<WinnersModel> winnersModelList=new ArrayList<> (  );
    static String roomCode;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding=ActivityWinnerBoardBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );
        mp= MediaPlayer.create(this, R.raw.firework_sound);

        AdView mAdView = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        if(roomCode==null)
        roomCode=getIntent ().getStringExtra ( "ROOM_CODE" );

        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child("prices").addListenerForSingleValueEvent ( new ValueEventListener ( ) {
            @Override
            public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                RoomModel roomModel=snapshot.getValue ( RoomModel.class );
                if(roomModel!=null)
                {
                    binding.winnerListRecycleView.setLayoutManager ( new LinearLayoutManager ( WinnerBoardActivity.this ) );
                    adapterForWinnerList=new AdapterForWinnerList ( winnersModelList,roomModel );
                    binding.winnerListRecycleView.setAdapter ( adapterForWinnerList);
                }

                else
                    Toast.makeText ( WinnerBoardActivity.this , "Error Occurred", Toast.LENGTH_SHORT ).show ( );
            }

            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {
            }
        } );

        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "claims" ).addChildEventListener ( new ChildEventListener ( ) {
            @Override
            public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

                String claimKey = snapshot.getKey ( );
                ClaimSuccessModel claimSuccessModel=snapshot.getValue ( ClaimSuccessModel.class );
                if(claimKey!=null && claimSuccessModel!=null)
                {
                    winnersModelList.add ( new WinnersModel ( claimKey,"00",claimSuccessModel.getPlayerName () ) );
                    adapterForWinnerList.notifyDataSetChanged ();
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

        binding.homeButton.setOnClickListener ( view -> WinnerBoardActivity.super.onBackPressed () );
    }
    @Override
    protected void onResume () {
        super.onResume ( );
        mp.start();
        mp.setLooping ( true );
    }

    @Override
    protected void onPause () {
        mp.pause ();
        super.onPause ( );
    }

}