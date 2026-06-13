/**
 * Console entry point of the Emergency Dispatcher application.
 *
 * <p>Launches the command-line interface by delegating to {@link CommandLineApp}.
 * This class exists solely to satisfy the JVM entry-point contract.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 * @see CommandLineApp
 */
public class Main {

    /**
     * Application entry point for command-line mode.
     *
     * @param args command-line arguments passed by the JVM; currently unused
     */
    public static void main(String[] args) {
        CommandLineApp app = new CommandLineApp();
        app.start();
    }
}
