
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class ControlDb {

    private Integer maxID = 0;

    private static String grandTline;

    private static final String DB_NAME_OUTPUT = ".\\data.games.db";

    private static final String INDEX_NAME_OUTPUT = ".\\index.games.db";

    private static final String INDEX_I_NAME_OUTPUT = ".\\indexI.games.db";

    private static final String INDEX_E_MAIN_NAME_OUTPUT = ".\\indexEmain.games.db";

    private static final String INDEX_E_YEAR_NAME_OUTPUT = ".\\indexEYear.games.db";

    private static final String INDEX_E_TEAM_NAME_OUTPUT = ".\\indexETeam.games.db";

    private static final String DICIONARIO_NAME_OUTPUT = ".\\Dicionario.games.db";

    private final Path DbPath = Paths.get(DB_NAME_OUTPUT);

    private final Path IndexPath = Paths.get(INDEX_NAME_OUTPUT);

    private final Path IndexIPath = Paths.get(INDEX_I_NAME_OUTPUT);

    private final Path IndexEmainPath = Paths.get(INDEX_E_MAIN_NAME_OUTPUT);

    private final Path IndexEYearPath = Paths.get(INDEX_E_YEAR_NAME_OUTPUT);

    private final Path IndexETeamPath = Paths.get(INDEX_E_TEAM_NAME_OUTPUT);

    private final Path DicionarioPath = Paths.get(DICIONARIO_NAME_OUTPUT);

    private final RandomAccessFile raf, rafIndex, rafIndexI, rafIndexEmain, rafIndexEyear, rafIndexETeam, rafDicionario;

    private final ArrayList<Integer> listaIds = new ArrayList<>();

    private final ArrayList<Long> yearEndList = new ArrayList<>();
    private final ArrayList<Short> yearList = new ArrayList<>();
    private final Map<Short, Integer> yearCountMap = new HashMap<>();

    private final ArrayList<Long> teamEndList = new ArrayList<>();
    private final ArrayList<String> teamList = new ArrayList<>();
    private final Map<String, Integer> teamCountMap = new HashMap<>();

    private final Scanner scan;

    //construtor
    public ControlDb() throws Exception, FileNotFoundException {
        raf = new RandomAccessFile(DbPath.toFile(), "rw");
        rafIndex = new RandomAccessFile(IndexPath.toFile(), "rw");
        rafIndexI = new RandomAccessFile(IndexIPath.toFile(), "rw");
        rafIndexEmain = new RandomAccessFile(IndexEmainPath.toFile(), "rw");
        rafIndexEyear = new RandomAccessFile(IndexEYearPath.toFile(), "rw");
        rafIndexETeam = new RandomAccessFile(IndexETeamPath.toFile(), "rw");
        rafDicionario = new RandomAccessFile(DicionarioPath.toFile(), "rw");
        scan = new Scanner(System.in);
        grandTline = new String();
    }

    //metodo para fechar os arquivos
    public void close() {
        try {

            rafIndexEyear.close();
            rafIndexETeam.close();
            rafIndexEmain.close();
            rafIndexI.close();
            rafIndex.close();
            raf.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar o arquivo");
        }
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

    ////////////////////////////INDEXACAO///////////////////////////////

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
            case 3 ->
                index_esparco();

            default -> {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////
    //////////////////////////INDEX DIRETO//////////////////////////////
    ////////////////////////////////////////////////////////////////////

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
                if (indexI_criado()) {
                    getByIndexI(old.gettitle());
                    rafIndexI.getFilePointer();
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

    //////////////////////////INDEX INDIRETO////////////////////////////

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

                    p = rafIndexI.getFilePointer() - 108;
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

    //retorna se o indice foio criado
    public boolean indexI_criado() {
        try {

            return rafIndexI.length() != 0;

        } catch (IOException ex) {
        }
        return false;
    }

    //////////////////////////INDEX MULTILISTA//////////////////////////

    //Chama os metodos para indexar os registros multilista
    public void index_esparco() throws IOException {
        if (!indexE_criado()) {
            System.out.print("\033c");// Limpa a tela(ANSI escape character)
            System.out.println("Indexando Registros");// Limpa a tela(ANSI escape character)
            index_multilista();
        }
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.println(
                "1.Pesquisar na multilista por ano x estudio, 9. Sair");
        System.out.println("Selecione a opera\u00E7\u00E3o: ");
        Integer op = scan.nextInt();
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        switch (op) {
            case 1 -> {

                System.out.println("Informe o ano de Lançamento do jogo");
                Short year = scan.nextShort();
                System.out.println("Informe o estudio de desenvolvimento do game: ");
                String team = scan.next();
                try {
                    if (!get_year(year)) {
                        System.out.print("\033c");// Limpa a tela(ANSI escape character)
                        System.out.print("Ano não encontrado\n\n");
                        break;
                    }
                    if (!get_team(team)) {
                        System.out.print("\033c");// Limpa a tela(ANSI escape character)
                        System.out.print("Estudio não encontrado\n\n");
                        break;
                    }

                } catch (IOException e) {
                    System.out.println("Erro");
                }

                List<Long> commonElements = getCommonElements();
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                for (Long pointer : commonElements) {
                    rafIndexEmain.seek(pointer);
                    Boolean lapide = rafIndexEmain.readBoolean();
                    if (lapide) {
                        continue;
                    }
                    raf.seek(rafIndexEmain.readLong());

                    Game game = bdToRam();

                    System.out.println(game.toString() + "\n\n\n");
                }

            }
            default -> {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println("Op\u00E7\u00E3o inv\u00E1lida");
            }
        }

        // get_year((short) 2017);
        //get_team("Nintendo");
    }

    //retorna se o indice foi criado
    public Boolean indexE_criado() {
        try {

            return rafIndexEmain.length() != 0;

        } catch (IOException ex) {
        }
        return false;
    }

    //cria index multilista (1 byte lapide, 8 bytes endereco para registro no bd, 2 bytes ano de lancamento
    //8 bytes endereco para proximo registro com mesmo ano de lancamento, 50 bytes estudio principal,8 bytes endereco para proximo registro com mesmo estudio principal, total 77 bytes por registro)
    private void index_multilista() throws IOException {
        Game retorno;
        raf.seek(0);
        rafIndexEmain.seek(0);
        this.maxID = raf.readInt();
        int i = 0;
        Short year;
        do {
            try {
                //le registro para memoria
                retorno = bdToRam();

                //escreve Lapide
                rafIndexEmain.writeBoolean(false);
                //escreve endereco para registro no bd(foi escolido usar o endereco diretamente para evitar o uso de strings de tamanho fixo, que teriam de ter no minimo 100 bytes)
                rafIndexEmain.writeLong(retorno.getEnd_DB());
                //escreve ano de lancamento e adiciona a lista de anos com o endereco do index
                year = retorno.calculateYears(retorno.getrelease_Date());
                add_year(year);
                rafIndexEmain.writeShort(year);
                //escreve o endereco do proximo registro com mesmo ano de lancamento(por enquanto -1)
                rafIndexEmain.writeLong(-1);//todo
                //escreve o estudio principal

                for (String studio : retorno.getteam()) {
                    byte[] b = studio.getBytes();
                    byte[] fixedSizeBytes = new byte[50];
                    System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 50));
                    rafIndexEmain.write(fixedSizeBytes);
                    //apenas o primeiro estudio é considerado
                    add_team(studio);
                    break;
                }

                // byte[] b = retorno.gettitle().getBytes();
                // // cria um array de bytes de tamanho fixo(100) para armazenar o título
                // byte[] fixedSizeBytes = new byte[50];
                // // copia o titulo para o array de bytes de tamanho fixo
                // System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 50));
                // //escreve titulo e ponteiro no index indireto para  o direto
                // rafIndexEmain.write(fixedSizeBytes);
                //escreve o endereco do proximo registro com mesmo estudio principal(por enquanto -1)
                rafIndexEmain.writeLong(-1);//todo
                i++;
            } catch (IOException e) {
                System.out.println("Erro");
            }

        } while (raf.getFilePointer() < raf.length());
        enderecar_multilista();
    }

    //cria uma lista com um hashmap para armazenar os anos e a quantidade de registros com esse ano
    private void add_year(Short year) {
        if (yearCountMap.containsKey(year)) {
            int count = yearCountMap.get(year);
            yearCountMap.put(year, count + 1);
        } else {
            // yearEndList.add(pointer);
            yearList.add(year);
            yearCountMap.put(year, 1);
        }
    }

    //cria uma lista com um hashmap para armazenar os times e a quantidade de registros com esse time
    private void add_team(String team) {
        if (teamCountMap.containsKey(team)) {
            int count = teamCountMap.get(team);
            teamCountMap.put(team, count + 1);
        } else {
            //teamEndList.add(pointer);
            if (team.equals("Team Reptile")) {
                System.out.println("ok");
            }
            teamList.add(team);
            teamCountMap.put(team, 1);
        }
    }

    //Edereca os registros da multilista para apontar para o proximo registro com o mesmo ano ou estudio principal
    private void enderecar_multilista() throws IOException {
        rafIndexEmain.seek(0);
        int nrep;
        long pointer;
        byte[] b = new byte[50];
        //pecorre enderacando os anos
        for (Short year : yearList) {
            nrep = yearCountMap.get(year);
            rafIndexEmain.seek(0);
            pointer = 0;
            yearEndList.clear();
            for (int i = 0; i < nrep;) {
                rafIndexEmain.readBoolean();
                rafIndexEmain.readLong();
                if (rafIndexEmain.readShort() == year) {
                    pointer = rafIndexEmain.getFilePointer() - 11;
                    yearEndList.add(pointer);
                    pointer = rafIndexEmain.getFilePointer() + 66;
                    rafIndexEmain.seek(pointer);
                    i++;
                } else {
                    pointer = rafIndexEmain.getFilePointer() + 66;
                    rafIndexEmain.seek(pointer);
                }
            }
            for (int i = 0; i < yearEndList.size(); i++) {
                rafIndexEmain.seek(yearEndList.get(i) + 11);

                if (i + 1 == yearEndList.size()) {
                    rafIndexEmain.writeLong(-1);
                } else {
                    rafIndexEmain.writeLong(yearEndList.get(i + 1));
                }

            }

        }
        //pecorre enderecando os estudios
        rafIndexEmain.seek(0);
        for (String team : teamList) {
            nrep = teamCountMap.get(team);
            rafIndexEmain.seek(0);
            pointer = 0;
            teamEndList.clear();
            //add a lista os enderecos dos registros com o mesmo estudio
            for (int i = 0; i < nrep;) {
                //vai para posicao do estudio
                rafIndexEmain.seek(rafIndexEmain.getFilePointer() + 19);
                rafIndexEmain.read(b);
                if (new String(b, StandardCharsets.UTF_8).trim().equals(team)) {
                    pointer = rafIndexEmain.getFilePointer() - 69;
                    teamEndList.add(pointer);
                    pointer = rafIndexEmain.getFilePointer() + 8;
                    rafIndexEmain.seek(pointer);
                    i++;
                } else {
                    pointer = rafIndexEmain.getFilePointer() + 8;
                    rafIndexEmain.seek(pointer);
                }//hereok
            }
            for (int i = 0; i < teamEndList.size(); i++) {
                rafIndexEmain.seek(teamEndList.get(i) + 69);

                if (i + 1 == teamEndList.size()) {
                    rafIndexEmain.writeLong(-1);
                } else {
                    rafIndexEmain.writeLong(teamEndList.get(i + 1));
                }

            }

        }
        //cria index de anos e times
        index_year();
        index_team();

    }

    //cria index de anos
    private void index_year() throws IOException {
        Short year;
        rafIndexEyear.seek(0);
        rafIndexEmain.seek(0);

        for (int i = 0; i < yearList.size(); i++) {
            year = yearList.get(i);
            rafIndexEyear.writeShort(year);
            rafIndexEyear.writeShort(yearCountMap.get(year));
            rafIndexEmain.seek(0);
            while (rafIndexEmain.getFilePointer() < rafIndexEmain.length()) {
                rafIndexEmain.readBoolean();
                rafIndexEmain.readLong();
                if (rafIndexEmain.readShort() == year) {
                    rafIndexEyear.writeLong(rafIndexEmain.getFilePointer() - 11);
                    break;
                } else {
                    rafIndexEmain.seek(rafIndexEmain.getFilePointer() + 66);
                }

            }
        }
    }

    //cria index de times
    private void index_team() throws IOException {
        String team;
        rafIndexETeam.seek(0);
        rafIndexEmain.seek(0);

        // Boolean ok = false;
        for (int i = 0; i < teamList.size(); i++) {
            // ok = false;
            team = teamList.get(i);
            byte[] b = team.getBytes();
            byte[] fixedSizeBytes = new byte[50];
            System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 50));
            rafIndexETeam.write(fixedSizeBytes);
            rafIndexETeam.writeShort(teamCountMap.get(team));
            rafIndexEmain.seek(0);
            while (rafIndexEmain.getFilePointer() < rafIndexEmain.length()) {
                rafIndexEmain.seek(rafIndexEmain.getFilePointer() + 19);
                byte[] b2 = new byte[50];
                rafIndexEmain.read(b2);
                if (new String(b2, StandardCharsets.UTF_8).trim().equals(team)) {
                    rafIndexETeam.writeLong(rafIndexEmain.getFilePointer() - 69);
                    // ok = true;
                    break;
                } else {
                    rafIndexEmain.seek(rafIndexEmain.getFilePointer() + 8);
                }
            }
            // if(!ok)
            //         System.err.println("Erro");
        }

    }

    //retorna se encontrou o ano e cria uma lista com os endereços dos registros com o mesmo ano
    private Boolean get_year(Short year) throws IOException {
        Short nRepYear;
        Long intial_pointer, pointer;
        yearEndList.clear();
        rafIndexEyear.seek(0);
        rafIndexEmain.seek(0);
        //Encontra ano e ponteiro inical no index de anos
        while (rafIndexEyear.getFilePointer() < rafIndexEyear.length()) {
            if (rafIndexEyear.readShort() == year) {
                nRepYear = rafIndexEyear.readShort();
                intial_pointer = rafIndexEyear.readLong();
                //vai para o ponteiro inicial
                rafIndexEmain.seek(intial_pointer);
                yearEndList.add(intial_pointer);

                //pecorre os registros com o mesmo ano
                for (int i = 1; i < nRepYear; i++) {
                    rafIndexEmain.seek(rafIndexEmain.getFilePointer() + 11);
                    pointer = rafIndexEmain.readLong();
                    rafIndexEmain.seek(pointer);
                    yearEndList.add(pointer);
                }
                return true;

            } else {
                rafIndexEyear.seek(rafIndexEyear.getFilePointer() + 10);
            }

        }
        return false;
    }

    //retorna se encontrou o time e cria uma lista com os endereços dos registros com o mesmo time
    private Boolean get_team(String team) throws IOException {
        Short nRepTeam;
        Long intial_pointer, pointer;
        teamEndList.clear();
        rafIndexETeam.seek(0);
        rafIndexEmain.seek(0);
        byte[] b = team.getBytes();
        byte[] fixedSizeBytes = new byte[50];
        System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 50));
        //Encontra estudio e ponteiro inical no index de estudios
        while (rafIndexETeam.getFilePointer() < rafIndexETeam.length()) {
            rafIndexETeam.read(fixedSizeBytes);
            if (new String(fixedSizeBytes, StandardCharsets.UTF_8).trim().equals(team)) {
                nRepTeam = rafIndexETeam.readShort();
                intial_pointer = rafIndexETeam.readLong();
                //vai para o ponteiro inicial
                rafIndexEmain.seek(intial_pointer);
                teamEndList.add(intial_pointer);

                //pecorre os registros com o mesmo estudio
                for (int i = 1; i < nRepTeam; i++) {
                    rafIndexEmain.seek(rafIndexEmain.getFilePointer() + 69);
                    pointer = rafIndexEmain.readLong();
                    rafIndexEmain.seek(pointer);
                    teamEndList.add(pointer);
                }
                return true;

            } else {
                rafIndexETeam.seek(rafIndexETeam.getFilePointer() + 10);
            }

        }
        return false;
    }

    //rotorna a intercecao entre os registros com o mesmo ano e estudio principal
    public List<Long> getCommonElements() {
        ArrayList<Long> commonElements = new ArrayList<>(yearEndList);
        commonElements.retainAll(teamEndList);
        return commonElements;
    }

    public void saveindexE(Game r, Game old) throws IOException {
        Short nRepYear;
        long endNew;

        if (r.getId() < 0) {
            //insercao
            // add ao E main
            rafIndexEmain.seek(rafIndexEmain.length());
            endNew = rafIndexEmain.getFilePointer();
            rafIndexEmain.writeBoolean(false);
            rafIndexEmain.writeLong(r.getEnd_DB());
            rafIndexEmain.writeShort(r.getrelease_year());
            rafIndexEmain.writeLong(-1);
            for (String studio : r.getteam()) {
                byte[] b = studio.getBytes();
                byte[] fixedSizeBytes = new byte[50];
                System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 50));
                rafIndexEmain.write(fixedSizeBytes);
                break;
            }
            rafIndexEmain.writeLong(-1);
            //add ao E year
            if (get_year(r.getrelease_year())) {
                rafIndexEyear.seek(0);

                while (rafIndexEyear.getFilePointer() < rafIndexEyear.length()) {
                    if (rafIndexEyear.readShort() == r.getrelease_year()) {
                        nRepYear = rafIndexEyear.readShort();
                        rafIndexEyear.seek(rafIndexEyear.getFilePointer() - 2);
                        rafIndexEyear.writeShort(nRepYear + 1);
                        break;
                    }
                    rafIndexEyear.seek(rafIndexEyear.getFilePointer() + 10);
                }
            } else {
                rafIndexEyear.writeShort(r.getrelease_year());
                rafIndexEyear.writeShort(1);
                rafIndexEyear.writeLong(endNew);
            }
            //add ao E team
            if (get_team(r.getteam().get(0))) {
                rafIndexETeam.seek(0);

                while (rafIndexETeam.getFilePointer() < rafIndexETeam.length()) {
                    byte[] b = r.getteam().get(0).getBytes();
                    byte[] fixedSizeBytes = new byte[50];
                    System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 50));
                    rafIndexETeam.read(fixedSizeBytes);
                    if (new String(fixedSizeBytes, StandardCharsets.UTF_8).trim().equals(r.getteam().get(0))) {
                        nRepYear = rafIndexETeam.readShort();
                        rafIndexETeam.seek(rafIndexETeam.getFilePointer() - 2);
                        rafIndexETeam.writeShort(nRepYear + 1);
                        break;
                    }
                    rafIndexETeam.seek(rafIndexETeam.getFilePointer() + 10);
                }
            } else {
                byte[] b = r.getteam().get(0).getBytes();
                byte[] fixedSizeBytes = new byte[50];
                System.arraycopy(b, 0, fixedSizeBytes, 0, Math.min(b.length, 50));
                rafIndexETeam.write(fixedSizeBytes);
                rafIndexETeam.writeShort(1);
                rafIndexETeam.writeLong(endNew);
            }
            //Reenderacamento por ano 
            if (yearEndList.size() > 1) {
                rafIndexEmain.seek(yearEndList.get(yearEndList.size() - 1) + 11);
                rafIndexEmain.writeLong(endNew);
            }
            //Reenderacamento por estudio
            if (teamEndList.size() > 1) {
                rafIndexEmain.seek(teamEndList.get(teamEndList.size() - 1) + 69);
                rafIndexEmain.writeLong(endNew);
            }

        } else {
            //atualizacao
            if (r.getrelease_year() != old.getrelease_year()) {
                Long pointer = r.getEnd_DB();
                while (rafIndexEmain.getFilePointer() < rafIndexEmain.length()) {
                    if (rafIndexEmain.readBoolean()) {
                        continue;
                    }
                    if (pointer == rafIndexEmain.readLong()) {
                        rafIndexEmain.seek(rafIndexEmain.getFilePointer() - 8);
                        rafIndexEmain.writeLong(r.getEnd_DB());
                        break;
                    }

                }
            }
        }
    }

    //////////////////////////Compressao//////////////////////////////

