/**
 * 配置数据功能模块。
 * 提供配置表读取、容器装载、通用常量注入、数据校验等核心能力。
 * 本模块依赖 Spring Framework 基础能力，但不依赖 Spring Boot。
 * 若运行在 Spring Boot 环境下，可通过 starter 模块完成自动装配和属性绑定。
 * 运行期配置统一通过 {@link jforgame.data.ResourceOptions} 表达，而不是 Boot 专用的配置绑定对象。
 */
package jforgame.data;
