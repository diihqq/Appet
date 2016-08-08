package spypet.com.spypet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controlador.Requisicao;
import modelo.Mensagem;
import modelo.Usuario;

/**
 * Created by Felipe on 24/04/2016.
 */
public class ActLogin extends Activity{
    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btLogin = (Button)findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Chama tela principal
                //Intent principal = new Intent(ActLogin.this, ActPrincipal.class);
                //startActivity(principal);

                try {
                    //Gera objeto para ser autenticado pela API.
                    JSONObject json = new JSONObject();
                    json.put("Email", "felipe@email.com");
                    //Chama método para recuperar usuário
                    new RequisicaoAsyncTask().execute("RecuperaUsuario", "0", json.toString());
                }catch(Exception ex){
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActLogin.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                String metodo = params[0];
                int id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo,id,conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActLogin.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            String url = "";
            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActLogin.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{
                    //Verifica se o objeto retornado foi um json ou um usuário
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActLogin.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                    }else{
                        //Recupera usuário retornado pela API
                        Usuario usuario = Usuario.jsonToUsuario(json);
                        Toast.makeText(ActLogin.this, "Bem vindo " + usuario.getNome(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActLogin.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
