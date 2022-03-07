package com.childstudy.tambola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.childstudy.tambola.adapters.AdapterPlayersList;
import com.childstudy.tambola.database.Database;
import com.childstudy.tambola.databinding.ActivityHostScreenBinding;
import com.childstudy.tambola.model.PlayerModel;
import com.childstudy.tambola.model.RoomModel;
import com.google.firebase.BuildConfig;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HostScreen extends AppCompatActivity {

    ActivityHostScreenBinding binding;
    private String roomCode;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding=ActivityHostScreenBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );


        binding.generateRoomCodeButton.setOnClickListener ( view -> {
            binding.progressBar.setVisibility ( View.VISIBLE );

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference ( );

            int randomNum= (int) (Math.random ( ) * 10000);
            roomCode= "R"+randomNum;

            RoomModel roomModel=new RoomModel ( binding.ticketPriceEdittext.getText ().toString (),
                    binding.firstLinePriceEdittext.getText ().toString (),
                    binding.middleLinePriceEdittext.getText ().toString (),
                    binding.lastLinePriceEdittext.getText ().toString (),
                    binding.firstLastPriceEdittext.getText ().toString (),
                    binding.fullHouseLinePriceEdittext.getText ().toString (),roomCode );

            myRef.child ( "Room" ).child(roomCode).setValue(roomModel).addOnCompleteListener ( task -> {
                if(task.isSuccessful ())
                {
                    List<PlayerModel> playerModelList=new ArrayList<> (  );
                    binding.shareCodeButton.setVisibility ( View.VISIBLE );
                    binding.roomCodeTextView.setVisibility ( View.VISIBLE );
                    binding.saveButton.setVisibility ( View.VISIBLE );
                    binding.generateRoomCodeButton.setVisibility ( View.INVISIBLE );
                    binding.roomCodeTextView.setText ( roomCode );
                    binding.startRoomButton.setVisibility ( View.VISIBLE );
                    binding.soldTicketsTextView.setVisibility ( View.VISIBLE );



                    binding.soldTicketsRecycleView.setLayoutManager ( new LinearLayoutManager ( HostScreen.this ) );
                    AdapterPlayersList adapter=new AdapterPlayersList ( playerModelList );
                    binding.soldTicketsRecycleView.setAdapter (adapter );

                    Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "players" ).addChildEventListener ( new ChildEventListener ( ) {
                        @Override
                        public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {

                            PlayerModel playerModel = snapshot.getValue(PlayerModel.class);
                            if(playerModel!=null)
                            {
                                playerModelList.add ( playerModel );
                                adapter.notifyDataSetChanged ();
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
                else
                {
                    Toast.makeText ( HostScreen.this , "Some Problem Occur" , Toast.LENGTH_SHORT ).show ( );
                }
                binding.progressBar.setVisibility ( View.INVISIBLE );
            } );
        } );

        binding.ticketPriceIncrementButton.setOnClickListener ( view -> {
            String newValue=Integer.parseInt (binding.ticketPriceEdittext.getText ().toString () )+10+"";
            binding.ticketPriceEdittext.setText (newValue );
        } );

        binding.ticketPriceDecrementButton.setOnClickListener ( view -> {
            String newValue=Integer.parseInt (binding.ticketPriceEdittext.getText ().toString () )-10+"";
            binding.ticketPriceEdittext.setText (newValue );
        } );

        binding.shareCodeButton.setOnClickListener ( view -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Tambola and play");
            String shareMessage= "\najay room code is\n\n"+roomCode;
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } );

        binding.saveButton.setOnClickListener ( view -> {
            binding.progressBar.setVisibility ( View.VISIBLE );
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference ( );
            RoomModel roomModel=new RoomModel ( binding.ticketPriceEdittext.getText ().toString (),
                    binding.firstLinePriceEdittext.getText ().toString (),
                    binding.middleLinePriceEdittext.getText ().toString (),
                    binding.lastLinePriceEdittext.getText ().toString (),
                    binding.firstLastPriceEdittext.getText ().toString (),
                    binding.fullHouseLinePriceEdittext.getText ().toString (),roomCode );

            myRef.child ( "Room" ).child(roomCode).child("prices").setValue(roomModel).addOnCompleteListener ( task -> {
                if(task.isSuccessful ())
                {
                    Toast.makeText ( HostScreen.this , "Update Success" , Toast.LENGTH_SHORT ).show ( );
                }
                else
                {
                    Toast.makeText ( HostScreen.this , "Some Problem Occur" , Toast.LENGTH_SHORT ).show ( );
                }
                binding.progressBar.setVisibility ( View.INVISIBLE );
            } );
        } );

        binding.startRoomButton.setOnClickListener ( view -> Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "current_number" )
                .setValue ( 1000 ).addOnCompleteListener ( task -> {
                    if ( task.isSuccessful () )
                    {
                        Database.getDatabaseReference ().child ( "Room" ).child(roomCode).child ( "status" ).setValue(true).addOnCompleteListener ( task1 -> {
                            if ( task1.isSuccessful () )
                            {
                                binding.progressBar.setVisibility ( View.VISIBLE );
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference ( );
                                RoomModel roomModel=new RoomModel ( binding.ticketPriceEdittext.getText ().toString (),
                                        binding.firstLinePriceEdittext.getText ().toString (),
                                        binding.middleLinePriceEdittext.getText ().toString (),
                                        binding.lastLinePriceEdittext.getText ().toString (),
                                        binding.firstLastPriceEdittext.getText ().toString (),
                                        binding.fullHouseLinePriceEdittext.getText ().toString (),roomCode );

                                myRef.child ( "Room" ).child(roomCode).child("prices").setValue(roomModel).addOnCompleteListener ( task2 -> {
                                    if(task2.isSuccessful ())
                                    {
                                        Toast.makeText ( HostScreen.this , "Update Success" , Toast.LENGTH_SHORT ).show ( );
                                        Intent intent=new Intent (HostScreen.this,GameStartHostScreen.class  );
                                        intent.putExtra ( "ROOM_ID",roomCode );
                                        startActivity ( intent);
                                        HostScreen.this.finish ();
                                    }
                                    else
                                    {
                                        Toast.makeText ( HostScreen.this , "Some Problem Occur" , Toast.LENGTH_SHORT ).show ( );
                                    }
                                    binding.progressBar.setVisibility ( View.INVISIBLE );
                                } );

                            }else
                            {
                                Toast.makeText ( HostScreen.this , "some problem occur" , Toast.LENGTH_SHORT ).show ( );
                            }
                        } );


                    }else
                    {
                        Toast.makeText ( HostScreen.this , "some problem occur" , Toast.LENGTH_SHORT ).show ( );
                    }

                } ) );


    }
}