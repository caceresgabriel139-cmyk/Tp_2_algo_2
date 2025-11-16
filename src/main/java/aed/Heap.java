package aed;

import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
    private ArrayList<Nodo> elementos;  // Lista interna que almacena el heap. Tamaño ≈ cantidad de elementos (E, K o lo que corresponda)

// ----------------------------- NODO ----------------------------- //

    private class Nodo {
        T valor;      // Valor almacenado en el nodo
        int indice;   // Índice del nodo dentro del array

        Nodo(T v, int idx) {
            valor = v;
            indice = idx;
        }
    }

// ----------------------------- HANDLES ----------------------------- //

public class HeapHandle implements Handle<T> {
    private Nodo nodo;  // Referencia directa al nodo, permite actualizar su valor en O(log N)

    private HeapHandle(Nodo n) {
        this.nodo = n;
    }

    // Método requerido por la interfaz Handle<T>
    @Override
    public T valor() {
        return nodo.valor;   // Devuelve el valor actual del nodo
    }

    // Alias opcional
    public T get() {
        return valor();
    }

    // Devuelve el índice del nodo dentro del heap
    private int indice() {
        return nodo.indice;
    }

    // Cambia el valor almacenado en el nodo
    private void setValor(T v) {
        nodo.valor = v;
    }
}

// ----------------------------- CONSTRUCTORES ----------------------------- //

    public Heap(int c) {
        elementos = new ArrayList<>(c); // Crea heap vacío con capacidad inicial c
    }

    public Heap(ArrayList<T> lista, ArrayList<HeapHandle> handlesOut) {
        int n = lista.size();
        elementos = new ArrayList<>(n); // Inicializa la estructura interna del heap del tamaño de la lista

        // Carga cada elemento como un nodo con índice correspondiente
        // Complejidad: O(E) si E es el tamaño de la lista
        for (int i = 0; i < n; i++) {
            elementos.add(new Nodo(lista.get(i), i)); // Asignación directa
        }

        // Crea un handle para cada nodo
        // Complejidad: O(E)
        for (Nodo nodo : elementos) {
            handlesOut.add(new HeapHandle(nodo));
        }

        buildHeap();   // Construye el heap en O(E)
    }

// ----------------------------- FUNCIONES INTERNAS ----------------------------- //

    public int Tamaño() {
        return elementos.size();   // Devuelve cantidad total de elementos en el heap
    }

    // Fórmulas estándar de árbol binario representado en array
    private int hijoIzquierdo(int i) {
        return 2 * i + 1;
    }

    private int hijoDerecho(int i) {
        return 2 * i + 2;
    }

    private int padre(int i) {
        return (i - 1) / 2;
    }

    // Construye el heap desde una lista arbitraria
    // Complejidad: O(E), usando heapify óptimo
    private void buildHeap() {
        // Recorre todos los nodos internos de abajo hacia arriba aplicando siftDown
        for (int i = (elementos.size() / 2) - 1; i >= 0; i--) {
            siftDown(i);  // Restaura la propiedad de heap para cada subárbol
        }
    }

    public T elemento(int i) {
        return elementos.get(i).valor; // Devuelve el valor almacenado en la posición i
    }

    // ----------------------------- ACTUALIZAR ----------------------------- //

    // Actualiza el valor de un nodo y reacomoda el heap según corresponda
    // Complejidad total: O(log E)
    public void actualizar(HeapHandle handle, T nuevoValor) {
        Nodo n = handle.nodo;

        T viejoValor = n.valor;  // Guarda valor previo
        n.valor = nuevoValor;    // Asigna nuevo valor al nodo

        int i = n.indice;        // Ubicación del nodo dentro del heap

        // Si el nuevo valor es menor, sube en el heap
        siftUp(i); // O(log E) 
        // Si el nuevo valor es mayor, baja en el heap
        siftDown(i); // O(log E)
        }
    

    // ----------------------------- AGREGAR ----------------------------- //

    // Inserta un nuevo elemento al final y lo reacomoda hacia arriba
    // Complejidad: O(log E)
    public HeapHandle agregar(T elem) {
        Nodo nuevo = new Nodo(elem, elementos.size());  // Crea nodo en última posición
        elementos.add(nuevo);                           // Inserta al final del array
        HeapHandle handle = new HeapHandle(nuevo);      // Devuelve handle para manipulación externa
        siftUp(nuevo.indice);                           // Restaura propiedad de heap
        return handle;
    }

    // ----------------------------- SIFT UP ----------------------------- //

    // Hace que un nodo suba mientras viole la propiedad de heap con su padre
    // Complejidad: O(log E)
    private void siftUp(int i) {
        if (i == 0) return; // Ya es la raíz, condición base

        int p = padre(i);

        // Si el valor actual es menor que el del padre, se intercambian
        if (elementos.get(i).valor.compareTo(elementos.get(p).valor) < 0) {
            swap(i, p);   // Intercambia posiciones en O(1)
            siftUp(p);     // Repite recursivamente hacia arriba
        }
    }

    // ----------------------------- SIFT DOWN ----------------------------- //

    // Hace que un nodo baje mientras alguno de sus hijos tenga menor valor
    // Complejidad: O(log E)
    private void siftDown(int i) {
        int izq = hijoIzquierdo(i);
        int der = hijoDerecho(i);
        int menor = i; // Asume que el nodo actual es el menor por ahora

        // Compara con hijo izquierdo
        if (izq < elementos.size() &&
            elementos.get(izq).valor.compareTo(elementos.get(menor).valor) < 0) {
            menor = izq;
        }

        // Compara con hijo derecho
        if (der < elementos.size() &&
            elementos.get(der).valor.compareTo(elementos.get(menor).valor) < 0) {
            menor = der;
        }

        // Si alguno de los hijos es menor, intercambia y continúa hacia abajo
        if (menor != i) {
            swap(i, menor);   // O(1)
            siftDown(menor);  // Continua el proceso en el subárbol
        }
    }

    // ----------------------------- DESENCOLAR ----------------------------- //

    // Remueve y devuelve el mínimo del heap (la raíz)
    // Complejidad: O(log E)
    public T desencolar() {
        if (elementos.isEmpty()) return null;

        // 1. Guardamos la raíz
        Nodo raiz = elementos.get(0);
        T valor = raiz.valor;

        // 2. Tomamos el último nodo del array
        Nodo ultimo = elementos.remove(elementos.size() - 1);

        // 3. Si todavía contiene elementos, mover el último a la raíz
        if (!elementos.isEmpty()) {
            elementos.set(0, ultimo);
            ultimo.indice = 0;
            siftDown(0);  // Restaura propiedad de heap — O(log E)
        }

        return valor;
    }

    // ----------------------------- SWAP ----------------------------- //

    // Intercambia dos nodos en el array y actualiza sus índices
    // Complejidad: O(1)
    private void swap(int i, int j) {
        Nodo a = elementos.get(i);
        Nodo b = elementos.get(j);

        elementos.set(i, b);
        elementos.set(j, a);

        a.indice = j;  // Actualización del índice luego del swap
        b.indice = i;
    }
}
