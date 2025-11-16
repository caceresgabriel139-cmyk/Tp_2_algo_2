package aed;

import java.util.ArrayList;
import java.util.List;

public class Edr {
    private ArrayList<Handle<Estudiante>> estudiantes; // Lista de handles indexada por ID (longitud = E)
    private Heap<Estudiante> estudiantesPorNota; // Heap que ordena estudiantes por criterio definido en Estudiante
    private ArrayList<Integer> examenCanonico; // Examen correcto (tamano = R)
    private int ladoAula; // Parámetro LadoAula (usado para calcular vecinos)
    private int estudiantesSinEntregar; // Contador de cuántos aún no entregaron (<= E)
    private boolean[] copionesPorId; // True si el estudiante con el id i se identifico como copion
    private boolean chequearonCopias; // Indica si ya se ejecutó chequearCopias

    // -------------------------------------------------NUEVO_EDR-------------------------------------------------
    // Constructor
    // Complejidad: O(E * R + E) ≈ O(E * R)
    // - Crear E estudiantes, cada Estudiante constructor hace O(R) para inicializar
    // su examen.
    // - Construcción del heap con handles es O(E).
    public Edr(int LadoAula, int Cant_estudiantes, int[] ExamenCanonico) {
        estudiantes = new ArrayList<>(Cant_estudiantes);// Crear estudiantes crudos

        ArrayList<Estudiante> listaCruda = new ArrayList<>(Cant_estudiantes);
        for (int i = 0; i < Cant_estudiantes; i++) {
            listaCruda.add(new Estudiante(ExamenCanonico.length, i));// Crea cada Estudiante con examen en blanco (internamente O(R))
        }

        ArrayList<Heap<Estudiante>.HeapHandle> handlesOut = new ArrayList<>(Cant_estudiantes);// Crear heap con handles (O(E))
        estudiantesPorNota = new Heap<>(listaCruda, handlesOut);

        // Guardar los handles por ID
        // Copia los handles según el id de cada estudiante para acceso directo por id
        // -> O(E)
        for (int i = 0; i < Cant_estudiantes; i++) {
            estudiantes.add(handlesOut.get(i));
        }
        // Valores restantes
        ladoAula = LadoAula;
        estudiantesSinEntregar = Cant_estudiantes;
        copionesPorId = new boolean[Cant_estudiantes];
        examenCanonico = new ArrayList<>(ExamenCanonico.length);
        // Copia el examen canonico a un ArrayList (O(R))
        for (int x : ExamenCanonico) {
            examenCanonico.add(x);
        }
    }

    // -------------------------------------------------COPIARSE-------------------------------------------------
    // Un estudiante intenta copiarse de sus vecinos inmediatos
    // Complejidad: O(R + log E) — O(R) por comparar exámenes vecinos (cada vecino
    // revisa R respuestas),
    // y O(log E) por la llamada a actualizarNota que notifica el heap.
    public void copiarse(int estudiante) { // O(R + Log(E))
        if (estudiantes.get(estudiante).valor().entrego()) {
            return; // Si ya entregó, no puede copiarse -> O(1)
        }
        List<Integer> vecinos = new ArrayList<>();
        int alumnosPorFila = (ladoAula + 1) / 2; // cantidad de estudiantes por fila, contando el asiento vacío
        int fila = estudiante / alumnosPorFila;
        int col = estudiante % alumnosPorFila;
        // vecino izquierda
        if (col > 0 && !estudiantes.get(estudiante - 1).valor().entrego()) {
            vecinos.add(estudiante - 1);
        }
        // vecino derecha
        if (col < alumnosPorFila - 1 && estudiante + 1 < estudiantes.size()
                && !estudiantes.get(estudiante + 1).valor().entrego()) {
            vecinos.add(estudiante + 1);
        }
        // vecino arriba
        if (fila < ladoAula && estudiante + alumnosPorFila < estudiantes.size()
                && !estudiantes.get(estudiante + alumnosPorFila).valor().entrego()) {
            vecinos.add(estudiante + alumnosPorFila);
        }
        int cantDeRespuestas = -1;
        int primerIndice = -1;
        int primerRespuesta = -1;
        // Examen actual del estudiante que quiere copiarse
        ArrayList<Integer> examenActual = estudiantes.get(estudiante).valor().examen();

        for (int i = 0; i < vecinos.size(); i++) { // Recorre cada vecino (a lo sumo 3) y busca cuántas respuestas
                                                   // nuevas podría copiar -> por vecino O(3R)
            ArrayList<Integer> examenVecino = estudiantes.get(vecinos.get(i)).valor().examen();
            int nuevas = 0;
            int indicePrimera = -1;
            int valorPrimera = -1;
            boolean primeraEncontrada = false;
            // Recorre los R ejercicios comparando examenActual y examenVecino -> O(R)
            for (int j = 0; j < examenActual.size(); j++) {
                // Si el alumno no respondió (-1) y el vecino sí respondió, es candidata a
                // copiar
                if (examenActual.get(j) == -1 && examenVecino.get(j) != -1) {
                    nuevas++;
                    if (!primeraEncontrada) {
                        indicePrimera = j;
                        valorPrimera = examenVecino.get(j);
                        primeraEncontrada = true;
                    }
                }
            }

            // se copia del vecino que mas respuestas completadas tenga que no tenga,
            // desempata por id mayor
            if (nuevas >= cantDeRespuestas && indicePrimera > primerIndice) {
                cantDeRespuestas = nuevas;
                primerIndice = indicePrimera;
                primerRespuesta = valorPrimera;
            }
        }
        if (primerIndice == -1) {
            return; // Si no encontró ninguna respuesta copiable, sale -> O(1)
        }

        actualizarNota(estudiante, primerIndice, primerRespuesta);// Se copia
    }

