package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.io.*;

/**
 * {@link FilenameFilter} implementation that accepts only files whose names
 * end with a particular extension. Used by file dialogs and directory scans
 * in the neuron analyzer to restrict browsing to image or project files of
 * a given type (such as TIFF).
 */
public class OnlyExt implements FilenameFilter {
String ext;

/**
 * Constructs the filter for the given extension. The {@code ext} parameter
 * is the extension without a leading dot (for example {@code "tif"}); the
 * constructor prepends the dot and stores the result in the {@code ext}
 * field so {@link #accept(File, String)} can match against it.
 */
public OnlyExt(String ext) {
this.ext = "." + ext;
}

/**
 * Returns {@code true} when the given file name ends with the stored
 * extension. The {@code dir} parameter is ignored because the check is
 * purely name-based; {@code name} is the candidate file name being tested.
 */
public boolean accept(File dir, String name) {
return name.endsWith(ext);
}
}
