package controlador;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import modelo.AppIndicadores;
import modelo.AppMovil;
import modelo.BannerFactura;
import modelo.Lista_adaptador;


public class VistaMoviles extends AppCompatActivity {

    private int idUsuario;
    private int idCliente;
    private int bucle;

    private boolean FondoBlanco;
    private String caracterBuscado;
    private String ordenSeleccionado;
    private String filtroSeleccionado;
    private ArrayList<AppMovil> datos;
    private ArrayList<AppMovil> datosFiltrado;
    private List<AppMovil> listadoMoviles;
    private List<Integer> listadoFiltroIndice;
    private AppIndicadores indicadores;
    private int indicadorSeleccionado;

    private ListView list;
    private Toolbar toolbar;
    private Timer timer;

    private static final String SOAP_ACTION = "http://tempuri.org/AppListarMoviles";
    private static final String SOAP_ACTION_INDICADORES = "http://tempuri.org/AppIndicadoresClientes";
    private static final String SOAP_ACTION_INDICADORES_FILTRO = "http://tempuri.org/AppIndicadoresMoviles";
    private static final String METHOD_NAME = "AppListarMoviles";
    private static final String METHOD_NAME_INDICADORES = "AppIndicadoresClientes";
    private static final String METHOD_NAME_INDICADORES_FILTRO = "AppIndicadoresMoviles";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://www.zonage.cl/AppSms/appservice.asmx";//?WSDL

    private Button btnFilterReporte;
    private Button btnFilterTemperatura;
    private Button btnFilterIgnicion;
    private Button btnFilterVelocidad;
    private Button btnFilterPatente;
    private Button btnFilterMovil;