    // Actualiza el examen de un estudiante y actualiza el heap
    // Complejidad: O(log E) (est.añadirRespuesta es O(1) y
    // estudiantesPorNota.actualizar es O(log E))
    public void actualizarNota(int estudianteId, int pregunta, int respuesta) {
        // Recuperamos el handle real del heap (acceso por id) -> O(1)
        Heap<Estudiante>.HeapHandle h = (Heap<Estudiante>.HeapHandle) estudiantes.get(estudianteId);
        Estudiante est = h.valor();
        // Actualizamos su examen (método Estudiante.añadirRespuesta — O(1))
        est.añadirRespuesta(pregunta, respuesta, examenCanonico);
        // Avisamos al heap que cambió (reordena O(log E))
        estudiantesPorNota.actualizar(h, est);
    }

    // -------------------------------------------------RESOLVER-------------------------------------------------
    // Responde un ejercicio: alias a actualizarNota
    public void resolver(int estudiante, int NroEjercicio, int res) {
        actualizarNota(estudiante, NroEjercicio, res);
    }

    // -----------------------------------------------CONSULTAR-DARKWEB-------------------------------------------------
    // Complejidad total: O(K * (R + log E)) + O(K * log E) = O(K(R + log E))
    public void consultarDarkWeb(int n, int[] examenDW) { // (K == n)
        Estudiante est;
        ArrayList<Estudiante> modificados = new ArrayList<>();
        // Complejidad del for: O(K (R + log E))
        for (int i = 0; i < n; i++) { // O(K)
            // Desencola los primeros K estudiantes del heap.
            est = estudiantesPorNota.desencolar(); // O(log E)
            if (est.entrego() == false) {
                // cambia los exámenes por el examen DW
                est.cambiarExamen(examenDW, examenCanonico);// O(R)
                modificados.add(est); // O(1)
            } else {
                i = n;
            }
        }
        // Complejidad del for: O(K * log E)
        // Agrega nuevamente al heap los estudiantes cuyos exámenes fueron modificados
        for (int i = 0; i < modificados.size(); i++) {
            estudiantesPorNota.agregar(modificados.get(i));
        }
    }

    // -------------------------------------------------NOTAS-------------------------------------------------
    // Devuelve las notas por id en un array (indexado por id)
    // Complejidad: O(E)
    public double[] notas() { // O(E)
        double[] notasPorId = new double[estudiantes.size()];
        for (int i = 0; i < estudiantes.size(); i++) { // Recorre el array de estudiantes por id -> O(E)
            notasPorId[i] = estudiantes.get(i).valor().nota(); // Accede a la nota del estudiante ->O(1)
        }
        return notasPorId;
    }

    // -------------------------------------------------ENTREGAR-------------------------------------------------
    // Marca entrega de un estudiante y reordena el heap
    // Complejidad: O(log E)
    public void entregar(int estudianteId) {
        // Recuperar el handle real del heap -> O(1)
        Heap<Estudiante>.HeapHandle h = (Heap<Estudiante>.HeapHandle) estudiantes.get(estudianteId);
        Estudiante est = h.valor();
        // Si ya entregó, no hacemos nada -> O(1)
        if (est.entrego()) {
            return;
        }
        // Marcar como entregado -> O(1)
        est.entregar();
        // Reacomodar la posición en el heap -> O(log E)
        estudiantesPorNota.actualizar(h, est);
        // Actualizar contador -> O(1)
        estudiantesSinEntregar--;
    }

