/* NAME Eesa Akhtar
   DATE 7th November 2020
   VERSION 3
   SUMMARY This program should be similar to the game 'pointless', it will ask each user
   questions and assign them points based on the options they pick.

 */


import java.io.*;
import java.util.*;

public class pointlessquiz {


    //The main method,
    public static void main(String[] args) {
        start();
        System.out.println("Done");
        System.exit(0);
    }


    //This method contains most of the method calls and initialises the program
    public static void start() {

        String file_to_read = introduction();

        QuestionBank[] Question_bank = initialise_file_into_questionbank(file_to_read);

        Question_bank = shuffle_questions(Question_bank);

        int number_of_rounds = ask_number_of_rounds(Question_bank.length);

        int number_of_players = ask_number_of_players();

        players[] players = initialise_players(number_of_players);
        pause(1000);
        print_welcome_message(players);

        ask_questions(Question_bank, players, number_of_rounds);

    }

    //Breaks the program off into admin mode if the string 'admin' is input
    public static String introduction() {
        String input = string_input("Welcome to the Pointless Quiz!\nPress enter to begin!");

        if (input.equalsIgnoreCase("admin")) {
            return admin_mode("Questions.txt");
        }
        return "Questions.txt";
    }


    //This method asks the user to input the amount of rounds they wish to play, it ensures the number of rounds is less than or equal to the amount of questions currently in the question bank.
    public static int ask_number_of_rounds(int number_of_questions) {
        System.out.println("How many rounds do you wish to play today?");
        boolean valid_number_of_rounds = false;
        int user_input = 0;

        while (!valid_number_of_rounds) {

            user_input = validate_positive_integer_input();

            if (user_input <= number_of_questions) {
                valid_number_of_rounds = true;
            } else {
                System.out.println("Error!\nPlease enter a number equal to or below " + number_of_questions + "!");
                continue;
            }

        }
        return user_input;
    }

    // This method prints a welcome message out, greeting the contestants, it uses an effective for loop with a nested if statement to make sure commas are concatenated correctly
    public static void print_welcome_message(players players[]) {
        System.out.println("\nWelcome to the Pointless Quiz!\n");

        int number_of_players = players.length;

        if (number_of_players > 1) {
            System.out.println("A warm welcome to our " + number_of_players + " contestants: ");
            for (int i = 0; i < number_of_players - 1; i++) {
                System.out.print(get_player_name(players[i]));
                if (i < number_of_players - 2) {
                    System.out.print(",");
                }
                System.out.print(" ");
            }

            System.out.print("and " + get_player_name(players[number_of_players - 1]));
            System.out.println("");
            return;
        }
        //If there is only one player
        else {
            System.out.println("A warm welcome to our contestant, " + get_player_name(players[0]));
        }
        pause(2000);
        return;
    }

    //ROUND METHODS
    //This method runs after everything has been initialised, it asks the users a question for each round
    public static void ask_questions(QuestionBank[] question_bank, players[] players, int number_of_rounds) {
        System.out.println("\nTime to start asking the questions!\n");
        String question;

        String[] all_players_names = get_all_players_names_as_array(players);

        String current_player_name;
        String player_input;

        boolean quiz_ended = false;

        int[] players_scores_for_round = new int[players.length];
        for (int i = 0; i < number_of_rounds; i++) {

            QuestionBank current_question = question_bank[i];
            question = get_question(current_question);
            if (i < number_of_rounds) {
                System.out.println("Round " + (i + 1));
            } else if (i == number_of_rounds) {
                System.out.println("Final ROund!");
            }

            System.out.println(question + "?");

            for (int z = 0; z < players.length; z++) {

                players current_player = players[z];

                current_player_name = get_player_name(current_player);

                player_input = string_input(current_player_name + ", what is your answer?");

                players_scores_for_round[z] = check_answer(player_input, current_question);

                current_player = add_points_to_player(current_player, players_scores_for_round[z]);

                players[z] = current_player;
            }

            pause(2000);
            System.out.println("That's the end of the round, let's see how everyone did!");
            check_round_scores(all_players_names, players_scores_for_round);
            pause(2000);
            System.out.println("Let's see the leaderboard right now!\n");
            sort_scores_and_print_leaderboard(players, quiz_ended);
            pause(2000);


        }
        quiz_ended = true;
        pause(2000);
        System.out.println("That's the end of the quiz, let's see how everyone did!");
        sort_scores_and_print_leaderboard(players, quiz_ended);


    }

