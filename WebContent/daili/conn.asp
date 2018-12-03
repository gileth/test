<%
'全局变量设定
dim cpz,cgd,conn1,webname,conn,ceshi,conn22,conn2,bili
dim rs,rs2,sqluser,sqldz,sqlpass,db_mac
set rs = Server.CreateObject("ADODB.RecordSet")
rs.CursorLocation=2
set rs2 = Server.CreateObject("ADODB.RecordSet")
rs2.CursorLocation=2
	sqldz = "127.0.0.1"
	sqluser	= "root"
	sqlpass	= "nuttertoolstf..@@#"
	db_mac = "gamechat"
conn1 = "DRIVER={MySQL ODBC 3.51 Driver};SERVER="&sqldz&";DATABASE="&db_mac&";User="&sqluser&";PASSWORD="&sqlpass&";Option=3;"
Sub SetConn()
Set conn = SERVER.CreateObject("ADODB.Connection")
conn.Open = conn1
conn.CursorLocation=3 
End Sub
Sub SetConn2()
Set conn2 = SERVER.CreateObject("ADODB.Connection")
conn2.Open = conn22
conn2.CursorLocation=3 
End Sub
Function re(xx)
re=request(xx)
End Function
Function re2(xx)
re2=request.Form(xx)
End Function
Function re3(xx)
re3=request.QueryString(xx)
End Function
Sub ts(nr)'提示框
response.Write("<script>alert('"&nr&"');</script>")
End Sub
Sub return()
response.Write("<script>history.back(-1);</script>")
response.end
End Sub
Sub tz(url)
response.Write("<script>window.location.replace('"&url&"');</script>")
End Sub
Function se(xx)
se=session(xx)
End Function
Function GetUrl(action)
GetUrl=request.servervariables("script_name") 'Ŀ¼ļ
if action="div" then exit Function
GetUrl=Mid(Request.ServerVariables("script_name"),InstrRev(Replace(Request.ServerVariables("script_name"),"\","/"),"/")+1) 'ļ
if action="page" then exit Function
GetUrl=request.servervariables("QUERY_STRING") 'ַ
if action="action" then exit Function
GetUrl="http://" 'http:// 
GetUrl=GetUrl & request.servervariables("HTTP_HOST") '+
if action="http" then exit Function
GetUrl=GetUrl & request.servervariables("script_name") '+Ŀ¼ļ
if action="alldiv" then exit Function
if request.servervariables("QUERY_STRING")<>"" then GetUrl=GetUrl & "?" & request.servervariables("QUERY_STRING") '+ַ
End Function

Public q_name,q_db,q_port,q_server,q_mima,q_bangding
Sub reusers()
 q_name=rs("users")
 q_db=rs("users")
 q_port=rs("port")
 q_server=rs("server")
 q_mima=rs("mima")
 q_bangding=rs("bangding")
end Sub

%>

