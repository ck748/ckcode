-- ============================================
-- 数据标注系统数据库设计
-- ============================================

USE defect_detection;

-- 1. 原始图片表（摄像头拍摄上传的图片）
DROP TABLE IF EXISTS raw_image;
CREATE TABLE raw_image
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    image_name      VARCHAR(128) NOT NULL COMMENT '图片名称',
    image_path      VARCHAR(255) NOT NULL COMMENT '图片存储路径',
    image_size      BIGINT COMMENT '图片大小(字节)',
    image_width     INT COMMENT '图片宽度',
    image_height    INT COMMENT '图片高度',
    upload_source   VARCHAR(32) DEFAULT 'camera' COMMENT '上传来源：camera-摄像头, manual-手动上传',
    device_id       SMALLINT COMMENT '设备ID（关联device表）',
    work_order_id   INTEGER COMMENT '工单ID（关联work_order表）',
    upload_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    upload_user_id  INTEGER COMMENT '上传用户ID',
    upload_user_name VARCHAR(32) COMMENT '上传用户名称',
    status          TINYINT DEFAULT 0 COMMENT '状态：0-待标注, 1-标注中, 2-已标注, 3-已检测',
    is_deleted      TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    remark          VARCHAR(255) COMMENT '备注',
    INDEX idx_status (status),
    INDEX idx_upload_time (upload_time),
    INDEX idx_work_order (work_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='原始图片表';

-- 2. 标注任务表
DROP TABLE IF EXISTS annotation_task;
CREATE TABLE annotation_task
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_name       VARCHAR(128) NOT NULL COMMENT '任务名称',
    task_type       VARCHAR(32) DEFAULT 'defect_detection' COMMENT '任务类型',
    description     TEXT COMMENT '任务描述',
    image_count     INT DEFAULT 0 COMMENT '图片总数',
    annotated_count INT DEFAULT 0 COMMENT '已标注数量',
    status          TINYINT DEFAULT 0 COMMENT '状态：0-待开始, 1-进行中, 2-已完成, 3-已暂停',
    priority        TINYINT DEFAULT 0 COMMENT '优先级：0-普通, 1-重要, 2-紧急',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_user_id  INTEGER COMMENT '创建人ID',
    create_user_name VARCHAR(32) COMMENT '创建人名称',
    assign_user_id  INTEGER COMMENT '分配标注员ID',
    assign_user_name VARCHAR(32) COMMENT '分配标注员名称',
    start_time      DATETIME COMMENT '开始时间',
    finish_time     DATETIME COMMENT '完成时间',
    deadline        DATETIME COMMENT '截止时间',
    is_deleted      TINYINT DEFAULT 0 COMMENT '逻辑删除',
    remark          VARCHAR(255) COMMENT '备注',
    INDEX idx_status (status),
    INDEX idx_assign_user (assign_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标注任务表';

-- 3. 标注数据表（存储标注的缺陷框和类别）
DROP TABLE IF EXISTS annotation_data;
CREATE TABLE annotation_data
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    raw_image_id    INTEGER NOT NULL COMMENT '原始图片ID（关联raw_image表）',
    task_id         INTEGER COMMENT '任务ID（关联annotation_task表）',
    category        VARCHAR(50) NOT NULL COMMENT '缺陷类别名称',
    category_id     TINYINT COMMENT '缺陷类别ID',
    x               DECIMAL(10, 4) NOT NULL COMMENT 'X坐标（相对坐标0-1或绝对坐标）',
    y               DECIMAL(10, 4) NOT NULL COMMENT 'Y坐标',
    width           DECIMAL(10, 4) NOT NULL COMMENT '框宽度（w或l）',
    height          DECIMAL(10, 4) NOT NULL COMMENT '框高度（h）',
    confidence      DECIMAL(6, 3) DEFAULT 1.000 COMMENT '置信度（人工标注默认1.0）',
    is_difficult    TINYINT DEFAULT 0 COMMENT '是否困难样本：0-否, 1-是',
    annotator_id    INTEGER COMMENT '标注人ID',
    annotator_name  VARCHAR(32) COMMENT '标注人名称',
    annotation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '标注时间',
    is_verified     TINYINT DEFAULT 0 COMMENT '是否已审核：0-未审核, 1-已审核, 2-审核不通过',
    verifier_id     INTEGER COMMENT '审核人ID',
    verify_time     DATETIME COMMENT '审核时间',
    is_deleted      TINYINT DEFAULT 0 COMMENT '逻辑删除',
    remark          VARCHAR(255) COMMENT '备注',
    INDEX idx_raw_image (raw_image_id),
    INDEX idx_task (task_id),
    INDEX idx_category (category_id),
    FOREIGN KEY (raw_image_id) REFERENCES raw_image(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标注数据表';

-- 4. 标注审核记录表
DROP TABLE IF EXISTS annotation_review;
CREATE TABLE annotation_review
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    annotation_id   INTEGER NOT NULL COMMENT '标注数据ID',
    raw_image_id    INTEGER NOT NULL COMMENT '原始图片ID',
    reviewer_id     INTEGER NOT NULL COMMENT '审核人ID',
    reviewer_name   VARCHAR(32) COMMENT '审核人名称',
    review_result   TINYINT NOT NULL COMMENT '审核结果：1-通过, 2-不通过',
    review_comment  TEXT COMMENT '审核意见',
    review_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    is_deleted      TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_annotation (annotation_id),
    INDEX idx_reviewer (reviewer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标注审核记录表';

-- 5. 图片标注关联表（一张图片可能在多个任务中）
DROP TABLE IF EXISTS task_image;
CREATE TABLE task_image
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id         INTEGER NOT NULL COMMENT '任务ID',
    raw_image_id    INTEGER NOT NULL COMMENT '原始图片ID',
    annotation_status TINYINT DEFAULT 0 COMMENT '标注状态：0-未标注, 1-已标注, 2-已审核',
    annotation_count INT DEFAULT 0 COMMENT '标注框数量',
    assign_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    complete_time   DATETIME COMMENT '完成时间',
    is_deleted      TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_task (task_id),
    INDEX idx_image (raw_image_id),
    UNIQUE KEY uk_task_image (task_id, raw_image_id),
    FOREIGN KEY (task_id) REFERENCES annotation_task(id) ON DELETE CASCADE,
    FOREIGN KEY (raw_image_id) REFERENCES raw_image(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务图片关联表';

-- ============================================
-- 修改现有表结构，增加数据来源标识
-- ============================================

-- 修改detect_log表，增加数据来源字段
ALTER TABLE detect_log
    ADD COLUMN data_source VARCHAR(32) DEFAULT 'detection' COMMENT '数据来源：detection-检测, annotation-标注' AFTER storage_path,
    ADD COLUMN raw_image_id INTEGER COMMENT '关联的原始图片ID' AFTER data_source,
    ADD INDEX idx_raw_image (raw_image_id);

-- 修改defection表，增加数据来源字段
ALTER TABLE defection
    ADD COLUMN data_source VARCHAR(32) DEFAULT 'detection' COMMENT '数据来源：detection-AI检测, manual-人工标注' AFTER category_id,
    ADD COLUMN annotation_id INTEGER COMMENT '关联的标注数据ID' AFTER data_source,
    ADD INDEX idx_annotation (annotation_id);
