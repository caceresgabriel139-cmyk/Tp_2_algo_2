package aed;

import java.util.ArrayList;
import java.util.List;

public class Edr {
    private ArrayList<Handle<Estudiante>> estudiantes;   // Lista de handles indexada por ID (longitud = E)
    private Heap<Estudiante> estudiantesPorNota;         // Heap que ordena estudiantes por criterio definido en Estudiante
    private ArrayList<Integer> examenCanonico;           // Examen correcto (tamaño = R)
    private int ladoAula;                                // Parámetro LadoAula (usado para calcular vecinos)
    private int estudiantesSinEntregar;                  // Contador de cuántos aún no entregaron (<= E)
    private boolean[] copionesPorId;                     // Marca si un estudiante (por id) es copión (K true's)
    private boolean chequearonCopias;                    // Indica si ya se ejecutó chequearCopias

// -------------------------------------------------NUEVO_EDR------------------------------------------------- //

    // Constructor
    // Complejidad: O(E * R + E) ≈ O(E * R)
    // - Crear E estudiantes, cada Estudiante constructor hace O(R) para inicializar su examen.
    // - Construcción del heap con handles es O(E).
    public Edr(int LadoAula, int Cant_estudiantes, int[] ExamenCanonico) {

        estudiantes = new ArrayList<>(Cant_estudiantes);

        // ----- Crear estudiantes crudos -----
        ArrayList<Estudiante> listaCruda = new ArrayList<>(Cant_estudiantes);
        for (int i = 0; i < Cant_estudiantes; i++) {
            // Crea cada Estudiante con examen en blanco (internamente O(R))
            listaCruda.add(new Estudiante(ExamenCanonico.length, i));
        }

        // ----- Crear heap con handles (O(E)) -----
        ArrayList<Heap<Estudiante>.HeapHandle> handlesOut = new ArrayList<>(Cant_estudiantes);
        estudiantesPorNota = new Heap<>(listaCruda, handlesOut);

        // ----- Guardar los handles por ID -----
        // Copia los handles según el id de cada estudiante para acceso directo por id -> O(E)
        for (int i = 0; i < Cant_estudiantes; i++) {
            estudiantes.add(handlesOut.get(i));
        }

        // ----- Valores restantes -----
        ladoAula = LadoAula;
        estudiantesSinEntregar = Cant_estudiantes;
        copionesPorId = new boolean[Cant_estudiantes];

        examenCanonico = new ArrayList<>(ExamenCanonico.length);
        // Copia el examen canonico a un ArrayList (O(R))
        for (int x : ExamenCanonico) {
            examenCanonico.add(x);
        }
    }

// -------------------------------------------------COPIARSE------------------------------------------------- //

