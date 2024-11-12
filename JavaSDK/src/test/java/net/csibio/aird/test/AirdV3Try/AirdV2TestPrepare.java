package net.csibio.aird.test.AirdV3Try;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;

public class AirdV2TestPrepare {
    static String indexPath = "E:\\msfile_converted\\Aird2Ex";

    @Test
    public void test() throws Exception {
        File folder = new File(indexPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                renameJsonFile(file.toPath());
            }
        }

    }

    private static void renameJsonFile(Path jsonFilePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(jsonFilePath));
            JSONObject jsonObject = JSON.parseObject(jsonContent);

            String type = jsonObject.getString("type");
            String manufacturer = jsonObject.getJSONArray("instruments").getJSONObject(0).getString("manufacturer");
            long fileSize = Files.size(jsonFilePath);
            double fileSizeMB = fileSize / 1024.0;
            String fileSizeMBStr = new DecimalFormat("#.##").format(fileSizeMB) + "MB";


            String newFileName = String.format("%s_%s_%s.json", type, manufacturer, fileSizeMBStr);
            String newAirdName = String.format("%s_%s_%s.aird", type, manufacturer, fileSizeMBStr);

            Path newFilePath = Paths.get(jsonFilePath.getParent().toString(), newFileName);
            Path airdFilePath = Paths.get(jsonFilePath.getParent().toString(),  getNameWithoutExtension(jsonFilePath.getFileName().toString()) + ".aird");
            Path newAirdFilePath = Paths.get(jsonFilePath.getParent().toString(), newAirdName);
            if (Files.exists(airdFilePath)) {
                Files.move(jsonFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                Files.move(airdFilePath, newAirdFilePath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Renamed file: " + newFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + jsonFilePath + " - " + e.getMessage());
        }
    }

    public static String getNameWithoutExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, extensionIndex) ;
    }

}
