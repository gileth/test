<!--#include file="conn.asp" -->
<!--#include file="MD5.asp" -->
<% 
function rw(xx)
Response.Write("|||"&xx&"|||")
end function
dim db
db = ""
sid	= re("sid")

if re("t") = "" then rw("参数错误!")
'if re("prot") = "" then  rw("参数错误2!")
if re("prot") <> "" then
call SetConn
	if 1>2 then
	rs.open "select * from dlq_list where port ='"&re("prot")&"' and serverid='"&sid&"'",conn,1,1
	if rs.eof then
	rw("参数错误3!")
	else
	db = rs("db")
	end if
	rs.close
	end if
rs.open "select * from dlq_server where id ='"&sid&"'",conn,1,1
if rs.eof then
rw("参数错误4!")
else
cgd = rs("cgd")
sqldz = rs("server")
end if
rs.close

end if
sqldz = "183.60.109.49"
db="mac"
 conn1 = "DRIVER={MySQL ODBC 3.51 Driver};SERVER="&sqldz&";DATABASE="&db&";User="&sqluser&";PASSWORD="&sqlpass&";Option=3;"
if db <> "" then
'--------------------------------------------------
if re("t") = "dlq_list" then
 call SetConn
'mac 传递过来的mac数据
'检测普通白名单
uname=re("u")
ip=re("ip")
mac=re("mac")
sid=re("sid")
tmp = ""& Vbcrlf
rs.open "select * from mac where name='" & uname & "' and dt ='888' and (" & mac & ")",conn,1,1
	if not rs.eof then tmp = tmp & "普通白名单=真" & Vbcrlf
rs.close
'检测终极黑名单
rs.open "select * from mac where name='66kj_2' and (" & mac & ")",conn,1,1
	if not rs.eof then tmp = tmp & "终极黑名单=真" & Vbcrlf
rs.close
'检测白名单
rs.open "select * from mac where name='bai' and (" & mac & " or mac='" & ip & "')",conn,1,1
	if not rs.eof then 
	tmp  = tmp & "超级白名单=真" & Vbcrlf
	conn.execute("delete from mac where name='bai' and dt='1' and (" & mac & " or mac='" & ip & "')")
	end if
rs.close
'检测清除名单
rs.open "select * from mac where name='clear' and (" & mac & " or mac='" & ip & "')",conn,1,1
	if not rs.eof then 
	tmp  = tmp & "清除名单=真" & Vbcrlf
	conn.execute("delete from mac where name='clear' and (" & mac & " or mac='" & ip & "')")
	end if
rs.close
'检测关机黑名单
rs.open "select * from mac where name='shutdown' and (" & mac & " or mac='" & ip & "')",conn,1,1
	if not rs.eof then 
	tmp  = tmp & "超级关机黑名单=真" & Vbcrlf
	conn.execute("delete from mac where name='shutdown' and dt ='1' and (" & mac & " or mac='" & ip & "')")
	end if
rs.close
'检测关机黑名单
rs.open "select * from mac where name='" & uname & "' and dt ='999' and (" & mac & ")",conn,1,1
	if not rs.eof then  tmp  = tmp & "关机黑名单=真" & Vbcrlf
rs.close

'检测普通黑名单
rs.open "select * from mac where (name='" & uname & "' or name ='root') and (" & mac & ")",conn,1,1
	if not rs.eof then  tmp  = tmp & "普通黑名单=真" & Vbcrlf
rs.close

'检测普通黑名单ip
rs.open "select * from mac where (name='" & uname & "' or name ='root') and mac='" & ip & "'",conn,1,1
	if not rs.eof then  tmp  = tmp & "普通黑名单IP=真" & Vbcrlf
rs.close

'检测非法捣乱(截图功能) 现在没用
rs.open "select * from mac where name='66kj' and (" & mac & " or mac='" & ip & "')",conn,1,1
	if not rs.eof then
	tmp  = tmp & "非法捣乱=真" & Vbcrlf
	conn.execute("delete from mac where name='66kj' and dt ='1' and (" & mac & " or mac='" & ip & "')")
	end if
