package flashcards;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    private static Scanner scanner;
   static Map<String,String> map;
   static Map<String, Integer> hardestCards;
    static StringBuilder stringLog = new StringBuilder();
    static boolean exportCLI = false;
    static String exportPathCLI;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        map = new LinkedHashMap<>();
        hardestCards = new LinkedHashMap<>();
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                if(args[i].equals("-import")){
                    new Main().importFrom(args[i+1]);
                }
                if(args[i].equals("-export")){
                    exportCLI = true;
                    exportPathCLI = args[i+1];
                }
            }
        }
        new Main().menu();
    }

    boolean inputKeyIsValid(String input){
        return !map.containsKey(input);
    }
    boolean inputValueIsValid(String input){
        return !map.containsValue(input);
    }


    void checkDefinition(int count){
        outer: for (int i = count; i > 0; i--) {
            Random random = new Random();
            List<Map.Entry<String,String>> entries = new ArrayList<>(map.entrySet());
            Collections.shuffle(entries);

            Map.Entry<String,String> entry = entries.get(random.nextInt(entries.size()));

            System.out.println("Print the definition of \""+entry.getKey()+"\":");
            stringLog.append("Print the definition of \"").append(entry.getKey()).append("\":");

            String definition = scanner.nextLine();
            stringLog.append(definition);

            if(definition.equals(entry.getValue())){
                System.out.println("Correct answer.\n");
                stringLog.append("Correct answer.\n");
            }
            else{
                int mistakes = 0;
                for (Map.Entry<String,String> entry1: map.entrySet()){
                    if(definition.equals(entry1.getValue())){
                        System.out.println("Wrong answer. The correct one is \""+entry.getValue()+"\", you've just written the definition of \""+entry1.getKey()+"\".");
                        stringLog.append("Wrong answer. The correct one is \"").append(entry.getValue()).append("\", you've just written the definition of \"").append(entry1.getKey()).append("\".\n");

                        /**
                         * Hardest card
                         */
                        for(Map.Entry<String,Integer> hardestEntry: hardestCards.entrySet()){
                            if(hardestEntry.getKey().equals(entry.getKey())){
                                mistakes = hardestEntry.getValue();
                            }
                        }
                        hardestCards.replace(entry.getKey(), ++mistakes);

                        continue outer;
                    }
                }
                System.out.println("Wrong answer. The correct one is \"" + entry.getValue() + "\".\n");
                stringLog.append("Wrong answer. The correct one is \"").append(entry.getValue()).append("\".\n\n");
                /**
                 * Hardest card
                 */
                for(Map.Entry<String,Integer> hardestEntry: hardestCards.entrySet()){
                    if(hardestEntry.getKey().equals(entry.getKey())){
                        mistakes = hardestEntry.getValue();
                    }
                }
                hardestCards.replace(entry.getKey(), ++mistakes);
            }
        }
    }

    void menu(){
        boolean end = false;
        while (!end){
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            stringLog.append("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n");

            String option = scanner.nextLine().toLowerCase();
            stringLog.append(option).append("\n");

            switch (option){
                case "add": input();
                    break;
                case "remove": remove();
                    break;
                case "import": importFrom();
                    break;
                case "export": exportTo();
                    break;
                case "ask": ask();
                    break;
                case "exit": exit();
                    end = true;
                    break;
                case "log": log();
                    break;
                case "hardest card": hardestCard();
                    break;
                case "reset stats": resetStats();
                    break;
            }
        }
    }
    void exit(){
        System.out.println("Bye bye!");
        if(exportCLI){
            exportTo(exportPathCLI);
        }
    }
    void input(){
        System.out.println("The card:");
        stringLog.append("The card:\n");
        boolean keX = false;
        while (!keX) {
            String key = scanner.nextLine();
            stringLog.append(key).append("\n");
            if (inputKeyIsValid(key+"\n")) {
                keX = true;
                System.out.println("The definition of the card:");
                stringLog.append("The definition of the card:\n");
                boolean valX = false;
                while (!valX) {
                    String value = scanner.nextLine();
                    stringLog.append(value).append("\n");
                    if (inputValueIsValid(value)) {
                        valX = true;
                        map.put(key, value);
                        System.out.println("The pair (\""+key+":"+value+"\") has been added.\n");
                        stringLog.append("The pair (\"").append(key).append(":").append(value).append("\") has been added.\n\n");

                        hardestCards.put(key, 0);

                    } else {
                        System.out.println("The definition \"" + value + "\" already exists.\n");
                        stringLog.append("The definition \"").append(value).append("\" already exists.\n\n");
                        break;
                    }
                }
            } else {
                System.out.println("The card \"" + key + "\" already exists.\n");
                stringLog.append("The card \"").append(key).append("\" already exists.\n\n");
                break;
            }
        }
    }
    void remove(){
        System.out.println("The card:");
        stringLog.append("The card:\n");
        String key = scanner.nextLine();
        stringLog.append(key).append("\n");

        if(map.containsKey(key)){
            map.remove(key);
            hardestCards.remove(key);
            System.out.println("The card has been removed.\n");
            stringLog.append("The card has been removed.\n\n");
        }else{
            System.out.println("Can't remove \""+key+"\"");
            stringLog.append("Can't remove \"").append(key).append("\"\n");

        }
    }
    void importFrom(String ... var){
        String fileName;

        if (var.length == 0){
            System.out.println("File name:");
            stringLog.append("File name:\n");
            fileName = /*"./Flashcards/task/src/flashcards/"+*/scanner.nextLine();
            stringLog.append(fileName).append("\n");
        }
        else{
            fileName = var[0];
        }

        try {
            File file = new File(fileName);
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String line;
            int loadCards = 0;
            while (true){
                line = fileReader.readLine();
                if(line == null){break;}
                else {
                    String[] entries = line.split("\n");
                    for (String entry : entries) {
                        String[] pairs = entry.split(":");
                        map.put(pairs[0], pairs[1]);
                        hardestCards.put(pairs[0], Integer.parseInt(pairs[2]));
                        loadCards++;
                    }
                }
            }
            System.out.println(loadCards + " cards have been loaded.\n");
            stringLog.append(loadCards).append(" cards have been loaded.\n\n");
        }catch (FileNotFoundException e){
            System.out.println("File not found.\n");
            stringLog.append("File not found.\n\n");
        } catch (IOException e) {
            stringLog.append(e.getMessage());
            e.printStackTrace();
        }


    }
    void exportTo(String ... var){
        String fileName;
        if(var.length == 0) {
            System.out.println("File name:");
            stringLog.append("File name:\n");
            fileName = /*"./Flashcards/task/src/flashcards/"+*/scanner.nextLine();
        }
        else{
            fileName = var[0];
        }
        try{
            stringLog.append(fileName).append("\n");
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName));
            int saveCards = 0;
                for(Map.Entry<String,String> entry : map.entrySet())
                {
                    String line = entry.getKey()+":"+entry.getValue()+":"+ hardestCards.get(entry.getKey())+"\n";
                    fileWriter.write(line);
                    saveCards++;
                }
                System.out.println(saveCards +" cards have been saved.\n");
                stringLog.append(saveCards).append(" cards have been saved.\n\n");
                fileWriter.close();
        }catch (FileNotFoundException e){
            System.out.println("File not found.\n");
            stringLog.append("File not found.\n\n");
        } catch (IOException e) {
            e.printStackTrace();
            stringLog.append(e.getMessage());
        }
    }
    void ask(){
        if(!map.isEmpty()){
            System.out.println("How many times to ask?");
            stringLog.append("How many times to ask?\n");
            int count = Integer.parseInt(scanner.nextLine());
            stringLog.append(count).append("\n");
            checkDefinition(count);
        }else{
            System.out.println("Add some cards\n");
            stringLog.append("Add some cards\n\n");
        }
    }
    void log() {

        System.out.println("File name:");
        stringLog.append("File name:\n");

        String fileName = scanner.nextLine();
        stringLog.append(fileName).append("\n");

        try{
            FileWriter logfile = new FileWriter(fileName);

            PrintWriter log = new PrintWriter(logfile);
            log.write(stringLog.toString());
            log.close();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("The log has been saved.\n");
        stringLog.append("The log has been saved.\n\n");







    }
    void hardestCard(){
            int numOfMistakes = 0;
            StringBuilder stringBuilder = new StringBuilder();
            int numOfCards = 0;
            Map<String, Integer> res = hardestCards.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            for(Map.Entry<String, Integer> entry: res.entrySet()){
                if(entry.getValue() > numOfMistakes){
                    numOfMistakes = entry.getValue();
                    numOfCards++;
                    stringBuilder.append("\"").append(entry.getKey()).append("\", ");
                    continue;
                }
                if (entry.getValue() == numOfMistakes && numOfMistakes != 0){
                    numOfCards++;
                    stringBuilder.append("\"").append(entry.getKey()).append("\"  ");
                }

            }
            String str = stringBuilder.toString();

            switch (numOfCards){
                case 1:
                    System.out.println("The hardest card is " + str.substring(0, str.length()-2) + ". You have " + numOfMistakes + " errors answering it.\n");
                    stringLog.append("The hardest card is ").append(str.substring(0, str.length() - 2)).append(". You have ").append(numOfMistakes).append(" errors answering it.\n\n");
                    break;
                case 0:
                    System.out.println("There are no cards with errors.\n");
                   stringLog.append("There are no cards with errors.\n\n");
                    break;
                default:
                    System.out.println("The hardest cards are " + str.substring(0, str.length()-1) + ". You have " + numOfMistakes + " errors answering it.\n");
                    stringLog.append("The hardest cards are ").append(str.substring(0, str.length() - 1)).append(". You have ").append(numOfMistakes).append(" errors answering it.\n\n");
                    break;

            }
//            System.out.println("The hardest card is \"" + t + "\". You have " + n + " errors answering it.\n");
//            stringLog.append("The hardest card is \"").append(t).append("\". You have ").append(n).append(" errors answering it.\n");
//        }

    }
    void resetStats(){
        for(Map.Entry<String,Integer> entry : hardestCards.entrySet()){
            entry.setValue(0);
        }

        System.out.println("Card statistics has been reset.\n");
        stringLog.append("Card statistics has been reset.\n");
    }
}