    // This method checks to see if the answer inputted by the user matches any of the
    public static int check_answer(String input, QuestionBank q) {
        int score = 100;
        String[] answer = get_answer(q);
        int[] points = get_points(q);
        boolean scanned_answers = false;
        while (!scanned_answers)
            for (int i = 0; i < 6; i++) {
                if (input.equalsIgnoreCase(answer[i])) {
                    score = points[i];
                    scanned_answers = true;
                }
                scanned_answers = true;
            }
        return score;
    }

    //This method prints out all the scores that individual users scored for the round
    public static void check_round_scores(String[] player_name, int[] players_scores_for_round) {

        for (int i = 0; i < player_name.length; i++) {
            System.out.print(player_name[i] + " scored " + players_scores_for_round[i] + ".");
            if (players_scores_for_round[i] == 0) {
                System.out.print(" POINTLESS!");
            }
            if (players_scores_for_round[i] == 100) {
                System.out.print(" INCORRECT ANSWER ");
            }
            System.out.print("\n");
        }


    }

    //This method can be called whenever the program wants to take a pause for a while.
    public static void pause(int ms) {
        try {
            Thread.sleep(ms);
            return;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Error in wait method");
        }
        return;
    }

//END OF ROUND METHODS

//LEADERBOARD METHODS

    //This method uses a bubble sort to sort out all the players scores accordingly, then passes those newly made arrays onto another method to print out a leaderboard
    public static void sort_scores_and_print_leaderboard(players[] p, boolean quiz_ended) {
        String[] players_names = get_all_players_names_as_array(p);
        int[] players_scores = get_all_players_scores_as_array(p);
        String temp_name;
        int temp_score;
        boolean sorted = false;
        while (!sorted) {
            for (int i = 0; i < p.length - 1; i++) {
                for (int z = 0; z < (p.length - i - 1); z++) {
                    if (players_scores[z] > players_scores[z + 1]) {

                        temp_score = players_scores[z];
                        temp_name = players_names[z];

                        players_scores[z] = players_scores[z + 1];
                        players_names[z] = players_names[z + 1];

                        players_scores[z + 1] = temp_score;
                        players_names[z + 1] = temp_name;
                    }

                }
            }
            sorted = true;
        }
        display_leaderboard(players_names, players_scores, quiz_ended);
        return;

    }

    //This method returns the leaderboard of the current (sorted) scores in a legible format which all players can see,
    //If the boolean quiz_ended is true, it also prints out a congratulatory message for the winner
    public static void display_leaderboard(String[] players_names, int[] players_sorted_scores, boolean quiz_ended) {
        System.out.println("Leaderboard:");
        System.out.println("\n");
        for (int i = 0; i < players_names.length; i++) {
            System.out.println(i + 1 + ") " + players_names[i] + ": " + players_sorted_scores[i] + "\n");
        }
        System.out.println(" ");

        if (quiz_ended == true) {
            System.out.println(players_names[0] + " is the winner with " + players_sorted_scores[0] + " points!");
        }

        return;


    }
//END OF LEADERBOARD METHODS

// PLAYER METHODS

    // Players ADT, stores their names and score
    static class players {
        String name;
        int score;
    }

    //this method initialises the players, prompting the user to enter their names
    public static players[] initialise_players(int number_of_players) {
        players[] player = new players[number_of_players];
        String player_name;

        for (int i = 0; i < number_of_players; i++) {

            player_name = string_input("Please enter the name of Player " + (i + 1) + ":");
            player[i] = create_player(player_name);
        }
        System.out.println(" ");
        return player;
    }

    //This method asks for the number of players, ensures that it is within the range of 1-4 (and if not prints out an error), once this condition is met, it returns the integer to the method call
    public static int ask_number_of_players() {
        int users = 0;
        System.out.println("How many contestants are there today?");
        boolean valid_number_of_players = false;

        while (!valid_number_of_players) {
            users = validate_positive_integer_input();

            if (users > 0 && users < 5) {
                valid_number_of_players = true;
            } else {
                System.out.println("Please enter a value between 1 and 4");
                continue;
            }

        }

        return users;
    }

    // SETTER METHOD: initial method, sets the player name to the String arguement passed into it
    public static players create_player(String name) {
        players p = new players();
        p.name = name;
        p.score = 0;
        return p;
    }

    //SETTER METHOD can be called upon to add points to a players current score.
    public static players add_points_to_player(players p, int points_to_add) {
        p.score = p.score + points_to_add;
        return p;
    }

