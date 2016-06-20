package spypet.com.spypet;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felipe on 05/06/2016.
 */
public class ActPrincipal extends AppCompatActivity {

    public int tabSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Adiciona as opções nas tabs
        configuraTabs();

        //Monta lista de animais perdidos
        listaPetsPerdidos();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void listaCompromissos(){
        List<String> lista = new ArrayList<String>();
        lista.add("Vacina 1");
        lista.add("Remédio 1");
        lista.add("Vacina 2");
        lista.add("Remédio 2");
        ArrayAdapter<String> a = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        a.addAll(lista);
        ListView lvCompromissos = (ListView)findViewById(R.id.lvCompromissos);
        lvCompromissos.setAdapter(a);
    }

    //Monta a lista de animais perdidos
    public void listaPetsPerdidos(){
        // método chamado para cada item do lvPetsPerdidos
        ArrayAdapter<Bitmap> adpPetsPerdidos = new ArrayAdapter<Bitmap>(this, R.layout.item_animais_perdidos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_animais_perdidos, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                Bitmap bmp = (Bitmap) getItem(position);
                ImageView ivAnimal1 = (ImageView) convertView.findViewById(R.id.ivAnimal1);
                ImageView ivAnimal2 = (ImageView) convertView.findViewById(R.id.ivAnimal2);
                ivAnimal1.setImageBitmap(bmp);
                ivAnimal2.setImageBitmap(bmp);

                return convertView;
            }
        };

        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.exemplo);
            adpPetsPerdidos.add(bmp);
            adpPetsPerdidos.add(bmp);
            adpPetsPerdidos.add(bmp);
            adpPetsPerdidos.add(bmp);
            adpPetsPerdidos.add(bmp);
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
