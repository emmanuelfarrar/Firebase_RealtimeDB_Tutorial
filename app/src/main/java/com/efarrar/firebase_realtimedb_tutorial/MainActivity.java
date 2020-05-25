package com.efarrar.firebase_realtimedb_tutorial;

/** URL: https://www.youtube.com/playlist?list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1
 * https://www.simplifiedcoding.net/firebase-realtime-database-crud/
 * https://github.com/probelalkhan/FirebaseRealtimeDatabaseTutorial
 *
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

        /**
         * setOnItemLongClickListener listener to call AlertDialog for updating
         */
        listViewArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = artistList.get(position);

                showUpdateDialog(artist.getArtistId(),artist.getArtistName());
                return false;
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

    /**showUpdateDialog()
     * Function is to build the alert dialog and set up the variables in it
     *
     *
     * @param artistId
     * @param artistName
     */
    private void showUpdateDialog(final String artistId, String artistName){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialogView);


        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);
        final Spinner spinnerUpdate = (Spinner) dialogView.findViewById(R.id.spinnerGenres);

        dialogBuilder.setTitle("Updating Artist "+ artistName);         //sets the title of the dialog, like a label

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        /**
         * take values newly updated and call updateArtist to pass them
         */
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if(TextUtils.isEmpty(name)){
                    editTextName.setError("Name required");
                    return;
                }

                updateArtist(artistId, name, genre);        //calling updateArtist to pass updated values
                alertDialog.dismiss();          //dismisses the alert Dialog
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArtist(artistId);
                alertDialog.dismiss();;
            }
        });

    }

    /** function to delete from DB */
    private void deleteArtist(String artistId) {
        DatabaseReference drArtist = FirebaseDatabase.getInstance().getReference("artist").child(artistId);
        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);

        drArtist.removeValue();
        drTracks.removeValue();
    }

    /**
     * function to update artist info in DB
     * @param id
     * @param name
     * @param genre
     * @return
     */
    private boolean updateArtist (String id, String name, String genre){

        //getting to the particular DBReference for the artist that needs to be updated
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artist").child(id);

        Artist artist = new Artist(id, name, genre);

        databaseReference.setValue(artist);     //setting the new values

        Toast.makeText(this, "Artist Updated Successfully", Toast.LENGTH_SHORT).show();

        return true;

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
