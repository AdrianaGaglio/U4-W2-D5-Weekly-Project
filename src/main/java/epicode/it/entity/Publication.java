package epicode.it.entity;

import epicode.it.service.Archive;

import java.util.Random;

abstract public class Publication {


    public static String generateRandomString(int length) {
        // caratteri per generare la stringa
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";

        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            // Estrai un carattere casuale dalla stringa alfanumerica
            int index = random.nextInt(alphanumeric.length());
            result.append(alphanumeric.charAt(index));
        }
        return result.toString();
    }

    private String ISBN = generateRandomString(10);
    private String title;
    private int year;
    private int pages;

    public Publication(String title, int year, int pages) {
        this.title = title;
        this.year = year;
        this.pages = pages;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getPages() {
        return pages;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "Publication{" +
                "ISBN='" + ISBN + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", pages=" + pages +
                '}';
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
}
