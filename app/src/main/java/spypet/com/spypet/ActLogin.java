package spypet.com.spypet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controlador.Requisicao;
import modelo.Mensagem;
import modelo.Usuario;

/**
 * Created by Felipe on 24/04/2016.
 */
public class ActLogin extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private Button btLogin;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configura objeto pra receber ID do usuário, email e informações basicas de perfil.
        //DEFAULT_SIGN_IN inclui ID e informações basicas de perfil.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        //Constroi objeto com acesso a API utilizando as opções fornecidas.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btLogin = (Button)findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener(){
            //Detecta click dos botões da tela
            @Override
            public void onClick(View v) {
                    logar();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    //Método para logar usuário no aplicativo
    private void logar() {
        Intent loginIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(loginIntent, RC_SIGN_IN);

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Recupera o resultado da autenticação usando a conta google
    @Override
    public void onActivityResult(int codigoRequest, int codigoResultado, Intent dados) {
        super.onActivityResult(codigoRequest, codigoResultado, dados);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (codigoRequest == RC_SIGN_IN) {
            GoogleSignInResult resultado = Auth.GoogleSignInApi.getSignInResultFromIntent(dados);
            verificaResultado(resultado);
        }
    }

    //Verifica o resultado retornado pela API do google
    private void verificaResultado(GoogleSignInResult resultado) {
        //Verifica se a autenticação funcionou
        if (resultado.isSuccess()) {
            GoogleSignInAccount conta = resultado.getSignInAccount();
            Toast.makeText(this,conta.getDisplayName(),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "zuou", Toast.LENGTH_LONG).show();
        }
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
