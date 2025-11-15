package aed;

import java.util.ArrayList;

public class Estudiante implements Comparable<Estudiante> {
    private int id;                           // Identificador del estudiante
    private ArrayList<Integer> examen;        // Respuestas del estudiante, tamaño R
    private boolean entrego;                  // Indica si entregó el examen
    private int respuestasCorrectas;          // Cantidad de respuestas correctas
    private double nota;                      // Nota final calculada

    // Se inicializa un estudiante con examen en blanco
    // Complejidad: O(R), porque llena la lista con R valores
    public Estudiante(int r, int i) {
        id = i;

        examen = new ArrayList<>(r);      // Se crea un vector de tamaño R
        for (int j = 0; j < r; j++){      // Recorre todos los ejercicios
            examen.add(-1);               // Inicializa cada respuesta como -1
        }

        entrego = false;
        respuestasCorrectas = 0;
        nota = 0;
    }

    // ----------------------- GETTERS -----------------------

    public double nota() {
        return nota;                      // Devuelve la nota actual del estudiante
    }

    public int id() {
        return id;                        // Devuelve el identificador
    }

    public boolean entrego() {
        return entrego;                   // Indica si el estudiante entregó
    }

    public void entregar(){
        entrego = true;                   // Marca al estudiante como que entregó
    }

    public ArrayList<Integer> examen() {
        return examen;                    // Devuelve la lista de respuestas
    }


    // ----------------------- MODIFICAR RESPUESTA -----------------------

    // Modifica la respuesta dada por el estudiante para un ejercicio específico
    // ejercicio: índice del ejercicio a modificar
    // respuesta: nueva respuesta del estudiante
    // canon: respuestas correctas del examen (tamaño R)
    // Complejidad: O(1), solo accesos directos por índice
    public void añadirRespuesta(int ejercicio, int respuesta, ArrayList<Integer> canon) {

        int anterior = examen.get(ejercicio);   // Obtiene la respuesta anterior del alumno

        // Si la respuesta anterior era correcta, se resta porque se va a cambiar
        if (anterior != -1 && anterior == canon.get(ejercicio)) {
            respuestasCorrectas--;
        }

        examen.set(ejercicio, respuesta);       // Se guarda la nueva respuesta

        // Si la respuesta nueva coincide con la correcta, suma una correcta
        if (respuesta == canon.get(ejercicio)) {
            respuestasCorrectas++;
        }

        // Recalcula la nota: puntaje proporcional en 0-100
        nota = respuestasCorrectas * (100.0 / canon.size());
    }


    // ----------------------- CAMBIAR EXAMEN COMPLETO -----------------------

    // Reemplaza todo el examen del estudiante por un nuevo arreglo
    // examenNuevo: vector de enteros de tamaño R
    // canon: examen correcto
    // Complejidad: O(R), recorre todas las respuestas una vez
    public void cambiarExamen(int[] examenNuevo, ArrayList<Integer> canon){
        ArrayList<Integer> e = new ArrayList<>(examenNuevo.length);

        respuestasCorrectas = 0;                 // Reinicia la cantidad de correctas

        // Copia y evalúa cada respuesta
        for (int i = 0; i < examenNuevo.length; i++){
            e.add(i, examenNuevo[i]);           // Copia la respuesta al nuevo ArrayList

            // Si coincide con el canon, suma punto
            if (examenNuevo[i] == canon.get(i)){
                respuestasCorrectas++;
            }
        }

        examen = e;                              // Reemplaza el examen completo

        // Recalcula la nota
        nota = respuestasCorrectas * (100.0 / canon.size());
    }


    // ----------------------- COMPARACIÓN PARA ORDENAR -----------------------

    // Define cómo se ordenan los estudiantes en el Heap
    // 1. Primero los que NO entregaron (entrego=false)
    // 2. Luego los de menor nota
    // 3. Luego menor id
    // Complejidad: O(1), solo comparaciones simples
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


    // ----------------------- CREAR OBJETO NotaFinal -----------------------

    // Devuelve un objeto NotaFinal con la nota e id del estudiante
    // Complejidad: O(1)
    public NotaFinal notaFinal() {
        NotaFinal res = new NotaFinal(this.nota, this.id);
        return res;
    }
}
