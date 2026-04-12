package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.io.DataOutputStream;
import java.io.DataInputStream;

/**
 * Persistence contract implemented by any neuron-analyzer data object that
 * can be written to and read from the application's binary project files.
 * The interface pairs a writer with a reader so an object can round-trip its
 * state through a {@link DataOutputStream}/{@link DataInputStream}, using an
 * {@code IoContainer} to share auxiliary bookkeeping (such as string tables
 * or cross-references) across the whole save/load operation.
 */
public interface Savable {

	/**
	 * Writes this object's state to the given output stream. The {@code ds}
	 * parameter is the stream to serialize into; {@code i} is a shared
	 * {@code IoContainer} used to coordinate the save with other objects
	 * being written in the same pass (for example to emit cross references
	 * or deduplicated data).
	 */
	public void Save(DataOutputStream ds,IoContainer i);

	/**
	 * Reconstructs an object from the given input stream and returns it.
	 * The {@code di} parameter is the source stream; {@code i} is the shared
	 * {@code IoContainer} carrying load-time context (matching the one used
	 * during save); {@code version} identifies the on-disk format revision
	 * so the implementation can branch on older layouts; and
	 * {@code groupList} provides the array of resolved {@link Group}
	 * instances so deserialized objects can be re-linked to the correct
	 * group by numeric id.
	 */
	public Object Load(DataInputStream di, IoContainer i,int version,Group[] groupList);
}
