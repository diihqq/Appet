package spypet.com.spypet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import controlador.Requisicao;
import modelo.Mensagem;

/**
 * Created by Felipe on 24/04/2016.
 */
public class ActLogin extends Activity{
    private Button btLogin;
    private EditText etEmail;
    private EditText etSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btLogin = (Button)findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent principal = new Intent(ActLogin.this, ActPrincipal.class);
                startActivity(principal);
            }
        });

        /*//Adiciona evento de clique ao botão de login
        btLogin = (Button)findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail = (EditText) findViewById(R.id.etEmail);
                etSenha = (EditText) findViewById(R.id.etSenha);

                //Verifica se o email e senha foram informados
                if (etEmail.getText().toString().equals("") || etSenha.getText().toString().equals("")) {
                    Toast.makeText(ActLogin.this, R.string.informe_email_e_senha, Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        String email = etEmail.getText().toString();
                        String senha = Usuario.geraMD5(etSenha.getText().toString());

                        //Gera objeto para ser autenticado pela API.
                        JSONObject json = new JSONObject();
                        json.put("usuario", email);
                        json.put("senha", senha);
                        new RequisicaoAsyncTask().execute("autenticacao", json.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                //Recupera parâmetros e realiza a requisição
                String metodo = params[0];
                String conteudo = params[1];
                return Requisicao.chamaMetodo(metodo,conteudo);
            } catch (Exception e) {
                Toast.makeText(ActLogin.this, "URL inválida.", Toast.LENGTH_LONG).show();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String resultado) {
            String url = "";
            try {
                //Verifica se algum erro ocorreu durante a requisição para ser mostrada a mensagem.
                if (resultado.contains("Erro: ")) {
                    Toast.makeText(ActLogin.this, resultado, Toast.LENGTH_SHORT).show();
                } else {
                    //Converte o resultado obtido em um obejto.
                    JSONObject json = new JSONObject(resultado);
                    Mensagem mensagem = Mensagem.jsonToMensagem(json);

                    //Verifica se o usuário foi logado com sucesso (código 4).
                    if(mensagem.getCodigo() == 4){
                        //Chamar outra activity ****************
                        Toast.makeText(ActLogin.this, mensagem.getMensagem(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ActLogin.this, mensagem.getMensagem(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (Exception e) {
                Toast.makeText(ActLogin.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
