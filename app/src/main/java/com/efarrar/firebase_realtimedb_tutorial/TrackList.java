package com.efarrar.firebase_realtimedb_tutorial;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TrackList extends ArrayAdapter<Track> {

    private Activity context;
    private List<Track> trackList;

    public TrackList (Activity context, List<Track> trackList) {
        super(context, R.layout.list_layout_tracks, trackList);
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout_tracks, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewRating = (TextView) listViewItem.findViewById(R.id.textViewRating);

        Track track = trackList.get(position);

        textViewName.setText(track.getTrackName());
        textViewRating.setText(String.valueOf(track.getTrackRating()));         //casting into String because this is a int

        return listViewItem;

    }
}
