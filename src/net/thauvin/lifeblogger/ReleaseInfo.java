/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Thu Apr 14 03:27:19 PDT 2005 */
package net.thauvin.lifeblogger;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class ReleaseInfo {


   /** buildDate (set during build process to 1113474439925L). */
   private static Date buildDate = new Date(1113474439925L);

   /**
    * Get buildDate (set during build process to Thu Apr 14 03:27:19 PDT 2005).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /**
    * Get buildNumber (set during build process to 1).
    * @return int buildNumber
    */
   public static final int getBuildNumber() { return 1; }


   /** version (set during build process to "0.2"). */
   private static String version = new String("0.2");

   /**
    * Get version (set during build process to "0.2").
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
