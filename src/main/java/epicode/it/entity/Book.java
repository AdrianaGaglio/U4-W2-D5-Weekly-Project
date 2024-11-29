/*
* NELLA CARTELLA ARCHIVE DELLA ROOT SONO PREDISPOSTI DEI FILE TXT PER L'ARCHIVIO E PER LA PERSONAL ARCHIVE
* AL CARICAMENTO, VIENE GENERATO COSì UN ARCHIVIO DI PARTENZA ED UNA LISTA PER L'UTENTE DOVE è POSSIBILE SALVARE LE PUBBLICATIONI
* HO LASCIATO GIà DELLE COSE SALVATE NELLA LSITA PERSONALE COSì DA MOSTRARE QUALCOSA, MA è POSSIBILE CANCELLARE ED AGGIUNGERE
* TRAMITE IL MENù DI SELEZIONE
* */

package epicode.it.entity;

public class Book extends Publication {
    private String author;
    private String genre;

    public Book(String title, int year, int pages, String author, String genre) {
        super(title, year, pages);
        this.author = author;
        this.genre = genre;
    }

    public Book(String ISBN, String title, int year, int pages, String author, String genre) {
        super(title, year, pages);
        this.author = author;
        this.genre = genre;
        this.setISBN(ISBN);
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }


    public void setGenre(String author) {
        this.genre = genre;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + this.getISBN() + '\'' +
                ", title='" + this.getTitle() + '\'' +
                ", author='" + this.getAuthor() + '\'' +
                ", genre='" + this.getGenre() + '\'' +
                ", pages='" + this.getPages() + '\'' +
                ", year='" + this.getYear() + '\'' +
                '}';
    }
}
