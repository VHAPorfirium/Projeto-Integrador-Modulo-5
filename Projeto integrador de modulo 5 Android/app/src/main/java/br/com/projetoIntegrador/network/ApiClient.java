package br.com.projetoIntegrador.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    private static Gson gson = new GsonBuilder().create();

    public static ApiService get() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    // MÉTODO ADICIONADO AQUI
    public static Retrofit getRetrofit() {
        // Garante que a instância do Retrofit seja criada antes de ser retornada
        if (retrofit == null) {
            get(); // Chama o método get() para inicializar o retrofit
        }
        return retrofit;
    }
}