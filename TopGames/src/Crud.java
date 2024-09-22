
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Crud {
    private Scanner scan;
    private Game game;
    ControlDb controller;

    public Crud() throws Exception {
    controller = new ControlDb();
    scan = new Scanner(System.in);
    game = new Game();   
}

public void run() throws IOException {
        Boolean active = true;
        while (active) {
            // System.out.print("\033c");// Limpa a tela(ANSI escape character)
            System.out.println(
                    "1. Pesquisar registro,2. Criar registro,3. Alterar registro,4. Deletar registro,9. Sair");
            System.out.println("Selecione a opera\u00E7\u00E3o: ");
            Integer op = scan.nextInt();
            // scan.nextLine();
            switch (op) {
                case 1:
                    pesquisar();
                    break;
                case 2:
                    //inserir();
                    break;
                case 3:
                  //  alterar();
                    break;
                case 4:
                   // deletar();
                    break;
                case 9:
                    active = false;
                    break;
                default:
                    System.out.print("\033c");// Limpa a tela(ANSI escape character)
                    System.out.println("Op\u00E7\u00E3o inv\u00E1lida");
                    break;
            }
        }
    }






    private void pesquisar() {
        Integer ID = null;
        try {
            System.out.print("\033c");// Limpa a tela(ANSI escape character)
            System.out.println("Informe o ID do registro a ser buscado: ");
            ID = scan.nextInt();
            game = controller.getById(ID);
            if (Objects.isNull(game) || game.getId() < 0) {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.printf("O id %d nao foi encontrado\n", ID);
                // Thread.sleep(5000);
                game = null;
            } else {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println(game.toString() + "\n\n\n");
            }

        } catch (Exception e) {
            System.out.printf("Erro ao buscar o ID %d\n", ID);
            System.out.printf("Erro causado: %s", e.getMessage());

        }
    }
















}
