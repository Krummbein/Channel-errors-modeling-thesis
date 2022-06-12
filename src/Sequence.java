import java.util.ArrayDeque;
import java.util.ArrayList;

public class Sequence extends Thread{
    private ArrayDeque<ArrayList<Integer>> messages;
    private ArrayList<Boolean> results;
    private ArrayList<Coder> coders;
    private ArrayList<Integer> starterMessage;
    private ArrayList<Integer> message;
    private int numOfErrors;
    private Splitter splitter;
    private int extraBits;


    public Sequence(ArrayDeque<ArrayList<Integer>> messages, ArrayList<Boolean> results,String[] codersSequence, int numOfErrors){
        this.messages = messages;
        this.results = results;
        this.numOfErrors = numOfErrors;

        splitter = new Splitter();
        coders = new ArrayList<>();

        for (String coderName : codersSequence
             ) {
            switch(coderName){
                case "Hamming": {
                    coders.add(new HammingCoder());
                    break;
                }
                case "BCH": {
                    coders.add(new BchCoder(30, 2));
                    break;
                }
            }
        }
    }

    public void getMessage() throws InterruptedException {
        synchronized (messages) {
            while (messages.isEmpty()) {
                messages.wait();
            }

            message = starterMessage = messages.poll();
            messages.notify();
        }
    }

    public void writeToResultList() {
        synchronized (results) {
            results.add(starterMessage.equals(message));

            results.notify();
        }
    }

    public double getExtraBitsCoef(){
        return (double)starterMessage.size()/ (starterMessage.size() + extraBits);
    }

    public int getExtraBits(){
        return extraBits;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                getMessage();

                for (Coder c : coders) {
                    message = splitter.processMessage(message, 10, c, true, true);
                }

                extraBits = message.size() - starterMessage.size();

                splitter.addErrors(message, numOfErrors);

                for (int i = coders.size() - 1; i >= 0; i--) {
                    message = splitter.processMessage(message, coders.get(i).getFragmentSize(), coders.get(i), false, false);
                }

                writeToResultList();

                sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
