package io.github.fonfalleh.songuploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FormUploadHelper {
    private static final Logger log = LoggerFactory.getLogger(FormUploadHelper.class);


    public static URI getUriForPort(int port) {
        return URI.create("http://localhost:" + port + "/formupload");
    }

    public static void uploadDirectory(String path, URI uri) {
        Optional<List<String>> filePaths = getFilePathsUnder(path);

        if (filePaths.isEmpty()) return;

        String multiFormBoundary = newBoundary();
        HttpRequest.BodyPublisher bodyPublisher = constructBodyPublisher(multiFormBoundary, filePaths.get());

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .setHeader("Content-Type", "multipart/form-data; boundary=" + multiFormBoundary)
                    .method("POST", bodyPublisher)
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Exception thrown while making request:", e);
            throw new RuntimeException(e);
        }
        log.info("Uploaded dir {} to uri {}", path, uri);

    }

    public static Optional<List<String>> getFilePathsUnder(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("Path {} does not correspond to a directory, won't upload any files.", path);
            return Optional.empty();
        }
        List<String> filePaths = Arrays.stream(dir.listFiles())
                .map(File::getPath)
                .toList();
        return Optional.of(filePaths);
    }

    public static void uploadChildDirectories(String path, URI uri) {
        Optional<List<String>> directories = getFilePathsUnder(path);
        if (directories.isEmpty()) {
            return;
        }
        for (String dir : directories.get()) {
            try {
                uploadDirectory(dir, uri);
            } catch (RuntimeException e) {
                log.warn("Error when trying to upload dir {}.", dir, e);
            }
        }
    }


    private static String newBoundary() {
        return "boundary" + System.nanoTime();
    }

    public static HttpRequest.BodyPublisher constructBodyPublisher(String multiFormBoundary, List<String> filePathStrings, String... formNameAndValue) {
        try {
            StringBuilder builder = new StringBuilder();

            builder.append("--" + multiFormBoundary + "\r\n");
            for (int i = 0; i < formNameAndValue.length; i += 2) {
                builder.append("Content-Disposition: form-data; name=\"").append(formNameAndValue[i]).append("\"\r\n\r\n")
                        .append(formNameAndValue[i + 1]);

                if (i + 2 == formNameAndValue.length && filePathStrings == null) {
                    // Skip separating boundary if last input and no file specified
                    break;
                }
                builder.append("\r\n--" + multiFormBoundary + "\r\n");
            }

            if (filePathStrings == null) {
                builder.append("\r\n--" + multiFormBoundary + "--");
                return HttpRequest.BodyPublishers.ofString(builder.toString());
            }

            HttpRequest.BodyPublisher[] filePublishers = createFilePublishers(multiFormBoundary, filePathStrings);


            return HttpRequest.BodyPublishers.concat(
                    HttpRequest.BodyPublishers.ofString(builder.toString()),
                    HttpRequest.BodyPublishers.concat(filePublishers),
                    HttpRequest.BodyPublishers.ofString("\r\n--" + multiFormBoundary + "--")
            );
        } catch (Exception e) {
            log.error("Exception thrown during body construction", e);
            throw new RuntimeException(e);
        }
    }

    private static HttpRequest.BodyPublisher[] createFilePublishers(String multiFormBoundary, List<String> filePathStrings) throws FileNotFoundException {
        HttpRequest.BodyPublisher[] filePublishers = new HttpRequest.BodyPublisher[filePathStrings.size()];
        for (int i = 0; i < filePathStrings.size(); i++) {
            StringBuilder builder = new StringBuilder();
            File file = new File(filePathStrings.get(i));
            if (i > 0) {
                builder.append("\r\n--" + multiFormBoundary + "\r\n");
            }
            builder.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
            builder.append("Content-Type: application/octet-stream\r\n\r\n"); //Octet stream seems to be generic binary, so seems ok

            filePublishers[i] = HttpRequest.BodyPublishers.concat(
                    HttpRequest.BodyPublishers.ofString(builder.toString()),
                    HttpRequest.BodyPublishers.ofFile(file.toPath())
            );
        }
        return filePublishers;
    }
}
