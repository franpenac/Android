package modelo;

import java.util.ArrayList;

/**
 * Created by Victor on 20-06-2017.
 */

public class posiciones {

    private double latitud;
    private double longitud;

    public posiciones(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public posiciones() {
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public ArrayList<posiciones> listadoP()
    {
        ArrayList<posiciones> pos = new ArrayList<posiciones>();

        //pos.add(new posiciones(0.0,0.0));


        return pos;
    }


}
