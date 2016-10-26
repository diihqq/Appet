package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Felipe on 02/10/2016.
 */
public class Notificacao {
    private int idNotificacao;
    private String mensagem;
    private String datanotificacao;
    private Usuario usuario;
    private boolean notificada;
    private boolean lida;

    public Notificacao(int idNotificacao, String mensagem, String datanotificacao, Usuario usuario, boolean notificada, boolean lida) {
        this.idNotificacao = idNotificacao;
        this.mensagem = mensagem;
        this.datanotificacao = datanotificacao;
        this.usuario = usuario;
        this.notificada = notificada;
        this.lida = lida;
    }

    public int getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(int idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDatanotificacao() {
        return datanotificacao;
    }

    public void setDatanotificacao(String datanotificacao) {
        this.datanotificacao = datanotificacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isNotificada() {
        return notificada;
    }

    public void setNotificada(boolean notificada) {
        this.notificada = notificada;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public static Notificacao jsonToNotificacao(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Usuario usuario = new Usuario(objeto.getInt("idUsuario"),objeto.getString("Nome"), objeto.getString("Email"), objeto.getString("Telefone"), objeto.getString("Cidade"), objeto.getString("Bairro"));
            Notificacao notificacao = new Notificacao(objeto.getInt("idNotificacao"),objeto.getString("Mensagem"),objeto.getString("DataNotificacao"),usuario,objeto.getInt("Notificada")==1?true:false,objeto.getInt("Lida")==1?true:false);
            return notificacao;
        }
    }

    public JSONObject notificacaoToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idNotificacao",this.getIdNotificacao());
        objeto.put("Mensagem",this.getMensagem());
        objeto.put("DataNotificacao",this.getDatanotificacao());
        objeto.put("idUsuario",this.getUsuario().getIdUsuario());
        objeto.put("Nome",this.getUsuario().getNome());
        objeto.put("Email",this.getUsuario().getEmail());
        objeto.put("Telefone",this.getUsuario().getTelefone());
        objeto.put("Cidade",this.getUsuario().getCidade());
        objeto.put("Bairro",this.getUsuario().getBairro());
        objeto.put("Notificada",this.isNotificada()?1:0);
        objeto.put("Lida",this.isLida()?1:0);
        return objeto;
    }
}
