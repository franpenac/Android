package controlador;


import android.graphics.Color;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import modelo.Temperatura;



public class VistaGrafico extends AppCompatActivity {

    private double temperaturaMax;
    private double temperaturaMin;
    private double contador;

    private XYPlot grafico;
    private ArrayList<Double> Vector;
    private List<Temperatura> temperaturas;
    private Double DatoX,DatoY;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_grafico);

        DatoX = 0.0;
        DatoY = 0.0;
        grafico = (XYPlot)findViewById(R.id.xyGrafico);
        Vector = new ArrayList<Double>();

        configuracionGrafico();
        cargarListaTemperatura();
        cargarVector();
        temperaturaMinMax();
        cargarHora();

    }

    public void temperaturaMinMax()
    {
        temperaturaMax =0.0;
        temperaturaMin =1000.0;
        for (Temperatura temp: temperaturas) {

            if (temperaturaMax < temp.getTempe())
            {
                temperaturaMax = temp.getTempe();
            }

            if (temperaturaMin > temp.getTempe())
            {
                temperaturaMin = temp.getTempe();
            }
        }

        grafico.setRangeBoundaries(temperaturaMin-1, temperaturaMax+1, BoundaryMode.FIXED);
        grafico.setDomainBoundaries(0 ,8, BoundaryMode.FIXED);
    }

    public void configuracionGrafico()
    {
        grafico.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);//X
        grafico.setRangeStep(XYStepMode.INCREMENT_BY_VAL,1);//Y

        grafico.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        grafico.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
        grafico.getGraphWidget().getGridBackgroundPaint().setColor(Color.rgb(115,106,106));
        grafico.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLACK);
        grafico.getBackgroundPaint().setColor(Color.parseColor("#5B9BD5"));
        grafico.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        grafico.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
    }

    public void cargarHora()
    {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        SimpleDateFormat dfs = new SimpleDateFormat("mm");

        String minutos = dfs.format(date);
        if(minutos.length()==1){minutos = "0"+minutos;}

        String horaActual = df.format(date);
        int horaAct = Integer.parseInt(horaActual);
        int horaAtras = horaAct-8;

        if(horaActual.length()==1){horaActual = "0"+horaActual;}
        if(horaAtras<0){horaAtras = 24+horaAtras;}
        String horaAtrasada = Integer.toString(horaAtras);
        if(horaAtrasada.length()==1){horaAtrasada = "0"+horaAtrasada;}

        grafico.setDomainLabel("Desde las "+horaAtrasada+":"+minutos+" hasta las "+horaActual+":"+minutos);
        grafico.setRangeLabel("Temperaturas");
    }

    public void cargarListaTemperatura()
    {
        String json = getIntent().getExtras().getString("json");
        JSONObject object = null;

        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        temperaturas = new ArrayList<>();

        JSONArray json_array = object.optJSONArray("temps");

        contador = 0;

        for (int i = 0; i < json_array.length(); i++) {
            try {
                temperaturas.add(new Temperatura(json_array.getJSONObject(i)));
                contador++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarVector()
    {
        double avanze = 8/contador;

        DatoX=DatoX-avanze;

        for (Temperatura t: temperaturas) {
            DatoX = DatoX+avanze;
            Vector.add(DatoX);
            DatoY  = t.getTempe();
            Vector.add(DatoY);
        }

        String patente = getIntent().getExtras().getString("patente");
        XYSeries series = new SimpleXYSeries(Vector,SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,patente);
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.rgb(220,22,22),0xCC0000,0xCC0000,null);

        grafico.clear();
        grafico.addSeries(series,seriesFormat);

    }
}
