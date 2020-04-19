package  com.kateellycott.concurrentpatterns.forkjoinframework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

class FolderProcessor extends CountedCompleter<List<String>> {


    private String path;
    private String extension;
    private List<String> results;
    private List<FolderProcessor> tasks;

    FolderProcessor(String path, String extension) {
        this.path = path;
        this.extension = extension;
    }

    protected FolderProcessor(CountedCompleter<?> completer, String path, String extension) {
        super(completer);
        this.path = path;
        this.extension = extension;
    }

    @Override
    public void compute() {
        results = new ArrayList<>();
        tasks = new ArrayList<>();

        File file = new File(path);
        File[] files = file.listFiles();
        if(files != null) {
            for(int i = 0; i < files.length; i++) {
                if(files[i].isDirectory()) {
                    FolderProcessor task = new FolderProcessor(this,
                            files[i].getAbsolutePath(), extension);
                    task.fork();
                    addToPendingCount(1);
                    tasks.add(task);
                }
                else if(checkFile(files[i].getAbsolutePath(), extension)) {
                    results.add(files[i].getAbsolutePath());
                }
            }
        }
        tryComplete();
    }

    @Override
    public void onCompletion(CountedCompleter<?> caller) {
        for(int i = 0; i < tasks.size(); i++) {
            FolderProcessor childTask = tasks.get(i);
            results.addAll(childTask.getRawResult());
        }
    }

    public List<String> getResultList() {
        return  results;
    }

    @Override
    public List<String> getRawResult() {
        return results;
    }

    private boolean checkFile(String filePath, String extension) {
        return (filePath.endsWith(extension));
    }
}

public class CountedCompleterDemo {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();

        FolderProcessor system = new FolderProcessor("C:\\Windows", ".log");
        FolderProcessor apps = new FolderProcessor("C:\\Program Files", ".exe");
        FolderProcessor documents = new FolderProcessor("C:\\Documents And Settings", ".log");

        pool.execute(system);
        pool.execute(apps);
        pool.execute(documents);

        do {
            System.out.printf("*********************************************\n");
            System.out.printf("Main: Active Threads: %d\n", pool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n", pool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n", pool.getStealCount());
            System.out.printf("*********************************************\n");

            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ((!system.isDone())||(!apps.isDone())||(!documents.isDone()));

        pool.shutdown();

        List<String> results;

        results = system.join();
        System.out.printf("System: %d files are found.\n", results.size());

        results = apps.join();
        System.out.printf("Apps: %d files are found.\n", results.size());

        results = documents.join();
        System.out.printf("Documents: %d files are found.\n", results.size());



    }
}