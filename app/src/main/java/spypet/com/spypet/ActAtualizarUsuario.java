package spypet.com.spypet;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import modelo.Animal;
import modelo.Mensagem;
import modelo.Usuario;
import modelo.Vacina;
import android.os.Build.*;

/**
 * Created by Felipe on 09/08/2016.
 */
public class ActAtualizarUsuario extends AppCompatActivity {
    private TextView etNome;
    private TextView etEmail;
    private TextView etTelefone;
    private TextView etCidade;
    private TextView etBairro;
    private Button btAtualizar;
    private Button btExcluir;

    private String nome;
    private String email;
    private ProgressDialog pd;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualiza_usuario);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Recupera os parâmetros passados na chamada dessa tela
        try{
            Intent i = getIntent();
            JSONObject json = new JSONObject(i.getStringExtra("Usuario"));
            usuario = Usuario.jsonToUsuario(json);
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
        }

        //Recupera objetos da tela
        etNome = (TextView)findViewById(R.id.etNome);
        etEmail = (TextView)findViewById(R.id.etEmail);
        etTelefone = (TextView)findViewById(R.id.etTelefone);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            etTelefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));
        }
        etCidade = (TextView)findViewById(R.id.etCidade);
        etBairro  = (TextView)findViewById(R.id.etBairro);
        btAtualizar = (Button)findViewById(R.id.btAtualizar);
        btExcluir = (Button)findViewById(R.id.btExcluir);

        etNome.setText(usuario.getNome());
        etNome.setEnabled(false);
        etEmail.setText(usuario.getEmail());
        etEmail.setEnabled(false);
        etTelefone.setText(usuario.getTelefone());
        etCidade.setText(usuario.getCidade());
        etBairro.setText(usuario.getBairro());

        //Cadastra usuário
        btAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if (etNome.getText().toString().trim().equals("") || etTelefone.getText().toString().trim().equals("")
                        || etCidade.getText().toString().trim().equals("") || etBairro.getText().toString().trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        //Gera objeto para ser autenticado pela API.
                        JSONObject usuarioJson = new JSONObject();
                        usuarioJson.put("Nome", etNome.getText().toString().trim());
                        usuarioJson.put("Email", etEmail.getText().toString().trim());
                        etTelefone.getText().toString().replace("-", "");
                        etTelefone.getText().toString().replace(" ", "");
                        usuarioJson.put("Telefone", etTelefone.getText().toString().trim());
                        usuarioJson.put("Cidade", etCidade.getText().toString().trim());
                        usuarioJson.put("Bairro", etBairro.getText().toString().trim());

                        //Insere usuário na API
                        new RequisicaoAsyncTask().execute("AtualizaUsuario",
                                String.valueOf(usuario.getIdUsuario()),
                                usuarioJson.toString());

                    } catch (Exception ex) {
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActAtualizarUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Cadastra usuário
        btExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Monta caixa de dialogo de confirmação de deleção.
                AlertDialog.Builder dialogo = new AlertDialog.Builder(ActAtualizarUsuario.this);
                dialogo.setTitle("Aviso!")
                        .setMessage("Você tem certeza que deseja apagar o seu usuário?")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Insere usuário na API
                                new RequisicaoAsyncTask().execute("ExcluiUsuario",
                                        String.valueOf(usuario.getIdUsuario()),"");
                            }
                        })
                        .setNegativeButton("Não", null);
                AlertDialog alerta = dialogo.create();
                alerta.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Trata click dos menus do toolbar
        switch (item.getItemId()) {
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActAtualizarUsuario.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActAtualizarUsuario.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActAtualizarUsuario.this, ActLogin.class);
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
                Toast.makeText(ActAtualizarUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {

            try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActAtualizarUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);

                        //Verifica se o usuário foi atualizado com sucesso
                        if (msg.getCodigo() == 10) {
                            //Salva usuário na sharedpreferences
                            GerenciadorSharedPreferences.setEmail(getBaseContext(), etEmail.getText().toString());

                            Toast.makeText(ActAtualizarUsuario.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                            //Chama tela principal
                            Intent principal = new Intent(ActAtualizarUsuario.this, ActPrincipal.class);
                            startActivity(principal);
                        } else
                        {
                            //Exclusão do usuário
                            if (msg.getCodigo() == 11)
                            {
                                //Limpa SharedPreferences
                                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                                //Chama tela de login
                                Intent principal = new Intent(ActAtualizarUsuario.this, ActLogin.class);
                                startActivity(principal);

                    }
                    else {
                        Toast.makeText(ActAtualizarUsuario.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                        Toast.makeText(ActAtualizarUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActAtualizarUsuario.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
