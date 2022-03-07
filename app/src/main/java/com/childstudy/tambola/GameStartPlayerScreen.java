package com.childstudy.tambola;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.childstudy.tambola.adapters.AdapterForPlayerTicket;
import com.childstudy.tambola.adapters.AdapterForWinnerList;
import com.childstudy.tambola.adapters.AdapterPlayersList;
import com.childstudy.tambola.database.Database;
import com.childstudy.tambola.databinding.ActivityGameStartPlayerScreenBinding;
import com.childstudy.tambola.databinding.ActivityWinnerBoardBinding;
import com.childstudy.tambola.databinding.LayoutForJoinedPlayersBinding;
import com.childstudy.tambola.databinding.LayoutHostClaimsBinding;
import com.childstudy.tambola.model.ClaimModelClass;
import com.childstudy.tambola.model.ClaimSuccessModel;
import com.childstudy.tambola.model.PlayerModel;
import com.childstudy.tambola.model.WinnersModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameStartPlayerScreen extends AppCompatActivity {

    ActivityGameStartPlayerScreenBinding binding;

    List<Integer> allIntegerList=new ArrayList<> (  );
    List<List<Integer>> selectedIntegerList=new ArrayList<> (  );
    List<String>  ticketList=new ArrayList<> (  );
    List<String>  ticketListId=new ArrayList<> (  );
    AdapterForPlayerTicket adapterForPlayerTicket;
    static AlertDialog.Builder builder1 ;
    MediaPlayer mediaPlayer;
    Long numberDisappearTime=6000L;
    Timer numberDisappearTimer;
    public static List<Integer> doneNumbersList=new ArrayList<> (  );

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding=ActivityGameStartPlayerScreenBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        builder1=new AlertDialog.Builder(GameStartPlayerScreen.this);
        String roomCode=getIntent ().getStringExtra ( "ROOM_ID" );
        String playerId=getIntent ().getStringExtra ( "PLAYER_ID" );
        binding.roomCodeTextView.setText ( "Room :-"+roomCode );
        mediaPlayer = MediaPlayer.create(this, R.raw.num_show_palyer_sound);


        binding.playersTicketRecycleView.setLayoutManager ( new LinearLayoutManager ( GameStartPlayerScreen.this ) );
        adapterForPlayerTicket=new AdapterForPlayerTicket ( selectedIntegerList,allIntegerList,ticketList,roomCode,playerId,ticketListId );
        binding.playersTicketRecycleView.setAdapter ( adapterForPlayerTicket );

        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                .child ( "tickets" ).addChildEventListener ( new ChildEventListener ( ) {
            @Override
            public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

                String ticket;
                ticket = snapshot.getValue (String.class);

                String ticketId=snapshot.getKey ();

                if(ticket!=null)
                {
                        boolean temp=false;
                            Log.d (  "MyTag", "onDataChange: "+ticket );

                            if(ticketList.size ()!=0)
                                if(ticketList.get ( ticketList.size ()-1 ).equals ( ticket ))
                                {
                                    temp=true;
                                }
                            if(!temp)
                            {
                                ticketList.add ( ticket );
                                ticketListId.add ( ticketId );
                                setTicketList();
                                adapterForPlayerTicket.notifyDataSetChanged ();
                            }


                }else
                {
                    Toast.makeText ( GameStartPlayerScreen.this , "Something problem" , Toast.LENGTH_SHORT ).show ( );
                    GameStartPlayerScreen.this.finish ();
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


        final boolean[] firstTime = {true};
        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "current_number" ).addValueEventListener ( new ValueEventListener ( ) {
            @Override
            public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                mediaPlayer.start();

                if( !firstTime[0] )
                {
                    Long currentNumberLong=  snapshot.getValue ( Long.class );

                    if(currentNumberLong!=null)
                    {
                        String myNumber;
                        if(currentNumberLong<10)
                            myNumber="0"+ currentNumberLong;
                        else
                            myNumber=String.valueOf ( currentNumberLong );

                        doneNumbersList.add ( Integer.valueOf ( myNumber ) );
                        setNumberAnimation ( myNumber);
                    }
                }
                firstTime[0] =false;
            }

            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {
                Toast.makeText ( GameStartPlayerScreen.this , "SomeThing Wrong" , Toast.LENGTH_SHORT ).show ( );
            }
        } );

        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).addChildEventListener (
                new ChildEventListener ( ) {
                    @Override
                    public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

                        String playerIde = snapshot.getKey ( );
                        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerIde )
                                .child ( "tickets" ).addChildEventListener ( new ChildEventListener ( ) {
                            @Override
                            public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

                                String key=snapshot.getKey (  );

                                assert key != null;
                                Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerIde )
                                        .child ( "tickets" ).child ( key).child ( "claims" ).addValueEventListener ( new ValueEventListener ( ) {
                                    @Override
                                    public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                                        ClaimModelClass claimModelClass=snapshot.getValue ( ClaimModelClass.class );
                                        if(claimModelClass!=null)
                                        {
                                            Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerIde )
                                                    .child ( "playerName" ).addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                                                @Override
                                                public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                                                    String playerName=snapshot.getValue ( String.class );
                                                    if(playerName!=null)
                                                    {
                                                        LayoutHostClaimsBinding layoutHostClaimsBinding=LayoutHostClaimsBinding.inflate ( getLayoutInflater () );
                                                        layoutHostClaimsBinding.claimType.setText ( ""+claimModelClass.getClaimType () );
                                                        layoutHostClaimsBinding.ticketId.setText ( ""+key);
                                                        layoutHostClaimsBinding.claimTicket.setText ( claimModelClass.getClaimTicket () );
                                                        layoutHostClaimsBinding.originalTicket.setText ( claimModelClass.getOriginalTicket () );
                                                        layoutHostClaimsBinding.claimTicket.setVisibility ( View.GONE );
                                                        layoutHostClaimsBinding.originalTicket.setVisibility ( View.GONE );
                                                        layoutHostClaimsBinding.textView3.setVisibility ( View.GONE );
                                                        layoutHostClaimsBinding.textView4.setVisibility ( View.GONE );
                                                        layoutHostClaimsBinding.ticketStatus.setText ( claimModelClass.getClaimStatus () );


                                                        if(builder1!=null)
                                                        {
                                                           builder1.create ().dismiss ();
                                                           builder1=new AlertDialog.Builder ( GameStartPlayerScreen.this );
                                                        }

                                                        if(claimModelClass.getClaimStatus ().contains ( "Right" ))
                                                            layoutHostClaimsBinding.claimAnimationView.setAnimationFromUrl ("https://assets8.lottiefiles.com/packages/lf20_eaawoubu.json");
                                                        else if(claimModelClass.getClaimStatus ().contains ( "Wrong" ))
                                                            layoutHostClaimsBinding.claimAnimationView.setAnimationFromUrl ("https://assets4.lottiefiles.com/packages/lf20_g0rackmk.json");
                                                        else if(claimModelClass.getClaimStatus ().contains ( "Block" ))
                                                            layoutHostClaimsBinding.claimAnimationView.setAnimationFromUrl ("https://assets4.lottiefiles.com/packages/lf20_bdnjxekx.json");



                                                        builder1.setTitle ( playerName+" claims");
                                                        builder1.setView ( layoutHostClaimsBinding.getRoot () );
                                                        builder1.setCancelable(true);
                                                        builder1.setPositiveButton ( "OK",null );
                                                        builder1.show ();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled ( @NonNull DatabaseError error ) {
                                                }
                                            } );
                                        }
                                    }

                                    @Override
                                    public void onCancelled ( @NonNull DatabaseError error ) {

                                    }
                                } );





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

                });

        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child("gameOver").addValueEventListener ( new ValueEventListener ( ) {
            @Override
            public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                Boolean isGameOver=snapshot.getValue ( Boolean.class );

                if(isGameOver!=null)
                if(isGameOver)
                {
                    binding.gameOverAnimationView.setVisibility ( View.VISIBLE );
                    new Handler (  ).postDelayed ( () -> {
                        Intent intent=new Intent ( GameStartPlayerScreen.this,WinnerBoardActivity.class );
                        intent.putExtra ( "ROOM_CODE",roomCode );
                        startActivity (intent);
                        GameStartPlayerScreen.this.finish ();
                    } ,3000 );
                }
            }

            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {

            }
        } );

        binding.showPlayers.setOnClickListener ( view -> {
            LayoutForJoinedPlayersBinding layoutForJoinedPlayersBinding=LayoutForJoinedPlayersBinding.inflate ( getLayoutInflater () );
            builder1=new AlertDialog.Builder ( GameStartPlayerScreen.this );

            List<PlayerModel> playerModelList1=new ArrayList<> (  );
            AdapterPlayersList adapter1=new AdapterPlayersList ( playerModelList1,false,roomCode ,builder1.create ());
            layoutForJoinedPlayersBinding.joinedPlayersRecycleView.setLayoutManager ( new LinearLayoutManager ( GameStartPlayerScreen.this ) );
            layoutForJoinedPlayersBinding.joinedPlayersRecycleView.setAdapter (adapter1);

            builder1.setView ( layoutForJoinedPlayersBinding.getRoot () );
            builder1.setCancelable(true);
            builder1.setPositiveButton ( "OK" , ( dialogInterface , i ) -> dialogInterface.cancel () );
            builder1.show ();

            Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).orderByKey ().addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                @Override
                public void onDataChange ( @NonNull DataSnapshot snapshot ) {

                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        PlayerModel playerModel = postSnapshot.getValue(PlayerModel.class);
                        if(playerModel!=null)
                        {
                            playerModel.setPlayerId ( postSnapshot.getKey () );
                            playerModelList1.add ( playerModel );
                        }
                    }
                    adapter1.notifyDataSetChanged ();
                }

                @Override
                public void onCancelled ( @NonNull DatabaseError error ) {

                }
            } );
        } );

        binding.winnerBoard.setOnClickListener ( view -> {
            ActivityWinnerBoardBinding winnerBoardBinding=ActivityWinnerBoardBinding.inflate ( getLayoutInflater () );
            builder1=new AlertDialog.Builder ( GameStartPlayerScreen.this );

            List<WinnersModel> winnersModelList=new ArrayList<> (  );
            winnerBoardBinding.winnerListRecycleView.setLayoutManager ( new LinearLayoutManager ( this ) );
            AdapterForWinnerList adapterForWinnerList=new AdapterForWinnerList ( winnersModelList );
            winnerBoardBinding.winnerListRecycleView.setAdapter ( adapterForWinnerList);


            builder1.setView ( winnerBoardBinding.getRoot () );
            builder1.setCancelable(true);
            builder1.setPositiveButton ( "OK" , ( dialogInterface , i ) -> dialogInterface.cancel () );
            builder1.show ();

            Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "claims" ).orderByKey ().addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                @Override
                public void onDataChange ( @NonNull DataSnapshot snapshot ) {

                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        ClaimSuccessModel claimSuccessModel=postSnapshot.getValue ( ClaimSuccessModel.class );
                        if(claimSuccessModel!=null)
                        {
                            claimSuccessModel.setPlayerId ( postSnapshot.getKey () );
                            winnersModelList.add ( new WinnersModel ( postSnapshot.getKey (),"00",claimSuccessModel.getPlayerName () ) );
                        }
                    }
                    adapterForWinnerList.notifyDataSetChanged ();
                }

                @Override
                public void onCancelled ( @NonNull DatabaseError error ) {

                }
            } );

        } );

        binding.exitButton.setOnClickListener ( view -> new AlertDialog.Builder(GameStartPlayerScreen.this)
                .setMessage("Are you sure to Exit Room?")
                .setPositiveButton(android.R.string.yes, ( dialog , which ) -> {
                    GameStartPlayerScreen.this.finish ();
                    // Continue with delete operation
                } )

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show() );
    }

    void setNumberAnimation(String currentNumber)
    {
        binding.pleaseWaitAnimation.setVisibility ( View.INVISIBLE );
        binding.firstNumberAnimationView.setVisibility ( View.VISIBLE);
        binding.secondNumberAnimationView.setVisibility ( View.VISIBLE);

        if(currentNumber.charAt ( 0 )=='0')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_0 );
        else if(currentNumber.charAt ( 0 )=='1')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_1 );
        else if(currentNumber.charAt ( 0 )=='2')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_2 );
        else if(currentNumber.charAt ( 0 )=='3')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_3 );
        else if(currentNumber.charAt ( 0 )=='4')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_4 );
        else if(currentNumber.charAt ( 0 )=='5')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_5 );
        else if(currentNumber.charAt ( 0 )=='6')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_6 );
        else if(currentNumber.charAt ( 0 )=='7')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_7 );
        else if(currentNumber.charAt ( 0 )=='8')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_8 );
        else if(currentNumber.charAt ( 0 )=='9')
            binding.firstNumberAnimationView.setAnimation ( R.raw.animation_number_9 );


        if(currentNumber.charAt ( 1 )=='0')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_0 );
        else if(currentNumber.charAt ( 1 )=='1')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_1 );
        else if(currentNumber.charAt ( 1 )=='2')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_2 );
        else if(currentNumber.charAt ( 1 )=='3')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_3 );
        else if(currentNumber.charAt ( 1 )=='4')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_4 );
        else if(currentNumber.charAt ( 1 )=='5')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_5 );
        else if(currentNumber.charAt ( 1 )=='6')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_6 );
        else if(currentNumber.charAt ( 1 )=='7')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_7 );
        else if(currentNumber.charAt ( 1 )=='8')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_8 );
        else if(currentNumber.charAt ( 1 )=='9')
            binding.secondNumberAnimationView.setAnimation ( R.raw.animation_number_9 );


        numberDisappearTime=6000L;

        if(numberDisappearTimer!=null)
            numberDisappearTimer.cancel ();


        numberDisappearTimer= new Timer();

        numberDisappearTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            Log.d ( "MyTag" , "run: timer" );

            if(numberDisappearTime>0)
                numberDisappearTime-=1000;
            else
            {
                new Handler ( Looper.getMainLooper ()  ).postDelayed ( () -> {
                    binding.pleaseWaitAnimation.setVisibility ( View.VISIBLE );
                    binding.firstNumberAnimationView.setVisibility ( View.INVISIBLE);
                    binding.secondNumberAnimationView.setVisibility ( View.INVISIBLE);
                } ,0 );
                this.cancel ();
            }
        }
    }, 0, 1000);


    }

    void setTicketList()
    {
        allIntegerList.clear ();
        for(int i=0;i<ticketList.size ();i++)
        {
            String ticket=ticketList.get ( i );
            String[] str =  ticket.split ( "-" );
            for(String st:str)
            {
                Integer temp=Integer.parseInt ( st );
                allIntegerList.add ( temp );
            }
        }

    }

    @Override
    public void onBackPressed () {
        new AlertDialog.Builder(GameStartPlayerScreen.this)
                .setMessage("Are you sure to Exit Room?")
                .setPositiveButton(android.R.string.yes, ( dialog , which ) -> {
                    super.onBackPressed ( );
                    // Continue with delete operation
                } )

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}