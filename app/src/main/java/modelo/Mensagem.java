package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Felipe on 24/04/2016.
 */
public class Mensagem {
    private int codigo;
    private String mensagem;

    public Mensagem(int codigo, String mensagem){
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public static Mensagem jsonToMensagem(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Mensagem mensagem = new Mensagem(objeto.getInt("Codigo"),objeto.getString("Mensagem"));
            return mensagem;
        }
    }

    public static boolean isMensagem(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return false;
        }else {
            if(objeto.has("Codigo") && objeto.has("Mensagem")) {
                return true;
            }else{
                return false;
            }
        }
    }
}
