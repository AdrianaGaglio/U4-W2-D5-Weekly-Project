package epicode.it;

import epicode.it.entity.Book;
import epicode.it.entity.Magazine;
import epicode.it.entity.Periodicity;
import epicode.it.entity.Publication;
import epicode.it.service.Archive;
import epicode.it.service.exceptions.IsbnNotFoundException;
import epicode.it.service.exceptions.PeriodicityException;
import epicode.it.service.exceptions.PublicationNotFoundException;
import epicode.it.service.exceptions.SameIsbnException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Application {
    public static final String ARCHIVE_PERSONAL_LIST_TXT = "archive/personalList.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static final String ARCHIVE_ARCHIVE_TXT = "archive/archive.txt";
    private static final Scanner scanner = new Scanner(System.in);

    public static int showMainMenu() {
        System.out.println("Select an option to proceed: (0 to exit program):");
        System.out.println("=> 1. Reload archive from file (this will overwrite the current archive if not saved yet)");
        System.out.println("=> 2. Show list of publications");
        System.out.println("=> 3. Add a new publication");
        System.out.println("=> 4. Search a publication by ISBN");
        System.out.println("=> 5. Delete a publication");
        System.out.println("=> 6. Update publication details");
        System.out.println("=> 7. Show publication grouped by year");
        System.out.println("=> 8. Search publication by year");
        System.out.println("=> 9. Search publication by author");
        System.out.println("=> 10. Show books grouped by genre");
        System.out.println("=> 11. Show Magazines list");
        System.out.println("=> 12. Show Books list");
        System.out.println("=> 13. Statistics");
        System.out.println("=> 14. Save changes");
        System.out.println("=> 15. Personal list");
        System.out.println();
        System.out.println("Select an option:");

        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }

    public static void reloadArchive(Archive archive, Archive personalArchive) {
        try {
            archive.getPublicationList().clear(); // Pulisce l'archivio esistente
            readSavedProducts(true).forEach(p -> archive.getPublicationList().add(p));
            personalArchive.getPublicationList().clear(); // Pulisce l'archivio esistente
            readSavedProducts(false).forEach(p -> personalArchive.getPublicationList().add(p));
            LOGGER.info("Publications loaded successfully.");
            System.out.println("Do you want to see the complete list? (y/n)");
            String answer = scanner.nextLine();

            if ("y".equals(answer)) {
                for (int i = 0; i < archive.getPublicationList().size(); i++) {
                    System.out.println(i + 1 + ". " + archive.getPublicationList().get(i));
                }
                System.out.println("==================================================");
                System.out.println("=> Select an option: 1. Save a publication to personal list 2. Go back to main menu");
                int subOption = scanner.nextInt();
                scanner.nextLine();

                if (subOption == 1) {
                    boolean valid = false;
                    while (!valid) {
                        System.out.println("Enter the number of the publication to save:");
                        int pubIndex = scanner.nextInt() - 1;
                        scanner.nextLine(); // Consume newline
                        if (pubIndex < 0) break;
                        if (pubIndex >= 0 && pubIndex < archive.getPublicationList().size()) {
                            try {
                                personalArchive.addToArchive(archive.getPublicationList().get(pubIndex));
                                saveArchive(ARCHIVE_PERSONAL_LIST_TXT, personalArchive.getPublicationList());
                                LOGGER.info("Publication saved to personal list.");
                                valid = true;
                            } catch (SameIsbnException e) {
                                LOGGER.error(e.getMessage());
                            }
                        } else {
                            System.out.println("Invalid publication number.");
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error loading publications: " + e.getMessage());
        }
    }

    public static void showPublications(Archive archive, Archive personalArchive) {
        try {
            System.out.println("List of general archive publications:");
            if (archive.getPublicationList().isEmpty()) {
                throw new PublicationNotFoundException("Archive is empty");
            }
            for (int i = 0; i < archive.getPublicationList().size(); i++) {
                System.out.println(i + 1 + ". " + archive.getPublicationList().get(i));
            }
            System.out.println("==================================================");
            System.out.println("=> Select an option: => 1. Save a publication to personal list => Any other number to go back to main menu");
            int selection = scanner.nextInt();
            scanner.nextLine();

            if (selection == 1) {
                boolean valid = false;
                while (!valid) {
                    System.out.println("Enter the number of the publication to save: (0 to go back to menu)");
                    int pubIndex = scanner.nextInt() - 1;
                    scanner.nextLine(); // Consume newline
                    if (pubIndex < 0) break;
                    if (pubIndex >= 0 && pubIndex < archive.getPublicationList().size()) {
                        try {
                            personalArchive.addToArchive(archive.getPublicationList().get(pubIndex));
                            saveArchive(ARCHIVE_PERSONAL_LIST_TXT,personalArchive.getPublicationList());
                            valid = true;
                            LOGGER.info("Publication saved to personal list.");
                        } catch (SameIsbnException e) {
                            LOGGER.error(e.getMessage());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("Invalid publication number.");
                    }
                }
            }

        } catch (PublicationNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void addPublication(Archive archive) {
        System.out.println("What do you want to add? 1. Book 2. Magazine (or 0 to cancel)");
        int subOption = scanner.nextInt();
        scanner.nextLine(); // Consuma il ritorno a capo lasciato da nextInt()

        if (subOption == 0) {
            System.out.println("Insertion cancelled. Returning to main menu...");
            return;
        }

        if (subOption == 1) {
            System.out.println("Enter the ISBN code (or type 'cancel' to abort):");
            String isbn = scanner.nextLine();
            final String finalIsbn = isbn;
            if (isbn.equalsIgnoreCase("cancel")) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            boolean found = archive.getPublicationList().stream().anyMatch(p -> p.getISBN().equals(finalIsbn));
            if (found) {
                System.out.println("The ISBN code is already in use.");
                System.out.println("Generating random ISBN...");
                isbn = Publication.generateRandomString(10);
            }

            System.out.println("Enter the title of the book (or type 'cancel' to abort):");
            String title = scanner.nextLine();
            if (title.equalsIgnoreCase("cancel")) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            System.out.println("Enter the author of the book (or type 'cancel' to abort):");
            String author = scanner.nextLine();
            if (author.equalsIgnoreCase("cancel")) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            System.out.println("Enter the genre of the book (or type 'cancel' to abort):");
            String genre = scanner.nextLine();
            if (genre.equalsIgnoreCase("cancel")) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            System.out.println("Enter the publication year (or type '0' to cancel):");
            int year = scanner.nextInt();
            scanner.nextLine(); // Consuma il ritorno a capo
            if (year == 0) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            System.out.println("Enter the publication pages (or type '0' to cancel):");
            int pages = scanner.nextInt();
            scanner.nextLine(); // Consuma il ritorno a capo
            if (pages == 0) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            Publication p = new Book(isbn, title, year, pages, author, genre);
            try {
                archive.addToArchive(p);
                LOGGER.info("Book added successfully!");
                System.out.println("==================================================");
            } catch (SameIsbnException e) {
                LOGGER.error(e.getMessage());
            }
        } else if (subOption == 2) {
            System.out.println("Enter the ISBN code (or type 'cancel' to abort):");
            String isbn = scanner.nextLine();
            final String finalIsbn = isbn;
            if (isbn.equalsIgnoreCase("cancel")) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            boolean found = archive.getPublicationList().stream().anyMatch(p -> p.getISBN().equals(finalIsbn));
            if (found) {
                System.out.println("The ISBN code is already in use.");
                System.out.println("Generating random ISBN...");
                isbn = Publication.generateRandomString(10);
            }

            System.out.println("Enter the title of the magazine (or type 'cancel' to abort):");
            String title = scanner.nextLine();
            if (title.equalsIgnoreCase("cancel")) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            System.out.println("Enter the publication year (or type '0' to cancel):");
            int year = scanner.nextInt();
            scanner.nextLine(); // Consuma il ritorno a capo
            if (year == 0) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            System.out.println("Enter the publication pages (or type '0' to cancel):");
            int pages = scanner.nextInt();
            scanner.nextLine(); // Consuma il ritorno a capo
            if (pages == 0) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            System.out.println("Select periodicity: 1.WEEKLY 2.MONTHLY 3.HALF_YEARLY (or type '0' to cancel):");
            int type = scanner.nextInt();
            scanner.nextLine(); // Consuma il ritorno a capo
            if (type == 0) {
                System.out.println("Insertion cancelled. Returning to main menu...");
                return;
            }

            Periodicity periodicity = switch (type) {
                case 1 -> Periodicity.WEEKLY;
                case 2 -> Periodicity.MONTHLY;
                case 3 -> Periodicity.HALF_YEARLY;
                default -> null;
            };

            if (periodicity == null) {
                System.out.println("Invalid periodicity. Returning to main menu...");
                return;
            }

            Publication p = new Magazine(isbn, title, year, pages, periodicity);
            try {
                archive.addToArchive(p);
                LOGGER.info("Magazine added successfully!");
                System.out.println("==================================================");
            } catch (SameIsbnException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void searchPublicationByISBN(Archive archive) {
        boolean valid = false;
        while (!valid) {
            System.out.println("Insert the ISBN code for the publication you want to search: (0 to go back to menu)");
            String isbn = scanner.nextLine();
            if (isbn.equals("0")) break;
            try {
                Publication p = archive.getPublicationByISBN(isbn);
                System.out.println("Publication found: => " + p);
                valid = true;
                System.out.println("==================================================");
            } catch (IsbnNotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void deletePublication(Archive archive) {
        boolean valid = false;
        while (!valid) {
            System.out.println("Insert the ISBN code for the publication you want to delete: (0 to go back to menu)");
            String isbn = scanner.nextLine();
            if (isbn.equals("0")) break;
            try {
                archive.deletePublication(isbn);
                LOGGER.info("Publication deleted successfully!");
                valid = true;
                System.out.println("==================================================");
            } catch (IsbnNotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void updatePublication(Archive archive) {
        boolean valid = false;
        while (!valid) {
            System.out.println("What is the ISBN code of the publication you want to update? (0 to go back to menu)");
            String isbn = scanner.nextLine();
            if (isbn.equals("0")) break;
            try {
                archive.updatePublicationInfo(isbn);
                valid = true;
                LOGGER.info("Publication updated successfully!");
                System.out.println("==================================================");
            } catch (PeriodicityException | IsbnNotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void showPublicationsGroupedByYear(Archive archive) {
        boolean valid = false;
        while (!valid) {
            System.out.println("List of publications grouped by year:");
            try {
                archive.getPublicationsGroupedByYear().forEach((year, list) -> {
                    System.out.println("Year: " + year);
                    list.forEach(System.out::println);
                });
                valid = true;
                System.out.println("==================================================");
            } catch (PublicationNotFoundException e) {
                LOGGER.error(e.getMessage());
            }

        }
    }

    public static void searchPublicationsByYear(Archive archive) {
        boolean valid = false;
        while (!valid) {
            System.out.println("Insert the year for the publication you want to search: (0 to go back to menu)");
            int year = scanner.nextInt();
            scanner.nextLine();
            if (year == 0) break;
            System.out.println("List of publications founded for year: " + year);
            try {
                archive.getByYear(year).forEach(System.out::println);
                valid = true;
                System.out.println("==================================================");
            } catch (PublicationNotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void searchPublicationsByAuthor(Archive archive) {
        boolean valid = false;
        while (!valid) {
            System.out.println("Digit author name to search: (0 to go back to menu)");
            String author = scanner.nextLine();
            if (author.equals("0")) break;
            try {
                archive.getBooksByAuthor(author).forEach(System.out::println);
                valid = true;
                System.out.println("==================================================");
            } catch (PublicationNotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void showBooksGroupedByGenre(Archive archive) throws PublicationNotFoundException {
        System.out.println("List of books grouped by genre:");
        try {
            archive.getBooksByGenre().forEach((genre, list) -> {
                System.out.println("-------------");
                System.out.println("Genre: " + genre);
                list.forEach(System.out::println);
            });
        } catch (PublicationNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        System.out.println("==================================================");
    }

    public static void showMagazinesList(Archive archive) {
        System.out.println("Magazines list:");
        archive.getPublicationList().stream().filter(p -> p instanceof Magazine).forEach(System.out::println);
        System.out.println("==================================================");
    }

    public static void showBooksList(Archive archive) {
        System.out.println("Books list:");
        archive.getPublicationList().stream().filter(p -> p instanceof Book).forEach(System.out::println);
        System.out.println("==================================================");
    }

    public static void showStatistics(Archive archive) {
        System.out.println("Statistics:");
        System.out.println("=> Total number of publications: " + archive.getPublicationList().size());
        System.out.println("=> Total number of books: " + archive.getNumberOfBooks());
        System.out.println("=> Total number of magazines: " + archive.getNumberOfMagazines());
        System.out.println("=> Max pages of a publications: " + archive.getMaxPages());
        System.out.println("=> Pages averages of all publications: " + archive.getAveragePages());
        System.out.println("==================================================");
    }

    public static void saveChanges(Archive archive, Archive personalArchive) throws IOException {
        try {
            saveArchive(ARCHIVE_ARCHIVE_TXT, archive.getPublicationList());
            saveArchive(ARCHIVE_PERSONAL_LIST_TXT, personalArchive.getPublicationList());
            LOGGER.info("Changes have been saved correctly!");
        } catch (IOException e) {
            LOGGER.error("Error saving changes: " + e.getMessage());
        }
    }

    public static void showPersonalList(Archive personalArchive) {
        try{
            if (personalArchive.getPublicationList().size() == 0) {
                throw new Exception("Personal list is empty!");
            } else {
                System.out.println("Personal list:");
                personalArchive.getPublicationList().forEach(System.out::println);
                System.out.println("==================================================");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        System.out.println("Choose an option: (0 to go back to menu)");
        System.out.println("=> 1. Delete a publication");
        System.out.println("=> 2. Show statistics");
        System.out.println("=> Any other number to go back to menu");
        int opt = scanner.nextInt();
        scanner.nextLine();
        if(opt == 1) {
            boolean valid = false;
            while (!valid) {
                System.out.println("Insert the ISBN code for the publication you want to delete: (0 to go back to menu)");
                String isbn = scanner.nextLine();
                if (isbn.equals("0")) break;
                try {
                    personalArchive.deletePublication(isbn);
                    LOGGER.info("Publication deleted successfully!");
                    valid = true;
                    System.out.println("==================================================");
                } catch (IsbnNotFoundException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        } else if(opt == 2){
            System.out.println("Personal statistics:");
            System.out.println("=> Total number of publications: " + personalArchive.getPublicationList().size());
            System.out.println("=> Total number of books: " + personalArchive.getNumberOfBooks());
            System.out.println("=> Total number of magazines: " + personalArchive.getNumberOfMagazines());
            System.out.println("=> Max pages of a publications: " + personalArchive.getMaxPages());
            System.out.println("=> Pages averages of all publications: " + personalArchive.getAveragePages());
            System.out.println("==================================================");
        }
    }


    public static void main(String[] args) throws IOException, PublicationNotFoundException {
        // CREO ARCHIVIO GLOBALE E PERSONALE
        Archive archive = new Archive();
        Archive personalArchive = new Archive();

        // SELEZIONO IL FILE DELL'ARCHIVIO SALVATO IN STORAGE
        File file = new File(ARCHIVE_ARCHIVE_TXT);
        String text = FileUtils.readFileToString(file, "UTF-8");

        // PRECARICAMENTO DI ALCUNI ELEMENTI IN ARCHIVIO COSì DA MOSTRARE UNA LISTA INIZIALE
        readSavedProducts(true).forEach(p -> archive.getPublicationList().add(p));
        readSavedProducts(false).forEach(p -> personalArchive.getPublicationList().add(p));

        boolean exit = false;

        while (!exit) {
            try {
                int option = showMainMenu();
                switch (option) {
                    case 1 -> reloadArchive(archive, personalArchive);
                    case 2 -> showPublications(archive, personalArchive);
                    case 3 -> addPublication(archive);
                    case 4 -> searchPublicationByISBN(archive);
                    case 5 -> deletePublication(archive);
                    case 6 -> updatePublication(archive);
                    case 7 -> showPublicationsGroupedByYear(archive);
                    case 8 -> searchPublicationsByYear(archive);
                    case 9 -> searchPublicationsByAuthor(archive);
                    case 10 -> showBooksGroupedByGenre(archive);
                    case 11 -> showMagazinesList(archive);
                    case 12 -> showBooksList(archive);
                    case 13 -> showStatistics(archive);
                    case 14 -> saveChanges(archive, personalArchive);
                    case 15 -> showPersonalList(personalArchive);
                    case 0 -> {
                        System.out.println("Exiting program...");
                        exit = true;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred. Please try again.");
                scanner.nextLine(); // Pulisce il buffer per eventuali problemi
            }
        }

        scanner.close();

    }

    public static void saveArchive(String filePath, List<Publication> archive) throws IOException {
        File file = new File(filePath);
        String publications = "";
        for (Publication p : archive) {
            if (p instanceof Book) {
                publications += "Book@" + ((Book) p).getISBN() + "@" + ((Book) p).getTitle() + "@" + ((Book) p).getYear() + "@" + ((Book) p).getPages() + "@" + ((Book) p).getAuthor() + "@" + ((Book) p).getGenre() + "#";
            } else if (p instanceof Magazine) {
                publications += "Magazine@" + ((Magazine) p).getISBN() + "@" + ((Magazine) p).getTitle() + "@" + ((Magazine) p).getYear() + "@" + ((Magazine) p).getPages() + "@" + ((Magazine) p).getPeriodicity().toString() + "#";
            }
        }
        FileUtils.writeStringToFile(file, publications, "UTF-8");
    }

    public static List<Publication> readSavedProducts(boolean isArchive) throws IOException {
        String path = isArchive ? ARCHIVE_ARCHIVE_TXT : ARCHIVE_PERSONAL_LIST_TXT;
        File file = new File(path);

        if (!file.exists() || file.length() == 0) {
            return List.of(); // Restituisce una lista vuota se il file non esiste o è vuoto
        }

        String allPublications = FileUtils.readFileToString(file, "UTF-8");

        return List.of(allPublications.split("#")).stream()
                .map(publication -> {
                    List<String> records = List.of(publication.split("@"));
                    try {
                        if (records.get(0).equals("Book")) {
                            Book book = new Book(records.get(2), Integer.parseInt(records.get(3)),
                                    Integer.parseInt(records.get(4)), records.get(5), records.get(6));
                            book.setISBN(records.get(1));
                            return book;
                        } else if (records.get(0).equals("Magazine")) {
                            Periodicity periodicity = Periodicity.valueOf(records.get(5));
                            Magazine magazine = new Magazine(records.get(2), Integer.parseInt(records.get(3)),
                                    Integer.parseInt(records.get(4)), periodicity);
                            magazine.setISBN(records.get(1));
                            return magazine;
                        }
                    } catch (Exception e) {
                        System.err.println("Errore nel parsing della pubblicazione: " + publication);
                    }
                    return null;
                })
                .filter(pub -> pub != null) // Filtra eventuali valori null
                .collect(Collectors.toList());
    }

    public static void clearList() {
        File file = new File(ARCHIVE_PERSONAL_LIST_TXT);
        try {
            FileUtils.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
