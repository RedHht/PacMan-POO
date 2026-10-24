public class Celda {
    private Posicion posicion;
    private boolean transitable;
    private Punto punto;
    
    public Celda(Posicion posicion, boolean transitable){
        this.posicion = posicion;
        this.transitable = transitable;
        this.punto = null;
    }

    public boolean esTransitable(){
        return this.transitable;
    }

    public boolean tienePunto(){
        return this.punto != null;
    }

    public Punto retirarPunto(){
        Punto aux = this.punto;
        this.punto = null;
        return aux;
    }

    public boolean tengoPosicion(Posicion posicion) {
    
    double[] distancia = this.posicion.distanciaA(posicion);
    return distancia[0] == 0 && distancia[1] == 0;
    }

}
