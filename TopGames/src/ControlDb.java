import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControlDb {
    private static final String DB_NAME_OUTPUT = ".\\data.games.db";

    private Path DbPath = Paths.get(DB_NAME_OUTPUT);

    private RandomAccessFile raf;

    private ArrayList<Integer> listaIds = new ArrayList<>();

    public ControlDb() throws Exception, FileNotFoundException {
        raf = new RandomAccessFile(DbPath.toFile(), "rw");
    }

    public void LoadCsv(String file){



    }

private Game CsvLineToGame(String linha) {
        Game retorno = new Game();

        try {
            String[] splited = linha.replace(";", ",").split("\",\"");
            for (int i = 0; i < splited.length; i++) {

                String s = splited[i].replace("\"", "");
                s = new String(s.getBytes(), StandardCharsets.UTF_8);
                switch (i) {
                    case 0:
                        retorno.setTitulo(s);
                        break;
                    case 1:
                        retorno.setGeneros(Stream.of(s.split(",")).collect(Collectors.toList()));
                        break;
                    case 2:
                        retorno.setAvaliacao(Float.parseFloat(s));
                        break;
                    case 3:
                        retorno.setVotos(Integer.parseInt(s.replace(",", "")));
                        break;
                    case 4:
                        break;
                    case 5:
                        retorno.setAnoLancamento(
                                Integer.parseInt(s.replace("(", "").replace(")", "").substring(0, 4)));

                        if (s.contains("–"))
                            retorno.setAnoEncerramento(s.indexOf(")") - s.indexOf("–") >= 4
                                    ? Integer.parseInt(s.substring(s.indexOf("–") + 1, s.indexOf(")")))
                                    : 0);
                        else
                            retorno.setAnoEncerramento(retorno.getAnoLancamento());
                        break;
                    case 6:
                        retorno.setResumo(s);
                        break;
                    case 7:
                        retorno.setElenco(Stream.of(s.split(",")).collect(Collectors.toList()));
                        break;
                }
                if (i > 7)
                    break;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return retorno;
    }


























}
