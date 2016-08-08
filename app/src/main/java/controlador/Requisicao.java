package controlador;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private static String urlBase = "http://www.appet.hol.es/index.php/";

    public static JSONArray chamaMetodo(String metodo, int id, String conteudo){
        String URL = "";
        JSONArray resposta = new JSONArray();

        if(id == 0) {
            URL = urlBase + metodo;
        }else{
            URL = urlBase + metodo + "/" + String.valueOf(id);
        }

        resposta = requisicao(URL, conteudo);

        return resposta;
    }

    private static JSONArray requisicao(String url, String conteudo){
        JSONArray resposta = new JSONArray();

        try {
            //Monta a URL da requisição
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //Adiciona os headers da requisição
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("authorization","Basic YXBwZXQ6QXBwZXRUY2MyMDE2");

            if(!conteudo.equals("")) {
                // Adiciona conteudo na requisição
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(conteudo);
                wr.flush();
                wr.close();
            }

            //Recupera o resultado da requisição
            BufferedReader res = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String linha;
            StringBuffer resultado = new StringBuffer();

            //Percorre o resultado da requisição
            while ((linha = res.readLine()) != null) {
                resultado.append(linha);
            }
            res.close();

            //Retorna o resultado.
            resposta.put(new JSONObject(resultado.toString()));

        } catch (Exception e) {
            Log.e("Erro",e.getMessage());
        }

        return resposta;
    }

}
