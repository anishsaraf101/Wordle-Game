// Variables to track the game state
let currentRow = 0; // Current row being edited
let currentTile = 0; // Current tile in the row
let wordleWord = ""; // The word the player aims to guess
let dictionaryWords = []; // Array to hold all dictionary words

// Variables to track guessed letters
const greenChars = new Set(); // Correct letter, correct position
const yellowChars = new Set(); // Correct letter, wrong position
const grayChars = new Set(); // Incorrect letter

// Select all rows and keys
const rows = document.querySelectorAll(".row");
const keys = document.querySelectorAll(".key");

// Save the boxes for wins and losing and load the correct word
const winBox = document.getElementById("win-box");
const lossBox = document.getElementById("loss-box");
const correctWordSpan = document.getElementById("correct-word");

// Function to generate a UUID
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

// Function to get or create the user's ID
function getOrCreateUserId() {
  let userId = localStorage.getItem('user_id');
  if (!userId) {
    userId = generateUUID();
    localStorage.setItem('user_id', userId); // Save the user_id in localStorage
    console.log(`New user_id generated: ${userId}`);
  } else {
    console.log(`Existing user_id found: ${userId}`);
  }
  return userId;
}

// Call the function to ensure a user ID is set
const userId = getOrCreateUserId();

// Function to fetch the dictionary words from the text file
async function loadDictionaryWords() {
  try {
    const response = await fetch("/static/files/DictionaryWords.txt");
    if (!response.ok) throw new Error("Failed to fetch dictionary words");

    const text = await response.text();
    dictionaryWords = text.split("\n").map(word => word.trim());
    console.log("Dictionary loaded:", dictionaryWords.length, "words");
  } catch (error) {
    console.error("Error loading dictionary words:", error);
  }
}

// Function to fetch a random word from the text file
async function fetchWordleWord() {
  try {
    const response = await fetch("/static/files/RandomWords.txt");
    if (!response.ok) throw new Error("Failed to fetch word list");

    const text = await response.text();
    const words = text.split("\n").map(word => word.trim()).filter(word => word.length === 5);
    wordleWord = words[Math.floor(Math.random() * words.length)];

    // Display the word on the screen for debugging purposes
    const debugElement = document.createElement("p");
    debugElement.textContent = `DEBUG: Wordle Word - ${wordleWord}`;
    debugElement.style.color = "red";
    debugElement.style.fontSize = "14px";
    document.body.appendChild(debugElement);
  } catch (error) {
    console.error("Error fetching the Wordle word:", error);
  }
}

// Function to validate user guesses
function isValidGuess(guess) {
  // Check if the word is exactly 5 characters
  if (guess.length !== 5) {
    alert("Guess must be exactly 5 letters long!");
    return false;
  }

  // Check if the word has duplicate letters
  const uniqueLetters = new Set(guess);
  if (uniqueLetters.size !== guess.length) {
    alert("Guess must not contain duplicate letters!");
    return false;
  }

  // Check if the word exists in the dictionary
  if (!dictionaryWords.includes(guess)) {
    alert("Word not found in dictionary!");
    return false;
  }

  return true;
}

// Call the functions to load the dictionary and Wordle word on page load
loadDictionaryWords();
fetchWordleWord();


// Function to handle letter input
function handleLetterInput(letter) {
  if (currentTile < 5) { // Ensure no more than 5 letters per row
    const tile = rows[currentRow].children[currentTile];
    tile.textContent = letter; // Set the letter in the box
    currentTile++; // Move to the next tile
  }
}

// Function to handle backspace input
function handleBackspaceInput() {
  if (currentTile > 0) { // Ensure there are tiles to backspace
    currentTile--; // Move back to the previous tile
    const tile = rows[currentRow].children[currentTile];
    tile.textContent = ""; // Clear the letter in the box
  }
}

// Function to handle guess submition
function submitGuess() {
  const guess = Array.from(rows[currentRow].children).map(tile => tile.textContent.toLowerCase()).join("");
  let isCorrect = false;
  // Validate the guess
  if (isValidGuess(guess)) {
    // Call evaluateGuess to color the tiles for the current guess and check if accurate
    isCorrect = evaluateGuess(guess);

    if (isCorrect) {
      displayWinMessage(); // Helper function to congrulate the winner
      return; // End the game
    }

    // Proceed to the next row after coloring the tiles
    currentRow++;
    currentTile = 0;

    if (currentRow >= rows.length) { // game lossed due to exceeding # of guesses
      displayLossMessage();
    }
  }
  else {
    Array.from(rows[currentRow].children).forEach(tile => { tile.textContent = ""; });
    currentTile = 0;
  }
}

