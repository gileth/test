<?xml version="1.0" encoding="UTF-8"?>
<role name="支付" parent="roles.base">
    <accredit>
        <apps acType="whitelist">
              <app id="app.lottery">



                  <catagory id="RECHARGE_WITHDRAW">
                         <app  id="userAddMoney">
                            <others/>
                         </app>
                         <app  id="recharge">
                            <others/>
                         </app>
                         <app  id="proxyRecharge">
                           <others/>
                         </app>

                        <app  id="transfer">
                          <others/>
                        </app>
                    </catagory>

                    <catagory id="room">
                         <app  id="roomList">
                           <action id="query"/>
                         </app>
                         <app  id="roomFee">
                           <others/>
                         </app>
                    </catagory>
              </app>
          </apps>
    </accredit>
</role>