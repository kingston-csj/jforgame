### Ⅰ. Introduction

- `jforgame-data-spring-boot-starter` is the Spring Boot starter assembly module for `jforgame-data`
- `jforgame-data` is responsible for configuration data parsing, container loading, common constant injection and data validation capabilities
- `jforgame-data-spring-boot-starter` is responsible for auto-assembly, property binding and default Bean registration in Spring Boot environment
- `ResourceProperties` in the starter reads `jforgame.data.*` configuration and converts it to the common configuration object `ResourceOptions` in `jforgame-data`
- The component supports csv/excel/json format
  A normal java class represents a csv/excel configuration, each instance of the class represents a row of records in the file
- No need to declare field types of configuration, automatically converted based on javabean definition
- Supports secondary cache, no additional code needed to solve configuration hot-reload cache consistency issues

### Ⅱ. Module Relationship

- If you do not use Spring Boot, you can directly depend on `jforgame-data` and create `ResourceOptions`, `DataReader`, `DataManager` yourself
- If you use Spring Boot, simply depend on `jforgame-data-spring-boot-starter`
- The responsibility boundaries of the two are as follows:

| Module | Function |
| --- | --- |
| `jforgame-data` | Function module, provides core capabilities such as configuration reading, cache loading, hot-reload validation |
| `jforgame-data-spring-boot-starter` | Assembly module, responsible for `ResourceProperties` configuration binding, `ResourceOptions` conversion and auto-injection |

### Ⅲ. Auto Mapping

- Records above the HEADER line are not read or written by the program, designers can add them freely, such as adding comments, field types, etc.
- The HEADER line marks the name of each field, the program starts reading from the row below the HEADER line
- The program stops reading when it reaches the line containing END, even if there is data below the END line, the program will not read it

  ![Image](../screenshots/csv_file.jpg "Configuration Format")

- The row containing EXPORT is optional, if not present, it means all fields are exported
- SERVER indicates that the field is used by the server, not needed by the client
- CLIENT indicates that the field is used by the client, not needed by the server
- BOTH indicates that the field is needed by both server and client
- Blank means neither server nor client needs it, just for designer notes

  ![Image](../screenshots/config_export.jpg "Configuration Format")

- Java class corresponding to Excel

  ![image](../screenshots/csv_bean.jpg "Java entity corresponding to configuration")

  Main annotations
- `@DataTable` indicates that the class is a configuration class
- `@Id` indicates that the field is the primary key of the configuration class
- `@Index` indicates that an index is established for this field, equivalent to SQL's GROUP BY

### Ⅳ. Usage

- Spring Boot environment component reference
  ```
    <dependency>
        <groupId>io.github.jforgame</groupId>
        <artifactId>jforgame-data-spring-boot-starter</artifactId>
        <version>latest</version>
    </dependency>
  ```
- Project configuration (application.yml file)
   ```
  jforgame:
    data:
      ## Configuration entity scan path
      tableScanPath: org.jforgame.server.game.database.config
      ## Secondary cache Container scan path
      containerScanPath: org.jforgame.server.game.database.config
  ```

- In non-Spring Boot environment, you can directly use `jforgame-data` and construct `ResourceOptions` yourself
  ```java
  ResourceOptions options = new ResourceOptions();
  options.setLocation("csv/");
  options.setSuffix(".csv");
  options.setTableScanPath("org.jforgame.server.game.database.config");
  options.setContainerScanPath("org.jforgame.server.game.database.config");

  DataManager dataManager = new DataManager(options, dataReader);
  dataManager.init();
  ```

- Secondary cache, default container only saves id-to-entity mapping, index-to-entity list mapping, if the program needs secondary cache, just inherit the Container class

  ![Image](../screenshots/csv_cache.jpg "Configuration secondary cache container")

- Configuration access
  1. Get single record
  ```
      // Query data with id=1
      ItemData itemData = GameContext.dataManager.queryById(ItemData.class, 1);
  ```
  2. Get grouped index data
  ```
      // Query all grouped data with type=1
      List<ItemData> itemDataList = GameContext.dataManager.queryByIndex(ItemData.class, "type", 1);
  ```
  3. Get unique index data
  ```
      // Query all grouped data with type=1
      HeroLevelData heroLevelData = GameContext.dataManager.queryByUniqueIndex(HeroLevelData.class, "index_id_level", "1_1");
  ```
  4. Get secondary cache data
  ```
      // Get secondary cache data
      QuestContainer container = GameContext.dataManager.queryContainer(QuestData.class, QuestContainer.class);
      Reward rewards = container.getRewardBy(1);
      System.out.println(JsonUtil.object2String(rewards));
  ```
  5. Configuration hot-reload
  ```
      for (String table : tables) {
          try {
              GameContext.dataManager.reload(table);
              succ.add(table);
          } catch (Exception e) {
              log.error("", e);
              failed.add(table);
          }
      }
      log.info("Hot-reload completed, success [{}], failed [{}]", JsonUtil.object2String(succ), JsonUtil.object2String(failed));
  ```