package modelo;

/**
 * Created by Victor on 04-01-2017.
 */

public class View_Info {

    public int idImagen;
    public int diferencia;
    public String textoEncima;

    public View_Info(int idImagen, String textoEncima,int diferencia) {
        this.idImagen = idImagen;
        this.textoEncima = textoEncima;
        this.diferencia = diferencia;
    }

    public View_Info() {

    }

    public String get_textoEncima() {
        return textoEncima;
    }

    public int get_diferencia() {
        return diferencia;
    }

    public int get_idImagen() {
        return idImagen;
    }
}
