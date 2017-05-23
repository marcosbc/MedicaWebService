package medicaws;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.*;

public class Medica {
    ObjectMapper JSONSerializer = null;
    private Vector cuentas = null;
    private Vector citas = null;
    File cuentasFile = null;
    File citasFile = null;

    public Medica() {
        // Cargar cuentas y citas
        // Esto emula el funcionamiento de una base de datos
        // TODO: ES POSIBLE QUE TENGAMOS QUE CAMBIAR DE VECTOR A LIST SI LOS JSON NO CARGAN CORRECTAMENTE
        cuentas = cargarCuentas();
        citas = cargarCitas();
    }

    public Cuenta crearCuenta(String nombre, String password) throws Exception {
        Cuenta c = null;
        if (nombre != null && password != null){
            if (nombreExiste(nombre) == false){
                c = new Cuenta();
                c.setNombre(nombre);
                c.setPassword(password);
                c.setProximaCita("");
                // Solo se pueden crear cuentas de paciente. Las cuentas de médico las crea el admin
                c.setRol(c.PACIENTE);
                cuentas.add(c);
                guardarCuentas();
            } else {
                throw new Exception("¡Ya existe una cuenta con el nombre indicado!");
            }
        }
        else
            throw new Exception("¡No se ha indicado nombre o ha habido un error de red!");
        return c;
    }

    public boolean nombreExiste(String n){
        boolean result = false;
        Cuenta c = null;

        for (int i=0; i < cuentas.size() && !result; i++) {

            c = (Cuenta)cuentas.get(i);

            if (c.getNombre().equals(n)) {
                result = true;
            }
        }
        return result;
    }

    public void cerrarCuenta(Cuenta c) throws Exception {
        boolean found = false;
        Cuenta c = null;

        for (int i=0; i < cuentas.size() && !result; i++) {

            iterador = (Cuenta) cuentas.get(i);

            if (iterador.equals(c)) {
                found = true;
            }
        }

        if (!found) {
            throw new Exception("¡La cuenta no existe!");
        } else {
            cuentas.remove(iterador);
            // TODO?
            guardarCuentas();
        }
    }

    public String citasDisponibles() {
        Cita c = new Cita();
        String s = new String();

        for (int i=0; i < citas.size(); i++) {

            c = (Cita)citas.get(i);

            if (c.getTomada() == false) {
                s += c.getFecha()+"\n";
            }
        }

        return s;
    }

    public void setCita(Cuenta cliente, String fecha) throws Exception {
        boolean result = false;	

        for (int i=0; i < citas.size() && !result; i++) {
            Cita c;
            c = (Cita)citas.get(i);

            if (c.getFecha().equals(fecha)) {
                result = true;
                if(c.getTomada() == false){
                    if (nombreExiste(cliente.getNombre())){
                        c.setTomada(true);
                        guardarCitas();	
                        cliente.setProximaCita(c.getFecha());
                        guardarCuentas();

                    } else {
                        throw new Exception ("¡El cliente especificado no existe!");
                    }

                } else{
                    throw new Exception ("¡La cita ya está tomada!");
                }
            }
        }

        if (result == false){
            throw new Exception("¡No existen citas en la fecha indicada!");
        }
    }

    public String citasPropias(Cuenta c){
        return c.getProximaCita();
    }

    //Si eres cliente, solo ves tu propio diagnostico. Si eres médico, cualquiera
    public String verDiagnostico(Cuenta c) {
        return c.listToString(c.getDiagnostico());
    }

    //Si eres cliente, solo ves tu propio diagnostico. Si eres médico, cualquiera
    public String verDiagnostico(Cuenta c, String n) throws Exception{
        boolean result = false;

        if (c.getRol() == c.PACIENTE){
            throw new Exception ("¡Solo los medicos pueden consultar historiales de otras personas!");
        } else {
            for (int i=0; i < cuentas.size() && !result; i++) {

                c = (Cuenta)cuentas.get(i);

                if (c.getNombre().equals(n)) {
                    result = true;
                }
            }

            if (result == true && c != null){
                return c.listToString(c.getDiagnostico());	
            } else {
                throw new Exception("¡El paciente especificado no existe!");
            }
        }
    }

    public void setDiagnostico(Cuenta c, String nombre, String diagnostico) throws Exception{

        if (c.getRol() == c.PACIENTE){
            throw new Exception ("¡Solo los medicos pueden realizar diagnosticos!");
        } else {
            c.setDiagnostico(diagnostico);
            guardarCuentas();
        }
    }

    private Vector cargarCuentas(){
        cuentasFile = new File ("./cuentas.json");
        JSONSerializer = new ObjectMapper();
        List<Cuenta> lc = new ArrayList<Cuenta>();
        Vector v = new Vector();


        try {
            if (cuentasFile.exists() && (cuentasFile.length() != 0)) {
                lc = JSONSerializer.readValue(cuentasFile, new TypeReference<List<Cuenta>>() {});
                Iterator<Cuenta> itc = lc.iterator();
                while (itc.hasNext()){
                    Cuenta c = itc.next();
                    v.add(c);
                }
            } else {
                cuentasFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private Vector cargarCitas(){
        citasFile = new File ("./citas.json");
        JSONSerializer = new ObjectMapper();
        List<Cita> lc = new ArrayList<Cita>();
        Vector v = new Vector();

        try {
            if (citasFile.exists() && (citasFile.length() != 0)) {
                lc = JSONSerializer.readValue(citasFile, new TypeReference<List<Cita>>() {});
                Iterator<Cita> itc = lc.iterator();
                while (itc.hasNext()){
                    Cita c = itc.next();
                    v.add(c);
                }
            } else {
                citasFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private void guardarCuentas(){
        try {
            JSONSerializer.writeValue(cuentasFile, this.cuentas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void guardarCitas(){
        try {
            JSONSerializer.writeValue(citasFile, this.citas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
