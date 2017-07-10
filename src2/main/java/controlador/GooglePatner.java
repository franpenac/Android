package controlador;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Victor on 24-05-2017.
 */

public class GooglePatner extends AppCompatActivity {

    private boolean pasar;
    Timer timer;
    //private ProgressBar pbGoogle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_google);

        //pbGoogle = (ProgressBar) findViewById(R.id.progressBar2);
        //pbGoogle.setVisibility(View.VISIBLE);
        timer = new Timer();
        pasar = false;

        Automatizador auto = new Automatizador();

        timer.scheduleAtFixedRate(auto,0,3000);

    }

    class Automatizador extends TimerTask {
        @Override
        public void run() {
            if(pasar==true){
                timer.cancel();
                //pbGoogle.setVisibility(View.INVISIBLE);
                Intent nuevoForm = new Intent(GooglePatner.this, MainActivity.class);
                startActivity(nuevoForm);
                finish();
            }else
            {
                pasar = true;
            }

        }
    }
}
