package org.example.utils;

import org.example.dto.TaskDTO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    public List<TaskDTO> parse(File file) throws IOException {
        List<TaskDTO> taskDTOs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);

                if (validateRow(values)) {
                    taskDTOs.add(new TaskDTO(values));
                }
            }
        }
        return taskDTOs;
    }

    public boolean validateRow(String[] row) {
        return row != null && row.length >= 10 && !row[0].trim().isEmpty();
    }
}