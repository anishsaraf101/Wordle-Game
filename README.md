# Wordle Game


Name: Anish Saraf
No known bugs or defects in the program
`
Everything is stored in one class... there is a main method at the bottom and the wordleRun method is the runner
for the actual wordle game. wordleRun is the only thing called from the main method. There are many methods besides
the wordleRun and main method used in the code, but they are fairly straightforward helper methods. There are comments
for functions of what each method does in code also. The wordleRun method uses all methods except public static void
main as helper methods. The code works just fine as tested and should face no issues.

To run the code, download the RandomWords.txt & WordleGuessingGame.java files. The first file stores a list of around
5,700 words for the chosen worldle word to randomly pick from. The latter contains all the class code to run the
program. To input guesses in the program, enter the guesses in the terminal when prompted for. At the end of the
program (either wordle word guessed correctly or unable to guess), the program will write a new file called
WordsNew.txt with all words except the one just used.

Explanations:
 - Green color represents a correct character placed in the right spot
 - Yellow color represents a correct character placed in the wrong spot
 - Red color represents an incorrect character that does not appear in the wordle word
 - WordOk checks to see if 5 letter word passed through is all alphabet letters that does not have duplicate letters
   The true/false argument is used to see if the method is being used to get a word from file or user input guess
 - All other code has comments explaining it and/or is fairly self-explanatory!
