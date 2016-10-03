package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Diogo on 01/10/2016.
 */
public class Compromisso {
    private Evento evento;
    private String nomelocal, latitude, longitude, datahora;

    public Compromisso(Evento evento, String nomelocal, String latitude, String longitude, String datahora) {
        this.evento = evento;
        this.nomelocal = nomelocal;
        this.latitude = latitude;
        this.longitude = longitude;
        this.datahora = datahora;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getNomelocal() {
        return nomelocal;
    }

    public void setNomelocal(String nomelocal) {
        this.nomelocal = nomelocal;
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

    public String getDatahora() {
        return datahora;
    }

    public void setDatahora(String datahora) {
        this.datahora = datahora;
    }

    public static Compromisso jsonToAnimal(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Especie especie = new Especie(objeto.getInt("idEspecie"),objeto.getString("NomeEspecie"));
            Raca raca = new Raca(objeto.getInt("idRaca"),objeto.getString("NomeRaca"),objeto.getString("DescricaoRaca"),especie);
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"),objeto.getString("Telefone"),objeto.getString("Cidade"),objeto.getString("Bairro"));
            Animal animal = new Animal(objeto.getInt("idAnimal"),objeto.getString("Nome"),objeto.getString("Genero"),objeto.getString("Cor"),objeto.getString("Porte"),objeto.getInt("Idade"),objeto.getString("Caracteristicas"),objeto.getString("QRCode"),objeto.getString("Foto"),objeto.getInt("Desaparecido") == 1?true:false,usuario,raca);
            Alerta alerta = new Alerta(objeto.getInt("idAlerta"),objeto.getString("nivelAlerta"),objeto.getInt("frequencia"));
            Evento evento = new Evento(objeto.getInt("idEvento"),objeto.getString("nome"),objeto.getString("observacoes"),objeto.getInt("flagalerta"),alerta,animal,objeto.getString("tipo"));;
            Compromisso compromisso = new Compromisso(evento,objeto.getString("nomelocal"),objeto.getString("latitude"),objeto.getString("longitude"),objeto.getString("datahora"));
            return compromisso;
        }
    }

    public JSONObject compromissoToJson() throws JSONException {
        //Evento
        JSONObject objeto = new JSONObject();
        objeto.put("idEvento",this.evento.getIdEvento());
        objeto.put("Nome",this.evento.getNome());
        objeto.put("Observacoes",this.evento.getObservacoes());
        objeto.put("FlagAlerta",this.evento.getFlagalerta());
        objeto.put("idAnimal",this.evento.getAnimal().getIdAnimal());
        objeto.put("Tipo",this.evento.getTipo());

        //Alerta
        objeto.put("idAlerta",this.evento.getAlerta().getidAlerta());
        objeto.put("NivelAlerta",this.evento.getAlerta().getNivelAlerta());
        objeto.put("Frequencia", this.evento.getAlerta().getFrequencia());

        //Compromisso
        objeto.put("NomeLocal",this.getNomelocal());
        objeto.put("Latitude",this.getLatitude());
        objeto.put("Longitude",this.getLongitude());
        objeto.put("DataHora",this.getDatahora());
        return objeto;
    }
}
