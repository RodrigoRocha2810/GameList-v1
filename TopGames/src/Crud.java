
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Crud {

    private final Scanner scan;
    private Game game, old;
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
                    "1. Pesquisar registro,2. Criar registro,3. Alterar registro,4. Deletar registro,5. Indexar registro,6. Comprimir banco,7. Procurar por padrao,9. Sair");
            System.out.println("Selecione a opera\u00E7\u00E3o: ");
            Integer op = scan.nextInt();
            // scan.nextLine();
            switch (op) {
                case 1 ->
                    pesquisar();
                case 2 ->
                    inserir();
                case 3 ->
                    alterar();
                case 4 ->
                    deletar();
                case 5 ->
                    indexar();
                // case 6 ->
                //     comprimir();
                    case 7 -> 
                        procurar();
                case 9 -> {
                    active = false;
                    controller.close();
                }
                default -> {
                    System.out.print("\033c");// Limpa a tela(ANSI escape character)
                    System.out.println("Op\u00E7\u00E3o inv\u00E1lida");
                }
            }
        }
    }

    private void alterar() {
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        game = null;
        int ID;
        try {

            System.out.print(
                    "Voc\u00EA precisa buscar um registro para Alterar. Digite o ID do registro que quer Alterar: ");
            ID = scan.nextInt();

            if (controller.index_criado()) {
                game = controller.getByIndex(ID);
            } else {
                game = controller.getById(ID);
            }

            if (Objects.isNull(game) || game.getId() < 0) {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.printf("O id %d nao foi encontrado\n", ID);
                game = null;
            } else {
                //clona o objeto para poder comparar depois
                old = new Game(game);
                //altera o objeto no banco de dados
                controller.save(game);
                //passa para o indice o id do registro alterado e o registro antigo(para facilicar busca no indice indireto)
                if (controller.index_criado()) {
                    controller.saveIndex(game, old);
                }
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.printf("O id %d foi Alterado\n", ID);
            }

        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao alterar\n");
            System.out.println("Erro: " + e.getMessage());
        }

    }

    private void deletar() {
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        game = null;
        int ID;
        try {

            System.out.print(
                    "Voc\u00EA precisa buscar um registro para excluir. Digite o ID do registro que quer excluir: ");
            ID = scan.nextInt();
            game = controller.getById(ID);

            if (Objects.isNull(game) || game.getId() < 0) {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.printf("O id %d nao foi encontrado\n", ID);
                // Thread.sleep(5000);
                game = null;
            } else {
                System.out.println("Confirma a exclus\u00E3o (S/N)? ");

                String aux = scan.next();
                if (aux.toUpperCase().charAt(0) == 'S') {
                    controller.deletar();
                    System.out.print("\033c");// Limpa a tela(ANSI escape character)
                    System.out.printf("O id %d foi Deletado\n", ID);
                }
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao excluir\n");
            System.out.println("Erro: " + e.getMessage());
        }

    }

    private void inserir() {
        game = null;
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        scan.nextLine();
        ////
        System.out.print("Informe o Título do game: ");
        game = new Game();
        game.settitle(scan.nextLine());
        scan.nextLine();
        ////
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.print("Informe o ano de Lançamento em dd/mm/yyyy: ");
        game.setrelease_Date(scan.nextLine());
        scan.nextLine();
        ////
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.print("Informe o time de desenvolvimento do game (separado por vírgulas): ");
        String aux = scan.nextLine();
        game.setteam(Arrays.asList(aux.split(",")));
        scan.nextLine();
        ////
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.print("Informe a avaliacao de 1 a 5: ");
        game.setrating(scan.nextFloat());
        scan.nextLine();
        ////
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.print("Informe o número total de reviews: ");
        game.setnreviews(scan.nextInt());
        scan.nextLine();
        ////
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.print("Informe o número total de usuarios que colocaram na wish list: ");
        game.setwishlist(scan.nextInt());
        scan.nextLine();
        ////
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.print("Informe os generos do game (separado por vírgulas): ");
        String aux2 = scan.nextLine();
        game.setgenres(Arrays.asList(aux2.split(",")));
        scan.nextLine();
        ////
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.print("Informe um review do game: ");
        game.setreview(scan.nextLine());
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        scan.nextLine();
        try {
            controller.save(game);
            if (controller.index_criado()) {
                game.setId(-1);
                controller.saveIndex(game, null);
            }
            if (controller.indexE_criado()) {
                game.setId(-1);
                controller.saveindexE(game, old);

            }
        } catch (Exception e) {
            System.out.print("Ocorreu um erro ao salvar a entidade em arquivo\n");
            System.out.print("Erro: " + e.getMessage());
        }

    }

    private void pesquisar() {
        Integer ID = null;
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        try {

            System.out.print("\033c");// Limpa a tela(ANSI escape character)
            System.out.println("01 - Perqusiar por Ids, 02 - Pesquisar por titulo(requer indice indireto criado)");
            System.out.println("Selecione a opera\u00E7\u00E3o: ");
            Integer op = scan.nextInt();
            if (op == 2 && controller.indexI_criado()) {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println("Informe o titulo do registro a ser buscado: ");
                scan.nextLine();
                String input = scan.nextLine();
                game = controller.getByIndexI(input);
                if (Objects.isNull(game) || game.getId() < 0) {
                    System.out.print("\033c");// Limpa a tela(ANSI escape character)
                    System.out.printf("O titulo %s nao foi encontrado\n", input);
                    // Thread.sleep(5000);
                    game = null;
                } else {
                    System.out.println(game.toString() + "\n\n\n");
                }

            } else {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println("Informe os ID do registro a ser buscados separados por virgula : ");
                scan.nextLine();
                String input = scan.nextLine();

                String[] numberStrings = input.split(",");

                for (String numberString : numberStrings) {

                    ID = Integer.valueOf(numberString.trim());

                    if (controller.index_criado()) {
                        game = controller.getByIndex(ID);
                    } else {
                        game = controller.getById(ID);
                    }

                    if (Objects.isNull(game) || game.getId() < 0) {
                        System.out.print("\033c");// Limpa a tela(ANSI escape character)
                        System.out.printf("O id %d nao foi encontrado\n", ID);
                        // Thread.sleep(5000);
                        game = null;
                    } else {
                        System.out.println(game.toString() + "\n\n\n");
                    }

                }
            }

        } catch (Exception e) {
            System.out.printf("Erro ao buscar o ID %d\n", ID);
            System.out.printf("Erro causado: %s", e.getMessage());

        }

    }

    private void indexar() {

        try {

            controller.index();
            System.out.println("Registros indexados com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao indexar registros");
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // private void comprimir() throws IOException {
    //     //verifica versao do arquivo comprimido para poder comprimir
    //     String vcompressed = "";
    //     Byte b = 1;
    //     for (Byte i = 1; i < 10; i++) {
    //         vcompressed = "data.compressed[" + i + "].db";
    //         if (checkFileExists(vcompressed)) {
    //             b = i;
    //             break;
    //         }
    //     }
    //     controller.comprimir(b);
    // }

    // private Boolean checkFileExists(String filePath) {
    //     Path path = Paths.get(filePath);
    //     return Files.exists(path);

    // }

    private void procurar() {
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.println("Informe o padrão a ser procurado: ");
        scan.nextLine();
        String input = scan.nextLine();
        try {
            controller.procurar(input);
        } catch (IOException e) {
            System.out.println("Erro ao procurar por padrão");
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
