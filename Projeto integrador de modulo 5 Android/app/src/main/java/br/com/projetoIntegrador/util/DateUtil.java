package br.com.projetoIntegrador.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    /**
     * Converte uma string de timestamp (ex: "1753016400.000000000") para uma data formatada.
     * @param timestampString O timestamp vindo do banco de dados.
     * @return Uma string de data formatada como "dd/MM/yyyy 'às' HH:mm".
     */
    public static String formatTimestamp(String timestampString) {
        if (timestampString == null || timestampString.isEmpty()) {
            return "Data inválida";
        }

        try {
            // Pega apenas a parte dos segundos (antes do ponto)
            long seconds = Long.parseLong(timestampString.split("\\.")[0]);

            // Converte os segundos para milissegundos
            Date date = new Date(seconds * 1000L);

            // Define o formato desejado (dd/MM/yyyy às HH:mm) e o fuso horário local
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault()); // Usa o fuso horário do dispositivo

            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Formato inválido"; // Retorna isso em caso de erro na conversão
        }
    }
}