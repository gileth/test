<?xml version="1.0" encoding="UTF-8"?>
<role name="风控" parent="roles.base">
    <accredit>
        <apps acType="whitelist">
              <app id="app.lottery">
                  <catagory id="CONFIG">

                       <app  id="realUsers">
                          <action id="query"/>
                          <action id="moneyOrder"/>
                          <action id="scoreOrder"/>
                          <action id="chargeAmountOrder"/>
                          <action id="registDateOrder"/>
                          <action id="loginDateOrder"/>
                       </app>
                      <app  id="user_ip">
                           <action id="query"/>
                           <action id="read"/>
                        </app>
                  </catagory>

                  <catagory id="shopCatalog">
                     <app  id="exchangeLog">
                         <action id="query"/>
                      </app>
                  </catagory>


                  <catagory id="RECHARGE_WITHDRAW">
                         <app  id="userAddMoney">
                            <action id="query"/>
                         </app>
                         <app  id="recharge">
                            <others/>
                         </app>
                         <app  id="proxyRecharge">
                           <others/>
                         </app>
                        <app  id="withdraw">
                          <action id="query"/>
                          <action id="read"/>
                          <action id="update"/>
                        </app>
                        <app  id="transfer">
                          <action id="query"/>
                          <action id="read"/>
                        </app>
                        <app  id="transfer">
                          <others/>
                        </app>
                    </catagory>

                    <catagory id="room">
                         <app  id="roomList">
                            <action id="query"/>
                         </app>
                         <app  id="roomPropList">
                            <action id="query"/>
                         </app>
                         <app  id="roomFee">
                           <action id="query"/>
                         </app>
                    </catagory>

                    <catagory id="BET">
                         <app  id="lottery">
                            <action id="query"/>
                            <action id="read"/>
                         </app>
                         <app  id="detail">
                            <others/>
                         </app>
                         <app  id="gameMonitor1">
                           <action id="query"/>
                           <action id="read"/>
                         </app>
                         <app  id="gameMonitor2">
                            <action id="read"/>
                          </app>
                    </catagory>

                    <catagory id="PCDD">
                         <app  id="betLog">
                            <action id="query"/>
                         </app>
                    </catagory>
              </app>
          </apps>
    </accredit>
</role>