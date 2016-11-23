package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Usuario {
    private int idUsuario;
    private String nome;
    private String email;
    private String telefone;
    private String cidade;
    private String bairro;

    public Usuario(int idUsuario, String nome, String email, String telefone, String cidade, String bairro) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cidade = cidade;
        this.bairro = bairro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public static Usuario jsonToUsuario(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("Nome"),objeto.getString("Email"),objeto.getString("Telefone"),objeto.getString("Cidade"),objeto.getString("Bairro"));
            return usuario;
        }
    }

    public JSONObject usuarioToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idUsuario", this.getIdUsuario());
        objeto.put("Nome", this.getNome());
        objeto.put("Email", this.getEmail());
        objeto.put("Telefone", this.getTelefone());
        objeto.put("Cidade", this.getCidade());
        objeto.put("Bairro", this.getBairro());
        return objeto;
    }
}
