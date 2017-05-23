package medicaclient;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;

import medicaws.*;

public class MedicaClient
{
    private static final int AXIS_PORT = 8888;
    private static final String ENDPOINT = "http://localhost:" + AXIS_PORT + "/axis/services/Medica";
    private static final String CMD_CLOSEACCOUNT = "/baja";
    private static final String CMD_AVAILABLEAPPOINTMENTS = "/disponibles";
    private static final String CMD_NEWAPPOINTMENT = "/nuevo";
    private static final String CMD_GETDIAGNOSIS = "/info";
    private static final String CMD_SETDIAGNOSIS = "/diagnosis";
    private static final String CMD_EXIT = "/exit";

    public static void main(String [] args) throws Exception {
        String nombre;
        String password;
        Cuenta c = null;
        boolean salir = false;
        Scanner input = new Scanner(System.in);

        // Fase de login
        if ((c = login()) == null) {
            System.exit(1);
        }

        // Bucle infinito de lectura de comandos e impresion de ayuda
        printHelp();
        while (!salir && input.hasNextLine()){
            String line = input.nextLine().trim();
            String words[] = line.split(" ");
            String command = words[0];
            if (words.length >= 1 && !command.equals("") && command.startsWith("#")) {
                // Dar usuario de baja
                if (command.equals(CMD_CLOSEACCOUNT)) {
                    invoca_cerrarCuenta(c);
                }
                // Ver huecos disponibles para cita
                else if (command.equals(CMD_AVAILABLEAPPOINTMENTS) && words.length == 1) {
                    invoca_citasDisponibles(c);
                }
                // Crear nueva cita
                else if (command.equals(CMD_NEWAPPOINTMENT) && words.length == 2) {
                    // TODO: Decidir rol
                    invoca_setCita(c);
                }
                // Obtener diagnosis por parte del paciente
                else if (command.equals(CMD_GETDIAGNOSIS) && words.length == 1) {
                    invoca_verDiagnostico(c);
                }
                // Obtener diagnosis por parte del medico
                else if (command.equals(CMD_GETDIAGNOSIS) && words.length == 2) {
                    invoca_verDiagnostico(c, words[1], words[2]);
                }
                // Actualizar diagnosis por parte del medico
                else if (command.equals(CMD_SETDIAGNOSIS) && words.length > 2) {
                    String[] diagnosis = Arrays.copyOfRange(words, 2, words.length - 1);
                    invoca_setDiagnostico(c, words[1], diagnosis);
                }
                // Comando no reconocido: Imprimir ayuda
                else {
                    printHelp();
                }
            }
            // Caso de comando vacío: No hacer nada
        }
    }

    private static void printHelp() {
        System.out.println(
                "Comandos disponibles:\n\n" +
                "  " + CMD_CLOSEACCOUNT + "\n\n" +
                "  " + CMD_AVAILABLEAPPOINTMENTS + "\n\n" +
                "  " + CMD_NEWAPPOINTMENT + "\n\n" +
                "  " + CMD_GETDIAGNOSIS + "\n\n" +
                "  " + CMD_SETDIAGNOSIS + "\n\n" +
                "  " + CMD_EXIT + "\n\n"
        );
    }

    private static void invoca_cerrarCuenta(Cuenta c) {
        System.out.println("cerrarCuenta: " + c);
    }

    private static void invoca_citasDisponibles(Cuenta c) {
        System.out.println("citasDisponibles: " + c);
    }

    private static void invoca_setCita(Cuenta c, String fecha) {
        System.out.println("setCita: " + c + ", " + fecha);
    }

    private static void invoca_verDiagnostico(Cuenta c) {
        System.out.println("verDiagnostico: " + c);
    }

    private static void invoca_verDiagnostico(Cuenta c, String nombre) {
        System.out.println("verDiagnostico: " + c + ", " + nombre);
    }

    private static void invoca_setDiagnostico(Cuenta c, String nombre, String diagnosis) {
        System.out.println("setDiagnostico: " + c + ", " + nombre + ", \"" + diagnosis + "\"");
    }
}
