import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class WordleGuessingGame {
    static int guessesTried; // member variable for number of guesses tried in game
    static ArrayList<String> userWordHistory = new ArrayList<String>(); // member variable for the history of user guesses
    static String[] newMyWords; // member variable for all the words read from file with the chosen word deleted
    static Scanner scnr = new Scanner(System.in); // new scanner object used for user input guess
    static HashSet<String> dictionary = new HashSet<>(); // new hash set object used to store all words in the dictionary


    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String RESET = "\033[0m";  // Text Reset

    public static String keyboard = "A B C D E F G H I \n" + // keyboard used and updated in game based on red/yellow/green characters
                                    "J K L M N O P Q R \n"  +
                                    "S T U V W X Y Z ";



    public static String randomWord() throws Exception { // gives a random word from the file read in considering constraints
        Random rand = new Random();  // new Random object
        String[] myWords = new String[5800]; // new myWords array of strings to be big enough for all words from file
        String word; // used to store each word from file
        String wordleWord; // randomized chosen word from file for wordle guessing game
        File f = new File("RandomWords.txt"); // opens a file named "RandomWords.txt", creating identifier f
        Scanner scnr = new Scanner(f); // creates a reference from file (f) to reader (scnr)


        int totalWords = 0;
        int i = 0;
        while (scnr.hasNextLine()) { // is there another word in file f
            word = scnr.nextLine(); // if so, save word in myWords array and increment totalWords
            if (wordOk(word, false)) { // only adds word read from file if helper method wordOk returns True
                myWords[i] = word;
                totalWords += 1;
                i += 1;
            }
        }

        scnr.close(); // close the file

        int location = rand.nextInt(totalWords); // choose a randomized index location of myWords to pick from
        wordleWord = myWords[location]; // use random index to pick the chosen word for the game

        newMyWords = new String[totalWords - 1]; //for loop makes newMyWords with the deleted wordleWord
        int newMyWordsIndex = 0;
        for (i = 0; i < totalWords; i++) {
            if (!(myWords[i].equals(wordleWord))) {
                newMyWords[newMyWordsIndex] = myWords[i];
                newMyWordsIndex++;
            }
        }
        return wordleWord;
    }

    public static void loadDictionary() throws IOException { // loads the dictionary file into a hash set
        File f = new File("DictionaryWords.txt");
        Scanner scnr = new Scanner(f); 
        while (scnr.hasNextLine()) { 
            String nextWord = scnr.nextLine().toLowerCase(); // makes all words in dictionary lowercase
            dictionary.add(nextWord); 
        }
        scnr.close(); // close the file
    }

    public static boolean wordOk(String word, boolean print) {
        word = word.toLowerCase(); // automatically makes all words lowercase
        // wordOk method used to check if a given word from a file or user guess is 5 characters long, consists of only letters, and has no duplicate letters
        // boolean print true if method used for user guess input; boolean print false if method used to read from file
        if (word.length() != 5) { //checks if word length is exactly 5
            if (print) {
                if (word.length() > 5) {
                    System.out.println("Error: More than 5 letters entered!");
                }
                if (word.length() < 5) {
                    System.out.println("Error: Less than 5 letters entered!");
                }
            }
            return false;
        }
        Character[] charArray = new Character[5];
        for (int i = 0; i < 5; i++) { // splits the word string into an array of Characters
            charArray[i] = word.charAt(i);
        }

        for (int i = 0; i < charArray.length; ++i) { // checks if any of the characters appear twice
            if (!Character.isLetter(charArray[i])) { // checks if each character is a letter in alphabet
                System.out.println("Error: Non letter character entered");
                return false;
            }
            for (int j = i + 1; j < charArray.length; ++j) { // if duplicate characters, return false; otherwise return true
                if ((j <= 4) && (charArray[i].compareTo(charArray[j]) == 0)) {
                    if (print) {
                        System.out.println("Error: duplicate letter entered");
                    }
                    return false;
                }
            }
        }

        if (!dictionary.contains(word)) { // checks if the word is in the dictionary
            if (print) {
                System.out.println("Error: Word not recognized in dictionary");
            }
            return false;
        }
        return true;

    }

    private static String userGuessWord() { // gets input for the user's guess word (used in wordleRun method)
        String userGuessWord;
        if (guessesTried != 0){ // outputs the number of user guesses attempted if the user has inputted at least one guess already
            System.out.println("You have attempted " + guessesTried + " of 6 guesses.");
            System.out.println();
        }
        do {    // run do while loop until a properly inputted guess word is recieved
            System.out.print("Input a guess (5 letters long and no duplicate letter): ");
            userGuessWord = scnr.next(); // save correct guess word to userGuessWord
        } while (!wordOk(userGuessWord, true));
        return userGuessWord;
    }

    public static ArrayList<Character> wordToChars(String word) { // takes in a word String and makes an array list with each element as a character
        ArrayList<Character> charsList = new ArrayList<Character>(); // new object type of array list w/ identifier charGuessList
        for (int i = 0; i < word.length(); i++) {     // Adds each character of the guessed word as element to array list
            charsList.add(word.charAt(i));
        }
        return charsList;
    }

    public static void wordleRun(String wordleWord) { // runs the wordle word game
        ArrayList<Character> wordleWordChars = wordToChars(wordleWord); // makes a character array list for each letter in the correct wordle word
        ArrayList<Character> historyGreenChars = new ArrayList<Character>(); // makes array list for all green chars, yellow chars, red chars given;
        ArrayList<Character> historyYellowChars = new ArrayList<Character>(); // used for keyboard to remember all guess input colors
        ArrayList<Character> historyRedChars = new ArrayList<Character>();

        for (guessesTried = 0; guessesTried < 6; guessesTried++) { // asks user for guesses to run the game while they have tried less than 6 attempts
            String guess = userGuessWord(); // gets correctly formatted guess using helper method
            StringBuilder coloredGuess = new StringBuilder(); // new string which has the correct color attachment for each character of guess
            ArrayList<Character> guessChars = wordToChars(guess);
            ArrayList<Character> greenChars = new ArrayList<Character>(); // new temp green, yellow, red chars resseted for each guess
            ArrayList<Character> yellowChars = new ArrayList<Character>();
            ArrayList<Character> redChars = new ArrayList<Character>();

            if (guess.equals(wordleWord)) { // first case; if the user successfully guesses the worldle word
                System.out.println("\nYOU HAVE SUCCESSFULLY GUESSED THE WORD... GOOD WORK!!\n");
                writeToFile();
                break;
            } else { // runs if user did not guess word correctly
                for (int i = 0; i < 5; i++) { // index for each guess character
                    for (int j = 0; j < 5; j++) { // index for each wordle word character
                        if (guessChars.get(i).equals(wordleWordChars.get(j))) { // if the 2 elements are the same character
                            if (i == j) { // if the 2 characters are in the same spot
                                greenChars.add(guessChars.get(i)); // add to guess's green characters
                                coloredGuess.append(GREEN_BOLD).append(guessChars.get(i)).append(RESET); //append green letter character to coloredGuess
                                historyGreenChars.add(guessChars.get(i)); // add to history of guess's green characters
                                String greenCharKeyboardReplace = String.valueOf(guessChars.get(i)).toUpperCase();                    // replace the character in the keyboard
                                keyboard = keyboard.replace(greenCharKeyboardReplace, GREEN_BOLD + greenCharKeyboardReplace + RESET); // with the same character but colored green
                            } else { //  if the 2 characters are not in the same spot; same as green functions but add to and replace to with yellow color
                                yellowChars.add(guessChars.get(i));
                                coloredGuess.append(YELLOW_BOLD).append(guessChars.get(i)).append(RESET);
                                if (!historyGreenChars.contains(guessChars.get(i))) { // add yellow character if not already green
                                    historyYellowChars.add(guessChars.get(i));
                                    String yellowCharKeyboardReplace = String.valueOf(guessChars.get(i)).toUpperCase();
                                    keyboard = keyboard.replace(yellowCharKeyboardReplace, YELLOW_BOLD + yellowCharKeyboardReplace + RESET);
                                }
                            }
                            break;
                        }

                    }
                    if (!greenChars.contains(guessChars.get(i)) && !yellowChars.contains(guessChars.get(i))) { // if the guess's character is already not green or yellow (entirely wrong letter)
                        coloredGuess.append(RED_BOLD).append(guessChars.get(i)).append(RESET); // same functions for green except add to and replace with red
                    }
                    if ((!historyGreenChars.contains(guessChars.get(i))) && (!historyYellowChars.contains(guessChars.get(i)))) { // replace red char in keyboard if not already green or yellow
                        historyRedChars.add(guessChars.get(i));
                        String redCharKeyboardReplace = String.valueOf(guessChars.get(i)).toUpperCase();
                        keyboard = keyboard.replace(redCharKeyboardReplace, RED_BOLD + redCharKeyboardReplace + RESET);
                    }
                    if (i == 4) { // on last character of guess word add entire updated colored guess word to userWordHistory
                        userWordHistory.add(coloredGuess.toString());
                    }
                }
            }
            System.out.println();
            System.out.println("Guessed words: ");
            for (int i = 0; i < userWordHistory.size(); ++i) { // every time after a guess, print history of
                System.out.println(userWordHistory.get(i));    // guesses and keyboard showing all characters tried
            }
            
            System.out.println();
            System.out.println(keyboard);
            System.out.println();
            if (guessesTried == 5) { // if still not guessed correctly on 6th (last) try, print correct word out right before ending for loop
                System.out.println("You did not guess correctly in 6 tries!\nThe word was: " + wordleWord + "\n\n");
                writeToFile();
            }
        }
        scnr.close(); // close scanner
    }

    static void writeToFile() { // writes a seperate file after program is done (user either guessed correctly or used up tries)
        BufferedWriter writer; // that includes all words in previous file except the word just used
        try { // newMyWords declared at the end of randomWord method
            writer = new BufferedWriter(new FileWriter("WordsNew.txt"));
            for (int i = 0; i < newMyWords.length; i++) {
                writer.write(newMyWords[i] + "\n");
            }
            writer.close();
        } catch (IOException e) { // catches error
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws Exception { // main method
        loadDictionary(); // loads dictionary file into hash set before starting wordle game
        wordleRun(randomWord()); // runs the whole method for user to play wordle game;
    }                            // many helper methods are called within
}