angular.module('starter.directives', [])
    .directive('hideTabs', function ($rootScope) {
        return {
            restrict: 'A',
            link: function ($scope, element, attributes) {
                $scope.$on('$ionicView.beforeEnter', function () {
                    $scope.$watch(attributes.hideTabs, function (value) {
                        $rootScope.hideTabs = value;
                    });
                });

                $scope.$on('$ionicView.beforeLeave', function () {
                    $rootScope.hideTabs = false;
                });
            }
        };
    })

    .directive('needLogin', function ($rootScope, $state, Auth) {
        return {
            restrict: 'A',
            link: function ($scope) {
                $scope.$on('$ionicView.beforeEnter', function () {
                    Auth.isSignIn(function (user) {
                        if (user) {
                            return;
                        }
                        $state.go('login');
                    });
                });
            }
        };
    })

    .directive('tabAnimation', function ($rootScope) {
        return {
            restrict: 'A',
            link: function ($scope) {
                $scope.$on('$ionicView.beforeLeave', function () {
                    delete $rootScope.tabAnimation;
                });
            }
        };
    })

    .directive('backButton', ['$ionicConfig', '$document', function ($ionicConfig, $document) {
        return {
            restrict: 'C',
            require: '^ionNavBar',
            compile: function(tElement) {
                var hasIcon = /ion-|icon/.test(tElement[0].className) || /ion-|icon/.test(tElement[0].firstChild.className);

                var iconEle = $document[0].createElement('i');
                var defaultIcon = $ionicConfig.backButton.icon();
                if (!hasIcon && defaultIcon && defaultIcon !== 'none') {
                    iconEle.setAttribute('class', 'icon ' + defaultIcon);
                    tElement[0].insertBefore(iconEle, tElement[0].firstChild);
                }
            }
        };
    }])
;