package implementation;

/**
 * Implementation copied from
 * https://w3sdesign.com/GoF_Design_Patterns_Reference0100.pdf
 * Main driver for the program
 * This is where the modules statrts executuion
 */
public class Driver {
    public static void main(String[] args) {
        ConcreteAggregate<Integer> integerBox = new ConcreteAggregate<>(9);
        for (int i = 9; i > 0; --i) {
            integerBox.add(i);
        }
        // getData() has been removed.
        // Client has to use Iterator.
        Iterator firstItr = integerBox.createIterator();
        Iterator secondItr = integerBox.createIterator();

        // Two simultaneous iterations
        while (firstItr.hasNext()){
            System.out.println("Iterator 1 -"+ firstItr.next()+" -- Iterator 2 -"+secondItr.next());
        }
    }

    public void run(){
        main(null);
    }
}
