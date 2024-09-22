import java.util.Scanner;

public class Main {



     public static void main(String[] args) throws Exception {
        ControlDb controle = new ControlDb();
        //private Crud crud;
        Scanner scan = new Scanner(System.in);
        int opc = 0;
        while (opc != 3) {
        
        System.out.println("===============SELECIONE A OPÇÃO DESEJADA==============");
        System.out.println("1. Carregar dados CSV\n2. Operar sobre arquivo binario\n3. Sair");
        
            opc = scan.nextInt();
            switch (opc) {
                case 1:
                    System.out.print("\033c");// Limpa a tela(ANSI escape character)
                    System.out.println("===============INFORME O ARQUIVO PARA IMPORTAÇÃO===============");
                    System.out.println("= Essa operação irá sobrescrever os dados atuais              =");
                    System.out.println("===============================================================");
                    String caminho = "C:\\Users\\rodri\\Desktop\\GameList\\GameList-v1\\GameList-v1\\TopGames\\games.csv";
                    System.out.print("\033c");// Limpa a tela(ANSI escape character)
                    try {
                        controle.LoadCsv(caminho);
                        System.out.print("\033c");// Limpa a tela(ANSI escape character)
                        System.out.println("Carga de dados completa!!!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                // case 2:
                //     // crud.setScan(scan);
                //     // crud.setController(controle);
                //     this.crud = new Crud(controle,scan);
                //     System.out.print("\033c");// Limpa a tela(ANSI escape character)
                //     crud.run();
                //     break;
                // default:
                // break;
            }
        }
        scan.close();
    }



























    
}


















