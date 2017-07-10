package controlador;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import modelo.BannerFactura;

public class VistaAcciones extends AppCompatActivity {

    private int idCliente;
    private int idMovil;
    private int idUsuario;
    private String patente;
    private String nombreMovil;
    private int numero;
    private Boolean temper;
    private double latitud;
    private double longitud;

    private ImageButton reiniciar;
    private ImageButton grafico;
    private Button ruta;
    private ImageButton gestion;

    private Toolbar toolbar;
    private ProgressBar progressBarMensajes;

    private static final String SOAP_ACTION = "http://tempuri.org/GuardarSms";
    private static final String METHOD_NAME= "GuardarSms";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://www.zonage.cl/AppSms/appservice.asmx";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_acciones);

        reiniciar = (ImageButton) findViewById(R.id.btnAccionReiniciar);
        grafico = (ImageButton) findViewById(R.id.btnGrafico);
        ruta = (Button) findViewById(R.id.btnRuta);
        gestion = (ImageButton) findViewById(R.id.btnGestion);
        latitud = getIntent().getExtras().getDouble("Latitud");
        longitud = getIntent().getExtras().getDouble("Longitud");
        temper = getIntent().getExtras().getBoolean("Modelo");
        patente = getIntent().getExtras().getString("Patente");
        nombreMovil = getIntent().getExtras().getString("Nombre");
        numero = getIntent().getExtras().getInt("numero");
        idCliente = getIntent().getExtras().getInt("idCliente");
        idUsuario = getIntent().getExtras().getInt("idUsuario");
        idMovil = getIntent().getExtras().getInt("idMovil");

        if(!temper)
        {
            grafico.setVisibility(View.INVISIBLE);
        }
        progressBarMensajes = (ProgressBar)findViewById(R.id.pbAcciones);

        CargarToolbar();

        eventosOnClick();
        final BannerFactura banner = (BannerFactura) getApplicationContext();
        banner.FacturaVencida(VistaAcciones.this);

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

    public void eventosOnClick(){

        reiniciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                ConfirmarReinicioGPS();
            }
        });

        grafico.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                reiniciar.setClickable(false);
                grafico.setClickable(false);
                ruta.setClickable(false);
                gestion.setClickable(false);
                progressBarMensajes.setVisibility(View.VISIBLE);
                new Temperaturas().execute();
            }
        });

        ruta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent nuevoForm = new Intent(VistaAcciones.this, VistaRutaUbicacion.class);
                nuevoForm.putExtra("Latitud",latitud);
                nuevoForm.putExtra("Longitud",longitud);
                startActivity(nuevoForm);

            }
        });

        gestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent nuevoForm = new Intent(VistaAcciones.this, VistaMensajes.class);

                nuevoForm.putExtra("idUsuario",idUsuario);
                nuevoForm.putExtra("idCliente",idCliente);
                nuevoForm.putExtra("idMovil",idMovil);
                nuevoForm.putExtra("numero",numero);
                nuevoForm.putExtra("Patente",patente);
                nuevoForm.putExtra("Nombre",nombreMovil);

                startActivity(nuevoForm);

            }
        });
    }


    public void ConfirmarReinicioGPS() {

        AlertDialog.Builder builder = new AlertDialog.Builder(VistaAcciones.this);
        builder.setTitle("Zona GPS")
                .setMessage("Este boton ejecuta la acción de refrescar la información del equipo GPS, cuando este presenta \"Posible anomalia\". ¿Esta seguro  de enviarlo al movil "+nombreMovil+", patente "+patente+"?")
                .setCancelable(false)

                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })

                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {
                                reiniciar.setClickable(false);
                                grafico.setClickable(false);
                                ruta.setClickable(false);
                                gestion.setClickable(false);
                                progressBarMensajes.setVisibility(View.VISIBLE);
                                new Reiniciar().execute();

                            }
                        }
                );

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Reiniciar(){
        PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);

        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS enviado", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "No se pudo enviar SMS", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "Servicio no diponible", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "PDU (Protocol Data Unit) es NULL", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getApplicationContext(), "Failed because radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(Integer.toString(numero), null, "*000000,991#" , sentIntent, null);


        //Envio de sms de reiniciar
    }

    public class Reiniciar extends AsyncTask<Void, Void, String> {

        String response = "";

        public void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {

            Reiniciar();

            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            int idCliente = getIntent().getExtras().getInt("idCliente");
            int idUsuario = getIntent().getExtras().getInt("idUsuario");
            int idMovil = getIntent().getExtras().getInt("idMovil");
            request.addProperty("cliente",idCliente);
            request.addProperty("usuario",idUsuario);
            request.addProperty("movil",idMovil);
            request.addProperty("accion",2);
            request.addProperty("comentario","");
            request.addProperty("envio",false);
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return response;
        }
        @Override
        public void onPostExecute(String res) {

            reiniciar.setClickable(true);
            progressBarMensajes.setVisibility(View.INVISIBLE);
        }
    }

    public class Temperaturas extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";

            final SoapObject request = new SoapObject(NAMESPACE, "AppTemperatura");
            request.addProperty("movilId",idMovil);//106
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try
            {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call("http://tempuri.org/AppTemperatura", envelope);

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

        public void onPostExecute(String result) {
            String json = result.toString();
            JSONObject object = null;
            try {
                object = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray json_array = object.optJSONArray("temps");
            if(json_array.length()>0){
            Intent nuevoForm = new Intent(VistaAcciones.this, VistaTemperatura.class);
            nuevoForm.putExtra("json", json);
            nuevoForm.putExtra("patente",patente);
            reiniciar.setClickable(true);
            grafico.setClickable(true);
            ruta.setClickable(true);
            gestion.setClickable(true);
            progressBarMensajes.setVisibility(View.INVISIBLE);
            startActivity(nuevoForm);}else
            {
                reiniciar.setClickable(true);
                grafico.setClickable(true);
                ruta.setClickable(true);
                gestion.setClickable(true);
                progressBarMensajes.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "No ha presentado registros de las ultimas 8 horas", Toast.LENGTH_SHORT).show();}
        }



    }


}
