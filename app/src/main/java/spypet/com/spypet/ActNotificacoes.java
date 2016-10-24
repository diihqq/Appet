package spypet.com.spypet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import modelo.Animal;
import modelo.Mensagem;
import modelo.Notificacao;
import modelo.Usuario;

/**
 * Created by Felipe on 02/10/2016.
 */
public class ActNotificacoes extends AppCompatActivity{

    private int processos = 0;
    private ProgressDialog pd;
    private ArrayAdapter<Notificacao> adpNotificacoes;
    private ListView lvNotificacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Carrega lista de notificações
        adpNotificacoes = new ArrayAdapter<Notificacao>(this,R.layout.item_notificacoes){
            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_notificacoes, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                TextView mensagem = (TextView) convertView.findViewById(R.id.tvMensagem);
                Notificacao notificacao = getItem(position);
                mensagem.setText(notificacao.getMensagem());

                //Se a notificação ja foi lida muda a cor do fundo
                if(notificacao.isLida()) {
                    convertView.setBackgroundResource(R.color.fundoItemLista);
                }

                return convertView;
            }
        };

        lvNotificacoes = (ListView)findViewById(R.id.lvNotificacoes);
        lvNotificacoes.setAdapter(adpNotificacoes);
        adpNotificacoes.addAll(ActPrincipal.listaNotificacoes);

        lerNotificacoes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        MenuItem item = menu.findItem(R.id.menuNotificacao);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Trata click dos menus do toolbar
        switch (item.getItemId()) {
            case R.id.menuSobre:
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActNotificacoes.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Seta todas as notificações do usuário como lidas
    public void lerNotificacoes(){
        try {
            processos++;
            pd = ProgressDialog.show(ActNotificacoes.this, "", "Por favor aguarde...", false);
            JSONObject json = new JSONObject();
            json.put("Email",GerenciadorSharedPreferences.getEmail(getBaseContext()));
            //Chama método para recuperar usuário logado
            new RequisicaoAsyncTask().execute("LerNotificacoesPorUsuario", "0", json.toString());
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActNotificacoes.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
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
                Toast.makeText(ActNotificacoes.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                if (resultado.length() > 0) {
                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        if(msg.getCodigo() != 10 || metodo != "LerNotificacoesPorUsuario") {
                            Toast.makeText(ActNotificacoes.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActNotificacoes.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            //remove dialogo de progresso da tela
            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }
}
