<template>
    <div>
        <!-- 订单薄窗口 -->
        <el-form label-width="80px">
            <el-form-item>
                <!-- 行情时间 -->
                <h4 style="color:#909399">
                    行情时间:{{hqttime}}
                </h4>
            </el-form-item>
            <!-- 五档行情 -->
            <el-form-item>
                <!-- 卖 -->
                <div class="orderBook">
                    <el-row v-for="item in sell" :key="item.name">
                        <!-- 档位名称 -->
                        <el-col :span="6">{{item.name}}</el-col>
                        <!-- 价格 -->
                        <el-col :span="6">{{item.price | filterempty}}</el-col>
                        <!-- 长度 -->
                        <el-col :span="6">
                            <div class="volumeratio">
                                <div class="sell" v-bind:style="{width:item.width + '%'}"></div>
                            </div>
                        </el-col>
                        <!-- 量 -->
                        <el-col :span="6">
                            {{item.volume | filterempty}}
                        </el-col>
                    </el-row>
                </div>
                <!-- 买 -->
                <div class="orderBook">
                    <el-row v-for="item in buy" :key="item.name">
                        <!-- 档位名称 -->
                        <el-col :span="6">{{item.name}}</el-col>
                        <!-- 价格 -->
                        <el-col :span="6">{{item.price | filterempty}}</el-col>
                        <!-- 长度 -->
                        <el-col :span="6">
                            <div class="volumeratio">
                                <div class="buy" v-bind:style="{width:item.width + '%'}"></div>
                            </div>
                        </el-col>
                        <!-- 量 -->
                        <el-col :span="6">
                            {{item.volume | filterempty}}
                        </el-col>
                    </el-row>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    export default {
        name:"OrderBook",
        filters: {
            filterempty(value) {
                if(value === -1) {
                    return '-';
                } else {
                    return value;
                }
            }
        },
        data() {
            return {
                hqttime: '--:--:--',
                sell: [
                    {
                        name: "卖五",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "卖四",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "卖三",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "卖二",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "卖一",
                        price: -1,
                        volume: -1,
                        width: 1,
                    }
                ],
                buy: [
                    {
                        name: "买一",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "买二",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "买三",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "买四",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                    {
                        name: "买五",
                        price: -1,
                        volume: -1,
                        width: 1,
                    },
                ],
            }
        },
    }
</script>

<style lang="scss" scoped>
    .orderBook {
        border: 1px solid #909399;;
        margin-left: 5%;
        margin-right: 5%;

        .el-row {
            .el-col {
                height: 35px;
                line-height: 35px;
            }
        }

        .volumeratio {
            margin: 10px auto;
            border: none;

            .sell {
                height: 10px;
                background: #67C23A;
            }

            .buy {
                height: 10px;
                background: #F56C6C;
            }
        }
    }
</style>