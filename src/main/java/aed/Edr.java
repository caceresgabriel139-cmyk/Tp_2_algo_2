package aed;

import java.util.ArrayList;

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
        throw new UnsupportedOperationException("Sin implementar");
    }

    // -------------------------------------------------COPIARSE-------------------------------------------------

    public void copiarse(int estudiante) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // -------------------------------------------------RESOLVER-------------------------------------------------

    public void resolver(int estudiante, int NroEjercicio, int res) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // -------------------------------------------------CONSULTAR DARKWEB-------------------------------------------------

    public void consultarDarkWeb(int n, int[] examenDW) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // -------------------------------------------------ENTREGAR-------------------------------------------------------------

    public void entregar(int estudiante) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // -----------------------------------------------------CORREGIR---------------------------------------------------------

    public NotaFinal[] corregir() {
        throw new UnsupportedOperationException("Sin implementar");
    }

    public int[] chequearCopias() {
        throw new UnsupportedOperationException("Sin implementar");
    }
}

// proc calcularNota(), calcula la nota de un examen unico