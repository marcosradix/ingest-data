package br.com.workmade.ingestdata.service;

import br.com.workmade.ingestdata.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RestService {

    private static final String API_URL = "https://api.bodegadigital.app/product/api";


    private static final String ACCESS_TOKEN = "access_token";

    public void sendData(ProductDTO productDTO) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(ACCESS_TOKEN);
            HttpEntity<ProductDTO> entity = new HttpEntity<>(productDTO, headers);

            restTemplate.postForObject(API_URL, entity, String.class);
            log.info("Inserido com sucesso");
        } catch (Exception e) {
            log.info("erro ao inserir produto: {}", productDTO.toString());
        }

    }
}
