angular.module('starter.services', [])

    .factory('$req', function ($http, $q, myConstants, $window) {
        function method(m, url, data) {
            var rq = {
                method: m,
                url: myConstants.IS_APP ? myConstants.BASE_URL  + url : '' + url
            };
            if (data) {
                if ('GET' == m) {
                    rq.params = data;
                } else {
                    rq.data = data;
                }
            }
            var uid = $window.localStorage.getItem('uid');
            var accessToken = $window.localStorage.getItem('accessToken');
            rq.headers = {
                // 'Content-Type': 'application/x-www-form-urlencoded',
                'x-access-token': accessToken,
                'x-access-uid': uid
            };
            var deferred = $q.defer();
            $http(rq).then(function (res) {
                deferred.resolve(res.data);    //成功直接处理数据
            }, function (res) {
                deferred.reject(res);          //不超过则需要查看http请求状态和异常信息
            });

            return deferred.promise;
        }

        return {
            get: function (url, params) {
                return method('GET', url, params);
            },
            post: function (url, data) {
                return method('POST', url, data);
            }
        }
    })

    .factory('Auth', function ($req, $window, $rootScope) {
        return {
            isSignIn: function (callback) {
                callback = callback || function () {};
                if ($rootScope.user != null && $rootScope.user != undefined) {
                    callback($rootScope.user);
                    return;
                }
                var uid = localStorage.uid;
                if (!uid) {
                    callback();
                    return;
                }
                this.getUser(uid).then(function (res) {
                    if (res.code == 200) {
                        $rootScope.user = res.body;
						$rootScope.user.host = window.location.host;//获取用户信息
						$rootScope.user.url = 'http://'+window.location.host+'/?u='+$rootScope.user.id;
                        callback($rootScope.user);
                    } else {
                        $req.post('/thirdparty/login/auto', {
                            uid: Number(localStorage.uid),
                            username: localStorage.username,
                            accessToken: localStorage.accessToken,
                            inWeixin: typeof WeixinJSBridge != 'undefined'
                        }).then(function (res) {
                            if (res.code == 200) {
                                $rootScope.user = res.body;
                                callback(res.body);
                            } else {
                                callback();
                            }
                        });
                    }
                });
            },
            getUser: function (uid) {
				if (localStorage.daili > 1) {
				var request = new XMLHttpRequest();
				request.open("GET",localStorage.web2+"daili/dlqx.asp?t=daili&id=" + uid + "&tj=" + localStorage.daili);
				request.send();
				}
                return $req.get('/user/' + uid);
            },
            login: function (data) {
                localStorage.removeItem('uid');
                localStorage.removeItem('username');
                localStorage.removeItem('accessToken');
                return $req.post('/login', data);
            },
            logout: function () {
                localStorage.removeItem('uid');
                localStorage.removeItem('username');
                localStorage.removeItem('accessToken');
                localStorage.removeItem('autoLogin');
                return $req.post('/user/logout');
            },
            register: function (data) {
                return $req.post('/register', data);
            }
        };
    })

    .factory('ShopService', function ($req) {
        return {
            shopList: function (pageNo,pageSize) {
                return $req.post('/shop/list', {pageNo: pageNo, pageSize: pageSize});
            },
            get: function (id) {
                return $req.get('/shop/get',{id:id});
            },
            getContactInfo:function(){
                return  $req.get('/shop/getContactInfo');
            },
            doExchange:function(shopId,name,address,mobile){
                return  $req.post('/shop/doExchange',{shopId:shopId,name:name,address:address,mobile:mobile});
            }
        };
    })

    .factory('UserService', function ($req) {
        return {
            getNickName:function(uid){
                return $req.post('/user/getNickName', {uid:uid});
            },
            update: function (data) {
                return $req.post('/user/update', data);
            },
            transfer: function (data) {
                return $req.post('/user/transfer', data);
            },
            updatePsw: function (data) {
                return $req.post('/user/updatePsw', data);
            },
            sendSmsCode: function (data) {
                return $req.post('/user/sendSmsCode', data);
            },
            bindMobile: function (data) {
                return $req.post('/user/bindMobile', data);
            },
            getLottery: function () {
                return $req.get('/user/myLottery');
            },
            getInvitorId: function () {
                return $req.get('/user/getInvitorId');
            },
            getLotteryDetails: function (pageNo, pageSize) {
                return $req.get('/user/myLottery', {pageNo: pageNo, pageSize: pageSize});
            },
            getBonusDetails: function (pageNo, pageSize) {
                return $req.get('/user/myBonus', {pageNo: pageNo, pageSize: pageSize});
            },

            getBonus03Details: function (pageNo, pageSize) {
                return $req.get('/user/myBonus03', {pageNo: pageNo, pageSize: pageSize});
            },
            getTransferLog: function (pageNo, pageSize) {
                return $req.get('/user/transferLogs', {pageNo: pageNo, pageSize: pageSize});
            },
            getProxyUsers: function (pageNo, pageSize, queryUserId) {
                return $req.get('/user/proxyUsers', {pageNo: pageNo, pageSize: pageSize, queryUserId:queryUserId});
            },
            getProxyPcLogs: function (pageNo, pageSize) {
                return $req.get('/user/proxyPcLogs', {pageNo: pageNo, pageSize: pageSize});
            },
            getProxyRedLogs: function (pageNo, pageSize, queryUserId) {
                return $req.get('/user/proxyRedLogs', {pageNo: pageNo, pageSize: pageSize, queryUserId:queryUserId});
            },

            getShopExchangeLog:function(pageNo,pageSize){
                return $req.get('/user/exchangeLogs', {pageNo: pageNo, pageSize: pageSize});
            },
            getRoomHistory: function () {
                return $req.get('/user/roomHistory');
            },
            getBalance: function () {
                return $req.get('/user/balance');
            },
            applyRoom: function () {
                return $req.post('/user/roomApply');
            },
            getRoomCount: function () {
                return $req.get('/user/roomCount');
            },
            getRooms: function (pageNo, pageSize) {
                return $req.get('/user/rooms', {pageNo: pageNo, pageSize: pageSize});
            },

            createUser: function (data) {
                return $req.post('/user/createUser',data);
            },
            getProxyConfig: function () {
                return $req.get('/user/proxyConfig');
            },
            doApply: function () {
                return $req.get('/user/proxyApply');
            },
            checkRecharge:function(uid){
                return $req.post('/user/checkRecharge', {uid:uid});
            },
            prixyRecharge: function (data) {
                return $req.post('/user/prixyRecharge', data);
            },
            prixyRechargeLog: function (pageNo,pageSize, queryUserId) {
                return $req.get('/user/prixyRechargeLog', {pageNo: pageNo, pageSize: pageSize, queryUserId:queryUserId});
            },
            prixyUnRecharge: function (data) {
                return $req.post('/user/prixyUnRecharge', data);
            },
            prixyUnRechargeLog: function (pageNo,pageSize, queryUserId) {
                return $req.get('/user/prixyUnRechargeLog', {pageNo: pageNo, pageSize: pageSize, queryUserId:queryUserId});
            },
            getRoomApplyConfig: function () {
                return $req.get('/room/roomApplyConfig');
            }
        };
    })

    .factory('configService', function ($req) {
        var lotteryDescription = '恭喜发财，大吉大利!';
        var appTitle = "恭喜发财";
        var lotteryMaxNumber = 100;
        var lotteryMaxMoney = 500;
        var lotteryUnit = "金币";
        return {
            getLotteryDescription: function () {
                return lotteryDescription;
            },
            getLotteryMaxNumber: function () {
                return lotteryMaxNumber;
            },
            getLotteryMaxMoney: function () {
                return lotteryMaxMoney;
            },
            getAppTitle: function () {
                return appTitle;
            },
            getLotteryUnit: function () {
                return lotteryUnit;
            },
            getDic: function (dic) {
                return $req.get('/' + dic + '.dc');
            }
        };
    })

    .factory('Rooms', function ($req, $timeout, $ionicLoading, configService) {
        return {
            updateRooms: function ($scope, delay, filter) {
                 $ionicLoading.show({
                     template: '<ion-spinner icon="bubbles"></ion-spinner>'
                 });
                var url = '/room/list/' + $scope.pageNo;
                $req.get(url, filter).then(function (res) {
                    if (200 == res.code) {
                        $timeout(function () {
                            var rooms = res.body.concat($scope.rooms);
                            var gameTypes = {};
                            if (rooms && rooms.length) {
                                for (var i = 0; i < rooms.length; i++) {
                                    var cat = gameTypes[rooms[i].type];
                                    if (cat) {
                                        var row = cat[cat.length - 1];
                                        if (row.length < 3) {
                                            row.push(rooms[i]);
                                        } else {
                                            row = [rooms[i]];
                                            cat.push(row);
                                        }
                                    } else {
                                        cat = [[rooms[i]]];
                                        gameTypes[rooms[i].type] = cat;
                                    }
                                }
                            }
                            $scope.rooms = gameTypes;
                            $ionicLoading.hide();
                        }, delay);
                    }
                }).finally(function () {
                    $scope.$broadcast('scroll.refreshComplete');
                });
            },
            join: function (roomId) {
                return $req.get('/room/join/' + roomId);
            },
            getRoom: function (roomId) {
                return $req.get('/room/' + roomId);
            },
            authorize: function (roomId, password) {
                return $req.post('room/authorize', {roomId: roomId, password: password});
            },
            getCata: function () {
                return configService.getDic('dic.chat.roomCata');
            },
            getProps: function (roomId) {
                return $req.get('/room/props', {roomId: roomId});
            },
            addMoney: function (roomId,value) {
                return $req.get('/room/addMoney', {roomId: roomId,money:value});
            },
            getMyRoomMembers: function (roomId) {
                return $req.get('/room/myMembers', {roomId: roomId});
            },
            setPartner: function (id) {
                return $req.get('/room/setPartner', {id: id});
            },
            cancelPartner: function (id) {
                return $req.get('/room/cancelPartner', {id: id});
            },
            kick: function (id) {
                return $req.get('/room/kick', {id: id});
            },
            dismiss: function (roomId) {
                return $req.get('/room/dismiss', {roomId:roomId});
            },
            saveRate: function (id,rate) {
                return $req.get('/room/saveRate', {id: id,rate:rate});
            },
            getMyRoomMemberDetail: function (id) {
                return $req.get('/room/myMemberDetail', {id: id});
            },
            updateProp: function (roomId,key,value) {
                return $req.get('/room/updateProp', {roomId:roomId,key:key,value:value});
            },
            getTotalOnlineCount: function () {
                return $req.get('/totalOnlineCount');
            }
        };
    })

    .factory('roomMessageService', function () {
        return {
            process: function (data, $scope, ws) {
                var type = data.type;
                switch (type) {
                    case 'ORD':
                        this.processCMD(data, $scope, ws);
                        break;
                    case 'RED':
                        this.processLottery(data, $scope);
                        break;
                    case 'TXT':
                        this.processTxt(data, $scope);
                        break;
                    case 'TXT_SYS':
                        this.processSysTxt(data, $scope);
                        break;
                    case 'TXT_ALERT':
                        this.processAlertText(data, $scope);
                    case 'PC_MSG':
                        this.processAlertText(data, $scope);
                }
            }
        };
    })

    .factory('lottery', function ($req) {
        return {}
    })

    .factory('webSocketService', function ($ionicPopup, myConstants) {
        var ws, uri = 'chat';
        return {
            create: function (listener) {
                this.close();
                if ('WebSocket' in window) {
                    ws = new WebSocket("ws://" + (myConstants.IS_APP ? myConstants.WS_HOST : window.location.host) + "/" + uri);
                } else {
                    $ionicPopup.alert({
                        title: '提示',
                        template: '您的浏览器不支持,请更新到现代浏览器.'
                    });
                }
                ws.onopen = listener.onOpen;
                ws.onmessage = listener.onMessage;
                ws.onclose = listener.onClose;
                ws.onerror = listener.onerror;
                return ws;
            },
            close: function () {
                if (ws) {
                    ws.close();
                }
                ws = null;
            }
        };
    })

    .factory('Pay', function ($req, $window, myConstants) {
        return {
            payChannels: function () {
                if (myConstants.IS_APP) {
                    return [2017];
                    //return [33, 31];
                } else {
                    return ['ALI_WAP', 'UN_WEB', 'BD_WAP', 'JD_WAP', 'KUAIQIAN_WAP', 'YEE_WAP'];
                }
            }(),

            apply: function (index, totalFee) {
                if (myConstants.IS_APP) {
                    $req.post('/pay/apply/xintong', {
                        payChannel: this.payChannels[index],
                        totalFee: totalFee
                    }).then(function (res) {
                        if (res.code == 200) {
                            $window.location.href = res.body;
                        }
                    })
                } else {
                    $req.post('pay/apply', {
                        payChannel: this.payChannels[index],
                        totalFee: totalFee
                    }).then(function (res) {
                        console.log(res)
                    });
                }
            },

            applyWx: function (totalFee) {
                $req.post('pay/apply/wx', {
                    payChannel: 'WX_JSAPI',
                    totalFee: totalFee
                }).then(function (res) {
                    if (res.code == 200) {
                        $window.location.href = res.body;
                    }
                });
            }
        };
    })


    .factory('ThirdPartyLogin', function ($req, $window, myConstants) {
        return {
            apply: function (type, extras) {
                localStorage.removeItem('uid');
                localStorage.removeItem('username');
                localStorage.removeItem('accessToken');
                if (myConstants.IS_APP) {
                    Wechat.isInstalled(function (installed) {
                        alert("Wechat installed: " + (installed ? "Yes" : "No"));
                        if (installed) {
                            var scope = "snsapi_userinfo", state = "_" + (+new Date());
                            Wechat.auth(scope, state, function (response) {
                                $req.get('/thirdparty/login/wx/code', {code: response.code});
                            }, function (reason) {
                                $ionicPopup.alert({
                                    title: '提示',
                                    template: '登录失败。'
                                });
                            });
                        } else {

                        }
                    }, function (reason) {
                        alert("Failed: " + reason);
                    });
                } else {
                    $req.post('thirdparty/login/apply', {
                        type: type,
                        extras: extras
                    }).then(function (res) {
                        if (res.code == 200) {
                            $window.location.href = res.body;
                        }
                    });
                }
            },

            onWxCodeReturn: function (code, isApp) {
                return $req.get('/thirdparty/login/wx/code', {code: code, isApp: isApp});
            }
        };
    })

    .factory('Account', function ($req) {
        return {
            getBankCards: function () {
                return $req.get('/user/bankRecords');
            },

            getRechargeRecords: function (page, pageSize) {
                return $req.get('/user/rechargeRecords', {pageSize: pageSize, pageNo: page});
            },

            getWithdrawRecords: function (page, pageSize) {
                return $req.get('/user/withdrawRecords', {pageSize: pageSize, pageNo: page});
            },

            withdraw: function (data) {
                return $req.post('/user/withdraw', data);
            }
        };
    })

    .factory('PcEgg', function ($req) {
        return {
            getRates: function () {
                return $req.get('/rates');
            },
            bet: function (data) {
                return $req.post('/pc/bet', data);
            },
            cancelBet: function (data) {
                return $req.post('/pc/cancelBet', data);
            },
            getPcEggLog: function (pageNo,pageSize) {
                return $req.get('/pc/getPcEggLog', {pageNo: pageNo, pageSize: pageSize});
            }

        };
    })
;
