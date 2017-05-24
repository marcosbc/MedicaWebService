package medicaclient;

import java.util.Scanner;
import java.util.Arrays;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;

import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;

import medicaws.Cuenta;
import medicaws.Cita;

public class MedicaClient
{
    private static final int AXIS_PORT = 8888;
    private static final String NSURI = "http://www.uc3m.es/WS/Medica";
    private static final String ENDPOINT = "http://localhost:" + AXIS_PORT + "/axis/services/Medica";
    private static final String CMD_CREATEACCOUNT = "/nuevo";
    private static final String CMD_CLOSEACCOUNT = "/baja";
    private static final String CMD_AVAILABLEAPPOINTMENTS = "/disponibles";
    private static final String CMD_NEWAPPOINTMENT = "/cita";
    private static final String CMD_GETDIAGNOSIS = "/diagnosis";
    private static final String CMD_SETDIAGNOSIS = "/setDiagnosis";
    private static final String CMD_EXIT = "/exit";

    public static void main(String [] args) throws Exception {
        String nombre;
        String password;
        Cuenta c = null;
        boolean salir = false;
        Scanner input = new Scanner(System.in);

        // Fase de login
        if ((c = login(input)) == null) {
            System.out.println("Error al iniciar sesion, saliendo...");
            System.exit(1);
        }

        // Bucle infinito de lectura de comandos e impresion de ayuda
        printHelp(c);
        System.out.print("Comando: ");
        while (!salir && input.hasNextLine()){
            String line = input.nextLine().trim();
            String words[] = line.split(" ");
            String command = words[0];
            if (words.length >= 1 && !command.equals("") && command.startsWith("/")) {
                // Comando de salida
                if (command.equals(CMD_EXIT)) {
                    break;
                }
                // Dar usuario de baja
                else if (command.equals(CMD_CLOSEACCOUNT)) {
                    invoca_cerrarCuenta(c);
                    salir = true;
                }
                // Ver huecos disponibles para cita
                else if (command.equals(CMD_AVAILABLEAPPOINTMENTS) && words.length == 1) {
                    invoca_citasDisponibles(c);
                }
                // Crear nueva cita
                else if (command.equals(CMD_NEWAPPOINTMENT) && words.length == 2) {
                    // TODO: Decidir rol
                    invoca_setCita(c, words[1]);
                }
                // Obtener diagnosis por parte del paciente
                else if (command.equals(CMD_GETDIAGNOSIS) && words.length == 1) {
                    invoca_verDiagnostico(c);
                }
                // Obtener diagnosis por parte del medico
                else if (command.equals(CMD_GETDIAGNOSIS) && words.length == 2) {
                    invoca_verDiagnostico(c, words[1]);
                }
                // Actualizar diagnosis por parte del medico
                else if (command.equals(CMD_SETDIAGNOSIS) && words.length > 2 && isMedico(c)) {
                    String[] diagnosis = Arrays.copyOfRange(words, 2, words.length);
                    invoca_setDiagnostico(c, words[1], String.join(" ", diagnosis));
                }
                // Comando no reconocido: Imprimir ayuda
                else {
                    printHelp(c);
                }
            }
            // Caso de comando vacío: Imprimir error
            else {
                System.out.println("Comando no valido");
            }
            if (!salir) {
                System.out.print("Comando: ");
            }
        }
    }

    private static boolean isMedico(Cuenta c) {
        return c != null && c.getRol() == Cuenta.MEDICO;
    }

