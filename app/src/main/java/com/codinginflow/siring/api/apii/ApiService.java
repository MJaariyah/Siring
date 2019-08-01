package com.codinginflow.siring.api.apii;

import com.codinginflow.siring.api.model.ModelJadwal;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("bandung.json")
    Call<ModelJadwal> getJadwal();

}
