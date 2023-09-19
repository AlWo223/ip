package duke;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class Duke {

    private static final String HORIZONTAL_LINE = "--------------------------------------------";
    private static ArrayList<Task> tasks = new ArrayList<Task>();
    public static void main(String[] args) {
        loadData();
        introduceBot();
        handleCommands();
        farewellBot();
    }

    public static void handleCommands() {
        Scanner in = new Scanner(System.in);
        String input;

        do {
            input = in.nextLine().trim();
            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();
            String argument = null;
            if (parts.length > 1) {
                argument = parts[1];
            }
            try {
                switch (command) {
                case "list":
                    printList();
                    break;
                case "mark":
                    editTask(argument, true);
                    break;
                case "unmark":
                    editTask(argument, false);
                    break;
                case "todo":
                    addToDo(argument);
                    break;
                case "deadline":
                    addDeadline(argument);
                    break;
                case "event":
                    addEvent(argument);
                    break;
                case "delete":
                    deleteTask(argument);
                    break;
                case "bye":
                    break;
                default:
                    throw new InvalidCommandException();
                }
                saveData();
            } catch (InvalidCommandException e) {
                System.out.println("Oops, seems like I don't know this command. Please provide a valid command!");
            }
            System.out.println(HORIZONTAL_LINE);
        } while (!input.equalsIgnoreCase("bye"));
    }

    public static void introduceBot(){
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        String name = "Lexi";

        System.out.println(logo);
        System.out.println(HORIZONTAL_LINE);
        System.out.println("Hello! I'm " + name);
        if (tasks.size() > 0) {
            printList();
        } else {
            System.out.println("Currently, you have no tasks in your list.");
        }
        System.out.println("How can I help you buddy?");
        System.out.println(HORIZONTAL_LINE);
    }

    public static void farewellBot(){
        System.out.println("Have a wonderful day! Hope to see you again soon!");
        System.out.println(HORIZONTAL_LINE);
    }

    public static void printList(){
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i+1) + "." + tasks.get(i));
        }
    }

    public static void editTask(String argument, boolean done){
        try {
            int index = Integer.parseInt(argument);
            tasks.get(index - 1).setDone(done);
            if (done){
                System.out.println("Great! I have marked this task as done:");
            } else{
                System.out.println("Alright, I have marked this task as not done:");
            }
            System.out.println(tasks.get(index - 1));
        } catch (IndexOutOfBoundsException | NumberFormatException e){
            System.out.println("This task id does not exist, please provide a valid task number!");
        }
    }

    public static void printTaskAddedMessage(Task task){
        System.out.println("Ok, I have added the following task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }
    public static void addToDo(String argument){
        if(argument==null || argument.isEmpty()){
            System.out.println("I am sorry, the todo cannot be empty!");
        }
        Task todo = new Todo(argument);
        tasks.add(todo);
        printTaskAddedMessage(todo);
    }

    public static void addDeadline(String argument){
        try {
            String dueDate = argument.split(" /by ")[1];
            String description = argument.split(" /by ")[0];
            Task deadline = new Deadline(description, dueDate);
            tasks.add(deadline);
            printTaskAddedMessage(deadline);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Oops, I don't understand that! Please provide a valid deadline in the format: " +
                    "deadline <description> /by <due date>");
        } catch (NullPointerException e){
            System.out.println("I am sorry, the deadline cannot be empty! Please provide a valid deadline in the " +
                    "format: deadline <description> /by <due date>");
        }
    }

    public static void deleteTask(String argument) {
        try {
            int index = Integer.parseInt(argument);
            Task task = tasks.get(index - 1);
            tasks.remove(index - 1);
            System.out.println("Alright, I have removed the following task:");
            System.out.println("   " + task);
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        } catch (IndexOutOfBoundsException | NumberFormatException e){
            System.out.println("This task id does not exist, please provide a valid task number!");
        }
    }

    public static void addEvent(String argument){
        try {
            String description = argument.split(" /from ")[0];
            String startDate = argument.split(" /from ")[1].split(" /to ")[0];
            String endDate = argument.split(" /from ")[1].split(" /to ")[1];
            Task event = new Event(description, startDate, endDate);
            tasks.add(event);
            printTaskAddedMessage(event);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Oops, I don't understand that! Please provide a valid event in the format: event " +
                    "<description> /from <start date> /to <end date>");
        } catch (NullPointerException e){
            System.out.println("I am sorry, the event cannot be empty! Please provide a valid event in the format: " +
                    "event <description> /from <start date> /to <end date>");
        }
    }

    public static void saveData() {
        try {
            String path = "data/duke.txt";
            File f = new File(path);
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileWriter fw = new FileWriter(path);
            for (Task task : tasks) {
                fw.write(task.toFileString()+"\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred when accessing the file.");
        }
    }

    public static void loadData() {
        try {
            tasks = new ArrayList<Task>();
            String path = "data/duke.txt";
            File f = new File(path);
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String input = s.nextLine();
                readDataLine(input);
            }
        } catch (IOException e) {
            System.out.println("An error occurred when accessing the file.");
        }
    }

    public static void readDataLine(String input) {
        String[] parts = input.split(" \\| ");
        try {
            Task task;

            switch(parts[0]) {
            case "T":
                task = new Todo(parts[2]);
                break;
            case "D":
                task = new Deadline(parts[2], parts[3]);
                break;
            case "E":
                task = new Event(parts[2], parts[3], parts[4]);
                break;
            default:
                throw new CorruptedFileException();
            }

            int binaryIsDone = Integer.parseInt(parts[1]);
            task.setDone(binaryIsDone);
            tasks.add(task);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | CorruptedFileException e) {
            System.out.println("Failed to read line, the file is corrupted.");
        }
    }
}
