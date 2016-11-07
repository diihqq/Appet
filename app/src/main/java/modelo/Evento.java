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
    private Compromisso compromisso;
    private Medicamento medicamento;
    private Vacina vacina;

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

    public Compromisso getCompromisso() {
        return compromisso;
    }

    public void setCompromisso(Compromisso compromisso) {
        this.compromisso = compromisso;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public Vacina getVacina() {
        return vacina;
    }

    public void setVacina(Vacina vacina) {
        this.vacina = vacina;
    }

    public static Evento jsonToEvento(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {

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

            Animal animal;
            Especie especie_t = new Especie(0,"");
            Raca raca = new Raca(0,"","",especie_t);
            Usuario usuario = new Usuario(0,"","","","","");

            //NomeAnimal nulo => ListaEventosPorAnimal => não precisa das informações do animal
            if (objeto.isNull("NomeAnimal"))
                animal = new Animal(0, "", "0", "0", "0", 0, "0", "0", "0", true,"0","0", usuario, raca);
            else //NomeAnimal preenchido => ListaEventosPorUsuario => precisa das informações do animal
               animal = new Animal(objeto.getInt("idAnimal"),objeto.getString("NomeAnimal"),objeto.getString("Genero"),objeto.getString("Cor"),objeto.getString("Porte"),objeto.getInt("Idade"),objeto.getString("Caracteristicas"),objeto.getString("QRCode"),objeto.getString("Foto"),objeto.getInt("Desaparecido") == 1?true:false,objeto.getString("FotoCarteira"),objeto.getString("DataFotoCarteira"),usuario,raca);

            Alerta alerta = new Alerta(objeto.getInt("idAlerta"),objeto.getString("NivelAlerta"),objeto.getInt("FrequenciaAlerta"));
            Evento evento = new Evento(objeto.getInt("idEvento"),objeto.getString("Nome"),objeto.getString("Observacoes"),objeto.getInt("FlagAlerta"),alerta,animal,objeto.getString("Tipo"));

            if (evento.getTipo().equals("Compromisso")) {
                Compromisso compromisso = new Compromisso(evento,objeto.getString("NomeLocal"),objeto.getString("Latitude"), objeto.getString("Longitude"),objeto.getString("DataHora"));
                evento.setCompromisso(compromisso);

            }

            if (evento.getTipo().equals("Medicamento")) {
                Medicamento medicamento = new Medicamento(evento,objeto.getString("HorasDeEspera"),objeto.getString("Inicio"),objeto.getString("Fim"),objeto.getString("FrequenciaDiaria"));
                evento.setMedicamento(medicamento);
            }
            if (evento.getTipo().equals("Vacina")) {
                Vacina vacina = new Vacina(evento,objeto.getInt("Aplicada"),objeto.getString("DataAplicacao"),objeto.getString("DataValidade"),objeto.getInt("FrequenciaAnual"),objeto.getInt("QtdDoses"));
                evento.setVacina(vacina);
            }

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
