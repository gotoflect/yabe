package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import logics.OnePdfRotator;
import models.PageConfig;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.mvc.Controller;
import constants.Constant;

public class Main extends Controller {

	public static void index(String path) {
		render(path);
	}

	public static void manipulate(long height, long width, int pagenum, long horizontal, long longitudinal, long angle, File pdf, String clickbutton) {
		if (Constant.UPLOAD_BUTTON.equals(clickbutton)) {
			Cache.set(Constant.PDF_KEY, pdf);
			index(null);
		} else if (Constant.REGULATE_BUTTON.equals(clickbutton)) {
			Object obj = Cache.get(Constant.PDF_KEY);
			pdf = (File) obj;
			session.put(Constant.HEIGHT_KEY, height);
			session.put(Constant.WIDTH_KEY, width);
			session.put(Constant.PAGE_NUM_KEY, pagenum);
			session.put(Constant.HORIZONTAL_KEY, horizontal);
			session.put(Constant.LONGITUDINAL_KEY, longitudinal);
			session.put(Constant.ANGLE_KEY, angle);

			String srcFilePath = Play.configuration.getProperty("rotate.inputdir") + "inputfile" + session.getId() + ".pdf";
			makeFile(pdf, srcFilePath);
			
			String path = "public/output/" + Constant.OUTPUT_FILE_NAME;
			
			regulate(pagenum, horizontal, longitudinal, angle);
			index(path);
		} else {
			Logger.debug("button is not clicked. clickbutton=" + clickbutton);
			error();
		}
		
//		if (pdf == null) {
//			Object obj = Cache.get(Constant.PDF_KEY);
//			if (obj == null) {
//				Logger.debug("pdf is null");
//				error();
//			}
//		} else {
//			Cache.set(Constant.PDF_KEY, pdf);
//		}
	}

	private static void makeFile(File file, String filePath) {
		File newFile = new File(filePath);
		FileChannel srcChannel = null;
	    FileChannel destChannel = null;
	    try {
	    	srcChannel = new FileInputStream(file).getChannel();
		    destChannel = new FileOutputStream(newFile).getChannel();
	        srcChannel.transferTo(0, srcChannel.size(), destChannel);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
	    	try {
	    		srcChannel.close();
		        destChannel.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	}

	private static void regulate(int pagenum, double horizontal, double longitudinal, double angle) {
		Logger.debug("regulate");
		PageConfig pageConfig = new PageConfig(pagenum, horizontal, longitudinal, angle);
		OnePdfRotator rotater = new OnePdfRotator();
		Map<Integer, PageConfig> map = new HashMap<Integer, PageConfig>();
		map.put(pageConfig.pagenum, pageConfig);
		rotater.rotatePdf(Constant.OUTPUT_FILE_NAME, "input" + session.getId() + ".pdf", map);
	}
}
