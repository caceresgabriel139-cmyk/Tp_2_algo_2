package aed;
import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
    private ArrayList<T> heap;
    
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
        Nodo nuevoNodo = new Nodo(elem);
        heap.add(elem);
        siftUp(nuevoNodo.indice);
        return new HeapHandle(nuevoNodo);
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

    public void Heapify() {
    for (int i = padre(heap.size()-1); i >= 0; i--) {
        siftDown(i);
    }   
    }

    private void siftUp(int i){

        if (i == 0) return;

        int padre = padre(i);

        if (heap.get(i).compareTo(heap.get(padre)) < 0) {
            swap(i, padre);

            siftUp(padre); 

        }
    }

     private void siftDown(int i){

        int izq = hijoIzquierdo(i);
        int der = hijoDerecho(i);
        int menor = i;

        if (izq < heap.size() && heap.get(izq).compareTo(heap.get(menor)) < 0) {
            menor = izq;
        }
        if (der < heap.size() && heap.get(der).compareTo(heap.get(menor)) < 0) {
            menor = der;
        }

        if (menor != i) {
            swap(i, menor);
            siftDown(menor);
        }

        
    }
    public T sacarRaiz(){
        

        return ;
    } 
    private void swap(int i, int j){
        T elem = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, elem);
    }
}
// @Override
//     public int compareTo(Estudiante e) {
//         // no entregó primero
//         if (this.entrego != e.entrego) {
//             return this.entrego ? 1 : -1;
//         }

//         //menor nota primero
//         if (this.nota != e.nota) {
//             return Integer.compare(this.nota, e.nota);
//         }

//         //menor id primero
//         return Integer.compare(this.id, e.id);
//     }