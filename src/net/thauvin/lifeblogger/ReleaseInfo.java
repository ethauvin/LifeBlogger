/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Sun Feb 06 00:48:16 PST 2005 */
package net.thauvin.lifeblogger;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class ReleaseInfo {


   /** buildDate (set during build process to 1107679696812L). */
   private static Date buildDate = new Date(1107679696812L);

   /**
    * Get buildDate (set during build process to Sun Feb 06 00:48:16 PST 2005).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /**
    * Get buildNumber (set during build process to 5).
    * @return int buildNumber
    */
   public static final int getBuildNumber() { return 5; }


   /** version (set during build process to "0.1.2"). */
   private static String version = new String("0.1.2");

   /**
    * Get version (set during build process to "0.1.2").
    * @return String version
    */
   public static final String getVersion() { return version; }


   /** project (set during build process to "LifeBlogger"). */
   private static String project = new String("LifeBlogger");

   /**
    * Get project (set during build process to "LifeBlogger").
    * @return String project
    */
   public static final String getProject() { return project; }

}
