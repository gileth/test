<?xml version="1.0" encoding="UTF-8"?>
<role name="操作员1" parent="roles.base">
    <accredit>
        <apps acType="whitelist">
              <app id="app.lottery">
                  <catagory id="BET">
                           <app  id="lottery">
                              <others/>
                           </app>
                         <app  id="detail">
                            <others/>
                         </app>
                         <app  id="gameMonitor1">
                            <action id="query"/>
                            <action id="read"/>
                            <action id="update"/>
                         </app>
                         <app  id="controlLog">
                            <others/>
                         </app>
                    </catagory>
              </app>
          </apps>
    </accredit>
</role>