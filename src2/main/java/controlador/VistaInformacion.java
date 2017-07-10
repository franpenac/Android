package controlador;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import modelo.BannerFactura;
import modelo.Lista_adaptador;
import modelo.View_Info;

public class VistaInformacion extends AppCompatActivity {

    private int numero;
    private int movilID;
    private double latitud;
    private double longitud;
    private int GPRS;
    private String patente;
    private String Nombre;
    private String alarma;
    private String curso;
    private int bucle;
    private ListView list;
    private Boolean tempe;
    private TextView direccion;
    Toolbar toolbar;
    Timer timer;
    private boolean FondoBlanco;

    private ArrayList<View_Info> datos;

    private static final String SOAP_ACTION_INFORMACION = "http://tempuri.org/AppInformacionMovil";
    private static final String METHOD_NAME_INFORMACION = "AppInformacionMovil";
    private static final String SOAP_ACTION_POSICION = "http://tempuri.org/PosicionMovil";
    private static final String METHOD_NAME_POSICION = "PosicionMovil";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://www.zonage.cl/AppSms/appservice.asmx";//?WSDL

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_informacion);

        tempe = false;
        movilID = getIntent().getExtras().getInt("Movil");
        patente = getIntent().getExtras().getString("Patente");
        Nombre = getIntent().getExtras().getString("Nombre");

        direccion = (TextView) findViewById(R.id.etDireccion);

        bucle = 1;
        list = (ListView)findViewById(R.id.lvInfo);

        final BannerFactura banner = (BannerFactura) getApplicationContext();
        banner.FacturaVencida(VistaInformacion.this);

        CargarToolbar();

        if(!isConected())
        {
            Toast.makeText(this,"No hay conexión a internet",Toast.LENGTH_SHORT).show();
            Intent nuevoForm = new Intent(VistaInformacion.this, MainActivity.class);
            bucle =0;
            startActivity(nuevoForm);
        }

        timer = new Timer();

        VistaInformacion.Automatizador auto = new VistaInformacion.Automatizador();
        VistaInformacion.Automatizador2 auto2 = new VistaInformacion.Automatizador2();

        timer.scheduleAtFixedRate(auto,0,30000);
        timer.scheduleAtFixedRate(auto2,0,30000);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                // TODO Auto-generated method stub
                View_Info elegido = (View_Info) list.getItemAtPosition(position);
                if(elegido.get_textoEncima().equals("Acciones"))
                {
                    Intent nuevoForm = new Intent(VistaInformacion.this, VistaAcciones.class);
                    int idCliente = getIntent().getExtras().getInt("idCliente");
                    int idUsuario = getIntent().getExtras().getInt("idUsuario");
                    int idMovil = getIntent().getExtras().getInt("Movil");
                    nuevoForm.putExtra("idUsuario",idUsuario);
                    nuevoForm.putExtra("idCliente",idCliente);
                    nuevoForm.putExtra("idMovil",idMovil);
                    nuevoForm.putExtra("numero",numero);
                    nuevoForm.putExtra("Modelo",tempe);
                    nuevoForm.putExtra("Patente",patente);
                    nuevoForm.putExtra("Nombre",Nombre);
                    nuevoForm.putExtra("Latitud",latitud);
                    nuevoForm.putExtra("Longitud",longitud);
                    startActivity(nuevoForm);

                }
            }

        });


    }

    public void CargarToolbar(){

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo2);
        toolbar.setTitleTextColor(Color.parseColor("#00000000"));
        toolbar.setBackgroundColor(Color.rgb(61,63,62));
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

    public class CargarUbicacion extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";

            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_POSICION);
            request.addProperty("movilId",movilID);
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try
            {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                androidHttpTransport.call(SOAP_ACTION_POSICION, envelope);

                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                String json = result.toString();
                JSONObject object = new JSONObject(json);
                latitud = object.getDouble("latitud");
                longitud = object.getDouble("longitud");
                GPRS = object.getInt("GPRS");
                alarma = object.getString("alarma");
                curso = object.getString("curso");

                response = result.toString();

            }
            catch (IOException e)
            {
                response = "Se ha producido un error";//Error de time out
            }
            catch (XmlPullParserException e)
            {
                response = "2";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;

        }

        public void onPostExecute(String result)
        {
            String json = result.toString();

            JSONObject object = null;

            try {
                if(result=="1")
                {
                    Toast toast2 = Toast.makeText(VistaInformacion.this,"Agotado tiempo de espera", Toast.LENGTH_SHORT);
                    toast2.show();
                    Intent nuevoForm = new Intent(VistaInformacion.this, MainActivity.class);
                    bucle = 0;
                    startActivity(nuevoForm);

                }else{
                    direccion.setText(setLocation());
                    UbicacionMovil ubicacion = new UbicacionMovil();
                    ubicacion.latitud = latitud;
                    ubicacion.longitud = longitud;
                    ubicacion.alarma = alarma;
                    ubicacion.curso = curso;
                    ubicacion.GPRS = GPRS;
                    ubicacion.patente = patente;
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.cfInfo, ubicacion).commit();

                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }


        }
    }

    public class BuscarMovil extends AsyncTask<Void, Void, String> {


        String response = "";

        public void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {

            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_INFORMACION);
            request.addProperty("movilId",movilID);

            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try
            {

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                androidHttpTransport.call(SOAP_ACTION_INFORMACION, envelope);

                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

                response = result.toString();

            }
            catch (IOException e)
            {
                response = "1";
            }
            catch (XmlPullParserException e)
            {
                response = "2";
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return response;
        }
        @Override
        public void onPostExecute(String res) {

            try{

                if(res=="1")
                {
                    Toast toast2 = Toast.makeText(VistaInformacion.this,"Agotado tiempo de espera", Toast.LENGTH_SHORT);
                    toast2.show();
                }else{

                    String json = res.toString();
                    JSONObject object = new JSONObject(json);
                    double temp = object.getInt("temperatura");
                    int km = object.getInt("velocidad");
                    String report =object.getString("reporte");
                    String grup =object.getString("grupo");
                    String mod =object.getString("modelo");

                    int ign = object.getInt("ignicion");
                    int dif = object.getInt("diferencia");
                    numero = object.getInt("numero");

                    String ignicion = "Apagado";
                    if(ign==1){ignicion="Encendido";}

                    String temperatura = "-";

                    View_Info info_kilometro = new View_Info(R.mipmap.odometro,Double.toString(km)+" km/h",0);
                    View_Info info_reporte = new View_Info(R.mipmap.reloj,report,dif);
                    View_Info info_ignicion = new View_Info(R.mipmap.ignicion,ignicion,0);
                    View_Info info_grupo = new View_Info(R.mipmap.usuarios,grup,0);
                    View_Info info_mensaje = new View_Info(R.mipmap.accion,"Acciones",0);

                    datos = new ArrayList<View_Info>();

                    datos.add(info_grupo);
                    datos.add(info_ignicion);
                    datos.add(info_reporte);
                    datos.add(info_kilometro);
                    if(mod.equals("AVL09"))
                    {
                        temperatura = Double.toString(temp)+"°C";
                        View_Info info_temperatura = new View_Info(R.mipmap.temperatura,temperatura,0);
                        datos.add(info_temperatura);
                        tempe = true;
                    }
                    datos.add(info_mensaje);
                    FondoBlanco = true;
                    CargarEntrada();

                }

            }catch(Exception ex)
            {
                Toast toast2 = Toast.makeText(VistaInformacion.this,"Error de HttpTransport", Toast.LENGTH_SHORT);
                toast2.show();
            }


        }
    }

    class Automatizador extends TimerTask {
        @Override
        public void run() {

            if(bucle ==1){
            new VistaInformacion.CargarUbicacion().execute();}
        }
    }

    class Automatizador2 extends TimerTask {
        @Override
        public void run()
        {
            if(bucle == 1){
            new VistaInformacion.BuscarMovil().execute();}
        }
    }

    public void CargarEntrada(){
        list.setAdapter(new Lista_adaptador(VistaInformacion.this, R.layout.view_info, datos){
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

                int rojo = Color.parseColor("#FF0000");
                int amarillo = Color.parseColor("#E0C716");
                int verde = Color.parseColor("#3DBB5E");

                int negro = Color.parseColor("#000000");
                int blanco = Color.parseColor("#FFFFFF");

                TextView texto = (TextView) view.findViewById(R.id.textView_superior);
                texto.setText(((View_Info) entrada).get_textoEncima());

                ImageView imagen = (ImageView) view.findViewById(R.id.imageView_imagen);
                imagen.setImageResource(((View_Info) entrada).get_idImagen());

                if(((View_Info) entrada).get_idImagen()== R.mipmap.reloj)
                {
                    int dif = ((View_Info) entrada).get_diferencia();

                    if (dif <= 5) {

                        texto.setTextColor(verde);
                    }
                    if (dif > 5 && dif <= 1440) {
                        texto.setTextColor(amarillo);
                    }
                    if (dif > 1440) {
                        int dias = dif/1440;
                        texto.setText(((View_Info) entrada).get_textoEncima() + " retraso de "+ dias+" días");
                        texto.setTextColor(rojo);
                    }

                }

                if(((View_Info) entrada).get_idImagen()== R.mipmap.ignicion)
                {
                    if(((View_Info) entrada).get_textoEncima().equals("ON")){texto.setTextColor(verde);}
                    else{texto.setTextColor(rojo);}
                }

            }});
    }

    public void onBackPressed (){
        if(isConected()){

            int idCliente = getIntent().getExtras().getInt("idCliente");
            int idUsuario = getIntent().getExtras().getInt("idUsuario");
            finish();
            bucle = 0;
            Intent nuevoForm = new Intent(VistaInformacion.this, VistaMoviles.class);
            nuevoForm.putExtra("idCliente",idCliente);
            nuevoForm.putExtra("idUsuario",idUsuario);
            timer.cancel();
            timer.purge();
            bucle = 0;
            startActivity(nuevoForm);

        }else
        {
            Toast.makeText(this,"No hay conexión a internet",Toast.LENGTH_SHORT).show();
            Intent nuevoForm = new Intent(VistaInformacion.this, MainActivity.class);
            bucle =0;
            startActivity(nuevoForm);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent nuevoForm = new Intent(VistaInformacion.this, MainActivity.class);
            timer.cancel();
            timer.purge();
            bucle = 0;
            startActivity(nuevoForm);

            finish();
            //Toast.makeText(this,"Prueba", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_contacto) {
            Intent nuevoForm = new Intent(VistaInformacion.this, VistaContacto.class);
            startActivity(nuevoForm);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public String setLocation() {

        if (latitud != 0.0 && longitud != 0.0 && GPRS ==0) {
            try {

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        latitud, longitud, 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    return DirCalle.getAddressLine(0).toString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
