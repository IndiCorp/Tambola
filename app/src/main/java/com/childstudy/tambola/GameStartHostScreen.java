package com.childstudy.tambola;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.childstudy.tambola.adapters.AdapterForBoardRecycleView;
import com.childstudy.tambola.adapters.AdapterForWinnerList;
import com.childstudy.tambola.adapters.AdapterPlayersList;
import com.childstudy.tambola.database.Database;
import com.childstudy.tambola.databinding.ActivityGameStartHostScreenBinding;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GameStartHostScreen extends AppCompatActivity {

    ActivityGameStartHostScreenBinding binding;
    List<Integer> allIntegerList=new ArrayList<> (  );
    List<Integer> selectedIntegerList=new ArrayList<> (  );
    AdapterForBoardRecycleView adapterForBoardRecycleView;
    AdapterForBoardRecycleView adapterForDoneNumbersRecycleView;
    static AlertDialog.Builder builder1 ;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding=ActivityGameStartHostScreenBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot ());

        String roomCode=getIntent ().getStringExtra ( "ROOM_ID" );
        binding.roomCodeTextView.setText ( "Room :-"+roomCode );

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        List<Integer> list=new ArrayList<> (  );
        generateRandomNumber ( list );

        String myNumber;
        if(list.get ( list.size ()-1 )<10)
            myNumber="0"+list.get ( list.size ()-1 );
        else
            myNumber=String.valueOf ( list.get ( list.size ()-1 ) );


        setNumberAnimation ( myNumber);

        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).addChildEventListener ( new ChildEventListener ( ) {
            @Override
            public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

                String playerId=snapshot.getKey ();
                if(playerId!=null)
                {
                    Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                            .child ( "ticketQuantity" ).addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                        @Override
                        public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                            String quantityString=snapshot.getValue ( String.class );
                            if(quantityString!=null)
                            {
                              int ticketQuantity=Integer.parseInt ( quantityString );
                              for(int i=0;i<ticketQuantity;i++)
                              {
                                  binding.progressBar.setVisibility ( View.VISIBLE );
                                  int finalI = i;
                                  Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                                          .child ( "tickets" ).push ( ).setValue ( generateTicket () ).addOnCompleteListener ( task -> {
                                              if(task.isSuccessful ())
                                              {
                                                 if( finalI ==(ticketQuantity-1))
                                                 {
                                                     Toast.makeText ( GameStartHostScreen.this , "ticket generate successfully" , Toast.LENGTH_SHORT ).show ( );
                                                 }
                                              }else
                                              {
                                                  GameStartHostScreen.this.finish ();
                                              }
                                              binding.progressBar.setVisibility ( View.INVISIBLE );
                                          } );
                              }

                            }else
                            {
                                Toast.makeText ( GameStartHostScreen.this , "problem occurred" , Toast.LENGTH_SHORT ).show ( );
                                GameStartHostScreen.this.finish ();
                            }
                        }

                        @Override
                        public void onCancelled ( @NonNull DatabaseError error ) {
                            Toast.makeText ( GameStartHostScreen.this , "problem occurred" , Toast.LENGTH_SHORT ).show ( );
                            GameStartHostScreen.this.finish ();
                        }
                    } );


                    Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                            .child ( "tickets" ).addChildEventListener ( new ChildEventListener ( ) {
                        @Override
                        public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

                            String key=snapshot.getKey (  );

                            assert key != null;
                            Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                                    .child ( "tickets" ).child ( key).child ( "claims" ).addValueEventListener ( new ValueEventListener ( ) {
                                @Override
                                public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                                    ClaimModelClass claimModelClass=snapshot.getValue ( ClaimModelClass.class );
                                    if(claimModelClass!=null)
                                    {
                                        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
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
                                                    layoutHostClaimsBinding.ticketStatus.setText ( claimModelClass.getClaimStatus () );
                                                  if(builder1!=null)
                                                  {
                                                      builder1.create ().cancel ();
                                                  }

                                                  if(claimModelClass.getClaimStatus ().contains ( "pending" ))
                                                  {
                                                      if(claimModelClass.getClaimStatus ().contains ( "Right" ))
                                                          layoutHostClaimsBinding.claimAnimationView.setAnimationFromUrl ("https://assets8.lottiefiles.com/packages/lf20_eaawoubu.json");
                                                      else if(claimModelClass.getClaimStatus ().contains ( "Wrong" ))
                                                          layoutHostClaimsBinding.claimAnimationView.setAnimationFromUrl ("https://assets4.lottiefiles.com/packages/lf20_g0rackmk.json");
                                                      builder1 = new AlertDialog.Builder(GameStartHostScreen.this);
                                                      builder1.setTitle ( playerName+" claims");
                                                      builder1.setView ( layoutHostClaimsBinding.getRoot () );
                                                      builder1.setCancelable(false);
                                                      builder1.setPositiveButton ( "APPROVE" , ( dialogInterface , i ) -> {
                                                          claimModelClass.setClaimStatus ( "Approved" );
                                                          Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                                                                  .child ( "tickets" ).child ( key).child ( "claims" ).setValue ( claimModelClass ).addOnCompleteListener ( task -> {
                                                                      if(task.isSuccessful ())
                                                                      {
                                                                          ClaimSuccessModel claimSuccessModel=new ClaimSuccessModel ( playerName,playerId,claimModelClass.getOriginalTicket () );
                                                                          Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "claims" ).child ( claimModelClass.getClaimType () ).setValue ( claimSuccessModel ).addOnCompleteListener (
                                                                                  task1 -> {
                                                                                      if( task1.isSuccessful ())
                                                                                          Toast.makeText ( GameStartHostScreen.this , "Approved Success" , Toast.LENGTH_SHORT ).show ( );
                                                                                      else
                                                                                          Toast.makeText ( GameStartHostScreen.this , "Approved Failed" , Toast.LENGTH_SHORT ).show ( );
                                                                                  } );
                                                                      }
                                                                      else
                                                                      {
                                                                          Toast.makeText ( GameStartHostScreen.this , "Some Problem Occurred" , Toast.LENGTH_SHORT ).show ( );
                                                                      }
                                                                  } );
                                                      } );
                                                      builder1.setNegativeButton ( "BLOCK" , ( dialogInterface , i ) -> Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "blocks" ).child ( "playerId" ).setValue ( claimModelClass.getPlayerId() ).addOnCompleteListener ( task -> {
                                                          if(task.isSuccessful ())
                                                          {
                                                              claimModelClass.setClaimStatus ( "Blocked" );
                                                              Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                                                                      .child ( "tickets" ).child ( key).child ( "claims" ).setValue ( claimModelClass ).addOnCompleteListener ( task12 -> {
                                                                          if( task12.isSuccessful ())
                                                                          {
                                                                            //  Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "blocks" ).child("players").child ( playerId ).setValue ( "Blocked" ).addOnCompleteListener ( task121 -> {
                                                                              Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "blocks" ).child("tickets").child (key).setValue ( "Blocked" ).addOnCompleteListener ( task121 -> {
                                                                                  if( task121.isSuccessful ())
                                                                                      Toast.makeText ( GameStartHostScreen.this , "Blocked Success" , Toast.LENGTH_SHORT ).show ( );
                                                                                  else
                                                                                      Toast.makeText ( GameStartHostScreen.this , "Blocked Failed" , Toast.LENGTH_SHORT ).show ( );
                                                                              } );
                                                                          }
                                                                          else
                                                                          {
                                                                              Toast.makeText ( GameStartHostScreen.this , "Some Problem Occurred" , Toast.LENGTH_SHORT ).show ( );
                                                                          }
                                                                      } );
                                                          } else
                                                          {
                                                              Toast.makeText ( GameStartHostScreen.this , "Some Problem Occurred" , Toast.LENGTH_SHORT ).show ( );
                                                          }


                                                      } ) );
                                                      builder1.setNeutralButton ( "REJECT" , ( dialogInterface , i ) -> {
                                                          claimModelClass.setClaimStatus ( "Rejected" );
                                                          builder1.create ().cancel ();
                                                          Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).child ( playerId )
                                                                  .child ( "tickets" ).child ( key).child ( "claims" ).setValue ( claimModelClass ).addOnCompleteListener ( task -> {
                                                                      if(task.isSuccessful ())
                                                                      {
                                                                          Toast.makeText ( GameStartHostScreen.this , "Rejected" , Toast.LENGTH_SHORT ).show ( );
                                                                      }
                                                                      else
                                                                      {
                                                                          Toast.makeText ( GameStartHostScreen.this , "Some Problem Occurred" , Toast.LENGTH_SHORT ).show ( );
                                                                      }
                                                                  } );
                                                      } );
                                                      builder1.show ();
                                                  }
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


                }else
                {
                    Toast.makeText ( GameStartHostScreen.this , "problem occurred" , Toast.LENGTH_SHORT ).show ( );
                    GameStartHostScreen.this.finish ();
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

        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child("gameOver").addValueEventListener ( new ValueEventListener ( ) {
            @Override
            public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                Boolean isGameOver=snapshot.getValue ( Boolean.class );

                if(isGameOver!=null)
                    if(isGameOver)
                    {
                        binding.gameOverAnimationView.setVisibility ( View.VISIBLE );
                        new Handler (  ).postDelayed ( () -> {
                                Intent intent=new Intent ( GameStartHostScreen.this,WinnerBoardActivity.class );
                                intent.putExtra ( "ROOM_CODE",roomCode );
                                startActivity (intent);
                                GameStartHostScreen.this.finish ();

                        } ,3000 );
                    }
            }
            @Override
            public void onCancelled ( @NonNull DatabaseError error ) {

            }
        } );

        binding.publishNumberButton.setOnClickListener ( view -> {
            binding.progressBar.setVisibility ( View.VISIBLE );
            Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "current_number" )
                    .setValue ( list.get ( list.size ()-1 ) ).addOnCompleteListener ( task -> {
                        if(task.isSuccessful ())
                        {
                            selectedIntegerList.add ( 0,list.get ( list.size ()-1 ));
                            adapterForBoardRecycleView.notifyItemChanged (list.get ( list.size ()-1)-1);

                            adapterForDoneNumbersRecycleView.notifyDataSetChanged ();

                            if(list.size ()==90)
                            {
                                binding.gameOverAnimationView.setVisibility ( View.VISIBLE );
                                Database.getDatabaseReference ().child ( "Room" ).child ( roomCode ).child("gameOver").setValue ( true ).addOnCompleteListener ( task13 -> {
                                    if( task13.isSuccessful ())
                                    {
                                        new Handler (  ).postDelayed ( () -> {
                                            Intent intent=new Intent ( GameStartHostScreen.this,WinnerBoardActivity.class );
                                            intent.putExtra ( "ROOM_CODE",roomCode );
                                            startActivity (intent);
                                            GameStartHostScreen.this.finish ();
                                        } ,2000 );
                                    }else
                                    {
                                        Toast.makeText ( GameStartHostScreen.this , "Some problem occurred" , Toast.LENGTH_SHORT ).show ( );
                                    }
                                } );
                            }else
                            {
                                generateRandomNumber ( list );
                                String myNumber1;
                                if(list.get ( list.size ()-1 )<10)
                                    myNumber1 ="0"+ list.get ( list.size ( ) - 1 );
                                else
                                    myNumber1 =String.valueOf ( list.get ( list.size ()-1 ) );

                                setNumberAnimation ( myNumber1 );
                            }



                        }else
                        {
                            Toast.makeText ( GameStartHostScreen.this , "problem Occurred" , Toast.LENGTH_SHORT ).show ( );
                        }
                        binding.progressBar.setVisibility ( View.INVISIBLE );
                    } );

        } );

        binding.showPlayers.setOnClickListener ( view -> {
            LayoutForJoinedPlayersBinding layoutForJoinedPlayersBinding=LayoutForJoinedPlayersBinding.inflate ( getLayoutInflater () );
            builder1=new AlertDialog.Builder ( GameStartHostScreen.this );

            List<PlayerModel> playerModelList1=new ArrayList<> (  );
            AdapterPlayersList adapter1=new AdapterPlayersList ( playerModelList1,true,roomCode ,builder1.create ());
            layoutForJoinedPlayersBinding.joinedPlayersRecycleView.setLayoutManager ( new LinearLayoutManager ( GameStartHostScreen.this ) );
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
            builder1=new AlertDialog.Builder ( GameStartHostScreen.this );

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

        binding.exitButton.setOnClickListener ( view -> new AlertDialog.Builder(GameStartHostScreen.this)
                .setMessage("Are you sure to Exit Room?")
                .setPositiveButton(android.R.string.yes, ( dialog , which ) -> {
                    GameStartHostScreen.this.finish ();
                    // Continue with delete operation
                } )

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show() );




        for(int i=1;i<91;i++)
            allIntegerList.add ( i );

        binding.boardRecycleView.setLayoutManager ( new GridLayoutManager (GameStartHostScreen.this, 9 ) );
        adapterForBoardRecycleView=new AdapterForBoardRecycleView (selectedIntegerList,allIntegerList,false  );
        binding.boardRecycleView.setAdapter ( adapterForBoardRecycleView );

        binding.doneNumbersListRecycleView.setLayoutManager (new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterForDoneNumbersRecycleView=new AdapterForBoardRecycleView (selectedIntegerList,selectedIntegerList,true  );
        binding.doneNumbersListRecycleView.setAdapter ( adapterForDoneNumbersRecycleView );

    }

    void generateRandomNumber(List<Integer> list)
    {
        boolean tempForLoop=true;
        while (tempForLoop)
        {
            int temp= (int) (Math.random ()*100);

            if(temp>90)
                continue;
            else if(temp==0)
                continue;

            boolean checklist=false;
            for(int i=0;i<list.size ();i++)
            {
                if(list.get( i )==temp)
                {
                    checklist=true;
                    break;
                }
            }

            if(checklist)
                continue;

            list.add (temp);
            tempForLoop=false;
        }
    }
    void setNumberAnimation(String currentNumber)
    {
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
    }

    String generateTicket() {
        int randomNumber = (int) (Math.random ( ) * 100);
        int pattern = randomNumber % 3;


        List<Integer> threeNumColumn = new ArrayList<> ( );
        List<Integer> twoNumColumn = new ArrayList<> ( );
        List<Integer> oneNumColumn = new ArrayList<> ( );


        //pattern  306
        if ( pattern == 0 ) {
                while (threeNumColumn.size ( ) != 3) {
                      boolean exist = false;
                      int rand = ((int) (Math.random ( ) * 1000)) % 9;

                      for (int i = 0; i < threeNumColumn.size ( ); i++) {
                          if ( threeNumColumn.contains ( rand ) )
                              exist = true;
                          }
                          if ( exist )
                            continue;
                threeNumColumn.add ( rand );
            }
                for(int i=0;i<9;i++)
                {
                    if(threeNumColumn.contains ( i ))
                        continue;
                    oneNumColumn.add ( i );
                }
        }
        //pattern  225
        if ( pattern == 1 ) {
            while (threeNumColumn.size ( ) != 2) {
                boolean exist = false;
                int rand = ((int) (Math.random ( ) * 1000)) % 9;

                for (int i = 0; i < threeNumColumn.size ( ); i++) {
                    if ( threeNumColumn.contains ( rand ) )
                        exist = true;
                }
                if ( exist )
                    continue;
                threeNumColumn.add ( rand );
            }
            while (twoNumColumn.size ( ) != 2) {
                boolean exist = false;
                int rand = ((int) (Math.random ( ) * 1000)) % 9;

                for (int i = 0; i < threeNumColumn.size ( ); i++) {
                    if ( threeNumColumn.contains ( rand ) )
                        exist = true;
                }
                for (int i = 0; i < twoNumColumn.size ( ); i++) {
                    if ( twoNumColumn.contains ( rand ) )
                        exist = true;
                }
                if ( exist )
                    continue;
                twoNumColumn.add ( rand );
            }

            for(int i=0;i<9;i++)
            {
                if(threeNumColumn.contains ( i ) || twoNumColumn.contains ( i ))
                    continue;
                oneNumColumn.add ( i );
            }
        }
        //pattern  144
        if ( pattern == 2 ) {

                threeNumColumn.add ( ((int) (Math.random ( ) * 1000)) % 9 );

                while (twoNumColumn.size ( ) != 4) {
                boolean exist = false;
                int rand = ((int) (Math.random ( ) * 1000)) % 9;

                    if ( threeNumColumn.contains ( rand ) )
                        exist = true;

                for (int i = 0; i < twoNumColumn.size ( ); i++) {
                    if ( twoNumColumn.contains ( rand ) )
                        exist = true;
                }
                if ( exist )
                    continue;
                twoNumColumn.add ( rand );
            }

            for(int i=0;i<9;i++)
            {
                if(threeNumColumn.contains ( i ) || twoNumColumn.contains ( i ))
                    continue;
                oneNumColumn.add ( i );
            }
        }

        HashMap<String,String> ticketHashMap=new HashMap<> (  );


        //assign every columns how many number have and then make single array
        for (int i = 0; i < threeNumColumn.size ( ); i++) {
          ticketHashMap.put ( ""+threeNumColumn.get ( i ),"three" );
        }
        for (int i = 0; i < twoNumColumn.size ( ); i++) {
            ticketHashMap.put ( ""+twoNumColumn.get ( i ),"two" );
        }
        for (int i = 0; i < oneNumColumn.size ( ); i++) {
            ticketHashMap.put ( ""+oneNumColumn.get ( i ),"one" );
        }


        HashMap<String,List<Integer>> columnValueMap=new HashMap<> (  );

        //for convert vertical to horizontal
        List<Integer> firstRaw=new ArrayList<>();
        List<Integer> secondRaw=new ArrayList<>();
        List<Integer> thirdRaw=new ArrayList<>();


        boolean conditionCheck=true;

        while(conditionCheck)
        {
            firstRaw.clear ();
            secondRaw.clear ();
            thirdRaw.clear ();
            columnValueMap.clear ();


            //ticketHashMap size is always 9
            for(int i=0;i<ticketHashMap.size ();i++)
            {
                int minLimit=(i*10);
                if( Objects.equals ( ticketHashMap.get ( "" + i ) , "three" ) )
                {
                    List<Integer> tempList=new ArrayList<> (  );
                    while (tempList.size ()!=3)
                    {
                        int temp=(int) (Math.random ()*10)+1+minLimit;
                        if(tempList.contains ( temp ))
                            continue;
                        tempList.add ( temp );
                    }
                    Collections.sort(tempList);
                    columnValueMap.put (""+ i,tempList);
                }
                else if( Objects.equals ( ticketHashMap.get ( "" + i ) , "two" ) )
                {
                    List<Integer> tempList=new ArrayList<> (  );
                    while (tempList.size ()!=2)
                    {
                        int temp=(int) (Math.random ()*10)+1+minLimit;
                        if(tempList.contains ( temp ))
                            continue;
                        tempList.add (temp );
                    }
                    Collections.sort(tempList);

                    int temporary= (int) ((Math.random ()*100)%3);
                    tempList.add ( temporary,0 );

                    columnValueMap.put (""+ i,tempList);
                }
                else if( Objects.equals ( ticketHashMap.get ( "" + i ) , "one" ) )
                {
                    List<Integer> tempList=new ArrayList<> (  );

                    int temporaryNumber= (int) ((Math.random ()*100)%3);
                    tempList.add ( 0 );
                    tempList.add ( 0 );
                    tempList.add ( temporaryNumber,(int) (Math.random ()*10)+1+minLimit );
                    columnValueMap.put (""+ i,tempList);
                }
            }


            int firstElementCount=0;
            int secondElementCount=0;
            int thirdElementCount=0;
            for(int i=0;i<ticketHashMap.size ();i++)
            {
                List<Integer> myList=columnValueMap.get("" + i);
                firstRaw.add(myList.get(0));
                secondRaw.add(myList.get(1));
                thirdRaw.add(myList.get(2));
            }
            for(int temp:firstRaw)
            {
                if(temp==0)
                    continue;
                firstElementCount++;
            }
            for(int temp:secondRaw)
            {
                if(temp==0)
                    continue;
                secondElementCount++;
            }
            for(int temp:thirdRaw)
            {
                if(temp==0)
                    continue;
                thirdElementCount++;
            }

            if(firstElementCount==5 && secondElementCount==5 && thirdElementCount==5)
            {
                conditionCheck=false;
            }
        }



        StringBuilder str= new StringBuilder ( );
        for(int i=0;i< firstRaw.size ( );i++)
        {
            str.append ( firstRaw.get ( i ) ).append ( "-" );
        }
        for(int i=0;i< secondRaw.size ( );i++)
        {
            str.append ( secondRaw.get ( i ) ).append ( "-" );
        }
        for(int i=0;i< thirdRaw.size ( );i++)
        {
            str.append ( thirdRaw.get ( i ) ).append ( "-" );
        }
        return str.toString ( );
    }


    @Override
    public void onBackPressed () {
        new AlertDialog.Builder(GameStartHostScreen.this)
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