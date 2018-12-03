<!--#include file="conn.asp" -->
<% 
function rw(xx)
Response.Write("|||"&xx&"|||")
end function
id = trim(re("id"))
url = trim(re("url"))
bg_img = trim(re("bgimg"))
qr_size = trim(re("qrsize"))
qr_top = trim(re("top"))
qr_left = trim(re("left"))
if id = "" then rw("参数错误!")
if url = "" then rw("参数错误!")
if qr_size = "" then qr_size = 45
if qr_top = "" then qr_top = 42
if qr_left = "" then qr_left = -1
 %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=0" name="viewport">
<style type="text/css">
<!--
.dsa {
	position: absolute;
	text-align:center;
	width:100%;
	vertical-align: middle;
	top: 30%;
}
-->
</style>
</head>
<body style="margin:0px;">
<div style="position: relative; height:1050px; width:100%; margin-left:0px; margin-top:0px;">
  <div style="position: absolute; text-align:center; width:100%;vertical-align: middle; margin-top:<%= qr_top %>%;margin-left:<%= qr_left %>%"> 
    <img width="<%= qr_size %>%" src="http://qr.topscan.com/api.php?&amp;bg=ffffff&amp;m=8&amp;fg=000000&amp;text=<%= url %>?u=<%= id %>&amp;logo=<%= url %>img/system.png" />
  </div>  
  <img width="100%" src="<%= bg_img %>"/>
</div>
</body>
</html>