    private static void printHelp(Cuenta c) {
        String help =
            "Comandos disponibles:\n\n" +
            "  " + CMD_CLOSEACCOUNT + "\n" +
            "  Eliminar tu cuenta de Medica.\n\n" +
            "  " + CMD_AVAILABLEAPPOINTMENTS + "\n" +
            "  Muestra fechas disponibles para coger cita.\n\n" +
            "  " + CMD_NEWAPPOINTMENT + " [fecha]\n" +
            "  Asignar nueva cita en la fecha escogida.\n\n" +
            "  " + CMD_GETDIAGNOSIS + "\n" +
            "  Obtener diagnosis del medico.\n\n";
        if (isMedico(c)) {
            help += "  " + CMD_GETDIAGNOSIS + " [nombre]\n" +
                    "  Obtener diagnosis del paciente con el nombre escogido.\n\n" +
                    "  " + CMD_SETDIAGNOSIS + " [nombre] [diagnosis]\n" +
                    "  Actualiza diagnosis del paciente con el nombre escogido.\n\n";
        }
        help +=
            "  " + CMD_EXIT + "\n" +
            "  Salir del programa\n";
        System.out.println(help);
    }

    private static Cuenta login(Scanner input) {
        Cuenta c = null;
        boolean finLogin = false;
        System.out.println(
                "*** Bienvenido a Medica para el proyecto de SDySW ***\n" +
                "Por favor, indique su nombre de usuario o escriba " + CMD_CREATEACCOUNT + " para crear uno.\n" +
                "En cualquier momento, escriba " + CMD_EXIT + " para salir del programa.\n"
        );
        // Obtener nombre de usuario
        System.out.print("Comando: ");
        while (!finLogin && input.hasNextLine()) {
            String line = input.nextLine().trim();
            String words[] = line.split(" ");
            // Caso de linea vacia: Mostrar mensaje de error
            if (line == null || line.equals("")) {
                System.out.println("Debe introducir un comando v\u00e1lido para el funcionamiento del programa.");
            }
            // Caso de mas de una palabra: Mostrar mensaje de error
            else if (words.length != 1) {
                System.out.println("\nEl nombre de usuario ha de estar formado por una sola palabra.");
            }
            // Caso de salida del programa
            else if (line.equalsIgnoreCase(CMD_EXIT)) {
                finLogin = true;
            }
            // Caso de registro de usuario con el servidor
            else if (line.equalsIgnoreCase(CMD_CREATEACCOUNT)) {
                // Si se ha creado el usuario correctamente, finalizar el login
                if ((c = crearCuenta(input)) != null) {
                    finLogin = true;
                }
            }
            // Validacion; los nombres no pueden empezar por "/" (reservado para comandos)
            else if (line.substring(0, 1).equals("/")) {
                System.out.println("\nEl nombre de usuario no puede empezar por \"/\", es un car\u00e1cter reservado para comandos.\n");
            }
            // Todo bien; pedimos contraseña
            else {
                String username = line;
                System.out.print("Contrase\u00f1a: ");
                String password = System.console().readPassword().toString();
                if ((c = invoca_loginCuenta(username, password)) != null) {
                    finLogin = true;
                } else {
                    System.out.println("Las credenciales no son correctas. Por favor, intente de nuevo.");
                }
            }
            if (!finLogin) {
                System.out.print("Comando: ");
            }
        }
        return c;
    }

    private static Cuenta crearCuenta(Scanner input) {
        Cuenta c = null;
        boolean finCrear = false;
        System.out.println("\nSe est\u00e1 procediendo a la creaci\u00f3n de un nuevo usuario\n");
        // Obtener nombre de usuario
        System.out.print("Nombre de usuario: ");
        while (!finCrear && input.hasNextLine()) {
            String line = input.nextLine().trim();
            String words[] = line.split(" ");
            // Caso de linea vacia: Mostrar mensaje de error
            if (line == null || line.equals("")) {
                System.out.println("Debe introducir un nombre de usuario no vac\u00edo.");
            }
            // Caso "/exit"; salir del programa
            else if (line.equalsIgnoreCase(CMD_EXIT)) {
                finCrear = true;
            }
            // Caso de mas de una palabra: Mostrar mensaje de error
            else if (words.length != 1) {
                System.out.println("\nEl nombre de usuario ha de estar formado por una sola palabra.");
            }
            // Validacion; los nombres no pueden empezar por "/" (reservado para comandos)
            else if (line.substring(0, 1).equals("/")) {
                System.out.println("\nEl nombre de usuario no puede empezar por /, es un car\u00e1cter reservado para comandos");
            }
            // Todo bien; pedimos contraseña
            else {
                String username = line;
                System.out.print("Contrase\u00f1a: ");
                String password = System.console().readPassword().toString();
                c = invoca_crearCuenta(username, password);
                // Si hubo error, se volvera al login
                finCrear = true;
                if (c != null) {
                    System.out.println("\n*** Usuario registrado correctamente ***\n");
                } else {
                    System.out.println("Se produjo un error al registrar la cuenta. Por favor, int\u00e9ntelo de nuevo volviendo a introducir /nuevo, o introduzca /exit para salir del programa.");
                }
            }
            if (!finCrear) {
                System.out.print("Nombre de usuario: ");
            }
        }
        // Si es nulo, no se ha creado el usuario correctamente
        return c;
    }

