package aed;

import java.util.ArrayList;

public class Estudiante implements Comparable<Estudiante> {
    private int id;
    private ArrayList<Integer> examen;
    private boolean entrego;
    private int respuestasCorrectas;
    private double nota;

    //se inicializa un estudiante con examen en blanco
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

    //Consultar los atributos del estudiante
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


    // Modifica el examen de un estudiante con la respuesta pasada por parametro
    // El ejercicio al que se le asigna la respuesta es pasada por parametro
    // Si la respuesta coincide con la del examen canonico suma el puntaje
    public void añadirRespuesta(int ejercicio, int respuesta, ArrayList<Integer> canon) {
        examen.set(ejercicio, respuesta);
        if (canon.get(ejercicio) == respuesta) {
            respuestasCorrectas++;
        }
        nota = respuestasCorrectas * (100.0 / canon.size());
    }

    // Intercambia el examen del estudiante por un examen pasado por parametro y recalcula la nota
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

    // Define como se ordenan los estudiantes en el Heap
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

    public NotaFinal notaFinal() {
        NotaFinal res = new NotaFinal(this.nota, this.id);
        return res;
    }
}
