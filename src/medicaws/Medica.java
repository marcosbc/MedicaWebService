package medicaws;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.*;

public class Medica {
    ObjectMapper JSONSerializer = null;
    private List<Cuenta> cuentas = null;
    private List<Cita> citas = null;
    File cuentasFile = null;
    File citasFile = null;

    public Medica() {
        // Cargar cuentas y citas
        // Esto emula el funcionamiento de una base de datos
        cuentas = cargarCuentas();
        citas = cargarCitas();
    }

    private Cuenta findCuenta (String name) {
        for (Cuenta cuenta: cuentas) {
            if (cuenta.getName().equals(name)) {
                return cuenta;
            }
        }
        return null;
    }

    private Cuenta findCuenta (Cuenta c) {
        for (Cuenta cuenta: cuentas) {
            if (cuenta.equals(c)) {
                return cuenta;
            }
        }
        return null;
    }

    private Cita findCita (String fecha) {
        for (Cita c: citas) {
            if (c.getFecha().equals(fecha)) {
                return c;
            }
        }
        return null;
    }

    private void validarCuenta (Cuenta c) throws Exception {
        Cuenta cuenta = findCuenta(c.getName());
        if (cuenta == null) {
            throw new Exception("\u00A1La cuenta no existe!");
        }
        if (!cuenta.equals(c)) {
            throw new Exception("\u00A1Las credenciales no son correctas!");
        }
    }

    public Cuenta crearCuenta(String nombre, String password) throws Exception {
        Cuenta c = null;
        if (nombre != null && password != null){
            if (findCuenta(nombre) == null) {
                c = new Cuenta(nombre, password);
                cuentas.add(c);
                guardarCuentas();
            } else {
                throw new Exception("\u00A1Ya existe una cuenta con el nombre indicado!");
            }
        }
        else
            throw new Exception("\u00A1No se ha indicado nombre o ha habido un error de red!");
        return c;
    }

    public Cuenta loginCuenta(String nombre, String password) throws Exception {
        if (nombre != null && password != null) {
            for(Cuenta cuenta: cuentas) {
                if (cuenta.checkCredentials(nombre, password)) {
                    return cuenta;
                }
            }
        }
        return null;
    }

    public void cerrarCuenta(Cuenta c) throws Exception {
        Cuenta cuenta = findCuenta(c);
        validarCuenta(cuenta);
        cuentas.remove(cuenta);
        // Desasignar cita actual del usuario, si la hay
        Cita citaAnterior = findCita(cuenta.getProximaCita());
        if (citaAnterior != null) {
            citaAnterior.setTomada(false);
        }
        // TODO?
        guardarCuentas();
    }

    public String citasDisponibles(Cuenta c) throws Exception {
        Cuenta cuenta = findCuenta(c);
        validarCuenta(cuenta);
        String citasDisponibles = new String();
        // Popular variable resultado
        for (Cita cita: citas) {
            if (!cita.getTomada()) {
                citasDisponibles += cita.getFecha() + "\n";
            }
        }
        return citasDisponibles;
    }

    public void setCita(Cuenta c, String fecha) throws Exception {
        Cuenta cuenta = findCuenta(c);
        validarCuenta(cuenta);
        Cita cita = findCita(fecha);
        if (cita == null) {
            throw new Exception("\u00A1No existen citas en la fecha indicada!");
        }
        if (cita.getTomada()) {
            throw new Exception ("\u00A1La cita ya está tomada!");
        }
        else {
            cita.setTomada(true);
            // Si ya habia una cita asignada, des-asignarla
            Cita citaAnterior = findCita(cuenta.getProximaCita());
            if (citaAnterior != null) {
                citaAnterior.setTomada(false);
            }
            cuenta.setProximaCita(cita.getFecha());
            // TODO?
            guardarCitas();
            guardarCuentas();
        }
    }

    public String citasPropias(Cuenta c) throws Exception {
        Cuenta cuenta = findCuenta(c);
        validarCuenta(cuenta);
        return cuenta.getProximaCita();
    }

    //Si eres cliente, solo ves tu propio diagnostico. Si eres médico, cualquiera
    public String verDiagnostico(Cuenta c) throws Exception {
        Cuenta cuenta = findCuenta(c);
        validarCuenta(cuenta);
        return cuenta.getDiagnostico();
    }

    //Si eres cliente, solo ves tu propio diagnostico. Si eres médico, cualquiera
    public String verDiagnostico(Cuenta c, String n) throws Exception {
        // Obtener medico de parametros
        Cuenta medico = findCuenta(c);
        validarCuenta(medico);
        // Obtener paciente de parametros
        Cuenta paciente = findCuenta(n);
        validarCuenta(paciente);
        if (medico.getRol() != Cuenta.MEDICO) {
            throw new Exception ("\u00A1Solo los medicos pueden consultar historiales de otras personas!");
        }
        return paciente.getDiagnostico();
    }

    public void setDiagnostico(Cuenta c, String n, String diagnostico) throws Exception {
        // Obtener medico de parametros
        Cuenta medico = findCuenta(c);
        validarCuenta(medico);
        // Obtener paciente de parametros
        Cuenta paciente = findCuenta(n);
        validarCuenta(paciente);
        if (medico.getRol() != Cuenta.MEDICO) {
            throw new Exception ("\u00A1Solo los medicos pueden realizar diagnosticos!");
        }
        paciente.setDiagnostico(diagnostico);
        // TODO?
        guardarCuentas();
    }

    private List<Cuenta> cargarCuentas(){
        cuentasFile = new File ("./cuentas.json");
        JSONSerializer = new ObjectMapper();
        List<Cuenta> lc = new ArrayList<Cuenta>();
        List<Cuenta> v = new ArrayList<Cuenta>();


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

    private List<Cita> cargarCitas(){
        citasFile = new File ("./citas.json");
        JSONSerializer = new ObjectMapper();
        List<Cita> lc = new ArrayList<Cita>();
        List<Cita> v = new ArrayList<Cita>();

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
