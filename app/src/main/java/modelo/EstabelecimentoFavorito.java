package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Felipe on 23/10/2016.
 */

public class EstabelecimentoFavorito {
    private int idEstabelecimentoFavorito;
    private String nome;
    private String latitude;
    private String longitude;
    private Usuario usuario;
    private String tipo;
    private String endereco;

    public EstabelecimentoFavorito(int idEstabelecimentoFavorito, String nome, String latitude, String longitude, Usuario usuario, String tipo, String endereco) {
        this.idEstabelecimentoFavorito = idEstabelecimentoFavorito;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.usuario = usuario;
        this.tipo = tipo;
        this.endereco = endereco;
    }

    public int getIdEstabelecimentoFavorito() {
        return idEstabelecimentoFavorito;
    }

    public void setIdEstabelecimentoFavorito(int idEstabelecimentoFavorito) {
        this.idEstabelecimentoFavorito = idEstabelecimentoFavorito;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public static EstabelecimentoFavorito jsonToEstabelecimentoFavorito(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"),objeto.getString("Telefone"),objeto.getString("Cidade"),objeto.getString("Bairro"));
            EstabelecimentoFavorito estabelecimentoFavorito = new EstabelecimentoFavorito(objeto.getInt("idEstabelecimentoFavorito"),objeto.getString("NomeEstFavorito"),objeto.getString("Latitude"),objeto.getString("Longitude"),usuario,objeto.getString("Tipo"),objeto.getString("Endereco"));
            return estabelecimentoFavorito;
        }
    }

    public JSONObject estabelecimentoFavoritoToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idEstabelecimentoFavorito",this.getIdEstabelecimentoFavorito());
        objeto.put("NomeEstFavorito",this.getNome());
        objeto.put("Latitude",this.getLatitude());
        objeto.put("Longitude",this.getLongitude());
        objeto.put("idUsuario",this.getUsuario().getIdUsuario());
        objeto.put("NomeUsuario",this.getUsuario().getNome());
        objeto.put("Email",this.getUsuario().getEmail());
        objeto.put("Telefone",this.getUsuario().getTelefone());
        objeto.put("Cidade",this.getUsuario().getCidade());
        objeto.put("Bairro",this.getUsuario().getBairro());
        objeto.put("Tipo",this.getTipo());
        objeto.put("Endereco",this.getEndereco());
        return objeto;
    }
}

