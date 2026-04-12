package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * A {@link LinkedList} subclass that additionally carries an integer key,
 * effectively turning the list into a keyed bucket. The neuron analyzer
 * uses this to associate a collection of related entries with a numeric
 * identifier (for example, a color, group id, or spatial bin) so the group
 * can be looked up and enumerated together.
 */
public class Keys extends LinkedList {
int key;

    /**
     * Constructs an empty keyed list tagged with the given key. The
     * parameter {@code k} is stored in the {@code key} field and used later
     * by callers to identify which bucket this list represents; the
     * underlying linked-list storage is initialized via {@code super()}.
     */
    public Keys(int k) {
        super();
        key = k;
    }
}
