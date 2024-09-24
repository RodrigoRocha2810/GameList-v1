
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class ControlDb {

    private Integer maxID = 0;

    private static final String DB_NAME_OUTPUT = ".\\data.games.db";

    private Path DbPath = Paths.get(DB_NAME_OUTPUT);

    private RandomAccessFile raf;

    private ArrayList<Integer> listaIds = new ArrayList<>();

    private Scanner scan;

    public ControlDb() throws Exception, FileNotFoundException {
        raf = new RandomAccessFile(DbPath.toFile(), "rw");
        scan = new Scanner(System.in);
    }

    //metodo para transferir o csv para um registro game e depois para o arquivo db
    public void LoadCsv(String CSVfile) throws IOException {
        listaIds.clear();
        maxID = 0;
        BufferedReader bf;
        Path p = Paths.get(CSVfile);
        //usa as bibliotecas bufferdRead e fileReader apra facilitar a leitura do csv
        if (p.toFile().exists() && p.toFile().isFile()) {
            bf = new BufferedReader(new FileReader(p.toFile()));
            raf.seek(0);
            raf.setLength(0);
            raf.writeInt(maxID);
            //pula primeira linha(cabecario)
            String line = bf.readLine();
            //loop para ler o csv linha a linha e converter no registro Game
            while ((line = bf.readLine()) != null) {
                byte[] b;
                Game registro = new Game(line);
                b = registro.toByteArray();
                raf.writeBoolean(false);
                raf.writeShort(b.length);
                raf.write(b);
                maxID++;
            }
            //escreve ultimo id registrado
            raf.seek(0);
            raf.writeInt(maxID);
            raf.close();

        }
    }

    //pesquisa sequencial por id
    public Game getById(Integer id) throws Exception {
        if (Objects.nonNull(raf)) {
            long pointer;
            Game retorno = new Game();
            raf.seek(0);
            Integer maxID = raf.readInt();

            if (id > maxID) {
                throw new Exception("Id solicitado maior que o último id cadastrado");
            }

            Boolean lapide;
            Short tamReg = 0;
            Integer idReg;
            do {
                try {
                    lapide = raf.readBoolean();
                    tamReg = raf.readShort();
                    idReg = raf.readInt();

                    if (Objects.equals(idReg, id) && !lapide) {
                        // Retorna 7 posições para retornar ao primeiro byte referente ao registro (byte
                        // de lápide)
                        // Sendo 7 bytes pela leitura do lápide (1 byte), leitura do tamanho (2 bytes) e
                        // leitura do ID para fazer a comparação (4 bytes)
                        pointer = raf.getFilePointer() - 7;
                        raf.seek(pointer);
                        retorno = bdToRam();
                        raf.seek(pointer);
                        // Só retorna se o registro estiver com a lápide como false (registro valido);
                        // Se não for válido segue procurando por um registro válido pro ID e com lápide
                        // falsa
                        return retorno;
                    }

                    raf.seek(raf.getFilePointer() + (tamReg - 4));
                } catch (Exception e) {
                    System.out.println("Erro");
                    e.printStackTrace();
                }

            } while (raf.getFilePointer() < raf.length());

            return retorno;
        }
        return null;
    }

    //Le o arquivo para uma entidade game
    private Game bdToRam() throws IOException {
        Short tamAux;
        byte count;
        Game retorno = new Game();
        //le lapide pois nao é nescessaria para esse contexto
        raf.readBoolean();
        //le tam registro pois nao é nescessaria para esse contexto
        raf.readShort();
        //le id
        retorno.setId(raf.readInt());
        /////Le titulo
        tamAux = raf.readShort();
        byte[] b = new byte[tamAux];
        raf.read(b);
        retorno.settitle(new String(b, StandardCharsets.UTF_8));
        /////
        //le data
        retorno.setrelease_Date(raf.readShort());
        ///// le times
        count = raf.readByte();
        for (int i = 0; i < count; i++) {
            tamAux = raf.readShort();
            b = new byte[tamAux];
            raf.read(b);
            retorno.getteam().add(new String(b, StandardCharsets.UTF_8));
        }
        ////
        //le rating
        retorno.setrating(raf.readFloat());
        //le numero de reviews
        retorno.setnreviews(raf.readInt());
        //le wishlist
        retorno.setwishlist(raf.readInt());
        ////le generos
        count = raf.readByte();
        for (int i = 0; i < count; i++) {
            tamAux = raf.readShort();
            b = new byte[tamAux];
            raf.read(b);
            retorno.getgenres().add(new String(b, StandardCharsets.UTF_8));
        }
        ////le review
        tamAux = raf.readShort();
        byte[] d = new byte[tamAux];
        raf.read(d);
        retorno.setreview(new String(d, StandardCharsets.UTF_8));
        /////
        return retorno;
    }

    // Metodos alterar e inserir
    public Game save(Game r) throws Exception {
        if (Objects.nonNull(raf)) {
            // Ela diferencia somente pela presenta ou não de um ID válido na entidade
            // enviada como parametro.
            // Uma entidade nova gera Id negativo e é tratada na primeira condição; Caso a a
            // entidade possua um id
            // válido; o registro antigo será marcado como "excluido" (lapide = true) e o
            // registro será escrito no fim do arquivo;
            // Registros inválidos são tratados após a ordenacao do arquivo;

            if (r.getId() <= 0) {
                // Criação
                raf.seek(0);
                r.setId(raf.readInt());
                raf.seek(raf.length());
                byte[] b = r.toByteArray();
                raf.writeBoolean(false);
                raf.writeShort(b.length);
                raf.write(b);

                raf.seek(0);
                raf.writeInt(r.getId() + 1);
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.printf("Id do anime inserido %d\n", r.getId());
            } else {
                //Atualização
                byte[] b;
                Long pointer;
                Integer idReg;
                Short tamReg = 0;
                //Ponterio ja esta no registro desejado devido o metodo getById chamado anteriormente

                //pula lapide e memoriza ponteiro inicial
                pointer = raf.getFilePointer();
                raf.readBoolean();
                tamReg = raf.readShort();
                idReg = r.getId();
                //chama funcao para alterar campos especificos do registro
                r = newGameToRam(r);
                b = r.toByteArray();
                //Verifica se espaço atual suporta alteraçoes, se sim escreve sobre registro antigo mantendo indicador de tamanho
                if(b.length<=tamReg){
                    raf.write(b);
                }
                else{
                    raf.seek(pointer);
                    deletar();
                    raf.seek(raf.length());
                    raf.write(b);
                }

            }
        }
        return r;
    }

    //Metodo deletar, usa o ponteiro que o metodo getby id parou, que conveniente é a lapide desejada
    public void deletar() throws Exception {
        raf.writeBoolean(true);
    }

    public Game newGameToRam(Game game) {

        System.out.println(
                "1. Alterar Titulo,2. Alterar o ano de Lançamento,3. Alterar o time de desenvolvimento,4. Alterar avaliacao,5. Alterar número de reviews,6. Alterar Wishlist,7. Alterar generos,8. Alterar review");
        System.out.println("Selecione a opera\u00E7\u00E3o: ");
        Integer op = scan.nextInt();
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        switch (op) {
            case 1:
                System.out.print("Informe o Título do game: ");
                game.settitle(scan.nextLine());
                scan.nextLine();
                break;
            case 2:
                System.out.print("Informe o ano de Lançamento em dd/mm/yyyy: ");
                game.setrelease_Date(scan.nextLine());
                scan.nextLine();
                break;
            case 3:
                System.out.print("Informe o time de desenvolvimento do game (separado por vírgulas): ");
                String aux = scan.nextLine();
                game.setteam(Arrays.asList(aux.split(",")));
                scan.nextLine();
                break;
            case 4:
                System.out.print("Informe a avaliacao de 1 a 5: ");
                game.setrating(scan.nextFloat());
                scan.nextLine();
                break;
            case 5:
                System.out.print("Informe o número total de reviews: ");
                game.setnreviews(scan.nextInt());
                scan.nextLine();
                break;
            case 6:
                System.out.print("Informe o número total de usuarios que colocaram na wish list: ");
                game.setwishlist(scan.nextInt());
                scan.nextLine();
                break;
            case 7:
                System.out.print("Informe os generos do game (separado por vírgulas): ");
                String aux2 = scan.nextLine();
                game.setgenres(Arrays.asList(aux2.split(",")));
                scan.nextLine();
                break;
            case 8:
                System.out.print("Informe um review do game: ");
                game.setreview(scan.nextLine());
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                scan.nextLine();
                break;
            default:
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println("Op\u00E7\u00E3o inv\u00E1lida");
                break;

        }
        return game;
    }
}