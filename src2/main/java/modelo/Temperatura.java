package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 11-01-2017.
 */

public class Temperatura {

    private double tempe;

    public Temperatura(double tempe) {
        this.tempe = tempe;
    }

    public Temperatura(JSONObject objetoJSON) throws JSONException {
        this.tempe = objetoJSON.getDouble("Temp");
    }

    public double getTempe() {
        return tempe;
    }

    public void setTempe(double tempe) {
        this.tempe = tempe;
    }
}
