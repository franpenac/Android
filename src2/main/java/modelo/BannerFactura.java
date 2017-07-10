package modelo;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;

/**
 * Created by Victor on 01-06-2017.
 */

public class BannerFactura extends Application {

    private int PK;

    public BannerFactura()
    {

    }

    public int getPK() {
        return PK;
    }

    public void setPK(int PK) {
        this.PK = PK;
    }

    public void FacturaVencida(Context ctx)
    {
        final BannerFactura banner = (BannerFactura) getApplicationContext();

        if(banner.getPK()==100)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Zona GPS")
                    .setMessage("Estimado cliente, usted tiene facturas vencidas, favor regularize el pago ")
                    .setCancelable(false)
                    .setNegativeButton("CERRAR",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            //Toast.makeText(getApplicationContext(), "PRUEBA"+banner.getPK(), Toast.LENGTH_SHORT).show();
        }

        if(banner.getPK()==102)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Zona GPS")
                    .setMessage(Html.fromHtml("Estimado cliente: Usted tiene 2 o más facturas vencidas,"+"<font color='#FF0000'> SE LE CORTARA EL SERVICIO DENTRO DE LAS SIGUIENTES 48 HORAS,</font>"+" favor regularize el pago"))
                    .setCancelable(false)
                    .setNegativeButton("CERRAR",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            //Toast.makeText(getApplicationContext(), "PRUEBA"+banner.getPK(), Toast.LENGTH_SHORT).show();
        }
    }
}
