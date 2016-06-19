package controlador;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Felipe on 24/04/2016.
 */
public class Requisicao {
    private static String urlBase = "http://10.0.2.2:8585/";

    public static String chamaMetodo(String metodo, String conteudo){
        String URL = "";
        String resposta = "";

        //Verifica qual método da API está sendo chamado.
        switch(metodo){
            case "autenticacao":
                URL = urlBase + metodo;
                resposta = requisicao(URL, conteudo);
                break;
        }

        return resposta;
    }

    private static String requisicao(String url, String conteudo){
        String resposta = "";

        try {
            //Monta a URL da requisição
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //Adiciona os headers da requisição
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("authorization","Basic c3B5cGV0OmFwaXNweXBldA==");

            if(!conteudo.equals("")) {
                // Adiciona conteudo na requisição
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(conteudo);
                wr.flush();
                wr.close();
            }

            //Recupera o resultado da requisição
            BufferedReader res = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer result = new StringBuffer();

            //Percorre o resultado da requisição
            while ((inputLine = res.readLine()) != null) {
                result.append(inputLine);
            }
            res.close();

            //Retorna o resultado.
            resposta = result.toString();

        } catch (Exception e) {
            resposta = "Erro: " + e.getMessage();
        }

        return resposta;
    }
}
