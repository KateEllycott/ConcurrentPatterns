package com.kateellycott.concurrentpatterns.threadsynchronization.utilities;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

class FileSearch implements Runnable {

    private final String initPath;
    private final String fileExtension;
    private Phaser phaser;
    private List<String> results;

    FileSearch(String initPath, String fileExtension, Phaser phaser) {
        this.initPath = initPath;
        this.fileExtension = fileExtension;
        this.phaser = phaser;
        results = new ArrayList<>();
    }

    private void processDirectory(File file) {
        File[] files = FileSystemView.getFileSystemView().getFiles(file, false);
        for(File value: files) {
            if(value.isDirectory()) {
                processDirectory(value);
            }
            else {
                processFile(value);
            }
        }
    }

    private boolean checkResults() {
        if(results.isEmpty()) {
            System.out.printf("%s: Phase: %d: 0 results\n", Thread.currentThread().getName(),
                    phaser.getPhase());
            System.out.printf("%s: Phase: %d: end\n", Thread.currentThread().getName(),
                    phaser.getPhase());
            phaser.arriveAndDeregister();
            return false;
        }
        else {
            System.out.printf("%s: Phase %d: %d results\n", Thread.currentThread().getName(),
                    phaser.getPhase(), results.size());
            phaser.arriveAndAwaitAdvance();
            return true;
        }
    }

    private void showInfo() {
        for(String value: results) {
            File file = new File(value);
            System.out.printf("%s: %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        phaser.arriveAndAwaitAdvance();
    }

    private void processFile(File file) {
        if(file.getName().endsWith(fileExtension)) {
            results.add(file.getAbsolutePath());
        }
    }

    private void filterResults() {
        List<String> filteredResults = new ArrayList<>();
        long actualDate = new Date().getTime();

        for(String value: results) {
            File file = new File(value);
            long fileDate = file.lastModified();

            if(actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                filteredResults.add(value);
            }
        }
        results = filteredResults;
    }
    @Override
    public void run() {
        phaser.arriveAndAwaitAdvance();
        System.out.printf("%s: Starting\n", Thread.currentThread().getName());
        File file = new File(initPath);
        if(file.isDirectory()) {
            processDirectory(file);
        }
        if(!checkResults()) {
            return;
        }
        filterResults();
        if(!checkResults()) {
            return;
        }
        showInfo();
        phaser.arriveAndDeregister();
        System.out.printf("%s: Work is completed\n", Thread.currentThread().getName());
    }
}

public class PhaserDemo {

    public static void main(String[] args) {
        Phaser phaser = new Phaser(3);

        FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
        FileSearch apps = new FileSearch("C:\\Program Files", "log", phaser);
        FileSearch documents = new FileSearch("C:\\files", "txt", phaser);

        Thread systemThread = new Thread(system, "System");
        Thread appsThread = new Thread(apps, "App");
        Thread documentsThread = new Thread(documents, "Document");

        systemThread.start();
        appsThread.start();
        documentsThread.start();

        try {
            systemThread.join();
            appsThread.join();
            documentsThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Terminated: " + phaser.isTerminated());
    }
}
