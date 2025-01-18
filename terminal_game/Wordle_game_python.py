import random

# ANSI color codes for terminal output
RED_BOLD = "\033[1;31m"  # RED
GREEN_BOLD = "\033[1;32m"  # GREEN
YELLOW_BOLD = "\033[1;33m"  # YELLOW
RESET = "\033[0m"  # Text Reset

keyboard = """A B C D E F G H I 
J K L M N O P Q R 
S T U V W X Y Z """

guesses_tried = 0  # member variable for the number of guesses tried in the game
user_word_history = []  # history of user guesses

# Load dictionary words into memory
with open("DictionaryWords.txt", "r") as f:
    DICTIONARY_WORDS = set(word.strip().lower() for word in f if word.strip())


def random_word():
    with open("RandomWords.txt", "r") as f:
        words = [word.strip() for word in f if word_ok(word.strip(), False)]
    chosen_word = random.choice(words)
    words.remove(chosen_word)

    with open("WordsNew.txt", "w") as f:
        f.write("\n".join(words))
    return chosen_word


def word_ok(word, print_message):
    if len(word) != 5:
        if print_message:
            if len(word) > 5:
                print("Error: More than 5 letters entered!")
            if len(word) < 5:
                print("Error: Less than 5 letters entered!")
        return False

    if not word.isalpha():
        if print_message:
            print("Error: Non-letter character entered")
        return False

    if len(set(word)) != 5:
        if print_message:
            print("Error: Duplicate letter entered")
        return False

    # Check if the word exists in the dictionary
    if word.lower() not in DICTIONARY_WORDS:
        if print_message:
            print("Error: Word not recognized.")
        return False

    return True


def user_guess_word():
    global guesses_tried
    if guesses_tried != 0:
        print(f"You have attempted {guesses_tried} of 6 guesses.\n")

    while True:
        user_guess = input("Input a guess (5 letters long and no duplicate letter): ").lower()
        if word_ok(user_guess, True):
            return user_guess


def word_to_chars(word):
    return list(word)


def wordle_run(wordle_word):
    global guesses_tried, keyboard

    wordle_word_chars = word_to_chars(wordle_word)
    history_green_chars = []
    history_yellow_chars = []
    history_red_chars = []

    while guesses_tried < 6:
        guess = user_guess_word()
        colored_guess = []
        guess_chars = word_to_chars(guess)
        green_chars = []
        yellow_chars = []
        red_chars = []

        if guess == wordle_word:
            print("\nYOU HAVE SUCCESSFULLY GUESSED THE WORD... GOOD WORK!!\n")
            break
        else:
            for i in range(5):
                if guess_chars[i] == wordle_word_chars[i]:
                    green_chars.append(guess_chars[i])
                    colored_guess.append(f"{GREEN_BOLD}{guess_chars[i]}{RESET}")
                    history_green_chars.append(guess_chars[i].upper())
                    keyboard = keyboard.replace(guess_chars[i].upper(), f"{GREEN_BOLD}{guess_chars[i].upper()}{RESET}")
                elif guess_chars[i] in wordle_word_chars:
                    yellow_chars.append(guess_chars[i])
                    colored_guess.append(f"{YELLOW_BOLD}{guess_chars[i]}{RESET}")
                    if guess_chars[i].upper() not in history_green_chars:
                        history_yellow_chars.append(guess_chars[i].upper())
                        keyboard = keyboard.replace(guess_chars[i].upper(), f"{YELLOW_BOLD}{guess_chars[i].upper()}{RESET}")
                else:
                    red_chars.append(guess_chars[i])
                    colored_guess.append(f"{RED_BOLD}{guess_chars[i]}{RESET}")
                    if guess_chars[i].upper() not in history_green_chars and guess_chars[i].upper() not in history_yellow_chars:
                        history_red_chars.append(guess_chars[i].upper())
                        keyboard = keyboard.replace(guess_chars[i].upper(), f"{RED_BOLD}{guess_chars[i].upper()}{RESET}")

            user_word_history.append("".join(colored_guess))
            guesses_tried += 1

        print("\nGuessed words: ")
        for history_word in user_word_history:
            print(history_word)
        print("\n" + keyboard + "\n")

        if guesses_tried == 6:
            print(f"You did not guess correctly in 6 tries!\nThe word was: {wordle_word}\n")
            break


if __name__ == "__main__":
    wordle_run(random_word())