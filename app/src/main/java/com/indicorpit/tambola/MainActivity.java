package com.indicorpit.tambola;

import static com.indicorpit.tambola.util.StorageClass.getName;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.indicorpit.tambola.database.Database;
import com.indicorpit.tambola.databinding.ActivityMainBinding;
import com.indicorpit.tambola.databinding.EnterNameLayoutBinding;
import com.indicorpit.tambola.model.RoomModel;
import com.indicorpit.tambola.util.StorageClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    static MediaPlayer mp;
    ActivityMainBinding binding;
    String m_Text;
    boolean exit=false;

    public static boolean MUSIC_PLAY=true;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        mp= MediaPlayer.create(this, R.raw.background_sound);
        binding=ActivityMainBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 ) {
            setTurnScreenOn ( true );
        }

        MobileAds.initialize(this, initializationStatus -> {
        } );

        AdView mAdView = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        binding.hostAnimationView.setOnClickListener ( view -> startActivity ( new Intent ( MainActivity.this,HostScreen.class ) ) );

        binding.contactUsTextview.setOnClickListener ( view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://indicorpit.com/"));
            startActivity(browserIntent);
        } );

        binding.developByIndicorp.setOnClickListener ( view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://indicorpit.com/"));
            startActivity(browserIntent);
        } );

        binding.playAnimationView.setOnClickListener ( view -> clickPlayButton () );

        binding.playerName.setOnClickListener ( view -> inputName () );

        binding.speakerIcon.setOnClickListener ( view -> {
            if(MUSIC_PLAY)
            {
                mp.pause ();
                MUSIC_PLAY=false;
                binding.speakerIcon.setImageResource ( R.drawable.ic_off_speaker );
            }
            else
            {
                mp.start ();
                MUSIC_PLAY=true;
                binding.speakerIcon.setImageResource ( R.drawable.ic_speaker_icon );
            }
        } );


        String name = getName (this);
        if(name.equalsIgnoreCase ( "default_value" ))
        {
            inputName ();
        }else
        {
            binding.playerName.setText ( getName (this) );
        }
    }

    @Override
    protected void onResume () {
        super.onResume ( );

        if(MUSIC_PLAY)
        {
            mp.start();
            mp.setLooping ( true );
        }
    }

    @Override
    protected void onPause () {
        mp.pause ();
        super.onPause ( );
    }



    void inputName()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EnterNameLayoutBinding enterNameLayoutBinding=EnterNameLayoutBinding.inflate ( getLayoutInflater () );
        builder.setView (enterNameLayoutBinding.getRoot () );
        builder.setCancelable ( false );
        AlertDialog dialog = builder.create();

        enterNameLayoutBinding.okButton.setOnClickListener ( view -> {
            m_Text = enterNameLayoutBinding.nameEdittext.getText().toString();
            if(m_Text.isEmpty ())
            {
                dialog.dismiss ();
                Toast.makeText ( getApplicationContext () , "please enter name" , Toast.LENGTH_SHORT ).show ( );
                inputName ();
            }else
            {
                StorageClass.saveName ( m_Text,MainActivity.this );
                binding.playerName.setText ( m_Text );
                dialog.dismiss ();
            }
        } );

        enterNameLayoutBinding.cancelButton.setOnClickListener ( view -> {
            if( getName (MainActivity.this ).equalsIgnoreCase ( "default_value" ))
            {
                int num= (int) (Math.random ( ) * 10000);
                String name="User"+num;
                StorageClass.saveName ( name ,MainActivity.this);
                binding.playerName.setText ( name );
            }
            dialog.dismiss ();
        } );
        dialog.show();
    }

    void clickPlayButton()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EnterNameLayoutBinding enterNameLayoutBinding=EnterNameLayoutBinding.inflate ( getLayoutInflater () );
        builder.setView (enterNameLayoutBinding.getRoot () );
        enterNameLayoutBinding.okButton.setText ( getString( R.string.enter) );
        enterNameLayoutBinding.enterNameTextView.setText ( getString( R.string.enter_room_code) );
        enterNameLayoutBinding.nameEdittext.setInputType ( InputType.TYPE_CLASS_NUMBER );

        builder.setCancelable ( false );
        AlertDialog dialog = builder.create();

        enterNameLayoutBinding.okButton.setOnClickListener ( view -> {
            String roomCode = enterNameLayoutBinding.nameEdittext.getText().toString();

            if(!roomCode.equalsIgnoreCase ( "" ))
            {
                dialog.dismiss ();
                binding.progressBar.setVisibility ( View.VISIBLE );
                Query myTopPostsQuery = Database.getDatabaseReference ().child("Room").child( roomCode );
                myTopPostsQuery.addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                    @Override
                    public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                        RoomModel roomModel = snapshot.getValue(RoomModel.class);

                        if(null!=roomModel)
                        {
                            Database.getDatabaseReference ().child ( "Room" ).child ( roomCode ).child ( "status" ).addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                                @Override
                                public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                                    String roomStatus=snapshot.getValue ( String.class );
                                    if ( roomStatus!=null )
                                    {
                                        if(roomStatus.equalsIgnoreCase ( "started" ))
                                        {
                                            Toast.makeText ( MainActivity.this , "Room Already Started", Toast.LENGTH_SHORT ).show ( );
                                        }else
                                        {
                                            Intent intent=new Intent (MainActivity.this,PlayRoom.class);
                                            intent.putExtra ( "ROOM_ID", roomCode );
                                            startActivity (intent );
                                        }
                                    }
                                    else
                                    {
                                        Intent intent=new Intent (MainActivity.this,PlayRoom.class);
                                        intent.putExtra ( "ROOM_ID", roomCode );
                                        startActivity (intent );
                                    }
                                    Log.d ( "MyTag" , "onDataChange:  after room status listener " );
                                }
                                @Override
                                public void onCancelled ( @NonNull DatabaseError error ) {
                                    Toast.makeText ( MainActivity.this , error.toString (), Toast.LENGTH_SHORT ).show ( );
                                }
                            } );
                        }
                        else
                        {
                            Toast.makeText ( MainActivity.this , "room not found", Toast.LENGTH_SHORT ).show ( );
                        }

                        binding.progressBar.setVisibility ( View.INVISIBLE );
                    }

                    @Override
                    public void onCancelled ( @NonNull DatabaseError databaseError ) {
                        Toast.makeText ( MainActivity.this , "exception="+databaseError.toException() , Toast.LENGTH_SHORT ).show ( );
                        Log.w("MyTag", "loadPost:onCancelled", databaseError.toException());
                        binding.progressBar.setVisibility ( View.INVISIBLE );
                    }
                } );

            }else
            {
                Toast.makeText ( MainActivity.this , "Please enter code" , Toast.LENGTH_SHORT ).show ( );
            }
        } );

        enterNameLayoutBinding.cancelButton.setOnClickListener ( view -> dialog.dismiss () );
        dialog.show();

    }

    @Override
    public void onBackPressed () {
        if(exit)
            super.onBackPressed ( );
        else
            Toast.makeText ( this , "Press again to exit" , Toast.LENGTH_SHORT ).show ( );

        exit=true;
        new Handler (  ).postDelayed ( () -> exit=false ,3000 );

    }
}