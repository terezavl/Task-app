package com.example.taskmanagment.service;

import com.example.taskmanagment.model.entities.Task;
import com.opencsv.CSVWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class FileUtil {
    public static ByteArrayOutputStream writeTasksCSV(List<Task> tasks){
        ByteArrayOutputStream csvContent = new ByteArrayOutputStream();
        try (CSVWriter csvWriter = new CSVWriter(
                new OutputStreamWriter(csvContent), ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, "\n")) {
            csvWriter.writeNext(new String[] { "TaskId", "Title", "Description", "Priority", "IsFinished" });
            for (Task task : tasks) {
                csvWriter.writeNext(new String[] { String.valueOf(task.getId()), task.getTitle(), task.getDescription(),
                        String.valueOf(task.getPriority()) , String.valueOf(task.getIsFinished()) });
            }
        } catch (IOException e) {
            System.out.println("Problem writing to file");
        }
        return csvContent;
    }
}
