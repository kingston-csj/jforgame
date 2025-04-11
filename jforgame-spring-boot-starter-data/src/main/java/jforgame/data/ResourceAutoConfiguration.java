package jforgame.data;

import jforgame.data.convertor.JsonToArrayConvertor;
import jforgame.data.convertor.JsonToListConvertor;
import jforgame.data.convertor.JsonToMapConvertor;
import jforgame.data.reader.CsvDataReader;
import jforgame.data.reader.DataReader;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Bean
    @ConditionalOnMissingBean
    public DataReader createDataReader() {
        return new CsvDataReader();
    }

    @Bean(name = {"dataConversionService"})
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
}