// public void comprimir(Byte version) throws FileNotFoundException, IOException {
//         String Compress_NAME_OUTPUT = ".\\data.compressed[" + version + "].db";
//         Path CompressPath = Paths.get(Compress_NAME_OUTPUT);
//         RandomAccessFile rafC;
//         rafC = new RandomAccessFile(CompressPath.toFile(), "rw");
//         raf.seek(0);
//         create_dicionario();

//         rafC.close();
//     }

//     private void create_dicionario() {
//         createGrandLine();
//         String dicionario = new String();
//         char c;
//     }

//     private void createGrandLine() {}


    //////////////////////////Casamento de padroes////////////////////
    



public void procurar(String input) throws IOException {
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        createGandTLine();
        System.out.println("Informe o metodo para procurar o padrão: ");
        System.out.println("1. Força Bruta, 2. KMP, 3. BM, 4. Todos");
        Integer op = scan.nextInt();
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        switch (op) {
            case 1 ->
                bruteForce(input);
            case 2 ->
                KMP(input);
            case 3 ->
                BM(input);
            case 4 -> {
                bruteForce(input);
                KMP(input);
                BM(input);
            }
            default -> {
                System.out.print("\033c");// Limpa a tela(ANSI escape character)
                System.out.println("Op\u00E7\u00E3o inv\u00E1lida");
            }
        }
    }

    //cria uma string com todos os titulos e reviews do banco de dados
    private void createGandTLine() throws IOException {
        Game game;
        grandTline = "";
        raf.seek(0);
        raf.readInt();
        do {
            game = bdToRam();
            grandTline += game.gettitle();
            grandTline += game.getreview();
        } while (raf.getFilePointer() < raf.length());
    }

    //algoritmo de força bruta
    private void bruteForce(String input) {
        long startTime = System.nanoTime(); // Calcula o tempo de execução
        int q = 0;//contador de padroes encontrados
        int comp = 0;
        int n = grandTline.length();
        int m = input.length();
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while ((j < m) && (grandTline.charAt(i + j) == input.charAt(j))) {
                j++;
                comp++;
            }
            if (j == m) {
                comp++;
                System.out.println("Padrão encontrado na posição " + i);
                q++;
            }
        }

        long endTime = System.nanoTime(); // Fim do cálculo do tempo
        long duration = endTime - startTime; // Tempo total
        System.out.println("Encontrado o padrao " + q + " vezes\nRealizando " + comp + " comparaçoes \nTempo de execução força bruta: " + duration + " nanosegundos\\n"
                + //
                " __________________________________________________");
    }

    private void KMP(String input) {
        long startTime = System.nanoTime(); // Calcula o tempo de execução
        int q = 0;
        int comp = 0;
        int n = grandTline.length();
        int m = input.length();
        int[] pi = new int[m];
        calculaFuncaoKMP(input, pi);
        // int qtd = 0;
        int i = 0;
        //loop externo para percorrer o banco
        for (int j = 0; j < n; j++) {
            //loop interno para percorrer o padrao com base no pi
            comp++;
            while (i > 0 && input.charAt(i) != grandTline.charAt(j)) {
                if (pi[i - 1] == -1) {
                    i = 0;
                } else {
                    i = pi[i - 1];
                }
                comp++;
            }
            //pula pra proxima letra incrementando posiçao de i no pi
            comp++;
            if (input.charAt(i) == grandTline.charAt(j)) {
                i++;
            }
            //se i for igual ao tamanho do padrao, o padrao foi encontrado
            if (i == m) {
                System.out.println("Padrão encontrado na posição " + (j - m + 1));
                q++;
                i = pi[i - 1];
            }
        }
        long endTime = System.nanoTime(); // Fim do cálculo do tempo
        long duration = endTime - startTime; // Tempo total
        System.out.println("Encontrado o padrao " + q + " vezes\n Realizando " + comp + " comparaçoes \nTempo de execução KMP: " + duration + " nanosegundos\n __________________________________________________");
    }

    //calcula a funcao para o algoritmo de KMP com melhora
    private void calculaFuncaoKMP(String padrao, int[] pi) {
        int m = padrao.length();
        int k = 0;
        pi[0] = 0;
        for (int q = 1; q < m; q++) {
            while (k > 0 && padrao.charAt(k) != padrao.charAt(q)) {
                k = pi[k - 1];
            }
            if (padrao.charAt(k) == padrao.charAt(q)) {
                k++;
            }
            pi[q] = k;
        }
        //Corrige funçao para kmp melhorar a eficiencia
        for (int i = 1; i < m; i++) {
            if (padrao.charAt(i) == padrao.charAt(pi[i - 1])) {
                if (pi[i - 1] == 0) {
                    pi[i - 1] = -1;
                } else {
                    pi[i - 1] = pi[pi[i - 1] - 1];

                }

            }
        }
    }

    private void BM(String input) {
        long startTime = System.nanoTime(); // Calcula o tempo de execução
        int q = 0;
        int comp = 0;
        int n = grandTline.length();
        int m = input.length();
        int[] ruimC = new int[256];
        int[] bomC = new int[m];
        //calcula os carateres bom e ruim
        caraterRuim(input, ruimC);
        caraterBom(input, bomC);
        int i = 0;
        //loop externo para percorrer o banco
        while (i <= n - m) {
            int j = m - 1;
            comp++;
            while (j >= 0 && input.charAt(j) == grandTline.charAt(i + j)) {
                j--;
                comp++;
            }
            if (j < 0) {
                System.out.println("Padrão encontrado na posição " + i);
                q++;
                i += bomC[0];
            } else {
                //compara desloacamento com base no carater bom e ruim para escolher o maior
                i += Math.max(bomC[j], ruimC[grandTline.charAt(i + j)] - m + 1 + j);
            }
        }
        long endTime = System.nanoTime(); // Fim do cálculo do tempo
        long duration = endTime - startTime; // Tempo total
        System.out.println("Encontrado o padrao " + q + " vezes\nRealizando " + comp + " comparaçoes \nTempo de execução BM: " + duration + " nanosegundos\n"
                + //
                " __________________________________________________");
    }
    //funcao auxiliar para o algoritmo de BM para calcular o carater ruim 

    private void caraterRuim(String input, int[] ruimC) {
        int m = input.length();
        for (int i = 0; i < ruimC.length; i++) {
            ruimC[i] = m;
        }
        for (int i = 0; i < m - 1; i++) {
            ruimC[input.charAt(i)] = m - i - 1;
        }
    }

    //funcao auxiliar para o algoritmo de BM para calcular o careter bom
    private void caraterBom(String input, int[] bomC) {
        int m = input.length();
        int[] suff = new int[m];
        suff = suffix(input);
        for (int i = 0; i < m; i++) {
            bomC[i] = m;
        }
        int j = 0;
        for (int i = m - 1; i >= 0; i--) {
            if (suff[i] == i + 1) {
                for (; j < m - 1 - i; j++) {
                    if (bomC[j] == m) {
                        bomC[j] = m - 1 - i;
                    }
                }
            }
        }
        for (int i = 0; i <= m - 2; i++) {
            bomC[m - 1 - suff[i]] = m - 1 - i;
        }
    }

    //funcao auxiliar para o algoritmo de BM para calcular o sufixo
    private int[] suffix(String input) {
        int m = input.length();
        int[] suff = new int[m];
        suff[m - 1] = m;
        int g = m - 1;
        int f = 0;
        for (int i = m - 2; i >= 0; i--) {
            if (i > g && suff[i + m - 1 - f] < i - g) {
                suff[i] = suff[i + m - 1 - f];
            } else {
                if (i < g) {
                    g = i;
                }
                f = i;
                while (g >= 0 && input.charAt(g) == input.charAt(g + m - 1 - f)) {
                    g--;
                }
                suff[i] = f - g;
            }
        }
        return suff;
    }

    //////////////////////////Criptografia////////////////////

