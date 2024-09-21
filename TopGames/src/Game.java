import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class Game {
    private Integer id;

    private String titulo;

    private short release_Date;

    private List<String> team;

    private Float rating;

    private Integer nreviews;

    //add
    private Integer wishlist;

    private List<String> genres;

    private String review;

    

    

    


    

    

    private Date cadastro;

    private Boolean excluido = false;

    
     public Game() {
        this.id = -1;
        this.titulo = "";
        this.genres = new ArrayList<String>();
        this.rating = 0f;
        this.nreviews = 0;
        this.release_Date = 0;
        this.review = "";
        this.team = new ArrayList<String>();
        this.cadastro = null;
        this.excluido = false;
    }
    
    @Override
    public String toString() {
        return "Registro encontrado!\n" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", genres=" + genres +
                ", rating=" + rating +
                ", nreviews=" + nreviews +
                ", release_Date=" + release_Date +
                ", review='" + review + '\'' +
                ", team=" + team +
                ", cadastro=" + cadastro ;
    }
    // Getters
    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<String> getgenres() {
        return genres;
    }

    public Float getrating() {
        return rating;
    }

    public Integer getnreviews() {
        return nreviews;
    }

    public short getrelease_Date() {
        return release_Date;
    }

    public String getreview() {
        return review;
    }

    public List<String> getteam() {
        return team;
    }

    public Date getCadastro() {
        return cadastro;
    }

    public Boolean getExcluido() {
        return excluido;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setgenres(List<String> genres) {
        this.genres = genres;
    }

    public void setrating(Float rating) {
        this.rating = rating;
    }

    public void setwishlist(int wishlist) {
        this.wishlist = wishlist;
    }

    public void setnreviews(Integer nreviews) {
        this.nreviews = nreviews;
    }

    public void setrelease_Date(String release_Date) {
        this.release_Date = calculateDays(release_Date);
        
    }

    public void setreview(String review) {
        this.review = review;
    }

    public void setteam(List<String> team) {
        this.team = team;
    }

    public void setCadastro(Date cadastro) {
        this.cadastro = cadastro;
    }

    public void setExcluido(Boolean excluido) {
        this.excluido = excluido;
    }
    
    
    //caldula o numero de dias do lacamento desde 1970
    public static short calculateDays(String dateString) {
  
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
     
        LocalDate inputDate = LocalDate.parse(dateString, formatter);
        

        LocalDate referenceDate = LocalDate.of(2025, 1, 1);
        
       
        long daysBetween = ChronoUnit.DAYS.between(inputDate, referenceDate);
      
        return (short) daysBetween;
    }
    

    
    
    

    // public byte[] gerarRegistro() {
    //     byte[] retorno, aux;

    //     // retorno = BytesUtils.toBytes(excluido);
    //     retorno = new byte[0];

    //     aux = BytesUtils.toBytes(id);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = BytesUtils.toBytes(Short.valueOf(Integer.valueOf(titulo.length()).shortValue()));
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = BytesUtils.toBytes(titulo);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = BytesUtils.toBytes(rating);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = BytesUtils.toBytes(nreviews);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     if (Objects.nonNull(genres) && genres.size() > 0) {

    //         aux = BytesUtils.toBytes(Short.valueOf(Integer.valueOf(genres.size()).shortValue()));
    //         retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //         System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //         for (String gen : genres) {
    //             aux = BytesUtils.toBytes(Short.valueOf(Integer.valueOf(gen.length()).shortValue()));
    //             retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //             System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //             aux = BytesUtils.toBytes(gen);
    //             retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //             System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);
    //         }

    //     } else {
    //         aux = BytesUtils.toBytes(Short.valueOf("0"));
    //         retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //         System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);
    //     }

    //     aux = BytesUtils.toBytes(Short.valueOf(Integer.valueOf(review.length()).shortValue()));
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = BytesUtils.toBytes(review);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = BytesUtils.toBytes(release_Date);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = BytesUtils.toBytes(anoEncerramento);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     if (Objects.nonNull(team) && team.size() > 0) {

    //         aux = BytesUtils.toBytes(Short.valueOf(Integer.valueOf(team.size()).shortValue()));
    //         retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //         System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //         for (String e : team) {
    //             aux = BytesUtils.toBytes(Short.valueOf(Integer.valueOf(e.length()).shortValue()));
    //             retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //             System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //             aux = BytesUtils.toBytes(e);
    //             retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //             System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);
    //         }

    //     } else {
    //         aux = BytesUtils.toBytes(Short.valueOf("0"));
    //         retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //         System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);
    //     }

    //     if (Objects.isNull(cadastro))
    //         gerarDataCadastro();

    //     aux = BytesUtils.toBytes(Objects.isNull(cadastro) ? 0L : cadastro.getTime());
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = retorno;
    //     retorno = BytesUtils.toBytes(Short.valueOf(Integer.valueOf(retorno.length).shortValue()));
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     aux = retorno;
    //     retorno = BytesUtils.toBytes(excluido);
    //     retorno = Arrays.copyOf(retorno, retorno.length + aux.length);
    //     System.arraycopy(aux, 0, retorno, retorno.length - aux.length, aux.length);

    //     return retorno;
    // }

    
    // public static Game desserializar(byte[] dados) {
    //     Game retorno = new Game();

    //     byte[] auxInt = new byte[4], auxLong = new byte[8], auxShort = new byte[2], auxString;
    //     Short tam = 0, field = 1, count = 0;
    //     Integer pos = 0;

    //     while (pos < dados.length) {
    //         switch (field) {
    //             case 1:
    //                 System.arraycopy(dados, pos, auxInt, 0, auxInt.length);
    //                 retorno.setId(BytesUtils.fromBytes(auxInt, retorno.getId().getClass()));
    //                 pos += auxInt.length;
    //                 break;
    //             case 2:
    //                 System.arraycopy(dados, pos, auxShort, 0, auxShort.length);
    //                 tam = BytesUtils.fromBytes(auxShort, tam.getClass());
    //                 pos += auxShort.length;

    //                 auxString = new byte[tam];
    //                 System.arraycopy(dados, pos, auxString, 0, tam);
    //                 retorno.setTitulo(BytesUtils.fromBytes(auxString, retorno.getTitulo().getClass()));
    //                 pos += auxString.length;
    //                 break;
    //             case 3:
    //                 System.arraycopy(dados, pos, auxInt, 0, auxInt.length);
    //                 retorno.setrating(BytesUtils.fromBytes(auxInt, retorno.getrating().getClass()));
    //                 pos += auxInt.length;
    //                 break;
    //             case 4:
    //                 System.arraycopy(dados, pos, auxInt, 0, auxInt.length);
    //                 retorno.setnreviews(BytesUtils.fromBytes(auxInt, retorno.getnreviews().getClass()));
    //                 pos += auxInt.length;
    //                 break;
    //             case 5:
    //                 System.arraycopy(dados, pos, auxShort, 0, auxShort.length);
    //                 count = BytesUtils.fromBytes(auxShort, tam.getClass());
    //                 pos += auxShort.length;

    //                 if (Objects.isNull(retorno.getgenres())) {
    //                     retorno.setgenres(new ArrayList<String>());
    //                 }

    //                 for (int i = 0; i < count; i++) {
    //                     System.arraycopy(dados, pos, auxShort, 0, auxShort.length);
    //                     tam = BytesUtils.fromBytes(auxShort, tam.getClass());
    //                     pos += auxShort.length;

    //                     auxString = new byte[tam];
    //                     System.arraycopy(dados, pos, auxString, 0, tam);
    //                     retorno.getgenres().add(BytesUtils.fromBytes(auxString, retorno.getTitulo().getClass()));
    //                     pos += auxString.length;
    //                 }
    //                 break;
    //             case 6:
    //                 System.arraycopy(dados, pos, auxShort, 0, auxShort.length);
    //                 tam = BytesUtils.fromBytes(auxShort, tam.getClass());
    //                 pos += auxShort.length;

    //                 auxString = new byte[tam];
    //                 System.arraycopy(dados, pos, auxString, 0, tam);
    //                 retorno.setreview(BytesUtils.fromBytes(auxString, retorno.getTitulo().getClass()));
    //                 pos += auxString.length;
    //                 break;
    //             case 7:
    //                 System.arraycopy(dados, pos, auxInt, 0, auxInt.length);
    //                 retorno.setrelease_Date(BytesUtils.fromBytes(auxInt, retorno.getrelease_Date().getClass()));
    //                 pos += auxInt.length;
    //                 break;
    //             case 8:
    //                 System.arraycopy(dados, pos, auxInt, 0, auxInt.length);
    //                 retorno.setAnoEncerramento(BytesUtils.fromBytes(auxInt, retorno.getAnoEncerramento().getClass()));
    //                 pos += auxInt.length;
    //                 break;
    //             case 9:
    //                 System.arraycopy(dados, pos, auxShort, 0, auxShort.length);
    //                 count = BytesUtils.fromBytes(auxShort, tam.getClass());
    //                 pos += auxShort.length;

    //                 if (Objects.isNull(retorno.getteam())) {
    //                     retorno.setteam(new ArrayList<String>());
    //                 }

    //                 for (int i = 0; i < count; i++) {
    //                     System.arraycopy(dados, pos, auxShort, 0, auxShort.length);
    //                     tam = BytesUtils.fromBytes(auxShort, tam.getClass());
    //                     pos += auxShort.length;

    //                     auxString = new byte[tam];
    //                     System.arraycopy(dados, pos, auxString, 0, tam);
    //                     retorno.getteam().add(BytesUtils.fromBytes(auxString, retorno.getTitulo().getClass()));
    //                     pos += auxString.length;
    //                 }
    //                 break;
    //             case 10:
    //                 System.arraycopy(dados, pos, auxLong, 0, auxLong.length);
    //                 retorno.setCadastro(
    //                         new Date(BytesUtils.fromBytes(auxLong, Long.valueOf(0l).getClass()).longValue()));
    //                 pos += auxInt.length;
    //                 break;
    //             default:
    //                 pos++;
    //                 break;
    //         }

    //         field++;
    //     }
    //     return retorno;
    // }



}
