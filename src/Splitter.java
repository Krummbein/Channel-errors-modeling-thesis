import java.util.ArrayList;
import java.util.Collections;

public class Splitter {

    private ArrayList<ArrayList<Integer>> split(ArrayList<Integer> fullMessage, int fragmentSize, boolean toFillUp) {
        ArrayList<ArrayList<Integer>> messageFragments = new ArrayList<>();

        int fullParts = fullMessage.size() / fragmentSize;
        int extraBits = fullMessage.size() % fragmentSize;

        for (int i = 0, part = 0; part < fullParts; i += fragmentSize, part++) {
            ArrayList<Integer> fragment = new ArrayList<>();
            fragment.addAll(0, fullMessage.subList(i, i + fragmentSize));
            messageFragments.add(fragment);
        }

        if (extraBits != 0 && toFillUp) {
            ArrayList<Integer> fragment = new ArrayList<>();
            fragment.addAll(0, fullMessage.subList(fullMessage.size() - extraBits, fullMessage.size()));
            for (int i = 0; i < fragmentSize - extraBits; i++) {
                fragment.add(0);
            }
            messageFragments.add(fragment);
        }

        return messageFragments;
    }

    private ArrayList<Integer> merge(ArrayList<ArrayList<Integer>> messageFragments) {
        ArrayList<Integer> fullMessage = new ArrayList<>();

        for (ArrayList<Integer> fragment : messageFragments
        ) {
            fullMessage.addAll(fragment);
        }

        return fullMessage;
    }

    public ArrayList<Integer> processMessage(ArrayList<Integer> message, int fragmentSize, Coder coder, boolean processingType, boolean toFillUp){

        ArrayList<ArrayList<Integer>> fragments = split(message, fragmentSize, toFillUp);

        ArrayList<ArrayList<Integer>> processedFragments = new ArrayList<>();

        for (int i = 0; i < fragments.size(); i++) {
            if(processingType)
                processedFragments.add(i, coder.encode(fragments.get(i)));
            else
                processedFragments.add(i, coder.decode(fragments.get(i)));
        }

        ArrayList<Integer> processedMessage = merge(processedFragments);

        return processedMessage;
    }

    public ArrayList<Integer> addErrors(ArrayList<Integer> encodedMessage, int numOfErrors){
        if (numOfErrors != 0) {

            ArrayList<Integer> errpos = new ArrayList<>();
            for (int i = 0; i < encodedMessage.size(); i++){
                errpos.add(i);
            }

            Collections.shuffle(errpos);
            errpos = new ArrayList<>(errpos.subList(0, numOfErrors));

            for (int i = 0; i < numOfErrors; i++){
                int position = errpos.get(i);
                Integer corruptedBit = encodedMessage.get(position) ^ 1;
                encodedMessage.set(position, corruptedBit);
            }
        }

        return encodedMessage;
    }

}

