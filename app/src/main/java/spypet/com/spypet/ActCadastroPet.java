package spypet.com.spypet;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import controlador.GerenciadorSharedPreferences;
import controlador.Imagem;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Especie;
import modelo.Evento;
import modelo.Mensagem;
import modelo.Raca;
import modelo.Usuario;

/**
 * Created by Felipe on 04/09/2016.
 */
public class ActCadastroPet extends AppCompatActivity {

    private EditText etNome;
    //private EditText etCor;
    //private EditText etIdade;
    private EditText etCaracteristicas;
    private Spinner spEspecie;
    private Spinner spRaca;
    private Spinner spGenero;
    private Spinner spPorte;
    private Spinner spCor;
    private Spinner spIdade;
    private ImageView ivFotoPet;
    private AlertDialog.Builder dialogo;
    private AlertDialog alerta;
    private static final int READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 1;
    private Intent selecionarImagem;
    Uri imagemSelecionada = null;
    private ProgressDialog pd;
    private ArrayList<Especie> especies = new ArrayList<>();
    private ArrayList<Raca> racas = new ArrayList<>();
    private ArrayList<String> cores = new ArrayList<>();
    private ArrayList<Integer> idades = new ArrayList<>();
    private int processos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pet);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Constrói mensagem de diálogo.
        dialogo = new AlertDialog.Builder(ActCadastroPet.this);
        dialogo.setIcon(R.mipmap.ic_launcher);
        //Apresenta mensagem de aviso ao usuário
        dialogo.setMessage("Para usar essa função é necessário que o aplicativo tenha permissão de acesso ao armazenamento de arquivos! Clique em 'OK' para ir até a tela de permissões do Appet.");
        dialogo.setTitle("Aviso!");
        dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startInstalledAppDetailsActivity(ActCadastroPet.this);
            }
        });
        alerta = dialogo.create();

        //Inicia variavel para seleção de imagem
        selecionarImagem = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //Recupera objeto de foto do animal e adiciona evento de click
        ivFotoPet = (ImageView) findViewById(R.id.ivFotoPet);
        ivFotoPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica permissões somente se a API for 23 ou maior
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    verificaPermissao();
                }
            }
        });

        //Carrega spinners da tela com os valores
        CarregaSpinners();

        //Adiciona evento de click no botão de salvar
        Button btSalvar = (Button) findViewById(R.id.btSalvar);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastraPet();
            }
        });
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
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
                Intent intent1 = new Intent(ActCadastroPet.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActCadastroPet.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActCadastroPet.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Carrega spinners na tela com os valores do banco de dados
    public void CarregaSpinners(){
        //Carrega spinner de espécies
        especies.clear();
        especies.add(new Especie(0,"Selecione a espécie"));
        spEspecie = (Spinner) findViewById(R.id.spEspecie);
        ArrayAdapter adEspecie = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,especies){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spEspecie.setAdapter(adEspecie);

        //Carrega lista de espécies
        pd = ProgressDialog.show(ActCadastroPet.this, "", "Por favor, aguarde...", false);
        processos++;
        new RequisicaoAsyncTask().execute("ListaEspecies", "0", "");

        //Adiciona evento de item selecionado no spinner de especie
        spEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    pd = ProgressDialog.show(ActCadastroPet.this, "", "Por favor, aguarde...", false);
                    racas.clear();
                    racas.add(new Raca(0, "Selecione a raça", "", null));
                    spRaca.setSelection(0);
                    processos++;
                    new RequisicaoAsyncTask().execute("ListaRacasPorEspecie", String.valueOf(especies.get(position).getIdEspecie()), "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carrega spinner de raças
        racas.clear();
        racas.add(new Raca(0, "Selecione a raça", "", null));
        spRaca = (Spinner) findViewById(R.id.spRaca);
        ArrayAdapter adRaca = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,racas){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }

                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spRaca.setAdapter(adRaca);

        //Recupera gêneros.
        String[] generos = new String[]{"Selecione o gênero","Macho","Fêmea"};
        spGenero = (Spinner) findViewById(R.id.spGenero);
        ArrayAdapter adGenero = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,generos){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }

                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spGenero.setAdapter(adGenero);

        //Recupera portes.
        String[] portes = new String[]{"Selecione o porte","Pequeno","Médio","Grande"};
        spPorte = (Spinner) findViewById(R.id.spPorte);
        ArrayAdapter adPorte = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,portes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }

                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spPorte.setAdapter(adPorte);

        //Carrega spinner de cor
        String[] cores = new String[]{"Selecione a cor do animal","Branco","Canela","Chocolate","Cinza","Marrom","Marrom Claro","Marrom Escuro","Marrom Terra","Preto","Outra"};
        spCor = (Spinner) findViewById(R.id.spCor);
        ArrayAdapter adCores = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,cores){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spCor.setAdapter(adCores);

        //Carrega spinner de idade
        String[] idades = new String[]{"Selecione a idade do animal","Até 1 ano","1 ano","2 anos","3 anos","4 anos","5 anos","6 anos","7 anos","8 anos","9 anos","10 anos","11 anos","12 anos","13 anos","14 anos","15 anos","16 anos","17 anos","18 anos","19 anos","20 anos","21 anos","22 anos","23 anos","24 anos","25 anos","26 anos","27 anos","28 anos","29 anos","30 anos"};
        spIdade = (Spinner) findViewById(R.id.spIdade);
        ArrayAdapter adIdades = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,idades){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spIdade.setAdapter(adIdades);
    }

    //Valida informações e cadastra o pet
    public void CadastraPet(){
        String erro = "";

        //Recupera nome do pet.
        etNome = (EditText) findViewById(R.id.etNome);

        //Recupera caracteristicas do pet.
        etCaracteristicas = (EditText) findViewById(R.id.etCaracteristicas);

        //Valida dados fornecidos
        if(etNome.getText().toString().trim().equals("")){
            erro = "Preencha o nome!";
        }else{
            if(spCor.getSelectedItemPosition() == 0){
                erro = "Preencha a cor!";
            }else{
                if(spIdade.getSelectedItemPosition() == 0){
                    erro = "Preencha a idade!";
                }else{
                    if(spEspecie.getSelectedItemPosition() == 0){
                        erro = "Selecione a espécie!";
                    }else{
                        if(spRaca.getSelectedItemPosition() == 0){
                            erro = "Selecione a raça!";
                        }else{
                            if(spGenero.getSelectedItemPosition() == 0){
                                erro = "Selecione o gênero!";
                            }else{
                                if(spPorte.getSelectedItemPosition() == 0){
                                    erro = "Selecione o porte!";
                                }else{
                                    if(imagemSelecionada == null){
                                        erro = "Selecione uma foto do seu pet!";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        try {
            //Verifica se foi encontrado algum problema
            if (erro.equals("")) {
                JSONObject json = new JSONObject();
                String idade = "";
                json.put("Nome", etNome.getText().toString());
                json.put("Genero", spGenero.getSelectedItem().toString());
                json.put("Cor", spCor.getSelectedItem().toString());
                json.put("Porte", spPorte.getSelectedItem().toString());

                if (spIdade.getSelectedItem().toString() == "Até 1 ano")
                    idade = "0";
                else
                    idade = spIdade.getSelectedItem().toString().replace("Até ","").replace(" ano", "").replace("s", "");

                json.put("Idade", idade);

                json.put("Caracteristicas", etCaracteristicas.getText().toString());
                json.put("QRCode", "");
                json.put("Foto", Imagem.fotoEncode(Imagem.recuperaCaminho(imagemSelecionada, ActCadastroPet.this)));
                json.put("Desaparecido", 0);
                json.put("FotoCarteira", "");
                json.put("DataFotoCarteira", "");
                json.put("idUsuario", ActPrincipal.usuarioLogado.getIdUsuario());
                json.put("idRaca", ((Raca)spRaca.getSelectedItem()).getIdRaca());

                //Carrega lista de espécies
                pd = ProgressDialog.show(ActCadastroPet.this, "", "Por favor, aguarde...", false);
                processos++;
                new RequisicaoAsyncTask().execute("InsereAnimal", "0", json.toString());

            } else {
                Toast.makeText(ActCadastroPet.this, erro, Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActCadastroPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    //Verifica se o aplicativo tem permissão para acessar o armazenamento de arquivos
    @TargetApi(Build.VERSION_CODES.M)
    public void verificaPermissao(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //Verifica se o usuário selecionou a opções de não perguntar novamente.
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
            }else{
                alerta.show();
            }
        }else{
            //Abre tela para seleção da imagem
            startActivityForResult(selecionarImagem , 1);
        }
    }

    // Callback da requisição de permissão
    @Override
    public void onRequestPermissionsResult(int codigoRequisicao, String permissoes[], int[] resultados) {
        // Verifica se esse retorno de resposta é referente a requisição de permissão da CAMERA
        if (codigoRequisicao == READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            if (resultados.length == 1 && resultados[0] == PackageManager.PERMISSION_GRANTED) {
                //Abre tela para seleção da imagem
                startActivityForResult(selecionarImagem, 1);
            } else {
                alerta.show();
            }
        }else{
            super.onRequestPermissionsResult(codigoRequisicao, permissoes, resultados);
        }
    }

    //Recebe a resposta da seleção de imagem.
    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent imagem) {
        super.onActivityResult(codigoRequisicao, codigoResultado, imagem);
        if(codigoRequisicao == 1 && codigoResultado == RESULT_OK){
            imagemSelecionada = imagem.getData();
            Picasso.with(ActCadastroPet.this).load(imagemSelecionada).transform(new TransformacaoCirculo()).into(ivFotoPet);
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
                Toast.makeText(ActCadastroPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActCadastroPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{

                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActCadastroPet.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        if(metodo == "InsereAnimal" && msg.getCodigo() == 7){
                            Intent tela = new Intent(ActCadastroPet.this,ActPrincipal.class);
                            startActivity(tela);
                        }

                    }else{
                        //Verifica qual foi o método chamado
                        if(metodo == "ListaEspecies") {
                            //Recupera especies
                            for(int i=0;i<resultado.length();i++){
                                especies.add(Especie.jsonToEspecie(resultado.getJSONObject(i)));
                            }
                        }else{
                            if(metodo == "ListaRacasPorEspecie"){
                                //Recupera racas
                                for(int i=0;i<resultado.length();i++){
                                    racas.add(Raca.jsonToRaca(resultado.getJSONObject(i)));
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActCadastroPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }
}
