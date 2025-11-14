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
    private boolean chequearonCopias;

// -------------------------------------------------NUEVO_EDR------------------------------------------------- //

    public Edr(int LadoAula, int Cant_estudiantes, int[] ExamenCanonico) { //O(E*R)
        ArrayList<Handle<Estudiante>> nuevosEstudiantesArray = new ArrayList<>(Cant_estudiantes); // Inicializa un array vacio de estudiantes
        Heap<Estudiante> nuevosEstudiantesHeap = new Heap<>(Cant_estudiantes);

        for (int i = 0; i < Cant_estudiantes; i++){ // Inicializa E cantidad de estudiantes y los va añadiendo al Heap
            Estudiante e = new Estudiante(ExamenCanonico.length, i); // O(R) porque se tiene que crear un examen de R elementos como examen del estudiante
            nuevosEstudiantesArray.add(nuevosEstudiantesHeap.agregar(e));
        }// O(E*R) Porque se inicializan en orden por id y se meten ordenados al heap(porque ninguno entrego y la nota de todos es 0)

        estudiantes = nuevosEstudiantesArray;
        estudiantesPorNota = nuevosEstudiantesHeap;
        ladoAula = LadoAula;
        estudiantesSinEntregar = Cant_estudiantes;
        copionesPorId = new boolean[Cant_estudiantes];

        // Copia el examen canonico al atributo privado de la clase
        ArrayList<Integer> examenCanon = new ArrayList<>(ExamenCanonico.length);
        for (int k = 0; k < ExamenCanonico.length; k++){
            examenCanon.add(ExamenCanonico[k]);
        } //O(R) ya que el examen canonico pasado por parametro tiene R posiciones
        examenCanonico = examenCanon;
    } //O(E*R) + O(R), como E*R > R queda O(E*R)

// -------------------------------------------------COPIARSE------------------------------------------------- //

    public void copiarse(int estudiante) { // O(R + Log(E))

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

// -------------------------------------------------NOTAS------------------------------------------------- //

    public double[] notas() { //O(E)
        double[] notasPorId = new double[estudiantes.size()];

        for(int i = 0; i < estudiantes.size(); i++){ //Recorre el array de estudiantes por id -> O(E)
            notasPorId[i] = estudiantes.get(i).valor().nota();  // Accede a la nota del estudiante ->O(1)
        }

        return notasPorId;
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
        Estudiante[] temp = new Estudiante [estudiantesPorNota.Tamaño()];
        NotaFinal[] res = new NotaFinal[estudiantesPorNota.Tamaño()];
        if (chequearonCopias == false){
            return null;
        }
        while (estudiantesPorNota.Tamaño() > 0){
            int i = 0;
            Estudiante raizSacada = estudiantesPorNota.desencolar();
            if (copionesPorId[i] == false) {
            res[i] = (raizSacada.notaFinal());
            }
            temp[i] = raizSacada; i++;
            }
        for (int j = 0; j < temp.length; j++){
            estudiantesPorNota.agregar(temp[j]);
        }
        return res;
    }

// -------------------------------------------------CHEQUEAR COPIAS------------------------------------------------- //

    public int[] chequearCopias() {

        ArrayList<ArrayList<Integer>> cantidadDeOpciones = new ArrayList<>(examenCanonico.size());

        for (int p = 0; p < examenCanonico.size(); p++) {
            ArrayList<Integer> sub = new ArrayList<>(10);

            for (int o = 0; o < 10; o++) {
                sub.add(0);
            }

            for (int e = 0; e < estudiantes.size(); e++) {
                int respuesta = estudiantes.get(e).valor().examen().get(p);

                if (respuesta == -1) {
                    continue;
                }

                sub.set(respuesta, sub.get(respuesta) + 1);
            }

            cantidadDeOpciones.add(sub);
        }

        ArrayList<Integer> estudiantesCopiones = new ArrayList<>();

        for (int e = 0; e < estudiantes.size(); e++) {

            Estudiante estudiante = estudiantes.get(e).valor();
            int copias = 0;

            for (int r = 0; r < cantidadDeOpciones.size(); r++) {

                int respuesta = estudiante.examen().get(r);

                // ignorar respuestas sin contestar (-1)
                if (respuesta == -1) {
                    continue;
                }

                double porcentaje = (cantidadDeOpciones.get(r).get(respuesta) - 1) * 100.0 / (estudiantes.size() - 1);

                if (porcentaje >= 25) {
                    copias++;
                }
            }

            if (copias == 10) {
                estudiantesCopiones.add(e);
            }
        }

            // Convertir a int[]
            int[] copiones = new int[estudiantesCopiones.size()];
            for (int i = 0; i < estudiantesCopiones.size(); i++) {
                copiones[i] = estudiantesCopiones.get(i);
            }

            chequearonCopias = true;
            return copiones;
        }
    }
