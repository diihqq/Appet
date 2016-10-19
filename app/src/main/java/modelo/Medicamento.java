package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Diogo on 01/10/2016.
 */
public class Medicamento {
    private Evento evento;
    private String inicio, fim, frequenciadiaria, horasdeespera;

    public Medicamento(Evento evento, String horasdeespera, String inicio, String fim, String frequenciadiaria) {
        this.horasdeespera = horasdeespera;
        this.evento = evento;
        this.inicio = inicio;
        this.fim = fim;
        this.frequenciadiaria = frequenciadiaria;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFim() {
        return fim;
    }

    public void setFim(String fim) {
        this.fim = fim;
    }

    public String getFrequenciadiaria() {
        return frequenciadiaria;
    }

    public void setFrequenciadiaria(String frequenciadiaria) {
        this.frequenciadiaria = frequenciadiaria;
    }

    public String getHorasdeespera() {
        return horasdeespera;
    }

    public void setHorasdeespera(String horasdeespera) {
        this.horasdeespera = horasdeespera;
    }

    public static Medicamento jsonToAnimal(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Especie especie = new Especie(objeto.getInt("idEspecie"),objeto.getString("NomeEspecie"));
            Raca raca = new Raca(objeto.getInt("idRaca"),objeto.getString("NomeRaca"),objeto.getString("DescricaoRaca"),especie);
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"),objeto.getString("Telefone"),objeto.getString("Cidade"),objeto.getString("Bairro"));
            Animal animal = new Animal(objeto.getInt("idAnimal"),objeto.getString("Nome"),objeto.getString("Genero"),objeto.getString("Cor"),objeto.getString("Porte"),objeto.getInt("Idade"),objeto.getString("Caracteristicas"),objeto.getString("QRCode"),objeto.getString("Foto"),objeto.getInt("Desaparecido") == 1?true:false,objeto.getString("FotoCarteira"),objeto.getString("DataFotoCarteira"),usuario,raca);
            Alerta alerta = new Alerta(objeto.getInt("idAlerta"),objeto.getString("nivelAlerta"),objeto.getInt("frequencia"));
            Evento evento = new Evento(objeto.getInt("idEvento"),objeto.getString("nome"),objeto.getString("observacoes"),objeto.getInt("flagalerta"),alerta,animal,objeto.getString("tipo"));;
            Medicamento medicamento = new Medicamento(evento,objeto.getString("horasdeespera"),objeto.getString("inicio"),objeto.getString("fim"),objeto.getString("frequenciadiaria"));
            return medicamento;
        }
    }

    public JSONObject medicamentoToJson() throws JSONException {
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

        //Medicamento
        objeto.put("Inicio",this.getInicio());
        objeto.put("Fim",this.getFim());
        objeto.put("FrequenciaDiaria",this.getFrequenciadiaria());
        objeto.put("HorasDeEspera",this.getHorasdeespera());
        return objeto;
    }
}
