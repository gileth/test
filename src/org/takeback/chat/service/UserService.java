// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.takeback.chat.entity.PubBank;
import org.takeback.chat.entity.PubWithdraw;
import org.takeback.util.valid.ValidateUtil;
import org.takeback.chat.utils.DateUtil;
import org.takeback.chat.entity.PubRoomApply;
import org.takeback.chat.entity.LoginLog;
import org.takeback.util.encrypt.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.takeback.chat.entity.PubExchangeLog;
import org.takeback.chat.entity.PubShop;
import org.takeback.util.identity.UUIDGenerator;
import org.takeback.chat.entity.PubRecharge;
import org.takeback.chat.entity.TransferLog;
import java.util.HashMap;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.util.List;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcRoomProperty;
import org.takeback.chat.utils.RoomTemplate;
import org.takeback.chat.entity.GcRoomMoney;
import java.util.Date;
import org.takeback.chat.entity.GcRoom;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.chat.service.admin.SystemConfigService;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class UserService extends BaseService
{
    public static Double ROOM_FEE;
    
    @Transactional(rollbackFor = { Throwable.class })
    public void createRoom(final int uid) {
        final Double price = Double.valueOf(SystemConfigService.getInstance().getValue("conf_room_money").toString());
        if (price < 0.0) {
            throw new CodedBaseRuntimeException("购买配置出错!");
        }
        final String hql = "update PubUser set money=money - :price where money>:price and  id=:uid";
        if (0 == this.dao.executeUpdate(hql,  ImmutableMap.of( "price", price,  "uid", uid))) {
            throw new CodedBaseRuntimeException("金币不足,无法创建房间!");
        }
        final GcRoom rm = new GcRoom();
        rm.setCatalog("");
        rm.setCreatedate(new Date());
        rm.setFeeAdd(0.05);
        rm.setHot(1);
        rm.setLimitNum(5000);
        rm.setName("牛牛房间");
        rm.setOwner(uid);
        rm.setPsw("");
        rm.setStatus("0");
        rm.setSumFee(0.0);
        rm.setType("G022");
        final String roomId = System.currentTimeMillis() + "";
        rm.setId(roomId);
        this.dao.save(GcRoom.class, rm);
        final GcRoomMoney gcRoomMoney = new GcRoomMoney();
        gcRoomMoney.setTotalMoney(price);
        gcRoomMoney.setRestMoney(price - UserService.ROOM_FEE);
        gcRoomMoney.setRoomId(roomId);
        this.dao.save(GcRoomMoney.class, gcRoomMoney);
        final List<GcRoomProperty> defaults = RoomTemplate.get("G022");
        if (defaults != null) {
            for (final GcRoomProperty prop : defaults) {
                prop.setRoomId(rm.getId());
                final GcRoomProperty n = new GcRoomProperty();
                BeanUtils.copy(prop, n);
                this.dao.save(GcRoomProperty.class, n);
                this.dao.getSession().flush();
            }
        }
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public PubUser updateUser(final int uid, final Map<String, Object> data) {
        final PubUser pubUser = this.dao.get(PubUser.class, uid);
        if (pubUser != null) {
            final Map<String, Object> params = new HashMap<String, Object>();
            for (final Map.Entry<String, Object> en : data.entrySet()) {
                if (en.getKey().equals("nickName") || en.getKey().equals("mobile") || en.getKey().equals("headImg") || en.getKey().equals("pwd") || en.getKey().equals("accessToken") || en.getKey().equals("tokenExpireTime")) {
                    params.put(en.getKey(), en.getValue());
                }
            }
            BeanUtils.copy(params, pubUser);
            this.dao.update(PubUser.class, pubUser);
        }
        return null;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void transfer(final Integer uid, final Integer account, final double money) {
        if (!"1".equals(SystemConfigService.getInstance().getValue("conf_transfer"))) {
            throw new CodedBaseRuntimeException("系统转账功能已关闭!");
        }
        final PubUser target = this.dao.get(PubUser.class, account);
        if (target == null) {
            throw new CodedBaseRuntimeException("目标账号不存在!");
        }
        if (target.getId().equals(uid)) {
            throw new CodedBaseRuntimeException("不允许给自己转账!");
        }
        final int effected = this.dao.executeUpdate("update PubUser set money=money -:money where money >:money and  id=:uid", ImmutableMap.of( "money", (money + 0.0),  "uid",  uid));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("金额不足!");
        }
        this.dao.executeUpdate("update PubUser set money=money +:money where id=:uid",  ImmutableMap.of( "money",  (money + 0.0),  "uid",  target.getId()));
        final PubUser fromUser = this.dao.get(PubUser.class, uid);
        final TransferLog tl = new TransferLog();
        tl.setFromUid(uid);
        tl.setFromNickName(fromUser.getUserId());
        tl.setToUid(target.getId());
        tl.setToNickName(target.getUserId());
        tl.setMoney(money);
        tl.setTransferDate(new Date());
        this.dao.save(TransferLog.class, tl);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void prixyRecharge(final Integer uid, final Integer account, final Integer money) {
        if (!"1".equals(SystemConfigService.getInstance().getValue("conf_proxyRecharge"))) {
            throw new CodedBaseRuntimeException("功能已关闭!");
        }
        final PubUser target = this.dao.get(PubUser.class, account);
        if (target == null) {
            throw new CodedBaseRuntimeException("目标账号不存在!");
        }
        if (target.getId().equals(uid)) {
            throw new CodedBaseRuntimeException("不允许给自己充值!");
        }
        if (!uid.equals(target.getParent())) {
            throw new CodedBaseRuntimeException("只能给直接下线上分!");
        }
        if (money <= 0) {
            throw new CodedBaseRuntimeException("请输入大于0的金额!");
        }
        final int effected = this.dao.executeUpdate("update PubUser set money=money -:money where money >:money and  id=:uid",  ImmutableMap.of( "money",  (money + 0.0), "uid",  uid));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("金额不足!");
        }
        this.dao.executeUpdate("update PubUser set money=money +:money where  id=:uid", ImmutableMap.of( "money",  (money + 0.0),  "uid", target.getId()));
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt("上分");
        pubRecharge.setFee(money + 0.0);
        pubRecharge.setGoodsname("上分");
        pubRecharge.setTradeno(UUIDGenerator.get());
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("2");
        pubRecharge.setUid(account);
        pubRecharge.setUserIdText(target.getUserId());
        pubRecharge.setOperator(uid);
        this.dao.save(PubRecharge.class, pubRecharge);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void prixyUnRecharge(final Integer uid, final Integer account, final Integer money) {
        if (!"1".equals(SystemConfigService.getInstance().getValue("conf_proxyWithdraw"))) {
            throw new CodedBaseRuntimeException("功能已关闭!");
        }
        final PubUser target = this.dao.get(PubUser.class, account);
        if (target == null) {
            throw new CodedBaseRuntimeException("目标账号不存在!");
        }
        if (target.getId().equals(uid)) {
            throw new CodedBaseRuntimeException("不允许给自己充值!");
        }
        if (!uid.equals(target.getParent())) {
            throw new CodedBaseRuntimeException("只能给直接下线下分!");
        }
        if (money <= 0) {
            throw new CodedBaseRuntimeException("请输入大于0的金额!");
        }
        final int effected = this.dao.executeUpdate("update PubUser set money=money -:money where  id=:uid  and  money >:money",  ImmutableMap.of( "money",  (money + 0.0),  "uid",  target.getId()));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("金额不足!");
        }
        this.dao.executeUpdate("update PubUser set money=money+:money where  id=:uid", ImmutableMap.of( "money",  (money + 0.0), "uid",  uid));
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt("下分");
        pubRecharge.setFee(money + 0.0);
        pubRecharge.setGoodsname("下分");
        pubRecharge.setTradeno(UUIDGenerator.get());
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("3");
        pubRecharge.setUid(account);
        pubRecharge.setUserIdText(target.getUserId());
        pubRecharge.setOperator(uid);
        this.dao.save(PubRecharge.class, pubRecharge);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void exchange(final Integer uid, final Integer goodId, final String name, final String address, final String mobile) {
        final PubUser u = this.dao.get(PubUser.class, uid);
        final PubShop s = this.dao.get(PubShop.class, goodId);
        if (s.getStorage() < 1) {
            throw new CodedBaseRuntimeException("库存商品!");
        }
        final int effected = this.dao.executeUpdate("update PubUser set money = coalesce(money,0) - :money where money>:money  and uid = :uid", ImmutableMap.of("money", s.getMoney(), "uid", uid));
        if (effected < 1) {
            throw new CodedBaseRuntimeException("账户金币不足!");
        }
        final PubExchangeLog pel = new PubExchangeLog();
        pel.setStatus("0");
        pel.setAddress(address);
        pel.setExchangeTime(new Date());
        pel.setMobile(mobile);
        pel.setMoney(s.getMoney());
        pel.setName(name);
        pel.setShopId(goodId.toString());
        pel.setShopName(s.getName());
        pel.setUid(uid);
        this.dao.save(PubExchangeLog.class, pel);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void bindMobile(final int uid, final String mobile) {
        this.dao.executeUpdate("update PubUser set mobile=:mobile where id=:uid",  ImmutableMap.of( "mobile", mobile,  "uid", uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void updatePwd(final int uid, final String pwd) {
        this.dao.executeUpdate("update PubUser set pwd=:pwd where id=:uid", ImmutableMap.of( "pwd", pwd,  "uid", uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void updateHeadImg(final int uid, final String headImg) {
        this.dao.executeUpdate("update PubUser set headImg=:headImg where id=:uid", ImmutableMap.of( "headImg", headImg, "uid",  uid));
    }
    
    @Transactional(readOnly = true)
    public PubUser login(final String username, final String password) {
        return this.get(username, password);
    }
    
    @Transactional
    public void setLoginInfo(final String ip, final Integer uid) {
        this.dao.executeUpdate("update PubUser set lastLoginDate=:loginDate,lastLoginIp = :lastLoginIp where id=:uid",  ImmutableMap.of( "loginDate",  new Date(),  "lastLoginIp",  ip,  "uid",  uid));
    }
    
    @Transactional(readOnly = true)
    public PubUser get(final String username, final String password) {
        final PubUser user = this.dao.getUnique(PubUser.class, "userId", username);
        if (user == null) {
            return null;
        }
        if (CryptoUtils.verify(user.getPwd(), password, StringUtils.reverse(user.getSalt()))) {
            return user;
        }
        return null;
    }
    
    @Transactional(readOnly = true)
    public PubUser get(final int uid, final String password) {
        final PubUser user = this.dao.getUnique(PubUser.class, "id", uid);
        if (user == null) {
            return null;
        }
        if (CryptoUtils.verify(user.getPwd(), password, StringUtils.reverse(user.getSalt()))) {
            return user;
        }
        return null;
    }
    
    @Transactional(readOnly = true)
    public double getBalance(final int uid) {
        final PubUser user = this.dao.getUnique(PubUser.class, "id", uid);
        if (user == null) {
            return 0.0;
        }
        return user.getMoney();
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public PubUser register(final String username, final String password, final String mobile, final String wx, final String alipay, final Integer parent, final String ip) {
        PubUser user = this.dao.getUnique(PubUser.class, "userId", username);
        if (user != null) {
            throw new CodedBaseRuntimeException("用户名已存在!");
        }
        user = new PubUser();
        final String salt = CryptoUtils.getSalt();
        user.setUserId(username);
        user.setNickName(username);
        user.setSalt(salt);
        user.setWx(wx);
        user.setUserType("1");
        user.setMobile(mobile);
        user.setLastLoginDate(new Date());
        user.setLastLoginIp(ip);
        user.setAlipay(alipay);
        user.setPwd(CryptoUtils.getHash(password, StringUtils.reverse(salt)));
        user.setMoneyCode(user.getPwd());
        user.setMoney(0.0);
        final Object conf = SystemConfigService.getInstance().getValue("conf_init_money");
        if (conf != null) {
            try {
                user.setMoney(Double.valueOf(conf.toString()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        user.setRegistDate(new Date());
        user.setUserType("1");
        user.setRegistIp(ip);
        if (parent != null) {
            final PubUser p = this.dao.get(PubUser.class, parent);
            if (p != null) {
                user.setParent(parent);
            }
        }
        this.dao.save(PubUser.class, user);
        final LoginLog l = new LoginLog();
        l.setLoginTime(new Date());
        l.setIp(ip);
        l.setUserId(user.getId());
        l.setUserName(user.getUserId());
        this.dao.save(LoginLog.class, l);
        return user;
    }
    
    @Transactional(readOnly = true)
    public PubUser getByWxOpenId(final String openId) {
        return this.dao.getUnique(PubUser.class, "wxOpenId", openId);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void roomApply(final String name, final String mobile, final int uid) {
        final PubRoomApply r = new PubRoomApply();
        r.setName(name);
        r.setCreateTime(new Date());
        r.setMobile(mobile);
        r.setUid(uid);
        final PubUser user = this.dao.get(PubUser.class, uid);
        r.setUserIdText(user.getUserId());
        this.dao.save(PubRoomApply.class, r);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void proxyApply(final Integer uid, final Map<String, Object> conf) {
        final PubUser u = this.dao.get(PubUser.class, uid);
        if ("2".equals(u.getUserType())) {
            throw new CodedBaseRuntimeException("你已经是代理,无需申请!");
        }
        final Double limit = Double.valueOf(conf.get("money").toString());
        final int effected = this.dao.executeUpdate("update PubUser set money=coalesce(money,0)-:money where money>=:money and  id =:uid ",  ImmutableMap.of( "money",  limit, "uid", uid));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("账户金币不足,申请失败!");
        }
        this.dao.executeUpdate("update PubUser set userType = '2' where id =:uid ", ImmutableMap.of( "uid",  uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void withdraw(final Map<String, Object> data, final int uid) {
        final double money = Double.valueOf(data.get("money").toString());
        if (money < 100.0) {
            throw new CodedBaseRuntimeException("最低提现金额100");
        }
        final List<PubWithdraw> wl = this.dao.findByHql("from PubWithdraw where uid=:uid and tradetime>:startDate and tradetime<=:endDate", ImmutableMap.of( "uid",  uid,  "startDate", DateUtil.getStartOfToday(), "endDate", DateUtil.getEndOfToday()));
        if (wl.size() >= 3) {
            throw new CodedBaseRuntimeException("每天提现次数已达上限:" + wl.size() + "次");
        }
        final String bankName = data.get("bankName").toString();
        if (!ValidateUtil.instance().isChinese(bankName)) {
            throw new CodedBaseRuntimeException("提款银行必须是中文");
        }
        final String account = data.get("account").toString();
        final String branch = data.get("branch").toString();
        if (branch != null && !"".equals(branch) && !ValidateUtil.instance().isChinese(bankName)) {
            throw new CodedBaseRuntimeException("提款银行分支必须是中文");
        }
        final String ownerName = data.get("ownerName").toString();
        if (!ValidateUtil.instance().isChinese(bankName)) {
            throw new CodedBaseRuntimeException("提款姓名必须是中文");
        }
        final String mobile = data.get("mobile").toString();
        final String hql = "update PubUser set money = money - :money where id=:id and money > :money";
        final int effect = this.dao.executeUpdate(hql,  ImmutableMap.of( "money", money, "id",  uid));
        if (effect == 0) {
            throw new CodedBaseRuntimeException("金额不足");
        }
        final PubWithdraw pw = new PubWithdraw();
        pw.setAccount(account);
        pw.setBankName(bankName);
        pw.setBranch(branch);
        pw.setFee(money);
        pw.setMobile(mobile);
        pw.setOwnerName(ownerName);
        pw.setUid(uid);
        final PubUser user = this.dao.get(PubUser.class, uid);
        pw.setUserIdText(user.getUserId());
        pw.setStatus("1");
        pw.setTradetime(new Date());
        this.dao.save(PubWithdraw.class, pw);
        final String hql2 = "from PubBank where userId=:userId and account =:account";
        final List<PubBank> bankList = this.dao.findByHql(hql2,  ImmutableMap.of( "userId", uid,  "account", account));
        PubBank pb;
        if (bankList.size() == 0) {
            pb = new PubBank();
            pb.setCreateTime(new Date());
            pb.setUserId(uid);
        }
        else {
            pb = bankList.get(0);
        }
        pb.setMobile(mobile);
        pb.setUserIdText(user.getUserId());
        pb.setBranch(branch);
        pb.setAccount(account);
        pb.setBankName(bankName);
        pb.setName(ownerName);
        this.dao.saveOrUpdate(PubBank.class, pb);
    }
    
    static {
        UserService.ROOM_FEE = 50.0;
    }
}