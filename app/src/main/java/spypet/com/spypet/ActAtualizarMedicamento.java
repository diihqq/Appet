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
import modelo.Medicamento;
import modelo.Mensagem;
import modelo.Raca;
import modelo.Usuario;
import modelo.Vacina;

/**
 * Created by Diogo on 01/10/2016.
 */
public class ActAtualizarMedicamento extends AppCompatActivity {
    private EditText etNomeMedicamento;
    private EditText etInicio;
    private EditText etFim;
    private TextView tvAnimal;
    private EditText etHorasDeEspera;
    private EditText etEventoObservacoes;
    private ImageView ivFotoPet;
    private Button btInscrever;
    private String nome;
    private String email;
    private ProgressDialog pd;
    private Especie especie_t = new Especie(0,"");
    private int processos = 0;
    private Medicamento medicamento;
    private Animal animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualiza_medicamento);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Recupera os parâmetros passados na chamada dessa tela
        //Recupera os parâmetros passados na chamada dessa tela
        try{
            Intent i = getIntent();
            nome = i.getStringExtra("Nome");
            email = i.getStringExtra("Email");

            JSONObject json = new JSONObject(i.getStringExtra("Medicamento"));
            medicamento = Medicamento.jsonToAnimal(json);

            JSONObject json2 = new JSONObject(i.getStringExtra("Animal"));
            animal = Animal.jsonToAnimal(json2);

            medicamento.getEvento().setAnimal(animal);

        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
        }

        //Recupera objetos da tela

        etNomeMedicamento = (EditText)findViewById(R.id.etNomeMedicamento);
        etInicio = (EditText)findViewById(R.id.etInicio);
        etFim = (EditText)findViewById(R.id.etFim);
        tvAnimal = (TextView)findViewById(R.id.tvAnimal);
        etHorasDeEspera = (EditText)findViewById(R.id.etHorasDeEspera);
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
                    etInicio.setText(current);
                    etInicio.setSelection(sel < current.length() ? sel : current.length());
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
                    etFim.setText(current);
                    etFim.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etInicio.addTextChangedListener(tw);
        etFim.addTextChangedListener(tw2);

        recuperaDadosMedicamento();

        btInscrever = (Button)findViewById(R.id.btCadastrar);

        //Cadastra evento e medicamento
        btInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if(etNomeMedicamento.getText().toString().trim().equals("") || etInicio.getText().toString().trim().equals("") ||
                        etFim.getText().toString().trim().equals("")){
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                }else{
                    try
                    {
                        Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(etInicio.getText().toString());
                        Date fimDate = new SimpleDateFormat("dd/MM/yyyy").parse(etFim.getText().toString());

                        //Data de inicio > fim
                        if (initDate.after(fimDate))
                        {
                            Toast.makeText(getBaseContext(), "Data de Início maior do que a Data de Fim!", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            //Gera objeto para ser autenticado pela API.
                            JSONObject usuarioJsonEvento = new JSONObject();

                            //Evento
                            usuarioJsonEvento.put("Nome", etNomeMedicamento.getText().toString().trim());

                            if (!etEventoObservacoes.getText().toString().trim().equals(""))
                                usuarioJsonEvento.put("Observacoes", etEventoObservacoes.getText().toString().trim());
                            else
                                usuarioJsonEvento.put("Observacoes", "");

                            //Medicamento

                            //Converte data pra yyyy-MM-dd
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String dataInicio = formatter.format(initDate);
                            usuarioJsonEvento.put("Inicio", dataInicio);

                            //Converte data pra yyyy-MM-dd
                            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                            String dataFim = formatter2.format(fimDate);
                            usuarioJsonEvento.put("Fim", dataFim);

                            if (!etHorasDeEspera.getText().toString().trim().equals(""))
                                usuarioJsonEvento.put("HorasDeEspera", etHorasDeEspera.getText().toString().trim());
                            else
                                usuarioJsonEvento.put("HorasDeEspera", "");

                            //Insere usuário na API
                            new RequisicaoAsyncTask().execute("AtualizaMedicamento",
                                    String.valueOf(medicamento.getEvento().getIdEvento()),
                                    usuarioJsonEvento.toString());
                        }
                    }
                    catch (Exception ex){
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActAtualizarMedicamento.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
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
                Intent intent1 = new Intent(ActAtualizarMedicamento.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActAtualizarMedicamento.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActAtualizarMedicamento.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Recupera os dados do medicamento
    public void recuperaDadosMedicamento(){

        //Carrega foto do pet selecionado.
        ivFotoPet = (ImageView) findViewById(R.id.ivFotoPet);
        Picasso.with(getBaseContext()).load(animal.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoPet);

        //Carrega nome do animal
        tvAnimal = (TextView) findViewById(R.id.tvAnimal);
        tvAnimal.setText(medicamento.getEvento().getAnimal().getNome());

        //Carrega nome do medicamento
        etNomeMedicamento = (EditText) findViewById(R.id.etNomeMedicamento);
        etNomeMedicamento.setText(medicamento.getEvento().getNome());

        //Carrega data de inicio e fim do medicamento
        etInicio = (EditText) findViewById(R.id.etInicio);
        etFim = (EditText) findViewById(R.id.etFim);

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            Date iniDate = new SimpleDateFormat("yyyy-MM-dd").parse(medicamento.getInicio().toString());
            String iniData = formatter.format(iniDate);

            Date fimDate = new SimpleDateFormat("yyyy-MM-dd").parse(medicamento.getFim().toString());
            String fimData = formatter.format(fimDate);

            etInicio.setText(iniData);
            etFim.setText(fimData);
        }
        catch (Exception e){}

        //Carrega horas de espera
        etHorasDeEspera = (EditText) findViewById(R.id.etHorasDeEspera);
        etHorasDeEspera.setText(String.valueOf(medicamento.getHorasdeespera()));

        //Carrega observações do medicamento
        etEventoObservacoes = (EditText) findViewById(R.id.etEventoObservacoes);
        etEventoObservacoes.setText(medicamento.getEvento().getObservacoes());

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
                Toast.makeText(ActAtualizarMedicamento.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActAtualizarMedicamento.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json))
                    {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActAtualizarMedicamento.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Verifica se o usuário foi inserido com sucesso
                        if (metodo == "AtualizaMedicamento" && msg.getCodigo() == 10) {
                            //Chama tela principal
                            Intent principal = new Intent(ActAtualizarMedicamento.this, ActPrincipal.class);
                            startActivity(principal);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActAtualizarMedicamento.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }

        }
    }

}
