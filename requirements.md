# 需求文档

## 简介

本文档定义了一套完整的电商商城后台系统的需求，包括面向消费者的前台 API 和面向管理员的后台管理 API。系统基于 Spring Boot 4.0.2 + Java 17 + MySQL 构建，支持商品管理、购物车、订单、库存、多货币和国际化等核心电商功能。

## 术语表

- **System**：电商商城后台系统整体
- **Product_Service**：商品服务模块，负责商品的展示、查询和管理
- **Category_Service**：分类服务模块，负责商品分类的管理和查询
- **Cart_Service**：购物车服务模块，负责购物车的增删改查
- **Order_Service**：订单服务模块，负责订单的创建、查询和管理
- **Inventory_Service**：库存服务模块，负责库存的扣减、查询和管理
- **Currency_Service**：货币服务模块，负责货币切换和价格转换
- **I18n_Service**：国际化服务模块，负责多语言内容的管理和切换
- **Admin_Service**：后台管理服务模块，负责管理端的综合管理功能
- **SKU**：Stock Keeping Unit，库存量单位，代表商品的具体规格组合
- **SPU**：Standard Product Unit，标准产品单位，代表一个抽象商品
- **Cart_Item**：购物车中的单个商品条目
- **Order**：用户下单后生成的订单记录
- **Specification**：商品规格，如颜色、尺寸等
- **Attribute**：商品属性，如材质、产地等描述性信息

## 需求

### 需求 1：商品展示与查询

**用户故事：** 作为消费者，我希望浏览和查询商品信息，以便了解商品详情并做出购买决策。

#### 验收标准

1. WHEN 消费者请求商品列表 THEN THE Product_Service SHALL 返回分页的商品列表，每个商品包含图片URL、名称、默认货币、价格信息
2. WHEN 消费者请求某个商品的详情 THEN THE Product_Service SHALL 返回该商品的完整信息，包含图片列表、名称、描述、价格、规格列表、属性列表和所属分类
3. WHEN 消费者按分类查询商品 THEN THE Product_Service SHALL 仅返回属于该分类及其子分类下的商品列表
4. WHEN 消费者按关键词搜索商品 THEN THE Product_Service SHALL 返回名称或描述中包含该关键词的商品列表
5. WHEN 消费者请求商品详情且该商品包含多个 SKU THEN THE Product_Service SHALL 返回所有 SKU 的规格组合及各自的价格和库存状态

### 需求 2：商品分类管理

**用户故事：** 作为消费者，我希望通过分类浏览商品；作为管理员，我希望管理商品分类体系，以便组织商品结构。

#### 验收标准

1. WHEN 消费者请求分类树 THEN THE Category_Service SHALL 返回完整的多级分类树形结构
2. WHEN 管理员创建分类并指定父分类 THEN THE Category_Service SHALL 在该父分类下创建子分类并返回创建结果
3. WHEN 管理员更新分类信息 THEN THE Category_Service SHALL 更新分类名称、排序等字段并返回更新后的分类
4. WHEN 管理员删除一个分类且该分类下存在商品 THEN THE Category_Service SHALL 拒绝删除并返回错误提示
5. IF 管理员尝试创建同名同级分类 THEN THE Category_Service SHALL 拒绝创建并返回重复错误

### 需求 3：购物车系统

**用户故事：** 作为消费者，我希望将商品添加到购物车并管理购物车中的商品数量，以便在下单前整理我的购买清单。

#### 验收标准

1. WHEN 消费者将某个 SKU 添加到购物车 THEN THE Cart_Service SHALL 在购物车中创建一条 Cart_Item 记录，包含 SKU 信息和数量
2. WHEN 消费者添加已存在于购物车中的 SKU THEN THE Cart_Service SHALL 将该 Cart_Item 的数量增加指定数值而非创建新记录
3. WHEN 消费者增加某个 Cart_Item 的数量 THEN THE Cart_Service SHALL 更新该 Cart_Item 的数量并返回更新后的购物车
4. WHEN 消费者减少某个 Cart_Item 的数量至零 THEN THE Cart_Service SHALL 从购物车中移除该 Cart_Item
5. WHEN 消费者查看购物车 THEN THE Cart_Service SHALL 返回购物车中所有 Cart_Item 的列表，包含商品名称、图片、规格、单价、数量和小计金额
6. IF 消费者添加的数量超过该 SKU 的可用库存 THEN THE Cart_Service SHALL 拒绝添加并返回库存不足的错误提示

### 需求 4：订单系统

**用户故事：** 作为消费者，我希望将购物车中的商品下单购买，以便完成购物流程。

#### 验收标准

1. WHEN 消费者提交订单 THEN THE Order_Service SHALL 基于购物车内容创建订单，记录商品快照、数量、单价、总金额和收货地址
2. WHEN 订单创建成功 THEN THE Inventory_Service SHALL 扣减对应 SKU 的库存数量
3. WHEN 订单创建成功 THEN THE Cart_Service SHALL 清空该消费者购物车中已下单的商品
4. IF 下单时某个 SKU 的库存不足 THEN THE Order_Service SHALL 拒绝创建订单并返回库存不足的具体 SKU 信息
5. WHEN 消费者查询订单列表 THEN THE Order_Service SHALL 返回该消费者的分页订单列表，包含订单号、状态、总金额和创建时间
6. WHEN 消费者查询订单详情 THEN THE Order_Service SHALL 返回订单的完整信息，包含商品快照、数量、单价、收货地址和订单状态
7. WHEN 管理员更新订单状态 THEN THE Order_Service SHALL 按照合法的状态流转规则更新订单状态
8. IF 管理员尝试将订单设置为非法的状态转换 THEN THE Order_Service SHALL 拒绝更新并返回非法状态转换的错误

