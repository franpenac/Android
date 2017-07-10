package controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import modelo.Ftp;

/**
 * Created by Victor on 12-01-2017.
 */

public class VistaFoto extends AppCompatActivity {

    private final String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Layout/";
    private File file = new File(ruta_fotos);
    private Button boton;
    private Button boton2;
    String ip;					//Almacena la direción ip del servidor
    String usuario;				//Almacena el usuario
    String contrasena;			//Almacena la contraseña

    Ftp ftp;					//Instancia manejador ftp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.vista_foto);

        ip = "200.63.97.29";
        usuario = "ZONAGE\\Administrador";
        contrasena ="tek.*.2016";


       //======== codigo nuevo ========
       boton = (Button) findViewById(R.id.btnFoto);
       boton2 = (Button) findViewById(R.id.btnSeria);
       //Si no existe crea la carpeta donde se guardaran las fotos
       file.mkdirs();
       //accion para el boton
       boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                 String file = ruta_fotos + getCode() + ".jpg";

                 File mi_foto = new File( file );
                 try {
                      mi_foto.createNewFile();
                  } catch (IOException ex) {
                   Log.e("ERROR ", "Error:" + ex);
                  }
                  //
                  Uri uri = Uri.fromFile( mi_foto );
                  //Abre la camara para tomar la foto
                  Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                  //Guarda imagen
                  cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                  //Retorna a la actividad
                  startActivityForResult(cameraIntent, 0);

            }

      });

      boton2.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v)
          {

              //Establece un servidor
              ftp = new Ftp(ip, usuario, contrasena, getApplicationContext());

              //Realiza login en el servidor
              try {
                  ftp.Login(usuario, contrasena);
              } catch (SocketException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }

              //Sube el archivo al servidor
              try {
                  ftp.SubirArchivo(getCode() + ".jpg");
              } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }


          }});
       //====== codigo nuevo:end ======
    }

    @SuppressLint("SimpleDateFormat")
     private String getCode()
     {
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
          String date = dateFormat.format(new Date() );
          String photoCode = "pic_" + date;
          return photoCode;
     }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





}
