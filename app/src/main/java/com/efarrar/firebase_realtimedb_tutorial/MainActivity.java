package com.efarrar.firebase_realtimedb_tutorial;

/** URL: https://www.youtube.com/playlist?list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1
 * https://www.simplifiedcoding.net/firebase-realtime-database-crud/
 * https://github.com/probelalkhan/FirebaseRealtimeDatabaseTutorial
 *
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    Button buttonAdd;
    Spinner spinnerGenres;

    //Database Reference
    DatabaseReference databaseArtist;

    ListView listViewArtist;
    List<Artist> artistList;        //List to store artist

    //to be used to pass these values to the intent that goes to AddTrackActivity
    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID = "artistid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * getReference can be left empty; however, if empty it will get info from root node
         * Here we are getting the artist nod
         **/
        databaseArtist = FirebaseDatabase.getInstance().getReference("artist");

        editTextName = (EditText) findViewById(R.id.editTextName);
        buttonAdd = (Button) findViewById(R.id.buttonAddArtist);
        spinnerGenres = (Spinner) findViewById(R.id.spinnerGenres);

        listViewArtist = (ListView) findViewById(R.id.listViewArtists);
        artistList = new ArrayList<>();



        //adding onclicklistenter to button to call addArtist
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });

        /**onCLickListener on the list view to pass id and name to AddTrackActivity.class */
        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //when this is clicked open the Track Activity
                Artist artist = artistList.get(position);

                Intent intent = new Intent(getApplicationContext(), AddTrackActivity.class);

                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());

                startActivity(intent);
            }
        });

    } //[END: onCreate()]

    /**Attaching value listener to databaseReference obj */
    @Override
    protected void onStart() {
        super.onStart();

        databaseArtist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //will be executed every time there is a change in the DB
                artistList.clear();             //clearing the previous artist list

                /**Iterating through all nodes and adding them to array */
                for(DataSnapshot artistSnapshot : dataSnapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);      //getting artist

                    artistList.add(artist);     //adding artist to list
                }

                ArtistList adapter = new ArtistList(MainActivity.this, artistList);     //creating adapter
                listViewArtist.setAdapter(adapter);     //attaching adapter to the listView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**Function to get the values entered */
    private void addArtist() {
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenres.getSelectedItem().toString();

        /**
         * if name is !empty
         * get the key identifier from the pushed element and assign it to String id
         * Then create a new artist
         * use Set value to store the artist; this must be done by id to child node
         * By doing this this way we make it so a unique id is made everytime artist information is pushed.
         * That id is also used to store the data in the child node.
         */
        if(!TextUtils.isEmpty(name)){
            String id = databaseArtist.push().getKey();
            Artist artist = new Artist(id, name, genre);
            databaseArtist.child(id).setValue(artist);
            editTextName.setText("");           //sets editTestName to empty

            Toast.makeText(this, "Artist Added", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
        }
    }


} // [END MainActivity]
