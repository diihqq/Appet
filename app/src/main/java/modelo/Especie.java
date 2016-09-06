package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Felipe on 05/09/2016.
 */
public class Especie {
    private int idEspecie;
    private String nome;

    public Especie(int idEspecie, String nome) {
        this.idEspecie = idEspecie;
        this.nome = nome;
    }

    public int getIdEspecie() {
        return idEspecie;
    }

    public void setIdEspecie(int idEspecie) {
        this.idEspecie = idEspecie;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public static Especie jsonToEspecie(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Especie especie = new Especie(objeto.getInt("idEspecie"),objeto.getString("Nome"));
            return especie;
        }
    }

    public JSONObject especieToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idEspecie",this.getIdEspecie());
        objeto.put("Nome",this.getNome());
        return objeto;
    }
}
