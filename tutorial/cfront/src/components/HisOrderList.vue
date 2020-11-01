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
        <!-- 历史委托查询结果 -->
        <el-table
            :data="tableData.slice(
                (query.currentPage - 1) * query.pageSize,
                query.currentPage * query.pageSize
            )"
            border
            :cell-style="cellStyle"
            @sort-change="changeTableSort"
        >
            <el-table-column prop="date" label="委托日期" align="center" sortable :sort-orders="['ascending', 'descending']"/>
            <el-table-column prop="time" label="委托时间" align="center" />
            <el-table-column prop="code" label="股票代码" align="center" />
            <el-table-column prop="name" label="名称" align="center" />
            <el-table-column prop="price" label="委托价格" align="center" />
            <el-table-column prop="amount" label="委托数量" align="center" />
            <el-table-column prop="status" label="状态" align="center" />
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

    import CodeInput from '../components/CodeInput'

    export default {
        name:"HisOrderList",
        components: {
            CodeInput,
        },
        data() {
            return {
                tableData: [
                    {
                        date: '20200105',
                        time: '14:00:01',
                        code: 600000,
                        name: '浦发银行',
                        price: 10,
                        amount: 100,
                        status: 1
                    },
                    {
                        date: '20200101',
                        time: '14:00:02',
                        code: 600000,
                        name: '浦发银行',
                        price: 11,
                        amount: 100,
                        status: 1
                    },
                    {
                        date: '20200103',
                        time: '14:00:03',
                        code: 600000,
                        name: '浦发银行',
                        price: 12,
                        amount: 100,
                        status: 1
                    },
                    {
                        date: '20200111',
                        time: '14:00:04',
                        code: 600000,
                        name: '浦发银行',
                        price: 13,
                        amount: 100,
                        status: 1
                    },
                ],
                query: {
                    currentPage: 1,//当前页码
                    pageSize: 3,//每页的数据条数
                    code: '',//股票代码
                    startDate: '',//开始日期
                    endDate: '',//结束日期
                },
                pageTotal: 4,
            }
        },
        methods: {
            updateSelectedCode(item) {
                this.query.code = item.code;
            },
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
                                if(b[fieldName] > a[fieldName]) {
                                    return -1;
                                } else if(a[fieldName] === b[fieldName]) {
                                    return 0;
                                } else {
                                    return 1;
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