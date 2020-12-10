package app;

import java.util.Scanner;

public final class MyThread extends Thread{
    public static void main(String[] args){

        System.out.println("User can enter CTRL + C to terminate the program");

        final MyThread thread = new MyThread();
        final Input userInput = thread.new Input();
        final SharedResources sr = new SharedResources();

        //Thread will shut down console program by entering CTRL + C
        Runtime.getRuntime().addShutdownHook(thread);

        final int numberOfDispatcher = userInput.inquireNumberOfDispatcher();

        final Dispatcher[] dispatcherThread = new Dispatcher[numberOfDispatcher];
        final Sorter sortItemThread = new Sorter(sr, dispatcherThread);
        final Item arrivalItemThread = new Item(sr, numberOfDispatcher);

        sortItemThread.start();
        arrivalItemThread.start();
        thread.instantiateAllDispatcher(dispatcherThread);
    }


    private void instantiateAllDispatcher(final Dispatcher[] dispatcherThread) {
        for (int i = 0; i < dispatcherThread.length; i++) {
            dispatcherThread[i] = new Dispatcher();
            dispatcherThread[i].start();
        }
    }


    //User defined cancel handler, terminate program
    //By entering CTRL + C, it will terminate the program
    //The output below will be the prove that it worked
    @Override
    public void run() {
        System.out.println("\nPerforming shutdown...");
        System.out.println("All threads are shutdown");
    }

    private final class Input {

        private boolean tryParseInt(final String input) {
            try {
                final int integer = Integer.parseInt(input);
                return integer > 0 && integer <= 26;
            } catch (final NumberFormatException e) {
                return false;
            }
        }

        private int inquireNumberOfDispatcher() {
            final Scanner scanner = new Scanner(System.in);
            boolean isInt = false;
            String input = null;
            while (!isInt) {
                System.out.print("To set number of dispatcher(s). Enter integer from 1 to 26 only: ");
                input = scanner.nextLine();
                isInt = this.tryParseInt(input);
            }
            scanner.close();
            return Integer.parseInt(input);
        }
    }
}