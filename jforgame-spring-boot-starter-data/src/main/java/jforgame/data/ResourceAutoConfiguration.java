package jforgame.data;

import jforgame.data.common.CommonValueAutoInjectHandler;
import jforgame.data.common.ConfigResourceRegistry;
import jforgame.data.convertor.JsonToArrayConvertor;
import jforgame.data.convertor.JsonToListConvertor;
import jforgame.data.convertor.JsonToMapConvertor;
import jforgame.data.reader.DataReader;
import jforgame.data.reader.ExcelDataReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration
@EnableConfigurationProperties({ResourceProperties.class})
public class ResourceAutoConfiguration {
    @Autowired
    private ResourceProperties properties;

    public ResourceAutoConfiguration() {
    }

    // 明确指定要注入的ConversionService Bean名称
    @Bean
    @ConditionalOnMissingBean
    public DataReader createDataReader(
            @Qualifier("dataConversionService") ConversionService dataConversionService) {
        return new ExcelDataReader(dataConversionService);
    }

    @Bean(name = {"dataConversionService"})
    @ConditionalOnMissingBean(name = "dataConversionService")
    public ConversionService createConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new JsonToListConvertor());
        conversionService.addConverter(new JsonToArrayConvertor());
        conversionService.addConverter(new JsonToMapConvertor());
        return conversionService;
    }

    @Bean(name = {"dataManager"})
    @DependsOn({"dataConversionService"})
    public DataManager createDataManager(ResourceProperties properties, DataReader dataReader) {
        DataManager dataManager = new DataManager(properties, dataReader);
        dataManager.init();
        return dataManager;
    }

    @Bean(name = {"commonValueAutoInjectService"})
    @ConditionalOnMissingBean(name = "commonValueAutoInjectService")
    public CommonValueAutoInjectHandler createCommonValueAutoInjectHandler(DataManager dataManager, @Qualifier("dataConversionService") ConversionService conversionService) {
        return new CommonValueAutoInjectHandler(dataManager, conversionService);
    }

    @Bean
    public ConfigResourceRegistry createConfigResourceRegistry(CommonValueAutoInjectHandler autoInjectHandler) {
        return new ConfigResourceRegistry();
    }
}
