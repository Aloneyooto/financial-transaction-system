<template>
    <div>
        <!-- 信息提示 -->
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-bank-card"></i>转账查询
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="handle-box">
            <el-row>
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
                <!-- 查询按钮 -->
                <el-button style="float:left; margin-left: 10px"
                    size="small"
                    type="primary"
                    icon="el-icon-search"
                    @click="handleSearch"
                >
                查询
                </el-button>
            </el-row>
        </div>
        <!-- 表格组件 -->
        <el-table
            :data="tableData.slice(
                (query.currentPage - 1) * query.pageSize,
                query.currentPage * query.pageSize
            )"
            border
            :cell-style="cellStyle"
            @sort-change="changeTableSort"
        >
            <el-table-column prop="date" label="委托日期" align="center" sortable :sort-orders="['ascending','descending']"/>
            <el-table-column prop="time" label="委托时间" align="center" />
            <el-table-column prop="kind" label="业务类别" align="center" />
            <el-table-column prop="money" label="币种" align="center" />
            <el-table-column prop="price" label="金额" align="center" />
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
    export default {
        name:"TransferQuery",
        data() {
            return {
                query: {
                    startDate: '',
                    endDate: '',
                    currentPage: 1,
                    pageSize: 2,
                },
                pageTotal: 2,
                tableData: [

                ],
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
        }
    }
</script>

<style scoped>
</style>