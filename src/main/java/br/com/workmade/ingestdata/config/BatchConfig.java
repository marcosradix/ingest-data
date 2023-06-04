package br.com.workmade.ingestdata.config;

import br.com.workmade.ingestdata.dto.CSVFileDTO;
import br.com.workmade.ingestdata.dto.ProductDTO;
import br.com.workmade.ingestdata.dto.ProductStockDTO;
import br.com.workmade.ingestdata.dto.StoreDTO;
import br.com.workmade.ingestdata.service.RestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.BindException;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RestService restService;

    @Bean
    public ItemReader<CSVFileDTO> csvReader() {
        FlatFileItemReader<CSVFileDTO> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1); // Pule o cabeçalho da planilha
        reader.setResource(new FileSystemResource("/home/marcosferreira/python_projects/data_normalize/data_normalized.csv")); // Altere o caminho para o seu arquivo
        DefaultLineMapper<CSVFileDTO> customerLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter("#");
        tokenizer.setNames("Name", "BarCode", "Description", "Price");
        customerLineMapper.setLineTokenizer(tokenizer);
        customerLineMapper.setFieldSetMapper(csvMapFieldSet());
        customerLineMapper.afterPropertiesSet();
        reader.setLineMapper(customerLineMapper);

        return reader;
    }

    @Bean
    public FieldSetMapper<CSVFileDTO> csvMapFieldSet() {
        return new FieldSetMapper<CSVFileDTO>() {
            @Override
            public CSVFileDTO mapFieldSet(FieldSet fieldSet) throws BindException {
                return CSVFileDTO.builder()
                        .name(fieldSet.readString("Name"))
                        .barCode(StringUtils.isNumeric(fieldSet.readString("BarCode")) ? fieldSet.readString("BarCode") : "0000000000000")
                        .description(fieldSet.readString("Description"))
                        .price(fieldSet.readString("Price").equalsIgnoreCase("") ? BigDecimal.ZERO : fieldSet.readBigDecimal("Price") )
                        .build();
            }
        };
    }

    @Bean
    public ItemWriter<CSVFileDTO> databaseWriterCSV() {
        return items -> {
            for (CSVFileDTO item : items) {
                StoreDTO storeDTO = StoreDTO.builder().id(1L).build();
                ProductStockDTO productStockDTO = ProductStockDTO.builder()
                        .buyCount(0)
                        .quantity(0)
                        .store(storeDTO)
                        .build();
                ProductDTO productDTO = ProductDTO.builder()
                        .name(item.getName())
                        .barCode(item.getBarCode())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .productStock(productStockDTO)
                        .build();
                System.out.printf("Name: %s, BarCode: %s, Description: %s, Price: %s%n", item.getName(), item.getBarCode(), item.getDescription(), item.getPrice());
                restService.sendData(productDTO);
            }
        };
    }

//    @Bean
//    public JdbcBatchItemWriter<CSVFileDTO> databaseWriter() {
//        JdbcBatchItemWriter<CSVFileDTO> itemWriter = new JdbcBatchItemWriter<>();
//
//        itemWriter.setDataSource(this.dataSource);
//        itemWriter.setSql("INSERT INTO PRODUCT VALUES (:name, :description, :bar_code, :price)");
//        itemWriter.setItemSqlParameterSourceProvider(item -> {
//            final MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
//            sqlParameterSource.addValue("name", item.getName());
//            sqlParameterSource.addValue("description", item.getDescription());
//            sqlParameterSource.addValue("bar_code", item.getBarCode());
//            sqlParameterSource.addValue("price", item.getPrice());
//            return sqlParameterSource;
//        });
//        itemWriter.afterPropertiesSet();
//        return itemWriter;
//    }

    @Bean
    public Step csvToDatabaseStep() {
        return stepBuilderFactory.get("excelToDatabaseStep")
                .<CSVFileDTO, CSVFileDTO>chunk(1000)
                .reader(csvReader())
                .writer(databaseWriterCSV())
                .build();
    }

    @Bean
    public Job excelToDatabaseJob(Step excelToDatabaseStep) {
        return jobBuilderFactory.get("excelToDatabaseJob")
                .start(excelToDatabaseStep)
                .build();
    }

}
