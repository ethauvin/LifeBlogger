/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Sat Jul 24 07:59:45 PDT 2004 */
package net.thauvin.lifeblogger;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class ReleaseInfo {


   /** buildDate (set during build process to 1090681185796L). */
   private static Date buildDate = new Date(1090681185796L);

   /**
    * Get buildDate (set during build process to Sat Jul 24 07:59:45 PDT 2004).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /**
    * Get buildNumber (set during build process to 111).
    * @return int buildNumber
    */
   public static final int getBuildNumber() { return 111; }


   /** version (set during build process to "0.1.0"). */
   private static String version = new String("0.1.0");

   /**
    * Get version (set during build process to "0.1.0").
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
