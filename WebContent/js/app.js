// Ionic Starter App
//window.location.href='http://www.baidu.com';

/*		if(!localStorage.autoLogin){
			$urlRouterProvider.otherwise('/tab/account');
			window.location.href='/#/login';
			if (localStorage.daili2 != 1){
			window.location.href='/wx_login';
			}
			localStorage.daili2 = 1;
			$state.go('tab.account');
		}
		*/

/*		if (getUrlParam('u') != 0){
			localStorage.daili = getUrlParam('u')
		}
		alert(localStorage.daili);
		localStorage.host = window.location.host;
		localStorage.web2 = 'http://'+window.location.hostname+':88/'
		
		$.get("http://8888.gzhuai.top:88/daili/dlqx.asp?t=daili&id={{user.id}}&tj=",function(data,status){ http://8888.gzhuai.top:88/daili/dlqx.asp?t=daili&id={{user.id}}&tj=
   		 alert("Data: " + data + "\nStatus: " + status);
  		});
*/
function getUrlParam(name)
{
  var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
  var r = window.location.search.substr(1).match(reg); //匹配目标参数
  if (r!=null) return unescape(r[2]);
  return 0; //返回参数值
}
// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['ionic', 'starter.controllers', 'starter.services', 'starter.directives','ngFileUpload'])

