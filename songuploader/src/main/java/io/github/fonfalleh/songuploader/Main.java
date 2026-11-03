package io.github.fonfalleh.songuploader;

import java.net.URI;

public class Main {

    static void main(String[] args) {
        //TODO better arg handling
        if (args.length >= 2 && args[0].equals("formupload")) {
            String formuploadURIString = args.length >= 3 ? args[2] : "http://localhost:7070/formupload";
            FormUploadHelper.uploadDirectory(args[1], URI.create(formuploadURIString));
        }
    }
}
