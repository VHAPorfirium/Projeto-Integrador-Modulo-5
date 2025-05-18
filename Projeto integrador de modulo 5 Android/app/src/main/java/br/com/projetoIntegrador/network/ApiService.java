package br.com.projetoIntegrador.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import java.util.List;

public interface ApiService {
    @POST("tokens")
    Call<Void> registerToken(@Body TokenRequest req);

    @POST("notifications")
    Call<Void> sendNotification(@Body NotificationRequest req);

    @GET("tokens/{userId}")
    Call<List<DeviceToken>> listTokens(@Path("userId") String userId);
}
