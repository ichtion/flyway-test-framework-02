package org.flywaydb.test.runner;

import org.flywaydb.core.api.MigrationVersion;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static org.flywaydb.test.runner.TestInstanceProvider.testInstanceProvider;

class FlywayMigrationSuiteRunner extends ParentRunner<FlywayParticularMigrationTestRunner> {
    private final MigrationVersion migrationVersion;
    private final List<FlywayParticularMigrationTestRunner> childRunners;

    public FlywayMigrationSuiteRunner(SuiteForMigrationVersion suiteForMigrationVersion) throws InitializationError {
        super(suiteForMigrationVersion.getClass());
        this.migrationVersion = suiteForMigrationVersion.getMigrationVersion();
        childRunners = getChildRunners(suiteForMigrationVersion);
    }

    private List<FlywayParticularMigrationTestRunner> getChildRunners(SuiteForMigrationVersion suiteForMigrationVersion) throws InitializationError {
        List<FlywayParticularMigrationTestRunner> childRunners = new ArrayList<FlywayParticularMigrationTestRunner>();
        List<FlywayParticularMigrationTestRunner> beforeMigrationRunners = new ArrayList<FlywayParticularMigrationTestRunner>();
        List<FlywayParticularMigrationTestRunner> afterMigrationRunners = new ArrayList<FlywayParticularMigrationTestRunner>();

        for (Class<?> testClass : suiteForMigrationVersion.getClasses()) {
            FlywayTest flywayTest = new FlywayTest(testClass);
            testInstanceProvider().createInstanceOf(flywayTest);

            beforeMigrationRunners.add(new FlywayBeforeParticularMigrationTestRunner(flywayTest));
            afterMigrationRunners.add(new FlywayAfterParticularMigrationTestRunner(flywayTest));
        }

        childRunners.addAll(beforeMigrationRunners);
        childRunners.addAll(afterMigrationRunners);

        return childRunners;
    }

    @Override
    protected List<FlywayParticularMigrationTestRunner> getChildren() {
        return childRunners;
    }

    @Override
    protected String getName() {
        return "v" + migrationVersion.getVersion().replace(".", "_");
    }

    @Override
    protected Description describeChild(FlywayParticularMigrationTestRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(FlywayParticularMigrationTestRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        return childrenInvoker(notifier);
    }
}
