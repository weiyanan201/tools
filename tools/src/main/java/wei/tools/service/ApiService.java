package wei.tools.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ApiService {

    private Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private RestTemplate restTemplate;


    /**
     * 默认返回JSONObject
     * @param url
     * @param params
     * @return
     */
    public JSONObject get(String url,String...params){
        return get(url,JSONObject.class,params);
    }

    public <T> T get(String url, Class<T> responseType, String... params){
        return restTemplate.getForObject(url,responseType,params);
    }

    /**
     * 默认返回JSONObject
     * @param url
     * @return
     */
    public JSONObject get(String url){
        return get(url,JSONObject.class);
    }

    public <T> T get(String url,Class<T> responseType){
        return restTemplate.getForObject(url,responseType, Maps.newHashMap());
    }

    /**
     * 默认返回JSONObject
     * @param url
     * @param params
     * @return
     */
    public JSONObject get(String url, Map<String,Object> params){
        return get(url,JSONObject.class,params);
    }

    public <T> T get(String url,Class<T> responseType,Map<String,Object>params){
        //拼参数
        //1.包含? 直接传map
        //2.不包含?
        //  a.包含{ 直接传map
        //  b.不包含{ 拼参数
        if (url.indexOf("?")!=-1){
            logger.info("call get : " + url);
            return restTemplate.getForObject(url,responseType,params);
        }else{
            if (url.indexOf("{")!=-1){
                logger.info("call get : " + url);
                return restTemplate.getForObject(url,responseType,params);
            }else{
                StringBuilder sb = new StringBuilder(url);
                if (params.size()>0){
                    sb.append("?");
                    for (Map.Entry<String,Object> entry: params.entrySet()){
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                    sb.delete(sb.lastIndexOf("&"),sb.length());
                }
                logger.info("call get : " + sb.toString());
                return restTemplate.getForObject(sb.toString(),responseType);
            }
        }
    }

    /**
     * get 添加header 访问
     * @param url
     * @param responseType
     * @param headers
     * @param <T>
     * @return
     */
    public <T> T get(String url,Class<T> responseType,HttpHeaders headers){
        logger.info("call get : " + url );
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType);
        return (T) response.getBody();
    }

    public JSONObject post(String url, JSONObject body){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        logger.info("call post : " + url + " ; body : " + body.toString());
        return this.post(url,body,headers);
    }

    public <T>T post(String url,JSONObject body,Class<T> responseType){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return post(url,body,headers,responseType);
    }

    /**
     *
     * @param url
     * @param body 参数
     * @param headers header
     * @return
     */
    public JSONObject post(String url, JSONObject body, HttpHeaders headers){
        HttpEntity requestBody = new HttpEntity(body, headers);
//        ResponseEntity<JSONObject> entity = restTemplate.postForEntity(url,requestBody,JSONObject.class);
        return restTemplate.postForEntity(url,requestBody,JSONObject.class).getBody();
    }

    public <T>T post(String url,JSONObject body,HttpHeaders headers,Class<T> responseType){
        HttpEntity requestBody = new HttpEntity(body, headers);
        logger.info("call post : " + url);
        return restTemplate.postForEntity(url,requestBody,responseType).getBody();
    }

}
