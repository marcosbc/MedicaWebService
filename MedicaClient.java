import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;

import medicaws.*;

public class MedicaClient
{

    static String endpoint = "http://localhost:8888/axis/services/Medica";

    public static void main(String [] args) throws Exception {

	String nombre;
	String password;
	Cuenta c = null;
	Scanner input = new Scanner(System.in);
	boolean salir = false;

	//FASE DE LOGIN
	c = login(); //Metodo Login no desarrollado aun.

	//LOGIN FALLIDO (LOGUEO INEXISTENTE O CREADA EXISTENTE)
	if (c == null){
	    System.exit(1);
	}

	//RESTO DEL PROGRAMA. BUCLE INFINITO DE LECTURA DE COMANDOS E IMPRESION DE AYUDA
	
	printHelp();
	while (salir == false && input.hasNextLine()){
            String line = input.nextLine().trim();
            String words[] = line.split(" ");
	}

	/************HASTA AQUÍ TENGO HECHO. NO QUIERO BORRAR
		POR SI HAY CÓDIGO UTIL DETRÁS DE ESTO. POR EJEMPLO
		LA INVOCACIÓN DE LOS MÉTODOS Y LOS QN***************/
	if (args.length < 1){
	System.out.println("Error en el paso de parametros");
	System.out.println("Métodos disponibles: crearcuenta, cerrarcuenta, citasdisponibles, proximacita, reservarcita, verdiagnostico y hacerdiagnostico\nIntroducir cualquiera de ellos para recibir instrucciones específicas.");
	System.exit(1);

	}

	if (args[0].equalsIgnoreCase("crearcuenta")) {
	    if (args.length == 3) {

		nombre = args[1];
		password = args[2];

		titular = new Titular();
		titular.setNombre(nombre);
		titular.setDni(dni);

		try {
		    invoca_crear(numCuenta, titular);
		    System.exit(0);
		}
		catch (Exception ex) {
		    System.out.println(ex);
		    System.exit(1);
		}
	    } else {
		System.out.println("Error en el paso de parametros");
		System.out.println("Uso: medicaclient crearcuenta nombre password");
		System.exit(1);
	    }
	}


	if (args[0].equalsIgnoreCase("cerrarcuenta")) {
	    if (args.length == 2) {
		numCuenta = args[1];
		try {
		    invoca_cerrar(numCuenta);
		    System.exit(0);
		}
		catch (Exception ex) {
		    System.out.println(ex);
		    System.exit(1);
		}
	    }
	    else {
		System.out.println("Error en el paso de parametros");
		System.out.println("Uso: bancoclient cerrarcuenta numCuenta");
		System.exit(1);
	    }
	}

	if (args[0].equalsIgnoreCase("ingresar")) {
	    if (args.length == 3) {
		numCuenta = args[1];
		cantidad = Integer.parseInt(args[2]);
		try {
		    invoca_ingresar(numCuenta, cantidad);
		    System.exit(0);
		}
		catch (Exception ex) {
		    System.out.println(ex);
		    System.exit(1);
		}
	    }
	    else {
		System.out.println("Error en el paso de parametros");
		System.out.println("Uso: bancoclient ingresar numCuenta cantidad");
		System.exit(1);
	    }
	}

	if (args[0].equalsIgnoreCase("retirar")) {
	    if (args.length == 3) {
		numCuenta = args[1];
		cantidad = Integer.parseInt(args[2]);
		try {
		    invoca_retirar(numCuenta, cantidad);
		    System.exit(0);
		}
		catch (Exception ex) {
		    System.out.println(ex);
		    System.exit(1);
		}
	    }
	    else {
		System.out.println("Error en el paso de parametros");
		System.out.println("Uso: bancoclient retirar numCuenta cantidad");
		System.exit(1);
	    }
	}

	if (args[0].equalsIgnoreCase("consultarcuentas")) {
	    if (args.length == 2) {
		dni = args[1];
		try {
		    invoca_consultarcuentas(dni);
		    System.exit(0);
		}
		catch (Exception ex) {
		    System.out.println(ex);
		    System.exit(1);
		}
	    }
	    else {
		System.out.println("Error en el paso de parametros");
		System.out.println("Uso: bancoclient consultarcuentas dni");
		System.exit(1);
	    }
	}


	if (args[0].equalsIgnoreCase("consultartitular")) {
	    if (args.length == 2) {
		numCuenta = args[1];
		try {
		    invoca_consultartitular(numCuenta);
		    System.exit(0);
		}
		catch (Exception ex) {
		    System.out.println(ex);
		    System.exit(1);
		}
	    }
	    else {
		System.out.println("Error en el paso de parametros");
		System.out.println("Uso: bancoclient consultartitular numCuenta");
		System.exit(1);
	    }
	}


	System.out.println("Error en el paso de parametros");
	System.out.println("Métodos disponibles: crearcuenta, cerrarcuenta, ingresar, retirar, consultarcuentas y consultartitular\nIntroducir cualquiera de ellos para recibir instrucciones específicas.");
	System.exit(1);
    }
    
    
    private static void invoca_crear(String numCuenta, Titular titular) {
	
	try {

	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    QName    qn       = new QName( "http://www.uc3m.es/WS/Banco", "Titular" );


	    call.registerTypeMapping(bancows.Titular.class, qn,
				     new org.apache.axis.encoding.ser.BeanSerializerFactory(bancows.Titular.class, qn),        
				     new org.apache.axis.encoding.ser.BeanDeserializerFactory(bancows.Titular.class, qn)); 
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName("crearCuenta");
            call.addParameter("numCuenta", XMLType.XSD_STRING, ParameterMode.IN);
	    call.addParameter("titular", qn, ParameterMode.IN);	    
	    call.setReturnType(XMLType.AXIS_VOID);
	    call.invoke(new Object [] { numCuenta,titular });

	}
	catch (Exception ex) {

	    System.out.println(ex);
	}
       
    }


