package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Felipe on 05/09/2016.
 */
public class Raca {
    private int idRaca;
    private String nome;
    private String descricao;
    private Especie especie;

    public Raca(int idRaca, String nome, String descricao, Especie especie) {
        this.idRaca = idRaca;
        this.nome = nome;
        this.descricao = descricao;
        this.especie = especie;
    }

    public int getIdRaca() {
        return idRaca;
    }

    public void setIdRaca(int idRaca) {
        this.idRaca = idRaca;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public static Raca jsonToRaca(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Especie especie = new Especie(objeto.getInt("idEspecie"),objeto.getString("NomeEspecie"));
            Raca raca = new Raca(objeto.getInt("idRaca"),objeto.getString("NomeRaca"),objeto.getString("Descricao"),especie);
            return raca;
        }
    }

    public JSONObject racaToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idRaca",this.getIdRaca());
        objeto.put("NomeRaca",this.getNome());
        objeto.put("Descricao",this.getDescricao());
        objeto.put("idEspecie",this.getEspecie().getIdEspecie());
        objeto.put("NomeEspecie",this.getEspecie().getNome());
        return objeto;
    }

    @Override
    public String toString() {
        return nome;
    }
}