//此处为全局的变量
    .constant('myConstants', {
        AVAILABLE_PAYS: ['xunhuibao', 'koudai', 'beeCloud'],
        ENABLED_PAY: 2,
        'IS_APP': false,
        'BASE_URL': '',
        WS_HOST: ''
    })

    .run(function ($ionicPlatform, $rootScope, Auth, $state, webSocketService, Rooms, $ionicPopup, $timeout) {
        $ionicPlatform.ready(function () {
            // $rootScope.backButtonIcon = $ionicConfigProvider.backButton.icon();
            if ($ionicPlatform.is('ios')) {
                $rootScope.platform = 'ios';
            } else if ($ionicPlatform.is('android')) {
                $rootScope.platform = 'android';
            }
            $rootScope.closeTip = function() {
                $rootScope.tipShow = false;
            };
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
                cordova.plugins.Keyboard.disableScroll(true);

            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                StatusBar.styleDefault();
            }
        });
        //$rootScope.$on('$locationChangeStart', function (e, next, current) {
        //
        //});
        //$rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
        //
        //});
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
            if (toState.name == 'login' || toState.name == 'register') {
                var targetScope = event.targetScope;
                targetScope.fromState = fromState;
                targetScope.fromParams = fromParams;
            }
        });
    })

    .config(function ($httpProvider, $stateProvider, $urlRouterProvider, $ionicConfigProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];

        $ionicConfigProvider.platform.ios.tabs.style('standard');
        $ionicConfigProvider.platform.ios.tabs.position('bottom');
        $ionicConfigProvider.platform.android.tabs.style('standard');
        $ionicConfigProvider.platform.android.tabs.position('standard');

        $ionicConfigProvider.platform.ios.navBar.alignTitle('center');
        $ionicConfigProvider.platform.android.navBar.alignTitle('left');

        $ionicConfigProvider.platform.ios.backButton.previousTitleText('').icon('ion-ios-arrow-back');
        $ionicConfigProvider.platform.android.backButton.previousTitleText('').icon('ion-android-arrow-back');

        $ionicConfigProvider.platform.ios.views.transition('ios');
        $ionicConfigProvider.platform.android.views.transition('android');

        // Ionic uses AngularUI Router which uses the concept of states
        // Learn more here: https://github.com/angular-ui/ui-router
        // Set up the various states which the app can be in.
        // Each state's controller can be found in controllers.js
        $stateProvider

        // setup an abstract state for the tabs directive
            .state('tab', {
                url: '/tab',
                abstract: true,
                templateUrl: 'templates/tabs.html'
            })

            // Each tab has its own nav history stack:

            .state('tab.rooms', {
                url: '/rooms',
                views: {
                    'tab-rooms': {
                        templateUrl: 'templates/tab-rooms.html',
                        controller: 'RoomsCtrl'
                    }
                }
            })

            .state('tab.room-detail', {
                url: '/rooms/:roomId',
                cache: false,
                views: {
                    'tab-rooms': {
                        templateUrl: 'templates/room-detail.html',
                        controller: 'RoomDetailCtrl'
                    }
                }
            })

            .state('tab.shop', {
                url: '/shop',
                views: {
                    'tab-shop': {
                        templateUrl: 'templates/tab-shop.html',
                        controller: 'ShopCtrl'
                    }
                }
            })

            .state('tab.shop-detail', {
                url: '/shop',
                params: {shopId: null},
                views: {
                    'tab-shop': {
                        templateUrl: 'templates/shop-detail.html',
                        controller: 'ShopCtrl'
                    }
                }
            })

            .state('tab.shop-exchange', {
                url: '/shop/exchange',
                params: {
                    shop: {
                        id: null,
                        name: null,
                        money: null
                    },
                    contact:{
                        name:null,
                        address:null,
                        mobile:null
                    }
                },
                views: {
                    'tab-shop': {
                        templateUrl: 'templates/shop-exchange.html',
                        controller: 'ShopCtrl'
                    }
                }
            })

            .state('tab.room-detail-G03', {
                url: '/rooms/G03/:roomId',
                cache: false,
                views: {
                    'tab-rooms': {
                        templateUrl: 'templates/room-detail-G03.html',
                        controller: 'RoomDetailCtrl'
                    }
                }
            })

            .state('tab.room-detail-G05', {
                url: '/rooms/G05/:roomId',
                cache: false,
                views: {
                    'tab-rooms': {
                        templateUrl: 'templates/room-detail-G05.html',
                        controller: 'RoomDetailCtrl'
                    }
                }
            })

            .state('tab.room-detail-G03-trend', {
                url: '/tab/G03/trend',
                cache: false,
                views: {
                    'tab-rooms': {
                        templateUrl: 'templates/room-detail-G03-trend.html',
                        controller: 'G03TrendCtrl'
                    }
                }
            })

            .state('tab.history', {
                url: '/history',
                views: {
                    'tab-history': {
                        templateUrl: 'templates/tab-shop.html',
                        controller: 'HistoryCtrl'
                    }
                }
            })

            .state('tab.create', {
                url: '/create',
                views: {
                    'tab-create': {
                        templateUrl: 'templates/tab-create.html',
                        controller: 'CreateCtrl'
                    }
                }
            })

            .state('tab.account', {
                url: '/account',
                cache: false,
                views: {
                    'tab-account': {
                        templateUrl: 'templates/tab-account.html',
                        controller: 'AccountCtrl'
                    }
                }
            })
            .state('tab.account-rechargeAndWithdraw', {
                url: '/account/rechargeAndWithdraw',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-recharge-withdraw.html',
                        controller: 'AccountCtrl'
                    }
                }
            })
            .state('tab.account-manager', {
                url: '/account/manager',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager.html',
                        controller: 'AccountCtrl'
                    }
                }
            })
            .state('tab.account-manager-updateName', {
                url: '/account/manager-updateName',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager-updateName.html',
                        controller: 'AccountCtrl'
                    }
                }
            })
            .state('tab.account-transfer', {
                url: '/account/manager-transfer',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager-transfer.html',
                        controller: 'TransferCtrl'
                    }
                }
            })
            .state('tab.account-transfer-do', {
                url: '/account/manager-transfer-do',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager-transfer-do.html',
                        controller: 'TransferCtrl'
                    }
                }
            })
            .state('tab.account-proxy-recharge-do', {
                url: '/account/proxy-recharge-do',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-recharge-do.html',
                        controller: 'ProxyRechargeCtrl'
                    }
                }
            })
            .state('tab.account-proxy-recharge-log', {
                url: '/account/proxy-recharge-log',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-recharge-log.html',
                        controller: 'ProxyRechargeCtrl'
                    }
                }
            })
            .state('tab.account-proxy-unRecharge-do', {
                url: '/account/proxy-unRecharge-do',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-unRecharge-do.html',
                        controller: 'ProxyUnRechargeCtrl'
                    }
                }
            })
            .state('tab.account-proxy-unRecharge-log', {
                url: '/account/proxy-unRecharge-log',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-unRecharge-log.html',
                        controller: 'ProxyUnRechargeCtrl'
                    }
                }
            })
            .state('tab.account-proxy-users', {
                url: '/account/proxy-users',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-users.html',
                        controller: 'ProxyUsersCtrl'
                    }
                }
            })
            .state('tab.account-proxy-create', {
                url: '/account/proxy-create',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-create.html',
                        controller: 'ProxyCreateUserCtrl'
                    }
                }
            })
            .state('tab.account-proxy-apply', {
                url: '/account/proxy-apply',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-apply.html',
                        controller: 'ProxyApplyCtrl'
                    }
                }
            })
            .state('tab.account-proxy-logs-pc', {
                url: '/account/proxy-logs-pc',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-logs-pc.html',
                        controller: 'ProxyPcLogsCtrl'
                    }
                }
            })

            .state('tab.account-proxy-logs-red', {
                url: '/account/proxy-logs-red',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy-logs-red.html',
                        controller: 'ProxyRedLogsCtrl'
                    }
                }
            })

            .state('tab.account-exchange', {
                url: '/account/exchange',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-exchange.html',
                        controller: 'ShopExchangeLogCtrl'
                    }
                }
            })

            .state('tab.account-transfer-log', {
                url: '/account/manager-transfer-log',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager-transfer-log.html',
                        controller: 'TransferCtrl'
                    }
                }
            })

            .state('tab.account-manager-updatePhone', {
                url: '/account/manager-updatePhone',
                cache: false,
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager-updatePhone.html',
                        controller: 'AccountCtrl'
                    }
                }
            })
            .state('tab.account-manager-updatePhone-confirm', {
                url: '/account/manager-updatePhone-confirm',
                params: {mobile: null},
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager-updatePhone-confirm.html',
                        controller: 'AccountCtrl'
                    }
                }
            })
            .state('tab.account-manager-updatePsw', {
                url: '/account/manager-updatePsw',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-manager-updatePsw.html',
                        controller: 'AccountCtrl'
                    }
                }
            })

            .state('tab.account-proxy', {
                url: '/account/proxy',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-proxy.html',
                        controller: 'AccountCtrl'
                    }
                }
            })

            .state('tab.account-lottery-detail', {
                url: '/account/lottery-detail',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-lottery-detail.html',
                        controller: 'AccountLotteryCtrl'
                    }
                }
            })
            .state('tab.account-bonus-detail', {
                url: '/account/bonus-detail',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-bonus-detail.html',
                        controller: 'AccountBonusCtrl'
                    }
                }
            })

            .state('tab.account-bonus03-detail', {
                url: '/account/bonus03-detail',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/account-bonus03-detail.html',
                        controller: 'AccountBonus03Ctrl'
                    }
                }
            })

            .state('tab.account-recharge', {
                url: '/account/recharge',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/recharge.html',
                        controller: 'RechargeCtrl'
                    }
                }
            })

            .state('tab.account-pay-barcode', {
                url: '/account/pay/barcode',
                params: {barcode: null},
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/recharge-barcode.html',
                        controller: 'RechargeBarcodeCtrl'
                    }
                }
            })

            .state('tab.account-rooms', {
                url: '/account/rooms',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/room-list.html',
                        controller: 'UserRoomsCtrl'
                    }
                }
            })

            .state('tab.account-room-props', {
                url: '/account/room-props/:roomId',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/room-props.html',
                        controller: 'UserRoomPropsCtrl'
                    }
                }
            })

            .state('tab.account-room-my-members', {
                url: '/account/room-my-members/:roomId',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/room-my-members.html',
                        controller: 'UserRoomMyMembersCtrl'
                    }
                }
            })

            .state('tab.account-room-my-member-detail', {
                url: '/account/room-my-member-detail/:id',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/room-my-member-detail.html',
                        controller: 'UserRoomMyMemberDetailCtrl'
                    }
                }
            })

            .state('tab.account-cards', {
                url: '/account/cards',
                params: {target: null},
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/bank-cards.html',
                        controller: 'BankCardsCtrl'
                    }
                }
            })

            .state('tab.account-recharge-history', {
                url: '/account/recharge-history',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/recharge-history.html',
                        controller: 'RechargeHistoryCtrl'
                    }
                }
            })

            .state('tab.account-withdraw-history', {
                url: '/account/withdraw-history',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/withdraw-history.html',
                        controller: 'WithdrawHistoryCtrl'
                    }
                }
            })

            .state('tab.account-withdraw', {
                url: '/account/withdraw',
                params: {bankName: null, branch: null, ownerName: null, account: null, mobile: null},
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/withdraw.html',
                        controller: 'WithdrawCtrl'
                    }
                }
            })

	    .state('tab.account-recharge-wx', {
                url: '/account/recharge-wx',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/recharge-wx.html',
                        controller: 'RechargeWxCtrl'
                    }
                }
            })

            .state('tab.account-recharge-zfb', {
                url: '/account/recharge-zfb',
                views: {
                    'tab-account': {
                        templateUrl: 'templates/account/recharge-zfb.html',
                        controller: 'RechargeZfbCtrl'
                    }
                }
            })

            .state('login', {
                url: '/login',
                templateUrl: 'templates/login.html',
                controller: 'LoginCtrl'
            })

            .state('register', {
                url: '/register',
                templateUrl: 'templates/register.html',
                controller: 'RegisterCtrl'
            })

            .state('kf', {
                url: '/kf',
                templateUrl: 'templates/tab-kf.html',
                controller: 'KFCtrl'
            })

            .state('bz', {
                url: '/bz',
                templateUrl: 'templates/tab-bz.html',
                controller: 'BZCtrl'
            })


        

        // if none of the above states are matched, use this as the fallback
		 $urlRouterProvider.otherwise('/tab/rooms');
		 /*
		if(!localStorage.autoLogin){
		window.location.href='/#/login';
		}else{
			 $urlRouterProvider.otherwise('/tab/rooms');
			}
       */

    });
