public class Pacman extends Personaje{
    private Direccion direccionSolicitada;
    
    public Pacman(Posicion posicionInicial){
        super(posicionInicial);
        this.direccionSolicitada = Direccion.ARRIBA;
    }

    public void solicitarDireccion(Direccion direccion){
        this.direccionSolicitada = direccion;
    }

    // public void mover(Laberinto laberinto){}
}
