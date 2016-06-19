package spypet.com.spypet;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Felipe on 05/06/2016.
 */
public class ActPrincipal extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Configura tabs
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

        descritor = abas.newTabSpec("Compromissos");
        descritor.setContent(R.id.llCompromissos);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_engrenagem, getTheme()));

        abas.addTab(descritor);

        //Monta lista de animais perdidos
        List<String> lista = new ArrayList<String>();
        lista.add("teste");
        lista.add("teste2");
        ArrayAdapter<String> a = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        a.addAll(lista);
        ListView lvPetsPerdidos = (ListView)findViewById(R.id.lvPetsPerdidos);
        lvPetsPerdidos.setAdapter(a);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.main_activity_bar, menu);
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

}
