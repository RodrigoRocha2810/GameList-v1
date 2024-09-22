
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
//classe para funcoes do crud e carregar o CSV

public class ControlDb {

    private Integer maxID = 0;

    private static final String DB_NAME_OUTPUT = ".\\data.games.db";

    private Path DbPath = Paths.get(DB_NAME_OUTPUT);

    private RandomAccessFile raf;

    private ArrayList<Integer> listaIds = new ArrayList<>();

    public ControlDb() throws Exception, FileNotFoundException {
        raf = new RandomAccessFile(DbPath.toFile(), "rw");
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
                    if(idReg == 127){
                        System.out.println("here");
                    }

                    if (Objects.equals(idReg, id) && !lapide) {
                        // Retorna 7 posições para retornar ao primeiro byte referente ao registro (byte
                        // de lápide)
                        // Sendo 7 bytes pela leitura do lápide (1 byte), leitura do tamanho (2 bytes) e
                        // leitura do ID para fazer a comparação (4 bytes)
                        raf.seek(raf.getFilePointer() - 7);
                        retorno = bdToRam();

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


    private Game bdToRam() throws IOException{
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

}
