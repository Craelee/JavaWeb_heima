package cn.itcast.travel.web.servlet;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet(name = "/user/*")
public class UserServlet extends BaseServlet {
    //声明UserService的业务对象
    private UserService userService = new UserServiceImpl();

    /**
     * 用户注册的方法
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证码的校验
        String check = request.getParameter("check");
        //从session中获取验证码
        HttpSession session = request.getSession();
        String checkcodeServer = (String) session.getAttribute("CHECKCODE_SERVER");
        //为了保证验证码只能使用一次
        session.removeAttribute("CHECKCODE_SERVER");
        //比较
        if (checkcodeServer == null || !checkcodeServer.equalsIgnoreCase(check)) {
            //验证码错误
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码错误！");
            //将resultInfo对象序列化为json
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(resultInfo);
            //将json数据写回到客户端
            //设置content-type
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(json);
            return;
        }
        //1.获取数据
        Map<String, String[]> map = request.getParameterMap();
        //2.封装对象
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.调用service完成注册
        //UserService userService = new UserServiceImpl();
        boolean flag = userService.regist(user);
        //4.响应结果
        ResultInfo resultInfo = new ResultInfo();
        if (flag) {
            //注册成功
            resultInfo.setFlag(true);
        } else {
            //注册失败
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("注册失败！");
        }
        //将resultInfo对象序列化为json
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(resultInfo);
        //将json数据写回到客户端
        //设置content-type
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    /**
     * 用户登录
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取用户名和密码数据
        Map<String, String[]> map = request.getParameterMap();
        //2.封装User对象
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用Service查询
        //UserService userService = new UserServiceImpl();
        User u = userService.login(user);

        ResultInfo resultInfo = new ResultInfo();
        //4.判断用户对象是否为null
        if (u == null) {
            //用户名或密码错误
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("用户名或密码错误");
        }
        //5.判断用户是否激活
        if (u != null && !"Y".equals(u.getStatus())) {
            //用户尚未激活
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("您尚未激活，请登录邮箱激活");
        }
        //6.判断登陆成功
        if (u != null && "Y".equals(u.getStatus())) {
            request.getSession().setAttribute("user", u);
            //登陆成功
            resultInfo.setFlag(true);
        }
        //响应数据
/*        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), resultInfo);*/
        writeValue(resultInfo, response);
    }

    /**
     * 查找一个用户
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.从session中获取登陆用户
        Object user = request.getSession().getAttribute("user");
        //2.将user写回客户端
/*        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), user);*/
        writeValue(user, response);
    }

    /**
     * 退出功能
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.销毁session
        request.getSession().invalidate();
        //2.跳转页面
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    /**
     * 激活用户
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取激活码
        String code = request.getParameter("code");
        if (code != null) {
            //2.调用userService完成激活
            //UserService userService = new UserServiceImpl();
            boolean flag = userService.active(code);
            //3.判断标记
            String msg = null;
            if (flag) {
                //激活成功，跳转到到响应的页面
                msg = "激活成功，请<a href='login.html'>登录</a>";
            } else {
                //激活失败，跳转到响应的页面
                msg = "激活失败，请联系管理员！";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }
}
