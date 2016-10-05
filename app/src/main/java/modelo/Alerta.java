package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Diogo on 01/10/2016.
 */
public class Alerta {
    private int idAlerta;
    private String nivelAlerta;
    private int frequencia;

    public Alerta(int idAlerta, String nivelAlerta, int frequencia) {
        this.idAlerta = idAlerta;
        this.nivelAlerta = nivelAlerta;
        this.frequencia = frequencia;
    }

    public int getidAlerta() {
        return idAlerta;
    }

    public void setidAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(String nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(int frequencia) {
        this.frequencia = frequencia;
    }

    public static Alerta jsonToAlerta(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Alerta alerta = new Alerta(objeto.getInt("idAlerta"),objeto.getString("NivelAlerta"),objeto.getInt("Frequencia"));
            return alerta;
        }
    }

    public JSONObject alertaToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idAlerta",this.getidAlerta());
        objeto.put("NivelAlerta",this.getNivelAlerta());
        objeto.put("Frequencia",this.getFrequencia());
        return objeto;
    }

    @Override
    public String toString() {
        return nivelAlerta;
    }
}