    // Un estudiante intenta copiarse de sus vecinos inmediatos
    // Complejidad: O(R + log E) — O(R) por comparar exámenes vecinos (cada vecino revisa R respuestas),
    // y O(log E) por la llamada a actualizarNota que notifica el heap.
    public void copiarse(int estudiante) { // O(R + Log(E))

        // Si ya entregó, no puede copiarse -> O(1)
        if (estudiantes.get(estudiante).valor().entrego()) {
            return;
        }

        List<Integer> vecinos = new ArrayList<>();
        int alumnosPorFila = (ladoAula + 1) / 2; // cantidad de estudiantes por fila, contando el asiento vacío

        int fila = estudiante / alumnosPorFila;
        int col  = estudiante % alumnosPorFila;

        // vecino izquierda (verifica límites y si no entregó) -> O(1)
        if (col > 0 && !estudiantes.get(estudiante - 1).valor().entrego()) {
            vecinos.add(estudiante - 1);
        }

        // vecino derecha (verifica límites y si no entregó) -> O(1)
        if (col < alumnosPorFila - 1 && estudiante + 1 < estudiantes.size() && !estudiantes.get(estudiante + 1).valor().entrego()) {
            vecinos.add(estudiante + 1);
        }

        // vecino arriba (verifica límites y si no entregó) -> O(1)
        if (fila < ladoAula &&  estudiante + alumnosPorFila < estudiantes.size() &&!estudiantes.get(estudiante + alumnosPorFila).valor().entrego()) {
            vecinos.add(estudiante + alumnosPorFila);
        }

        int cantDeRespuestas = -1;   // mejor cantidad de respuestas que puede ganar
        int primerIndice = -1;       // índice del primer ejercicio copiable encontrado
        int primerRespuesta = -1;    // respuesta correspondiente

        // Examen actual del estudiante que quiere copiarse (referencia, tamaño R)
        ArrayList<Integer> examenActual = estudiantes.get(estudiante).valor().examen();

        // Recorre cada vecino (a lo sumo 3) y busca cuántas respuestas nuevas podría copiar -> por vecino O(R)
        for (int i = 0; i < vecinos.size(); i++) {

            ArrayList<Integer> examenVecino = estudiantes.get(vecinos.get(i)).valor().examen();

            int nuevas = 0;               // cuántas respuestas puede copiar del vecino i
            int indicePrimera = -1;       // primer índice copiable encontrado en este vecino
            int valorPrimera = -1;        // valor de esa primera respuesta
            boolean primeraEncontrada = false;

            // Recorre los R ejercicios comparando examenActual y examenVecino -> O(R)
            for (int j = 0; j < examenActual.size(); j++) {
                // Si el alumno no respondió (-1) y el vecino sí respondió, es candidata a copiar
                if (examenActual.get(j) == -1 && examenVecino.get(j) != -1) {
                    nuevas++;

                    if (!primeraEncontrada) {
                        indicePrimera = j;
                        valorPrimera = examenVecino.get(j);
                        primeraEncontrada = true;
                    }
                }
            }

            // Política de elección: preferir vecinos que aporten mayor cantidad de respuestas nuevas.
            // Si iguales, se usa el primerIndice mayor (criterio del autor del código).
            if (nuevas >= cantDeRespuestas && indicePrimera > primerIndice){

                cantDeRespuestas = nuevas;
                primerIndice = indicePrimera;
                primerRespuesta = valorPrimera;
            }
        }

        // Si no encontró ninguna respuesta copiable, sale -> O(1)
        if (primerIndice == -1) {
            return;
        }

        // Actualiza la nota copiando la primera respuesta seleccionada
        // actualizarNota hace est.añadirRespuesta (O(1)) + actualizar heap (O(log E))
        actualizarNota(estudiante, primerIndice, primerRespuesta);
    }

    // Actualiza el examen de un estudiante y notifica al heap
    // Complejidad: O(log E) (est.añadirRespuesta es O(1) y estudiantesPorNota.actualizar es O(log E))
    public void actualizarNota(int estudianteId, int pregunta, int respuesta) {

        // Recuperamos el handle real del heap (acceso por id) -> O(1)
        Heap<Estudiante>.HeapHandle h =
            (Heap<Estudiante>.HeapHandle) estudiantes.get(estudianteId);

        Estudiante est = h.valor();

        // Actualizamos su examen (método Estudiante.añadirRespuesta — O(1))
        est.añadirRespuesta(pregunta, respuesta, examenCanonico);

        // Avisamos al heap que cambió (reordena O(log E))
        estudiantesPorNota.actualizar(h, est);
    }

// -------------------------------------------------RESOLVER------------------------------------------------- //

