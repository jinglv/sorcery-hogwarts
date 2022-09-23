DROP DATABASE IF EXISTS `sorcery-hogwarts`;
CREATE DATABASE `sorcery-hogwarts` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# t_user用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`                        INT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_name`                 VARCHAR(50) NOT NULL COMMENT '用户名',
    `password`                  VARCHAR(32) NOT NULL COMMENT '密码',
    `email`                     VARCHAR(40) DEFAULT NULL COMMENT '邮箱',
    `auto_create_case_job_name` VARCHAR(50) NULL COMMENT '自动生成用例job名称，不为空时表示已经创建job',
    `start_test_job_name`       VARCHAR(50) NULL COMMENT '执行测试job，不为空时表示已经创建job',
    `default_jenkins_id`        INT         NULL COMMENT '默认Jenkins服务器',
    `create_time`               TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`               TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='用户表';

# t_project项目
DROP TABLE IF EXISTS `t_project`;
CREATE TABLE `t_project`
(
    `id`                        INT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_name`              VARCHAR(50) NOT NULL COMMENT '项目名称',
    `git_name`                  VARCHAR(255) NOT NULL COMMENT 'git工程名称',
    `git_address`               VARCHAR(255) NOT NULL COMMENT 'git工程地址',
    `git_credentials_id`        VARCHAR(255) NOT NULL COMMENT 'git认证id',
    `description`                  VARCHAR(255) NULL COMMENT '项目描述',
    `image`                     VARCHAR(50) NULL COMMENT '项目图片地址',
    `del_flag`                  TINYINT     NULL COMMENT '是否删除，0-未删除 1-已删除',
    `create_user_id`            INT         NOT NULL COMMENT '创建人id，test_user主键id',
    `create_time`               TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`               TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='项目表';

# t_jenkins
DROP TABLE IF EXISTS `t_jenkins`;
CREATE TABLE `t_jenkins`
(
    `id`                      INT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    `jenkins_name`            VARCHAR(100)        NULL COMMENT '名称',
    `jenkins_url`             VARCHAR(100)        NULL COMMENT 'Jenkins的baseUrl',
    `jenkins_username`        VARCHAR(100)        NULL COMMENT 'Jenkins认证登录用户名',
    `jenkins_password`        VARCHAR(100)        NULL COMMENT 'Jenkins认证登录密码',
    `command`            VARCHAR(100)        NULL COMMENT '执行测试命令',
    `command_run_case_type`   INT       DEFAULT 1 NOT NULL COMMENT '命令运行的测试用例类型  1 文本 2 文件',
    `command_run_case_suffix` VARCHAR(100)        NULL COMMENT '测试用例后缀名 如果case为文件时，此处必填',
    `remark`                  VARCHAR(100)        NULL COMMENT '备注',
    `create_user_id`          INT                 NOT NULL COMMENT '创建人id，test_user主键id',
    `create_time`             TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`             TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='Jenkins服务表';

# t_case
DROP TABLE IF EXISTS `t_case`;
CREATE TABLE `t_case`
(
    `id`             INT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    `case_name`      VARCHAR(200) NULL COMMENT '用例名称',
    `case_data`      LONGTEXT     NULL COMMENT '测试数据',
    `remark`         VARCHAR(100) NULL COMMENT '备注',
    `project_id`     INT NOT NULL COMMENT '项目id',
    `del_flag`       TINYINT      NULL COMMENT '删除标志 0 未删除 1 已删除',
    `create_user_id` INT          NOT NULL COMMENT '创建人id，test_user主键id',
    `create_time`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='测试用例表';

# t_task
DROP TABLE IF EXISTS `t_task`;
CREATE TABLE `t_task`
(
    `id`              INT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    `task_name`       VARCHAR(100)        NULL COMMENT '名称',
    `jenkins_id` INT                 NOT NULL COMMENT '运行测试的Jenkins服务器id',
    `build_url`       VARCHAR(100)        NULL COMMENT 'Jenkins的构建url',
    `command`    TEXT                NOT NULL COMMENT 'Jenkins执行测试时的命令脚本',
    `case_count` INT                 NULL COMMENT '测试用例数量',
    `task_type`       TINYINT   DEFAULT 1 NOT NULL COMMENT '任务类型 1 执行测试任务 2 一键执行测试的任务',
    `status`          TINYINT   DEFAULT 1 NOT NULL COMMENT '状态 0 无效 1 新建 2 执行中 3 执行完成',
    `remark`          VARCHAR(100)        NULL COMMENT '备注',
    `create_user_id`  INT                 NOT NULL COMMENT '创建人id，test_user主键id',
    `create_time`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='测试任务表';

# t_task_case_rel
DROP TABLE IF EXISTS `t_task_case_rel`;
CREATE TABLE `t_task_case_rel`
(
    `id`             INT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    `task_id`   INT NULL COMMENT '任务id',
    `case_id`   INT NULL COMMENT '用例id',
    `create_user_id` INT NULL COMMENT '创建人id，test_user主键id',
    `create_time`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='测试任务表';
