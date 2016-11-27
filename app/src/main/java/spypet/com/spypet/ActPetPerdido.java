package spypet.com.spypet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Animal;
import modelo.Mensagem;

/**
 * Created by Felipe on 04/10/2016.
 */
public class ActPetPerdido extends AppCompatActivity {

    private Animal animal;
    private Button btComunicarLocalizacao;
    private String solicitante;
    private AlertDialog.Builder dialogo;
    private ProgressDialog pd;
    private int processos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_perdido);

        Intent i_Soc = getIntent();
        solicitante = i_Soc.getStringExtra("Solicitante");

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        try {
            Intent i = getIntent();
            JSONObject json = new JSONObject(i.getStringExtra("Animal"));
            animal = Animal.jsonToAnimal(json);
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPetPerdido.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        //Carrega informações do cachorro
        ImageView ivFotoPet = (ImageView) findViewById(R.id.ivFotoPet);
        Picasso.with(ActPetPerdido.this).load(animal.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoPet);

        TextView tvNome = (TextView) findViewById(R.id.tvNome);
        tvNome.setText(animal.getNome());

        TextView tvGenero = (TextView) findViewById(R.id.tvGenero);
        tvGenero.setText(animal.getGenero());

        TextView tvCor = (TextView) findViewById(R.id.tvCor);
        tvCor.setText(animal.getCor());

        TextView tvPorte = (TextView) findViewById(R.id.tvPorte);
        tvPorte.setText(animal.getPorte());

        TextView tvCaracteristicas = (TextView) findViewById(R.id.tvCaracteristicas);
        tvCaracteristicas.setText(animal.getCaracteristicas());

        TextView tvInformacoesDono = (TextView) findViewById(R.id.tvInformacoesDono);
        tvInformacoesDono.setText(animal.getUsuario().getNome() + " - " + animal.getUsuario().getTelefone());

        //Adiciona evento de click no botão de comunicar localização
        btComunicarLocalizacao = (Button) findViewById(R.id.btComunicarLocalizacao);
        btComunicarLocalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Dialog dialog = new Dialog(ActPetPerdido.this);
                //dialog.setContentView(R.layout.popup_mensagem);
                //EditText etMensagem = (EditText) findViewById(R.id.etMensagem);
                //dialog.show();

                AlertDialog.Builder alert = new AlertDialog.Builder(ActPetPerdido.this);

                alert.setTitle("Comunicar Localização do Pet");
                alert.setMessage("Insira a mensagem que será enviada para o dono do pet.");

                // Set an EditText view to get user input
                final EditText mensagem = new EditText(ActPetPerdido.this);
                alert.setView(mensagem);

                alert.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        try
                        {
                            //Mostra janela de progresso
                            pd = ProgressDialog.show(ActPetPerdido.this, "", "Por favor, aguarde...", false);
                            processos++;

                            //Monta JSON
                            JSONObject json = new JSONObject();
                            json.put("Mensagem", mensagem.getText().toString());
                            json.put("idAnimal", animal.getIdAnimal());
                            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));

                            //Envia notificação ao dono do animal
                            new RequisicaoAsyncTask().execute("InsereNotificacaoManual","0",json.toString());
                        }
                        catch(Exception ex)
                        {
                            Log.e("Erro", ex.getMessage());
                            Toast.makeText(ActPetPerdido.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
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
            case R.id.menuAjuda:
                Intent intentA = new Intent(ActPetPerdido.this, ActAjuda.class);
                startActivity(intentA);
                return true;
            case R.id.menuUsuario:
                Intent intentU = new Intent(ActPetPerdido.this, ActAtualizarUsuario.class);
                startActivity(intentU);
                return true;
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActPetPerdido.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent2 = new Intent(ActPetPerdido.this, ActNotificacoes.class);
                startActivity(intent2);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActPetPerdido.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                metodo = params[0];
                int id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo, id, conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPetPerdido.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActPetPerdido.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{

                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);

                        if(metodo == "InsereNotificacaoManual" && msg.getCodigo() == 18){
                            //Constrói mensagem de diálogo.
                            dialogo = new AlertDialog.Builder(ActPetPerdido.this);
                            dialogo.setIcon(R.mipmap.ic_launcher);
                            //Envia notificação ao usuário e apresenta mensagem ao usuário
                            dialogo.setMessage("Uma notificação foi enviada ao dono do animal!");
                            dialogo.setTitle("Obrigado!");
                            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alerta = dialogo.create();
                            alerta.show();
                        }else{
                            Toast.makeText(ActPetPerdido.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        //Se o retorno não for mensagem
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPetPerdido.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }

    public void chamaTelaAnterior(){
        //Chama tela que solicitou a leitura
        Intent tela;
        if(solicitante.equals(ActLogin.class.toString())) {
            tela = new Intent(ActPetPerdido.this, ActLogin.class);
        }else{
            tela = new Intent(ActPetPerdido.this, ActNotificacoes.class);
        }
        startActivity(tela);
    }

}
