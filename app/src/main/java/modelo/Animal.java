package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Felipe on 20/09/2016.
 */
public class Animal {
    private int idAnimal;
    private String nome;
    private String genero;
    private String cor;
    private String porte;
    private int idade;
    private String caracteristicas;
    private String qrcode;
    private String foto;
    private boolean desaparecido;
    private Usuario usuario;
    private Raca raca;

    public Animal(int idAnimal, String nome, String genero, String cor, String porte, int idade, String caracteristicas, String qrcode, String foto, boolean desaparecido, Usuario usuario, Raca raca) {
        this.idAnimal = idAnimal;
        this.nome = nome;
        this.genero = genero;
        this.cor = cor;
        this.porte = porte;
        this.idade = idade;
        this.caracteristicas = caracteristicas;
        this.qrcode = qrcode;
        this.foto = foto;
        this.desaparecido = desaparecido;
        this.usuario = usuario;
        this.raca = raca;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isDesaparecido() {
        return desaparecido;
    }

    public void setDesaparecido(boolean desaparecido) {
        this.desaparecido = desaparecido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Raca getRaca() {
        return raca;
    }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }

    public static Animal jsonToAnimal(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Especie especie = new Especie(objeto.getInt("idEspecie"),objeto.getString("NomeEspecie"));
            Raca raca = new Raca(objeto.getInt("idRaca"),objeto.getString("NomeRaca"),objeto.getString("DescricaoRaca"),especie);
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"),objeto.getString("Telefone"),objeto.getString("Cidade"),objeto.getString("Bairro"));
            Animal animal = new Animal(objeto.getInt("idAnimal"),objeto.getString("Nome"),objeto.getString("Genero"),objeto.getString("Cor"),objeto.getString("Porte"),objeto.getInt("Idade"),objeto.getString("Caracteristicas"),objeto.getString("QRCode"),objeto.getString("Foto"),objeto.getInt("Desaparecido") == 1?true:false,usuario,raca);
            return animal;
        }
    }

    public JSONObject animalToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idAnimal",this.getIdAnimal());
        objeto.put("Nome",this.getNome());
        objeto.put("Genero",this.getGenero());
        objeto.put("Cor",this.getCor());
        objeto.put("Porte",this.getPorte());
        objeto.put("Idade",this.getIdade());
        objeto.put("Caracteristicas",this.getCaracteristicas());
        objeto.put("QRCode",this.getQrcode());
        objeto.put("Foto",this.getFoto());
        objeto.put("Desaparecido",this.isDesaparecido()?1:0);
        objeto.put("idUsuario",this.usuario.getIdUsuario());
        objeto.put("idRaca",this.raca.getIdRaca());
        objeto.put("NomeRaca",this.raca.getNome());
        objeto.put("DescricaoRaca",this.raca.getDescricao());
        objeto.put("NomeUsuario",this.usuario.getNome());
        objeto.put("Email",this.usuario.getEmail());
        objeto.put("Telefone",this.usuario.getTelefone());
        objeto.put("Cidade",this.usuario.getCidade());
        objeto.put("Bairro",this.usuario.getBairro());
        objeto.put("idEspecie",this.raca.getEspecie().getIdEspecie());
        objeto.put("NomeEspecie",this.raca.getEspecie().getNome());
        return objeto;
    }

    @Override
    public String toString() {
        return nome;
    }
}
