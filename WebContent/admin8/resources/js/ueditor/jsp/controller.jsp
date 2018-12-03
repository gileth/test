<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="com.baidu.ueditor.ActionEnter"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.OutputStream" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%

//    request.setCharacterEncoding( "utf-8" );
//	response.setHeader("Content-Type" , "text/html");
	String rootPath = application.getRealPath( "/" );
	OutputStream out1 = response.getOutputStream();
	out1.write(new ActionEnter( request, rootPath ).exec().getBytes());

//	out.write( new ActionEnter( request, rootPath ).exec() );

%>