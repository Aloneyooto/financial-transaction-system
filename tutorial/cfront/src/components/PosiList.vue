<template>
    <div>
        <!-- 可用资金 -->
        <el-row>
            <el-col :span="5">
                可用资金: {{balance}}
            </el-col>
        </el-row>
        <!-- 持仓列表 表格 -->
        <el-table
            :data="tableData.slice(
                (query.currentPage - 1) * query.pageSize,
                query.currentPage * query.pageSize
            )"
            border
            :cell-style="cellStyle"
            @sort-change="changeTableSort"
        >
            <!-- vs code向下复制一行的快捷键:shift+alt+down -->
            <!-- 当前页记录按当前行排序:sortable :sort-orders="['ascending', 'descending']" -->
            <el-table-column prop="code" label="代码" align="center" 
                             sortable :sort-orders="['ascending', 'descending']"
                             :formatter="codeFormatter"
            />
            <el-table-column prop="name" label="名称" align="center"/>
            <el-table-column prop="count" label="股票数量" align="center"/>
            <el-table-column prop="cost" label="总投入" align="center" :formatter="moneyFormatter"/>
            <el-table-column label="成本" align="center" :formatter="costFormatter"/>
        </el-table>
        <!-- 分页空间 刷新按钮 -->
        <div class="pagination">
            <!-- 刷新按钮 -->
            <el-button round
                type="primary" size="mini"
                style="margin-top: 2px;float: right"
                icon="el-icon-refresh"
                @click="queryRefresh()"
                >
            刷新
            </el-button>
            <!-- 分页控件 -->
            <el-pagination
                background
                layout="total, prev, pager, next"
                :current-page="query.currentPage"
                :page-size="query.pageSize"
                :total="dataTotalCount"
                @current-change="handlePageChange"
            />
        </div>
    </div>
</template>

<script>

    import {constants} from '../api/constants'
    import {codeFormat, moneyFormat} from '../api/formatter'
    import {queryPosi, queryBalance} from '../api/orderApi'

    export default {
        name:"PosiList",
        computed: {
            posiData() {
                return this.$store.state.posiData;
            },
            balanceData() {
                return moneyFormatter(this.$store.state.balance);
            }
        },
        watch: {
            posiData: function(val) {
                this.tableData = val;
                this.dataTotalCount = val.length;
            },
            balanceData: function(val) {
                this.balance = val;
            }
        },
        data() {
            return {
                balance: 10,
                tableData: [
                    
                ],
                dataTotalCount: 0,//总记录数
                query: {
                    currentPage: 1,//当前页码
                    pageSize: 2,//每页的记录数
                }
            }
        },
        created() {
            this.tableData = this.posiData;
            this.balance = this.balanceData;  
        },
        methods: {
            cellStyle(row, column, rowIndex, columnIndex) {
                return "padding:2px";
            },
            handlePageChange(val) {
                //直接修改无法触发vue进行重绘
                this.$set(this.query, 'currentPage', val);
            },
            changeTableSort(column) {
                if(column.order == "descending") {
                    this.tableData = this.tableData.sort(
                        (a, b) => b[column.prop] - a[column.prop]
                    );
                } else {
                    this.tableData = this.tableData.sort(
                        (a, b) => a[column.prop] - b[column.prop]
                    );
                }
            },
            //成本转换器
            costFormatter(row, column) {
                return (row.cost / constants.MULTI_FACTOR / row.count).toFixed(2);
            },
            moneyFormatter(row, column) {
                return moneyFormat(row.cost);
            },
            codeFormatter(row, column) {
                return codeFormat(row.code);
            },
            //刷新按钮
            queryRefresh() {
                queryPosi();
                queryBalance();
            }
        }
    }
</script>

<style scoped>
</style>