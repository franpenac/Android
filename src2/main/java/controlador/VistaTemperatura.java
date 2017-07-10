package controlador;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import modelo.Lista_adaptador;
import modelo.Temperatura;
import modelo.View_Info;
import modelo.View_Temp;

/**
 * Created by Victor on 16-01-2017.
 */

public class VistaTemperatura extends AppCompatActivity {


    Toolbar toolbar;
    private ArrayList<View_Temp> datos;
    private ListView list;
    private boolean FondoBlanco;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_temperatura);

        FondoBlanco = true;
        CargarToolbar();

        list = (ListView)findViewById(R.id.lvTemp);

        if(!isConected())
        {
            Toast.makeText(this,"No hay conexión a internet",Toast.LENGTH_SHORT).show();
            Intent nuevoForm = new Intent(VistaTemperatura.this, MainActivity.class);
            startActivity(nuevoForm);
        }

        String json = getIntent().getExtras().getString("json");
        JSONObject object = null;

        try {
            object = new JSONObject(json);

            datos = new ArrayList<View_Temp>();
            JSONArray json_array = object.optJSONArray("temps");

            for (int i = 0; i < json_array.length(); i++) {

                View_Temp  tem=  new View_Temp(json_array.getJSONObject(i));
                datos.add(tem);

            }

            CargarEntrada();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void CargarToolbar(){

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo2);
        toolbar.setTitleTextColor(Color.parseColor("#00000000"));
        toolbar.setBackgroundColor(Color.rgb(61,63,62));
        getSupportActionBar().setTitle("  "+getSupportActionBar().getTitle());
    }

    public boolean isConected() {
        if(!isNetDisponible()){return false;}
        if(!isOnlineNet()){return false;}

        return true;
    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public Boolean isOnlineNet() {

        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public void CargarEntrada(){
        list.setAdapter(new Lista_adaptador(VistaTemperatura.this, R.layout.view_temperatura, datos){
            @Override
            public void onEntrada(Object entrada, View view) {

                if(FondoBlanco==true)
                {
                    view.setBackgroundColor(Color.rgb(255,255,255));
                    FondoBlanco = false;
                }else
                {
                    view.setBackgroundColor(Color.rgb(229,230,232));
                    FondoBlanco = true;
                }

                TextView fecha = (TextView) view.findViewById(R.id.tvViewHora);
                fecha.setText(((View_Temp) entrada).getFecha());

                TextView temperatura = (TextView) view.findViewById(R.id.tvViewTemp);
                temperatura.setText(Double.toString(((View_Temp) entrada).getTemperatura())+"°C");


            }});
    }
}

