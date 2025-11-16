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
    public boolean equals(Object otra) {
        if(otra==null || otra.getClass()!=this.getClass()){
            return false;
        }
        NotaFinal otraNota = (NotaFinal) otra;
        return this._id == otraNota._id && Double.compare(this._nota, otraNota._nota) == 0;
    }
}
