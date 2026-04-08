package neuron_analyzer;
import java.io.DataOutputStream;
import java.io.DataInputStream;
public interface Savable {

	public void Save(DataOutputStream ds,IoContainer i);
	public Object Load(DataInputStream di, IoContainer i,int version,Group[] groupList);
}
