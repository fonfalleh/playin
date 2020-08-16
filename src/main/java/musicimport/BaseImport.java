package musicimport;

import base.Song;

import java.io.File;

public interface BaseImport {
    public Song importSong(File file);
}
