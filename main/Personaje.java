public class Personaje {
    private Posicion posicion;
    private Posicion posicionInicial;
    private Direccion direccionActual;

    public Personaje(Posicion posicionInicial){
        this.posicionInicial = posicionInicial;
        this.posicion = posicionInicial;
        this.direccionActual = Direccion.ARRIBA;
    }

    public void volverPosicionInicial(){
        this.posicion = this.posicionInicial;
    }

    public Posicion getPosicion(){
        return this.posicion;
    }

    protected void cambiarPosicion(Posicion nuevaPosicion){
        this.posicion = nuevaPosicion;
    }

    protected void cambiarDireccion(Direccion nuevaDireccion){
        this.direccionActual = nuevaDireccion;
    }

    protected Direccion getDireccionActual(){
        return this.direccionActual;
    }

    protected Posicion getPosicionInicial(){
        return this.posicionInicial;
    }
}