rs.close
	'读参数信息
	rs.open "select * from mac where id < 100",conn,1,3
	while not rs.eof 
	tmp = tmp & rs("name") & "=" & rs("mac") & Vbcrlf
	rs.movenext
	wend
	rs.close

'获取游戏分组信息
rs.open "SELECT column_name,column_comment,DATA_TYPE,COLUMN_TYPE FROM INFORMATION_SCHEMA.Columns WHERE table_name='dlq_server'",conn,1,1
sl = rs.recordcount
while not rs.eof
	'读取字段数组
	if tmp_zd = "" then
		tmp_zd = rs("column_name")
	else
		tmp_zd = tmp_zd & "|" & rs("column_name")
	end if
	'读取字段数组 - 备注
	if tmp_zd_name = "" then
		tmp_zd_name = rs("column_comment")
	else
		tmp_zd_name = tmp_zd_name & "|" & rs("column_comment")
	end if
	
	'读取字段数组 - 类型
	if tmp_zd_type = "" then
		tmp_zd_type = rs("DATA_TYPE")
	else
		tmp_zd_type = tmp_zd_type & "|" & rs("DATA_TYPE")
	end if
	
	'读取字段数组 - 类型(长度)
	if tmp1 = "" then
		tmp1 = rs("column_comment") & "," & rs("column_name") & "," & rs("COLUMN_TYPE")
	else
		tmp1 = tmp1 & "|" & rs("column_comment") & "," & rs("column_name") & "," & rs("COLUMN_TYPE")
	end if
	rs.movenext
wend
arr_zd = tmp1
rs.close

'取字段数组
d_arr_zd = split(tmp_zd,"|")
'取字段标记名称数组
d_arr_zd_name = split(tmp_zd_name,"|")
'取字段类型
d_arr_zd_type = split(tmp_zd_type,"|")


dim list
tmp = tmp & Vbcrlf & "[游戏分组数据]" & Vbcrlf
rs.open "select * from dlq_server where id="& sid,conn,1,1
if not rs.eof then 
	list = 1
	for i = 0 to Ubound(d_arr_zd)
	tmp = tmp & d_arr_zd(i) & "=" & rs(d_arr_zd(i)) & Vbcrlf
	next
else
    ' “没有列表！”
end if
rs.close


' 载入游戏列表…
if list = 1 then
	rs.open "select count(*) as aa from dlq_list where serverid='" & sid & "' order by id desc",conn,1,1
	tmp = tmp & "分区数量=" & rs("aa")  & Vbcrlf
	rs.close
	rs.open "select * from dlq_list where serverid='" & sid & "' order by id desc",conn,1,1
	
	jc = 0
	while not rs.eof
		jc = jc + 1
		tmp = tmp  & "[dlq" & jc & "]" & Vbcrlf
		tmp = tmp  & "name=" & rs("name") & Vbcrlf
		tmp = tmp  & "port=" & rs("port") & Vbcrlf
		tmp = tmp  & "dir=" & rs("dir") & Vbcrlf
		rs.movenext
	wend
	rs.close
end if
rw(tmp)

end if
'--------------------------------------------------
if re("t") = "Regedit" then
	if isnull(re("mac")) then
	mac="admins"
	else
	mac=re("mac")
	end if
	if isnull(re("city")) then
	city="无"
	else
	city=re("city")
	end if
	if isnull(re("lr")) then
	lr="无"
	else
	lr=re("lr")
	end if
'http://e.520-money.com/reg/dlqx.asp?prot=11801&t=Regedit&hwd=1&pw2=2&name=1
 if re("hwd") = "" or re("pw2") ="" or re("name") = "" then
 rw("信息填写不完整，请重新输入后再注册!")
 end if
 if instr(re("name"),"_") > 0 then
 rw("帐号不允许含下划线 _ !")
 end if

 call SetConn
