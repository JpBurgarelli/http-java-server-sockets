import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Estudantes {

    private Map<Integer, String> estudantes;
    private int contador;

    public Estudantes() {
        this.estudantes = new HashMap<>();
        this.contador = 0;
    }

    public synchronized int gerarId() {
        return ++this.contador;
    }

    public String adicionarAluno() {
        int id = gerarId();
        String nome = gerarNomeAleatorio();
        estudantes.put(id, nome);
        return "ID: " + id + ", Nome: " + nome;
    }

    public boolean deletarAluno(int id) {
        if (estudantes.containsKey(id)) {
            estudantes.remove(id);
            return true;
        }
        return false;
    }

    public String buscarAlunoPorId(int id) {
        return estudantes.get(id);
    }

    private String gerarNomeAleatorio() {
        String[] nomes = {"Frodo", "Gandalf", "Aragorn", "Legolas", "Samwise"};
        Random rand = new Random();
        return nomes[rand.nextInt(nomes.length)] + " " + "Baggins";
    }
}

