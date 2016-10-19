package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Diogo on 01/10/2016.
 */
public class Evento {
    private int idEvento;
    private String nome;
    private String observacoes;
    private int flagalerta;
    private Alerta alerta;
    private Animal animal;
    private String tipo;

    public Evento(int idEvento, String nome, String observacoes, int flagalerta, Alerta alerta, Animal animal, String tipo) {
        this.idEvento = idEvento;
        this.nome = nome;
        this.observacoes = observacoes;
        this.flagalerta = flagalerta;
        this.alerta = alerta;
        this.animal = animal;
        this.tipo = tipo;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public int getFlagalerta() {
        return flagalerta;
    }

    public void setFlagalerta(int flagalerta) {
        this.flagalerta = flagalerta;
    }

    public Alerta getAlerta() {
        return alerta;
    }

    public void setAlerta(Alerta alerta) {
        this.alerta = alerta;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public static Evento jsonToEvento(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            //Especie especie = new Especie(objeto.getInt("idEspecie"),objeto.getString("NomeEspecie"));
            //Raca raca = new Raca(objeto.getInt("idRaca"),objeto.getString("NomeRaca"),objeto.getString("DescricaoRaca"),especie);
            //Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"),objeto.getString("Telefone"),objeto.getString("Cidade"),objeto.getString("Bairro"));
            //Animal animal = new Animal(objeto.getInt("idAnimal"),objeto.getString("Nome"),objeto.getString("Genero"),objeto.getString("Cor"),objeto.getString("Porte"),objeto.getInt("Idade"),objeto.getString("Caracteristicas"),objeto.getString("QRCode"),objeto.getString("Foto"),objeto.getInt("Desaparecido") == 1?true:false,objeto.getString("FotoCarteira"),objeto.getString("DataFotoCarteira"),usuario,raca);

            if (objeto.isNull("Aplicada"))
                objeto.put("Aplicada",0);

            if (objeto.isNull("FrequenciaAnual"))
                objeto.put("FrequenciaAnual",0);

            if (objeto.isNull("QtdDoses"))
                objeto.put("QtdDoses",0);

            if (objeto.isNull("Aplicada"))
                objeto.put("Aplicada",0);

            if (objeto.isNull("NomeLocal"))
                objeto.put("NomeLocal",0);

            if (objeto.isNull("Latitude"))
                objeto.put("Latitude",0);

            if (objeto.isNull("Longitude"))
                objeto.put("Longitude",0);

            if (objeto.isNull("FrequenciaDiaria"))
                objeto.put("FrequenciaDiaria",0);

            Usuario usuario_t = new Usuario(0,"","","","","");
            Especie especie_t = new Especie(0,"");
            Raca raca_t = new Raca(0,"","",especie_t);
            Animal animal = new Animal(0, "", "0", "0", "0", 0, "0", "0", "0", true,"0","0", usuario_t, raca_t);
            Alerta alerta = new Alerta(objeto.getInt("idAlerta"),objeto.getString("NivelAlerta"),objeto.getInt("FrequenciaAlerta"));
            Evento evento = new Evento(objeto.getInt("idEvento"),objeto.getString("Nome"),objeto.getString("Observacoes"),objeto.getInt("FlagAlerta"),alerta,animal,objeto.getString("Tipo"));;
            Compromisso compromisso = new Compromisso(evento,objeto.getString("NomeLocal"),objeto.getString("Latitude"),objeto.getString("Longitude"),objeto.getString("DataHora"));
            Medicamento medicamento = new Medicamento(evento,objeto.getString("HorasDeEspera"),objeto.getString("Inicio"),objeto.getString("Fim"),objeto.getString("FrequenciaDiaria"));
            Vacina vacina = new Vacina(evento,objeto.getInt("Aplicada"),objeto.getString("DataAplicacao"),objeto.getString("DataValidade"),objeto.getInt("FrequenciaAnual"),objeto.getInt("QtdDoses"));

            return evento;
        }
    }

    public JSONObject eventoToJson() throws JSONException {

        JSONObject objeto = new JSONObject();

        //Evento
        objeto.put("idEvento", this.getIdEvento());
        objeto.put("Nome", this.getNome());
        objeto.put("Observacoes", this.getObservacoes());
        objeto.put("FlagAlerta", this.getFlagalerta());
        objeto.put("idAnimal", this.getAnimal().getIdAnimal());
        objeto.put("Tipo", this.getTipo());

        //Alerta
        //objeto.put("idAlerta",this.evento.getAlerta().getidAlerta());
        //objeto.put("NivelAlerta",this.evento.getAlerta().getNivelAlerta());
        //objeto.put("Frequencia", this.evento.getAlerta().getFrequencia());

        //Medicamento
        //objeto.put("Inicio",this.getInicio());
        //objeto.put("Fim",this.getFim());
        //objeto.put("FrequenciaDiaria",this.getFrequenciadiaria());
        //objeto.put("HorasDeEspera",this.getHorasdeespera());

        //Vacina
        //objeto.put("Aplicada",this.getAplicada());
        //objeto.put("DataAplicacao",this.getDataaplicacao());
        //objeto.put("DataValidade",this.getDatavalidade());
        //objeto.put("FrequenciaAnual",this.getFrequenciaanual());
        //objeto.put("QtdDoses",this.getQtddoses());

        //Compromisso
        //objeto.put("NomeLocal",this.getIdEvento());
        //objeto.put("Latitude",this.getLatitude());
        //objeto.put("Longitude",this.getLongitude());
        //objeto.put("DataHora",this.getDatahora());
        return objeto;
    }
}
