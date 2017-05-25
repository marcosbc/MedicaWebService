package medicaws;

import java.util.*;

public class Cuenta implements java.io.Serializable {
    // Constantes
    public static final int PACIENTE = 0;
    public static final int MEDICO   = 1;
    // Parametros identificativos de la cuenta
    private String name;
    private String password;
    private int rol;
    // Parametros de utilidad al programa
    private String proximaCita;
    private String diagnostico;
    public Cuenta () {
        diagnostico = "";
    }
    public Cuenta (String name, String password) {
        setName(name);
        setPassword(password);
        // Solo se pueden crear cuentas de pacientes, las cuentas de medico las crea el administrador
        setRol(PACIENTE);
        setProximaCita("");
        diagnostico = "";
    }
    // No se limita el nombre para permitir nombres completos
    public void setName (String name) {
        this.name = name;
    }
    public void setPassword (String password) {
        this.password = password;
    }
    // El cliente no tendra varias citas, solo una
    public void setProximaCita (String proximaCita) {
        this.proximaCita = proximaCita;
    }
    // Los diagnosticos se agregan al final, nunca se borran
    // Como el historial de un paciente
    public void setDiagnostico (String diagnostico) {
        this.diagnostico = diagnostico;
    }
    public void addDiagnostico (String diagnostico) {
        boolean primerDiagnostico = false;
        if (this.diagnostico.equals("")) {
            primerDiagnostico = true;
        }
        this.diagnostico = new String(
                (new Date()).toString() + "\n" +
                diagnostico + "\n" +
                (primerDiagnostico ? "" : "-----------------------------\n" + this.diagnostico)
        );
    }
    public void setRol (int r){
        this.rol = r;
    }
    public String getName () {
        return this.name;
    }
    public String getPassword () {
        return this.password;
    }
    public String getProximaCita () {
        return this.proximaCita;
    }
    public String getDiagnostico () {
        return this.diagnostico;
    }
    public int getRol () {
        return this.rol;
    }
    // Convertir objeto List<String> a un objeto String
    public String listToString (List<String> ls) {
        String result = new String();
        Iterator<String> it = ls.iterator();
        while (it.hasNext()) {
            String s = it.next();
            result += s+"\n";
        }
        return result;
    }
    public boolean checkCredentials(String name, String password) {
        return this.name.equals(name) && this.password.equals(password);
    }
    public boolean equals(Cuenta c) {
        return checkCredentials(c.getName(), c.getPassword());
    }
}
