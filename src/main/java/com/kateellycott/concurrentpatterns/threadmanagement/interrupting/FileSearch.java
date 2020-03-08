package com.kateellycott.concurrentpatterns.threadmanagement.interrupting;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class FileSearch implements Runnable {
    private final String initPath;
    private final String fileName;

    public FileSearch(String initPath, String fileName) {
        this.initPath = initPath;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        File file = new File(initPath);
        try {
            if (file.isDirectory()) {
                processDirectory(file);
            }
            else {
                processFile(file);
            }
        } catch (InterruptedException e) {
            System.out.println("The search has been interrupted: " + Thread.currentThread().getName());
        }
    }

    private void processDirectory(File file) throws InterruptedException {
        File[] fileList = file.listFiles();
        if(fileList != null) {
            for(File value: fileList) {
                if(value.isDirectory()) {
                    processDirectory(value);
                }
                else {
                    processFile(value);
                }
            }
        }
        if(Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    private void processFile(File file) throws InterruptedException {
        if(file.getName().equals(fileName)) {
            System.out.println("The file has been found: " + file.getAbsolutePath());
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    public static void main(String[] args) {
        FileSearch fileSearch = new FileSearch("D:\\kateellycott", "Вопросы.txt");
        Thread task = new Thread(fileSearch);
        task.start();

        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task.interrupt();
        System.out.println("Is interrupted: " + task.isInterrupted());
        System.out.println("isAlive: " + task.isAlive());
    }
}