    //ACCESSOR METHOD: obtains a single players name by calling this method, passing the player ADT as a parameter
    public static String get_player_name(players p) {
        String name = p.name;
        return name;
    }

    //ACCESSOR METHOD: Gets all the players names as an array, returning this array as a String type array
    public static String[] get_all_players_names_as_array(players[] p) {
        String[] players_names = new String[p.length];

        for (int i = 0; i < p.length; i++) {
            players_names[i] = p[i].name;
        }

        return players_names;
    }

    //ACCESSOR METHOD: Gets all the players scores as an array, returning this array as an int type array
    public static int[] get_all_players_scores_as_array(players[] p) {
        int[] players_scores = new int[p.length];

        for (int i = 0; i < p.length; i++) {
            players_scores[i] = p[i].score;
        }

        return players_scores;


    }
// END OF PLAYER METHODS


// QUESTION BANK METHODS

    //QuestionBank ADT, stores questions, answers and points in an array.
    static class QuestionBank {
        String question;
        String[] answer = new String[6];
        int[] points = new int[6];
    }

    //This method is here to shuffle the Questions currently stored in the question bank
    public static QuestionBank[] shuffle_questions(QuestionBank[] q) {
        Random rand = new Random();

        for (int i = 0; i < q.length; i++) {
            int random_index_for_swap = rand.nextInt(q.length);
            QuestionBank temp_value = q[random_index_for_swap];
            q[random_index_for_swap] = q[i];
            q[i] = temp_value;
        }

        return q;
    }

    //SETTER METHOD: This method creates an individual record of the type Questionbank and returns it to the method call, 6 answers and 6 points assigned per question
    public static QuestionBank create_question(String Question, String[] answer, int[] points) {

        QuestionBank q = new QuestionBank();
        q.question = Question;

        for (int i = 0; i < 6; i++) {
            q.answer[i] = answer[i];
            q.points[i] = points[i];
        }

        return q;
    }

    //ACCESSOR METHOD: takes a questionbank instance as an argument and returns the Question property as a String to the method call
    public static String get_question(QuestionBank q) {
        String question = q.question;
        return question;
    }

    //ACCESSOR METHOD: returns the answers contained in a question bank as a string array
    public static String[] get_answer(QuestionBank q) {
        String[] answers = q.answer;
        return answers;
    }

    //ACCESSORE METHOD: returns the points contained in a question bank as an int array
    public static int[] get_points(QuestionBank q) {
        int[] points = q.points;
        return points;
    }


// END OF QUESTION BANK METHODS

    // FILE READING METHODS
    //This method reads the questions.txt file and creates a QuestionBank array containing the respective questions, answers and points, and returns it to the method call.
    public static QuestionBank[] initialise_file_into_questionbank(String question_file) {
        File file = new File(question_file);

        if (file.exists()) {

            String question = "";
            String[] answers = new String[6];
            int[] points = new int[6];

            int number_of_lines = read_number_of_lines_in_file(file);

            //Allows the Questionbank array to be initialised with the index of how many questions there are (1 question per line)
            QuestionBank[] q = new QuestionBank[number_of_lines];

            try {

                Scanner input = new Scanner(file);
                //This splits the Questions, answers and points they're assigned into tokens, then assigns them accordingly in the questionbank q.
                //The for loop ensures it does it for every line. The nested for loop makes sure it goes through each question answer and points.

                for (int i = 0; i < number_of_lines; i++) {
                    String[] tokens = input.nextLine().split(",");

                    for (int z = 0; z < 6; z++) {
                        question = tokens[0];
                        answers[z] = tokens[z + (z + 1)];
                        points[z] = Integer.parseInt(tokens[z + (z + 2)]);
                    }
                    q[i] = create_question(question, answers, points);
                }

                input.close();
                return q;
            } catch (IOException e) {
                System.out.println("Error initialising questions");
            }
        } else {
            System.out.println("File does not exist!");
            start();
            return null;
        }

        return null;
    }

    //This input reads the number of lines (questions) there are in the file and returns it to the method call.
    public static int read_number_of_lines_in_file(File file) {
        int lines = 0;

        try {
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                lines = lines + 1;
                input.nextLine();
            }

            input.close();
        } catch (IOException e) {
            System.out.println("Error reading number of lines");
        }
        return lines;
    }


//END /FILE READING METHODS


