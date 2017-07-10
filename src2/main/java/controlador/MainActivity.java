package controlador;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.util.List;
import java.util.Locale;

import modelo.BannerFactura;
import modelo.posiciones;

public class MainActivity extends AppCompatActivity {

    int idUsuario;
    Boolean RespuestaWC;
    String usuario;
    String contrasena;
    String cliente;
    int estado;
    Boolean loop;

    private static final int SOLICITUD_PERMISO_LECTURA = 1;
    private static final int SOLICITUD_PERMISO_SMS = 1;

    private Toolbar toolbar;
    private ProgressBar progressBarLogin;
    private ImageButton login;

    public ArrayList<String> strings = new ArrayList<String>();
    private static final String SOAP_ACTION = "http://tempuri.org/Login";
    private static final String METHOD_NAME = "Login";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://www.zonage.cl/AppSms/appservice.asmx";//?WSDL



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        progressBarLogin = (ProgressBar)findViewById(R.id.pbLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo5);
        getSupportActionBar().setTitle("  "+getSupportActionBar().getTitle());
        toolbar.setTitleTextColor(Color.parseColor("#00000000"));
        toolbar.setBackgroundColor(Color.rgb(255,255,255));

        //setLocation();
        //crearPreferencias();
        leerPreferencias();

        login = (ImageButton)findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                try {
                   if(validarPermisos()) {


                        if (ValidarCampos() == false) {
                            Toast toast = Toast.makeText(MainActivity.this, "Error de credenciales", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {

                            if (!isConected()) {
                                Toast.makeText(MainActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                            } else {

                                login.setClickable(false);
                                progressBarLogin.setVisibility(View.VISIBLE);
                                new MyTask().execute();

                            }
                        }
                    }else
                    {
                        Toast.makeText ( MainActivity.this,"Debe autorizar permisos para el funcionamiento de la aplicación", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText ( MainActivity.this,"Fallo de json", Toast.LENGTH_SHORT).show();
                }

            }
        });



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            explicarUsoPermisoSMS();
            solicitarPermisoSMS();
        }

    }

    public boolean ValidarCampos() {
        String nombre = ((EditText) findViewById(R.id.etNombreUsuario)).getText().toString();
        String contrasenaWc = ((EditText) findViewById(R.id.etContrasena)).getText().toString();
        String clienteWc = ((EditText) findViewById(R.id.etCliente)).getText().toString();

        if (nombre == "") {
            return false;
        }

        if (contrasenaWc.equals("")) {
            return false;
        }

        if (clienteWc.equals("")) {
            return false;
        }

        usuario = nombre;
        contrasena = contrasenaWc;
        cliente = clienteWc;

        return true;
    }

    public class MyTask extends AsyncTask<Void, Void, String> {

        String response = "";

        public void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {


            final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("usuario",usuario);
            request.addProperty("contrasena",contrasena);
            request.addProperty("cliente",cliente);/*getImei(MainActivity.this)*/

            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try
            {

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                androidHttpTransport.call(SOAP_ACTION, envelope);

                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                String json = result.toString();
                JSONObject object = new JSONObject(json);
                idUsuario = object.getInt("idUsuario");
                RespuestaWC = object.getBoolean("loginValido");
                estado = object.getInt("estado");

                response = result.toString();

            }
            catch (IOException e)
            {
                response = "1";

            }
            catch (XmlPullParserException e)
            {
                response = "2";
            } catch (JSONException e) {
                e.printStackTrace();

            }


            return response;
        }
        @Override
        public void onPostExecute(String res) {

            try{

                final BannerFactura banner = (BannerFactura) getApplicationContext();


                if(res=="1")
                {
                    Toast toast2 = Toast.makeText(MainActivity.this,"Agotado tiempo de espera", Toast.LENGTH_SHORT);
                    toast2.show();
                    login.setClickable(true);
                    progressBarLogin.setVisibility(View.INVISIBLE);
                }else{
                if(idUsuario > 0)
                {
                    int valor;
                    String json = res.toString();
                    JSONObject object = null;

                    try {

                        object = new JSONObject(json);
                        valor = object.getInt("idUsuario");
                        int idCliente = object.getInt("idCliente");

                        EditText nombre = (EditText) findViewById(R.id.etNombreUsuario);
                        EditText cliente = (EditText) findViewById(R.id.etCliente);
                        EditText contra = (EditText) findViewById(R.id.etContrasena);
                        CheckBox checkBox = (CheckBox) findViewById(R.id.chkGuardar);

                        String guard = "N";
                        if(checkBox.isChecked()){guard = "Y";}

                        guardarPreferencias(nombre.getText().toString(),cliente.getText().toString(),contra.getText().toString(),guard);

                        login.setClickable(true);
                        progressBarLogin.setVisibility(View.INVISIBLE);

                        //GuardarCredenciales();
                        Intent nuevoForm = new Intent(MainActivity.this, VistaMoviles.class);
                        nuevoForm.putExtra("idUsuario",valor);
                        nuevoForm.putExtra("idCliente",idCliente);

                        banner.setPK(estado);
                        startActivity(nuevoForm);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        login.setClickable(true);
                        progressBarLogin.setVisibility(View.INVISIBLE);
                    }
                }else
                {
                    if(idUsuario == -1){
                        Toast toast2 = Toast.makeText(MainActivity.this,"Telefono no registrado", Toast.LENGTH_SHORT);
                        toast2.show();
                        login.setClickable(true);
                        progressBarLogin.setVisibility(View.INVISIBLE);
                    }
                    else{
                        Toast toast2 = Toast.makeText(MainActivity.this,"Credenciales invalidas", Toast.LENGTH_SHORT);
                        toast2.show();
                        login.setClickable(true);
                        progressBarLogin.setVisibility(View.INVISIBLE);
                        }
                    }
                }

            }catch(Exception ex)
            {
                Toast toast2 = Toast.makeText(MainActivity.this,"Error de HttpTransport", Toast.LENGTH_SHORT);
                toast2.show();
                login.setClickable(true);
                progressBarLogin.setVisibility(View.INVISIBLE);
            }


        }
    }

