package com.example.ayps;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.api.rest.RestResponse;
import com.amplifyframework.core.Amplify;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.spot_list )
    RecyclerView spotListRV;

    private ArrayList<SpotModal> spotArrayList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static private final String CLASS_NAME = ExploreFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.bind(this, view);

        spotArrayList = new ArrayList<>();

        SpotModal spotModal = new SpotModal("title", "desc", "placeName", "locality",
                "place", "region", "country", "latitude",
                "longitude", "rabiixx12", "tags", 0);

        spotArrayList.add( spotModal );
        spotArrayList.add( spotModal );
        spotArrayList.add( spotModal );
        spotArrayList.add( spotModal );
        spotArrayList.add( spotModal );

        SpotAdapter adapter = new SpotAdapter( getActivity().getApplicationContext(), spotArrayList );
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false );

        spotListRV.setLayoutManager( llm );
        spotListRV.setAdapter( adapter );

        llm.scrollToPositionWithOffset(3, 1369);
//        llm.scrollToPosition(2););

//        getData();

        /*RestOptions options = RestOptions.builder()
                .addPath("/spot/list")
                .addBody("{}".getBytes())
                .build();

        Amplify.API.post(options,
                response -> {
                    try {
                        JSONArray jsonArray = (JSONArray) response.getData().asJSONObject().get("Items");

                        for (int i = 0; i < jsonArray.length(); ++i ) {

                            JSONObject spotObj = jsonArray.getJSONObject( i );

                            String title = spotObj.getString("title");
                            String desc = spotObj.getString("description");
                            String longitude = spotObj.getString("longitude");
                            String latitude = spotObj.getString("latitude");
                            String placeName = spotObj.getString("placeName");
                            String locality = spotObj.getString("locality");
                            String place = spotObj.getString("place");
                            String region = spotObj.getString("region");
                            String country = spotObj.getString("country");
                            String created_at = spotObj.getString("created_at");
                            //String tags = spotObj.getString("tags");
                            //String numLikes = spotObj.getString("numLikes");

                            SpotModal spotModal = new SpotModal(title, desc, placeName, locality,
                                    place, region, country, latitude,
                                    longitude, "rabiixx12", "tags", 0);


                            spotArrayList.add( spotModal );

                            SpotAdapter adapter = new SpotAdapter( getActivity().getApplicationContext(), spotArrayList );
//                            LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false );

                            spotListRV.setLayoutManager( new LinearLayoutManager( view.getContext() ) );
                            spotListRV.setAdapter( adapter );

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(CLASS_NAME, "POST succeeded: " + response);
                },
                error -> Log.e(CLASS_NAME, "POST failed.", error)
        );*/

        return view;
    }

   /* private void getData() {




    }*/

}