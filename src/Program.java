import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Program {
    private ArrayDeque<ArrayList<Integer>> messagesQueue = new ArrayDeque<>();
    private ArrayList<Boolean> resultsList = new ArrayList<>();
    private Sequence[] sequencesArray;
    private boolean filled = false;

    private double correctionEffectiveness = 0;
    private int extraBits;
    private double extraBitsCoef;
    private double speedWeight;
    private double correctionWeight;
    private double sequenceEffectiveness = 0;

    public void setSpeedWeight(double speedWeight){
        this.speedWeight = speedWeight;
    }

    public void setCorrectionWeight(double correctionWeight){
        this.correctionWeight = correctionWeight;
    }

    public Program(int sequenceAmount){
        sequencesArray = new Sequence[sequenceAmount];
    }

    public void startTransmission(String[] codersSet, int numOfErrors) {
        correctionEffectiveness = 0;
        messagesQueue.clear();
        resultsList.clear();

        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Integer[] data = new Integer[10];
            for (int j = 0; j < 10; j++) {
                data[j] = random.nextBoolean() ? 1 : 0;
            }
            messagesQueue.add(new ArrayList<Integer>(Arrays.asList(data)));
        }
        filled = true;


        for(int i = 0; i < sequencesArray.length; i++){
            sequencesArray[i] = new Sequence(messagesQueue, resultsList, codersSet, numOfErrors);
            sequencesArray[i].start();
        }

        try {
            while (filled & !messagesQueue.isEmpty())
                Thread.sleep(10);
        } catch (InterruptedException e) {
        }

        for(int i = 0; i < sequencesArray.length; i++){
            extraBits = sequencesArray[i].getExtraBits();
            extraBitsCoef = sequencesArray[i].getExtraBitsCoef();
            sequencesArray[i].interrupt();
        }

        for (Boolean result : resultsList
             ) {
            if(result) correctionEffectiveness++;
        }

        correctionEffectiveness = correctionEffectiveness / resultsList.size();
        sequenceEffectiveness = (Math.pow(correctionEffectiveness, correctionWeight) * Math.pow(extraBitsCoef, speedWeight));

        return;
    }

    public double getExtraBits(){
        return extraBits;
    }

    public double getCorrectionEffectiveness(){
        return correctionEffectiveness;
    }

    public double getSequenceEffectiveness(){
        return sequenceEffectiveness;
    }
}
