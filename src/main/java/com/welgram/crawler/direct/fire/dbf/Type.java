package com.welgram.crawler.direct.fire.dbf;

import java.util.ArrayList;
import java.util.HashMap;

public class Type {
	private String type;
	private ArrayList<HashMap<String, String>> option;
	
	public Type(){
		
	}

	public Type(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<HashMap<String, String>> getOption() {
		return option;
	}
	public void setOption(ArrayList<HashMap<String, String>> option) {
		this.option = option;
	}
	
}
