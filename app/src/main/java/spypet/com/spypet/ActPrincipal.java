package spypet.com.spypet;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import controlador.GerenciadorSharedPreferences;

/**
 * Created by Felipe on 05/06/2016.
 */
public class ActPrincipal extends AppCompatActivity {

    public int tabSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Toast.makeText(getBaseContext(),GerenciadorSharedPreferences.getEmail(getBaseContext()),Toast.LENGTH_LONG).show();

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Adiciona as opções nas tabs
        configuraTabs();

        //Monta lista de animais perdidos
        listaPetsPerdidos();

        //Monta lista de compromissos
        listaCompromissos();

        //Monta lista de pets
        listaPets();
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
                GerenciadorSharedPreferences.setEmail(getBaseContext(),"");

                //Chama tela de login
                Intent principal = new Intent(ActPrincipal.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void listaPets(){
        List<String> lsConfiguracoes = new ArrayList<String>();
        lsConfiguracoes.add("http://blog.emania.com.br/content/uploads/2016/01/cachorro-curiosidades.jpg");
        lsConfiguracoes.add("https://www.dogsshop.com.br/sitewp2015/wp-content/uploads/2015/11/cachorro.jpg");
        lsConfiguracoes.add("http://wallpaper.ultradownloads.com.br/45586_Papel-de-Parede-Filhote-de-Cachorro_1024x768.jpg");

        ArrayAdapter<String> adpConfiguracoes = new ArrayAdapter<String>(this,R.layout.item_compromissos){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_configuracoes, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                ImageView ivFotoAnimal = (ImageView) convertView.findViewById(R.id.ivFotoAnimal);
                TextView tvNomeAnimal = (TextView) convertView.findViewById(R.id.tvNomeAnimal);

                Picasso.with(getContext()).load(getItem(position)).into(ivFotoAnimal);
                tvNomeAnimal.setText("Pet");

                return convertView;
            }
        };

        adpConfiguracoes.addAll(lsConfiguracoes);
        ListView lvConfiguracoes = (ListView)findViewById(R.id.lvConfiguracoes);
        lvConfiguracoes.setAdapter(adpConfiguracoes);

        //Evento click do botão flutuante de adicionar pets
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.fbAddPet);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Adicionar pet", Toast.LENGTH_LONG).show();
            }
        });
    }

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

        ArrayAdapter<String> adpCompromissos = new ArrayAdapter<String>(this,R.layout.item_compromissos){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_compromissos, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                ImageView ivFotoAnimal = (ImageView) convertView.findViewById(R.id.ivFotoAnimal);
                TextView tvNomeCompromisso = (TextView) convertView.findViewById(R.id.tvNomeCompromisso);
                TextView tvInformacao = (TextView) convertView.findViewById(R.id.tvInformacao);

                Picasso.with(getContext()).load("http://www.farejadordecaes.com.br/wp-content/uploads/o-que-saber-antes-de-comprar-cachorro-01.png").into(ivFotoAnimal);
                tvNomeCompromisso.setText("Vacina");
                tvInformacao.setText("Dia 20/12/2018");

                return convertView;
            }
        };

        adpCompromissos.addAll(lsCompromissos);
        ListView lvCompromissos = (ListView)findViewById(R.id.lvCompromissos);
        lvCompromissos.setAdapter(adpCompromissos);

        //Evento click do botão flutuante de adicionar compromissos
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.fbAddCompromisso);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Adicionar compromisso", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Monta a lista de animais perdidos
    public void listaPetsPerdidos(){
        // método chamado para cada item do lvPetsPerdidos
        ArrayAdapter<String> adpPetsPerdidos = new ArrayAdapter<String>(this, R.layout.item_animais_perdidos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_animais_perdidos, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                ImageView ivAnimal1 = (ImageView) convertView.findViewById(R.id.ivAnimal1);
                ImageView ivAnimal2 = (ImageView) convertView.findViewById(R.id.ivAnimal2);

                Picasso.with(getContext()).load("https://static.tudointeressante.com.br/uploads/2015/10/cachorro_atencao_dest.jpg").into(ivAnimal1);
                Picasso.with(getContext()).load("http://cdn2.tudosobrecachorros.com.br/wp-content/uploads/cachorro-selfie-4.jpg").into(ivAnimal2);

                return convertView;
            }
        };

        try {
            adpPetsPerdidos.add("");
            adpPetsPerdidos.add("");
            adpPetsPerdidos.add("");
            adpPetsPerdidos.add("");
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
        }

        ListView lvPetsPerdidos = (ListView)findViewById(R.id.lvPetsPerdidos);
        lvPetsPerdidos.setAdapter(adpPetsPerdidos);
    }

    public void configuraTabs(){
        //Adiciona as opções nas tabs da tela principal
        TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);

        abas.setup();

        TabHost.TabSpec descritor = abas.newTabSpec("Principal");
        descritor.setContent(R.id.llPrincipal);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_globo, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Compromissos");
        descritor.setContent(R.id.llCompromissos);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_calendario, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Configuracoes");
        descritor.setContent(R.id.llConfiguracoes);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_engrenagem, getTheme()));
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

    //Animação das TABs
    //Anima a transição vinda da direita
    public Animation direita()
    {
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
    public Animation esquerda()
    {
        Animation esquerda = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        esquerda.setDuration(240);
        esquerda.setInterpolator(new AccelerateInterpolator());
        return esquerda;
    }

}
