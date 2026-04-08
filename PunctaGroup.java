package neuron_analyzer;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PunctaGroup {
//keeps info for integrated intensity for the puncta group and the total number of puncta
int intensity;
int counter;


    public PunctaGroup() {
        intensity = 0;
        counter = 0;
    }

    public void addPuncta(int i)
    {
        intensity += i;
        counter++;
    }
}
