package spypet.com.spypet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import controlador.GerenciadorSharedPreferences;
import controlador.Imagem;
import controlador.QRCode;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Animal;
import modelo.Especie;
import modelo.Mensagem;
import modelo.Raca;

/**
 * Created by Felipe on 24/08/2016.
 */
public class ActPets extends AppCompatActivity {

    public int tabSelecionada;
    private Animal animal;
    private ProgressDialog pd;
    private ArrayList<Especie> especies = new ArrayList<>();
    private ArrayList<Raca> racas = new ArrayList<>();
    private int processos = 0;
    private ImageView ivFotoAnimal;
    private EditText etNome;
    private EditText etCor;
    private EditText etIdade;
    private TextView tvNomePet;
    private TextView tvRacaPet;
    private ImageView ivRemover;
    private Spinner spEspecie;
    private Spinner spRaca;
    private ArrayAdapter adEspecie;
    private ArrayAdapter adRaca;
    private ImageView ivQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        try{
            Intent intent = getIntent();
            JSONObject json = new JSONObject(intent.getStringExtra("Animal"));
            animal = Animal.jsonToAnimal(json);
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPets.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        pd = ProgressDialog.show(ActPets.this, "", "Por favor aguarde...", false);
        processos++;

        carregaSpinners();

        recuperaDadosPet();

        listaCompromissos();

        carregaQRCode();

        configuraTabs();
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
            case R.id.menuConfiguracoes:
                return true;
            case R.id.menuNotificacao:
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActPets.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Configura tabs da tela de pet
    public void configuraTabs(){
        //Adiciona as opções nas tabs da tela principal
        TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);

        abas.setup();

        TabHost.TabSpec descritor = abas.newTabSpec("Dados");
        descritor.setContent(R.id.llDados);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_dados, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Compromissos");
        descritor.setContent(R.id.llCompromissos);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_calendario, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("QRCode");
        descritor.setContent(R.id.llQRCode);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_qrcode, getTheme()));
        abas.addTab(descritor);

        //Seta o fundo da primeira tab selecionada
        tabSelecionada = abas.getCurrentTab();
        abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.buttonColorPrimary));

        abas.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String arg0) {
                //Seta a cor de fundo da tab selecionada
                TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);
                for (int i = 0; i < abas.getTabWidget().getChildCount(); i++) {
                    abas.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                }
                abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.buttonColorPrimary));

                //Anima a transição de tabs
                View viewSelecionada = abas.getCurrentView();
                if (abas.getCurrentTab() > tabSelecionada)
                {
                    viewSelecionada.setAnimation(direita());
                }
                else
                {
                    viewSelecionada.setAnimation(esquerda());
                }
                tabSelecionada = abas.getCurrentTab();

            }

        });
    }

    //Anima a transição vinda da direita
    public Animation direita() {
        Animation direita = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        direita.setDuration(240);
        direita.setInterpolator(new AccelerateInterpolator());
        return direita;
    }

    //Anima a transição vinda da esquerda
    public Animation esquerda() {
        Animation esquerda = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        esquerda.setDuration(240);
        esquerda.setInterpolator(new AccelerateInterpolator());
        return esquerda;
    }

    //Recupera os dados de raça e espécie
    public void carregaSpinners(){
        especies.clear();
        especies.add(new Especie(0,"Selecione a espécie"));
        //Carrega lista de espécies
        new RequisicaoAsyncTask().execute("ListaEspecies", "0", "");
    }

    //Recupera os dados do pet selecionado
    public void recuperaDadosPet(){
        //Carrega foto do pet selecionado.
        ivFotoAnimal = (ImageView)findViewById(R.id.ivFotoAnimal);
        Picasso.with(getBaseContext()).load(animal.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoAnimal);

        //Carrega nome do pet.
        etNome = (EditText) findViewById(R.id.etNome);
        etNome.setText(animal.getNome());

        //Carrega cor do pet.
        etCor = (EditText) findViewById(R.id.etCor);
        etCor.setText(animal.getCor());

        //Carrega idade do pet.
        etIdade = (EditText) findViewById(R.id.etIdade);
        etIdade.setText(String.valueOf(animal.getIdade()));

        //Carrega nome do pet selecionado.
        tvNomePet = (TextView) findViewById(R.id.tvNomePet);
        tvNomePet.setText(animal.getNome());

        //Carrega raça do pet selecionado.
        tvRacaPet = (TextView) findViewById(R.id.tvRacaPet);
        tvRacaPet.setText(animal.getRaca().getNome());

        //Adiciona evento de click no botão de deletar pet.
        ivRemover = (ImageView) findViewById(R.id.ivRemover);
        ivRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Monta caixa de dialogo de confirmação de deleção.
                AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPets.this);
                dialogo.setTitle("Aviso!")
                        .setMessage("Você tem certeza que deseja apagar esse pet? Todos os compromissos relacionados a esse pet também serão apagados.")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Apagou", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Não", null);
                AlertDialog alerta = dialogo.create();
                alerta.show();
            }
        });

        //Recupera espécies.
        spEspecie = (Spinner) findViewById(R.id.spEspecie);
        adEspecie = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,especies){
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

        //Adiciona evento de item selecionado no spinner de especie
        spEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pd = ProgressDialog.show(ActPets.this, "", "Por favor aguarde...", false);
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

        //Recupera raças.
        spRaca = (Spinner) findViewById(R.id.spRaca);
        racas.clear();
        racas.add(new Raca(0, "Selecione a raça", "", null));
        spRaca.setSelection(0);
        adRaca = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,racas){
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

        //Recupera gênero.
        String[] generos = new String[]{"Selecione o gênero","Macho","Fêmea"};
        Spinner spGenero = (Spinner) findViewById(R.id.spGenero);
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
        int pGenero = 0;
        for(int i=0;i<generos.length;i++){
            if(generos[i].equals(animal.getGenero())){
                pGenero = i;
                break;
            }
        }
        spGenero.setSelection(pGenero);

        //Recupera porte.
        String[] portes = new String[]{"Selecione o porte","Pequeno","Médio","Grande"};
        Spinner spPorte = (Spinner) findViewById(R.id.spPorte);
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
        int pPorte = 0;
        for(int i=0;i<portes.length;i++){
            if(portes[i].equals(animal.getPorte())){
                pPorte = i;
                break;
            }
        }
        spPorte.setSelection(pPorte);

        //Carrega características do pet.
        EditText etCaracteristicas = (EditText) findViewById(R.id.etCaracteristicas);
        etCaracteristicas.setText(animal.getCaracteristicas());

        //Carrega flag de desaparecimento.
        CheckBox cbDesaparecido = (CheckBox) findViewById(R.id.cbDesaparecido);
        cbDesaparecido.setChecked(animal.isDesaparecido());

        //Adiciona evento de click no botão de salvar
        Button btSalvar = (Button) findViewById(R.id.btSalvar);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActPets.this,"Salvando", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Lista os compromissos do pet
    public void listaCompromissos(){
        List<String> lsCompromissos = new ArrayList<String>();
        lsCompromissos.add("Vacina 1");
        lsCompromissos.add("Remédio 1");
        lsCompromissos.add("Remédio 2");
        lsCompromissos.add("Remédio 3");
        lsCompromissos.add("Remédio 4");
        lsCompromissos.add("Vacina 2");
        lsCompromissos.add("Vacina 3");
        lsCompromissos.add("Vacina 4");
        lsCompromissos.add("Compromisso 1");
        lsCompromissos.add("Compromisso 2");
        lsCompromissos.add("Compromisso 3");

        ArrayAdapter<String> adpCompromissos = new ArrayAdapter<String>(this,R.layout.item_compromissos_pet){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_compromissos_pet, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                ImageView ivIconeEvento = (ImageView) convertView.findViewById(R.id.ivIconeEvento);
                TextView tvNomeCompromisso = (TextView) convertView.findViewById(R.id.tvNomeCompromisso);
                TextView tvInformacao = (TextView) convertView.findViewById(R.id.tvInformacao);

                Picasso.with(getContext()).load(R.drawable.ic_medicamento).into(ivIconeEvento);
                tvNomeCompromisso.setText("Vacina");
                tvInformacao.setText("Dia 20/12/2018");

                //Adiciona evento de click no botão de deletar pet.
                ImageView ivRemover = (ImageView) convertView.findViewById(R.id.ivExcluirCompromisso);
                ivRemover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Monta caixa de dialogo de confirmação de deleção.
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPets.this);
                        dialogo.setTitle("Aviso!")
                                .setMessage("Você tem certeza que deseja apagar esse compromisso?")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getBaseContext(), "Apagou", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("Não", null);
                        AlertDialog alerta = dialogo.create();
                        alerta.show();
                    }
                });

                return convertView;
            }
        };

        adpCompromissos.addAll(lsCompromissos);
        ListView lvCompromissos = (ListView)findViewById(R.id.lvCompromissos);
        lvCompromissos.setAdapter(adpCompromissos);
    }

    //Carrega QRCode do pet
    public void carregaQRCode(){
        ivQRCode = (ImageView) findViewById(R.id.ivQRCode);

        if(animal.getQrcode().equals("")){
            //Gera endereço do QRCode
            String endereco = controlador.QRCode.urlQRCode + animal.getIdAnimal();
            //Gera QRCode
            Bitmap QRCode = controlador.QRCode.gerarQRCode(endereco);
            //Converte QRCode para base64
            String arquivo = Imagem.qrCodeEncode(QRCode);

            try {
                JSONObject json = new JSONObject();
                json.put("QRCode", arquivo);
                new RequisicaoAsyncTask().execute("AtualizaQRCode", String.valueOf(animal.getIdAnimal()), json.toString());
            }catch(Exception ex){
                Log.e("Erro", ex.getMessage());
                Toast.makeText(ActPets.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Picasso.with(getBaseContext()).load(animal.getQrcode()).into(ivQRCode);
        }

        //Adiciona evento de clique no botão de salvar QRCode.
        Button btSalvarQRCode = (Button) findViewById(R.id.btSalvarQRCode);
        btSalvarQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Salvando...",Toast.LENGTH_LONG).show();
            }
        });
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
                Toast.makeText(ActPets.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActPets.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{
                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActPets.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                    }else{
                        //Verifica qual foi o método chamado
                        if(metodo == "ListaEspecies") {
                            //Recupera especies
                            for(int i=0;i<resultado.length();i++){
                                especies.add(Especie.jsonToEspecie(resultado.getJSONObject(i)));
                            }

                            //Seleciona espécie do animal
                            int pEspecie = 0;
                            for(int i=0;i<especies.size();i++){
                                if(especies.get(i).getIdEspecie() == animal.getRaca().getEspecie().getIdEspecie()){
                                    pEspecie = i;
                                    break;
                                }
                            }
                            spEspecie.setSelection(pEspecie);
                        }else{
                            if(metodo == "ListaRacasPorEspecie"){
                                //Recupera racas
                                for(int i=0;i<resultado.length();i++){
                                    racas.add(Raca.jsonToRaca(resultado.getJSONObject(i)));
                                }

                                //Seleciona raça do animal
                                int pRaca = 0;
                                for(int i=0;i<racas.size();i++){
                                    if(racas.get(i).getIdRaca() == animal.getRaca().getIdRaca()){
                                        pRaca = i;
                                        break;
                                    }
                                }
                                spRaca.setSelection(pRaca);
                            }else{
                                if(metodo == "AtualizaQRCode") {
                                    JSONObject qrcode = resultado.getJSONObject(0);
                                    animal.setQrcode(qrcode.getString("QRCode"));
                                    Picasso.with(getBaseContext()).load(animal.getQrcode()).into(ivQRCode);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPets.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }
}
