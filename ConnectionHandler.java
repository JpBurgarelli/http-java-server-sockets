import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler implements Runnable {
    private final Socket conexao;
    private final Estudantes estudantes;

    public ConnectionHandler(Socket socket, Estudantes estudantes) {
        this.conexao = socket;
        this.estudantes = estudantes;
    }

    @Override
    public void run() {
        try {
            System.out.println("Cliente conectado: " + this.conexao.getInetAddress().getHostAddress() + ":" + this.conexao.getPort());
            try (PrintWriter output = new PrintWriter(this.conexao.getOutputStream());
                 BufferedReader input = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()))) {

                String linhaDeRequisicao = input.readLine();
                if (linhaDeRequisicao != null && !linhaDeRequisicao.isEmpty()) {
                    System.out.println("Requisição recebida: " + linhaDeRequisicao);
                    String[] partes = linhaDeRequisicao.split(" ");
                    String argumento = partes.length > 1 ? partes[1] : "";

                    if (linhaDeRequisicao.startsWith("GET")) {
                        processarGetRequest(output, argumento);
                    } else if (linhaDeRequisicao.startsWith("POST")) {
                        processarPostRequest(output, argumento);
                    } else if (linhaDeRequisicao.startsWith("DELETE")) {
                        processarDeleteRequest(output, argumento);
                    } else {
                        responderHtml(output, "Método não permitido", "red", 405);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fecharConexao();
        }
    }

    public void processarGetRequest(PrintWriter out, String arg) {
        if (arg.contains("/aluno")) {
            try {
                String[] partes = arg.split("/");
                if (partes.length > 1) {
                    int idEstudante = Integer.parseInt(partes[2]);
                    String estudante = estudantes.buscarAlunoPorId(idEstudante);
                    if (estudante != null) {
                        responderHtml(out, estudante, "green", 200);
                    } else {
                        responderHtml(out, "Aluno não encontrado", "red", 404);
                    }
                }
            } catch (NumberFormatException e) {
                responderHtml(out, "ID inválido. Informe um número válido.", "red", 400);
            }
        }
    }

    public void processarPostRequest(PrintWriter out, String arg) {
        if (arg.equalsIgnoreCase("/aluno")) {
            String aluno = estudantes.adicionarAluno();
            responderHtml(out, aluno, "blue", 201);
        } else {
            responderHtml(out, "Requisição inválida", "red", 400);
        }
    }

    public void processarDeleteRequest(PrintWriter out, String arg) {
        if (arg.contains("/aluno/")) {
            try {
                String[] partes = arg.split("/");
                if (partes.length > 1) {
                    int idEstudante = Integer.parseInt(partes[2]);
                    boolean sucesso = estudantes.deletarAluno(idEstudante);
                    if (sucesso) {
                        responderHtml(out, "Aluno deletado com sucesso", "blue", 200);
                    } else {
                        responderHtml(out, "Aluno não encontrado", "red", 404);
                    }
                }
            } catch (NumberFormatException e) {
                responderHtml(out, "ID inválido. Informe o número do aluno.", "red", 400);
            }
        } else {
            responderHtml(out, "Requisição inválida para exclusão", "red", 400);
        }
    }

    private void responderHtml(PrintWriter out, String conteudo, String cor, int codigoStatus) {
        String mensagemStatus = obterMensagemStatus(codigoStatus);
        String corpoResposta = "<html><body style='color:" + cor + ";'><h3>" + conteudo + "</h3></body></html>";
        out.println("HTTP/1.1 " + codigoStatus + " " + mensagemStatus);
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Content-Length: " + corpoResposta.getBytes(StandardCharsets.UTF_8).length);
        out.println();
        out.println(corpoResposta);
        out.flush();
    }

    private String obterMensagemStatus(int codigoStatus) {
        switch (codigoStatus) {
            case 200: return "OK";
            case 201: return "Criado";
            case 400: return "Solicitação Inválida";
            case 404: return "Não Encontrado";
            case 405: return "Método Não Permitido";
            default: return "Erro Interno";
        }
    }

    private void fecharConexao() {
        try {
            this.conexao.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

