package aed;

import java.util.ArrayList;

public class Estudiante implements Comparable<Estudiante> {
    private int id;
    private ArrayList<Integer> examen;
    private boolean entrego;
    private int respuestasCorrectas;
    private double nota;

    public Estudiante(int r, int i) {
        id = i;

        examen = new ArrayList<>(r);
        for (int j = 0; j < r; j++){
            examen.add(-1);
        }

        entrego = false;
        respuestasCorrectas = 0;
        nota = 0;
    }

    public double nota() {
        return nota;
    }

    public int id() {
        return id;
    }

    public boolean entrego() {
        return entrego;
    }

    public void entregar(){
        entrego=true;
    }

    public ArrayList<Integer> examen() {
        return examen;
    }


    public void añadirRespuesta(int e, int o, ArrayList<Integer> canon) {
        examen.set(e, o);
        if (canon.get(e) == o) {
            respuestasCorrectas++;
        }
        nota = respuestasCorrectas * (100.0 / canon.size());
    }

    public void cambiarExamen(int[] examenNuevo, ArrayList<Integer> canon){
        ArrayList<Integer> e = new ArrayList<>(examenNuevo.length);
        respuestasCorrectas=0;
        for (int i=0;i<examenNuevo.length;i++){
            e.add(i, examenNuevo[i]);
            if(examenNuevo[i]==canon.get(i)){
                respuestasCorrectas++;
            }
        }
        examen=e;
        nota = respuestasCorrectas * (100.0 / canon.size());
        

    }

    @Override
    public int compareTo(Estudiante e) {
        // no entregó primero
        if (this.entrego != e.entrego) {
            return this.entrego ? 1 : -1;
        }

        // menor nota primero
        if (this.nota != e.nota) {
            return Double.compare(this.nota, e.nota);
        }

        // menor id primero
        return Integer.compare(this.id, e.id);
    }
}