set rs = Server.CreateObject("ADODB.RecordSet")
rs.open "select * from "&db&".h_hqrjacc where name='"&re("name")&"'",conn,1,3
if not rs.eof then
rw("该帐号已经有人使用，请使用其他帐号名再注册!")
end if
rs.addnew
rs("name")=re("name")
rs("hwd")=md5(re("hwd"),32)
rs("pw2")=md5(re("pw2"),32)
rs("pv")=1			'注册送权限
if cgd > 9999 then
cgd=9999
end if
rs("gd")=cgd		'注册送钻石
rs("name_tj")=mac
rs("city")=city 'IP地址
rs("lr")=lr '地理位置
rs.update
rs.close
set rs = nothing
rw("注册成功，祝您游戏愉快!")
end if
'--------------------------------------------------
if re("t") = "Password" then
 if re("hwd") = "" or re("name") = "" or re("pw2") = "" then
 rw("信息填写不完整，请重新输入后再提交！")
 end if
 pw2=md5(re("pw2"),32)
 hwd=md5(re("hwd"),32)
 call SetConn
set rs = Server.CreateObject("ADODB.RecordSet")
rs.open "select * from "&db&".h_hqrjacc where name='"& trim(re("name"))&"' and pw2='"&trim(pw2)&"'",conn,1,3
 if rs.eof then
 rw("帐号或密码输入错误，请检查后再提交！")
 rs.close
 set rs = nothing
 end if
rs("hwd")=hwd
rs.update
rs.close
set rs = nothing
rw("密码修改成功！")
end if

'--------------------------------------------------
if re("t") = "bangding" then '手机绑定
 if re("name") = "" or re("tel") = "" or re("code") = "" then
 rw("信息填写不完整，请重新输入后再提交！")
 end if
 u_name = re("name")
 u_tel = re("tel")
 u_code = re("code")
 u_tjr = re("tjr")
 call SetConn
set rs = Server.CreateObject("ADODB.RecordSet")
'-----------判断手机是否绑定过
rs.open "select * from mac.code where db='"& trim(re("prot"))&"' and tel='"&trim(u_tel)&"' zt='1'",conn,1,3
if rs.recordcount > 0 then
rw("该手机已经绑定过游戏帐号，无法重复绑定！")
rs.close
set rs = nothing
end if
rs.close
'-----------判断推荐人是否存在
if u_tjr <> "860001728" then
	rs.open "select * from "&db&".h_hqrjacc where name='"& trim(re("name"))&"'",conn,1,1
    if rs.eof then
 	 rw("推荐人不存在或推荐人尚未绑定手机，若无其他推荐人请不要写推荐人！")
	 rs.close
	 set rs = nothing
 	end if
	rs.close
end if
'-----------判断帐号是否绑定过手机
rs.open "select * from "&db&".h_hqrjacc where name='"& trim(re("name"))&"'",conn,1,1
if rs.eof then
 rw("游戏帐号不存在，请检查后再提交！")
 rs.close
 set rs = nothing
end if
if rs("jifen_tj1") <> "0" then
 rw("该帐号已经绑定过手机无需重复绑定！")
 rs.close
 set rs = nothing
end if
rs.close
'-----------判断验证码是否正确
rs.open "select * from mac.code where db='"& trim(re("prot"))&"' and tel='"&trim(u_tel)&"' code='"&trim(u_code)&"'",conn,1,3
 if rs.eof then
 rw("验证码错误，请检查后再提交！")
 rs.close
 set rs = nothing
 end if
rs("zt") = 1
rs.update
rs.close
'-----------执行帐号绑定
rs.open "select * from "&db&".h_hqrjacc where name='"& trim(re("name"))&"' and jifen_tj1='0'",conn,1,3
 if rs.eof then
 rw("游戏帐号不存在或已经绑定过，请检查后再提交！")
 rs.close
 set rs = nothing
 end if
rs("jifen_tj1")=u_tel
rs("money4")=999
rs.update
rs.close

