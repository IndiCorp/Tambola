package com.childstudy.tambola;

import static com.childstudy.tambola.util.StorageClass.getName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.childstudy.tambola.databinding.ActivityMainBinding;
import com.childstudy.tambola.model.RoomModel;
import com.childstudy.tambola.util.StorageClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mp;
    ActivityMainBinding binding;
    String m_Text;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        mp= MediaPlayer.create(this, R.raw.background_sound);
        binding=ActivityMainBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );

        binding.hostButton.setOnClickListener ( view -> startActivity ( new Intent ( MainActivity.this,HostScreen.class ) ) );

        binding.playButton.setOnClickListener ( view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            final EditText edittext = new EditText(binding.getRoot ().getContext ());
            alert.setTitle("Enter Room Code");
            alert.setView(edittext);



            alert.setPositiveButton("Enter", ( dialog , whichButton ) -> {
                binding.progressBar.setVisibility ( View.VISIBLE );
                String roomId = edittext.getText().toString();
                Query myTopPostsQuery =  FirebaseDatabase.getInstance().getReference ( ).child("Room").child(roomId);
                myTopPostsQuery.addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                    @Override
                    public void onDataChange ( @NonNull DataSnapshot snapshot ) {
                        RoomModel roomModel = snapshot.getValue(RoomModel.class);

                        if(null!=roomModel)
                        {
                            Intent intent=new Intent (MainActivity.this,PlayRoom.class);
                            intent.putExtra ( "ROOM_ID",roomId );
                            startActivity (intent );
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
            } );



            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            alert.show();
        } );

        binding.playerName.setOnClickListener ( view -> inputName () );


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
        mp.start();
        mp.setLooping ( true );
    }

    @Override
    protected void onPause () {
        mp.pause ();
        super.onPause ( );
    }

    void inputName()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Name");
        final EditText input = new EditText(this);
        input.setInputType( InputType.TYPE_CLASS_TEXT );
        builder.setView(input);
        builder.setCancelable ( false );
        builder.setPositiveButton("OK", ( dialog , which ) -> {
            m_Text = input.getText().toString();

            if(m_Text.isEmpty ())
            {
                dialog.cancel ();
                Toast.makeText ( getApplicationContext () , "please enter name" , Toast.LENGTH_SHORT ).show ( );
                inputName ();
            }else
            {
                StorageClass.saveName ( m_Text,MainActivity.this );
                binding.playerName.setText ( m_Text );
            }
        } );
        builder.setNegativeButton("Cancel", ( dialog , which ) -> {

            if( getName (this ).equalsIgnoreCase ( "default_value" ))
            {
                int num= (int) (Math.random ( ) * 10000);
                String name="User"+num;
                StorageClass.saveName ( name ,this);
                binding.playerName.setText ( name );
            }
            dialog.cancel();
        } );

        builder.show();
    }


}