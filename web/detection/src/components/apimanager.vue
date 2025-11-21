<template>
  <div class="api-management">
    <div class="header">
      <h1><i class="el-icon-s-management"></i> API密钥管理</h1>
      <div class="operation-buttons">
        <el-button type="primary" @click="handleAdd" icon="el-icon-plus">添加</el-button>
        <el-button type="primary" @click="handleAddmore" icon="el-icon-document-add">批量添加</el-button>
        <el-button type="danger" @click="deleteMore" icon="el-icon-delete">批量删除</el-button>
        <span v-if="selectedRows.length > 0" class="selection-count">已选择 {{ selectedRows.length }} 项</span>
      </div>
    </div>

    <div class="table-container">
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        @selection-change="handleSelectionChange"
        stripe
        border
        :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
        :cell-style="{padding: '8px 0'}">
        <el-table-column type="selection" width="50" align="center"></el-table-column>
        <el-table-column type="index" label="编号" width="80" align="center"></el-table-column>
        <el-table-column prop="createName" label="创建者" width="100" align="center"></el-table-column>
        <el-table-column prop="apiKey" label="API密钥" width="150" align="center">
          <template slot-scope="scope">
            <span class="api-key-text">{{ scope.row.apiKey }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expirationDate" label="到期日期" width="120" align="center">
          <template slot-scope="scope">
            <span class="date-text">{{ calculateExpirationDate(scope.row.validityPeriod) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="validityPeriod" label="有效期(天)" width="110" align="center">
          <template slot-scope="scope">
            <el-tag :type="getValidityPeriodType(scope.row.validityPeriod)" class="validity-tag">
              {{ calculateExpirationDate1(scope.row.validityPeriod) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="validityTimes" label="有效次数" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getValidityTimesType(scope.row.validityTimes)" class="validity-tag">
              {{ calculateExpirationDate2(scope.row.validityTimes) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissionLevel" label="权限等级" width="100" align="center">
          <template slot-scope="scope">
            <el-tag 
              :type="getPermissionLevelType(scope.row.permissionLevel)"
              effect="dark"
              class="permission-tag">
              权限{{ scope.row.permissionLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag 
              :type="scope.row.status === 1 ? 'success' : 'danger'"
              effect="dark"
              class="status-tag">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="120" show-overflow-tooltip align="center">
          <template slot-scope="scope">
            <span class="remark-text">{{ scope.row.remark || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button size="mini" type="danger" @click="handleDelete(scope.$index)" icon="el-icon-delete">删除</el-button>
              <el-button size="mini" type="warning" @click="handleedit(scope.$index)" icon="el-icon-edit">修改</el-button>
              <el-button size="mini" type="primary" @click="handleCopy(scope.$index)" icon="el-icon-document-copy">复制</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <!-- 添加数据对话框 -->
    <el-dialog title="添加API密钥" :visible.sync="dialogVisible" width="520px" center class="custom-dialog">
      <div class="dialog-content">
        <el-form ref="formData" :model="formData" :rules="formRules" label-width="100px">
          <el-form-item label="有效期(天)" prop="validityPeriod" class="required-field">
            <el-input v-model="formData.validityPeriod" placeholder="请输入有效期天数，-1代表无限期"></el-input>
          </el-form-item>
          <el-form-item label="有效次数" prop="validityTimes" class="required-field">
            <el-input v-model="formData.validityTimes" placeholder="请输入有效次数，-1代表无限次"></el-input>
          </el-form-item>
          <el-form-item label="权限等级" prop="permissionLevel">
            <el-select v-model="formData.permissionLevel" placeholder="请选择权限等级">
              <el-option 
                v-for="item in permissionOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value">
                <span :style="{ color: getPermissionColor(item.value) }">{{ item.label }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input type="textarea" v-model="formData.remark" placeholder="请输入备注信息" :rows="3"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="addData" class="confirm-btn">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 批量添加数据对话框 -->
    <el-dialog title="批量添加API密钥" :visible.sync="moreAdddialogVisible" width="520px" center class="custom-dialog">
      <div class="dialog-content">
        <el-form ref="formDataMore" :rules="formRulesMore" :model="formDataMore" label-width="100px">
          <el-form-item label="数量" prop="num" class="required-field">
            <el-input v-model="formDataMore.num" placeholder="请输入要生成的数量"></el-input>
          </el-form-item>
          <el-form-item label="有效期(天)" prop="validityPeriod" class="required-field">
            <el-input v-model="formDataMore.validityPeriod" placeholder="请输入有效期天数，-1代表无限期"></el-input>
          </el-form-item>
          <el-form-item label="有效次数" prop="validityTimes" class="required-field">
            <el-input v-model="formDataMore.validityTimes" placeholder="请输入有效次数，-1代表无限次"></el-input>
          </el-form-item>
          <el-form-item label="权限等级" prop="permissionLevel">
            <el-select v-model="formDataMore.permissionLevel" placeholder="请选择权限等级">
              <el-option 
                v-for="item in permissionOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value">
                <span :style="{ color: getPermissionColor(item.value) }">{{ item.label }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="formDataMore.status" placeholder="请选择状态">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input type="textarea" v-model="formDataMore.remark" placeholder="请输入备注信息" :rows="3"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="moreAdddialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="addDatamore" class="confirm-btn">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 编辑数据对话框 -->
    <el-dialog title="编辑API密钥" :visible.sync="EditdialogVisible" width="520px" center class="custom-dialog">
      <div class="dialog-content">
        <el-form ref="EditformData" :model="EditformData" :rules="formRules" label-width="100px">
          <el-form-item label="有效期(天)" prop="validityPeriod" class="required-field">
            <el-input v-model="EditformData.validityPeriod" placeholder="请输入有效期天数，-1代表无限期"></el-input>
          </el-form-item>
          <el-form-item label="有效次数" prop="validityTimes" class="required-field">
            <el-input v-model="EditformData.validityTimes" placeholder="请输入有效次数，-1代表无限次"></el-input>
          </el-form-item>
          <el-form-item label="权限等级" prop="permissionLevel">
            <el-select v-model="EditformData.permissionLevel" placeholder="请选择权限等级">
              <el-option 
                v-for="item in permissionOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value">
                <span :style="{ color: getPermissionColor(item.value) }">{{ item.label }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="EditformData.status" placeholder="请选择状态">
              <el-option label="禁用" :value="0"></el-option>
              <el-option label="启用" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input type="textarea" v-model="EditformData.remark" placeholder="请输入备注信息" :rows="3"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="EditdialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="EditData" class="confirm-btn">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    // 自定义数字验证规则
    const validateNumber = (rule, value, callback) => {
      if (value === '' || value === null || value === undefined) {
        callback(new Error('该字段不能为空'));
      } else if (isNaN(Number(value))) {
        callback(new Error('必须为数字值'));
      } else {
        callback();
      }
    };

    return {
      page: 1,
      pageSize: 10,
      total: 0,
      user: JSON.parse(localStorage.getItem('useradmin') || '{}'),
      permissionOptions: [
        { label: '权限1', value: '1' },
        { label: '权限2', value: '2' },
        { label: '权限3', value: '3' }
      ],
      formRules: {
        validityPeriod: [
          { required: true, validator: validateNumber, trigger: 'blur' }
        ],
        validityTimes: [
          { required: true, validator: validateNumber, trigger: 'blur' }
        ],
        permissionLevel: [
          { required: false, message: '权限等级不能为空', trigger: 'change' },
        ],
        status: [
          { required: false, message: '状态不能为空', trigger: 'change' },
        ]
      },
      formRulesMore: {
        num: [
          { required: true, validator: validateNumber, trigger: 'blur' }
        ],
        validityPeriod: [
          { required: true, validator: validateNumber, trigger: 'blur' }
        ],
        validityTimes: [
          { required: true, validator: validateNumber, trigger: 'blur' }
        ],
        permissionLevel: [
          { required: false, message: '权限等级不能为空', trigger: 'change' },
        ],
        status: [
          { required: false, message: '状态不能为空', trigger: 'change' },
        ]
      },
      dialogVisible: false,
      moreAdddialogVisible:false,
      tableData: [],
      formData: {
        num:1,
        validityPeriod: '',
        validityTimes: '',
        permissionLevel: '1',
        status: 1,
        remark: '',
        createName:'',
      },
      formDataMore:{
        num:null,
        validityPeriod: '',
        validityTimes: '',
        permissionLevel: '1',
        status: 1,
        remark: '',
        createName:'',
      },
      selectedRows: [],
      EditdialogVisible :false,
      EditformData :{
        validityPeriod: '',
        validityTimes: '',
        permissionLevel: '1',
        status: '',
        remark: '',
        createName:'',
      },
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    // 获取权限等级对应的颜色
    getPermissionColor(level) {
      switch(level) {
        case '1': return '#409EFF'; // 权限1 - 蓝色
        case '2': return '#67C23A'; // 权限2 - 绿色
        case '3': return '#E6A23C'; // 权限3 - 橙色
        default: return '#909399';
      }
    },
    
    // 获取有效期类型对应的颜色
    getValidityPeriodType(validityPeriod) {
      if (validityPeriod < 0) {
        return 'warning'; // 无限期使用橙色
      } else {
        return 'primary'; // 有限期使用蓝色
      }
    },
    
    // 获取有效次数类型对应的颜色
    getValidityTimesType(validityTimes) {
      if (validityTimes < 0) {
        return 'warning'; // 无限次使用橙色
      } else {
        return 'success'; // 有限次使用绿色
      }
    },
    
    // 获取权限等级对应的颜色类型 - 修复主界面表格颜色
    getPermissionLevelType(level) {
      switch(level) {
        case '1': return '';   // 权限1 - 使用自定义蓝色
        case '2': return '';   // 权限2 - 使用自定义绿色
        case '3': return '';   // 权限3 - 使用自定义橙色
        default: return 'info';
      }
    },
    
    handleedit(index){
      this.EditformData = Object.assign({}, this.tableData[index]);
      this.EditdialogVisible=true;
    },
    
    async EditData() {
        try {
          // 确保数据类型正确
          const submitData = {
            ...this.EditformData,
            validityPeriod: Number(this.EditformData.validityPeriod),
            validityTimes: Number(this.EditformData.validityTimes),
            status: Number(this.EditformData.status)
          };
          
          const response = await axios.patch(`api/sysManage/api/update`, submitData);
          if (response.data.code === 200) {
            this.EditdialogVisible = false;
            this.$message.success("保存编辑数据成功");
            // 不再重新获取数据，直接更新本地数据
            const index = this.tableData.findIndex(item => item.id === this.EditformData.id);
            if (index !== -1) {
              this.tableData.splice(index, 1, { ...this.EditformData });
            }
          } else {
            this.$message.error("保存编辑数据失败")
          }
        } catch (error) {
          this.$message.error("保存编辑数据失败"+error)
        }
    },

    calculateExpirationDate(validityPeriod) {
      if (validityPeriod < 0) {
        return '无限期'; // 恢复显示无限期
      } else {
        const today = new Date();
        const expirationDate = new Date(today.getTime() + validityPeriod * 24 * 60 * 60 * 1000);
        return expirationDate.toISOString().slice(0, 10);
      }
    },
    
    calculateExpirationDate1(validityPeriod) {
      if (validityPeriod < 0) {
        return '无限期';
      } else {
        return validityPeriod;
      }
    },
    
    calculateExpirationDate2(validityTimes) {
      if (validityTimes < 0) {
        return '无限次';
      } else {
        return validityTimes
      }
    },
    
    fetchData() {
      axios.get('api/sysManage/api/info', {
        params: {
          page: this.page,
          pageSize: this.pageSize,
          dateL:this.dateL,
          dateR:this.dateR
        }
      })
          .then(response => {
            console.log("response.code",response.data.code);
            if (response.data.code === 200) {
              this.tableData = response.data.data;
              this.total=response.data.data[0].totals;
            } else {
              this.$message({
                message: "获取数据失败，错误代码：" + response.data.code,
                type: "error"
              });
            }
          })
          .catch(error => {
            console.error(error);
            this.$message({
              message:"获取数据失败",
              type:"error"
            });
          });
    },
    
    handleDelete(index) {
      // 删除操作
      const item = this.tableData[index];
      const ids = [item.id];
      axios.delete("api/sysManage/api/delete",{
        data: ids
      })
          .then(response => {
            if (response.data.code === 200) {
              this.tableData.splice(index, 1);
              this.$message.success("删除成功");
            } else {
              this.$message({
                message:response.data.message,
                type: "error"
              });
            }
          })
          .catch(error => {
            console.error(error);
            this.$message({
              message: "删除失败",
              type: "error"
            });
          });
    },
    
    handleCopy(index) {
      const item = this.tableData[index];
      if (!item) {
        this.$message.warning("没有找到要复制的项");
        return;
      }
      const serializedItem = JSON.stringify(item);
      navigator.clipboard.writeText(serializedItem)
          .then(() => {
            this.$message.success("已成功复制到剪贴板");
          })
          .catch(error => {
            console.error("复制失败", error);
            this.$message.error("复制失败，请重试");
          });
    },
    
    handleAdd(){
      this.dialogVisible=true;
    },
    
    addData() {
      this.$refs['formData'].validate((valid) => {
        if (valid) {
          let name = this.user.name;
          this.formData.createName = name;
          
          // 确保数据类型正确
          const submitData = {
            ...this.formData,
            validityPeriod: Number(this.formData.validityPeriod),
            validityTimes: Number(this.formData.validityTimes),
            status: Number(this.formData.status)
          };
          
          axios.post('api/sysManage/api/add', submitData)
              .then(response => {
                if (response.data.code === 200) {
                  this.$message.success("添加成功");
                  this.dialogVisible = false;
                  // 重置表单
                  this.formData = {
                    num:1,
                    validityPeriod: '',
                    validityTimes: '',
                    permissionLevel: '1',
                    status: 1,
                    remark: '',
                    createName:'',
                  };
                  // 重新获取数据以显示新添加的项
                  this.fetchData();
                } else {
                  this.$message.error("添加失败");
                }
              })
              .catch(error => {
                this.$message.error("添加失败");
              });
        } else {
          this.$message.warning('请填写完整信息！');
        }
      });
    },
    
    handleAddmore(){
      this.moreAdddialogVisible=true;
    },
    
    addDatamore() {
      this.$refs['formDataMore'].validate((valid) => {
        if (valid) {
          let name = this.user.name;
          this.formDataMore.createName = name;
          
          // 确保数据类型正确
          const submitData = {
            ...this.formDataMore,
            validityPeriod: Number(this.formDataMore.validityPeriod),
            validityTimes: Number(this.formDataMore.validityTimes),
            status: Number(this.formDataMore.status)
          };
          
          axios.post('api/sysManage/api/add', submitData)
              .then(response => {
                if (response.data.code === 200) {
                  this.moreAdddialogVisible = false;
                  this.$message.success("批量添加成功");
                  // 重置表单
                  this.formDataMore = {
                    num:null,
                    validityPeriod: '',
                    validityTimes: '',
                    permissionLevel: '1',
                    status: 1,
                    remark: '',
                    createName:'',
                  };
                  // 重新获取数据以显示新添加的项
                  this.fetchData();
                } else {
                  this.$message.error("批量添加失败");
                }
              })
              .catch(error => {
                this.$message.error("批量添加失败");
              });
        } else {
          this.$message.warning('请填写完整信息！');
        }
      });
    },
    
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    
    deleteMore() {
      if (this.selectedRows.length === 0) {
        this.$message.warning("请先勾选要删除的数据");
        return;
      }
      const ids = this.selectedRows.map(item => item.id);
      axios.delete("api/sysManage/api/delete", {
              data: ids
          })
          .then(response => {
            if(response.data.code===200){
              this.selectedRows.forEach(item => {
                const index = this.tableData.findIndex(data => data.id === item.id);
                this.tableData.splice(index, 1);
              });
              this.$message.success("批量删除成功");
              this.selectedRows = [];
            }else{
              this.$message.error("批量删除失败")
            }
          })
          .catch(error => {
            console.error(error);
            this.$message.error("批量删除失败");
          });
    },
    
    handleSizeChange(val) {
      this.pageSize = val;
      this.fetchData();
    },
    
    handleCurrentChange(val) {
      this.page = val;
      this.fetchData();
    }
  }
};
</script>

<style scoped>
.api-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 40px);
}

.header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  margin: 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.header h1 i {
  margin-right: 10px;
  color: #409EFF;
  font-size: 24px;
}

.operation-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selection-count {
  background: #409EFF;
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 10px;
}

.table-container {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.api-key-text {
  font-family: 'Courier New', monospace;
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 4px;
  border-left: 3px solid #409EFF;
  color: #409EFF;
  font-weight: 500;
  font-size: 12px;
  word-break: break-all;
}

.date-text {
  color: #606266;
  font-weight: 500;
}

.validity-tag {
  font-weight: 500;
  min-width: 60px;
}

.permission-tag {
  font-weight: 500;
  min-width: 60px;
}

.status-tag {
  font-weight: 500;
  min-width: 50px;
}

.remark-text {
  color: #909399;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-buttons .el-button {
  margin: 0;
  font-size: 12px;
  padding: 5px 10px;
}

.pagination-container {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  text-align: right;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
}

/* 必填字段样式 - 只对有效期和有效次数显示红色星号 */
.required-field ::v-deep .el-form-item__label::before {
  content: '*';
  color: #F56C6C;
  margin-right: 4px;
}

/* 禁止表格列宽拖动 */
::v-deep .el-table__header-wrapper .el-table__header .el-table__cell {
  user-select: none;
}

::v-deep .el-table .el-table__header .el-table__cell .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

::v-deep .el-table .el-table__header .el-table__cell {
  resize: none;
}

/* 自定义对话框样式 - 美化版 */
::v-deep .custom-dialog .el-dialog {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

::v-deep .custom-dialog .el-dialog__header {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  margin: 0;
  padding: 25px 30px;
  border-radius: 0;
  position: relative;
  text-align: center;
}

::v-deep .custom-dialog .el-dialog__title {
  color: white;
  font-weight: 700;
  font-size: 20px;
  letter-spacing: 0.5px;
}

::v-deep .custom-dialog .el-dialog__headerbtn {
  top: 28px;
  right: 30px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
}

::v-deep .custom-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 16px;
  font-weight: bold;
}

::v-deep .custom-dialog .el-dialog__headerbtn:hover {
  background: rgba(255, 255, 255, 0.3);
}

::v-deep .custom-dialog .el-dialog__body {
  padding: 0;
}

.dialog-content {
  padding: 35px 40px 25px;
  background: white;
}

::v-deep .custom-dialog .el-form {
  background: white;
}

::v-deep .custom-dialog .el-form-item {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

::v-deep .custom-dialog .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
  width: 100px !important;
  text-align: right;
  padding-right: 15px;
}

/* 权限等级和状态字段不显示红色星号 */
::v-deep .custom-dialog .el-form-item:not(.required-field) .el-form-item__label::before {
  content: none !important;
}

::v-deep .custom-dialog .el-form-item__content {
  flex: 1;
  margin-left: 0 !important;
}

::v-deep .custom-dialog .el-input__inner,
::v-deep .custom-dialog .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e8e8e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 12px 16px;
  background: #fafbfc;
  width: 100%;
  height: 44px;
}

::v-deep .custom-dialog .el-input__inner:focus,
::v-deep .custom-dialog .el-textarea__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
  background: white;
  transform: translateY(-1px);
}

::v-deep .custom-dialog .el-select .el-input__inner {
  border-radius: 10px;
}

::v-deep .custom-dialog .el-textarea__inner {
  resize: vertical;
  min-height: 90px;
  line-height: 1.5;
}

::v-deep .custom-dialog .el-dialog__footer {
  background: #f8f9fa;
  padding: 20px 40px;
  border-top: 1px solid #e8e8e8;
  border-radius: 0 0 16px 16px;
  text-align: center;
}

::v-deep .custom-dialog .dialog-footer .el-button {
  border-radius: 10px;
  padding: 12px 32px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  margin: 0 12px;
  min-width: 100px;
  border: 2px solid transparent;
}

.cancel-btn {
  background: #f5f7fa;
  border-color: #dcdfe6 !important;
  color: #606266;
}

.cancel-btn:hover {
  background: #e4e7ed;
  border-color: #c0c4cc !important;
  color: #409EFF;
  transform: translateY(-2px);
}

.confirm-btn {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border: none;
  color: white;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409EFF 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

/* 主界面表格权限等级标签颜色 - 修复版 */
::v-deep .el-table .permission-tag.el-tag {
  border: none;
  color: white !important;
}

/* 权限等级1 - 蓝色 */
::v-deep .el-table .el-tag[data-level="1"],
::v-deep .el-table .el-tag:contains("权限1") {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}

/* 权限等级2 - 绿色 */
::v-deep .el-table .el-tag[data-level="2"],
::v-deep .el-table .el-tag:contains("权限2") {
  background-color: #67C23A !important;
  border-color: #67C23A !important;
}

/* 权限等级3 - 橙色 */
::v-deep .el-table .el-tag[data-level="3"],
::v-deep .el-table .el-tag:contains("权限3") {
  background-color: #E6A23C !important;
  border-color: #E6A23C !important;
}

/* 通过自定义属性设置权限等级颜色 */
::v-deep .el-table .permission-tag[data-level="1"] {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
}

::v-deep .el-table .permission-tag[data-level="2"] {
  background-color: #67C23A !important;
  border-color: #67C23A !important;
}

::v-deep .el-table .permission-tag[data-level="3"] {
  background-color: #E6A23C !important;
  border-color: #E6A23C !important;
}

/* 权限等级选项颜色 */
::v-deep .custom-dialog .el-select-dropdown .el-select-dropdown__item {
  font-weight: 500;
  padding: 10px 16px;
}

::v-deep .custom-dialog .el-select-dropdown .el-select-dropdown__item.selected {
  font-weight: 600;
  background: #f5f7fa;
}

/* 权限等级1 - 蓝色 */
::v-deep .custom-dialog .el-select-dropdown .el-select-dropdown__item[data-value="1"] {
  color: #409EFF !important;
}

/* 权限等级2 - 绿色 */
::v-deep .custom-dialog .el-select-dropdown .el-select-dropdown__item[data-value="2"] {
  color: #67C23A !important;
}

/* 权限等级3 - 橙色 */
::v-deep .custom-dialog .el-select-dropdown .el-select-dropdown__item[data-value="3"] {
  color: #E6A23C !important;
}

/* 输入框placeholder样式 */
::v-deep .custom-dialog .el-input__inner::placeholder,
::v-deep .custom-dialog .el-textarea__inner::placeholder {
  color: #c0c4cc;
  font-size: 13px;
}
</style>