'-----------执行推荐人奖励
rs.open "select * from "&db&".h_hqrjacc where name='"& trim(u_tjr)&"' and jifen_tj1='0'",conn,1,3
 if rs.eof then
 rs.close
 set rs = nothing
 end if
 rs("money4")=rs("money4")+1
 rs.update
 rs.close
rw("手机绑定成功，请重新上游戏领取绑定奖励！")
end if
'--------------------------------------------------
if re("t") = "VIP" then
'-----------------------------
dim u_huaqiacid,bpv,bpv2
dim apv
dim apv2
set rs = Server.CreateObject("ADODB.RecordSet")
rs.open "select * from site",connac,1,1
bpv		= rs("pv")
bpv2	= rs("pv2")
rs.close
if isnull(bpv) or bpv ="" then bpv = "1"
if bpv <> "1" then
apv=split(bpv,"|")
end if
if isnull(bpv2) or bpv2 ="" then bpv2 = "1"
if bpv2 <> "1" then
apv2=split(bpv2,"|")
end if

if bpv = "1" and bpv2 = "1" then
 rw("暂不开放该功能!")
 return()
 end if
'-----------------------------
 if re("hwd") = "" or re("name") = "" then
 rw("信息填写不完整，请重新输入后再提交！")
 end if
 hwd=md5(re("hwd"),32)
 call SetConn
rs.open "select * from "&db&".h_hqrjacc where name='"& trim(re("name"))&"' and hwd='"&trim(hwd)&"'",conn,1,3
 if rs.eof then
 rw("帐号或密码输入错误，请检查后再提交！")
 rs.close
 set rs = nothing
 end if
'------------------------
 dim ccgd,ccjf,ccpv,ddpv
 dim ss
 ccgd = rs("gd") + rs("ut")
 ccjf = rs("money") + rs("money2")
   '判断是否钻石提升权限
 ccpv = 1
  if bpv <> "1" then
  for i = 0 to Ubound(apv)
  	ss = split(apv(i),",")
  	if ccgd >= Clng(ss(1)) then ccpv = Clng(ss(0))
	'ts("ccpv1="&ccpv)
  next
  end if
  'ts("ccpv="&ccpv)
  '判断是否积分提升权限
 ddpv = 1 
  if bpv2 <> "1" then
  for i = 0 to Ubound(apv2)
  	ss = split(apv2(i),",")
  	if ccjf >= Clng(ss(1)) then ddpv = Clng(ss(0))
  next
  end if
 if ddpv > ccpv then ccpv = ddpv
 if ccpv < 8 and ccpv > 0 then
 rs("pv")=ccpv
 rs.update
 rs.close
 set rs = nothing
 rw("成功提升为VIP，如果游戏在线中，重新上线就会生效!")
 else
 rw("参数错误,提权失败!")
 end if
end if
'--------------------------------------------------
if re("t") = "Rename" then
dim huaqiacid
 if re("hwd") = "" or re("name") = "" or re("name2") = "" then
 rw("信息填写不完整，请重新输入后再提交！")
 end if
 hwd=md5(re("hwd"),32)
 call SetConn
set rs = Server.CreateObject("ADODB.RecordSet")
rs.open "select * from "&db&".h_hqrjacc where name='"& trim(re("name"))&"' and hwd='"&trim(hwd)&"'",conn,1,1
 if rs.eof then
 rw("帐号或密码输入错误，请检查后再提交！")
 rs.close
 set rs = nothing
 end if
huaqiacid=rs("huaqiacid")
rs.close
rs.open "select * from "&db&".h_hqrj where huaqiacid='"&trim(huaqiacid)&"' and name='"&trim(re("name2"))&"'",conn,1,3
 if rs.eof then
 rw("角色不存在，请检查后再提交！")
 rs.close
 set rs = nothing
 end if
rs("changename")=1
rs("freeze")=1
rs.update
rs.close
set rs = nothing
rw("改名成功，上线后激活角色，然后点击进入游戏，如果没有提醒输入新名字请再来此提交，正常1-2次就会提示输入新角色名了！")
end if
'--------------------------------------------------
end if
%>

