package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/* ImageFilter.java is used by FileChooserDemo2.java. */
/**
 * Swing FileFilter used by the application's JFileChooser dialogs so that the
 * browse list only shows image files that this tool can load. It passes through
 * any directory (so the user can navigate into folders) and accepts regular
 * files whose extension matches one of the common raster image formats
 * recognised by the Utils helper (tiff, tif, gif, jpeg, jpg, png). The tiff /
 * tif entries are the formats actually analyzed by the program; the others are
 * accepted so that a user inspecting a mixed folder still sees familiar images.
 */
public class ImageFilter extends FileFilter {

    /**
     * Decides whether a given file should be shown in the file-chooser listing.
     * Directories always return true so the user can descend into them. For a
     * regular file, the extension is extracted via Utils.getExtension(f) and
     * compared against the accepted image extensions defined as constants on
     * Utils (tiff, tif, gif, jpeg, jpg, png); a match returns true, any other
     * extension returns false, and a file with no extension at all also
     * returns false. The parameter f is the candidate file supplied by the
     * JFileChooser, and the local variable extension holds the lower-case
     * extension string (or null if the file has no dot-separated suffix).
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.tiff) ||
                extension.equals(Utils.tif) ||
                extension.equals(Utils.gif) ||
                extension.equals(Utils.jpeg) ||
                extension.equals(Utils.jpg) ||
                extension.equals(Utils.png)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    /**
     * Returns the human-readable label shown in the JFileChooser's file-type
     * pulldown. The fixed string "Just Images" tells the user that this
     * filter limits the listing to the recognised image extensions enforced
     * by accept(File).
     */
    public String getDescription() {
        return "Just Images";
    }
}
