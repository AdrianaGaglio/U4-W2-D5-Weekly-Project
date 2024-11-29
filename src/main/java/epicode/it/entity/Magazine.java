package epicode.it.entity;

public class Magazine extends Publication{
    private Periodicity periodicity;

    public Magazine(String title, int year, int pages) {
        super(title, year, pages);
    }

    public Magazine(String title, int year, int pages, Periodicity periodicity) {
        super(title, year, pages);
        this.periodicity = periodicity;
    }

    public Magazine(String ISBN, String title, int year, int pages, Periodicity periodicity) {
        super(title, year, pages);
        this.periodicity = periodicity;
        this.setISBN(ISBN);
    }

    public Periodicity getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Periodicity periodicity) {
        this.periodicity = periodicity;
    }

    @Override
    public String toString() {
        return "Magazine{" +
                "ISBN='" + this.getISBN() + '\'' +
                ", title='" + this.getTitle() + '\'' +
                ", pages='" + this.getPages() + '\'' +
                ", year='" + this.getYear() + '\'' +
                ", periodicity='" + this.getPeriodicity() + '\'' +
                '}';
    }
}
