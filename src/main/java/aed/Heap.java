package aed;

import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
    private ArrayList<Nodo> elementos;

    private class Nodo {
        int indice;
        T valor;

        public Nodo(T e, int i) {
            this.valor = e;
            this.indice = i;
        }
    }

    public Heap(int c) {
        elementos = new ArrayList<>(c);
    }

    public class HeapHandle implements Handle<T> {
        private Nodo nodoApuntado;

        private HeapHandle(Nodo n) {
            this.nodoApuntado = n;
        }

        @Override
        public T valor() {
            return nodoApuntado.valor;
        }
    }

    public HeapHandle agregar(T elem) {
        Nodo nuevoNodo = new Nodo(elem, elementos.size());
        elementos.add(nuevoNodo);
        siftUp(nuevoNodo.indice);
        return new HeapHandle(nuevoNodo);
    }

    public int hijoDerecho(int i) {
        return 2 * i + 2;
    }

    public int hijoIzquierdo(int i) {
        return 2 * i + 1;
    }

    public int padre(int i) {
        return (i - 1) / 2;
    }

    // actualiza el heap despues de una modificacion
    public void actualizar(Handle<T> handle, T nuevoValor) {
        HeapHandle h = (HeapHandle) handle;
        Nodo n = h.nodoApuntado;
        n.valor = nuevoValor;
        siftDown(n.indice);
}


    // agarra a un estdiante y lo sube lo maximo que puede
    private void siftUp(int i) {
        if (i == 0)
            return;
        int p = padre(i);

        if (elementos.get(i).valor.compareTo(elementos.get(p).valor) < 0) {
            swap(i, p);
            siftUp(p);
        }
    }

    // agarra a un estudiante y lo baja lo maximo que puede
    private void siftDown(int i) {
        int izq = hijoIzquierdo(i);
        int der = hijoDerecho(i);
        int menor = i;
        if (izq < elementos.size() && elementos.get(izq).valor.compareTo(elementos.get(menor).valor) < 0) {
            menor = izq;
        }
        if (der < elementos.size() && elementos.get(der).valor.compareTo(elementos.get(menor).valor) < 0) {
            menor = der;
        }
        if (menor != i) {
            swap(i, menor);
            siftDown(menor);
        }
    }

    // Reemplaza el primer elemento con la última hoja y eliminar la última hoja,
    // baja la nueva raiz y devuelve el valor de la anterior raiz
    public T desencolar() {
        if (elementos.size() == 0)
            {return null;}
        Nodo raiz = elementos.get(0);
        T valor = raiz.valor;
        Nodo ultimo = elementos.remove(elementos.size() - 1);
        if (!elementos.isEmpty()) {
            elementos.set(0, ultimo);
            ultimo.indice = 0;
            siftDown(0);
        }
        return valor;
    }

    // intercambia los estudiantes uno a unno
    private void swap(int i, int j) {
        Nodo ni = elementos.get(i);
        Nodo nj = elementos.get(j);
        elementos.set(i, nj);
        elementos.set(j, ni);
        ni.indice = j;
        nj.indice = i;
    }
}