    // Responde un ejercicio: alias a actualizarNota
    public void resolver(int estudiante, int NroEjercicio, int res) {
        actualizarNota(estudiante, NroEjercicio, res);
    }

// -------------------------------------------------CONSULTAR DARKWEB------------------------------------------------- //

// Simula usar un examen de la "DarkWeb" en los k peores estudiantes que todavía no entregaron.
    // Complejidad dominante:
    //  - Vaciar heap: O(E log E) (E veces desencolar O(log E))
    //  - Ordenación manual (insertion sort) de los no entregados: O(E^2) en el peor caso
    //  - Aplicar examenDW a k estudiantes: O(k * R)
    //  - Reconstruir heap: O(E)
    // => Complejidad total: O(E^2 + k*R + E log E) → dominante O(E^2 + k*R)
    public void consultarDarkWeb(int k, int[] examenDW) {
        if (k > estudiantesSinEntregar) {
            throw new IllegalArgumentException("No hay suficientes estudiantes sin entregar");
        }

        // 1. Vaciar el heap y recolectar todos los estudiantes
        ArrayList<Estudiante> allStudents = new ArrayList<>();
        while (estudiantesPorNota.Tamaño() > 0) {
            // desencolar() devuelve el Estudiante mínimo; cada llamada O(log E)
            allStudents.add(estudiantesPorNota.desencolar());
        }

        // 2. Filtrar los estudiantes que no han entregado -> O(E)
        ArrayList<Estudiante> estudiantesTemporales = new ArrayList<>();
        for (int i = 0; i < allStudents.size(); i++) {
            Estudiante est = allStudents.get(i);
            if (!est.entrego()) {
                estudiantesTemporales.add(est);
            }
        }

        // 3. Ordenar manualmente estudiantesTemporales por nota ascendente, luego id ascendente
        //    (insertion sort usando compareTo) — O(N^2) donde N = estudiantesTemporales.size() ≤ E
        for (int i = 1; i < estudiantesTemporales.size(); i++) {
            Estudiante estudianteTemporal = estudiantesTemporales.get(i);
            int j = i - 1;
            while (j >= 0 && estudianteTemporal.compareTo(estudiantesTemporales.get(j)) < 0) {
                estudiantesTemporales.set(j + 1, estudiantesTemporales.get(j)); // movimiento O(1)
                j--;
            }
            estudiantesTemporales.set(j + 1, estudianteTemporal);
        }

        // 4. Tomar los primeros k (los peores) y modificar sus exámenes -> O(k * R)
        for (int i = 0; i < k; i++) {
            Estudiante est = estudiantesTemporales.get(i);
            est.cambiarExamen(examenDW, examenCanonico); // O(R)
        }

        // 5. Reconstruir el heap con TODOS los estudiantes (O(E))
        ArrayList<Heap<Estudiante>.HeapHandle> newHandles = new ArrayList<>();
        estudiantesPorNota = new Heap<>(allStudents, newHandles);

        // 6. Actualizar el ArrayList de handles por id (O(E))
        for (int i = 0; i < allStudents.size(); i++) {
            estudiantes.set(allStudents.get(i).id(), newHandles.get(i));
        }
    }



// -------------------------------------------------NOTAS------------------------------------------------- //

    // Devuelve las notas por id en un array (indexado por id)
    // Complejidad: O(E)
    public double[] notas() { //O(E)
        double[] notasPorId = new double[estudiantes.size()];

        for(int i = 0; i < estudiantes.size(); i++){ //Recorre el array de estudiantes por id -> O(E)
            notasPorId[i] = estudiantes.get(i).valor().nota();  // Accede a la nota del estudiante ->O(1)
        }

        return notasPorId;
    } 

// -------------------------------------------------ENTREGAR------------------------------------------------- //

