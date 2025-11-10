package aed;

import java.util.ArrayList;

public class Estudiante implements Comparable<Estudiante> {
    private int id;
    private ArrayList<Integer> examen;
    private boolean entrego;
    private int respuestasCorrectas;
    private int nota;

    public Estudiante(int r, int i) {
        id = i;

        examen = new ArrayList<>(r);
        for (int j = 0; j < r; j++){
            examen.add(-1);
        }

        entrego = false;
        respuestasCorrectas = 0;
        nota = respuestasCorrectas / r;
    }

    public void añadirRespuesta(int e, int o, ArrayList<Integer> canon) {
        examen.set(e, o);
        if (canon.get(e) == o) {
            respuestasCorrectas++;
        }
    }

    @Override
    public int compareTo(Estudiante e) {
        // no entregó primero
        if (this.entrego != e.entrego) {
            return this.entrego ? 1 : -1;
        }

        // menor nota primero
        if (this.nota != e.nota) {
            return Integer.compare(this.nota, e.nota);
        }

        // menor id primero
        return Integer.compare(this.id, e.id);
    }
}
