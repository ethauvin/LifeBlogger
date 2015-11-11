/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Wed Nov 11 01:32:04 PST 2015 */
package net.thauvin.lifeblogger;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class ReleaseInfo {


   /** buildDate (set during build process to 1447234324519L). */
   private static Date buildDate = new Date(1447234324519L);

   /**
    * Get buildDate (set during build process to Wed Nov 11 01:32:04 PST 2015).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /** project (set during build process to "LifeBlogger"). */
   private static String project = new String("LifeBlogger");

   /**
    * Get project (set during build process to "LifeBlogger").
    * @return String project
    */
   public static final String getProject() { return project; }


   /** version (set during build process to "0.2"). */
   private static String version = new String("0.2");

   /**
    * Get version (set during build process to "0.2").
    * @return String version
    */
   public static final String getVersion() { return version; }


   /**
    * Get buildNumber (set during build process to 2).
    * @return int buildNumber
    */
   public static final int getBuildNumber() { return 2; }

}
