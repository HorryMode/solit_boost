package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FilePayload {
    @NotNull
    private String fileName;

    public String parseNameWithoutExtension(){
        int dotIndex = this.fileName.lastIndexOf(".");
        return this.fileName.substring(0, dotIndex);
    }
    public static String parseNameWithoutExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, dotIndex);
    }

    public String parseExtension(){
        int dotIndex = this.fileName.lastIndexOf(".");
        return this.fileName.substring(dotIndex + 1);
    }

    public static String parseExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex + 1);
    }
}
