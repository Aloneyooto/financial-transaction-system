<template>
    <div>
        <!-- 委托列表 -->
        <el-table
            :data="tableData.slice(
                (query.currentPage - 1) * query.pageSize,
                query.currentPage * query.pageSize
            )"
            border
            :cell-style="cellStyle"
            @sort-change="changeTableSort"
            :default-sort="{prop: 'time', order: 'descending'}"
        >
            <!-- 委托时间 股票代码 名称 委托价格 委托数量 方向 状态 -->
            <el-table-column prop="time" label="委托时间" align="center" sortable :sort-orders="['ascending', 'descending']"/>
            <el-table-column prop="code" label="股票代码" align="center" />
            <el-table-column prop="name" label="名称" align="center" />
            <el-table-column prop="price" label="委托价格" align="center" />
            <el-table-column prop="amount" label="委托数量" align="center" />
            <el-table-column prop="direction" label="方向" align="center" />
            <el-table-column prop="status" label="状态" align="center" />
            <!-- 撤单按钮 -->
        </el-table>
        <!-- 分页控件+刷新按钮 -->
        <div class="pagination">
            <!-- 刷新按钮 -->
            <el-button round
                type="primary" size="mini"
                style="margin-top: 2px;float: right"
                icon="el-icon-refresh"
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
    export default {
        name: "OrderList",
        data() {
            return {
                balance: 10,
                tableData: [
                    {
                        time: '09:55:00',
                        code: '000001',
                        name: '平安银行',
                        price: 100,
                        amount: 10,
                        direction: '买入',
                        status: 3
                    },
                    {
                        time: '09:50:00',
                        code: '000001',
                        name: '平安银行',
                        price: 100,
                        amount: 10,
                        direction: '买入',
                        status: 1
                    },
                    {
                        time: '09:40:00',
                        code: '000001',
                        name: '平安银行',
                        price: 100,
                        amount: 10,
                        direction: '买入',
                        status: 3
                    }
                ],
                dataTotalCount: 3,//总记录数
                query: {
                    currentPage: 1,//当前页码
                    pageSize: 2,//每页的记录数
                }
            }
        },
        methods: {
            cellStyle(row, column, rowIndex, columnIndex) {
                return "padding:2px";
            },
            changeTableSort(column) {
                let fieldName = column.prop;
                let sortingType = column.order;
                if(fieldName === 'time') {
                    if(sortingType == "descending") {
                        this.tableData = this.tableData.sort(
                            (a, b) => {
                                if(b[fieldName] > a[fieldName]) {
                                    return 1;
                                } else if(b[fieldName] === a[fieldName]) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        );
                    } else {
                        this.tableData = this.tableData.sort(
                            (a, b) => {
                                if(a[fieldName] > b[fieldName]) {
                                    return 1;
                                } else if(a[fieldName] === b[fieldName]) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        );
                    }
                } 
            },
            handlePageChange(val) {
                //直接修改无法触发vue进行重绘
                this.$set(this.query, 'currentPage', val);
            },
        }
    }
</script>

<style scoped>
</style>