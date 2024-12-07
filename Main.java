import java.io.IOException;

public class Main {
    public static void main(String... args) {
        try {
            WebServer server = new WebServer(8080);
            server.iniciar();
        } catch (IOException e) {
            System.out.println("Falha ao iniciar o servidor: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}

