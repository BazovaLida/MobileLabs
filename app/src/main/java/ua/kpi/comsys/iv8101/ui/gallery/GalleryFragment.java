package ua.kpi.comsys.iv8101.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.kpi.comsys.iv8101.MainActivity;
import ua.kpi.comsys.iv8101.R;

public class GalleryFragment extends Fragment {
    private GalleryAdapter galleryAdapter;
    public static SQLiteDatabaseGalHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new SQLiteDatabaseGalHandler(requireActivity().getApplicationContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) requireContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        galleryAdapter = new GalleryAdapter(screenWidth);

        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = view.findViewById(R.id.add_picture_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        RecyclerView recView = view.findViewById(R.id.recycler_gallery);
        recView.setAdapter(galleryAdapter);
//        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        String url = "https://pixabay.com/api/?key=19193969-87191e5db266905fe8936d565&q=hot+summer&image_type=photo&per_page=24";
        AndroidNetworking.get(url)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray images = response.getJSONArray("hits");

                            for (int i = 0; i < images.length(); i++) {
                                String currURL = images.getJSONObject(i).getString("webformatURL");
                                Picture currPict = new Picture(currURL);
                                galleryAdapter.addElementURL(currPict);
                                db.addItem(currPict);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "JSON exception!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getContext(), "Error while getting response", Toast.LENGTH_LONG).show();
                        ArrayList<Picture> pictures_in_DB = db.allItems();
                        for (int i = 0; i < pictures_in_DB.size(); i++) {
                            galleryAdapter.addElementURL(pictures_in_DB.get(i));
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == MainActivity.RESULT_OK && data != null){
            Uri uri = data.getData();
            galleryAdapter.addElementUri(uri);
        }
    }
}