package spypet.com.spypet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import modelo.Alerta;
import modelo.Animal;
import modelo.Especie;
import modelo.Mensagem;
import modelo.Raca;
import modelo.Usuario;

/**
 * Created by Diogo on 01/10/2016.
 */
public class ActCadastroCompromisso extends AppCompatActivity {
    private TextView etNomeCompromisso;
    private TextView etNomeLocal;
    private EditText etDataHora;
    private TextView etEventoObservacoes;
    //private TextView etFlagAlerta;
    //private TextView etAlerta
    private Spinner spAnimal;
    private Spinner spAlerta;
    private Button btInscrever;
    private String nome;
    private String email;
    private ProgressDialog pd;
    private ArrayList<Alerta> alertas = new ArrayList<>();
    private ArrayList<Animal> animais = new ArrayList<>();
    private Usuario usuario_t = new Usuario(0,"","","","","");
    private Especie especie_t = new Especie(0,"");
    private Raca raca_t = new Raca(0,"","",especie_t);
    private Animal animal_escolhido;
    private Alerta alerta_escolhido;
    private int processos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_compromisso);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Recupera os parâmetros passados na chamada dessa tela
        Intent i = getIntent();
        nome = i.getStringExtra("Nome");
        email = i.getStringExtra("Email");

        //Recupera objetos da tela
        etNomeCompromisso = (TextView)findViewById(R.id.etNomeCompromisso);
        etNomeLocal = (TextView)findViewById(R.id.etNomeLocal);
        etDataHora = (EditText)findViewById(R.id.etDataHora);
        etEventoObservacoes = (TextView)findViewById(R.id.etEventoObservacoes);

        //Carrega spinners da tela com os valores
        CarregaSpinners();

        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "ddmmaaaa";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    etDataHora.setText(current);
                    etDataHora.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };


        etDataHora.addTextChangedListener(tw);

        btInscrever = (Button)findViewById(R.id.btCadastrar);

        //Cadastra evento e medicamento
        btInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if(etNomeCompromisso.getText().toString().trim().equals("") || etNomeLocal.getText().toString().trim().equals("") ||
                        etDataHora.getText().toString().trim().equals("") || spAlerta.getSelectedItemPosition() == 0 ||
                        spAnimal.getSelectedItemPosition() == 0 ){
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                }else{
                    try {
                        //Gera objeto para ser autenticado pela API.
                        JSONObject usuarioJsonEvento = new JSONObject();

                        //Evento
                        usuarioJsonEvento.put("Nome", etNomeCompromisso.getText().toString().trim());
                        if (!etEventoObservacoes.getText().toString().trim().equals(""))
                            usuarioJsonEvento.put("Observacoes", etEventoObservacoes.getText().toString().trim());
                        else
                            usuarioJsonEvento.put("Observacoes", "");

                        usuarioJsonEvento.put("FlagAlerta", "1");
                        usuarioJsonEvento.put("idAlerta", alerta_escolhido.getidAlerta());
                        usuarioJsonEvento.put("idAnimal", animal_escolhido.getIdAnimal());
                        usuarioJsonEvento.put("Tipo", "Compromisso");

                        //Medicamento
                        usuarioJsonEvento.put("NomeLocal", etNomeLocal.getText().toString().trim());
                        usuarioJsonEvento.put("Latitude", "x");
                        usuarioJsonEvento.put("Longitude", "x");

                        //Converte data pra yyyy-MM-dd
                        Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataHora.getText().toString());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String parsedDate = formatter.format(initDate);

                        usuarioJsonEvento.put("DataHora",parsedDate);

                        //Insere usuário na API
                         new RequisicaoAsyncTask().execute("InsereEvento", "0", usuarioJsonEvento.toString());

                    }catch (Exception ex){
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
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
            case R.id.menuConfiguracoes:
                return true;
            case R.id.menuNotificacao:
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActCadastroCompromisso.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void CarregaSpinners()
    {
        //Carrega spinner de alertas
        alertas.clear();
        alertas.add(new Alerta(0, "Selecione o alerta",0));
        spAlerta = (Spinner) findViewById(R.id.spAlerta);
        ArrayAdapter adAlerta = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, alertas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if (position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                } else {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                } else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spAlerta.setAdapter(adAlerta);

        //Adiciona evento de item selecionado no spinner de alerta
        spAlerta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    alerta_escolhido = (Alerta) spAlerta.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carrega lista de alertas
        pd = ProgressDialog.show(ActCadastroCompromisso.this, "", "Por favor, aguarde...", false);
        processos++;
        new RequisicaoAsyncTask().execute("ListaAlertas", "0", "");

        //Carrega spinner de animais
        animais.clear();
        animais.add(new Animal(0, "Selecione o animal", "0", "0", "0", 0, "0", "0", "0", true, usuario_t, raca_t));
        spAnimal = (Spinner) findViewById(R.id.spAnimal);
        ArrayAdapter adAnimal = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, animais) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if (position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                } else {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                } else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spAnimal.setAdapter(adAnimal);

        //Adiciona evento de item selecionado no spinner de animal
        spAnimal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    animal_escolhido = (Animal)spAnimal.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carrega lista de animais
        try {
            JSONObject json = new JSONObject();
            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
            new RequisicaoAsyncTask().execute("ListaAnimaisDoUsuario", "0", json.toString());
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {

            try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json))
                    {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActCadastroCompromisso.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Verifica se o usuário foi inserido com sucesso
                        if (metodo == "InsereEvento" && msg.getCodigo() == 7) {
                            //Chama tela principal
                            Intent principal = new Intent(ActCadastroCompromisso.this, ActPrincipal.class);
                            startActivity(principal);
                        }
                    }
                    else {
                        //Verifica qual foi o método chamado
                        if (metodo == "ListaAlertas") {
                            //Recupera alertas
                            for (int i = 0; i < resultado.length(); i++) {
                                alertas.add(Alerta.jsonToAlerta(resultado.getJSONObject(i)));
                            }
                        }

                        //Verifica qual foi o método chamado
                        if (metodo == "ListaAnimaisDoUsuario") {
                            //Recupera animais do usuário
                            for (int i = 0; i < resultado.length(); i++) {
                                animais.add(Animal.jsonToAnimal(resultado.getJSONObject(i)));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if (processos == 0) {
                pd.dismiss();

            }
        }
    }

}
