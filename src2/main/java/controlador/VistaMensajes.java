package controlador;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import modelo.BannerFactura;

/**
 * Created by Victor on 05-01-2017.
 */

public class VistaMensajes extends AppCompatActivity {

    private int numero;
    private String Nombre;
    private String Patente;

    private Toolbar toolbar;
    private ProgressBar progressBarMensajes;
    private ImageButton reanudar;
    private ImageButton inmovilizar;


    private static final String SOAP_ACTION = "http://tempuri.org/GuardarSms";
    private static final String METHOD_NAME= "GuardarSms";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://www.zonage.cl/AppSms/appservice.asmx";//?WSDL

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_mensajes);

        Patente = getIntent().getExtras().getString("Patente");
        Nombre = getIntent().getExtras().getString("Nombre");

        CargarToolbar();

        numero = getIntent().getExtras().getInt("numero");

        progressBarMensajes = (ProgressBar)findViewById(R.id.pbMensajes);

        if(!isConected())
        {Toast.makeText(this,"No hay conexión a internet",Toast.LENGTH_SHORT).show();
            Intent nuevoForm = new Intent(VistaMensajes.this, MainActivity.class);
            startActivity(nuevoForm);
        }

        inmovilizar = (ImageButton)findViewById(R.id.btnInmovilizar);
        inmovilizar.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view){
                ConfirmarInmovilizacion();
            }
        });

        reanudar = (ImageButton)findViewById(R.id.btnReanudar);
        reanudar.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                ConfirmarReanudacion();
            }
        });

        final BannerFactura banner = (BannerFactura) getApplicationContext();
        banner.FacturaVencida(VistaMensajes.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent nuevoForm = new Intent(VistaMensajes.this, MainActivity.class);
            startActivity(nuevoForm);
            //Toast.makeText(this,"Prueba", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_contacto) {
            Intent nuevoForm = new Intent(VistaMensajes.this, VistaContacto.class);
            startActivity(nuevoForm);

            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void ConfirmarInmovilizacion() {

        AlertDialog.Builder builder = new AlertDialog.Builder(VistaMensajes.this);
        builder.setTitle("Zona GPS")
                .setMessage("Estimado usuario, al seleccionar la opción “inmovilizar”, el vehículo se detendrá de manera automática, lo cual puede ocasionar riesgo de accidente. ¿Esta seguro que desea inmovilizar el movil "+Nombre+", patente "+Patente+"?")
                .setCancelable(false)

                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {
                                reanudar.setClickable(false);
                                inmovilizar.setClickable(false);
                                progressBarMensajes.setVisibility(View.VISIBLE);
                                new Inmovilizar().execute();
                            }
                        }
                );

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Inmovilizar() {
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

        sms.sendTextMessage(Integer.toString(numero), null, "*000000,025,A,1#" , sentIntent, null);

        //Envio de sms de inmovilizar
    }

    public void ConfirmarReanudacion() {

        AlertDialog.Builder builder = new AlertDialog.Builder(VistaMensajes.this);
        builder.setTitle("Zona GPS")
                .setMessage("Al seleccionar la opción “Reanudar encendido” activara la corriente del vehículo, esta acción puede tomar unos minutos. ¿Esta seguro que desea reanudar el movil "+Nombre+", patente "+Patente+"?")
                .setCancelable(false)

                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {
                                reanudar.setClickable(false);
                                inmovilizar.setClickable(false);
                                progressBarMensajes.setVisibility(View.VISIBLE);
                                new Reanudar().execute();

                            }
                        }
                );

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Reanudar(){
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

        sms.sendTextMessage(Integer.toString(numero), null, "*000000,025,A,0#" , sentIntent, null);

        //Envio de sms de reanudar
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

    public class Reanudar extends AsyncTask<Void, Void, String> {

        String response = "";

        public void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {

            Reanudar();

            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            int idCliente = getIntent().getExtras().getInt("idCliente");
            int idUsuario = getIntent().getExtras().getInt("idUsuario");
            int idMovil = getIntent().getExtras().getInt("idMovil");
            request.addProperty("cliente",idCliente);
            request.addProperty("usuario",idUsuario);
            request.addProperty("movil",idMovil);
            request.addProperty("accion",1);
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

            reanudar.setClickable(true);
            inmovilizar.setClickable(true);
            progressBarMensajes.setVisibility(View.INVISIBLE);
        }
    }

    public class Inmovilizar extends AsyncTask<Void, Void, String> {

        String response = "";

        public void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {

            Inmovilizar();

            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            int idCliente = getIntent().getExtras().getInt("idCliente");
            int idUsuario = getIntent().getExtras().getInt("idUsuario");
            int idMovil = getIntent().getExtras().getInt("idMovil");
            request.addProperty("cliente",idCliente);
            request.addProperty("usuario",idUsuario);
            request.addProperty("movil",idMovil);
            request.addProperty("accion",0);
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

            reanudar.setClickable(true);
            inmovilizar.setClickable(true);
            progressBarMensajes.setVisibility(View.INVISIBLE);
        }
    }
}
