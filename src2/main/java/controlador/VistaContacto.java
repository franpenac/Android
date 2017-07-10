package controlador;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import modelo.BannerFactura;

/**
 * Created by Victor on 09-01-2017.
 */

public class VistaContacto extends AppCompatActivity {

    private String contactoMatias;
    private String contactoCasaMatriz;

    private String direccionCorreo;
    private String numeroLlamada;
    private ImageButton Llamar;
    private ImageButton Correo;

    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_contacto);

        contactoMatias = "965864395";
        contactoCasaMatriz = "222286762";
        final BannerFactura banner = (BannerFactura) getApplicationContext();
        banner.FacturaVencida(VistaContacto.this);
        Calendar cal = new GregorianCalendar();

        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        SimpleDateFormat dfs = new SimpleDateFormat("EEE");

        String horaMarcada = df.format(date);
        String dia = dfs.format(date);

        int hora = Integer.parseInt(horaMarcada);

        if(dia.equals("sab") || dia.equals("Sat") || dia.equals("dom") || dia.equals("Sun") )
        {
            direccionCorreo ="m.davila@zonagps.cl";
            numeroLlamada = contactoMatias;
        }else
        {
            if(hora>10 && hora <19)
            {
                direccionCorreo ="facturacion@zonagps.cl";
                numeroLlamada = contactoCasaMatriz;
            }else
            {
                direccionCorreo ="m.davila@zonagps.cl";
                numeroLlamada = contactoMatias;
            }
        }

        Llamar = (ImageButton) findViewById(R.id.btnLlamar);
        Correo = (ImageButton) findViewById(R.id.btnCorreo);

        CargarToolbar();

        eventosOnClick();

    }

    public void CargarToolbar(){

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo2);
        toolbar.setTitleTextColor(Color.parseColor("#00000000"));
        toolbar.setBackgroundColor(Color.rgb(65,64,66));

    }

    private void enviar(String[] to, String[] cc,
                        String asunto, String mensaje) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        //String[] to = direccionesEmail;
        //String[] cc = copias;
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email "));
    }

    public void eventosOnClick()
    {
        Llamar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {

                    Intent intent = new Intent(Intent.ACTION_CALL);

                    intent.setData(Uri.parse("tel:" + numeroLlamada));

                    if (ActivityCompat.checkSelfPermission(VistaContacto.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(intent);
                    }else
                    {
                        ActivityCompat.requestPermissions(VistaContacto.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);
                    }

                } catch (Exception e) {
                    Toast.makeText ( VistaContacto.this,"Fallo de llamada", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Correo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {

                    String[] to = { direccionCorreo};
                    String[] cc = { "" };
                    enviar(to, cc, "Consulta Zona GPS",
                            "" +
                                    "");

                } catch (Exception e) {
                    Toast.makeText ( VistaContacto.this,"Fallo de envio de correo", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent nuevoForm = new Intent(VistaContacto.this, MainActivity.class);
            startActivity(nuevoForm);
            //Toast.makeText(this,"Prueba", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

