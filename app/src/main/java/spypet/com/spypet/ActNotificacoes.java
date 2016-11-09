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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
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
    String animal = "";
    String lat = "";
    String lot = "";
    TextView tvData;
    TextView tvPet;
    TextView tvMensagem;
    TextView tvContato;
    TextView tvContatoLabel;
    ImageView ivFotoPet;
    ImageView ivTipoNotificacao;

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

                tvData = (TextView) convertView.findViewById(R.id.tvData);
                tvPet = (TextView) convertView.findViewById(R.id.tvPet);
                tvMensagem = (TextView) convertView.findViewById(R.id.tvMensagem);
                tvContato = (TextView) convertView.findViewById(R.id.tvContato);
                tvContatoLabel = (TextView) convertView.findViewById(R.id.tvContatoLabel);
                ivTipoNotificacao = (ImageView)convertView.findViewById(R.id.ivTipoNotificacao);

                //Recebe mensagem
                Notificacao notificacao = getItem(position);
                String mensagem = notificacao.getMensagem();

                //Se não for mensagem manual
                if (!notificacao.getMensagem().contains("foi localizado pelo usuário"))
                {

                    //Seu pet xx foi visto na data xx/xx/xxxx hh:mm:ss em xxx.
                    //As coordenadas são latitude = xx e longitude = xx.

                    //Troca o valor 'As coordenadas são latitude = x e longitude = x' por outra mensagem.
                    String msg_notificacao_quebra = notificacao.getMensagem().substring(0, notificacao.getMensagem().indexOf("As coordenadas"));
                    msg_notificacao_quebra += "Clique aqui para ver a localização no mapa." ;

                    Picasso.with(getContext()).load(R.drawable.mapa).into(ivTipoNotificacao);

                    //Altera o label e insere mensagem de localização
                    tvContatoLabel.setText("Localização:");
                    tvContato.setText("Clique aqui para ver o local no mapa.");

                    //Recupera cidade onde o pet foi localizado
                    tvMensagem.setText("Pet visto em " + msg_notificacao_quebra.substring(msg_notificacao_quebra.indexOf("em") + 3,
                            msg_notificacao_quebra.indexOf(". Clique")));
                }
                else
                {
                    //Seu pet X foi localizado pelo usuário X na data xx/xx/xxxx hh:mm:ss
                    //Mensagem: xx
                    //Contato: xxxx-xxxx

                    Picasso.with(getContext()).load(R.drawable.mensagem).into(ivTipoNotificacao);

                    //Recupera mensagem:
                    tvMensagem.setText(mensagem.substring(mensagem.indexOf("Mensagem:") + 10, mensagem.indexOf("\nContato:") - 1));

                    //Recupera nome do usuário + contato de telefone
                    tvContato.setText(mensagem.substring(mensagem.indexOf("usuário") + 8, mensagem.indexOf("na data") - 1) + " - " +
                            mensagem.substring(mensagem.indexOf("Contato:") + 9, mensagem.length()));
                }

                //Recupera nome de pet e data de notificação
                tvPet.setText(mensagem.substring(mensagem.indexOf("pet") + 4, mensagem.indexOf("foi")));
                tvData.setText(mensagem.substring(mensagem.indexOf("data") + 5, mensagem.indexOf("data") + 5 + 19));

                //Se a notificação ja foi lida muda a cor do fundo
                if(notificacao.isLida()) {
                    convertView.setBackgroundResource(R.color.fundoItemLista);
                }

                    return convertView;
            }
        };

        lvNotificacoes = (ListView)findViewById(R.id.lvNotificacoes);

        //Adiciona o evento de click nos items da lista
        lvNotificacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Notificacao notificacao = (Notificacao) parent.getItemAtPosition(position);

                if (!notificacao.getMensagem().contains("foi localizado pelo usuário")) {

                    //Recupera as informações animal, latitude e longitude
                    String msg_notificacao = notificacao.getMensagem();
                    String data_notificacao = transformaData(notificacao.getDatanotificacao());
                    animal = msg_notificacao.substring(8, msg_notificacao.indexOf(" foi")) + " encontrado na data " + data_notificacao;
                    lat = msg_notificacao.substring(msg_notificacao.indexOf("latitude ") + 11, msg_notificacao.indexOf("e longitude ") - 1);
                    lot = msg_notificacao.substring(msg_notificacao.indexOf("longitude ") + 12, msg_notificacao.length() - 1);

                    Intent i = new Intent(ActNotificacoes.this, ActTelaMapa.class);
                    i.putExtra("Latitude", lat);
                    i.putExtra("Longitude", lot);
                    i.putExtra("Animal", animal);
                    startActivity(i);
                }
                else {}
            }
        });

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
                Intent intent1 = new Intent(ActNotificacoes.this, ActSobre.class);
                startActivity(intent1);
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

    public String transformaData(String data1)
    {
        String format = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(data1.replaceAll("-", "/")));
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
