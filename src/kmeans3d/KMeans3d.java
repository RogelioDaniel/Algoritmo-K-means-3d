/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans3d;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.ArrayUtils;
import org.jzy3d.analysis.*;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 *
 * @author Rogelio
 */

public class KMeans3d extends AbstractAnalysis {

    /**
     * @param args the command line arguments
     */ 
    private int noPuntos, noClases;
    private Scatter scatter = new Scatter();
    Coord3d[] puntos3d;
    Coord3d[] clases3d;
    Color[] colors;
    Color[] colorsAtractors;

    public int getnoPuntos() {
        return noPuntos;
    }

    public void setnoPuntos(int noPuntos) {
        this.noPuntos = noPuntos;
    }

    public int getnoClases() {
        return noClases;
    }

    public void setnoClases(int noClases) {
        this.noClases = noClases;
    }

    public Scatter getScatter() {
        return scatter;
    }

    public void setScatter(Scatter scatter) {
        this.scatter = scatter;
    }

    public Coord3d[] getPoints() {
        return puntos3d;
    }

    public void setPoints(Coord3d[] puntos3d) {
        this.puntos3d = puntos3d;
    }

    public Coord3d[] getAtractors() {
        return clases3d;
    }

    public void setAtractors(Coord3d[] clases3d) {
        this.clases3d = clases3d;
    }
    

    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public KMeans3d(int n, int atrac){
        this.noPuntos = n;
        this.noClases = atrac;
    }
    public KMeans3d(){
        
    }
    
    public void generar(int n, int atrac) throws Exception{
        AnalysisLauncher.open(new KMeans3d(n, atrac));
    }
    @Override
    public void init() {

        float x;
        float y;
        float z;

        puntos3d = new Coord3d[noPuntos];
        colors = new Color[noPuntos];
        
        clases3d = new Coord3d[noClases];
        colorsAtractors = new Color[noClases];

        Random r = new Random();

        for (int i = 0; i < noPuntos; i++) {
            x = r.nextFloat() - 0.5f;
            y = r.nextFloat() - 0.5f;
            z = r.nextFloat() - 0.5f;
            puntos3d[i] = new Coord3d(x, y, z);
            colors[i] = new Color(0, 0, 0);
        }
        int listAtrac = 1;
        for (int i = 0; i < noClases; i++) {
            x = r.nextFloat() - 0.5f;
            y = r.nextFloat() - 0.5f;
            z = r.nextFloat() - 0.5f;
            clases3d[i] = new Coord3d(x, y, z);
            Color aux = escogerColor(i+1);
            colorsAtractors[i] = new Color(aux.r, aux.g, aux.b, aux.a);
          
            listAtrac++;
        }
        setAtractors(clases3d);
      
        boolean t;
        int numIteraciones=0;
        do{
            numIteraciones++;
            
            setPoints(distancias(getPoints(), clases3d, colors, colorsAtractors));
            
            t = calcularCentroides(getPoints(), clases3d, colors, colorsAtractors);
        }while(t);

        Coord3d[] puntos3d2= (Coord3d[])ArrayUtils.addAll(puntos3d, clases3d);
      
        Color[] colors2= (Color[])ArrayUtils.addAll(colors, colorsAtractors);
        
        scatter.setData(puntos3d2);
        scatter.setColors(colors2);
        scatter.setWidth(5);
        scatter.setBoundingBoxDisplayed(true);
        scatter.updateBounds();
        chart = AWTChartComponentFactory.chart(Quality.Fastest, "newt");
        chart.getScene().add(scatter); // Ploteamos
        
    }
    public Color escogerColor(int c){
        switch(c){
                case 0:
                    JOptionPane.showMessageDialog( null ,"Debes colocar una clase minimo", "K-Means",JOptionPane.WARNING_MESSAGE);
                    
                default:
                Random r = new Random();
                Color colores = new Color( r.nextInt(256), r.nextInt(256), r.nextInt(256) );
                return colores;
            }
    }
    public Coord3d[] distancias(Coord3d[] puntos3d, Coord3d[] clases3d, Color[] auxColor, Color[] auxColorsAtractors){
        ArrayList<Float> arrayDistancias = new ArrayList<>();
        float aux=0;
        int min = -1;
        int listado=1;
        for (int i = 0; i < noPuntos; i++) {
            for(int j=0; j< noClases; j++){
                arrayDistancias.add(euclidiana(puntos3d[i].x, puntos3d[i].y, puntos3d[i].z, clases3d[j].x, clases3d[j].y, clases3d[j].z));
            }
            aux = Collections.min(arrayDistancias);
            min = arrayDistancias.indexOf(aux);
            auxColor[i] = auxColorsAtractors[min];
           
            listado++;
            auxColor[i].a = -2.0f;
            arrayDistancias.clear();
        }
        setColors(auxColor);
        return puntos3d;
    }
    public float euclidiana(float x1, float y1, float z1, float x2, float y2, float z2){
        float d;
        return d =(float) Math.sqrt( Math.pow(x2-x1,2)+ Math.pow(y2-y1,2)+ Math.pow(z2-z1,2));
    }
    public boolean calcularCentroides(Coord3d[] puntos3d, Coord3d[] clases3d, Color[] auxColor, Color[] auxColorsAtractors){
        Coord3d[] auxAtractors = clases3d;
        int arrayContadores[] = new int[noClases];//contadores de numero de clases
        
        for(int i=0;i<noClases;i++){
            auxAtractors[i].x = 0;
            auxAtractors[i].y = 0;
            auxAtractors[i].z = 0;

            arrayContadores[i] = 0;
        }
        float acumulador = 0;
        
        for(int i=0;i< noPuntos;i++){
            for(int j=0; j< noClases;j++){
                
                if(auxColor[j].equals(auxColorsAtractors[j])){
                    
                    acumulador = auxAtractors[j].x + puntos3d[j].x;
                    auxAtractors[j].x = acumulador;
                    acumulador = 0;
                    acumulador = auxAtractors[j].y + puntos3d[j].y;
                    auxAtractors[j].y = acumulador;
                    acumulador = 0;
                    acumulador = auxAtractors[j].z + puntos3d[j].z;
                    auxAtractors[j].z = acumulador;
                    acumulador = 0;
                    arrayContadores[j] = arrayContadores[j]+1;
                }
            }
        }
     
        boolean t;
        t = pintarNuevoCentroide(auxAtractors, auxColorsAtractors, auxColor, arrayContadores);
        
        return t;
    }
    public boolean pintarNuevoCentroide(Coord3d[] auxAtractors, Color[] auxColorsAtractors,Color[] auxColors, int contadores[]){
        int countBooleans = 0; 
        for(int i=0;i<noClases;i++){
            auxAtractors[i].x = auxAtractors[i].x/contadores[i];
            auxAtractors[i].y = auxAtractors[i].y/contadores[i];
            auxAtractors[i].z = auxAtractors[i].z/contadores[i];
            if(auxAtractors.equals(getAtractors())){
                countBooleans++;
            }
        }

        setAtractors(auxAtractors);

        if(countBooleans == noClases){

            return false;
        }else{

            return true;
        }
    }

}

    

