package br.com.projetoIntegrador;

import android.app.Application;

// Esta classe é o ponto de entrada principal do aplicativo.
// Pode ser usada para inicializar bibliotecas ou configurações uma única vez.
public class ProjetoIntegradorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Lógica de inicialização pode ser adicionada aqui no futuro
        // Ex: Iniciar uma biblioteca de Log, Injeção de Dependência (Hilt/Koin), etc.
    }
}