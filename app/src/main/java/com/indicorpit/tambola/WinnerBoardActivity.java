package com.indicorpit.tambola;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.indicorpit.tambola.adapters.AdapterForWinnerList;
import com.indicorpit.tambola.database.Database;
import com.indicorpit.tambola.databinding.ActivityWinnerBoardBinding;
import com.indicorpit.tambola.model.ClaimSuccessModel;
import com.indicorpit.tambola.model.RoomModel;
import com.indicorpit.tambola.model.WinnersModel;
import com.indicorpit.tambola.viewModel.WinnerActivityRoomModel;

import java.util.ArrayList;
import java.util.List;

public class WinnerBoardActivity extends AppCompatActivity {

    ActivityWinnerBoardBinding binding;
    AdapterForWinnerList adapterForWinnerList;
    MediaPlayer mp;
    List<WinnersModel> winnersModelList=new ArrayList<> (  );
    WinnerActivityRoomModel winnerActivityRoomModel;
    ChildEventListener childEventListener;
    RoomModel pricesRoomModel;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding=ActivityWinnerBoardBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );
        winnerActivityRoomModel=new ViewModelProvider (this).get( WinnerActivityRoomModel.class);
        mp= MediaPlayer.create(this, R.raw.firework_sound);

        AdView mAdView = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        binding.shareWinnerList.setOnClickListener ( view -> {
            if(winnersModelList.size ()>0)
            {
                Intent shareWinnerIntent = new Intent(Intent.ACTION_SEND);
                shareWinnerIntent.setType("text/plain");
                shareWinnerIntent.putExtra(Intent.EXTRA_SUBJECT, "Winner List");
                StringBuilder shareMessage= new StringBuilder ( " Winner List\n\n" );

                for(WinnersModel winnersModel:winnersModelList)
                {
                    shareMessage.append ( "\n" ).append ( "Winner Name :-" ).append ( winnersModel.getWinnerName () );
                    shareMessage.append ( "\n" ).append ( "Claim Type :-" ).append ( winnersModel.getClaimType () );
                    shareMessage.append ( "\n" ).append ( "Claim Price :-" ).append ( getPrice(winnersModel.getClaimType (),pricesRoomModel) ).append ( "\n" );
                }
                shareWinnerIntent.putExtra(Intent.EXTRA_TEXT, shareMessage.toString ( ) );
                startActivity(Intent.createChooser(shareWinnerIntent, "choose one"));
            }else
            {
                Toast.makeText ( WinnerBoardActivity.this , "Wait for winner list" , Toast.LENGTH_SHORT ).show ( );
            }
        } );


        if(winnerActivityRoomModel.getRoom_code ()==null)
        winnerActivityRoomModel.setRoom_code ( getIntent ().getStringExtra ( "ROOM_CODE" ) );

        Database.getDatabaseReference ().child ( "Room" ).child(winnerActivityRoomModel.getRoom_code ()).child("prices").addListenerForSingleValueEvent ( new ValueEventListener ( ) {
            @Override
            public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                 pricesRoomModel=snapshot.getValue ( RoomModel.class );
                if(pricesRoomModel!=null)
                {
                    binding.winnerListRecycleView.setLayoutManager ( new LinearLayoutManager ( WinnerBoardActivity.this ) );
                    adapterForWinnerList=new AdapterForWinnerList ( winnersModelList,pricesRoomModel );
                    binding.winnerListRecycleView.setAdapter ( adapterForWinnerList);
                }
                else
                    Toast.makeText ( WinnerBoardActivity.this , "Error Occurred", Toast.LENGTH_SHORT ).show ( );
            }

            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {
            }
        } );

        childEventListener=Database.getDatabaseReference ().child ( "Room" ).child(winnerActivityRoomModel.getRoom_code ()).child ( "claims" ).addChildEventListener ( new ChildEventListener ( ) {
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


        binding.homeButton.setOnClickListener ( view -> WinnerBoardActivity.this.onBackPressed () );
    }
    private String getPrice (String claimType,RoomModel pricesRoomModel) {
        switch (claimType)
        {
            case "Middle Line":
                return pricesRoomModel.getMiddleLinePrice ();

            case "Last Line":
                return pricesRoomModel.getLastLinePrice ();

            case  "Full house":
                return pricesRoomModel.getFullHousePrice ();

            case  "First Line":
                return pricesRoomModel.getFirstLinePrice ();

            case  "Early Five":
                return pricesRoomModel.getEarlyfivePrice ();

            case  "Ladoo":
                return pricesRoomModel.getLadooPrice ();

            case  "King Corners":
                return pricesRoomModel.getKingCornersPrice ();

            case  "Queen Corners":
                return pricesRoomModel.getQueenCornersPrice ();

            case "Second House":
                return pricesRoomModel.getSecondHousePrice ();

            case "Bamboo":
                return pricesRoomModel.getBambooPrice ();
        }
        return "00";
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

    @Override
    public void onBackPressed () {
        Database.getDatabaseReference ().child ( "Room" ).child(winnerActivityRoomModel.getRoom_code ()).removeValue ();
        super.onBackPressed ( );
    }

    @Override
    protected void onDestroy () {
        if(childEventListener!=null)
            Database.getDatabaseReference ().removeEventListener ( childEventListener );
        super.onDestroy ( );
    }
}