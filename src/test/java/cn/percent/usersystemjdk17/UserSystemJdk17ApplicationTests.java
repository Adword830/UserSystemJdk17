package cn.percent.usersystemjdk17;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SpringBootTest
class UserSystemJdk17ApplicationTests {


    /*@Test
    void contextLoads() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));

        try (FileInputStream inputStream = new FileInputStream("D:\\test.txt")) {
            inputStream.available();
        } catch (Exception e) {
            e.printStackTrace();
        }


        int[] num = {100, 97, 68, 67, 10};
        int find = 67;
        // 折半二分查找
        int low = 0;
        int high = num.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (num[mid] == find) {
                System.out.println("找到了，索引是：" + mid);
                break;
            } else if (num[mid] < find) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
    }*/


    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * @param num int整型一维数组
     * @return int整型ArrayList<ArrayList <>>
     */
    public ArrayList<ArrayList<Integer>> permute(int[] num) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        sort(num, result);
        return result;
    }

    public void sort(int[] num, ArrayList<ArrayList<Integer>> result) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < num.length; i++) {
            // 集合中存在就不在向里面加了
            if (arrayList.contains(num[i])) {
                continue;
            }
            // 不存在存进去
            arrayList.add(num[i]);
            // 重复此操作
            sort(num, result);
            result.add(arrayList);
        }

    }

    /**
     * 预计要插入多少数据
     */
    private static final int size = 1000000;

    /**
     * 期望的误判率
     */
    private static final double fpp = 0.01;

    /**
     * 布隆过滤器
     */
    private static final BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, fpp);

    @Test
    public void test01() {
        // 插入10万样本数据
        for (int i = 0; i < size; i++) {
            bloomFilter.put(i);
        }

        // 用另外十万测试数据，测试误判率
        int count = 0;
        for (int i = size; i < size + 100000; i++) {
            if (bloomFilter.mightContain(i)) {
                count++;
                System.out.println(i + "误判了");
            }
        }
        System.out.println("总共的误判数:" + count);
    }


    /**
     * 度值：（116.389550， 39.928167）
     */
    @Test
    public void test02() {
        double x = 116.389550;
        double y = 39.928167;

        // geoHash 算法
        String geoHash = getGeoHash(x, y);
        System.out.println(geoHash);
    }


    private String getGeoHash(double x, double y) {
        // 经度范围
        double[] lon = {-180, 0, 180};
        // 纬度范围
        double[] lat = {-90, 0, 90};
        StringBuilder bitLat = new StringBuilder();
        StringBuilder bitLon = new StringBuilder();

        // 先计算纬度的值y x需要把范围切割
        for (int i = 0; i < 10; i++) {
            double minLat = lat[0];
            double midLat = lat[1];
            double maxLat = lat[2];

            double minLon = lon[0];
            double midLon = lon[1];
            double maxLon = lon[2];

            if (minLat <= y && y < midLat) {
                lat[0] = minLat;
                lat[2] = midLat;
                lat[1] = (midLat + minLat) / 2;
                bitLat.append("0");
            }
            if (midLat <= y && y <= maxLat) {
                lat[0] = midLat;
                lat[2] = maxLat;
                lat[1] = (maxLat + midLat) / 2;
                bitLat.append("1");
            }
            if (minLon <= x && x < midLon) {
                lon[0] = minLon;
                lon[2] = midLon;
                lon[1] = (minLon + midLon) / 2;
                bitLon.append("0");
            }
            if (midLon <= x && x <= maxLon) {
                lon[0] = midLon;
                lon[2] = maxLon;
                lon[1] = (midLon + maxLon) / 2;
                bitLon.append("1");
            }

        }
        System.out.println(Arrays.toString(lat));
        System.out.println(Arrays.toString(lon));
        System.out.println(bitLat);
        System.out.println(bitLon);

        return "";
    }

    /**
     * Two Sum 两数之和 双for循环优化为map O(n^2)--->O(n)
     */
    @Test
    void test03() {
        int[] num = {7, 2, 3, 4, 1};
        int target = 3;

        int[] result = new int[2];

        // 将数组转为map
        HashMap<Integer, Integer> map = new HashMap<>();
        // 时间复杂度O(n)
        for (int i = 0; i < num.length; i++) {
            if (map.containsKey(target - num[i])) {
                result[0] = map.get(target - num[i]);
                result[1] = i;
            } else {
                map.put(num[i], i);
            }


        }

        System.out.println("result = " + result[0] + "," + result[1]);

    }
}
