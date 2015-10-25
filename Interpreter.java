import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;

/** The interpreter for Adventurous.
 *  @author Vi Le
 */
public class Interpreter {
    Interpreter(Scanner reader) {
        _reader = reader;
    }

    void play() {
        System.out.println("Hello, what is your name?");
        _name = _reader.nextLine();
        System.out.println("Alright, " + _name + ", let's go on an adventure.\nRemember to forage for items so you can sell them later.");
        weapon();
        while (!_beeEventPass && !_catEventPass) {
            System.out.println("You come up to the a fork in the road. Left or right?");
            fork();
        }
        dragon();
        if (_deserveReward) {
            store();
        }
        _reader.close();
    }

    void weapon() {
        System.out.println("You might want to choose a weapon before going on this journey. Name your weapon: ");
        _weapon = _reader.nextLine();
    }

    void fork() {
        String choice = _reader.nextLine();
        choice = choice.toLowerCase();
        if (choice.equals("left")) {
            left();
        } else if (choice.equals("right")) {
            right();
        } else {
            System.out.println("No going off the path! Left or right?");
            _reader.nextLine();
            fork();
        }
    }

    void transverse() {
        Random generator = new Random();
        if (_encounterItems.size() != 0) {
            int index = generator.nextInt(_encounterItems.size()-1);
            String newItem = _encounterItems.get(index);
            System.out.println("Oh, look, you found " + newItem + ".");
            System.out.println("It is added to your inventory.");
            _inventory.add(newItem);
        }
    }
    void left() {
        transverse();
        if (_catEventPass) {
            _beeEventPass = bee();
        } else {
            _catEventPass = cat();
            _catEvent = true;
        }
    }

    void right() {
        transverse();
        if (_beeEventPass) {
            _catEventPass = cat();
        } else {
            _beeEventPass = bee();
            _beeEvent = true;
        }
    }

    boolean cat() {
        if (_catEventPass) {
            return true;
        }
        if (_annoyanceCount == 3) {
            System.out.println("The cat is annoyed. You cannot pass this way.");
            _annoyanceCount = 0;
            return false;
        }
        if (_catEvent) {
            System.out.println("There is a cat blocking the path. What will you use?");
            inventoryPrint();
            String choice = _reader.nextLine();
            if (choice.equals("catnip")) {
                System.out.println("The cat was satisfied and moved.");
                _catEventPass = true;
                return true;
            } else if (choice.equals("a stone") || choice.equals("a rock") || choice.equals("a torch") || choice.equals(_weapon)) {
                System.out.println("The cat grows annoyed.");
                _annoyanceCount++;
                cat();
            } else if (_inventory.contains(choice)) {
                System.out.println("The cat ignored you.");
                return false;
            } else {
                System.out.println("That is not something you have.");
                cat();
            }
        }
        return false;
    }

    boolean bee() {
        if (_beeEventPass) {
            return true;
        } else if (_annoyanceCount == 3) {
            System.out.println("The bee is annoyed. It stings you and dies.");
            _annoyanceCount = 0;
            _beeEventPass = true;
            return true;
        } else if (_beeEvent) {
            System.out.println("There is a bee blocking the path. What will you use?");
            inventoryPrint();
            String choice = _reader.nextLine();
            if (choice.equals("a flower")) {
                System.out.println("The bee was satisfied and moved.");
                _beeEventPass = true;
                return true;
            } else if (choice.equals("honey")) {
                System.out.println("Bees already make this.");
                bee();
            } else if (choice.equals("a magic staff") || choice.equals("a torch") || choice.equals("a branch") || choice.equals(_weapon)) {
                System.out.println("The bee grows annoyed.");
                _annoyanceCount++;
                return bee();
            } else if (_inventory.contains(choice)) {
                System.out.println("The bee ignored you.");
                return false;
            } else {
                System.out.println("That is not something you have.");
                return bee();
            }
        }
        return false;
    }

    void inventoryPrint() {
        System.out.print("Inventory: ");
        for (String item : _inventory) {
            System.out.print(item + ", ");
        }
        System.out.println(_weapon);
    }

