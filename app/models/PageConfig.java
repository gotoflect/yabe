package models;

import play.db.jpa.Model;
import constants.Constant;

public class PageConfig extends Model {
	
	private static final long serialVersionUID = 4090240856394314118L;
	
	public int pagenum;
	public double tx;
	public double ty;
	public double angle;
	public double pdfAngle;
	
	public double height;
	public double width;
	
	public PageConfig(int height, int width, int pagenum, double tx, double ty, double angle) {
		this.height = height;
		this.width = width;
		this.pagenum = pagenum;
		this.tx = tx;
		this.ty = ty;
		this.angle = angle;
		this.pdfAngle = angle * Constant.ONE_DEGREE;
	}
	
	public PageConfig() {
		
	}
}
