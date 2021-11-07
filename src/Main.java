import dispatch.Dispatcher;
import item.Item;
import sharedResources.SharedResources;
import sorter.Sorter;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public final class Main {

    private final Sorter sorter;
    private final Item item;

    private Main (final int numberOfDispatcher) {
        final SharedResources sharedResources = new SharedResources();
        final List<Dispatcher> dispatcherList = this.createDispatchers(numberOfDispatcher);
        this.sorter = new Sorter(sharedResources, dispatcherList);
        this.item = new Item(sharedResources, numberOfDispatcher);
    }

    public static void main(final String[] args){
        new Main(new Input().inquireNumberOfDispatcher()).startMain();
    }

    private List<Dispatcher> createDispatchers(final int numberOfDispatcher) {
        return IntStream.range(0, numberOfDispatcher).boxed().map(Dispatcher::new).toList();
    }

    private void startMain() {
        this.sorter.start();
        this.item.start();
    }

    private final static class Input {

        private final static Pattern NUMBER_ONLY_PATTERN = Pattern.compile("^(2[0-6]|1[0-9]|[1-9])$");

        private int inquireNumberOfDispatcher() {
            final Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("To set number of dispatcher(s). Enter integer from 1 to 26 only: ");
                final String input = scanner.nextLine();
                if (NUMBER_ONLY_PATTERN.matcher(input).matches()) {
                    scanner.close();
                    return Integer.parseInt(input);
                }
            }
        }
    }
}