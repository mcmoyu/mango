package com.moyu.mango.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moyu.mango.R;
import com.moyu.mango.bmob.MangoUser;
import com.moyu.mango.tools.MD5;
import com.moyu.mango.tools.StatusBarUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Bmob后端云 Application ID
    String BMOB_APPID = "7747989a391017f93a9fbfcd40b90ba7";

    // 调试变量
    String id;

    // 控件变量
    EditText et_username;
    EditText et_password;
    Button btn_register;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 沉浸式状态栏
        StatusBarUtil.setTransparent(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRxJavaErrorHandler();

        // Bmob后端云 初始化
        Bmob.initialize(this, BMOB_APPID);

        // 控件变量赋值
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                userRegister();
                break;
            case R.id.btn_login:
                userLogin();
                break;
        }
    }

    private void userRegister() {
        final String username = et_username.getText().toString();
        String pattern = "^[a-zA-Z]+[0-9]*[a-zA-Z]*$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(username);
        if("".equals(username)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
        } else {
            final String password = et_password.getText().toString();
            if("".equals(password)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            } else {
                if(m.matches()){
                    if(username.length() < 6) {
                        Toast.makeText(this, "账号长度范围为6-15位(包含6和15)", Toast.LENGTH_SHORT).show();
                    } else {
                        pattern = "^[^\\x22\\u4E00-\\u9FA5]{9,25}$";
                        r = Pattern.compile(pattern);
                        m = r.matcher(password);
                        if(m.matches()){
                            BmobQuery<MangoUser> mangoUserBmobQuery = new BmobQuery<>();
                            mangoUserBmobQuery.addWhereEqualTo("username", username);
                            mangoUserBmobQuery.findObjects(new FindListener<MangoUser>() {
                                @Override
                                public void done(List<MangoUser> object, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(MainActivity.this, "账号已存在，请直接登录！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        final MangoUser mangoUser = new MangoUser();
                                        mangoUser.setUsername(username);
                                        mangoUser.setPassword(MD5.encrypt(password));
                                        mangoUser.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if(e == null){
                                                    id = s;
                                                    Toast.makeText(MainActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MainActivity.this, "注册失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(this, "密码由任意非汉字组成，长度为9-25位(包含9和25)", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "账号以大小写字母开头，且只能由大小写字母和数字组成", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void userLogin() {
        final String username = et_username.getText().toString();
        BmobQuery<MangoUser> mangoUserBmobQuery = new BmobQuery<>();
        mangoUserBmobQuery.addWhereEqualTo("username", username);
        mangoUserBmobQuery.findObjects(new FindListener<MangoUser>() {
            @Override
            public void done(List<MangoUser> object, BmobException e) {
                if(e == null) {
                    String pattern = "^[a-zA-Z]+[0-9]*[a-zA-Z]*$";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(username);
                    if ("".equals(username)) {
                        Toast.makeText(MainActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    } else {
                        String password = et_password.getText().toString();
                        if ("".equals(password)) {
                            Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                        } else {
                            if (m.matches()) {
                                if (username.length() < 6) {
                                    Toast.makeText(MainActivity.this, "账号长度范围为6-15位(包含6和15)", Toast.LENGTH_SHORT).show();
                                } else {
                                    pattern = "^[^\\x22\\u4E00-\\u9FA5]{9,25}$";
                                    r = Pattern.compile(pattern);
                                    m = r.matcher(password);
                                    if (m.matches()) {
                                        if (MD5.encrypt(password).equals(object.get(0).getPassword())) {
                                            // Toast.makeText(MainActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            MainActivity.this.finish();
                                        } else {
                                            Toast.makeText(MainActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "密码由任意非汉字组成，长度为9-25位(包含9和25)", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "账号以大小写字母开头，且只能由大小写字母和数字组成", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "账号不存在！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("Main", "Throw test");
            }
        });
    }
}
