package org.takeback.chat.controller;

import java.io.File;

public class Test {
	
	public static void list(File file) {
		if(file.isDirectory()) {
			System.out.println("java -jar procyon-decompiler-0.5.30.jar "+file.getPath()+"//***.class -o e://test");
			File[] files = file.listFiles();
			for(File f : files) {
				list(f);
			}
		}
	}

	public static void main(String[] args) {
		File file = new File("E:\\工作\\仿微信扫雷\\仿微信扫雷\\扫雷\\apache-tomcat-8.0.29\\webapps\\ROOT\\WEB-INF\\classes\\org");
		list(file);
	}
}
