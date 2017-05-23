package medicaws;

public class Cita implements java.io.Serializable {

    private int id;
    private String fecha;
    private boolean tomada;

    public void setId(int i) {
	this.id = i;
    }
    public void setFecha(String f) {
	this.fecha = f;
    }
    public void setTomada(boolean b){
	this.tomada = b;
    }

    public int getId() {
	return this.id;
    }
    public String getFecha() {
	return this.fecha;
    }
    public Boolean getTomada() {
	return this.tomada;
    }

}
