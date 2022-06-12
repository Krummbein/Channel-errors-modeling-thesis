import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HammingCoder extends Coder {
    int length = 14;
    int prt = 0;

    int getParity(int b[], int power) {
        int parity = 0;
        for(int i=0 ; i < b.length ; i++) {
            if(b[i] != 2) {
                // If 'i' doesn't contain an unset value,
                // We will save that index value in k, increase it by 1,
                // Then we convert it into binary:

                int k = i+1;
                String s = Integer.toBinaryString(k);

                //Nw if the bit at the 2^(power) location of the binary value of index is 1
                //Then we need to check the value stored at that location.
                //Checking if that value is 1 or 0, we will calculate the parity value.

                int x = ((Integer.parseInt(s))/((int) Math.pow(10, power)))%10;
                if(x == 1) {
                    if(b[i] == 1) {
                        parity = (parity+1)%2;
                    }
                }
            }
        }
        return parity;
    }

    public int getFragmentSize() {
        return length;
    }

    public ArrayList<Integer> encode(ArrayList<Integer> message)
    {
        // We will return the array 'b'.
        int[] a = new int[message.size()];

        for(int i = 0; i<a.length; i++){
            a[i] = message.get(i).intValue();
        }

        int b[];

        // We find the number of parity bits required:
        int i=0, parity_count=0 ,j=0, k=0;
        while(i < message.size()) {
            // 2^(parity bits) must equal the current position
            // Current position is (number of bits traversed + number of parity bits + 1).
            // +1 is needed since array indices start from 0 whereas we need to start from 1.

            if(Math.pow(2,parity_count) == i+parity_count + 1) {
                parity_count++;
            }
            else {
                i++;
            }
        }

        // Length of 'b' is length of original data (a) + number of parity bits.
        b = new int[message.size() + parity_count];

        // Initialize this array with '2' to indicate an 'unset' value in parity bit locations:

        for(i=1 ; i <= b.length ; i++) {
            if(Math.pow(2, j) == i) {
                // Found a parity bit location.
                // Adjusting with (-1) to account for array indices starting from 0 instead of 1.

                b[i-1] = 2;
                j++;
            }
            else {
                b[k+j] = message.get(k++);
            }
        }
        for(i=0 ; i < parity_count ; i++) {
            // Setting even parity bits at parity bit locations:

            b[((int) Math.pow(2, i))-1] = getParity(b, i);
        }

        prt = parity_count;

        ArrayList<Integer> encodedMessage = new ArrayList<>();

        for(int f = 0; f < b.length; f++){
            encodedMessage.add(b[f]);
        }

        return encodedMessage;
    }

    public ArrayList<Integer> decode(ArrayList<Integer> message)
    {
        //, int parity_count
        // This is the receiver code. It receives a Hamming code in array 'a'.
        // We also require the number of parity bits added to the original data.
        // Now it must detect the error and correct it, if any.

        int parity_count = prt;

        int[] receivedMessage = new int[message.size()];

        for(int i = 0; i<receivedMessage.length; i++){
            receivedMessage[i] = message.get(i).intValue();
        }

        int power;
        // We shall use the value stored in 'power' to find the correct bits to check for parity.

        int parity[] = new int[parity_count];
        // 'parity' array will store the values of the parity checks.

        String syndrome = new String();
        // 'syndrome' string will be used to store the integer value of error location.

        for(power=0 ; power < parity_count ; power++) {
            // We need to check the parities, the same no of times as the no of parity bits added.

            for(int i=0 ; i < receivedMessage.length ; i++) {
                // Extracting the bit from 2^(power):

                int k = i+1;
                String s = Integer.toBinaryString(k);
                int bit = ((Integer.parseInt(s))/((int) Math.pow(10, power)))%10;
                if(bit == 1) {
                    if(receivedMessage[i] == 1) {
                        parity[power] = (parity[power]+1)%2;
                    }
                }
            }
            syndrome = parity[power] + syndrome;
        }

        // This gives us the parity check equation values.
        // Using these values, we will now check if there is a single bit error and then correct it.

        try{
            int error_location = Integer.parseInt(syndrome, 2);
            if(error_location != 0) {
                if(error_location-1 <= receivedMessage.length)
                    receivedMessage[error_location-1] = (receivedMessage[error_location-1]+1)%2;
            }
        } catch (ArrayIndexOutOfBoundsException e){
        }


        ArrayList<Integer> messageHolder = new ArrayList<>();

        // Finally, we shall extract the original data from the received (and corrected) code:
        power = parity_count-1;
        for(int i=receivedMessage.length ; i > 0 ; i--) {
            if(Math.pow(2, power) != i) {
                messageHolder.add(receivedMessage[i-1]);
            }
            else {
                power--;
            }
        }

        Collections.reverse(messageHolder);

        // message = messageHolder.toArray(new Integer[messageHolder.size()]);

        return messageHolder;
    }
}
