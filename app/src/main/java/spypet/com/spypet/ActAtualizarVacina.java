package spypet.com.spypet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Alerta;
import modelo.Animal;
import modelo.Especie;
import modelo.Mensagem;
import modelo.Raca;
import modelo.Usuario;
import modelo.Vacina;

/**
 * Created by Diogo on 01/10/2016.
 */
public class ActAtualizarVacina extends AppCompatActivity {
    private EditText etNomeVacina;
    private TextView tvAnimal;
    private EditText etDataAplicacao;
    private EditText etDataValidade;
    private ImageView ivFotoPet;
   // private TextView etFrequenciaAnual;
    private EditText etQtdDoses;
    private EditText etEventoObservacoes;
    //private Spinner spAlerta;
    private Spinner spAplicada;
    private Button btInscrever;
    private String nome;
    private String email;
    private ProgressDialog pd;
    private ArrayList<Alerta> alertas = new ArrayList<>();
    private ArrayList<Animal> animais = new ArrayList<>();
    private ArrayList<String> aplicadas = new ArrayList<>();
    private Especie especie_t = new Especie(0,"");
    private int aplicada;
    private int processos = 0;
    private Vacina vacina;
    private Animal animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualiza_vacina);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Recupera os parâmetros passados na chamada dessa tela
        try{
            Intent i = getIntent();
            nome = i.getStringExtra("Nome");
            email = i.getStringExtra("Email");

            JSONObject json = new JSONObject(i.getStringExtra("Vacina"));
            vacina = Vacina.jsonToAnimal(json);

            JSONObject json2 = new JSONObject(i.getStringExtra("Animal"));
            animal = Animal.jsonToAnimal(json2);

            vacina.getEvento().setAnimal(animal);

        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
        }

        //Recupera objetos da tela
        etNomeVacina = (EditText)findViewById(R.id.etNomeVacina);
        etDataAplicacao = (EditText)findViewById(R.id.etDataAplicacao);
        etDataValidade  = (EditText)findViewById(R.id.etDataValidade);
        tvAnimal = (TextView)findViewById(R.id.tvAnimal);
        etQtdDoses = (EditText)findViewById(R.id.etQtdDoses);
        etEventoObservacoes = (EditText)findViewById(R.id.etEventoObservacoes);

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
                    etDataValidade.setText(current);
                    etDataValidade.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };

        TextWatcher tw2 = new TextWatcher() {
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

                    etDataAplicacao.setText(current);
                    etDataAplicacao.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etDataValidade.addTextChangedListener(tw);
        etDataAplicacao.addTextChangedListener(tw2);

        recuperaDadosVacina();

        btInscrever = (Button)findViewById(R.id.btCadastrar);

        //Cadastra evento e medicamento
        btInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if(etNomeVacina.getText().toString().trim().equals("") || spAplicada.getSelectedItemPosition() == 0){
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                }else{
                    try {

                        //Gera objeto para ser autenticado pela API.
                        JSONObject usuarioJsonEvento = new JSONObject();

                        //Evento
                        usuarioJsonEvento.put("Nome", etNomeVacina.getText().toString().trim());

                        if (!etEventoObservacoes.getText().toString().trim().equals(""))
                            usuarioJsonEvento.put("Observacoes", etEventoObservacoes.getText().toString().trim());
                        else
                            usuarioJsonEvento.put("Observacoes", "");

                        //Vacina
                        usuarioJsonEvento.put("Aplicada", aplicada);

                        if (!etQtdDoses.getText().toString().trim().equals(""))
                            usuarioJsonEvento.put("QtdDoses",etQtdDoses.getText().toString().trim());
                        else
                            usuarioJsonEvento.put("QtdDoses","0");

                        //Se ambas datas preenchidas, compara datas
                        if (!etDataAplicacao.getText().toString().trim().equals("") &&
                                !etDataValidade.getText().toString().trim().equals(""))
                        {
                            Date aplDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataAplicacao.getText().toString());
                            Date valDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataValidade.getText().toString());

                            //Data de aplicação > validade
                            if (aplDate.after(valDate))
                            {
                                Toast.makeText(getBaseContext(), "Data de Aplicação maior do que a Data de Validade!", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String aplData = formatter.format(aplDate);
                                usuarioJsonEvento.put("DataAplicacao",aplData);
                                String valData = formatter.format(valDate);
                                usuarioJsonEvento.put("DataValidade", valData);

                                //Atualiza vacina na API
                                new RequisicaoAsyncTask().execute("AtualizaVacina",
                                        String.valueOf(vacina.getEvento().getIdEvento()),
                                        usuarioJsonEvento.toString());
                            }
                        }
                        else
                        {
                            //Se data de aplicação preenchida => Preenche dado
                            if (!etDataAplicacao.getText().toString().trim().equals("")) {
                                Date aplDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataAplicacao.getText().toString());
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String aplData = formatter.format(aplDate);
                                usuarioJsonEvento.put("DataAplicacao", aplData);
                            }
                            else
                                usuarioJsonEvento.put("DataAplicacao", "");

                            //Se data de validade preenchida => Preenche dado
                            if (!etDataValidade.getText().toString().trim().equals(""))
                            {
                                Date valDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataValidade.getText().toString());
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String valData = formatter.format(valDate);
                                usuarioJsonEvento.put("DataValidade", valData);
                            }
                            else
                                usuarioJsonEvento.put("DataValidade", "");

                            //Atualiza vacina na API
                            new RequisicaoAsyncTask().execute("AtualizaVacina",
                                    String.valueOf(vacina.getEvento().getIdEvento()),
                                    usuarioJsonEvento.toString());
                        }

                    }catch (Exception ex){
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActAtualizarVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
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
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActAtualizarVacina.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActAtualizarVacina.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActAtualizarVacina.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Recupera os dados do pet selecionado
    public void recuperaDadosVacina(){

        //Carrega foto do pet selecionado.
        ivFotoPet = (ImageView) findViewById(R.id.ivFotoPet);
        Picasso.with(getBaseContext()).load(animal.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoPet);

        //Carrega nome do animal
        tvAnimal = (TextView) findViewById(R.id.tvAnimal);
        tvAnimal.setText(vacina.getEvento().getAnimal().getNome());

        //Carrega nome do pet.
        etNomeVacina = (EditText) findViewById(R.id.etNomeVacina);
        etNomeVacina.setText(vacina.getEvento().getNome());

        //Carrega data de aplicação e validade da vacina
        etDataAplicacao = (EditText) findViewById(R.id.etDataAplicacao);
        etDataValidade = (EditText) findViewById(R.id.etDataValidade);

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            Date aplDate = new SimpleDateFormat("yyyy-MM-dd").parse(vacina.getDataaplicacao().toString());
           String aplData = formatter.format(aplDate);

            Date valDate = new SimpleDateFormat("yyyy-MM-dd").parse(vacina.getDatavalidade().toString());
            String valData = formatter.format(valDate);

            etDataAplicacao.setText(aplData);
            etDataValidade.setText(valData);
        }
        catch (Exception e){}

        //Carrega quantidade de doses da vacina
        etQtdDoses = (EditText) findViewById(R.id.etQtdDoses);
        etQtdDoses.setText(String.valueOf(vacina.getQtddoses()));

        //Carrega observações da vacina
        etEventoObservacoes = (EditText) findViewById(R.id.etEventoObservacoes);
        etEventoObservacoes.setText(vacina.getEvento().getObservacoes());

        //Carrega spinner de vacina aplicada
        String[] aplicadas = new String[]{"Vacina Aplicada?","Sim","Não"};
        spAplicada = (Spinner) findViewById(R.id.spAplicada);
        ArrayAdapter adAplicada = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aplicadas) {
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
        spAplicada.setAdapter(adAplicada);

        int pAplicada = 0;

        //Carrega aplicação da vacina do animal
        for(int i=0;i<aplicadas.length;i++)
        {
            String vacina_apl = "Não";

            if (vacina.getAplicada() == 1)
                vacina_apl = "Sim";

            if(aplicadas[i].equals(vacina_apl)){
                pAplicada = i;
                break;
            }
        }
        spAplicada.setSelection(pAplicada);

        //Adiciona evento de item selecionado no spinner de vacina aplicada
        spAplicada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (position == 1)
                        aplicada = 1;
                    else
                        aplicada = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                Toast.makeText(ActAtualizarVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {

               try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActAtualizarVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json))
                    {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActAtualizarVacina.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Verifica se o usuário foi inserido com sucesso
                        if (metodo == "AtualizaVacina" && msg.getCodigo() == 10) {
                            //Chama tela principal
                            Intent principal = new Intent(ActAtualizarVacina.this, ActPrincipal.class);
                            startActivity(principal);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActAtualizarVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }

}
