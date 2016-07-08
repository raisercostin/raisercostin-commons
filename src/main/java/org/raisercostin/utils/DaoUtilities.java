package org.raisercostin.utils;

import org.hibernate.cfg.*;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * <pre>
 * Class with utilities functions related with Dao packages/objects.
 * </pre>
 */
public class DaoUtilities {
    /**
     * <pre>
     * 
     * Creates the 'CREATE TABLE' SQLs for all the annotated objects found
     * in the hibernate configuration file received as parameter.
     * In order to use this method from an adapter there must be made a simple 
     * class with a main method that will call this method:
     * 
     * Ex:
     *     public class DaoUtilities {
     *           public static void main(String[] args) {
     *               com.xoom.integration.dao.utils.DaoUtilities.exportSchema(
     *                   &quot;hibernate-metrobank.cfg.xml&quot;,
     *                   &quot;schema-export.sql&quot;
     *               );
     *          }
     *      }
     *  
     * </pre>
     * 
     * @param hibernateConfigurationFileName
     * @param schemaExportFileName
     */
    public static void exportSchema(String hibernateConfigurationFileName, String schemaExportFileName) {
        /* Configuration settings */
        Configuration cfg = new Configuration();
        cfg.configure(hibernateConfigurationFileName);
        // set programatically the naming strategy (in hibernate.cfg is not pisible)
        cfg.setNamingStrategy(new ImprovedNamingStrategy());

        /* Schema export */
        SchemaExport schemaExport = new SchemaExport(cfg);
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        schemaExport.setOutputFile(schemaExportFileName);
        schemaExport.create(true, false);
    }
}
