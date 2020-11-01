<template>
    <div>
        <!-- 搜索条件栏 -->
        <div class="handle-box">
            <el-row>
                <!-- 自动提示框 -->
                <el-col :span="4">
                    <code-input />
                </el-col>
                <!-- 日期选择器 -->
                <div style="float:left; margin-left: 10px">
                    <!-- 开始日期 -->
                    <el-date-picker
                        size="small"
                        type="date"
                        placeholder="选择日期"
                        value-format="yyyyMMdd"
                        v-model="query.startDate" 
                    />
                    <!-- 结束日期 -->
                    <el-date-picker
                        size="small"
                        type="date"
                        placeholder="选择日期"
                        value-format="yyyyMMdd"
                        v-model="query.endDate" 
                    />
                </div>
                <!-- 搜索按钮 -->
                <el-button style="float:left; margin-left: 10px"
                    size="small"
                    type="primary"
                    icon="el-icon-search"
                    @click="handleSearch"
                >
                搜索
                </el-button>
            </el-row>
        </div>
        <!-- 历史成交查询结果 -->
        <el-table
            :data="tableData.slice(
                (query.currentPage - 1) * query.pageSize,
                query.currentPage * query.pageSize
            )"
            border
            :cell-style="cellStyle"
            @sort-change="changeTableSort"
        >
            <el-table-column prop="date" label="成交日期" align="center" sortable :sort-orders="['ascending', 'descending']"/>
            <el-table-column prop="time" label="成交时间" align="center" />
            <el-table-column prop="code" label="股票代码" align="center" />
            <el-table-column prop="name" label="名称" align="center" />
            <el-table-column prop="price" label="成交价格(元)" align="center" />
            <el-table-column prop="amount" label="成交数量(股)" align="center" />
            <el-table-column prop="totalPrice" label="成交金额(元)" align="center" />
            <el-table-column prop="direction" label="方向" align="center" />
        </el-table>
        <!-- 分页控件 -->
        <div class= "pagination">
            <el-pagination
                background
                layout="total, prev, pager, next"
                :current-page="query.currentPage"
                :page-size="query.pageSize"
                :total="pageTotal"
                @current-change="handlePageChange"
            />
        </div>
    </div>
</template>

<script>

    import CodeInput from './CodeInput'
    export default {
        name:"HisTradeList",
        components: {
            CodeInput,
        },
        data() {
            return {
                query: {
                    currentPage: 1,//当前页码
                    pageSize: 3,//每页的数据条数
                    code: '',//股票代码
                    startDate: '',//开始日期
                    endDate: '',//结束日期
                },
                tableData: [
                    {
                        date: '20200105',
                        time: '14:00:01',
                        code: 600000,
                        name: '浦发银行',
                        price: 10,
                        amount: 100,
                        totalPrice: 1000,
                        direction: 1
                    },
                    {
                        date: '20200106',
                        time: '14:00:02',
                        code: 600000,
                        name: '浦发银行',
                        price: 11,
                        amount: 100,
                        totalPrice: 1100,
                        direction: 1
                    },
                    {
                        date: '20200205',
                        time: '14:00:03',
                        code: 600000,
                        name: '浦发银行',
                        price: 12,
                        amount: 100,
                        totalPrice: 1200,
                        direction: 1
                    },
                    {
                        date: '20200305',
                        time: '14:00:04',
                        code: 600000,
                        name: '浦发银行',
                        price: 13,
                        amount: 100,
                        totalPrice: 1300,
                        direction: 1
                    },
                    {
                        date: '20201005',
                        time: '14:00:05',
                        code: 600000,
                        name: '浦发银行',
                        price: 14,
                        amount: 100,
                        totalPrice: 1400,
                        direction: 1
                    },
                ],
                pageTotal: 5,
            }
        },
        methods: {
            handleSearch() {

            },
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
            updateSelectedCode(item) {
                this.query.code = item.code;
            },
        },
        created() {
            this.$bus.on("codeinput-selected", this.updateSelectedCode);
        },
        beforeDestroy() {
            this.$bus.off("codeinput-selected", this.updateSelectedCode);
        }
    }
</script>

<style scoped>
</style>