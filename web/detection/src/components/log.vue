<template>
  <div class="log-container">
    <!-- 顶部筛选区域 -->
    <div class="filter-section" ref="filterRef">
      <div class="filter-group">
        <span class="filter-label">时间段:</span>
        <div class="date-picker-group">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="pickerOptions"
            class="date-picker"
          ></el-date-picker>
          <el-button type="primary" class="confirm-btn" @click="mainTime">确认时间段</el-button>
        </div>
      </div>
      
      <div class="search-group">
        <el-input 
          v-model="searchName" 
          placeholder="搜索主体或工号" 
          class="search-input" 
          prefix-icon="el-icon-search" 
          @input="search"
          clearable
        ></el-input>
      </div>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-section">
      <el-card class="table-card">
        <div class="table-wrapper">
          <el-table 
            :data="filteredtableData" 
            style="width: 100%"
            stripe
            v-loading="loading"
            element-loading-text="数据加载中..."
            element-loading-spinner="el-icon-loading"
            :max-height="tableHeight"
          >
            <el-table-column label="编号" width="100" align="center" header-align="center">
              <template slot-scope="scope">
                <span>{{ (page - 1) * pageSize + scope.$index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="mainRole" label="主体" min-width="120" align="center" header-align="center"></el-table-column>
            <el-table-column prop="label" label="工号或账号" min-width="150" align="center" header-align="center"></el-table-column>
            <el-table-column prop="op" label="操作" min-width="180" align="center" header-align="center"></el-table-column>
            <el-table-column prop="time" label="时间" min-width="160" align="center" header-align="center"></el-table-column>
          </el-table>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-container" ref="paginationRef">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="page"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            background
          >
          </el-pagination>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import moment from "moment/moment";

export default {
  data() {
    return {
      searchName: '',
      tableData: [],
      page: 1,
      pageSize: 10,
      total: 0,
      dateRange: [],
      dateL: '',
      dateR: '',
      loading: false,
      tableHeight: 400,
      pickerOptions: {
        shortcuts: [
          {
            text: '最近一周',
            onClick(picker) {
              const end = moment().endOf('day');
              const start = moment(end).subtract(1, 'week').startOf('day');
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: '最近一月',
            onClick(picker) {
              const end = moment().endOf('day');
              const start = moment(end).subtract(1, 'month').startOf('day');
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: '最近一年',
            onClick(picker) {
              const end = moment().endOf('day');
              const start = moment(end).subtract(1, 'year').startOf('day');
              picker.$emit('pick', [start, end]);
            }
          }
        ],
        disabledDate(time) {
          return time.getTime() > Date.now();
        }
      },
    };
  },
  created() {
    this.fetchData();
    this.$nextTick(() => {
      setTimeout(() => {
        this.calculateTableHeight();
      }, 100);
    });
    window.addEventListener('resize', this.calculateTableHeight);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.calculateTableHeight);
  },
  computed: {
    filteredtableData() {
      if (!this.searchName) {
        return this.tableData;
      } else {
        const searchName = this.searchName.toLowerCase();
        return this.tableData.filter(log => {
          return (
            log.mainRole.toLowerCase().includes(searchName) ||
            log.label.toLowerCase().includes(searchName)
          );
        });
      }
    }
  },
  methods: {
    calculateTableHeight() {
      const viewportHeight = window.innerHeight;
      const logContainer = document.querySelector('.log-container');
      
      if (!logContainer) return;
      
      // 获取容器距离顶部的距离
      const containerTop = logContainer.getBoundingClientRect().top;
      
      // 获取筛选区域高度
      let filterHeight = 0;
      if (this.$refs.filterRef) {
        filterHeight = this.$refs.filterRef.offsetHeight;
      }
      
      // 获取分页区域高度（使用固定值，因为此时可能还未渲染）
      const paginationHeight = 80; // 分页组件的大概高度
      
      // 计算可用高度：视口高度 - 容器顶部距离 - 筛选区域高度 - 分页区域高度 - 安全边距
      const safeMargin = 40; // 安全边距
      const availableHeight = viewportHeight - containerTop - filterHeight - paginationHeight - safeMargin;
      
      // 设置表格高度，确保至少有200px
      this.tableHeight = Math.max(availableHeight, 200);
      
      console.log('表格高度计算:', {
        viewportHeight,
        containerTop,
        filterHeight,
        paginationHeight,
        availableHeight,
        tableHeight: this.tableHeight
      });
    },
    search() {
      this.searchName = this.searchName.trim();
    },
    mainTime() {
      console.log("这里是时间段哦", this.dateRange);
      if (this.dateRange && this.dateRange.length === 2) {
        let startTime = new Date(this.dateRange[0]);
        let endTime = new Date(this.dateRange[1]);
        startTime.setHours(23, 59, 59, 999);
        endTime.setHours(23, 59, 59, 999);
        this.dateL = startTime.getTime();
        this.dateR = endTime.getTime();
        console.log("开始时间的时间戳：", this.dateL);
        console.log("结束时间的时间戳：", this.dateR);
        this.fetchData();
      } else {
        this.$message.warning('请选择完整的时间范围');
      }
    },
    fetchData() {
      this.loading = true;
      axios.get('api/log/info', {
        params: {
          page: this.page,
          pageSize: this.pageSize,
          dateL: this.dateL,
          dateR: this.dateR
        }
      })
        .then(response => {
          this.loading = false;
          if (response.data.code === 200) {
            this.tableData = response.data.data;
            console.log("22222222222", response.data.data)
            this.total = this.tableData[0].totals;
            console.log("1111111111111111111", this.total)
            this.$message({
              message: '查询到日志信息',
              type: 'success'
            });
            // 数据加载完成后重新计算高度
            this.$nextTick(() => {
              setTimeout(() => {
                this.calculateTableHeight();
              }, 100);
            });
          } else {
            this.$message.error("日志信息获取失败")
          }
        })
        .catch(error => {
          this.loading = false;
          console.error('获取数据失败', error);
          this.$message({
            message: '未能查询到日志信息',
            type: 'error'
          });
        });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.page = 1; // 重置到第一页
      this.fetchData();
    },
    handleCurrentChange(val) {
      this.page = val;
      this.fetchData();
    }
  }
}
</script>

<style scoped>
.log-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
  box-sizing: border-box;
}

.filter-group {
  display: flex;
  align-items: center;
}

.filter-label {
  font-weight: 500;
  color: #606266;
  margin-right: 12px;
  font-size: 14px;
}

.date-picker-group {
  display: flex;
  align-items: center;
}

.date-picker {
  width: 280px;
  margin-right: 12px;
}

.confirm-btn {
  height: 40px;
  padding: 0 20px;
}

.search-group {
  display: flex;
}

.search-input {
  width: 240px;
}

.table-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: none;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-wrapper {
  flex: 1;
  overflow: hidden;
}

.pagination-container {
  margin-top: auto; /* 让分页始终在底部 */
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
  padding: 20px;
  background: white;
  border-top: 1px solid #ebeef5;
  min-height: 80px; /* 确保分页区域有足够高度 */
  box-sizing: border-box;
}

/* 表格样式优化 */
.el-table {
  font-size: 14px;
}

.el-table th {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

/* 确保表格内容完整显示 */
:deep(.el-table .cell) {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-group {
    margin-top: 16px;
    width: 100%;
  }
  
  .search-input {
    width: 100%;
  }
  
  .date-picker-group {
    flex-direction: column;
    width: 100%;
  }
  
  .date-picker {
    width: 100%;
    margin-right: 0;
    margin-bottom: 12px;
  }
  
  .confirm-btn {
    width: 100%;
  }
  
  .pagination-container {
    padding: 15px 10px;
    min-height: 70px;
  }
}
</style>