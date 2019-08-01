package com.codinginflow.siring.navigationdrawerexample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codinginflow.siring.R;
import com.codinginflow.siring.api.apii.ApiService;
import com.codinginflow.siring.api.apii.ApiUrl;
import com.codinginflow.siring.api.model.ModelJadwal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArahKiblatFragment extends Fragment {

    private TextView tv_lokasi_value, tv_fajr_value, tv_shurooq_value,
            tv_dhuhr_value, tv_asr_value, tv_maghrib_value, tv_isha_value;
    private FloatingActionButton fab_refresh;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_masjidterdekat, container, false);

        //tv_lokasi_value = view.findViewById(R.id.tv_lokasi_value);
        tv_fajr_value = view.findViewById(R.id.tv_fajr_value);
        //tv_shurooq_value = view.findViewById(R.id.tv_shurooq_value);
        tv_dhuhr_value = view.findViewById(R.id.tv_dhuhr_value);
        tv_asr_value = view.findViewById(R.id.tv_asr_value);
        tv_maghrib_value = view.findViewById(R.id.tv_maghrib_value);
        tv_isha_value = view.findViewById(R.id.tv_isha_value);
        fab_refresh = view.findViewById(R.id.fab_refresh);


        return view;

    }

    private void getJadwal () {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait / Silahkan tunggu ...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrl.URL_ROOT_HTTP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ModelJadwal> call = apiService.getJadwal();
        call.enqueue(new Callback<ModelJadwal>() {
            @Override
            public void onResponse(Call<ModelJadwal> call, Response<ModelJadwal> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //String stringDate = DateFormat.getDateTimeInstance().format(response.body().getItems().get(0).getDateFor());
                    tv_lokasi_value.setText(response.body().getQuery()+", "+response.body().getItems().get(0).getDateFor());
                    tv_fajr_value.setText(response.body().getItems().get(0).getFajr());
                    tv_shurooq_value.setText(response.body().getItems().get(0).getShurooq());
                    tv_dhuhr_value.setText(response.body().getItems().get(0).getDhuhr());
                    tv_asr_value.setText(response.body().getItems().get(0).getAsr());
                    tv_maghrib_value.setText(response.body().getItems().get(0).getMaghrib());
                    tv_isha_value.setText(response.body().getItems().get(0).getIsha());

                } else {

                }
            }

            @Override
            public void onFailure(Call<ModelJadwal> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Sorry, please try again... server Down..", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
