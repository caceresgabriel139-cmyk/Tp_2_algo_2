package aed;

import java.util.ArrayList;
import java.util.List;

public class Edr {
    private ArrayList<Handle<Estudiante>> estudiantes;
    private Heap<Estudiante> estudiantesPorNota;
    private ArrayList<Integer> examenCanonico;
    private int ladoAula;
    private int estudiantesSinEntregar;
    private boolean[] copionesPorId;

// -------------------------------------------------NUEVO_EDR------------------------------------------------- //

    public Edr(int LadoAula, int Cant_estudiantes, int[] ExamenCanonico) {

        ArrayList<Handle<Estudiante>> nuevosEstudiantesArray = new ArrayList<>(Cant_estudiantes);
        Heap<Estudiante> nuevosEstudiantesHeap = new Heap<>(Cant_estudiantes);

        for (int i = 0; i < Cant_estudiantes; i++){
        Estudiante e = new Estudiante(ExamenCanonico.length, i);
        nuevosEstudiantesArray.add(nuevosEstudiantesHeap.agregar(e));
        }

        estudiantes = nuevosEstudiantesArray;
        estudiantesPorNota = nuevosEstudiantesHeap;
        ladoAula = LadoAula;
        estudiantesSinEntregar = Cant_estudiantes;
        copionesPorId = new boolean[Cant_estudiantes];

        ArrayList<Integer> examenCanon = new ArrayList<>(ExamenCanonico.length);
        for (int k = 0; k < ExamenCanonico.length; k++){
            examenCanon.add(ExamenCanonico[k]);
        }
        examenCanonico = examenCanon;
    }

// -------------------------------------------------NOTAS------------------------------------------------- //

    public double[] notas() {
        double[] notasPorId = new double[estudiantes.size()];

        for(int i = 0; i < estudiantes.size(); i++){
            notasPorId[i] = estudiantes.get(i).valor().nota();
        }

        return notasPorId;
    }

// -------------------------------------------------COPIARSE------------------------------------------------- //

    public void copiarse(int estudiante) {

        if (estudiantes.get(estudiante).valor().entrego()) {
            return;
        }

        List<Integer> vecinos = new ArrayList<>();
        int alumnosPorFila = (ladoAula + 1) / 2; // cantidad de estudiantes por fila, contando el asiento vacío

        int fila = estudiante / alumnosPorFila;
        int col  = estudiante % alumnosPorFila;

        // vecino izquierda
        if (col > 0 && !estudiantes.get(estudiante - 1).valor().entrego()) {
            vecinos.add(estudiante - 1);
        }

        // vecino derecha
        if (col < alumnosPorFila - 1 && estudiante + 1 < estudiantes.size() && !estudiantes.get(estudiante + 1).valor().entrego()) {
            vecinos.add(estudiante + 1);
        }

        // vecino arriba
        if (fila > 0 && !estudiantes.get(estudiante - alumnosPorFila).valor().entrego()) {
            vecinos.add(estudiante - alumnosPorFila);
        }

        int cantDeRespuestas = -1;
        int primerIndice = -1;
        int primerRespuesta = -1;

        ArrayList<Integer> examenActual = estudiantes.get(estudiante).valor().examen();

        for (int i = 0; i < vecinos.size(); i++) {

            ArrayList<Integer> examenVecino = estudiantes.get(vecinos.get(i)).valor().examen();

            int nuevas = 0;
            int indicePrimera = -1;
            int valorPrimera = -1;
            boolean primeraEncontrada = false;

            for (int j = 0; j < examenActual.size(); j++) {
                if (examenActual.get(j) == -1 && examenVecino.get(j) != -1) {
                    nuevas++;

                    if (!primeraEncontrada) {
                        indicePrimera = j;
                        valorPrimera = examenVecino.get(j);
                        primeraEncontrada = true;
                    }
                }
            }

            if (nuevas > cantDeRespuestas) {
                cantDeRespuestas = nuevas;
                primerIndice = indicePrimera;
                primerRespuesta = valorPrimera;
            }
        }

        if (primerIndice == -1) {
            return;
        }

        actualizarNota(estudiante, primerIndice, primerRespuesta);
    }

    public void actualizarNota(int estudianteId, int pregunta, int respuesta) {
        // Tomamos el estudiante y su handle
        Handle<Estudiante> handle = estudiantes.get(estudianteId);
        Estudiante est = handle.valor();

        // Actualizamos el examen del estudiante
        est.añadirRespuesta(pregunta, respuesta, examenCanonico);

        // Reordenamos el heap según la nueva nota
        estudiantesPorNota.actualizar(handle, est);
}

// -------------------------------------------------RESOLVER------------------------------------------------- //

    public void resolver(int estudiante, int NroEjercicio, int res) {
        actualizarNota(estudiante, NroEjercicio, res);
    }

// -------------------------------------------------CONSULTAR DARKWEB------------------------------------------------- //

    public void consultarDarkWeb(int n, int[] examenDW) {
        
        Estudiante est;
        ArrayList<Estudiante> modificados = new ArrayList<>();
        for (int i=0;i<n;i++){
            est=estudiantesPorNota.desencolar();
            if(est.entrego()==false){
            est.cambiarExamen(examenDW,examenCanonico);
            modificados.add(est);
            }else{
            i=n;    
            }
        }
        for (int i=0;i<modificados.size();i++){
        estudiantesPorNota.agregar(modificados.get(i));
        }    
        
    }

// -------------------------------------------------ENTREGAR------------------------------------------------- //

    public void entregar(int estudiante) {
        Handle<Estudiante> handle = estudiantes.get(estudiante);
        Estudiante est = handle.valor();
        estudiantes.get(estudiante).valor().entregar();
        estudiantesPorNota.actualizar(handle, est);
        estudiantesSinEntregar=estudiantesSinEntregar-1;
        
    }

// -------------------------------------------------CORREGIR------------------------------------------------- //

    public NotaFinal[] corregir() {
        throw new UnsupportedOperationException("Sin implementar");
    }

// -------------------------------------------------CHEQUEAR COPIAS------------------------------------------------- //


    public int[] chequearCopias() {
        throw new UnsupportedOperationException("Sin implementar");
    }
}