### 需求 5：库存管理

**用户故事：** 作为管理员，我希望管理商品库存，以便确保库存数据准确并支撑销售。

#### 验收标准

1. THE Inventory_Service SHALL 为每个 SKU 维护独立的库存数量记录
2. WHEN 管理员设置某个 SKU 的库存数量 THEN THE Inventory_Service SHALL 更新该 SKU 的库存并记录变更日志
3. WHEN 管理员查询库存列表 THEN THE Inventory_Service SHALL 返回分页的 SKU 库存列表，包含 SKU 信息、当前库存数量和预警阈值
4. WHEN 某个 SKU 的库存数量低于预警阈值 THEN THE Inventory_Service SHALL 在库存查询结果中标记该 SKU 为低库存状态
5. IF 库存扣减操作导致库存数量为负数 THEN THE Inventory_Service SHALL 拒绝该操作并返回库存不足错误

### 需求 6：货币切换

**用户故事：** 作为消费者，我希望切换显示货币，以便用我熟悉的货币查看商品价格。

#### 验收标准

1. WHEN 消费者指定目标货币查询商品 THEN THE Currency_Service SHALL 将商品价格从基准货币转换为目标货币并返回
2. THE Currency_Service SHALL 维护一套货币汇率配置，包含基准货币和各目标货币的汇率
3. WHEN 管理员更新汇率配置 THEN THE Currency_Service SHALL 立即生效新的汇率用于后续价格转换
4. WHEN 消费者查看购物车且指定了目标货币 THEN THE Cart_Service SHALL 使用 Currency_Service 将所有价格转换为目标货币显示
5. IF 消费者指定了系统不支持的货币代码 THEN THE Currency_Service SHALL 返回不支持的货币错误

### 需求 7：国际化（多语言）

**用户故事：** 作为消费者，我希望切换系统语言，以便用我熟悉的语言浏览商品和操作系统。

#### 验收标准

1. WHEN 消费者在请求头中指定语言代码 THEN THE I18n_Service SHALL 返回对应语言的系统提示信息和错误消息
2. WHEN 消费者指定语言查询商品 THEN THE Product_Service SHALL 返回该语言版本的商品名称和描述（如果存在翻译）
3. IF 请求的语言版本不存在 THEN THE I18n_Service SHALL 回退到默认语言（中文）返回内容
4. WHEN 管理员为商品添加多语言翻译 THEN THE Product_Service SHALL 存储该商品的多语言名称和描述
5. THE System SHALL 支持至少中文（zh）和英文（en）两种语言

### 需求 8：商品管理（后台）

**用户故事：** 作为管理员，我希望对商品进行完整的增删改查管理，以便维护商城的商品数据。

#### 验收标准

1. WHEN 管理员创建商品 THEN THE Admin_Service SHALL 创建 SPU 记录，包含名称、描述、图片、分类、属性，并返回创建结果
2. WHEN 管理员为商品添加 SKU THEN THE Admin_Service SHALL 创建 SKU 记录，包含规格组合、价格和初始库存
3. WHEN 管理员更新商品信息 THEN THE Admin_Service SHALL 更新 SPU 的指定字段并返回更新后的商品
4. WHEN 管理员删除商品 THEN THE Admin_Service SHALL 将商品标记为下架状态而非物理删除
5. WHEN 管理员查询商品列表 THEN THE Admin_Service SHALL 返回分页的商品列表，支持按名称、分类、状态筛选
6. IF 管理员创建商品时未提供必填字段 THEN THE Admin_Service SHALL 返回字段校验错误的详细信息

### 需求 9：订单管理（后台）

**用户故事：** 作为管理员，我希望查看和管理所有订单，以便跟踪订单状态和处理售后问题。

#### 验收标准

1. WHEN 管理员查询订单列表 THEN THE Admin_Service SHALL 返回分页的全部订单列表，支持按状态、时间范围、订单号筛选
2. WHEN 管理员查看订单详情 THEN THE Admin_Service SHALL 返回订单的完整信息，包含消费者信息、商品快照、支付信息和物流信息
3. WHEN 管理员发货 THEN THE Order_Service SHALL 将订单状态更新为已发货并记录物流信息

### 需求 10：数据序列化

**用户故事：** 作为系统，我需要将内部数据对象序列化为 JSON 格式返回给客户端，并能从 JSON 请求体反序列化为内部对象，以便前后端正确通信。

#### 验收标准

1. THE System SHALL 使用 JSON 格式作为所有 API 请求和响应的数据格式
2. FOR ALL 有效的商品对象，序列化为 JSON 再反序列化 SHALL 产生等价的商品对象（往返一致性）
3. FOR ALL 有效的订单对象，序列化为 JSON 再反序列化 SHALL 产生等价的订单对象（往返一致性）
4. FOR ALL 有效的购物车对象，序列化为 JSON 再反序列化 SHALL 产生等价的购物车对象（往返一致性）
