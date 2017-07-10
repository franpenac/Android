package controlador;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import google.Distance;
import google.Duration;
import google.Route;


public class UbicacionMovil extends Fragment implements OnMapReadyCallback {

    public double latitud;
    public String patente;
    public double longitud;
    public int GPRS;
    public String alarma;
    public String curso;
    private int imagen;

    private Route rot;
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyAWtLyrDL048DsX1uCtkA8spsYLNqMqchA";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_ubicacion, container,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng marker = new LatLng(latitud, longitud);
        CargarIconoMapa();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));

        if(GPRS==0)
        {

            Marker marke =googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(imagen)).position(marker).title("Patente").snippet(patente));
            marke.showInfoWindow();

        }else
            {
                Circle circle;
                circle = googleMap.addCircle(new CircleOptions()
                        .center(marker)
                        .radius(100)
                        .strokeWidth(10)
                        .strokeColor(Color.rgb(198,198,198))
                        .fillColor(Color.rgb(198,198,198))
                        .clickable(true));
            }

    }


    public int BuscarIconoMapa(String nombre)
    {
        if(nombre.equals("pos_0")){ return R.mipmap.pos_0; }
        if(nombre.equals("pos_1")){ return R.mipmap.pos_1; }
        if(nombre.equals("pos_2")){ return R.mipmap.pos_2; }
        if(nombre.equals("pos_3")){ return R.mipmap.pos_3; }
        if(nombre.equals("pos_4")){ return R.mipmap.pos_4; }
        if(nombre.equals("pos_5")){ return R.mipmap.pos_5; }
        if(nombre.equals("pos_6")){ return R.mipmap.pos_6; }
        if(nombre.equals("pos_7")){ return R.mipmap.pos_7; }
        if(nombre.equals("pos_10")){ return R.mipmap.pos_10; }
        if(nombre.equals("pos_11")){ return R.mipmap.pos_11; }

        if(nombre.equals("ale_0")){ return R.mipmap.ale_0; }
        if(nombre.equals("ale_1")){ return R.mipmap.ale_1; }
        if(nombre.equals("ale_2")){ return R.mipmap.ale_2; }
        if(nombre.equals("ale_3")){ return R.mipmap.ale_3; }
        if(nombre.equals("ale_4")){ return R.mipmap.ale_4; }
        if(nombre.equals("ale_5")){ return R.mipmap.ale_5; }
        if(nombre.equals("ale_6")){ return R.mipmap.ale_6; }
        if(nombre.equals("ale_7")){ return R.mipmap.ale_7; }
        if(nombre.equals("ale_10")){ return R.mipmap.ale_10; }

        return 0;
    }

    public void CargarIconoMapa()
    {
        String Pos = "pos_";
        String Ale = "ale_";

        if (alarma != "91")
        {
            if (alarma == "AA" || alarma == "FF")
            {

                imagen = BuscarIconoMapa(Pos + curso);
            }
            else
            {
                imagen = BuscarIconoMapa(Ale + curso);
            }
        }
        else
        {
            imagen = R.mipmap.pos_11;

        }

    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
