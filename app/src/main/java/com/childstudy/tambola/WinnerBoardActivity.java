package com.childstudy.tambola;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.childstudy.tambola.adapters.AdapterForWinnerList;
import com.childstudy.tambola.database.Database;
import com.childstudy.tambola.databinding.ActivityWinnerBoardBinding;
import com.childstudy.tambola.model.ClaimSuccessModel;
import com.childstudy.tambola.model.WinnersModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class WinnerBoardActivity extends AppCompatActivity {

    ActivityWinnerBoardBinding binding;
    AdapterForWinnerList adapterForWinnerList;
    MediaPlayer mp;
    List<WinnersModel> winnersModelList=new ArrayList<> (  );
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding=ActivityWinnerBoardBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );
        mp= MediaPlayer.create(this, R.raw.firework_sound);

        binding.winnerListRecycleView.setLayoutManager ( new LinearLayoutManager ( this ) );
        adapterForWinnerList=new AdapterForWinnerList ( winnersModelList );
        binding.winnerListRecycleView.setAdapter ( adapterForWinnerList);

        String roomCode=getIntent ().getStringExtra ( "ROOM_CODE" );
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