package aed;
import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
    private ArrayList<T> elementos;
    
    private class Nodo {
        int indice;
        T valor;

        public Nodo(T e) {
            this.valor = e;
        }
    }

    public class HeapHandle implements Handle<T> {
        private Nodo nodoApuntado ;
        private HeapHandle( Nodo n) {
            this.nodoApuntado = n ;
        }
        @Override
        public T valor () {
            return nodoApuntado.valor ;
        }
        @Override
        public void eliminar () {
            // TODO Implementar la eliminación del nodo del heap
        }
    }
    
    public HeapHandle agregar(T elem){
        public HeapHandle agregar(T elem) {
        Nodo nuevoNodo = new Nodo(elem, elementos.size());
        elementos.add(nuevoNodo);
        siftUp(nuevoNodo.indice);
        return new HeapHandle(nuevoNodo);
    }
    }
    
    public int hijoDerecho(int i){
        return 2*i+2;
    }
    public int hijoIzquierdo(int i){
        return 2*i+1;
    }
    
    public int padre(int i){
        return (i-1)/2; 
    }
    // actualiza el heap despues de una modificacion 
    public void heapify() {
        for (int i = padre(elementos.size()-1); i >= 0; i--) {
            siftDown(i);
        }   
    }

    // agarra a un estdiante y lo sube lo maximo que puede
    private void siftUp(int i){
        if (i == 0) return;
        int padre = padre(i);
        if (elementos.get(i).compareTo(elementos.get(padre)) < 0) {
            swap(i, padre);
            siftUp(padre); 
        }
    }

    // agarra a un estudiante y lo baja lo maximo que puede
    private void siftDown(int i){
        int izq = hijoIzquierdo(i);
        int der = hijoDerecho(i);
        int menor = i;
        if (izq < elementos.size() && elementos.get(izq).compareTo(elementos.get(menor)) < 0) {
            menor = izq;
        }
        if (der < elementos.size() && elementos.get(der).compareTo(elementos.get(menor)) < 0) {
            menor = der;
        }
        if (menor != i) {
            swap(i, menor);
            siftDown(menor);
        }
    }
    public T sacarRaiz(){
    }

    // intercambia los estudiantes uno a unno
    private void swap(int i, int j) {
        Nodo ni = elementos.get(i);
        Nodo nj = elementos.get(j);
        heap.set(i, nj);
        heap.set(j, ni);
        ni.indice = j;
        nj.indice = i;
    }
}