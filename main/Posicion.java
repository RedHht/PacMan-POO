public class Posicion {
    private double x;
    private double y;

    public double getX(){return x;}

    public double getY(){return y;}

    public double[] distanciaA(Posicion other){

        double distanciaX = Math.abs(this.x - other.getX());
        double distanciaY = Math.abs(this.y - other.getY());
        double[] vectorDistancia = {distanciaX, distanciaY};
        return vectorDistancia;
 }  
}
