package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import android.text.TextUtils;
import cn.changl.safe360.android.App;

/**
 * 登录信息类
 * 
 * @author rocksen
 * 
 */
public class LoginObject extends BaseObject {
    private static final long serialVersionUID = -5309950792580269318L;

    // 手机号码
    private String phone;

    // 密码
    private String password;

    // 经度
    private String lng;

    // 纬度
    private String lat;

    // 位置
    private String location;
    
    //名称
    private String addrname;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLng() {
        if (TextUtils.isEmpty(lng) && App.getInstance().getLocater() != null) {
            lng = App.getInstance().getLocater().getLng();
        }
        return lng;
    }

    public double getLng2() {
        double value = 0.0;
        try {
            value = Double.valueOf(lng);
        } catch (Exception e) {

        }
        return value;
    }

    public void setLng(String longitude) {
        this.lng = longitude;
    }

    public String getLat() {
        if (TextUtils.isEmpty(lat) && App.getInstance().getLocater() != null) {
            lat = App.getInstance().getLocater().getLat();
        }
        return lat;
    }

    public double getLat2() {
        double value = 0.0;
        try {
            value = Double.valueOf(lat);
        } catch (Exception e) {

        }
        return value;
    }

    public void setLat(String latitude) {
        this.lat = latitude;
    }

    public String getLocation() {
        if (null == location) {
            return "";
        }
        if (TextUtils.isEmpty(location) && App.getInstance().getLocater() != null) {
            return App.getInstance().getLocater().getAddress();
        }
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddrname() {
        if (TextUtils.isEmpty(addrname)) {
            return "";
        }
        return addrname;
    }

    public void setAddrname(String addrname) {
        this.addrname = addrname;
    }
}
