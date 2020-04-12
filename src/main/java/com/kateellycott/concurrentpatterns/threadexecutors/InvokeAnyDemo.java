package com.kateellycott.concurrentpatterns.threadexecutors;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

class UserValidator {

    private final String name;

    UserValidator(String name) {
        this.name = name;
    }

    boolean validate(String name, String password) {
        Random random = new Random();
        long duration = (long)(Math.random()*10);
        System.out.printf(" UserValidator: %s performing a validation for %d ms\n", this.name, duration);
        try {
            TimeUnit.MILLISECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            return false;
        }
        return random.nextBoolean();
    }

    String getName() {
        return name;
    }
}

class ValidatorTask implements Callable<String> {

    private final UserValidator userValidator;
    private final String userName;
    private final String password;


    ValidatorTask(UserValidator userValidator, String userName, String password) {
        this.userValidator = userValidator;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String call() throws Exception {
        boolean result = userValidator.validate(userName, password);
        if(!result) {
            System.out.printf("Validator: %s The user has not been found\n", userValidator.getName());
            throw new Exception("Error validating user\n");
        }
            System.out.printf("Validator: %s The has been found\n", userValidator.getName());
            return userValidator.getName();
    }
}
public class InvokeAnyDemo {
    public static void main(String[] args) {
        String userName = "user";
        String password = "user";

        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DATABASE");

        ValidatorTask ldapTask = new ValidatorTask(ldapValidator, userName, password);
        ValidatorTask dbTask = new ValidatorTask(dbValidator, userName, password);

        List<ValidatorTask> validators = new ArrayList<>();
        validators.add(ldapTask);
        validators.add(dbTask);

        ExecutorService executorService = (ExecutorService) Executors.newCachedThreadPool();
        String  result;

        try {
            result = executorService.invokeAny(validators);
            System.out.printf("Result (validator name): %s\n", result);
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        System.out.printf("Main: End of the execution");

    }
}