public void descriptografar() throws Exception {
        Game game;
        int opc;
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.println("Informe id do registro a ter o review descriptografado: ");
        opc = scan.nextInt();
        game = getById(opc);
        decifraColunas(game);
        getById(opc);
        decifraCezar(game);
    }

    public void criptografar() throws Exception {
        Game game;
        int opc;
        System.out.print("\033c");// Limpa a tela(ANSI escape character)
        System.out.println("Informe id do registro a ter o review criptografado: ");
        opc = scan.nextInt();
        game = getById(opc);
        cifraCezar(game);
        getById(opc);
        cifraColunas(game);
        System.out.println("Review criptografado com sucesso");
    }

    private void cifraCezar(Game game) throws IOException {

        byte[] b;

        String input = game.getreview();
        Integer deslocamento = 3;
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    c = (char) (c + deslocamento);
                    if (c > 'z') {
                        c = (char) (c - 26);
                    } else if (c < 'a') {
                        c = (char) (c + 26);
                    }
                } else if (Character.isUpperCase(c)) {
                    c = (char) (c + deslocamento);
                    if (c > 'Z') {
                        c = (char) (c - 26);
                    } else if (c < 'A') {
                        c = (char) (c + 26);
                    }
                }
            }
            output += c;
        }
        game.setreview(output);
        b = game.toByteArray();
        raf.writeBoolean(false);
        raf.writeShort(b.length);
        raf.write(b);
    }

    private void decifraCezar(Game game) throws IOException {
        byte[] b;
        String input = game.getreview();
        Integer deslocamento = 3;
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    c = (char) (c - deslocamento);
                    if (c > 'z') {
                        c = (char) (c - 26);
                    } else if (c < 'a') {
                        c = (char) (c + 26);
                    }
                } else if (Character.isUpperCase(c)) {
                    c = (char) (c - deslocamento);
                    if (c > 'Z') {
                        c = (char) (c - 26);
                    } else if (c < 'A') {
                        c = (char) (c + 26);
                    }
                }
            }
            output += c;
        }
        game.setreview(output);
        b = game.toByteArray();
        raf.writeBoolean(false);
        raf.writeShort(b.length);
        raf.write(b);
    }

    private void cifraColunas(Game game) {
        byte[] b;
        String input = game.getreview();
        String output = "";
        int n = 3;
        int m = (int) Math.ceil((double) input.length() / n);
        char[][] matriz = new char[m][n];
        int k = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n && k < input.length(); j++) {
                matriz[i][j] = input.charAt(k);
                k++;
            }
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                output += matriz[i][j];
            }
        }
        game.setreview(output);
        try {
            b = game.toByteArray();
            raf.writeBoolean(false);
            raf.writeShort(b.length);
            raf.write(b);
        } catch (IOException e) {
            System.out.println("Erro");
        }
    }

    private void decifraColunas(Game game) {
        byte[] b;
        String input = game.getreview();
        String output = "";
        int n = 3;
        int m = (int) Math.ceil((double) input.length() / n);
        char[][] matriz = new char[m][n];
        int k = 0;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m && k < input.length(); i++) {
                matriz[i][j] = input.charAt(k);
                k++;
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                output += matriz[i][j];
            }
        }
        game.setreview(output);
        try {
            b = game.toByteArray();
            raf.writeBoolean(false);
            raf.writeShort(b.length);
            raf.write(b);
        } catch (IOException e) {
            System.out.println("Erro");
        }
    }
}
