package org.flywaydb.test.db;

import java.util.HashMap;
import java.util.Map;

import static org.flywaydb.test.db.DbMigrator.dbMigratorForConfiguration;

public class DbMigratorProvider {
    private static DbMigratorProvider dbMigratorProvider = new DbMigratorProvider();
    private Map<FlywayConfiguration, DbMigrator> dbMigratorForFlywayConfiguration = new HashMap<FlywayConfiguration, DbMigrator>();

    public static DbMigratorProvider dbMigratorProvider() {
        return dbMigratorProvider;
    }

    public DbMigrator provideDbMigratorForConfiguration(FlywayConfiguration flywayConfiguration) {
        if (!dbMigratorForFlywayConfiguration.containsKey(flywayConfiguration)) {
            dbMigratorForFlywayConfiguration.put(flywayConfiguration, dbMigratorForConfiguration(flywayConfiguration));
        }

        return dbMigratorForFlywayConfiguration.get(flywayConfiguration);
    }
}