    void dragon() {
        System.out.println("You continue a long winding road.\n...\nROAR!\nThere is a castle up ahead. Head towards it?");
        String choice = _reader.nextLine();
        if (choice.equals("Yes") || choice.equals("yes")) {
            System.out.println("Alright! Off to see the dragon!");
        } else {
            System.out.println("A strong gust of wind knocks you over and you roll downhill towards the castle.");
        }
        System.out.println("You come face to face with the dragon. It looms over you.");
        while (!_dragonDefeated) {
            _dragonDefeated = dragonBattle();
        }
        if (_deserveReward) {
            System.out.println("You have calmed the dragon! \nThe castle residents come out to thank you and reward you with an apple pie.");
            _inventory.add("apple pie");
            _encounterItems.add("apple pie");
            _prices.add(12.00);
        }
    }

    boolean dragonBattle() {
        if (_inventory.size() == 0) {
            System.out.println("You've run out of things and decided to go back.");
            _deserveReward = false;
            return true;
        }
        System.out.println("What will you use?");
        inventoryPrint();
        String choice = _reader.nextLine();
        if (choice.equals("a taco") || choice.equals("honey") || choice.equals("bread")) {
            System.out.println("The dragon ate " + choice + " and calmed down.");
            return true;
        } else if (choice.equals("a flower") || choice.equals("holy water") || choice.equals("dirty water")) {
            System.out.println("The dragon is allergic to " + choice +".\nIt sneezed fire and burned it.");
            _inventory.remove(choice);
        } else if (_inventory.contains(choice)) {
            System.out.println("The dragon is enraged. It burned " + choice + " away.");
            _inventory.remove(choice);
        } else if (choice.equals(_weapon)) {
            System.out.println(_weapon + " did not do any damage.");
        } else {
            System.out.println("That is not something you have.");
        }
        return false;
    }

    void store() {
        HashMap<String,Double> store = mapItems(_encounterItems, _prices);
        System.out.println("You go back to sell the things you've gotten today.\nHere is a list of what sells at the store:");
        printHash(_encounterItems, _prices);
        inventoryPrint();
        Double total = 0.00;
        for (String item : _inventory) {
            total += store.get(item);
        }
        System.out.println("How much do you think you made?");
        selling(total);
    }

    void selling(Double total) {
        String amount = "$" + String.format("%.2f",total);
        String guess = _reader.nextLine();
        if (guess.equals(amount) || guess.equals(total.toString())) {
            System.out.println("Congratulations! You've made " + amount + " today!");
        } else {
            System.out.println("Hm... You may want to double check your work. Guess again: ");
        }
    }

    HashMap<String,Double> mapItems(ArrayList<String> encounterItems, ArrayList<Double> price) {
        HashMap<String,Double> store = new HashMap<String,Double>();
        for (int i = 0; i < encounterItems.size(); i++) {
            store.put(encounterItems.get(i), price.get(i));
        }
        return store;
    }

    void printHash(ArrayList<String> encounterItems, ArrayList<Double> price) {
        for (int i = 0; i < encounterItems.size(); i++) {
            if (i == 0) {
                System.out.print(encounterItems.get(i) + ": $" + String.format("%.2f", price.get(i)));
            } else {
                System.out.print(", " + encounterItems.get(i) + ": $" + String.format("%.2f", price.get(i)));
            }
        }
        System.out.println("");
    }

    private Scanner _reader;
    private ArrayList<String> _inventory = new ArrayList<String>();
    private String _name;
    private String _weapon;
    private ArrayList<String> _encounterItems =
            new ArrayList<String>(Arrays.asList("honey", "catnip", "a stone", "holy water", "dirty water", "a torch",
            "a magic staff", "a rock", "a taco", "a branch", "a clump of dirt", "bread", "a flower"));
    private ArrayList<Double> _prices = new ArrayList<Double>(Arrays.asList(3.00, 2.50, 0.01, 10.00, 0.01, 1.00, 10.00, 0.01, 3.00, 0.01, 0.01, 4.00, 2.00));
    private boolean _catEvent, _beeEvent, _catEventPass, _beeEventPass, _dragonDefeated, _deserveReward = true;
    private int _annoyanceCount = 0;
}
