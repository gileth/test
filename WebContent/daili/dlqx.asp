<!--#include file="conn.asp" -->
<% 
function rw(xx)
Response.Write("|||"&xx&"|||")
end function
function rw2(xx)
Response.Write(xx)
end function
t=re("t")
if t  = "" then rw("t参数错误!")
call SetConn

'-----------------------------------------------------------------
'代理设置
'-----------------------------------------------------------------
if re("t") = "daili" then
userid	= trim(re("id"))
tj_id = trim(re("tj"))
if tj_id = "" then  rw("tj参数错误2!")

if tj_id = userid then
rw("不能设置自己为代理!")
end if
rs.open "select * from pub_user where id ='"&tj_id&"'",conn,1,3
	if rs.eof then
	rs.close
	rw("代理不存在!")
	else
	if rs("id") = rs("parent") then
	rs("parent") = 0
	rs.update
	end if
	end if
rs.close
rs.open "select * from pub_user where id ='"&userid&"'",conn,1,3
	if rs.eof then
	rs.close
	rw("用户名不存在!")
	else
	if isnull(rs("parent")) and rs("id") <> tj_id then
	rs("parent") = tj_id
	rs.update
	end if
	end if
rs.close

set rs = nothing
rw("设置代理成功!")
end if
'-----------------------------------------------------------------
'微信登录
'-----------------------------------------------------------------
if re("t") = "wxlogin" then
dim tid,jg
tid = trim(re("tid"))
jg = "{""code"":""0"",""msg"":""未登录""}"
if tid <> "" then
	rs.open "select id,accessToken from pub_user where pwd='" & tid & "' and headImg <> ''",conn,1,1
	if not rs.eof then
		jg = "{""code"":""200"",""uid"":""" & rs("id") & """,""accessToken"":""" & rs("accessToken") & """}"
	end if
	rs.close
end if
rw2(jg)
end if
%>

