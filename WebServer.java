import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class WebServer {

    private final int porta;
    private final Estudantes estudantes;

    public WebServer(int porta) {
        this.porta = porta;
        this.estudantes = new Estudantes();
    }

    public void iniciar() throws IOException {
        try (ServerSocket servidor = new ServerSocket(this.porta, 2048, InetAddress.getByName("127.0.0.1"))) {
            System.out.println("Servidor iniciado na porta " + this.porta);
            while (true) {
                new Thread(new ConnectionHandler(servidor.accept(), this.estudantes)).start();
            }
        }
    }
}

