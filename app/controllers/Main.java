package controllers;

import java.io.File;
import java.io.FileInputStream;
<<<<<<< HEAD
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import logics.OnePdfRotator;
=======
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import logics.PdfRotator;
>>>>>>> dev
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
	
	public static void manipulate(int height, int width, int pagenum, long horizontal, long longitudinal, double angle, File pdf, String clickbutton) {
		if (Constant.UPLOAD_BUTTON.equals(clickbutton)) {
			setSessions(height, width, pagenum, horizontal, longitudinal, angle);
			String srcFilePath = Play.configuration.getProperty("rotate.inputdir") + Play.configuration.getProperty("rotate.inputFileName");
			makeFile(pdf, srcFilePath);
			index(null);
		} else if (Constant.REGULATE_BUTTON.equals(clickbutton)) {
			setSessions(height, width, pagenum, horizontal, longitudinal, angle);
			Object obj = Cache.get(Constant.PDF_KEY);
			pdf = (File) obj;
			String path = "public/output/" + Constant.OUTPUT_FILE_NAME;
			regulate(height, width, pagenum, horizontal, longitudinal, angle);
			index(path);
		} else if (Constant.OUTPUT_BUTTON.equals(clickbutton)) {
			setSessions(height, width, pagenum, horizontal, longitudinal, angle);
			outputPdf();
		} else {
			Logger.debug("button is not clicked. clickbutton=" + clickbutton);
			error();
		}
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

	private static void regulate(int height, int width, int pagenum, double horizontal, double longitudinal, double angle) {
		Logger.debug("regulate");
		PageConfig pageConfig = new PageConfig(height, width, pagenum, horizontal, longitudinal, angle);
		PdfRotator rotater = new PdfRotator();
		Map<Integer, PageConfig> map = new HashMap<Integer, PageConfig>();
		map.put(pageConfig.pagenum, pageConfig);
		int number = rotater.rotatePdf(Constant.OUTPUT_FILE_NAME, Constant.INPUT_FILE_NAME, map, 0);
		session.put(Constant.NUMBER_OF_PAGES_KEY, number);
		Cache.set(Constant.PAGE_CONFIG_KEY + String.valueOf(pagenum), pageConfig);
	}
	
	private static void outputPdf() {
		Logger.debug("output");
		PdfRotator rotater = new PdfRotator();
		Map<Integer, PageConfig> map = new HashMap<Integer, PageConfig>();
		int number = Integer.valueOf(session.get(Constant.NUMBER_OF_PAGES_KEY));
		for (int i = 0; i < number; i++) {
			PageConfig pageConfig = Cache.get(Constant.PAGE_CONFIG_KEY + String.valueOf(i), PageConfig.class);
			if (pageConfig != null) {
				map.put(pageConfig.pagenum, pageConfig);
			}
		}
		rotater.rotatePdf(Constant.OUTPUT_FILE_NAME, Constant.INPUT_FILE_NAME, map, 0);
	}
	
	private static void outputConfigs() {
		int number = Integer.valueOf(session.get(Constant.NUMBER_OF_PAGES_KEY));
		boolean isFirstPage = true;
		OutputStream os = null;
		try {
			os = new FileOutputStream(Play.configuration.getProperty("rotate.configFilePath"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < number; i++) {
			PageConfig pageConfig = Cache.get(Constant.PAGE_CONFIG_KEY + String.valueOf(i), PageConfig.class);
			if (pageConfig != null) {
				Properties prop = new Properties();
				int pagenum = pageConfig.pagenum;
		        prop.setProperty("tx" + pagenum, String.valueOf(pageConfig.tx));
		        prop.setProperty("ty" + pagenum, String.valueOf(pageConfig.ty));
		        prop.setProperty("angle" + pagenum, String.valueOf(pageConfig.angle));
		        prop.setProperty("height" + pagenum, String.valueOf(pageConfig.height));
		        prop.setProperty("width" + pagenum, String.valueOf(pageConfig.width));
				try {
					prop.storeToXML(os, String.valueOf(pageConfig.pagenum));
					if (isFirstPage) {
						Properties commonProp = new Properties();
						commonProp.storeToXML(os, "common");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}
	
	private static Map<Integer, PageConfig> restoreConfigs() {
        Properties prop = new Properties();
        InputStream is;
		try {
			is = new FileInputStream(Play.configuration.getProperty("rotate.configFilePath"));
			prop.loadFromXML(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<Integer, PageConfig> map = new HashMap<Integer, PageConfig>();
        for (Entry<Object, Object> entry : prop.entrySet()) {
        	PageConfig config = new PageConfig();
            System.out.println(entry.getKey() + " = " + entry.getValue());
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (key.contains("tx")) {
            	String[] keys = key.split("tx");
            	String pagenumStr = keys[1];
            	PageConfig assentConfig = map.get(Integer.valueOf(pagenumStr));
            	if (assentConfig != null) {
            		
            	}
            } else if (key.contains("tx")) {
            	
            } else if (key.contains("ty")) {
            	
            } else if (key.contains("angle")) {
            } else if (key.contains("height")) {
            } else if (key.contains("width")) {
            }
        }
        return null;
	}
	
	private static void setMap(Map<Integer, PageConfig> configMap, String key, String value) {
		boolean hasConfig = false;
		if (key.contains("tx")) {
        	String[] keys = key.split("tx");
        	String pagenumStr = keys[1];
        	PageConfig assentConfig = configMap.get(Integer.valueOf(pagenumStr));
        	if (assentConfig != null) {
        		assentConfig.tx = Double.parseDouble(value);
        	} else {
        		PageConfig newConfig = new PageConfig();
        		newConfig.tx = Double.parseDouble(value);
        		// TODO pagenumê›íË
        		// configMap.put(key, value);
        	}
        } else if (key.contains("ty")) {
        	String[] keys = key.split("ty");
        	String pagenumStr = keys[1];
        	PageConfig assentConfig = configMap.get(Integer.valueOf(pagenumStr));
        	if (assentConfig != null) {
        		assentConfig.ty = Double.parseDouble(value);
        	} else {
        		PageConfig newConfig = new PageConfig();
        		newConfig.ty = Double.parseDouble(value);
        	}
        } else if (key.contains("angle")) {
        	String[] keys = key.split("angle");
        	String pagenumStr = keys[1];
        	PageConfig assentConfig = configMap.get(Integer.valueOf(pagenumStr));
        	if (assentConfig != null) {
        		assentConfig.angle = Double.parseDouble(value);
        	} else {
        		PageConfig newConfig = new PageConfig();
        		newConfig.angle = Double.parseDouble(value);
        	}
        } else if (key.contains("height")) {
        	String[] keys = key.split("height");
        	String pagenumStr = keys[1];
        	PageConfig assentConfig = configMap.get(Integer.valueOf(pagenumStr));
        	if (assentConfig != null) {
        		assentConfig.height = Double.parseDouble(value);
        	} else {
        		PageConfig newConfig = new PageConfig();
        		newConfig.height = Double.parseDouble(value);
        	}
        } else if (key.contains("width")) {
        	String[] keys = key.split("width");
        	String pagenumStr = keys[1];
        	PageConfig assentConfig = configMap.get(Integer.valueOf(pagenumStr));
        	if (assentConfig != null) {
        		assentConfig.width = Double.parseDouble(value);
        	} else {
        		PageConfig newConfig = new PageConfig();
        		newConfig.width = Double.parseDouble(value);
        	}
        }
	}
	
	private static void setSessions(long height, long width, int pagenum, long horizontal, long longitudinal, double angle) {
		session.put(Constant.HEIGHT_KEY, String.valueOf(height));
		session.put(Constant.WIDTH_KEY, String.valueOf(width));
		session.put(Constant.PAGE_NUM_KEY, String.valueOf(pagenum));
		session.put(Constant.HORIZONTAL_KEY, String.valueOf(horizontal));
		session.put(Constant.LONGITUDINAL_KEY, String.valueOf(longitudinal));
		session.put(Constant.ANGLE_KEY, String.valueOf(angle));
	}
}