    private static void invoca_cerrar(String numCuenta) {
	
	try {

	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName("cerrarCuenta");
            call.addParameter("numCuenta", XMLType.XSD_STRING, ParameterMode.IN);   
	    call.setReturnType(XMLType.AXIS_VOID);
	    call.invoke(new Object [] { numCuenta });

	}
	catch (Exception ex) {

	    System.out.println(ex);
	}
       
    }


    private static void invoca_ingresar(String numCuenta, int cantidad) {
	
	try {

	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName("ingresar");
            call.addParameter("numCuenta", XMLType.XSD_STRING, ParameterMode.IN);
	    call.addParameter("cantidad", XMLType.XSD_INTEGER, ParameterMode.IN);	    
	    call.setReturnType(XMLType.AXIS_VOID);
	    call.invoke(new Object [] { numCuenta,cantidad });

	}
	catch (Exception ex) {

	    System.out.println(ex);
	}
       
    }


    private static void invoca_retirar(String numCuenta, int cantidad) {
	
	try {

	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName("retirar");
            call.addParameter("numCuenta", XMLType.XSD_STRING, ParameterMode.IN);
	    call.addParameter("cantidad", XMLType.XSD_INTEGER, ParameterMode.IN);	    
	    call.setReturnType(XMLType.AXIS_VOID);
	    call.invoke(new Object [] { numCuenta,cantidad });

	}
	catch (Exception ex) {

	    System.out.println(ex);
	}
       
    }
    
    
    private static void invoca_consultarcuentas(String dni) {
	
	try {
	    
	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    QName    qn      = new QName( "http://www.uc3m.es/WS/Banco", "Cuenta" );
	    QName    qna     = new QName( "http://www.uc3m.es/WS/Agenda", "ArrayOfCuenta" );
	    QName    qnt     = new QName( "http://www.uc3m.es/WS/Banco", "Titular" );

	    call.registerTypeMapping(bancows.Titular.class, qn,
				     new org.apache.axis.encoding.ser.BeanSerializerFactory(bancows.Titular.class, qn),        
				     new org.apache.axis.encoding.ser.BeanDeserializerFactory(bancows.Titular.class, qn)); 

	    
	    call.registerTypeMapping(bancows.Cuenta.class, qn,
				     new org.apache.axis.encoding.ser.BeanSerializerFactory(bancows.Cuenta.class, qn),        
				     new org.apache.axis.encoding.ser.BeanDeserializerFactory(bancows.Cuenta.class, qn)); 
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName("cuentasDelTitular");
	    call.addParameter("dni", XMLType.XSD_STRING, ParameterMode.IN );
	    
	    call.setReturnType(qna);
	    
	    Cuenta obj[] = (Cuenta [])call.invoke(new Object [] { dni });

	    for (int k=0; k < obj.length; k++) {
		
		Cuenta c = obj[k];

		System.out.println("N. Cuenta: " + c.getNumCuenta());
		System.out.println("Saldo: " + c.getBalance());
		System.out.println("Nombre: " + c.getTitular().getNombre());
		System.out.println("DNI: " + c.getTitular().getDni());
	    }

	}
	catch (Exception e) {

	    System.out.println(e);
	}
    }


    private static void invoca_consultartitular(String numCuenta) {
	
	try {
	    
	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    QName    qn      = new QName( "http://www.uc3m.es/WS/Banco", "Titular" );
	    
	    call.registerTypeMapping(bancows.Titular.class, qn,
				     new org.apache.axis.encoding.ser.BeanSerializerFactory(bancows.Titular.class, qn),        
				     new org.apache.axis.encoding.ser.BeanDeserializerFactory(bancows.Titular.class, qn)); 
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName("titularDeCuenta");
	    call.addParameter("numCuenta", XMLType.XSD_STRING, ParameterMode.IN );
	    
	    call.setReturnType(qn);
	    
	    Titular t = (Titular)call.invoke(new Object [] { numCuenta });

		System.out.println("Nombre: " + t.getNombre());
		System.out.println("DNI: " + t.getDni());
	    

	}
	catch (Exception e) {

	    System.out.println(e);
	}
    }

}
