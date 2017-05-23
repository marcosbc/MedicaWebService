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
    	/********AQUI VA LA CARGA DE CUENTAS Y CITAS, PARA EL RESTO DE LOS METODOS SUPONDRE QUE ASI ES.
	*********ES POSIBLE QUE TENGAMOS QUE CAMBIAR DE VECTOR A LIST SI LOS JSON NO CARGAN CORRECTAMENTE********/
	cuentas = cargarCuentas();
	citas = cargarCitas();
    }

    public void crearCuenta(String nombre, String password) throws Exception {
	if (nombre != null && password != null){
	    if (nombreExiste(nombre) == false){
	        Cuenta c = new Cuenta();
	        c.setNombre(nombre);
	        c.setPassword(password);
		c.setProximaCita("");
		c.setRol(c.PACIENTE);//Solo se pueden crear cuentas de paciente. Las cuentas de médico las crea el admin
	        cuentas.add(c);
		guardarCuentas();
	    } else {
		throw new Exception("¡Ya existe una cuenta con el nombre indicado!");
	    }
	}
	else
	    throw new Exception("¡No se ha indicado nombre o ha habido un error de red!");

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

    public void cerrarCuenta(String nombre, String password) throws Exception {
	boolean result = false;
	Cuenta c = null;

	for (int i=0; i < cuentas.size() && !result; i++) {
	    
	    c = (Cuenta)cuentas.get(i);
	    
	    if (c.getNombre().equals(nombre) && c.getPassword().equals(password)) {
		result = true;
	    }
	}

	if (c == null || result == false){
	    throw new Exception("¡La cuenta no existe!");
  	} else {
	    cuentas.remove(c);
	    guardarCuentas();
	}
    }

    public String citasDisponibles(){
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

    public void setCita(Cuenta cliente, String fecha) throws Exception{
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

    public String verDiagnóstico(Cuenta c){ //Si eres cliente, solo ves tu propio diagnostico. Si eres médico, cualquiera.
	return c.listToString(c.getDiagnostico());
    }

    public String verDiagnóstico(Cuenta c, String n) throws Exception{ //Si eres cliente, solo ves tu propio diagnostico. Si eres médico, cualquiera.
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

    public void setDiagnóstico(Cuenta c, String nombre, String diagnostico) throws Exception{
	
	if (c.getRol() == c.PACIENTE){
	    throw new Exception ("¡Solo los medicos pueden realizar diagnosticos!");
	} else {
	    c.setDiagnostico(diagnostico);
	    guardarCuentas();
	}
    }

    public Vector cargarCuentas(){
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

    public Vector cargarCitas(){
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

    public void guardarCuentas(){
        try {
            JSONSerializer.writeValue(cuentasFile, this.cuentas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarCitas(){
        try {
            JSONSerializer.writeValue(citasFile, this.citas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public Cuenta[] cuentasDelTitular(String dni) throws Exception {

	Vector v = new Vector();
    
	for (int i=0; i < cuentas.size(); i++) {
	    
	    Cuenta c = (Cuenta)cuentas.get(i);
	    
	    if (c.getTitular().getDni().equals(dni)) {
		v.add(c);
	    }
	}

	if (v.size() > 0) {

	    Cuenta vn[] = new Cuenta[v.size()];

	    for (int k=0; k < v.size(); k++) {
		vn[k] = (Cuenta)v.get(k);
	    }
	    
	    return vn;

	}else
	    throw new Exception("DNI no encontrado.");

    }


    public Titular titularDeCuenta(String numCuenta) throws Exception {
	boolean result = false;
	Cuenta c = null;

	for (int i=0; i < cuentas.size() && !result; i++) {	    
	    c = (Cuenta)cuentas.get(i);
	    
	    if (c.getNumCuenta().equals(numCuenta)) {
		result = true;
	    }
	}

	if (c == null || result == false){
	    throw new Exception("¡La cuenta no existe!");
	} else {
	    return c.getTitular();
	}

    }*/

}