// Function to evaluate the guess
function evaluateGuess(guess) {
  const wordleChars = wordleWord.split(""); // Convert Wordle word to an array of characters
  const guessChars = guess.split(""); // Convert guess to an array of characters
  let allGreen = false;

  // Iterate over the tiles in the current row
  for (let i = 0; i < 5; i++) {
    const letter = guessChars[i];
    const tile = rows[currentRow].children[i]; // DOM element representing the HTML tile object being observed 
    
    if (guessChars[i] === wordleChars[i]) {
      // Correct letter in the correct position (green)
      tile.style.backgroundColor = "#6aaa64"; // Green
      wordleChars[i] = null; // Mark the letter in the Wordle word as used for subsequent yellow letters
      greenChars.add(letter); // Add to greenChars
      yellowChars.delete(letter); // Remove from yellowChars if present
      grayChars.delete(letter); // Remove from grayChars if present
    } else if (wordleChars.includes(guessChars[i])) {
      // Correct letter in the wrong position (yellow)
      tile.style.backgroundColor = "#c9b458"; // Yellow
      wordleChars[wordleChars.indexOf(guessChars[i])] = null; // Mark the first instance of the letter as used
      if (!greenChars.has(letter)) {
        yellowChars.add(letter); // Add to yellowChars
        grayChars.delete(letter); // Remove from grayChars if present
      }
    } else {
      // Incorrect letter (gray)
      tile.style.backgroundColor = "#787c7e"; // Gray
      if (!greenChars.has(letter) && !yellowChars.has(letter)) {
        grayChars.add(letter); // Add to grayChars
      }
    }
  }
  if (wordleWord === guess) { // if user successfully guessed the word
    allGreen = true;
  }
  else {
    allGreen = false;
  }

  updateKeyboardColors(); // Update the keyboard colors after evaluating the guess

  return allGreen; // return if user successfully guessed the word
}

// Function to update the colors of the on-screen keyboard keys
function updateKeyboardColors() {
  keys.forEach(key => {
    const letter = key.textContent.trim().toLowerCase();

    if (greenChars.has(letter)) {
      key.style.backgroundColor = "#6aaa64"; // Green
    } else if (yellowChars.has(letter)) {
      key.style.backgroundColor = "#c9b458"; // Yellow
    } else if (grayChars.has(letter)) {
      key.style.backgroundColor = "#787c7e"; // Gray
    } else {
      key.style.backgroundColor = ""; // Reset if not in any list
    }
  });
}

// Function to display the celebratory win message
function displayWinMessage() {
  winBox.classList.remove("hidden"); // Show the win box
  setTimeout(() => location.reload(), 3000); // Reload after 3 seconds
}

// Function to display the loss message (future-proofing)
function displayLossMessage() {
  correctWordSpan.textContent = wordleWord; // Set the correct word in the loss message
  lossBox.classList.remove("hidden"); // Show the loss box
  setTimeout(() => location.reload(), 4000); // Reload after 3 seconds
}




// ON SCREEN KEYBOARD FUNCTIONALITY
// Add event listeners to all lettered keys

// Add backspace functionality to the on-screen Backspace key b/c backspace button does not contain letters
const backspaceKey = document.querySelector(".key.action-key:first-child");
backspaceKey.addEventListener("click", handleBackspaceInput);

// Add functionality for all other english letter containing keys
keys.forEach(key => {
  key.addEventListener("click", () => {
    const letter = key.textContent.trim();
    if (letter === "ENTER") {
      submitGuess();
    }
    else if (letter.length === 1 && /^[A-Z]$/i.test(letter)) { // Ensure it's a single letter
      handleLetterInput(letter);
    }
  });
});




// PHYSICAL KEYBOARD FUNCTIONALITY

// Function to handle physical keyboard input

// Extend physical keyboard functionality for Backspace
function handlePhysicalKeyboardInputExtended(event) {
  if (event.key === "Enter") {
    submitGuess();
  }
  else if (event.key === "Backspace") {
    handleBackspaceInput();
  }
  else {
    const key = event.key.toUpperCase();
    if (key.length === 1 && /^[A-Z]$/.test(key)) {
      handleLetterInput(key);
    }
  }
}


// Add event listener for physical keyboard input
document.addEventListener("keydown", handlePhysicalKeyboardInputExtended); // passes an event object to the helper function based on which physical key was pressed 