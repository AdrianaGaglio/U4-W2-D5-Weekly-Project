package epicode.it.service;

import epicode.it.entity.Book;
import epicode.it.entity.Magazine;
import epicode.it.entity.Periodicity;
import epicode.it.entity.Publication;
import epicode.it.service.exceptions.*;

import java.util.*;
import java.util.stream.Collectors;

public class Archive {
    private List<Publication> publicationList = new ArrayList<>();

    public List<Publication> getPublicationList() {
        return this.publicationList;
    }

    public void addToArchive(Publication p) throws SameIsbnException {

        boolean found = this.publicationList.stream().anyMatch(pub -> pub.getISBN().equals(
                p.getISBN()
        ));
        if (!found) {
            this.publicationList.add(p);
        } else {
            throw new SameIsbnException("This ISBN is already in the archive!");
        }
    }

    public Publication getPublicationByISBN(String ISBN) throws IsbnNotFoundException {
        return this.publicationList.stream().filter(p -> p.getISBN().equals(ISBN)).findFirst().orElseThrow(() -> new IsbnNotFoundException("Publication not found"));
    }

    public void deletePublication(String ISBN) throws IsbnNotFoundException {
        boolean found = this.publicationList.remove(getPublicationByISBN(ISBN));
        if (!found) {
            throw new IsbnNotFoundException("Publication not found");
        }
    }

    public Map<Integer, List<Publication>> getPublicationsGroupedByYear() throws PublicationNotFoundException {
        List<Publication> books = this.publicationList.stream().filter(p -> p instanceof Book).toList();
        return books.stream().collect(Collectors.groupingBy(Publication::getYear));
    }

    public List<Publication> getByYear(int year) throws PublicationNotFoundException {
        if (this.publicationList.stream().anyMatch(p -> p.getYear() == year)) {
            return this.publicationList.stream()
                    .filter(p -> p.getYear() == year)
                    .sorted(Comparator.comparing(Publication::getTitle))
                    .collect(Collectors.toList());
        } else {
            throw new PublicationNotFoundException("No publication found for this year");
        }
    }

    public List<Publication> getBooksByAuthor(String author) throws PublicationNotFoundException {
        if (this.publicationList.stream().anyMatch(p -> p instanceof Book && ((Book) p).getAuthor().contains(author))) {
            return this.publicationList.stream().filter(p -> p instanceof Book && ((Book) p).getAuthor().contains(author)).toList();
        } else {
            throw new PublicationNotFoundException("No publication found for this author");
        }
    }

    public Map<String, List<Publication>> getBooksByGenre() throws PublicationNotFoundException {
        List<Publication> books = this.publicationList.stream().filter(p -> p instanceof Book).toList();
        return books.stream().collect(Collectors.groupingBy(p -> ((Book) p).getGenre()));
    }

    public int getNumberOfBooks() {
        return this.publicationList.stream().filter(p -> p instanceof Book).toList().size();
    }

    public int getNumberOfMagazines() {
        return this.publicationList.stream().filter(p -> p instanceof Magazine).toList().size();
    }

    public Publication getMaxPages() {
        return this.publicationList.stream().max(Comparator.comparingInt(Publication::getPages)).orElse(null);
    }

    public double getAveragePages() {
        return this.publicationList.stream().mapToDouble(Publication::getPages).average().orElse(0);
    }

    public void updatePublicationInfo(String ISBN) throws PeriodicityException, IsbnNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Updating publication with ISBN: " + ISBN + "\n" + getPublicationByISBN(ISBN));
        Publication p = getPublicationByISBN(ISBN);
        if (p instanceof Book) {
            System.out.println("New title: (digit 'next' to skip)");
            String title = scanner.nextLine();
            if (!title.equals("next")) ((Book) p).setTitle(title);
            System.out.println("New author: (digit 'next' to skip)");
            String author = scanner.nextLine();
            if (!author.equals("next")) ((Book) p).setAuthor(author);
            System.out.println("New genre: (digit 'next' to skip)");
            String genre = scanner.nextLine();
            if (!genre.equals("next")) ((Book) p).setGenre(genre);
            System.out.println("New pages number: (digit 0 to skip)");
            int pages = scanner.nextInt();
            scanner.nextLine();
            if (pages != 0) ((Book) p).setPages(pages);
            System.out.println("New year: (digit 0 to skip)");
            int year = scanner.nextInt();
            scanner.nextLine();
            if (year != 0) ((Book) p).setYear(year);
            System.out.println(p);
        } else if (p instanceof Magazine) {
            System.out.println("New title: (digit 'next' to skip)");
            String title = scanner.nextLine();
            if (!title.equals("")) ((Magazine) p).setTitle(title);
            System.out.println("New pages number: (digit 'next' to skip");
            int pages = scanner.nextInt();
            scanner.nextLine();
            if (pages > 0) ((Magazine) p).setPages(pages);
            System.out.println("New year: (digit 0 to skip)");
            int year = scanner.nextInt();
            scanner.nextLine();
            if (year > 0) ((Magazine) p).setYear(year);
            System.out.println("Updated successfully!");
            System.out.println("New periodicity: 1-WEEKLY, 2-MONTHLY, 3-YEARLY (press enter to skip)");
            int select = scanner.nextInt();
            switch (select) {
                case 1 -> ((Magazine) p).setPeriodicity(Periodicity.WEEKLY);
                case 2 -> ((Magazine) p).setPeriodicity(Periodicity.MONTHLY);
                case 3 -> ((Magazine) p).setPeriodicity(Periodicity.HALF_YEARLY);
                default -> throw new PeriodicityException("Invalid periodicity!");
            }
        }



    }


}
