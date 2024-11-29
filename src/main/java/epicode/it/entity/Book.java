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
