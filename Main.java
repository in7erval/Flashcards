package flashcards;
import java.awt.desktop.SystemEventListener;
import java.awt.image.AreaAveragingScaleFilter;
import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.util.spi.CalendarDataProvider;

public class Main {

    public static class CardClass {
        public String term;
        public String definition;
        public Integer errors;

        public CardClass() {
            this.term = "";
            this.definition = "";
            this.errors = 0;
        }

        public CardClass(String term, String definition, Integer errors) {
            this.term = term;
            this.definition = definition;
            this.errors = errors;
        }

    }

    public static boolean hasTerm(ArrayList<CardClass> cards, String term) {
        for (var x : cards) {
            if (x.term.equals(term)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasDefinition(ArrayList<CardClass> cards, String definition) {
        for (var x : cards) {
            if (x.definition.equals(definition)) {
                return true;
            }
        }
        return false;
    }

    public static void removeTerm(ArrayList<CardClass> cards, String term) {
        cards.removeIf(x -> x.term.equals(term));
    }

    public static CardClass findTerm(ArrayList<CardClass> cards, String term) {
        for (var x : cards) {
            if (x.term.equals(term)) {
                return x;
            }
        }
        return null;
    }

    public static void add(Scanner scan, ArrayList<CardClass> cards, ArrayList<String> log) {
        String term;
        String definition;

        System.out.println("The card:");
        log.add("The card:");
        term = scan.nextLine();
        log.add(term);
        if (hasTerm(cards, term)) {
            System.out.println("The card \"" + term + "\" already exists.");
            log.add("The card \"" + term + "\" already exists.");
        } else {
            System.out.println("The definition of the card:");
            log.add("The definition of the card:");
            definition = scan.nextLine();
            log.add(definition);
            if (hasDefinition(cards, definition)) {
                System.out.println("The definition \"" + definition + "\" already exists.");
                log.add("The definition \"" + definition + "\" already exists.");
            } else {
                cards.add(new CardClass(term, definition, 0));
                System.out.println("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
                log.add("The pair (\"" + term+ "\":\"" + definition + "\") has been added.");
            }
        }
    }

    public static void remove(Scanner scan, ArrayList<CardClass> cards, ArrayList<String> log) {
        String term;

        System.out.println("The card:");
        log.add("The card:");
        term = scan.nextLine();
        log.add(term);
        if (hasTerm(cards, term)) {
            removeTerm(cards, term);
            System.out.println("The card has been removed.");
            log.add("The card has been removed.");
        } else {
            System.out.println("Can't remove \"" + term + "\": there is no such card.");
            log.add("Can't remove \"" + term + "\": there is no such card.");
        }
    }

    public static void importCards(Scanner scan, ArrayList<CardClass> cards, ArrayList<String> log) {
        String  line;
        String filename;
        String term;
        String definition;
        Integer errors;
        int     i = 0;

        System.out.println("File name:");
        log.add("File name:");
        filename = scan.nextLine();
        log.add(filename);
        File file = new File(filename);
        try (Scanner scanfile = new Scanner(file)) {
            while (scanfile.hasNext()) {
                term = scanfile.nextLine();
                definition = scanfile.nextLine();
                errors = Integer.parseInt(scanfile.nextLine());
                removeTerm(cards, term);
                cards.add(new CardClass(term, definition, errors));
                i++;
            }
            System.out.printf("%d cards have been loaded.\n", i);
            log.add(String.format("%d cards have been loaded.\n", i));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }

    public static void exportCards(Scanner scan, ArrayList<CardClass> cards, ArrayList<String> log) {
        String filename;
        int i = 0;

        System.out.println("File name:");
        log.add("File name:");
        filename = scan.nextLine();
        log.add(filename);
        File file = new File(filename);
        try (PrintWriter pw = new PrintWriter(filename)) {
            for (var x : cards) {
                i++;
                pw.printf("%s\n%s\n%d\n", x.term, x.definition, x.errors);
            }
            System.out.printf("%d cards have been saved.\n", i);
            log.add(String.format("%d cards have been saved.\n", i));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            log.add("Error: " + e.getMessage());
        }
    }

    public static void ask(Scanner scan, ArrayList<CardClass> cards, ArrayList<String> log) {
        Integer times;
        String str;
        System.out.println("How many times to ask?");
        log.add("How many times to ask?");
        times = Integer.parseInt(scan.nextLine());
        log.add(times.toString());
        while (times > 0) {
            for (var x : cards) {
                if (times <= 0) {
                    break;
                }
                System.out.printf("Print the definition of \"%s\":\n", x.term);
                log.add(String.format("Print the definition of \"%s\":\n", x.term));
                str = scan.nextLine();
                log.add(str);
                if (str.equals(x.definition)) {
                    System.out.println("Correct answer");
                    log.add("Correct answer");
                } else {
                    if (hasDefinition(cards, str)) {
                        for (var y : cards) {
                            if (y.definition.equals(str)) {
                                System.out.printf("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".\n", x.definition, y.term);
                                log.add(String.format("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".\n", x.definition, y.term));
                            }
                        }
                    } else {
                        System.out.printf("Wrong answer. The correct one is \"%s\".\n", x.definition);
                        log.add(String.format("Wrong answer. The correct one is \"%s\".\n", x.definition));
                    }
                    x.errors++;
                }
                times--;
            }
            if (times <= 0) {
                break;
            }
        }
    }

    public static void saveLog(Scanner scan, ArrayList<String> log) {
        String filename;

        System.out.println("File name:");
        log.add("File name:");
        filename = scan.nextLine();
        log.add(filename);
        try (FileWriter fw = new FileWriter(filename)) {
            for (String s : log) {
                fw.write(s + "\n");
            }
            System.out.println("The log has been saved.");
            log.add("The log has been saved.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            log.add("Error: " + e.getMessage());
        }
    }

    public static void hardestCard(ArrayList<CardClass> cards, ArrayList<String> log) {
        int max = 0;
        ArrayList <String> terms = new ArrayList<String>();
        StringBuilder str;

        for (var x : cards) {
            if (x.errors > max) {
                terms.clear();
                max = x.errors;
                terms.add(x.term);
            } else if (x.errors == max && x.errors != 0) {
                terms.add(x.term);
            }
        }
        if (terms.size() == 0) {
            System.out.println("There are no cards with errors.");
            log.add("There are no cards with errors.");
        }
        else if (terms.size() == 1) {
            System.out.printf("The hardest card is \"%s\". You have %d errors answering it.\n", terms.get(0), max);
            log.add(String.format("The hardest card is \"%s\". You have %d errors answering it.\n", terms.get(0), max));
        } else {
            str = new StringBuilder(String.format("The hardest cards are \"%s\"", terms.get(0)));
            for (int i = 1; i < terms.size(); i++) {
                str.append(String.format(", \"%s\"", terms.get(i)));
            }
            str.append(String.format(". You have %d errors answering them.\n", max));
            System.out.println(str);
            log.add(str.toString());
        }

    }

    public static void resetStats(ArrayList<CardClass> cards, ArrayList<String> log) {
        for (var x : cards) {
            x.errors = 0;
        }
        System.out.println("Stats are reseted");
        log.add("Stats are reseted");
    }

    public static void importCardsArg(String filename, ArrayList<CardClass> cards, ArrayList<String> log) {
        String  line;
        String term;
        String definition;
        Integer errors;
        int     i = 0;

        File file = new File(filename);
        try (Scanner scanfile = new Scanner(file)) {
            while (scanfile.hasNext()) {
                term = scanfile.nextLine();
                definition = scanfile.nextLine();
                errors = Integer.parseInt(scanfile.nextLine());
                cards.add(new CardClass(term, definition, errors));
                i++;
            }
            System.out.printf("%d cards have been loaded.\n", i);
            log.add(String.format("%d cards have been loaded.\n", i));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }

    public static void exportCardsArg(String filename, ArrayList<CardClass> cards, ArrayList<String> log) {
        int i = 0;

        File file = new File(filename);
        try (PrintWriter pw = new PrintWriter(filename)) {
            for (var x : cards) {
                i++;
                pw.printf("%s\n%s\n%d\n", x.term, x.definition, x.errors);
            }
            System.out.printf("%d cards have been saved.\n", i);
            log.add(String.format("%d cards have been saved.\n", i));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            log.add("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<CardClass> cardStats = new ArrayList<CardClass>();
        ArrayList<CardClass> cards = new ArrayList<CardClass>();
        ArrayList<String> log = new ArrayList<String>();
        String action;
        boolean export = false;
        String filenameExport = "";

        if (args.length > 0 && (args[0].equals("-import") || args[0].equals("-export"))) {
            if (args[0].equals("-import")) {
                importCardsArg(args[1], cards, log);
            }
            if (args[0].equals("-export")) {
                export = true;
                filenameExport = args[1];
            }
            if (args.length > 2) {
                if (args[2].equals("-import")) {
                    importCardsArg(args[3], cards, log);
                }
                if (args[2].equals("-export")) {
                    export = true;
                    filenameExport = args[3];
                }
            }
        }
        do {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            log.add("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            action = scan.nextLine();
            log.add(action);
            if (action.equals("add")) {
                add(scan, cards, log);
            } else if (action.equals("remove")) {
                remove(scan, cards, log);
            } else if (action.equals("import")) {
                importCards(scan, cards, log);
            } else if (action.equals("export")) {
                exportCards(scan, cards, log);
            } else if (action.equals("ask")) {
                ask(scan, cards, log);
            } else if (action.equals("log")) {
                saveLog(scan, log);
            } else if (action.equals("hardest card")) {
                hardestCard(cards, log);
            } else if (action.equals("reset stats")) {
                resetStats(cards, log);
            } else if (!action.equals("exit")) {
                System.out.println("Wrong action. Try again!");
                log.add("Wrong action. Try again!");
            }
            System.out.println("");
            log.add("");
        } while (!action.equals("exit"));
        System.out.println("Bye bye!");
        log.add("Bye bye!");
        if (export) {
            exportCardsArg(filenameExport, cards, log);
        }
    }
}
