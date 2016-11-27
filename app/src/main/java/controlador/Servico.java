package controlador;

import android.app.ExpandableListActivity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import modelo.Evento;
import modelo.Mensagem;
import modelo.Notificacao;
import spypet.com.spypet.ActPrincipal;
import spypet.com.spypet.ActTelaMapa;

/**
 * Created by Felipe on 30/10/2016.
 */

public class Servico extends IntentService {

    public static boolean processoRodando = false;
    private ArrayList<Notificacao> notificacoes = new ArrayList<>();

    public Servico(){
        super("Servico");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        processoRodando = true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Cria uma thread com intervalo de 30 segundos de execução
        new Thread(new Runnable(){
            public void run() {
                int id = 0;
                while(true)
                {
                    try {

                        if(Conexao.verificaConexao(getBaseContext())) {
                            //Lista as notificações do usuário
                            JSONObject json = new JSONObject();
                            json.put("Email",GerenciadorSharedPreferences.getEmail(getBaseContext()));
                            //Chama método para recuperar usuário logado
                            new Servico.RequisicaoAsyncTask().execute("ListaNotificacoesPorUsuario", "0", json.toString());
                        }else{
                            break;
                        }
                        Thread.sleep(30000);

                    } catch (Exception ex) {
                        Log.e("Erro", ex.getMessage());
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;
        private int id;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                metodo = params[0];
                id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo, id, conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                if (resultado.length() > 0) {
                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (!Mensagem.isMensagem(json)) {

                        notificacoes.clear();
                        //Monta lista de eventos dos animais do usuário logado
                        for (int i = 0; i < resultado.length(); i++) {
                            notificacoes.add(Notificacao.jsonToNotificacao(resultado.getJSONObject(i)));
                        }

                        //Lança as notificações pendentes
                        for(int i = 0; i<notificacoes.size(); i++) {
                            if(!notificacoes.get(i).isNotificada()) {
                                Notificacoes n = new Notificacoes(getBaseContext(), "Appet", notificacoes.get(i).getMensagem());
                                n.Notificar(notificacoes.get(i).getIdNotificacao());
                            }
                        }

                        //Lista as notificações do usuário
                        JSONObject conteudo = new JSONObject();
                        conteudo.put("Email",GerenciadorSharedPreferences.getEmail(getBaseContext()));
                        //Atualiza as notificações pendentes
                        new Servico.RequisicaoAsyncTask().execute("AlertarNotificacoesPorUsuario", "0", conteudo.toString());
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
            }
        }
    }
}
