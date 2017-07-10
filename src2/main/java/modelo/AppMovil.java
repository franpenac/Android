package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 29-12-2016.
 */

public class AppMovil {


    public int MovilId;
    public String TituloPatente;
    public String TituloMovil;
    public String TituloVelocidad;
    public String TituloTemperatura;
    public String TituloReporte;
    public String TituloIgnicion;
    public int TituloDiferenciaMinutos;
    public String TituloModelo;
    public String TituloAlarma;
    public int GPS;

    public AppMovil(int id,String patente, String velocidad, String temperatura, String reporte, String ignicion, int diferencia, String modelo, String movil, String alarma, int gps) {
        this.MovilId = id;
        this.TituloPatente = patente;
        this.TituloMovil = movil;
        this.TituloVelocidad = velocidad;
        this.TituloTemperatura = temperatura;
        this.TituloReporte = reporte;
        this.TituloIgnicion = ignicion;
        this.TituloDiferenciaMinutos = diferencia;
        this.TituloModelo = modelo;
        this.TituloAlarma = alarma;
        this.GPS = gps;
    }

    public AppMovil(JSONObject objetoJSON) throws JSONException {

        this.MovilId = objetoJSON.getInt("MovilId");
        this.TituloPatente = objetoJSON.getString("TituloPatente");
        this.TituloMovil = objetoJSON.getString("TituloNombre");
        this.TituloVelocidad = objetoJSON.getString("TituloVelocidad");
        this.TituloTemperatura = objetoJSON.getString("TituloTemperatura");
        this.TituloReporte = objetoJSON.getString("TituloReporte");
        this.TituloIgnicion = objetoJSON.getString("TituloIgnicion");
        this.TituloDiferenciaMinutos = objetoJSON.getInt("TituloDiferenciaMinutos");
        this.TituloModelo = objetoJSON.getString("TituloModelo");
        this.TituloAlarma = objetoJSON.getString("TituloAlarma");
        this.GPS = objetoJSON.getInt("GPS");

    }

    public AppMovil() {

    }

    public int get_MovilId() {
        return MovilId;
    }

    public String get_TituloPatente() {
        return TituloPatente;
    }

    public String get_TituloVelocidad() {
        return TituloVelocidad;
    }

    public String get_TituloTemperatura() {
        return TituloTemperatura;
    }

    public String get_TituloModelo() {
        return TituloModelo;
    }

    public String get_TituloReporte() {
        return TituloReporte;
    }

    public String get_TituloIgnicion() {
        return TituloIgnicion;
    }

    public String get_TituloMovil() {
        return TituloMovil;
    }

    public int get_TituloDiferenciaMinutos() {
        return TituloDiferenciaMinutos;
    }

    public String get_TituloAlarma() {
        return TituloAlarma;
    }

    public int get_GPS() {
        return GPS;
    }




}
