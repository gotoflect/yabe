package models;

import constants.Constant;

public class PageConfig {
	
	public int pagenum;
	public double tx;
	public double ty;
	public double angle;
	
	public double height;
	public double width;
	
	public PageConfig(int pagenum, double tx, double ty, double angle) {
		this.pagenum = pagenum;
		this.tx = tx;
		this.ty = ty;
		this.angle = angle * Constant.ONE_DEGREE;
	}
	
	public PageConfig() {
		
	}
}
