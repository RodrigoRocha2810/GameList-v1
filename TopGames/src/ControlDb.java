
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

    private static final String INDEX_NAME_OUTPUT = ".\\index.games.db";

    private static final String INDEX_I_NAME_OUTPUT = ".\\indexI.games.db";

    private final Path DbPath = Paths.get(DB_NAME_OUTPUT);

    private final Path IndexPath = Paths.get(INDEX_NAME_OUTPUT);

    private final Path IndexIPath = Paths.get(INDEX_I_NAME_OUTPUT);

    private final RandomAccessFile raf, rafIndex, rafIndexI;

    private final ArrayList<Integer> listaIds = new ArrayList<>();

    private final Scanner scan;

    public ControlDb() throws Exception, FileNotFoundException {
        raf = new RandomAccessFile(DbPath.toFile(), "rw");
        rafIndex = new RandomAccessFile(IndexPath.toFile(), "rw");
        rafIndexI = new RandomAccessFile(IndexIPath.toFile(), "rw");
        scan = new Scanner(System.in);
    }

    //metodo para transferir o csv para um registro game e depois para o arquivo db
    public void LoadCsv(String CSVfile) throws IOException {
        listaIds.clear();
        this.maxID = 0;
        BufferedReader bf;
        Path p = Paths.get(CSVfile);
        //usa as bibliotecas bufferdRead e fileReader apra facilitar a leitura do csv
        if (p.toFile().exists() && p.toFile().isFile()) {
            try (raf) {
                bf = new BufferedReader(new FileReader(p.toFile()));
                raf.seek(0);
                raf.setLength(0);
                raf.writeInt(this.maxID);
                //pula primeira linha(cabecario)
                String line;
                bf.readLine();
                //loop para ler o csv linha a linha e converter no registro Game
                while ((line = bf.readLine()) != null) {
                    byte[] b;
                    Game registro = new Game(line);
                    b = registro.toByteArray();
                    raf.writeBoolean(false);
                    raf.writeShort(b.length);
                    raf.write(b);
                    this.maxID++;
                }
                //escreve ultimo id registrado
                raf.seek(0);
                raf.writeInt(this.maxID);
            }

        }
    }

    //pesquisa sequencial por id
    public Game getById(Integer id) throws Exception {
        if (Objects.nonNull(raf)) {
            long pointer;
            Game retorno = new Game();
            raf.seek(0);
            this.maxID = raf.readInt();

            if (id > this.maxID) {
                throw new Exception("Id solicitado maior que o último id cadastrado");
            }

            Boolean lapide;
            Short tamReg;
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
                } catch (IOException e) {
                    System.out.println("Erro");
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
        //le endereco de localizacao do registro
        retorno.setEnd_DB(raf.getFilePointer());
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

            if (r.getId() < 0) {
                // Criação
                raf.seek(0);
                r.setId(raf.readInt());
                raf.seek(raf.length());
                r.setEnd_DB(raf.getFilePointer());
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
                Short tamReg;
                //Ponterio ja esta no registro desejado devido o metodo getById chamado anteriormente

                //pula lapide e memoriza ponteiro inicial
                pointer = raf.getFilePointer();
                raf.readBoolean();
                tamReg = raf.readShort();
                //chama funcao para alterar campos especificos do registro
                r = getAlteredGame(r);
                b = r.toByteArray();
                //Verifica se espaço atual suporta alteraçoes, se sim escreve sobre registro antigo mantendo indicador de tamanho
                if (b.length <= tamReg) {
                    raf.write(b);
                } else {
                    raf.seek(pointer);
                    deletar();
                    raf.seek(raf.length());
                    pointer = raf.getFilePointer();
                    r.setEnd_DB(pointer);
                    raf.writeBoolean(false);
                    raf.writeShort(b.length);
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

    //Metodo para retononar o game com a caracteristica alterada desejada
    public Game getAlteredGame(Game game) {

        System.out.println(
                "1. Alterar Titulo,2. Alterar o ano de Lançamento,3. Alterar o time de desenvolvimento,4. Alterar avaliacao,5. Alterar número de reviews,6. Alterar Wishlist,7. Alterar generos,8. Alterar review");
        System.out.println("Selecione a opera\u00E7\u00E3o: ");
        Integer op = scan.nextInt();
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        switch (op) {
            case 1 -> {
                scan.nextLine();
                System.out.print("Informe o Título do game: ");
                game.settitle(scan.nextLine());
            }
            case 2 -> {
                scan.nextLine();
                System.out.print("Informe o ano de Lançamento em dd/mm/yyyy: ");
                game.setrelease_Date(scan.nextLine());
            }
            case 3 -> {
                scan.nextLine();
                System.out.print("Informe o time de desenvolvimento do game (separado por vírgulas): ");
                String aux = scan.nextLine();
                game.setteam(Arrays.asList(aux.split(",")));
            }
            case 4 -> {
                scan.nextLine();
                System.out.print("Informe a avaliacao de 1 a 5: ");
                game.setrating(scan.nextFloat());
            }
            case 5 -> {
                scan.nextLine();
                System.out.print("Informe o número total de reviews: ");
                game.setnreviews(scan.nextInt());
            }
            case 6 -> {
                scan.nextLine();
                System.out.print("Informe o número total de usuarios que colocaram na wish list: ");
                game.setwishlist(scan.nextInt());
            }
            case 7 -> {
                scan.nextLine();
                System.out.print("Informe os generos do game (separado por vírgulas): ");
                String aux2 = scan.nextLine();
                game.setgenres(Arrays.asList(aux2.split(",")));
            }
            case 8 -> {
                scan.nextLine();
                System.out.print("Informe um review do game: ");
                game.setreview(scan.nextLine());
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
            }
            default -> {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println("Op\u00E7\u00E3o inv\u00E1lida");
            }

        }
        return game;
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////INDEXACAO///////////////////////////////
    ////////////////////////////////////////////////////////////////////

    //Metodo para deirecionar o tipo de idexacao desejada
    public void index() throws IOException {
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.println(
                "1.direto e denso, 2.indireto e denso, 3.indireto e esparso,9. Sair");
        System.out.println("Selecione a opera\u00E7\u00E3o: ");
        Integer op = scan.nextInt();
        switch (op) {
            case 1 -> {
                System.out.println("Indexando registros...");
                index_direto();
            }
            case 2 -> {
                System.out.println("Indexando registros...");
                if (!index_criado()) {
                    index_direto();
                }
                index_indireto();
            }
            // case 3 ->
            //     index_esparco();
            // 
            // }
            default -> {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println("Op\u00E7\u00E3o inv\u00E1lida");
            }
        }
    }

    //Metodo para indexacao direta e densa
    private void index_direto() throws IOException {

        Game retorno;
        raf.seek(0);
        this.maxID = raf.readInt();
        rafIndex.writeInt(this.maxID);
        for (int i = 0; i < this.maxID; i++) {
            try {
                retorno = getById(i);
                rafIndex.writeInt(retorno.getId());
                rafIndex.writeLong(retorno.getEnd_DB());
            } catch (Exception ex) {
            }

        }

    }

    //verifica existencia de index
    public boolean index_criado() {
        try {

            return rafIndex.length() != 0;

        } catch (IOException ex) {
        }
        return false;
    }

    //Metodo para buscar por id no index
    public Game getByIndex(Integer id) throws Exception {

        Game retorno;
        rafIndex.seek(0);
        this.maxID = rafIndex.readInt();

        if (id > this.maxID) {
            throw new Exception("Id solicitado maior que o último id cadastrado");
        }

        Integer idReg;
        Long endReg;
        //procura sequencialmente o registro no index
        do {
            try {
                idReg = rafIndex.readInt();
                endReg = rafIndex.readLong();

                if (Objects.equals(idReg, id)) {
                    //apos encontrar o registro desejado, le o registro no arquivo db a partir do endereco
                    raf.seek(endReg);
                    //reseta o ponteiro do index para o inicio do registro(para uso posterior em alteracoes)
                    rafIndex.seek(rafIndex.getFilePointer() - 8);
                    retorno = bdToRam();
                    return retorno;
                }

            } catch (IOException e) {
                System.out.println("Erro");
            }

        } while (rafIndex.getFilePointer() < rafIndex.length());

        return null;

    }

    //Metodo para salvar no index
    public void saveIndex(Game r, Game old) throws Exception {
        if (Objects.nonNull(rafIndex)) {
            // Ela diferencia somente pela presenta ou não de um ID válido na entidade
            // enviada como parametro.
            // Uma entidade nova gera Id negativo e é tratada na primeira condição; Caso a a
            // entidade possua um id
            // válido; o registro antigo será marcado como "excluido" (lapide = true) e o
            // registro será escrito no fim do arquivo;
            // Registros inválidos são tratados após a ordenacao do arquivo;

            if (r.getId() < 0) {
                // Criação
                rafIndex.seek(0);
                r.setId(rafIndex.readInt());
                rafIndex.seek(rafIndex.length());
                rafIndex.writeInt(r.getId());
                rafIndex.writeLong(r.getEnd_DB());
                if (indexI_criado()) {
                    rafIndexI.seek(rafIndexI.length());
                    byte[] b = r.gettitle().getBytes();
                    // cria um array de bytes de tamanho fixo(100) para armazenar o título
                    byte[] fixedSizeBytes = new byte[100];
                    // copia o titulo para o array de bytes de tamanho fixo
                    System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 100));
                    //escreve titulo e ponteiro no index indireto para  o direto
                    rafIndexI.write(fixedSizeBytes);
                    rafIndexI.writeLong(rafIndex.getFilePointer() - 12);
                }
                rafIndex.seek(0);
                rafIndex.writeInt(r.getId() + 1);
                rafIndexI.seek(0);
                rafIndexI.writeInt(r.getId() + 1);
                // System.out.print("\033c");// Limpa a tela(ANSI escape character)
                //System.out.printf("Id do anime inserido %d\n", r.getId());
            } else {
                //Atualização
                getByIndex(r.getId());
                rafIndex.writeLong(r.getEnd_DB());
                if(indexI_criado()){
                    getByIndexI(old.gettitle());
                    Long pointer = rafIndexI.getFilePointer();
                    byte[] b = r.gettitle().getBytes();
                    // cria um array de bytes de tamanho fixo(100) para armazenar o título
                    byte[] fixedSizeBytes = new byte[100];
                    // copia o titulo para o array de bytes de tamanho fixo
                    System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 100));
                    //escreve titulo e ponteiro no index indireto para  o direto
                    rafIndexI.write(fixedSizeBytes);
                    
                }

            }
        }
    }

    public void index_indireto() throws IOException {
        rafIndex.seek(0);
        this.maxID = rafIndex.readInt();
        rafIndexI.writeInt(this.maxID);
        long pointer4title, pointer2indexI;
        short tamAux;
        System.out.println("50%...");
        // loop para criar o index indireto a aprtir do index direto e do banco de dados
        //utilizando o titulo como chave primaia
        do {
            pointer2indexI = rafIndex.getFilePointer();
            rafIndex.readInt();
            pointer4title = rafIndex.readLong();
            //busca nome do registro no banco de dados
            raf.seek(pointer4title + 7);
            /////Le titulo
            tamAux = raf.readShort();
            byte[] b = new byte[tamAux];
            raf.read(b);
            ///// 
            // cria um array de bytes de tamanho fixo(100) para armazenar o título
            byte[] fixedSizeBytes = new byte[100];
            // copia o titulo para o array de bytes de tamanho fixo
            System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 100));
            //escreve titulo e ponteiro no index indireto para  o direto
            rafIndexI.write(fixedSizeBytes);
            rafIndexI.writeLong(pointer2indexI);

        } while (rafIndex.getFilePointer() < rafIndex.length());

    }

    public Game getByIndexI(String title) throws Exception {
        Game retorno;
        rafIndexI.seek(0);
        this.maxID = rafIndexI.readInt();
        byte[] b = new byte[100];
        long pointer, p;
        //procura sequencialmente o registro no index
        do {
            try {
                rafIndexI.read(b);
                if (new String(b, StandardCharsets.UTF_8).trim().equals(title)) {
                    //utiliza o ponteiro para buscar o registro no index direto
                    pointer = rafIndexI.readLong();



                    p = rafIndexI.getFilePointer()-108;
                    rafIndexI.seek(p);
                    rafIndex.seek(pointer);
                    rafIndex.readInt();
                    //utiliza o ponteiro do index direto para buscar no arquivo db
                    pointer = rafIndex.readLong();
                    raf.seek(pointer);
                    //le e retorna o registro no arquivo db
                    retorno = bdToRam();
                    return retorno;
                } else {
                    rafIndexI.readLong();
                }

            } catch (IOException e) {
                System.out.println("Erro");
            }

        } while (rafIndexI.getFilePointer() < rafIndexI.length());

        return null;

    }

    public boolean indexI_criado() {
        try {

            return rafIndexI.length() != 0;

        } catch (IOException ex) {
        }
        return false;
    }

    public void close() {
        try {
            rafIndexI.close();
            rafIndex.close();
            raf.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar o arquivo");
        }
    }

}
