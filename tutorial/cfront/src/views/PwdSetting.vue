<template>
    <div class="crumbs">
        <!-- 提示文字 -->
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>
              <i class="el-icon-setting"></i>修改密码
          </el-breadcrumb-item>
        </el-breadcrumb>
        <!-- 主要逻辑,由三个输入框和一个按钮构成 -->
        <el-card shadow="hover" class="container">
            <!-- 输入框 -->
            <el-row style="padding:0 15%">
                <el-form :model="form">
                    <el-form-item label="旧密码" label-width="80px">
                        <el-input :type="'password'" v-model="form.oldpassword"/>
                    </el-form-item>
                    <el-form-item label="新密码" label-width="80px">
                        <el-input :type="'password'" v-model="form.newpassword"/>
                    </el-form-item>
                    <el-form-item label="确认密码" label-width="80px">
                        <el-input :type="'password'" v-model="form.newpasswordcfm"/>
                    </el-form-item>
                    <!-- 渲染按钮 -->
                    <el-form-item>
                        <el-button style="float:right" size="small" type="primary" @click="onSubmit">确认修改</el-button>
                    </el-form-item>
                </el-form>
            </el-row>
        </el-card>
    </div>
</template>

<script>
    import { pwdUpdate, logout } from '../api/loginApi';
    import encryptMD5 from 'js-md5';

    export default {
        name:"PwdSetting",
        data() {
            return {
                form: {
                    oldpassword: '',
                    newpassword: '',
                    newpasswordcfm: '',
                }
            }
        },
        methods: {
            logoutCallback(code, msg, data) {
                if(code != 0) {
                    this.$message.error(msg);
                } else {
                    logout();
                }
            },
            onSubmit() {
                //校验两次输入的新密码是否一致
                if(this.form.newpassword != this.form.newpasswordcfm) {
                    this.$message.warning("两次密码输入不一致,请重新输入");
                    return;
                }
                //限制新密码的长度不能太短
                if(this.form.newpassword.length < 4) {
                    this.$message.warning("密码长度太短,请重新输入");
                    return;
                }
                // console.log(sessionStorage.getItem("uid"));
                pwdUpdate({
                    uid: sessionStorage.getItem("uid"),
                    oldPwd: encryptMD5(this.form.oldpassword),
                    newPwd: encryptMD5(this.form.newpassword),
                },
                    this.logoutCallback
                )
            },
        }
    }
</script>

<style scoped>
</style>