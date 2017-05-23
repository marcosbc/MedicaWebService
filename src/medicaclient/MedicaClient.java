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
        }
    }

    private static boolean isMedico(Cuenta c) {
        return c != null && c.getRol() == Cuenta.MEDICO;
    }

    private static void printHelp(Cuenta c) {
        String help =
            "Comandos disponibles:\n\n" +
            "  " + CMD_CLOSEACCOUNT + "\n\n" +
            "  " + CMD_AVAILABLEAPPOINTMENTS + "\n\n" +
            "  " + CMD_NEWAPPOINTMENT + "\n\n" +
            "  " + CMD_GETDIAGNOSIS + "\n\n";
        if (isMedico(c)) {
            help += "  " + CMD_SETDIAGNOSIS + "\n\n";
        }
        help +=
            "  " + CMD_EXIT + "\n";
        System.out.println(help);
    }

    private static Cuenta login(Scanner input) {
        boolean finLogin = false;
        System.out.println(
                "*** Bienvenido a Medica para el proyecto de SDySW ***\n" +
                "Por favor, indique su nombre de usuario o escriba /nuevo para crear uno.\n" +
                "En cualquier momento, escriba /exit para salir del programa.\n"
        );
        // Obtener nombre de usuario
        System.out.print("Comando: ");
        while (c == null && !finLogin && input.hasNextLine()) {
            String line = input.nextLine().trim();
            String words[] = line.split(" ");
            // Caso de linea vacia: Mostrar mensaje de error
            if (line == null || line.equals("")) {
                Logger.warn("Debe introducir un comando v\u00e1lido para el funcionamiento del programa.");
            }
            // Caso de mas de una palabra: Mostrar mensaje de error
            else if (words.length != 1) {
                Logger.warn("\nEl nombre de usuario ha de estar formado por una sola palabra.");
            }
            // Caso "/exit"; salir del programa
            else if (line.equalsIgnoreCase("/exit")){
                finLogin = true;
            }
            // Caso "/nuevo"; registro de usuario con el servidor
            else if (line.equalsIgnoreCase("/nuevo")){
                // Si se ha creado el usuario correctamente, finalizar el login
                c = crearCuenta(input);
            }
            // Validacion; los nombres no pueden empezar por "/" (reservado para comandos)
            else if (line.substring(0, 1).equals("/")) {
                Logger.warn("\nEl nombre de usuario no puede empezar por \"/\", es un car\u00e1cter reservado para comandos.\n");
            }
            // Todo bien; pedimos contraseña
            else {
                username = line;
                Logger.prompt("Contrase\u00f1a: ");
                password = System.console().readPassword().toString();
                c = invoca_login(username, password);
            }
        }
        return c;
    }

    private static Cuenta crearCuenta(Scanner input) {
        Cuenta c = null;

        return c;
    }

    private static Cuenta invoca_loginCuenta(String nombre, String password) {
        Cuenta c = null;

        return c;
    }

    private static Cuenta invoca_crearCuenta(String nombre, String password) {
        Cuenta c = null;
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qn,
                                     new BeanSerializerFactory(Cuenta.class, qn),
                                     new BeanDeserializerFactory(Cuenta.class, qn));
            // Configurar la llamada
            call.setTargetEndpointAddress(new java.net.URL(ENDPOINT));
            call.setOperationName("cerrarCuenta");
            call.addParameter("nombre", XMLType.AXIS_STRING, ParameterMode.IN);
            call.addParameter("password", XMLType.AXIS_STRING, ParameterMode.IN);
            call.setReturnType(qnCuenta);
            // Retorna clase de tipo "Cuenta"
            c = (Cuenta) call.invoke(new Object [] { nombre, password });
        } catch (Exception e) {
            System.out.println(e);
        }
        return c;
    }

    private static void invoca_cerrarCuenta(Cuenta c) throws Exception {
        System.out.println("cerrarCuenta: " + c);
        try {
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            QName    qnCuenta = new QName(NSURI, "Cuenta");
            // Registrar tipos
            call.registerTypeMapping(Cuenta.class, qn,
                                     new BeanSerializerFactory(Cuenta.class, qn),
                                     new BeanDeserializerFactory(Cuenta.class, qn));
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
        System.out.println("citasDisponibles: " + c);
    }

    private static void invoca_setCita(Cuenta c, String fecha) throws Exception {
        System.out.println("setCita: " + c + ", " + fecha);
    }

    private static void invoca_verDiagnostico(Cuenta c) throws Exception {
        System.out.println("verDiagnostico: " + c);
    }

    private static void invoca_verDiagnostico(Cuenta c, String nombre) throws Exception {
        System.out.println("verDiagnostico: " + c + ", " + nombre);
    }

    private static void invoca_setDiagnostico(Cuenta c, String nombre, String diagnosis) throws Exception {
        System.out.println("setDiagnostico: " + c + ", " + nombre + ", \"" + diagnosis + "\"");
    }
}
