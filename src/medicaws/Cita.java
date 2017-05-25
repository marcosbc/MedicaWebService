package medicaws;

public class Cita implements java.io.Serializable {
    private int id;
    private String fecha;
    private boolean tomada;
    public Cita () {
    }
    public Cita (int id, String fecha) {
        setId(id);
        setFecha(fecha);
        setTomada(false);
    }
    public void setId (int id) {
        this.id = id;
    }
    public void setFecha (String fecha) {
        this.fecha = fecha;
    }
    public void setTomada (boolean tomada) {
        this.tomada = tomada;
    }
    public int getId () {
        return id;
    }
    public String getFecha () {
        return fecha;
    }
    public Boolean getTomada () {
        return tomada;
    }
}