    public void onBackPressed (){
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    //Permisos para android//

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SOLICITUD_PERMISO_LECTURA) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

                Toast.makeText(this, "Debe darle permisos a la aplicación", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void solicitarPermisoLectura() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                SOLICITUD_PERMISO_LECTURA);
    }

    private void explicarUsoPermisoLectura() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            alertDialogBasico();
        }
    }

    private void solicitarPermisoSMS() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                SOLICITUD_PERMISO_SMS);
    }

    private void explicarUsoPermisoSMS() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            alertDialogBasico();
        }
    }

    public boolean validarPermisos()
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            explicarUsoPermisoLectura();
            solicitarPermisoLectura();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            explicarUsoPermisoSMS();
            solicitarPermisoSMS();
        }

        return true;
    }

    public void alertDialogBasico() {


            // 1. Instancia de AlertDialog.Builder con este constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Encadenar varios métodos setter para ajustar las características del diálogo
            builder.setMessage(R.string.dialog_message);


            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });


            builder.show();

        }

    public void guardarPreferencias(String nombre, String cliente, String contrasena, String guardado)
    {
        SharedPreferences prefs = getSharedPreferences("CredencialesUsuarios",0);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nombre",nombre);
            editor.putString("cliente",cliente);

            if(guardado.equals("Y")){
            editor.putString("contrasena",contrasena);
            editor.putString("guardado",guardado);
            }else{
                editor.putString("contrasena","");
                editor.putString("guardado","");
            }
            editor.commit();


    }

    public void leerPreferencias()
    {
        SharedPreferences prefs = getSharedPreferences("CredencialesUsuarios",0);
        EditText nombre = (EditText)findViewById(R.id.etNombreUsuario);
        nombre.setText(prefs.getString("nombre",""));

        EditText cliente = (EditText)findViewById(R.id.etCliente);
        cliente.setText(prefs.getString("cliente",""));

        EditText contrase = (EditText)findViewById(R.id.etContrasena);
        contrase.setText(prefs.getString("contrasena",""));

        CheckBox guardado = (CheckBox)findViewById(R.id.chkGuardar);
        String guard = prefs.getString("guardado","");
        if(guard.equals("Y"))
        {
            guardado.setChecked(true);
        }else{
            guardado.setChecked(false);
        }
    }
/**
     public boolean onCreateOptionsMenu(Menu menu) {
     getMenuInflater().inflate(R.menu.menu_login, menu);
     return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
     int id = item.getItemId();
     if (id == R.id.foto_menu) {
     Intent nuevoForm = new Intent(MainActivity.this, VistaFoto.class);

     startActivity(nuevoForm);
     //Toast.makeText(this,"Prueba", Toast.LENGTH_SHORT).show();
     return true;
     }
     return super.onOptionsItemSelected(item);
     }**/


    // SOLO PARA CASOS DE BUSQUEDA DE DIRECCIONES, vista informacion, setLocation, usar el modelo posiciones


    public String setLocation() {

        ArrayList<posiciones> pos =   new posiciones().listadoP();
        String texto = "";
        for (posiciones p: pos) {
            if (p.getLatitud() != 0.0 && p.getLongitud() != 0.0 ) {
                try {

                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation( p.getLatitud(), p.getLongitud(), 1);

                    if (!list.isEmpty()) {
                        Address DirCalle = list.get(0);
                        texto = texto+ DirCalle.getAddressLine(0).toString()+"\n";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    texto = texto + "\n";
                }
            }
        }

        return texto;
    }
}







