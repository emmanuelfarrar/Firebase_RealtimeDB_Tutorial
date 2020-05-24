package com.efarrar.firebase_realtimedb_tutorial;

/** URL: https://www.youtube.com/playlist?list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1
 * https://www.simplifiedcoding.net/firebase-realtime-database-crud/
 * https://github.com/probelalkhan/FirebaseRealtimeDatabaseTutorial
 *
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    Button buttonAdd;
    Spinner spinnerGenres;

    //Database Reference
    DatabaseReference databaseArtist;

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

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
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

            Toast.makeText(this, "Artist Added", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
        }
    }


} // [END MainActivity]
