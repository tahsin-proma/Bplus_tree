package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class App {

    private final BPlusTree dictionaryTree;

    public App(int order) {
        this.dictionaryTree = new BPlusTree(order);
    }

    public void loadWordsFromFile(String filePath) {
        System.out.println("Loading dictionary from file: " + filePath);
        long startTime = System.currentTimeMillis();
        int wordCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(",", 3);
                if (parts.length >= 1) {
                    String word = parts[0].trim().toLowerCase();
                    WordDefinition definition = new WordDefinition(word);
                    dictionaryTree.insert(word, definition);
                    wordCount++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the dictionary file: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Loading complete. " + wordCount + " words loaded in " + (endTime - startTime) + " ms.");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("\nWelcome to the Word Validator!");
        System.out.println("Type a word and press Enter to check if it exists.");
        System.out.println("Type 'exit' to quit the application.");

        while (true) {
            System.out.print("\nEnter a word to check > ");
            input = scanner.nextLine().trim().toLowerCase();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            WordDefinition definition = dictionaryTree.search(input);

            if (definition != null) {
                System.out.println("Success! The word '" + input + "' exists in the dictionary.");
            } else {
                System.out.println("The word '" + input + "' was not found.");
            }
        }

        scanner.close();
        System.out.println("Thank you for using the Word Validator. Goodbye!");
    }

    public static void main(String[] args) {
        int order = 1000;
        App app = new App(order);
        app.loadWordsFromFile("words.txt");
        app.start();
    }
}