package aed;

public class NotaFinal implements Comparable<NotaFinal> {
    public double _nota;
    public int _id;

    public NotaFinal(double nota, int id) {
        _nota = nota;
        _id = id;
    }

    public int compareTo(NotaFinal otra) {
        if (otra._id != this._id) {
            return this._id - otra._id;
        }
        return Double.compare(this._nota, otra._nota);
    }

    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NotaFinal notaFinal = (NotaFinal) o;
    boolean notaIgual = Math.abs(notaFinal._nota - _nota) < 1e-9;
    boolean idIgual = _id == notaFinal._id;
    System.out.println("Comparando: " + _nota + "," + _id + " vs " + notaFinal._nota + "," + notaFinal._id + " -> notaIgual: " + notaIgual + ", idIgual: " + idIgual);
    return notaIgual && idIgual;
}


}
