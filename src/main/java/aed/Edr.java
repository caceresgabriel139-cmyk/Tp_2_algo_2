package aed;

import java.util.ArrayList;

public class Edr {
    private ArrayList<Handle<Estudiante>> estudiantes;
    private Heap<Estudiante> estudiantesPorNota;
    private ArrayList<Integer> examenCanonico;
    private int ladoAula;
    private int estudiantesSinEntregar;
    private boolean[] copionesPorId;

    // -------------------------------------------------NUEVO_EDR----------------------------------------------------------------

    public Edr(int LadoAula, int Cant_estudiantes, int[] ExamenCanonico) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // -------------------------------------------------NOTAS--------------------------------------------------------------------------

    public double[] notas() {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // ------------------------------------------------COPIARSE------------------------------------------------------------------------

    public void copiarse(int estudiante) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // -----------------------------------------------RESOLVER----------------------------------------------------------------

    public void resolver(int estudiante, int NroEjercicio, int res) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    // ------------------------------------------------CONSULTAR DARK
    // WEB-------------------------------------------------------

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