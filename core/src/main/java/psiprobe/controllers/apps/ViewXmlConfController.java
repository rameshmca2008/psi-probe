/*
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 */

package psiprobe.controllers.apps;

import org.apache.catalina.Context;
import org.springframework.web.servlet.ModelAndView;

import psiprobe.Utils;
import psiprobe.controllers.ContextHandlerController;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Displays a deployment descriptor (web.xml) or a context descriptor (context.xml) of a web
 * application
 * 
 * @author Andy Shapoval
 * @author Vlad Ilyushchenko
 */
public class ViewXmlConfController extends ContextHandlerController {

  /** The Constant TARGET_WEB_XML. */
  public static final String TARGET_WEB_XML = "web.xml";
  
  /** The Constant TARGET_CONTEXT_XML. */
  public static final String TARGET_CONTEXT_XML = "context.xml";

  /** Type of a file to be displayed. */
  private String displayTarget;

  /** Url that will be used in the view to download the file. */
  private String downloadUrl;

  /**
   * Gets the display target.
   *
   * @return the display target
   */
  public String getDisplayTarget() {
    return displayTarget;
  }

  /**
   * Sets the display target.
   *
   * @param displayTarget the new display target
   */
  public void setDisplayTarget(String displayTarget) {
    this.displayTarget = displayTarget;
  }

  /**
   * Gets the download url.
   *
   * @return the download url
   */
  public String getDownloadUrl() {
    return downloadUrl;
  }

  /**
   * Sets the download url.
   *
   * @param downloadUrl the new download url
   */
  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  @Override
  protected ModelAndView handleContext(String contextName, Context context,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    if (displayTarget == null) {
      throw new RuntimeException("Display target is not set for " + getClass().getName());
    }

    String xmlPath;
    File xmlFile = null;
    ModelAndView mv = new ModelAndView(getViewName());

    if (TARGET_WEB_XML.equals(displayTarget)) {
      ServletContext sctx = context.getServletContext();
      xmlPath = sctx.getRealPath("/WEB-INF/web.xml");
      xmlFile = new File(xmlPath);
      mv.addObject("fileDesc",
          getMessageSourceAccessor().getMessage("probe.src.app.viewxmlconf.webxml.desc"));
    } else if (TARGET_CONTEXT_XML.equals(displayTarget)) {
      xmlFile = getContainerWrapper().getTomcatContainer().getConfigFile(context);
      if (xmlFile != null) {
        xmlPath = xmlFile.getPath();
      } else {
        xmlPath = null;
      }
      mv.addObject("fileDesc",
          getMessageSourceAccessor().getMessage("probe.src.app.viewxmlconf.contextxml.desc"));
    } else {
      throw new RuntimeException("Unknown display target " + getDisplayTarget());
    }

    mv.addObject("displayTarget", displayTarget);
    mv.addObject("downloadUrl", downloadUrl);

    if (xmlFile != null) {
      mv.addObject("fileName", xmlFile.getName());
      if (xmlFile.exists()) {
        FileInputStream fis = new FileInputStream(xmlFile);
        try {
          String encoding = System.getProperty("file.encoding");
          mv.addObject("content", Utils.highlightStream("web.xml", fis, "xml",
              encoding == null ? "ISO-8859-1" : encoding));
        } finally {
          fis.close();
        }
      } else {
        logger.debug("File " + xmlPath + " of " + contextName + " application does not exists.");
      }
    } else {
      logger.debug("Cannot determine path to " + getDisplayTarget() + " file of " + contextName
          + " application.");
    }

    return mv;
  }

}