package spypet.com.spypet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import controlador.GerenciadorSharedPreferences;
import controlador.QRCode;
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
    private Button btEscanear;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog pd;
    private GoogleSignInAccount conta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Verifica se já existe algum usuário logado.
        if (GerenciadorSharedPreferences.getEmail(getBaseContext()).equals("")) {
            //Configura objeto pra receber ID do usuário, email e informações basicas de perfil.
            //DEFAULT_SIGN_IN inclui ID e informações basicas de perfil.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

            //Constroi objeto com acesso a API utilizando as opções fornecidas.
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addApi(AppIndex.API).build();

            //Botão de login
            btLogin = (Button) findViewById(R.id.btLogin);
            btLogin.setOnClickListener(new View.OnClickListener() {
                //Detecta click dos botões da tela
                @Override
                public void onClick(View v) {
                    logar();
                }
            });

            //Botão de escanear QRCode
            btEscanear = (Button) findViewById(R.id.btEscanear);
            btEscanear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Chama leitor de QRCode
                    Intent leitorQRCode = new Intent(ActLogin.this, ActLeitorQRCode.class);
                    leitorQRCode.putExtra("Solicitante", ActLogin.class.toString());
                    startActivity(leitorQRCode);
                }
            });
        } else {
            //Chama tela principal
            Intent principal = new Intent(ActLogin.this, ActPrincipal.class);
            startActivity(principal);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {

    }

    //Método para logar usuário no aplicativo
    private void logar() {
            Intent loginIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(loginIntent, RC_SIGN_IN);
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
            conta = resultado.getSignInAccount();

            //Chama API passando o email da google de parâmetro
            try {
                //Gera objeto para ser autenticado pela API.
                JSONObject json = new JSONObject();
                json.put("Email", conta.getEmail());
                //Chama método para recuperar usuário
                new RequisicaoAsyncTask().execute("RecuperaUsuario", "0", json.toString());
            }catch(Exception ex){
                Log.e("Erro", ex.getMessage());
                Toast.makeText(ActLogin.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Não foi possível logar com sua conta google!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActLogin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://spypet.com.spypet/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActLogin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://spypet.com.spypet/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            //Faz algo antes de executar o procedimento assincrono
            pd = ProgressDialog.show(ActLogin.this,"","Por favor aguarde...",false);
        }

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
            pd.dismiss();

            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActLogin.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{
                    //Verifica se o objeto retornado foi um json ou um usuário
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);

                        //Se o registro não foi encontrado, cadastrar novo usuário
                        if(msg.getCodigo() == 8){
                            //Chama tela de cadastro
                            Intent cadastroUsuario = new Intent(ActLogin.this, ActCadastroUsuario.class);
                            cadastroUsuario.putExtra("Nome",conta.getDisplayName());
                            cadastroUsuario.putExtra("Email",conta.getEmail());
                            startActivity(cadastroUsuario);
                        }else {
                            Toast.makeText(ActLogin.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        //Recupera usuário retornado pela API
                        Usuario usuario = Usuario.jsonToUsuario(json);

                        //Salva usuário no sharedpreferences
                        GerenciadorSharedPreferences.setEmail(getBaseContext(), usuario.getEmail());

                        //Chama tela principal
                        Intent principal = new Intent(ActLogin.this, ActPrincipal.class);
                        startActivity(principal);
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
