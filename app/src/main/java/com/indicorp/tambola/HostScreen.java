package com.indicorp.tambola;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.indicorp.tambola.adapters.AdapterPlayersList;
import com.indicorp.tambola.database.Database;
import com.indicorp.tambola.databinding.ActivityHostScreenBinding;
import com.indicorp.tambola.databinding.SelectEventDialogBinding;
import com.indicorp.tambola.model.PlayerModel;
import com.indicorp.tambola.model.RoomModel;
import com.indicorp.tambola.viewModel.HostScreenViewModel;
import com.google.firebase.BuildConfig;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Map;

public class HostScreen extends AppCompatActivity {

    ActivityHostScreenBinding binding;
    HostScreenViewModel hostScreenViewModel;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate ( savedInstanceState );
        binding=ActivityHostScreenBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );
        hostScreenViewModel= new ViewModelProvider (this).get(HostScreenViewModel.class);

        AdView mAdView = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(hostScreenViewModel.getEventList ().size ()==0)
          setEventNullList();
        else
            setEvents();


        setValueChangeListeners ();

        if(hostScreenViewModel.getRoomCode ()!=null)
            setButtonsVisible();

        if(hostScreenViewModel.getAdapterPlayersList ()==null)
        hostScreenViewModel.setAdapterPlayersList (new AdapterPlayersList ( hostScreenViewModel.getPlayerModelList () ));

        binding.soldTicketsRecycleView.setLayoutManager ( new LinearLayoutManager ( HostScreen.this ) );
        binding.soldTicketsRecycleView.setAdapter (hostScreenViewModel.getAdapterPlayersList () );

        binding.totalPrice.setText ( String.valueOf ( hostScreenViewModel.getTotalPrice () ) );

        binding.totalPrice.setOnClickListener ( view -> Toast.makeText ( HostScreen.this , "Auto Increment while Tickets sold" , Toast.LENGTH_SHORT ).show ( ) );

        binding.addPriceCategoryButton.setOnClickListener ( view -> showEventList() );

        binding.generateRoomCodeButton.setOnClickListener ( view -> {
            binding.progressBar.setVisibility ( View.VISIBLE );

            int randomNum= (int) (Math.random ( ) * 10000);
            hostScreenViewModel.setRoomCode ( "R"+randomNum );


            setEventPrice();
            RoomModel roomModel=new RoomModel ( binding.ticketPriceEdittext.getText ().toString (),
                    hostScreenViewModel.getEventPriceList (),hostScreenViewModel.getRoomCode ());

            Database.getDatabaseReference ().child ( "Room" ).child(hostScreenViewModel.getRoomCode ()).child("prices").setValue(roomModel).addOnCompleteListener ( task -> {
                if(task.isSuccessful ())
                {
                    setButtonsVisible ();
                    hostScreenViewModel.getAdapterPlayersList ().notifyDataSetChanged ();

                    Database.getDatabaseReference ().child ( "Room" ).child(hostScreenViewModel.getRoomCode ()).child ( "players" ).addChildEventListener ( new ChildEventListener ( ) {
                        @Override
                        public void onChildAdded ( @NonNull DataSnapshot snapshot , @Nullable String previousChildName ) {
                            PlayerModel playerModel = snapshot.getValue(PlayerModel.class);
                            if(playerModel!=null)
                            {
                                hostScreenViewModel.getPlayerModelList ().add ( playerModel );
                                hostScreenViewModel.getAdapterPlayersList ().notifyDataSetChanged ();

                                hostScreenViewModel.setNumOfTickets ( hostScreenViewModel.getNumOfTickets ( ) +Integer.parseInt ( playerModel.getTicketQuantity () ) );
                                hostScreenViewModel.setTotalPrice ( hostScreenViewModel.getNumOfTickets ( ) * Integer.parseInt (binding.ticketPriceEdittext.getText ().toString () ));
                                binding.totalPrice.setText ( hostScreenViewModel.getTotalPrice ()+"" );
                              //  binding.numOfTickets.setText ( numOfTickets+"" );
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
            hostScreenViewModel.setTotalPrice ( Integer.parseInt ( newValue ) );
           // totalPrice=Integer.parseInt (newValue) * Integer.parseInt (binding.numOfTickets.getText ().toString () );
            binding.totalPrice.setText (( hostScreenViewModel.getTotalPrice ()*hostScreenViewModel.getNumOfTickets ())+"" );
        } );

        binding.ticketPriceDecrementButton.setOnClickListener ( view -> {

            int num=Integer.parseInt (binding.ticketPriceEdittext.getText ().toString () )-10;
            String newValue=num+"";

            if(num>0)
            {
                binding.ticketPriceEdittext.setText (newValue );
                hostScreenViewModel.setTotalPrice ( Integer.parseInt ( newValue ) );
                binding.totalPrice.setText (( hostScreenViewModel.getTotalPrice ()*hostScreenViewModel.getNumOfTickets ())+"" );
            }else
            {
                Toast.makeText ( HostScreen.this , "Ticket Price can not be set less than 10" , Toast.LENGTH_SHORT ).show ( );
            }

        } );

        binding.shareCodeButton.setOnClickListener ( view -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Tambola and play");
            String shareMessage= "\najay room code is\n\n"+hostScreenViewModel.getRoomCode ();
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } );

        binding.saveButton.setOnClickListener ( view -> {
            binding.progressBar.setVisibility ( View.VISIBLE );

            setEventPrice ();
            RoomModel roomModel=new RoomModel ( binding.ticketPriceEdittext.getText ().toString (),
                    hostScreenViewModel.getEventPriceList ( ) , hostScreenViewModel.getRoomCode ( ) );

            Database.getDatabaseReference ().child ( "Room" ).child( hostScreenViewModel.getRoomCode ( ) ).child("prices").setValue(roomModel).addOnCompleteListener ( task -> {
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

        binding.startRoomButton.setOnClickListener ( view -> {

            setEventPrice ();
            int totalValue=0;
            try {
                for (Map.Entry<String,Integer> entry : hostScreenViewModel.getEventPriceList ().entrySet())
                    totalValue=totalValue+entry.getValue();
            }catch (Exception ignored)
            {

            }

            // agar ticket price change hota hai to total value bhi change ho acc to tickets sold
            if(totalValue!= hostScreenViewModel.getTotalPrice ( ))
            {
                Toast.makeText ( HostScreen.this , "Event prices must be equal to Total Price" , Toast.LENGTH_SHORT ).show ( );
            }else
            {
                Database.getDatabaseReference ().child ( "Room" ).child(hostScreenViewModel.getRoomCode ()).child ( "current_number" )
                        .setValue ( 1000 ).addOnCompleteListener ( task -> {
                    if ( task.isSuccessful () )
                    {
                        Database.getDatabaseReference ().child ( "Room" ).child(hostScreenViewModel.getRoomCode ()).child ( "status" ).setValue("started").addOnCompleteListener ( task1 -> {
                            if ( task1.isSuccessful () )
                            {
                                binding.progressBar.setVisibility ( View.VISIBLE );

                                setEventPrice ();
                                RoomModel roomModel=new RoomModel ( binding.ticketPriceEdittext.getText ().toString (),
                                        hostScreenViewModel.getEventPriceList ( ) , hostScreenViewModel.getRoomCode ( ) );

                                Database.getDatabaseReference ().child ( "Room" ).child( hostScreenViewModel.getRoomCode ( ) ).child("prices").setValue(roomModel).addOnCompleteListener ( task2 -> {
                                    if(task2.isSuccessful ())
                                    {
                                        Intent intent=new Intent (HostScreen.this,GameStartHostScreen.class  );
                                        intent.putExtra ( "ROOM_ID",hostScreenViewModel.getRoomCode () );
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

                } );
            }
        } );

    }

    private void setButtonsVisible () {
        binding.shareCodeButton.setVisibility ( View.VISIBLE );
        binding.roomCodeTextView.setVisibility ( View.VISIBLE );
        binding.saveButton.setVisibility ( View.VISIBLE );
        binding.generateRoomCodeButton.setVisibility ( View.INVISIBLE );
        binding.roomCodeTextView.setText ( hostScreenViewModel.getRoomCode () );
        binding.startRoomButton.setVisibility ( View.VISIBLE );
        binding.soldTicketsTextView.setVisibility ( View.VISIBLE );
    }

    private void setEvents () {

        if(  hostScreenViewModel.getEventList ().get ( "first_line"))
            binding.firstLineConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.firstLineConstraintLayout.setVisibility ( View.GONE );


        if( hostScreenViewModel.getEventList ().get ( "middle_line"))
            binding.middleLineConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.middleLineConstraintLayout.setVisibility ( View.GONE );


        if( hostScreenViewModel.getEventList ().get ( "last_line"))
            binding.lastLineConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.lastLineConstraintLayout.setVisibility ( View.GONE );



        if( hostScreenViewModel.getEventList ().get ( "early_five"))
            binding.earlyFiveConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.earlyFiveConstraintLayout.setVisibility ( View.GONE );


        if( hostScreenViewModel.getEventList ().get ( "ladoo"))
            binding.ladooConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.ladooConstraintLayout.setVisibility ( View.GONE );


        if( hostScreenViewModel.getEventList ().get ( "king_corners"))
            binding.kingCornersConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.kingCornersConstraintLayout.setVisibility ( View.GONE );


        if( hostScreenViewModel.getEventList ().get ( "queen_corners"))
            binding.queenCornersConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.queenCornersConstraintLayout.setVisibility ( View.GONE );


        if( hostScreenViewModel.getEventList ().get ( "bamboo"))
            binding.bambooConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.bambooConstraintLayout.setVisibility ( View.GONE );



        if( hostScreenViewModel.getEventList ().get ( "full_house"))
            binding.fullHouseConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.fullHouseConstraintLayout.setVisibility ( View.GONE );


        if( hostScreenViewModel.getEventList ().get ( "second_house"))
            binding.secondHouseConstraintLayout.setVisibility ( View.VISIBLE );
        else
            binding.secondHouseConstraintLayout.setVisibility ( View.GONE );

    }

    private void setEventPrice () {
        hostScreenViewModel.getEventPriceList ().clear ();
        for (Map.Entry<String,Boolean> entry : hostScreenViewModel.getEventList ().entrySet () )
        {
            if(entry.getValue ())
            {
                hostScreenViewModel.getEventPriceList ().put ( entry.getKey ( ) , returnEventValue(entry.getKey ( )) );
            }
        }
    }

    private Integer returnEventValue ( String key ) {

        String value="00";
        if(key.equalsIgnoreCase ( "first_line" ))
           value= binding.firstLinePriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "middle_line" ))
            value= binding.middleLinePriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "last_line" ))
            value= binding.lastLinePriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "early_five" ))
            value= binding.earlyFivePriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "ladoo" ))
            value= binding.ladooPriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "king_corners" ))
            value= binding.kingCornersPriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "queen_corners" ))
            value= binding.queenCornersPriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "bamboo" ))
            value= binding.bambooPriceValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "full_house" ))
            value= binding.fullHouseValueEdittext.getText ().toString ();

        else if(key.equalsIgnoreCase ( "second_house" ))
            value= binding.secondHouseValueEdittext.getText ().toString ();

        return Integer.parseInt ( value );
    }

    private void setEventNullList () {
        hostScreenViewModel.getEventList ().put ( "first_line",true );
        hostScreenViewModel.getEventList ().put ( "middle_line",true );
        hostScreenViewModel.getEventList ().put ( "last_line",true );
        hostScreenViewModel.getEventList ().put ( "early_five",true );
        hostScreenViewModel.getEventList ().put ( "ladoo",false );
        hostScreenViewModel.getEventList ().put ( "king_corners",false );
        hostScreenViewModel.getEventList ().put ( "queen_corners",false );
        hostScreenViewModel.getEventList ().put ( "bamboo",false );
        hostScreenViewModel.getEventList ().put ( "full_house",true );
        hostScreenViewModel.getEventList ().put ( "second_house",false );
    }

    private void showEventList () {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SelectEventDialogBinding selectEventDialogBinding=SelectEventDialogBinding.inflate ( getLayoutInflater () );
        builder.setView (selectEventDialogBinding.getRoot () );
        builder.setCancelable ( true );
        AlertDialog dialog = builder.create();

        setSelectedEventBackground (selectEventDialogBinding);
        setSelectDeselectEventListener ( selectEventDialogBinding );
        selectEventDialogBinding.saveButton.setOnClickListener ( view -> {
            saveButtonAction(selectEventDialogBinding);
            dialog.dismiss ();
        } );

        selectEventDialogBinding.cancelButton.setOnClickListener ( view -> dialog.cancel () );
        dialog.show();
    }

    private void saveButtonAction (SelectEventDialogBinding selectEventDialogBinding) {
        if(selectEventDialogBinding.firstLineTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "first_line",true );
            binding.firstLineConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "first_line",false );
            binding.firstLineConstraintLayout.setVisibility ( View.GONE );
        }


        if(selectEventDialogBinding.secondLineTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "middle_line",true );
            binding.middleLineConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "middle_line",false );
            binding.middleLineConstraintLayout.setVisibility ( View.GONE );
        }


        if(selectEventDialogBinding.lastLineTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "last_line",true );
            binding.lastLineConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "last_line",false );
            binding.lastLineConstraintLayout.setVisibility ( View.GONE );
        }


        if(selectEventDialogBinding.earlyFiveTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "early_five",true );
            binding.earlyFiveConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "early_five",false );
            binding.earlyFiveConstraintLayout.setVisibility ( View.GONE );
        }

        if(selectEventDialogBinding.ladooTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "ladoo",true );
            binding.ladooConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "ladoo",false );
            binding.ladooConstraintLayout.setVisibility ( View.GONE );
        }


        if(selectEventDialogBinding.kingCornersTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "king_corners",true );
            binding.kingCornersConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "king_corners",false );
            binding.kingCornersConstraintLayout.setVisibility ( View.GONE );
        }

        if(selectEventDialogBinding.queenCornersTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "queen_corners",true );
            binding.queenCornersConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "queen_corners",false );
            binding.queenCornersConstraintLayout.setVisibility ( View.GONE );
        }


        if(selectEventDialogBinding.bambooTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "bamboo",true );
            binding.bambooConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "bamboo",false );
            binding.bambooConstraintLayout.setVisibility ( View.GONE );
        }


        if(selectEventDialogBinding.fullHouseTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "full_house",true );
            binding.fullHouseConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "full_house",false );
            binding.fullHouseConstraintLayout.setVisibility ( View.GONE );
        }

        if(selectEventDialogBinding.secondHouseTick.getVisibility ()==View.VISIBLE)
        {
            hostScreenViewModel.getEventList ().put ( "second_house",true );
            binding.secondHouseConstraintLayout.setVisibility ( View.VISIBLE );
        }
        else
        {
            hostScreenViewModel.getEventList ().put ( "second_house",false );
            binding.secondHouseConstraintLayout.setVisibility ( View.GONE );
        }
        setTotalEventPrice();
    }

    void setSelectedEventBackground(SelectEventDialogBinding selectEventDialogBinding) {
        if(! hostScreenViewModel.getEventList ().get ( "first_line" ))
           selectEventDialogBinding.firstLineTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "middle_line" ))
           selectEventDialogBinding.secondLineTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "last_line" ))
           selectEventDialogBinding.lastLineTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "early_five" ))
           selectEventDialogBinding.earlyFiveTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "ladoo" ))
           selectEventDialogBinding.ladooTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "king_corners" ))
           selectEventDialogBinding.kingCornersTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "queen_corners" ))
           selectEventDialogBinding.queenCornersTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "bamboo" ))
           selectEventDialogBinding.bambooTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "full_house" ))
           selectEventDialogBinding.fullHouseTick.setVisibility ( View.INVISIBLE );
       if(! hostScreenViewModel.getEventList ().get ( "second_house" ))
           selectEventDialogBinding.secondHouseTick.setVisibility ( View.INVISIBLE );
    }

    void setSelectDeselectEventListener(SelectEventDialogBinding selectEventDialogBinding) {
        selectEventDialogBinding.firstLineBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.firstLineTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.firstLineTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.firstLineTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.secondLineBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.secondLineTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.secondLineTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.secondLineTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.lastLineBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.lastLineTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.lastLineTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.lastLineTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.earlyFiveBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.earlyFiveTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.earlyFiveTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.earlyFiveTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.ladooBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.ladooTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.ladooTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.ladooTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.kingCornersBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.kingCornersTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.kingCornersTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.kingCornersTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.queenCornersBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.queenCornersTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.queenCornersTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.queenCornersTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.bambooBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.bambooTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.bambooTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.bambooTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.fullHouseBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.fullHouseTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.fullHouseTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.fullHouseTick.setVisibility ( View.VISIBLE );
        } );

        selectEventDialogBinding.secondHouseBox.setOnClickListener ( view -> {
            if(selectEventDialogBinding.secondHouseTick.getVisibility ()==View.VISIBLE)
                selectEventDialogBinding.secondHouseTick.setVisibility ( View.INVISIBLE );
            else
                selectEventDialogBinding.secondHouseTick.setVisibility ( View.VISIBLE );
        } );
    }

    void setTotalEventPrice() {
        Integer totalMoneyValue=0;
        if(!binding.firstLinePriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.firstLineConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.firstLinePriceValueEdittext.getText ().toString () );

        if(!binding.middleLinePriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.middleLineConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.middleLinePriceValueEdittext.getText ().toString () );

        if(!binding.lastLinePriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.lastLineConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.lastLinePriceValueEdittext.getText ().toString () );

        if(!binding.earlyFivePriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.earlyFiveConstraintLayout.getVisibility ()==View.VISIBLE )
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.earlyFivePriceValueEdittext.getText ().toString () );

        if(!binding.ladooPriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.ladooConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.ladooPriceValueEdittext.getText ().toString () );

        if(!binding.kingCornersPriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.kingCornersConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.kingCornersPriceValueEdittext.getText ().toString () );

        if(!binding.queenCornersPriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.queenCornersConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.queenCornersPriceValueEdittext.getText ().toString () );

        if(!binding.bambooPriceValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.bambooConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.bambooPriceValueEdittext.getText ().toString () );

        if(!binding.fullHouseValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.fullHouseConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.fullHouseValueEdittext.getText ().toString () );

        if(!binding.secondHouseValueEdittext.getText ().toString ().equalsIgnoreCase ( "" )
                && binding.secondHouseConstraintLayout.getVisibility ()==View.VISIBLE)
        totalMoneyValue=totalMoneyValue+Integer.parseInt ( binding.secondHouseValueEdittext.getText ().toString () );

        binding.totalEventsMoneyEdittext.setText ( String.valueOf ( totalMoneyValue ) );
    }

    void setValueChangeListeners() {
        TextWatcher textWatcher= new TextWatcher ( ) {
            @Override
            public void beforeTextChanged ( CharSequence charSequence , int i , int i1 , int i2 ) {

            }

            @Override
            public void onTextChanged ( CharSequence charSequence , int i , int i1 , int i2 ) {
                setTotalEventPrice ();
            }

            @Override
            public void afterTextChanged ( Editable editable ) {

            }
        };
        binding.firstLinePriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.middleLinePriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.lastLinePriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.earlyFivePriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.ladooPriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.kingCornersPriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.queenCornersPriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.bambooPriceValueEdittext.addTextChangedListener ( textWatcher );
        binding.fullHouseValueEdittext.addTextChangedListener ( textWatcher );
        binding.secondHouseValueEdittext.addTextChangedListener ( textWatcher );
    }


}