//ADMIN METHODS + FILE OUTPUT

    //Admin mode method, allows user to choose whether he wants to create a new questionbank file, or choose from pre-existing questionbank files
    public static String admin_mode(String question_file) {
        String input = string_input("ADMIN MODE!\nPLEASE ONLY ENTER A NUMBER FOR A SELECTION\nCurrent Questionbank_file: " + question_file + "\nDo you want to:\n (1) Create a new QuestionBank file,\n (2) Select a question file, \n (3) Exit and return to the quiz!");
        if (input.equalsIgnoreCase("1")) {
            write_questionbank_file();
            admin_mode(question_file);
        } else if (input.equalsIgnoreCase("2")) {
            question_file = select_file();
            System.out.println("File selected: " + question_file);
            admin_mode(question_file);
        } else if (input.equalsIgnoreCase("3")) {
            return question_file;
        } else {
            System.out.println("Error, please enter 1, 2 or 3");
            admin_mode(question_file);
        }
        return question_file;
    }


    //This method allows the user to select a text file within the directory, returning the value of the file name as a string back to the method call
    public static String select_file() {
        System.out.println("Please enter what file you want to select (number input please!)");
        String file_name = "";
        File[] files = find_files();
        int i;
        for (i = 0; i < files.length; i++) {
            file_name = files[i].getName();
            System.out.println((i + 1) + ": " + file_name);
        }
        boolean valid_input = false;
        int userinput;
        while (!valid_input) {
            userinput = validate_positive_integer_input();
            if (userinput > 0 && userinput <= i) {
                file_name = files[userinput - 1].getName();
                valid_input = true;
            } else {
                System.out.println("Please enter a number between 1 and " + (i));
                continue;
            }
        }
        return file_name;
    }

    //This method finds all the text files within the root directory, returning all of them as an array
    public static File[] find_files() {
        File root = new File(System.getProperty("user.dir"));
        File[] files = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        return files;
    }

    public static void write_questionbank_file() {
        String file_name = string_input("What would you like the question file to be called?");
        String question;
        String[] answer = new String[6];
        int[] points = new int[6];
        int no_of_questions = 0;
        boolean valid_input = false;

        System.out.println("How many questions do you want to write?");
        while (!valid_input) {
            no_of_questions = validate_positive_integer_input();
            if (no_of_questions < 1) {
                System.out.println("Please enter a value over 0");
                continue;
            } else {
                valid_input = true;
            }
        }

        try (FileWriter filestream = new FileWriter(file_name + ".txt")) {
            BufferedWriter qbankfile = new BufferedWriter(filestream);
            for (int i = 1; i <= no_of_questions; i++) {
                question = string_input("Type Question " + i);
                for (int y = 0; y < 6; y++) {
                    valid_input = false;
                    answer[y] = string_input("Please enter answer " + (y + 1) + ", hit enter and then type the points for the answer");
                    while (!valid_input) {
                        points[y] = validate_positive_integer_input();
                        if (points[y] > 100) {
                            System.out.println("Please enter a value below 100!");
                            continue;
                        } else {
                            valid_input = true;
                        }
                    }
                }
                qbankfile.write(String.format(question));
                for (int z = 0; z < 6; z++) {
                    qbankfile.write(String.format("," + answer[z] + "," + points[z]));
                }
                qbankfile.write(String.format("\n"));
            }
            qbankfile.close();
        } catch (IOException e) {
            System.out.println("Error writing file");
        }

        return;
    }

//END OF ADMIN METHODS

    // INPUT METHODS
    //This method validates that the user as inputted a positive integer value, and if so, returns the value, if not, it keeps prompting the user.
    public static int validate_positive_integer_input() {
        int user_input = 0;
        boolean input_is_positive = false;
        Scanner input = new Scanner(System.in);

        while (!input_is_positive) {
            try {
                user_input = input.nextInt();

                if (user_input < 0) {
                    System.out.println("Please enter a positive value!");
                    continue;
                } else {
                    input_is_positive = true;
                }

            } catch (InputMismatchException exception) {
                System.out.println("Please enter only an integer value!");
                input.nextLine();

            }
        }

        return user_input;
    }

    //This method waits for a user input and returns the string value to the method call, if the message arguement is "NA" it does not print a message, otherwise, it prints the message passed as a parameter into the method.
    public static String string_input(String message) {

        if (!message.equals("NA")) {
            System.out.println(message);
        }

        Scanner input = new Scanner(System.in);
        String user_input = input.nextLine();
        return user_input;
    }
// END OF INPUT METHODS
}
