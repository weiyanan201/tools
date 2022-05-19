package wei.tools.service.order;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Map;

/**
 * @Author: weiyanan
 * @Date: 2022/3/3 18:18
 */
public class OrderService {

    /**
     * 没有统计账单
     */
    public static void statisticsByMonth() throws IOException {
        File file = new File("C:\\Users\\homay\\Desktop\\test.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        Map<String,Integer> map = Maps.newHashMap();
        while((line=br.readLine())!=null){
            String[] ss = line.split("([：|:|；])");
            if (ss!=null && ss.length==2){
                String key = StringUtils.trim(ss[0]);
                String valueStr = StringUtils.trim(ss[1]);
                if (StringUtils.isNumeric(valueStr)){
                    int value = 0;
                    if (valueStr.contains(".")){
                        //有小数点
                        double d = Double.valueOf(StringUtils.trim(ss[1]));
                        value = (int) d;
                    }else {
                        value = Integer.valueOf(StringUtils.trim(ss[1]));
                    }
                    if (!map.containsKey(key)){
                        map.put(key,0);
                    }
                    map.put(key,map.get(key)+value);
                }


            }
        }

        for (Map.Entry<String,Integer> entry : map.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    public static void main(String[] args) throws IOException {
        statisticsByMonth();
    }
}
