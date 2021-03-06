package cn.itcast.travel.service;

import cn.itcast.travel.domain.User;

public interface UserService {
    /**
     * 注册用户的方法
     *
     * @param user
     * @return
     */
    boolean regist(User user);

    /**
     * 激活用户的方法
     *
     * @param code
     * @return
     */
    boolean active(String code);

    /**
     * 登录的方法
     * @param user
     * @return
     */
    User login(User user);

}
