package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @Override
    public boolean regist(User user) {
        //1.根据同户名查询用户对象
        User queryUser = userDao.findByUsername(user.getUsername());
        //判断queryUser是否为null
        if (queryUser != null) {
            //用户名存在，注册失败
            return false;
        } else {
            //2.保存用户信息
            //2.1设置激活码，唯一字符串
            user.setCode(UuidUtil.getUuid());
            //2.2设置激活状态
            user.setStatus("N");
            userDao.save(user);
            //3.激活邮件发送
            String content = "<a href='http://localhost:8080/travel/user/active?code=" + user.getCode() + "'>点击激活【黑马旅游网站】</a>";
            MailUtils.sendMail(user.getEmail(), content, "激活邮件");
            return true;
        }
    }

    /**
     * 激活用户的方法
     *
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        //1.根据激活码查询用户对象
        User user = userDao.findByCode(code);
        if (user != null) {
            //2.调用dao的修改激活状态的方法
            userDao.updateStatus(user);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用户登录的方法
     *
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }
}
