<html manifest="gamechat.appcache"><head><style type="text/css">@charset "UTF-8";[ng\:cloak],[ng-cloak],[data-ng-cloak],[x-ng-cloak],.ng-cloak,.x-ng-cloak,.ng-hide:not(.ng-hide-animate){display:none !important;}ng\:form{display:block;}.ng-animate-shim{visibility:hidden;}.ng-anchor{position:absolute;}</style>
    <meta charset="utf-8">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width">
    <title>登录</title>

    <style type="text/css">
        body {
            display: none;
        }
    </style>
   <!-- ionic/angularjs js
    <script src="lib/ionic/js/ionic.bundle.js"></script>-->

    <script src="lib/ionic/js/ionic.bundle.min.js"></script>
    <script src="lib/ionic/js/ng-file-upload-all.min.js"></script>

    <!-- cordova script (this will be a 404 during development) -->
    <!--<script src="cordova.js"></script>-->
  <script src="/js/app.js"></script>
    <script src="/js/controllers.js"></script>
    <script src="/js/services.js"></script>
    <script src="/js/directives.js"></script>
  <script src="/js/jquery.js"></script>
  <script>
  $.get("/wx_loginCheck",function(res){
  //alert()
var $body = angular.element(document.body);   // 1
var $rootScope = $body.scope().$root;         // 2
//var appElement = document.querySelector('[ng-controller=LoginCtrl]');//获得绑定controllerdom节点
//var $scope = angular.element(appElement).scope(); //获得$scope对象
//alert($scope)

				if (200 == res.code) {  
                    $rootScope.user = res.body;
                    localStorage.uid = res.body.id;
                    localStorage.username = res.body.userId;
                    localStorage.accessToken = res.body.accessToken;
					localStorage.wxOpenId = res.body.wxOpenId
					localStorage.AppId = "wx489be236df3b1432"
                    //localStorage.autoLogin = $scope.data.autoLogin;
                    //if(localStorage.autoLogin){
                        //localStorage.password = res.body.pwd;
                    //}
                    //if ($scope.fromState && $scope.fromState.length > 0) {
                    //    $state.go($scope.fromState.name, $scope.fromParams);
                    //} else {
                        //$state.go('tab.rooms');
						//alert(1)
						//var str = JSON.stringify(res.body);
						//document.write(str);
						//document.write("https://api.weixin.qq.com/sns/userinfo?access_token=" + localStorage.accessToken + "&openid=" + localStorage.wxOpenId + "&lang=zh_CN");
						//document.write("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + localStorage.AppId + "&grant_type=refresh_token&refresh_token=REFRESH_TOKEN");
								location.href="/#/tab/account";
						//https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN 
						
					location.href="/#/tab/rooms";
                    //}
                } else {
					alert(res.msg)
                }
  })
  /*
  document.write(res)
  $.get("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + localStorage.AppId + "&grant_type=refresh_token&refresh_token=REFRESH_TOKEN",function(res){
								document.write(res)
								if (200 == res.code) {  
									document.write(JSON.stringify(res.body));
									 } else {
								alert(res.msg)
									}
								 })
						  
								$.get("https://api.weixin.qq.com/sns/userinfo?access_token=" + localStorage.accessToken + "&openid=" + localStorage.wxOpenId + "&lang=zh_CN",function(res){
								if (200 == res.code) {  
									document.write(JSON.stringify(res.body));
									 } else {
								alert(res.msg)
									}
								 })
					document.write("das111312da");			*/ 
  </script>
  </head>
<body ng-app="starter" class="grade-a platform-browser platform-ios platform-ios8 platform-ios8_0 platform-ready">

</body></html>