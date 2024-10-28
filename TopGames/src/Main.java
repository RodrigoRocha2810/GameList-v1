
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        ControlDb controle = new ControlDb();
        Crud crud = new Crud();
        try (Scanner scan = new Scanner(System.in)) {
            int opc = 0;
            while (opc != 3) {

                System.out.println("===============SELECIONE A OPÇÃO DESEJADA==============");
                System.out.println("1. Carregar dados CSV\n2. Operar sobre arquivo binario\n3. Sair");

                opc = scan.nextInt();
                switch (opc) {
                    case 1 -> {
                        System.out.print("\033c");// Limpa a tela(ANSI escape character)
                        System.out.println("===============INFORME O ARQUIVO PARA IMPORTAÇÃO===============");
                        System.out.println("= Essa operação irá sobrescrever os dados atuais              =");
                        System.out.println("===============================================================");
                        String caminho = "GameList-v1\\GameList-v1\\TopGames\\gamesRM3.csv";
                        System.out.print("\033c");// Limpa a tela(ANSI escape character)
                        try {
                            controle.LoadCsv(caminho);
                            System.out.print("\033c");// Limpa a tela(ANSI escape character)
                            System.out.println("Carga de dados completa!!!");
                            controle.close();
                        } catch (IOException e) {
                        }
                    }
                    case 2 -> {
                        System.out.print("\033c");// Limpa a tela(ANSI escape character)
                        crud.run();
                    }
                    default -> {
                    }
                }
            }
        }
    }

}
