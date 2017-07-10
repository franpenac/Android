package modelo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;



public class View_Temp {

    private double temperatura;
    private String fecha ;

    public View_Temp(JSONObject objetoJSON) throws JSONException {

        try {
            this.fecha = objetoJSON.getString("Fecha");
            this.temperatura = objetoJSON.getDouble("Temp");
            int v = 0;
        }

        catch(JSONException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public View_Temp(double temperatura, String fecha) {
        this.temperatura = temperatura;
        this.fecha = fecha;
    }

    public View_Temp() {

    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
