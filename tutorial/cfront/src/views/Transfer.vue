<template>
    <div>
        <!-- 信息提示 -->
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-bank-card"></i>银证转账
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <!-- 几个输入框和下拉框还有按钮 -->
        <el-card shadow="hover" class="container">
            <el-row style="padding:0 15%">
                <!-- 新建表单 -->
                <el-form :model="form">
                    <!-- 转账方式 下拉框 -->
                    <el-form-item label="转账方式">
                        <el-select style="width: 90%" v-model="type">
                            <el-option v-for="item in form.type"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id" />
                        </el-select>
                    </el-form-item>
                    <!-- 选择银行 下拉框 -->
                    <el-form-item label="选择银行">
                        <el-select style="width: 90%" v-model="bank">
                            <el-option v-for="item in form.bank"
                                :key="item.id"
                                :label="item.name"
                                :value="item.name" />
                        </el-select>
                    </el-form-item>
                    <!-- 银行密码 密码框 -->
                    <el-form-item label="银行密码">
                        <!-- password加单引号表示它是个变量 -->
                        <el-input style="width: 90%" :type="'password'" v-model="form.password"/>
                    </el-form-item>
                    <!-- 选择币种 下拉框 -->
                    <el-form-item label="选择币种">
                        <el-select style="width: 90%" v-model="moneytype">
                            <el-option v-for="item in form.moneytype"
                                :key="item.id"
                                :label="item.name"
                                :value="item.name" />
                        </el-select>
                    </el-form-item>
                    <!-- 金额 输入框 -->
                    <el-form-item label="金  额">
                        <el-input style="width: 90%" :type="'text'" v-model="form.money"/>
                    </el-form-item>
                    <!-- 提交按钮 -->
                    <el-form-item>
                        <el-button style="float:right" size="small" type="primary">转账</el-button>
                    </el-form-item>
                </el-form>
            </el-row>
        </el-card>
    </div>
</template>

<script>
    export default {
        name:"Transfer",
        data() {
            return {
                form: {
                    type: [{id: 1, name: '银行转证券(转入)'}, {id: 0, name: '证券转银行(转出)'}],
                    bank: [{id: 0, name: '招商银行'}, {id: 1, name: '建设银行'}],
                    password: '',
                    moneytype: [{id: 0,name:'人民币'}, {id: 1,name:'美元'}],
                    money: 0,
                },
                type: 0,
                bank: "招商银行",
                moneytype: 0,
            }
        }
    }
</script>

<style scoped>
</style>