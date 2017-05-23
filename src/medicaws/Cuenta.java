package medicaws;

import java.util.*;

public class Cuenta implements java.io.Serializable {
    public static final int PACIENTE = 0;
    public static final int MEDICO   = 1;

    private String nombre;
    private String password;
    private String proximaCita;

    private List<String> diagnostico = new ArrayList<String>();
    private int rol;

    /************NO LIMITAMOS EL NOMBRE COMO EN EL CHAT PARA
      PERMITIR NOMBRES COMPLETOS******************/
    public void setNombre(String n) {
        this.nombre = n;
    }
    public void setPassword(String pw){
        this.password = pw;
    }

    /**************EL CLIENTE NO TENDRÁ VARIAS CITAS, SOLO UNA
      (LA PROXIMA CITA)***************************/
    public void setProximaCita(String pc) {
        this.proximaCita = pc;
    }

    /*********LOS DIAGNOSTICOS SE AÑADEN AL FINAL, NUNCA SE BORRAN, COMO EL 
      HISTORIAL REAL DE UN PACIENTE**********/
    public void setDiagnostico(String d) {
        this.diagnostico.add(d);
    }
    public void setRol(int r){
        this.rol = r;
    }
    public String getNombre() {
        return this.nombre;
    }
    public String getPassword(){
        return this.password;
    }
    public String getProximaCita() {
        return this.proximaCita;
    }
    public List<String> getDiagnostico() {
        return this.diagnostico;
    }
    public int getRol () {
        return this.rol;
    }
    public String listToString (List<String> ls){
        String result = new String();
        Iterator<String> it = ls.iterator();

        while (it.hasNext()){
            String s = it.next();
            result += s+"\n";
        }
        return result;
    }
    public boolean equals(Cuenta c) {
        return nombre.equals(c.getNombre()) && password.equals(c.getPassword());
    }
}
