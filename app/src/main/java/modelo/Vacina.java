package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Diogo on 01/10/2016.
 */
public class Vacina {
    private Evento evento;
    private int aplicada, frequenciaanual,qtddoses;
    private String dataaplicacao, datavalidade;

    public Vacina(Evento evento, int aplicada, String dataaplicacao, String datavalidade, int frequenciaanual, int qtddoses) {
        this.evento = evento;
        this.aplicada = aplicada;
        this.frequenciaanual = frequenciaanual;
        this.qtddoses = qtddoses;
        this.dataaplicacao = dataaplicacao;
        this.datavalidade = datavalidade;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public int getAplicada() {
        return aplicada;
    }

    public void setAplicada(int aplicada) {
        this.aplicada = aplicada;
    }

    public int getFrequenciaanual() {
        return frequenciaanual;
    }

    public void setFrequenciaanual(int frequenciaanual) {
        this.frequenciaanual = frequenciaanual;
    }

    public int getQtddoses() {
        return qtddoses;
    }

    public void setQtddoses(int qtddoses) {
        this.qtddoses = qtddoses;
    }

    public String getDataaplicacao() {
        return dataaplicacao;
    }

    public void setDataaplicacao(String dataaplicacao) {
        this.dataaplicacao = dataaplicacao;
    }

    public String getDatavalidade() {
        return datavalidade;
    }

    public void setDatavalidade(String datavalidade) {
        this.datavalidade = datavalidade;
    }

    public static Vacina jsonToAnimal(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {

            Animal animal;
            Especie especie_t = new Especie(0,"");
            Raca raca = new Raca(0,"","",especie_t);
            Usuario usuario = new Usuario(0,"","","","","");

            //NomeAnimal nulo => ListaEventosPorAnimal => não precisa das informações do animal
        //    if (objeto.isNull("NomeAnimal"))
                animal = new Animal(0, "", "0", "0", "0", 0, "0", "0", "0", true,"0","0", usuario, raca);
           // else //NomeAnimal preenchido => ListaEventosPorUsuario => precisa das informações do animal
               // animal = new Animal(objeto.getInt("idAnimal"),objeto.getString("Nome"),objeto.getString("Genero"),objeto.getString("Cor"),objeto.getString("Porte"),objeto.getInt("Idade"),objeto.getString("Caracteristicas"),objeto.getString("QRCode"),objeto.getString("Foto"),objeto.getInt("Desaparecido") == 1?true:false,objeto.getString("FotoCarteira"),objeto.getString("DataFotoCarteira"),usuario,raca);

            Alerta alerta = new Alerta(objeto.getInt("idAlerta"),objeto.getString("NivelAlerta"),objeto.getInt("Frequencia"));
            Evento evento = new Evento(objeto.getInt("idEvento"),objeto.getString("Nome"),objeto.getString("Observacoes"),objeto.getInt("FlagAlerta"),alerta,animal,objeto.getString("Tipo"));;
            Vacina vacina = new Vacina(evento,objeto.getInt("Aplicada"),objeto.getString("DataAplicacao"),objeto.getString("DataValidade"),objeto.getInt("FrequenciaAnual"),objeto.getInt("QtdDoses"));
            return vacina;
        }
    }

    public JSONObject vacinaToJson() throws JSONException {
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

        //Vacina
        objeto.put("Aplicada",this.getAplicada());
        objeto.put("DataAplicacao",this.getDataaplicacao());
        objeto.put("DataValidade",this.getDatavalidade());
        objeto.put("FrequenciaAnual",this.getFrequenciaanual());
        objeto.put("QtdDoses",this.getQtddoses());
        return objeto;
    }
}