    // Marca entrega de un estudiante y reordena el heap
    // Complejidad: O(log E)
    public void entregar(int estudianteId) {

        // Recuperar el handle real del heap -> O(1)
        Heap<Estudiante>.HeapHandle h =
            (Heap<Estudiante>.HeapHandle) estudiantes.get(estudianteId);

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

// -------------------------------------------------CORREGIR------------------------------------------------- //

    // Devuelve array de NotaFinal de los no-copiones ordenado por nota descendente y id descendente.
    // Complejidad:
    //  - Vaciar heap: O(E log E)
    //  - Filtrar: O(E)
    //  - Ordenar notasNoCopiones (insertion sort): O((E-K)^2) donde K = #copiones
    //  - Restaurar heap (agregar E elementos): O(E log E)
    // => Total dominante: O((E-K)^2 + E log E) → normalmente O(E^2) en peor caso.
    public NotaFinal[] corregir() {
        Estudiante[] temp = new Estudiante[estudiantesPorNota.Tamaño()];
        ArrayList<NotaFinal> notasNoCopiones = new ArrayList<NotaFinal>();
        if (!chequearonCopias) {  // Asumiendo que chequearonCopias es boolean
            return null;
        }
        int i = 0;

        // Vaciar heap y filtrar no copiones
        while (estudiantesPorNota.Tamaño() > 0) {
            Estudiante raizSacada = estudiantesPorNota.desencolar(); // O(log E) cada vez
            if (!copionesPorId[raizSacada.id()]) {  // añade si no es copión -> O(1)
                notasNoCopiones.add(raizSacada.notaFinal());
            }
            temp[i] = raizSacada; // guardamos para restaurar luego
            i++;
        }

        // Ordenar notasNoCopiones manualmente: nota descendente, id descendente (insertion sort)
        for (int p = 1; p < notasNoCopiones.size(); p++) {
            NotaFinal notaTemporal = notasNoCopiones.get(p);
            int q = p - 1;
            while (q >= 0) {
                NotaFinal current = notasNoCopiones.get(q);
                // Comparar: mayor nota primero, luego mayor id primero
                boolean shouldSwap = (notaTemporal._nota > current._nota) || (notaTemporal._nota == current._nota && notaTemporal._id > current._id);
                if (!shouldSwap) break;
                notasNoCopiones.set(q + 1, current);
                q--;
            }
            notasNoCopiones.set(q + 1, notaTemporal);
        }

        // Crear array resultado (O(E))
        NotaFinal[] res = new NotaFinal[notasNoCopiones.size()];
        for (int j = 0; j < res.length; j++) {
            res[j] = notasNoCopiones.get(j);
        }

        // Restaurar heap: volver a agregar todos los estudiantes previamente extraídos
        // Cada agregar es O(log E) => total O(E log E)
        for (int j = 0; j < temp.length; j++) {
            estudiantesPorNota.agregar(temp[j]);
        }

        return res;
    }

// -------------------------------------------------CHEQUEAR COPIAS------------------------------------------------- //

    // Detecta estudiantes que probablemente copiaron.
    // Complejidad: O(E * R)
    // - Para cada pregunta (R) cuenta cuántos eligieron cada opción -> O(R * E)
    // - Luego para cada estudiante (E) recorre R preguntas y decide si es copión -> O(E * R)
    // Total: O(E * R)
    public int[] chequearCopias() {

        ArrayList<ArrayList<Integer>> cantidadDeOpciones = new ArrayList<>(examenCanonico.size());

        // Para cada pregunta p (R) creamos un contador de 10 opciones y contamos respuestas de todos los estudiantes (E)
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

        // Para cada estudiante (E) recorremos las R preguntas y contamos cuántas respuestas coinciden en porcentaje alto
        for (int e = 0; e < estudiantes.size(); e++) {

            Estudiante estudiante = estudiantes.get(e).valor();
            int copias = 0;
            int conRespuesta = 0;

            // Por cada pregunta r (R)
            for (int r = 0; r < cantidadDeOpciones.size(); r++) {

                int respuesta = estudiante.examen().get(r);

                // ignorar respuestas sin contestar (-1)
                if (respuesta > -1) {
                    // Calcula porcentaje de alumnos (excluyendo al propio) que dieron la misma respuesta
                    double porcentaje = (cantidadDeOpciones.get(r).get(respuesta) - 1) * 100.0 / (estudiantes.size() - 1);

                    if (porcentaje >= 25.0) {
                        copias++;
                    }
                    conRespuesta++;
                }
            }

            // Si todas las respuestas contestadas del alumno están en el umbral de similitud, lo marca como copión
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
