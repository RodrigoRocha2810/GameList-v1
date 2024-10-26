
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Game {

    private Integer id;

    private String title;

    private short release_Date;

    private List<String> team;

    private Float rating;

    private Integer nreviews;

    private long end_DB;

    private Integer wishlist;

    private List<String> genres;

    private String review;

    private Date cadastro;

    private Boolean excluido = false;

    public Game() {
        this.id = -1;
        this.title = "";
        this.genres = new ArrayList<>();
        this.rating = 0f;
        this.nreviews = 0;
        this.release_Date = 0;
        this.review = "";
        this.team = new ArrayList<>();
        this.cadastro = null;
        this.excluido = false;
        this.wishlist = 0;
        this.end_DB = -1;
    }
    //metodo para clonar um objeto game
    public Game(Game other) {
        this.id = other.id;
        this.title = other.title;
        this.genres = other.genres;
        this.rating = other.rating;
        this.nreviews = other.nreviews;
        this.release_Date = other.release_Date;
        this.review = other.review;
        this.team = other.team;
        this.cadastro = other.cadastro;
        this.excluido = other.excluido;
        this.wishlist = other.wishlist;
        this.end_DB = other.end_DB;
    }


    public Game(String csvLine) {
        // Assuming the CSV format is: id, title, team, rating, nreviews, wishlist, genres, review
        String[] values = csvLine.split(";"); // Split by comma or any delimiter

        this.id = Integer.valueOf(values[0].trim()); // Convert String to Integer
        this.title = values[1].trim(); // Parse title

        // Parse release_Date as a short
        this.release_Date = calculateDays(values[2]);

        // Parse the team into a List<String> (assuming team members are separated by semicolons)
        this.team = Arrays.asList(values[3].trim().split(":"));

        this.rating = Float.valueOf(values[4].trim()); // Convert to Float
        this.nreviews = Integer.valueOf(values[5].trim()); // Convert to Integer
        this.wishlist = Integer.valueOf(values[6].trim()); // Convert to Integer

        // Parse the genres into a List<String> (assuming genres are separated by semicolons)
        this.genres = Arrays.asList(values[7].trim().split(":"));

        this.review = values[8].trim();  // Trim the review string
    }

    @Override
    public String toString() {
        return """
               Registro encontrado!
               id=""" + id
                + ", title='" + title + '\''
                + ", release_Date=" + release_Date
                + ", team=" + team
                + ", rating=" + rating
                + ", nreviews=" + nreviews
                + ", wishlist=" + wishlist
                + ", genres=" + genres
                + ", review='" + review;
    }
    //caldula o numero de dias do lacamento desde 1970 ate 2025
    public static short calculateDays(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate inputDate = LocalDate.parse(dateString, formatter);

        LocalDate referenceDate = LocalDate.of(2025, 1, 1);

        long daysBetween = ChronoUnit.DAYS.between(inputDate, referenceDate);

        return (short) daysBetween;
    }

// Tranforma o objeto game para um vetor de bytes seguindo as regras de escrita
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.title);
        dos.writeShort(this.release_Date);
        // Indicador de tamnho do campo de tam variado do time
        dos.writeByte(this.team.size());
        for (String teams : this.team) {
            dos.writeUTF(teams);
        }
        ////
        dos.writeFloat(this.rating);
        dos.writeInt(this.nreviews);
        dos.writeInt(this.wishlist);
        // Indicador de tamnho do campo de tam variado dos generos
        dos.writeByte(this.genres.size());
        for (String genre : this.genres) {
            dos.writeUTF(genre);
        }
        ////
        dos.writeUTF(this.review);
        return baos.toByteArray();
    }






    // Getters
    public Integer getId() {
        return id;
    }

    public String gettitle() {
        return title;
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

    public long getEnd_DB() {
        return end_DB;
    }   

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void settitle(String title) {
        this.title = title;
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

    public void setrelease_Date(short release_Date) {
        this.release_Date = release_Date;

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

    public void setEnd_DB(long end_DB) {
        this.end_DB = end_DB;
    }
}
