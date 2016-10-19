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
            Especie especie = new Especie(objeto.getInt("idEspecie"),objeto.getString("NomeEspecie"));
            Raca raca = new Raca(objeto.getInt("idRaca"),objeto.getString("NomeRaca"),objeto.getString("DescricaoRaca"),especie);
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("NomeUsuario"),objeto.getString("Email"),objeto.getString("Telefone"),objeto.getString("Cidade"),objeto.getString("Bairro"));
            Animal animal = new Animal(objeto.getInt("idAnimal"),objeto.getString("Nome"),objeto.getString("Genero"),objeto.getString("Cor"),objeto.getString("Porte"),objeto.getInt("Idade"),objeto.getString("Caracteristicas"),objeto.getString("QRCode"),objeto.getString("Foto"),objeto.getInt("Desaparecido") == 1?true:false,objeto.getString("FotoCarteira"),objeto.getString("DataFotoCarteira"),usuario,raca);
            Alerta alerta = new Alerta(objeto.getInt("idAlerta"),objeto.getString("nivelAlerta"),objeto.getInt("frequencia"));
            Evento evento = new Evento(objeto.getInt("idEvento"),objeto.getString("nome"),objeto.getString("observacoes"),objeto.getInt("flagalerta"),alerta,animal,objeto.getString("tipo"));;
            Vacina vacina = new Vacina(evento,objeto.getInt("aplicada"),objeto.getString("dataaplicacao"),objeto.getString("datavalidade"),objeto.getInt("frequenciaanual"),objeto.getInt("qtddoses"));
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
