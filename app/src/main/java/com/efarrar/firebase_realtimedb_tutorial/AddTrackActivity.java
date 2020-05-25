package com.efarrar.firebase_realtimedb_tutorial;

/** URL: https://www.youtube.com/watch?v=jEmq1B1gveM&list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1&index=2
 * Code here covers the topics of Relating Data in Firebase
 * The objective of this is to add tracks to the artist.
 * Database wise we are adding a new nod called tracks using the the id of the artist as a foriegn key of sorts though NoSQL doesn't have fks
 * This is were the term "relating" comes in
 * For this modifications were made to MainActivty.class
 * Additional files added are: AddTrackActivity.class, Track.class, and activity_add_track.xml
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {

    TextView textViewArtistName;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    Button buttonAddTrack;

    ListView listViewTracks;

    DatabaseReference databaseReference;

    List<Track> tracks;         //used for the trackList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        textViewArtistName = (TextView) findViewById(R.id.textViewArtistName);
        editTextTrackName = (EditText) findViewById(R.id.editTextTrackName);
        seekBarRating = (SeekBar) findViewById(R.id.seekBarRating);

        buttonAddTrack = (Button) findViewById(R.id.buttonAddTrack);
        listViewTracks = (ListView) findViewById(R.id.listViewTracks);

        tracks = new ArrayList<>();         //instantiating tracks array for trackList

        //intent to get the info from the intent in MainActivity  > AddTrackActivity
        Intent intent = getIntent();

        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME);

        textViewArtistName.setText(name);       //setting artist name to name from intent

        //creates a new node called tracks under the id
        databaseReference = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        buttonAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrack();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //detecting value changes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get all the tracks for the artist
                tracks.clear();

                for(DataSnapshot trackSnapshot : dataSnapshot.getChildren()){
                    Track track = trackSnapshot.getValue(Track.class);          //getting tracks
                    tracks.add(track);      //adding tracks to list
                }

                TrackList trackListAdapter = new TrackList(AddTrackActivity.this, tracks);      //creating new adapter
                listViewTracks.setAdapter(trackListAdapter);        //adding array to adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveTrack() {
        String trackName = editTextTrackName.getText().toString().trim();
        int rating = seekBarRating.getProgress();

        if(!TextUtils.isEmpty(trackName)) {
            String id = databaseReference.push().getKey();

            Track track = new Track(id, trackName, rating);

            databaseReference.child(id).setValue(track);
            trackName = "";

            Toast.makeText(this, "Track saved successfully", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Track name should not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
