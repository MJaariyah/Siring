package com.codinginflow.siring.navigationdrawerexample;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codinginflow.siring.R;
import com.codinginflow.siring.adzan.MusikService;
import com.codinginflow.siring.api.apii.ApiService;
import com.codinginflow.siring.api.apii.ApiUrl;
import com.codinginflow.siring.api.model.ModelJadwal;
import com.codinginflow.siring.notif.AppReceiver;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JadwalSholatFragment extends Fragment {

    //deklarasi notif
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 134;
    //set interval notifikasi 10 detik
    private int interval_seconds = 10;
    private int NOTIFICATION_ID = 1;

    private MediaPlayer mp;

    //yang asli
    private TextView tv_tanggal, tv_nama_daerah, tv_fajr_value, tv_shurooq_value,
            tv_dhuhr_value, tv_asr_value, tv_maghrib_value, tv_isha_value;
    private FloatingActionButton fab_refresh;
    private ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_jadwalsholat, container, false);

        tv_tanggal = view.findViewById(R.id.tv_tanngal);
        tv_nama_daerah = view.findViewById(R.id.tv_nama_daerah);
        tv_fajr_value = view.findViewById(R.id.tv_fajr_value);
        tv_dhuhr_value = view.findViewById(R.id.tv_dhuhr_value);
        tv_asr_value = view.findViewById(R.id.tv_asr_value);
        tv_maghrib_value = view.findViewById(R.id.tv_maghrib_value);
        tv_isha_value = view.findViewById(R.id.tv_isha_value);
        fab_refresh = view.findViewById(R.id.fab_refresh);

        getJadwal();

        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJadwal();
            }
        });

        Intent alarmIntent = new Intent(getContext(), AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), ALARM_REQUEST_CODE, alarmIntent, 0);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
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
                    tv_tanggal.setText(response.body().getItems().get(0).getDateFor());
                    tv_nama_daerah.setText(response.body().getQuery());
                    tv_fajr_value.setText(response.body().getItems().get(0).getFajr());
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

    //untuk yang tombol aktif non aktif
    public void startAlarmManager(View v) {
        //set waktu sekarang berdasarkan interval
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, interval_seconds);
        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        //set alarm manager dengan memasukkan waktu yang telah dikonversi menjadi milliseconds
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        Toast.makeText(getContext(), "AlarmManager Start.", Toast.LENGTH_SHORT).show();

        //munculkan suara adzan
        /**
         * Dijalankan Oleh Tombol Play
         */
        getContext().startService(new Intent(getContext(), MusikService.class));

        /** Memanggil File MP3 "indonesiaraya.mp3" */
        mp = MediaPlayer.create(getContext(), R.raw.adzan);

        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Menjalankan Audio */
        mp.start();

        /** Penanganan Ketika Suara Berakhir */
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //stop
                mp.stop();

                try{
                    mp.prepare();
                    mp.seekTo(0);
                }catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }
    //Stop/Cancel alarm manager
    public void stopAlarmManager(View v) {
        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //close existing/current notifications
        NotificationManager notificationManager = (NotificationManager) getContext().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        //jika app ini mempunyai banyak notifikasi bisa di cancelAll()
        //notificationManager.cancelAll();
        Toast.makeText(getContext(), "AlarmManager Stopped by User.", Toast.LENGTH_SHORT).show();
    }
}
