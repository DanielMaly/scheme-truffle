package net.danielmaly.scheme.types;

public class ConsCell {
    private Object car;
    private Object cdr;

    public ConsCell() {
        this(NilValue.NIL, NilValue.NIL);
    }

    public ConsCell(Object car) {
        this(car, NilValue.NIL);
    }

    public ConsCell(Object car, Object cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public Object getCar() {
        return car;
    }

    public void setCar(Object car) {
        this.car = car;
    }

    public Object getCdr() {
        return cdr;
    }

    public void setCdr(Object cdr) {
        this.cdr = cdr;
    }

}
