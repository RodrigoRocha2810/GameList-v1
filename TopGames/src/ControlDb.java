import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public void LoadCsv(String CSVfile) throws IOException{
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
            //pula primeira linha(cabecerio)
            String line = bf.readLine();
            //loop para ler o csv linha a linha e converter no registro Game
            while ((line = bf.readLine()) != null) {
                Game registro = CsvLineToGame(line);
                if (registro.getTitulo().equals(""))
                    continue;
                raf.write(registro.gerarRegistro());
            }
        
        
        
        
        
        
        
        
        
        
        
        }
    }

private Game CsvLineToGame(String linha) {
        Game game = new Game();

        try {
            
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return game;
    }


























}
