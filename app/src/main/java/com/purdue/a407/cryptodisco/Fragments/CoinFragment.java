package com.purdue.a407.cryptodisco.Fragments;


import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleohanian.databinding.modelbindingforms.Listeners.OnBindDialogCancelListener;
import com.kyleohanian.databinding.modelbindingforms.Listeners.OnBindDialogCreateListener;
import com.kyleohanian.databinding.modelbindingforms.UIObjects.ModelForm;
import com.purdue.a407.cryptodisco.Adapter.ChatRoomAdapter;
import com.purdue.a407.cryptodisco.Adapters.ExchangesAdapter;
import com.purdue.a407.cryptodisco.Api.CDApi;
import com.purdue.a407.cryptodisco.App;
import com.purdue.a407.cryptodisco.CacheData.CDResource;
import com.purdue.a407.cryptodisco.Data.AppDatabase;
import com.purdue.a407.cryptodisco.Data.Entities.CoinPairingEntity;
import com.purdue.a407.cryptodisco.Data.Entities.ExchangeEntity;
import com.purdue.a407.cryptodisco.Data.Entities.UserExchangeEntity;
import com.purdue.a407.cryptodisco.Data.Entities.WatchListEntity;
import com.purdue.a407.cryptodisco.Helpers.DeviceID;
import com.purdue.a407.cryptodisco.R;
import com.purdue.a407.cryptodisco.Repos.CoinPairingRepository;
import com.purdue.a407.cryptodisco.ViewModels.ExchangeViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoinFragment extends Fragment {


    String titleString;

    @Inject
    DeviceID deviceID;

    @BindView(R.id.coin_like)
    Button coinLike;

    @BindView(R.id.title)
    TextView title;

    @Inject
    AppDatabase appDatabase;

    @Inject
    CDApi cdApi;


    ExchangeViewModel viewModel;

    ArrayAdapter<Object> arrayAdapter;
    Context context;

    public CoinFragment() {
        // Required empty public constructor
    }

    public static CoinFragment newInstance(String title) {
        CoinFragment coinFragment = new CoinFragment();
        coinFragment.setTitle(title);
        return coinFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_coin, container, false);
        ButterKnife.bind(this, view);
        ((App) getActivity().getApplication()).getNetComponent().inject(this);
        context = getActivity();


        coinLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int coinNum = appDatabase.coinDao().getID(titleString);
                int numliked = appDatabase.coinDao().getTimeUserLikedCoin(deviceID.getDeviceID(), coinNum);

                Log.d("NUM LIKED ", Integer.toString(numliked));

                String likeStatus = coinLike.getText().toString().toLowerCase();


                if (likeStatus.equals("like")) {


                    WatchListEntity watchListEntity = new WatchListEntity(deviceID.getDeviceID(), coinNum);

                    cdApi.insertLikedCoin(watchListEntity).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() != 200) {
                                Log.d("Coin Result", String.valueOf(response.code()));
                            } else {
                                Log.d("Coin Result", "Success");

                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("Coin Result", "Failure");

                        }
                    });

                    coinLike.setText("Unlike");
                } else {
                    coinLike.setText("Like");
                }

            }
        });

        title.setText(titleString);


        return view;
    }

    public void setTitle(String titleString) {
        this.titleString = titleString;
    }



}
