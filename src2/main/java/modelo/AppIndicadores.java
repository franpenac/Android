package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 09-01-2017.
 */

public class AppIndicadores {

    private int total;
    private int online;
    private int retraso;
    private int offline;
    private int dormido;
    private int dormido2;
    private int autoApagado;

    public AppIndicadores(int total, int online, int retraso, int offline, int dormido, int dormido2, int autoApagado){
        this.total = total;
        this.online = online;
        this.retraso = retraso;
        this.offline = offline;
        this.dormido = dormido;
        this.dormido2 = dormido2;
        this.autoApagado = autoApagado;
    }

    public AppIndicadores(JSONObject objetoJSON) throws JSONException {

        this.total = objetoJSON.getInt("total");
        this.retraso = objetoJSON.getInt("retraso");
        this.online = objetoJSON.getInt("online");
        this.offline = objetoJSON.getInt("offline");
        this.dormido = objetoJSON.getInt("dormido");
        this.dormido2 = objetoJSON.getInt("dormido2");
        this.autoApagado = objetoJSON.getInt("autoApagado");
    }

    public AppIndicadores() {

    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getRetraso() {
        return retraso;
    }

    public void setRetraso(int retraso) {
        this.retraso = retraso;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public int getDormido() {
        return dormido;
    }

    public void setDormido(int dormido) {
        this.dormido = dormido;
    }

    public int getDormido2() {
        return dormido2;
    }

    public void setDormido2(int dormido2) {
        this.dormido2 = dormido2;
    }

    public int getAutoApagado() {
        return autoApagado;
    }

    public void setAutoApagado(int autoApagado) {
        this.autoApagado = autoApagado;
    }
}