    // -------------------------------------------------CORREGIR-------------------------------------------------
    // Devuelve array de NotaFinal de los no-copiones ordenado por nota descendente
    // y id descendente.
    // Complejidad:
    // - Vaciar heap: O(E log E)
    // - Filtrar: O(1)
    // - Restaurar heap (agregar E elementos): O(E log E)
    // - Complejidad total: O(E log E + E + E log E) = O(E* log E)
    public NotaFinal[] corregir() {
        Estudiante[] temp = new Estudiante[estudiantesPorNota.tamano()]; // O(1)
        ArrayList<NotaFinal> notasNoCopiones = new ArrayList<NotaFinal>(); // O(1)

        if (chequearonCopias == false) { // O(1)
            return null;
        }
        int i = 0;
        // Complejidad del while: O(E*(log E))
        while (estudiantesPorNota.tamano() > 0) { // O(E)
            Estudiante raizSacada = estudiantesPorNota.desencolar(); // O(log E)
            if (copionesPorId[raizSacada.id()] == false) { // O(1)
                notasNoCopiones.add(raizSacada.notaFinal()); // O(1)
            }
            temp[i] = raizSacada; // O(1)
            i++; // O(1)
        }
        NotaFinal[] res = new NotaFinal[notasNoCopiones.size()]; // O(1)

        int k = 0;
        // Da vuelta las notas: Complejidad del for = O(E)
        for (int j = res.length - 1; j > -1; j--) {
            res[k] = notasNoCopiones.get(j);
            k++;
        }
        // Rearma el heap: Complejidad del for = O(E * log E)
        for (int j = 0; j < temp.length; j++) {
            estudiantesPorNota.agregar(temp[j]);
        }
        return res;
    }

    // -------------------------------------------------CHEQUEAR-COPIAS-------------------------------------------------
    // Complejidad: O(E * R)
    // - Para cada pregunta (R) cuenta cuántos eligieron cada opción -> O(R * E)
    // - Luego para cada estudiante (E) recorre R preguntas y decide si es copión ->
    // O(E * R)
    // Total: O(E * R)
    public int[] chequearCopias() {

        ArrayList<ArrayList<Integer>> cantidadDeOpciones = new ArrayList<>(examenCanonico.size());

        // Para cada pregunta p (R) creamos un contador de 10 opciones y contamos
        // respuestas de todos los estudiantes (E)
        for (int p = 0; p < examenCanonico.size(); p++) {
            ArrayList<Integer> sub = new ArrayList<>(10);
            // Inicializa contadores de 0 a 9 (10 opciones) -> O(1) constante (10)
            for (int o = 0; o < 10; o++) {
                sub.add(0);
            }

            // Cuenta cuántos estudiantes respondieron cada opción en la pregunta p -> O(E)
            for (int e = 0; e < estudiantes.size(); e++) {
                int respuesta = estudiantes.get(e).valor().examen().get(p);
                if (respuesta > -1) {
                    sub.set(respuesta, sub.get(respuesta) + 1);
                }
            }

            cantidadDeOpciones.add(sub); // Añade el vector de conteos para la pregunta p
        }

        ArrayList<Integer> estudiantesCopiones = new ArrayList<>();

        // Para cada estudiante (E) recorremos las R preguntas y contamos cuántas
        // respuestas coinciden en porcentaje alto
        for (int e = 0; e < estudiantes.size(); e++) {

            Estudiante estudiante = estudiantes.get(e).valor();
            int copias = 0;
            int conRespuesta = 0;

            // Por cada pregunta r (R)
            for (int r = 0; r < cantidadDeOpciones.size(); r++) {

                int respuesta = estudiante.examen().get(r);

                // ignorar respuestas sin contestar (-1)
                if (respuesta > -1) {
                    // Calcula porcentaje de alumnos (excluyendo al propio) que dieron la misma
                    // respuesta
                    double porcentaje = (cantidadDeOpciones.get(r).get(respuesta) - 1) * 100.0
                            / (estudiantes.size() - 1);
                    if (porcentaje >= 25.0) {
                        copias++;
                    }
                    conRespuesta++;
                }
            }

            // Si todas las respuestas contestadas del alumno coinciden con las sospechosas
            // de copia, lo marca como copión
            if (conRespuesta > 0 && copias == conRespuesta) {
                estudiantesCopiones.add(e);
                copionesPorId[e] = true; // marca en boolean[] para uso posterior
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
