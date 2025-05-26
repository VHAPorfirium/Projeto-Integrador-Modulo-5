package br.com.projetoIntegrador.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Equipe, atenção aqui! Esta `BASE_URL` precisa ser ajustada dependendo de onde a API está rodando.
    // Se estivermos usando o emulador Android, o IP `10.0.2.2` geralmente aponta para o localhost da máquina host.
    // Se for um dispositivo físico na mesma rede Wi-Fi, usem o IP local do computador que está rodando o servidor,
    // algo como "http://192.168.1.10:8080/".
    // Por padrão, deixei configurado para o emulador.
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static ApiService instance;

    /**
     * Pessoal, este método é o nosso ponto de acesso centralizado (singleton) para obter a instância do `ApiService`.
     * Se a instância `instance` ainda não foi criada (for nula):
     * 1. Configuramos um `HttpLoggingInterceptor`. Isso é MUITO útil para debug, pois vai logar
     * os detalhes das requisições HTTP (headers, body) e das respostas. Definam o nível para `BODY`.
     * 2. Criamos um `OkHttpClient` e adicionamos o `loggingInterceptor` a ele.
     * 3. Configuramos o `Retrofit.Builder`:
     * - `baseUrl(BASE_URL)`: Define a URL base para todas as requisições.
     * - `client(client)`: Associa o nosso `OkHttpClient` configurado.
     * - `addConverterFactory(GsonConverterFactory.create())`: Informa ao Retrofit para usar o Gson
     * para desserializar as respostas JSON para os nossos objetos DTO e serializar os DTOs para JSON nas requisições.
     * 4. Criamos a instância do `ApiService` usando `retrofit.create(ApiService.class)`.
     * Finalmente, retornamos a instância (nova ou existente) do `ApiService`.
     * @return A instância singleton do ApiService.
     */
    public static ApiService get() {
        if (instance == null) {
            // Configurem o interceptor para logar as requisições e respostas HTTP.
            // Isso é essencial para debugar problemas de comunicação com a API.
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Construam o OkHttpClient adicionando o interceptor de log.
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Construam a instância do Retrofit.
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // URL base da nossa API.
                    .client(client) // Nosso OkHttpClient customizado com logging.
                    .addConverterFactory(GsonConverterFactory.create()) // Fábrica de conversores para JSON (Gson).
                    .build();
            // Criem a implementação da nossa interface ApiService.
            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }
}