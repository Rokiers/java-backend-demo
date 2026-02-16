# 实施任务列表

## 任务 1：项目基础设施搭建
- [x] 1.1 更新 pom.xml 添加所需依赖（Spring Data JPA, MySQL Driver, H2, Jakarta Validation, Lombok, jqwik）
- [x] 1.2 配置 application.properties（数据源、JPA、国际化）
- [x] 1.3 创建包结构（config, controller/api, controller/admin, service, service/impl, repository, entity, dto/request, dto/response, enums, exception, i18n, util）
- [x] 1.4 创建统一响应类 ApiResponse 和 PageResponse
- [x] 1.5 创建全局异常处理器 GlobalExceptionHandler 和自定义异常类
- [x] 1.6 创建国际化配置和消息文件（messages.properties, messages_en.properties）

## 任务 2：数据模型层（Entity + Repository）
- [x] 2.1 创建枚举类型（ProductStatus, OrderStatus, InventoryChangeType）
- [x] 2.2 创建 Category 实体和 CategoryRepository
- [x] 2.3 创建 Product, ProductAttribute, ProductTranslation 实体和对应 Repository
- [x] 2.4 创建 Sku, SkuSpecification 实体和对应 Repository
- [x] 2.5 创建 Inventory, InventoryLog 实体和对应 Repository
- [x] 2.6 创建 Cart, CartItem 实体和对应 Repository
- [x] 2.7 创建 Order, OrderItem 实体和对应 Repository
- [x] 2.8 创建 CurrencyRate 实体和 CurrencyRateRepository

## 任务 3：DTO 层
- [x] 3.1 创建商品相关 DTO（请求和响应）
- [x] 3.2 创建分类相关 DTO
- [x] 3.3 创建购物车相关 DTO
- [x] 3.4 创建订单相关 DTO
- [x] 3.5 创建库存相关 DTO
- [x] 3.6 创建货币相关 DTO

## 任务 4：Service 层 — 核心业务逻辑
- [x] 4.1 实现 CurrencyService（货币转换、汇率管理）
- [x] 4.2 实现 CategoryService（分类树构建、CRUD、删除保护）
- [x] 4.3 实现 ProductService（商品查询、搜索、分类过滤、国际化）
- [x] 4.4 实现 InventoryService（库存扣减、设置、日志记录、非负约束）
- [x] 4.5 实现 CartService（添加、数量更新、库存校验、SKU唯一性）
- [x] 4.6 实现 OrderService（创建订单、库存扣减、购物车清空、状态机）

## 任务 5：前台 API Controller 层
- [x] 5.1 实现 ProductController（商品列表、详情、分类查询、搜索）
- [x] 5.2 实现 CategoryController（分类树、分类详情）
- [x] 5.3 实现 CartController（查看、添加、更新数量、移除）
- [x] 5.4 实现 OrderController（创建订单、订单列表、订单详情）
- [x] 5.5 实现 CurrencyController（货币列表、价格转换）

## 任务 6：后台管理 API Controller 层
- [x] 6.1 实现 AdminProductController（商品CRUD、SKU管理、翻译管理）
- [x] 6.2 实现 AdminCategoryController（分类CRUD）
- [x] 6.3 实现 AdminInventoryController（库存查询、设置、日志）
- [x] 6.4 实现 AdminOrderController（订单列表、详情、状态更新）
- [x] 6.5 实现 AdminCurrencyController（汇率查询、更新）

## 任务 7：SQL 初始化脚本
- [x] 7.1 创建 schema.sql 数据库建表脚本（含所有表、索引、约束）
- [x] 7.2 创建 data.sql 初始数据脚本（默认分类、默认汇率）