    private static Cuenta invoca_loginCuenta(String nombre, String password) {
        Cuenta c = null;
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("loginCuenta");
            call.addParameter("nombre", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("password", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(qnCuenta);
            // Retorna objeto de tipo "Cuenta"
            c = (Cuenta) call.invoke(new Object [] { nombre, password });
        } catch (Exception e) {
            System.out.println(e);
        }
        return c;
    }

    private static Cuenta invoca_crearCuenta(String nombre, String password) {
        Cuenta c = null;
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("crearCuenta");
            call.addParameter("nombre", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("password", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(qnCuenta);
            // Retorna objeto de tipo "Cuenta"
            c = (Cuenta) call.invoke(new Object [] { nombre, password });
        } catch (Exception e) {
            System.out.println(e);
        }
        return c;
    }

    private static void invoca_cerrarCuenta(Cuenta c) throws Exception {
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("cerrarCuenta");
            call.addParameter("cuenta", qnCuenta, ParameterMode.IN);
            call.setReturnType(XMLType.AXIS_VOID);
            // Sin retorno de valor
            call.invoke(new Object [] { c });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void invoca_citasDisponibles(Cuenta c) throws Exception {
        String citasDisponibles = null;
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("citasDisponibles");
            call.addParameter("cuenta", qnCuenta, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            // Retorna objeto de tipo "String"
            citasDisponibles = (String) call.invoke(new Object [] { c });
            System.out.println(citasDisponibles);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void invoca_setCita(Cuenta c, String fecha) throws Exception {
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("setCita");
            call.addParameter("cuenta", qnCuenta, ParameterMode.IN);
            call.addParameter("fecha", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.AXIS_VOID);
            // No retorna nada
            call.invoke(new Object [] { c, fecha });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void invoca_verDiagnostico(Cuenta c) throws Exception {
        String diagnostico = null;
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("verDiagnostico");
            call.addParameter("cuenta", qnCuenta, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            // Retorna objeto de tipo "String"
            diagnostico = (String) call.invoke(new Object [] { c });
            System.out.println(diagnostico);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void invoca_verDiagnostico(Cuenta c, String nombre) throws Exception {
        String diagnostico = null;
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("verDiagnostico");
            call.addParameter("cuenta", qnCuenta, ParameterMode.IN);
            call.addParameter("nombre", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            // Retorna objeto de tipo "String"
            diagnostico = (String) call.invoke(new Object [] { c, nombre });
            System.out.println(diagnostico);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void invoca_setDiagnostico(Cuenta c, String nombre, String diagnosis) throws Exception {
        String diagnostico = null;
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qnCuenta,
                                     new BeanSerializerFactory(Cuenta.class, qnCuenta),
                                     new BeanDeserializerFactory(Cuenta.class, qnCuenta));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("setDiagnostico");
            call.addParameter("cuenta", qnCuenta, ParameterMode.IN);
            call.addParameter("nombre", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("diagnostico", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            // No retorna nada
            call.invoke(new Object [] { c, nombre, diagnostico });
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
