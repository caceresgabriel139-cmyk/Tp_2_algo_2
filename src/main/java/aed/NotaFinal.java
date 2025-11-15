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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotaFinal notaFinal = (NotaFinal) o;
        return Double.compare(notaFinal._nota, _nota) == 0 && _id == notaFinal._id;
    }
}