    private HorizontalScrollView scrollView;
    private EditText etBuscarPatente;
    private Spinner spin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_moviles);

        indicadorSeleccionado = 0;
        ordenSeleccionado = "asc";
        filtroSeleccionado = "Patente";
        caracterBuscado = "";
        bucle = 1;
        FondoBlanco = true;

        final BannerFactura banner = (BannerFactura) getApplicationContext();
        banner.FacturaVencida(VistaMoviles.this);

        listadoFiltroIndice = new ArrayList<Integer>();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        spin = (Spinner) findViewById(R.id.spnMoviles);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo2);
        toolbar.setTitleTextColor(Color.parseColor("#00000000"));
        toolbar.setBackgroundColor(Color.rgb(61,63,62));

        if(!isConected())
        {
            Toast.makeText(this,"No hay conexiÃ³n a internet",Toast.LENGTH_SHORT).show();
            Intent nuevoForm = new Intent(VistaMoviles.this, MainActivity.class);
            timer.cancel();
            timer.purge();
            bucle = 0;
            startActivity(nuevoForm);

            finish();
        }
        scrollView = (HorizontalScrollView) findViewById(R.id.sbVistaMoviles);
        btnFilterReporte = (Button)findViewById(R.id.btnFilterReport);
        btnFilterTemperatura = (Button)findViewById(R.id.btnFilterTemperatura);
        btnFilterIgnicion = (Button)findViewById(R.id.btnFilterIgnicion);
        btnFilterVelocidad = (Button)findViewById(R.id.btnFilterVelocidad);
        btnFilterPatente = (Button)findViewById(R.id.btnFilterPatente);
        btnFilterMovil = (Button)findViewById(R.id.btnFilterMovil);

        etBuscarPatente = (EditText)findViewById(R.id.etBuscarPatente);

        etBuscarPatente.clearFocus();
        etBuscarPatente.setInputType(InputType.TYPE_NULL);

        scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        timer = new Timer();

        VistaMoviles.Automatizador auto = new VistaMoviles.Automatizador();
        timer.scheduleAtFixedRate(auto,0,30000);

        VistaMoviles.Automatizador2 auto2 = new VistaMoviles.Automatizador2();
        timer.scheduleAtFixedRate(auto2,0,30000);

        new VistaMoviles.CargarLista().execute();

        list = (ListView)findViewById(R.id.lvMain);

        Eventos();
    }

    public void MetodosEntrada(Object entrada, View view)
    {
        TextView gPatente = (TextView) view.findViewById(R.id.tvGridPatente);
        TextView gMovil = (TextView) view.findViewById(R.id.tvGridMovil);
        TextView gVelocidad = (TextView) view.findViewById(R.id.tvGridVelocidad);
        TextView gTemperatura = (TextView) view.findViewById(R.id.tvGridTemperatura);
        TextView gReporte = (TextView) view.findViewById(R.id.tvGridReporte);
        TextView gIgnicion = (TextView) view.findViewById(R.id.tvGridIgnicion);
        ImageView bateria = (ImageView) view.findViewById(R.id.imgBateria);
        ImageView antena = (ImageView) view.findViewById(R.id.imgAntena);

        String alarmaMovil = ((AppMovil) entrada).get_TituloAlarma();
        int gps = ((AppMovil) entrada).get_GPS();

        if(alarmaMovil.equals("10") || alarmaMovil.equals("09") || alarmaMovil.equals("91"))
        {
            if(alarmaMovil.equals("10")){bateria.setBackgroundResource(R.drawable.bateria_baja);}

            if(alarmaMovil.equals("09")){bateria.setBackgroundResource(R.drawable.sin_bateria);}

            if(alarmaMovil.equals("91")){bateria.setBackgroundResource(R.drawable.dormido);}

            AnimationDrawable frame = (AnimationDrawable)bateria.getBackground();
            frame.start();
        }else
        {
            bateria.setBackgroundResource(R.color.transparent_color);
        }

        if(gps==1)
        {
            antena.setBackgroundResource(R.drawable.con_antena);

            AnimationDrawable frame2 = (AnimationDrawable)antena.getBackground();
            frame2.start();

        }else
        {
            antena.setBackgroundResource(R.color.transparent_color);
        }



        int negro = Color.parseColor("#000000");
        int blanco = Color.parseColor("#FFFFFF");

        FrameLayout glReporte = (FrameLayout) view.findViewById(R.id.fmlReporte);
        FrameLayout glPatente = (FrameLayout) view.findViewById(R.id.fmlPatente);
        FrameLayout glIgnicion = (FrameLayout) view.findViewById(R.id.fmlIgnicion);
        FrameLayout glTemperatura = (FrameLayout) view.findViewById(R.id.fmlTemperatura);
        FrameLayout glVelocidad = (FrameLayout) view.findViewById(R.id.fmlVelocidad);
        FrameLayout glMovil = (FrameLayout) view.findViewById(R.id.fmlMovil);

        int diferencia = ((AppMovil) entrada).get_TituloDiferenciaMinutos();

        if(FondoBlanco==true)
        {
            view.setBackgroundColor(Color.rgb(255,255,255));
            glReporte.setBackgroundColor(Color.rgb(255,255,255));
            glPatente.setBackgroundColor(Color.rgb(255,255,255));
            glIgnicion.setBackgroundColor(Color.rgb(255,255,255));
            glTemperatura.setBackgroundColor(Color.rgb(255,255,255));
            glVelocidad.setBackgroundColor(Color.rgb(255,255,255));
            glMovil.setBackgroundColor(Color.rgb(255,255,255));
            FondoBlanco = false;
        }else
        {
            view.setBackgroundColor(Color.rgb(229,230,232));
            glReporte.setBackgroundColor(Color.rgb(229,230,232));
            glPatente.setBackgroundColor(Color.rgb(229,230,232));
            glIgnicion.setBackgroundColor(Color.rgb(229,230,232));
            glTemperatura.setBackgroundColor(Color.rgb(229,230,232));
            glVelocidad.setBackgroundColor(Color.rgb(229,230,232));
            glMovil.setBackgroundColor(Color.rgb(229,230,232));
            FondoBlanco = true;
        }

        CargarTextos(gVelocidad, gMovil, gTemperatura, gReporte, gPatente, ((AppMovil) entrada));

        CargarColoresTextos(gVelocidad, gMovil, gTemperatura, gReporte, gPatente);

        CargarTemperatura(((AppMovil) entrada).get_TituloModelo(), ((AppMovil) entrada).get_TituloTemperatura(), gTemperatura);

        ColorIgnicion(((AppMovil) entrada).get_TituloIgnicion(), gIgnicion);

        ColorReporte(diferencia, glReporte);
    }

    public void CargarEntrada()
    {
        list.setAdapter(new Lista_adaptador(VistaMoviles.this, R.layout.view_movil, datosFiltrado){
            @Override
            public void onEntrada(Object entrada, View view) {

                try {
                    MetodosEntrada(entrada, view);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }});
    }

    public void CargarTextos(TextView gVelocidad, TextView gMovil, TextView gTemperatura, TextView gReporte, TextView gPatente, AppMovil appMovil)
    {
        int negro = Color.parseColor("#000000");
        int blanco = Color.parseColor("#FFFFFF");

        gPatente.setText(appMovil.get_TituloPatente());
        gMovil.setText(appMovil.get_TituloMovil());
        gVelocidad.setText(appMovil.get_TituloVelocidad());
        gReporte.setText((appMovil).get_TituloReporte());

    }

    public void CargarColoresTextos(TextView gVelocidad, TextView gMovil, TextView gTemperatura, TextView gReporte, TextView gPatente)
    {
        int negro = Color.parseColor("#000000");
        int blanco = Color.parseColor("#FFFFFF");

        gVelocidad.setTextColor(negro);
        gMovil.setTextColor(negro);
        gTemperatura.setTextColor(negro);
        gReporte.setTextColor(negro);
        gPatente.setTextColor(negro);


    }

    public void CargarTemperatura(String modelo, String temperatura, TextView gTemperatura)
    {
        if(modelo.equals("AVL09")) {
            gTemperatura.setText(temperatura);
        }else
        {
            gTemperatura.setText("-");
        }
    }

    public void ColorIgnicion(String ignicion, TextView gIgnicion)
    {
        int rojo = Color.parseColor("#FF0000");
        int verde = Color.parseColor("#3DBB5E");

        if (ignicion.equals("1")) {
            gIgnicion.setText("ON");
            gIgnicion.setTextColor(verde);
        } else {
            gIgnicion.setText("OFF");
            gIgnicion.setTextColor(rojo);
        }
    }

    public void ColorReporte(int diferencia, FrameLayout glReporte)
    {
        int rojo = Color.parseColor("#FF0000");
        int amarillo = Color.parseColor("#DFC723");
        int verde = Color.parseColor("#3DBB5E");

        if (diferencia <= 5) {

            glReporte.setBackgroundColor(verde);
        }
        if (diferencia > 5 && diferencia <= 1440) {
            glReporte.setBackgroundColor(amarillo);
        }
        if (diferencia > 1440) {
            glReporte.setBackgroundColor(rojo);
        }
    }

    public class CargarLista extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            idUsuario = getIntent().getExtras().getInt("idUsuario");
            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("idUsuario",idUsuario);
            request.addProperty("filtro",filtroSeleccionado);
            request.addProperty("orden",ordenSeleccionado);
            request.addProperty("buscado","");
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try
            {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION, envelope);

                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

                response = result.toString();

            }
            catch (IOException e)
            {
                response = "1";//Error de time out
            }
            catch (XmlPullParserException e)
            {
                response = "2";
            }
            return response;

        }

        public void onPostExecute(String result)
        {
            String json = result.toString();

            JSONObject object = null;

            if(result=="1")
            {
                Toast toast2 = Toast.makeText(VistaMoviles.this,"Agotado tiempo de espera de servidor", Toast.LENGTH_SHORT);
                toast2.show();

                Intent nuevoForm = new Intent(VistaMoviles.this, MainActivity.class);
                timer.cancel();
                timer.purge();
                bucle = 0;
                startActivity(nuevoForm);
                finish();

            }else {
                try {
                    object = new JSONObject(json);

                    List<AppMovil> moviles = new ArrayList<>();

                    JSONArray json_array = object.optJSONArray("listadoMoviles");

                    for (int i = 0; i < json_array.length(); i++) {
                        moviles.add(new AppMovil(json_array.getJSONObject(i)));

                    }


                    listadoMoviles = moviles;
                    list = (ListView) findViewById(R.id.lvMain);

                    datos = new ArrayList<AppMovil>();
                    for (AppMovil movil : moviles) {
                        AppMovil entrada = new AppMovil();
                        entrada.TituloPatente = movil.get_TituloPatente();
                        entrada.TituloMovil = movil.get_TituloMovil();
                        entrada.TituloVelocidad = movil.get_TituloVelocidad();
                        entrada.TituloTemperatura = movil.get_TituloTemperatura();
                        entrada.TituloReporte = movil.get_TituloReporte();
                        entrada.TituloIgnicion = movil.get_TituloIgnicion();
                        entrada.TituloDiferenciaMinutos = movil.get_TituloDiferenciaMinutos();
                        entrada.MovilId = movil.get_MovilId();
                        entrada.TituloModelo = movil.get_TituloModelo();
                        entrada.TituloAlarma = movil.get_TituloAlarma();
                        entrada.GPS = movil.get_GPS();

                        datos.add(entrada);
                    }

                    SeleccionarFiltro();

                    scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    scrollView.pageScroll(View.FOCUS_LEFT);
                    scrollView.setVerticalScrollbarPosition(View.FOCUS_LEFT);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public class FiltroIndicadores extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {

            String response = "";

            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_INDICADORES_FILTRO);
            request.addProperty("clienteId",idCliente);
            request.addProperty("accion",indicadorSeleccionado);
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try
            {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION_INDICADORES_FILTRO, envelope);

                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

                response = result.toString();

            }
            catch (IOException e)
            {
                response = "1";//Error de time out
            }
            catch (XmlPullParserException e)
            {
                response = "2";
            }
            return response;

        }

        public void onPostExecute(String result)
        {
            String json = result.toString();

            JSONObject object = null;

            if(result=="1")
            {
                Toast toast2 = Toast.makeText(VistaMoviles.this,"Agotado tiempo de espera de servidor", Toast.LENGTH_SHORT);
                toast2.show();
            }else {
                try {

                    listadoFiltroIndice = new ArrayList<Integer>();

                    object = new JSONObject(json);

                    JSONArray json_array = object.optJSONArray("PK_Movil");

                    for (int i = 0; i < json_array.length(); i++) {

                        listadoFiltroIndice.add(json_array.getInt(i));
                    }
                    CargarFiltro();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public boolean isConected()
    {
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent nuevoForm = new Intent(VistaMoviles.this, MainActivity.class);
            timer.cancel();
            timer.purge();
            bucle = 0;
            startActivity(nuevoForm);

            finish();
            //Toast.makeText(this,"Prueba", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_contacto) {
            Intent nuevoForm = new Intent(VistaMoviles.this, VistaContacto.class);
            startActivity(nuevoForm);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class Automatizador extends TimerTask
    {
        @Override
        public void run()
        {
            if(bucle == 1){
            new VistaMoviles.CargarLista().execute();}
        }
    }

    class Automatizador2 extends TimerTask
    {
        @Override
        public void run()
        {
            if(bucle == 1){
                new VistaMoviles.BuscarIndicadores().execute();
            }

        }
    }

    public void Eventos()
    {
        etBuscarPatente.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                caracterBuscado = etBuscarPatente.getText().toString();

                SeleccionarFiltro();
            }
        });

        btnFilterMovil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtroSeleccionado=="Movil"){if(ordenSeleccionado=="asc"){ordenSeleccionado = "desc";}else{ordenSeleccionado="asc";}}
                filtroSeleccionado = "Movil";
                new VistaMoviles.CargarLista().execute();
            }
        });

        btnFilterReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtroSeleccionado=="Reporte"){if(ordenSeleccionado=="asc"){ordenSeleccionado = "desc";}else{ordenSeleccionado="asc";}}
                filtroSeleccionado = "Reporte";
                new VistaMoviles.CargarLista().execute();
            }
        });

        btnFilterTemperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtroSeleccionado=="Temperatura"){if(ordenSeleccionado=="asc"){ordenSeleccionado = "desc";}else{ordenSeleccionado="asc";}}
                filtroSeleccionado = "Temperatura";
                new VistaMoviles.CargarLista().execute();
            }
        });

        btnFilterIgnicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtroSeleccionado=="Ignicion"){if(ordenSeleccionado=="asc"){ordenSeleccionado = "desc";}else{ordenSeleccionado="asc";}}
                filtroSeleccionado = "Ignicion";
                new VistaMoviles.CargarLista().execute();
            }
        });

        btnFilterVelocidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtroSeleccionado=="Velocidad"){if(ordenSeleccionado=="asc"){ordenSeleccionado = "desc";}else{ordenSeleccionado="asc";}}
                filtroSeleccionado = "Velocidad";
                new VistaMoviles.CargarLista().execute();
            }
        });

        btnFilterPatente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtroSeleccionado=="Patente"){if(ordenSeleccionado=="asc"){ordenSeleccionado = "desc";}else{ordenSeleccionado="asc";}}
                filtroSeleccionado = "Patente";
                new VistaMoviles.CargarLista().execute();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                // TODO Auto-generated method stub
                AppMovil elegido = (AppMovil) list.getItemAtPosition(position);

                int idSeleccionado = elegido.get_MovilId();
                AppMovil movilSelecionado = null;

                for (AppMovil movil : listadoMoviles) {
                    int idBuscado = movil.get_MovilId();
                    if (idSeleccionado == idBuscado) {
                        movilSelecionado = movil;
                    }
                }

                Intent nuevoForm = new Intent(VistaMoviles.this, VistaInformacion.class);
                nuevoForm.putExtra("Movil", movilSelecionado.get_MovilId());
                nuevoForm.putExtra("Patente", movilSelecionado.get_TituloPatente());
                nuevoForm.putExtra("Nombre", movilSelecionado.get_TituloMovil());
                nuevoForm.putExtra("idCliente", idCliente);
                nuevoForm.putExtra("idUsuario", idUsuario);
                bucle = 0;
                startActivity(nuevoForm);

            }

        });

        etBuscarPatente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etBuscarPatente.setText("");
                etBuscarPatente.setInputType(InputType.TYPE_CLASS_TEXT);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etBuscarPatente, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                indicadorSeleccionado =position;
                new VistaMoviles.FiltroIndicadores().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast toast2 = Toast.makeText(VistaMoviles.this,"evento no se", Toast.LENGTH_SHORT);
                toast2.show();
            }

        });

    }

    public class BuscarIndicadores extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            idCliente = getIntent().getExtras().getInt("idCliente");
            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_INDICADORES);
            request.addProperty("clienteId",idCliente);
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try
            {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION_INDICADORES, envelope);

                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

                response = result.toString();

            }
            catch (IOException e)
            {
                response = "1";//Error de time out
            }
            catch (XmlPullParserException e)
            {
                response = "2";

            }
            return response;

        }

        public void onPostExecute(String result){
            String json = result.toString();

            JSONObject object = null;

            if(result=="1")
            {
                Toast toast2 = Toast.makeText(VistaMoviles.this,"Agotado tiempo de espera de servidor", Toast.LENGTH_SHORT);
                toast2.show();

                Intent nuevoForm = new Intent(VistaMoviles.this, MainActivity.class);
                timer.cancel();
                timer.purge();
                bucle = 0;
                startActivity(nuevoForm);

                finish();

            }else {
                try {
                    object = new JSONObject(json);

                    indicadores = new AppIndicadores(object);

                    CargarIndicadores();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void CargarIndicadores()
    {
        try{

            String[] names = { "Total: "+Integer.toString(indicadores.getTotal())
                            , "Online: "+Integer.toString(indicadores.getOnline())
                            , "Off-line: "+Integer.toString(indicadores.getOffline())
                            , "Retraso: "+Integer.toString(indicadores.getRetraso())
                            , "Dormido: "+Integer.toString(indicadores.getDormido())
                            , "Dormido Ex.2: "+Integer.toString(indicadores.getDormido2())
                            , "Auto-Apago: "+Integer.toString(indicadores.getAutoApagado())};

            ArrayAdapter adapters = new ArrayAdapter<String>(this,
                    R.layout.item_spinner, names);

            spin.setAdapter(adapters);

            spin.setSelection(indicadorSeleccionado);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(VistaMoviles.this,"",Toast.LENGTH_SHORT).show();
        }

    }

    public void onBackPressed (){

        if(isConected()){
            finish();
            bucle = 0;
            Intent nuevoForm = new Intent(VistaMoviles.this, MainActivity.class);
            timer.cancel();
            timer.purge();
            bucle = 0;
            startActivity(nuevoForm);
        }else
        {

            Toast.makeText(this,"No hay conexiÃ³n a internet",Toast.LENGTH_SHORT).show();
            Intent nuevoForm = new Intent(VistaMoviles.this, MainActivity.class);
            bucle =0;
            startActivity(nuevoForm);

        }
    }

    public void SeleccionarFiltro()
    {
        if(caracterBuscado.equals(""))
        {
            datosFiltrado = new ArrayList<AppMovil>();
            datosFiltrado = datos;
            CargarEntrada();

        }else
        {
            CargarFiltro();
            CargarEntrada();
        }
    }

    public void CargarFiltro()
    {
        datosFiltrado = new ArrayList<AppMovil>();

        if(indicadorSeleccionado != 0)
        {
            for (int pk:listadoFiltroIndice) {

                for (AppMovil app :datos) {

                    if(app.get_MovilId() == pk){
                    if(app.get_TituloPatente().contains(caracterBuscado.toUpperCase()) || app.get_TituloMovil().contains(caracterBuscado.toUpperCase()) || app.get_TituloPatente().contains(caracterBuscado) || app.get_TituloMovil().contains(caracterBuscado))
                    {
                        datosFiltrado.add(app);
                    }}
                }
            }
        }else
        {
            for (AppMovil app :datos) {

                if(app.get_TituloPatente().contains(caracterBuscado.toUpperCase()) || app.get_TituloMovil().contains(caracterBuscado.toUpperCase()) || app.get_TituloPatente().contains(caracterBuscado) || app.get_TituloMovil().contains(caracterBuscado))
                {
                    datosFiltrado.add(app);
                }
            }
        }




        CargarEntrada();
    }

}

