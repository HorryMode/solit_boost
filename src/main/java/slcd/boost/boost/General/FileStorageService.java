package slcd.boost.boost.General;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import slcd.boost.boost.General.Exceptions.ResourceAlreadyExistsException;

import java.io.*;

@Service
public class FileStorageService{
    private final String storageDirectory = System.getProperty("user.dir") + "/../storage";

    public void saveFile(MultipartFile file, String extension, String directory) throws IOException {
        String directoryString = storageDirectory + directory;
        File directoryFile = new File(directoryString);
        if(!directoryFile.exists())
            directoryFile.mkdirs();

        File outputFile = new File(
                        directoryString
                                .concat("/")
                                .concat(file.getName())
                                .concat(".")
                                .concat(extension)
        );

        byte [] content = file.getBytes();

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            outputStream.write(content);
        }
    }

    public boolean fileDelete(String uuid, String extension, String directory){
        var filePathString = storageDirectory + directory + "/" + uuid + "." + extension;
        File file = new File(filePathString);

        return file.delete();
    }

    public File getFileContent(String uuid, String extension, String directory){
        var filePathString = storageDirectory + directory + "/" + uuid + "." + extension;
        File file = new File(filePathString);

        return file;
    }

    public void checkFileSize(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        int fileSize = fileBytes.length;
        if (fileSize > Constants.MAX_FILE_SIZE) {
            throw new IllegalArgumentException(Constants.FILE_SIZE_EXCEEDS_MAX_SIZE_MESSAGE);
        }
    }

    public void isFileExists(String fileName, String directory){
        File file = new File(storageDirectory + directory + "/" + fileName);
        if(file.exists())
            throw new ResourceAlreadyExistsException("ааа");
    }
}
